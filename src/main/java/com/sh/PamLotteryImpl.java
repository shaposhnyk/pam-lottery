package com.sh;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Represents single (NB!) round of lottery.
 * Thread NOT safe.
 */
public class PamLotteryImpl implements PamLottery {

  private final Random rng;
  private final BigDecimal initialPot;

  // list of ball in game
  private final List<Integer> balls;
  // list of stakes
  private final List<BigDecimal> stakes;

  // purchases done. multiple purchases are allowed
  private final Map<Integer, List<String>> purchases;
  // current draw
  private final List<Integer> draw;

  public PamLotteryImpl(long seed, BigDecimal initialPot) {
    this(seed, initialPot, 30, 66, 33, 1);
  }

  // test purposes only
  public PamLotteryImpl(long seed, BigDecimal initialPot, int totalBalls, int... stakes) {
    this.rng = new Random(seed);
    this.initialPot = initialPot;
    this.balls = IntStream.range(1, totalBalls + 1).boxed().collect(Collectors.toList());

    this.purchases = new HashMap<>();
    this.draw = new ArrayList<>();
    this.stakes = IntStream.of(stakes)
        .mapToObj(BigDecimal::valueOf)
        .map(v -> initialPot.multiply(v).divide(BigDecimal.valueOf(100)))
        .map(v -> v.setScale(0))
        .collect(Collectors.toList());
  }

  // test purposes only
  public PamLotteryImpl(
      Random rng,
      BigDecimal initialPot,
      List<Integer> balls,
      List<BigDecimal> stakes,
      Map<Integer, List<String>> purchases,
      List<Integer> draw
  ) {
    this.rng = rng;
    this.initialPot = initialPot;
    this.balls = balls;
    this.stakes = stakes;
    this.purchases = purchases;
    this.draw = draw;
  }

  @Override
  public BigDecimal info() {
    return draw.isEmpty() ? initialPot : initialPot.subtract(winnersTotal());
  }

  /**
   * @return total money won by lottery users
   */
  private BigDecimal winnersTotal() {
    return winners().values().stream()
        .filter(w -> !w.owners().isEmpty())
        .map(Winners::stake)
        .reduce(BigDecimal::add)
        .orElse(BigDecimal.ZERO);
  }

  @Override
  public Integer purchase(String lastName) {
    if (lastName == null || lastName.trim().isEmpty()) {
      throw new IllegalArgumentException("Name should not be empty");
    }
    initialPot.add(BigDecimal.TEN);
    final Integer ball = randomBall();
    purchases.computeIfAbsent(ball, key -> new ArrayList<>()).add(lastName);
    return ball;
  }

  @Override
  public Map<Integer, Winners> winners() {
    if (draw.isEmpty()) {
      throw new IllegalStateException("Should draw first");
    }
    Map<Integer, Winners> winners = IntStream.range(0, draw.size())
        .mapToObj(Integer::valueOf)
        .collect(Collectors.toMap(i -> draw.get(i), ballNo -> winnerOf(ballNo)));
    return winners;
  }

  private Winners winnerOf(int drawIdx) {
    List<String> winners = purchases.getOrDefault(draw.get(drawIdx), Collections.emptyList());
    return new WinnersImpl(stakes.get(drawIdx), winners);
  }

  @Override
  public List<Integer> draw() {
    if (!draw.isEmpty()) {
      throw new IllegalStateException("Draw is already made");
    }

    Integer first = drawRandom();
    Integer second = drawRandom(first);
    Integer third = drawRandom(first, second);
    draw.addAll(Arrays.asList(first, second, third));
    return draw;
  }

  private Integer drawRandom(Integer... excluded) {
    Collection<Integer> excludedSet = new HashSet<>(Arrays.asList(excluded));
    Integer ball = randomBall();
    // it's not optimal, but it shoud work
    while (excludedSet.contains(ball)) {
      ball = randomBall();
    }
    return ball;
  }

  private Integer randomBall() {
    return balls.get(rng.nextInt(balls.size()));
  }
}
