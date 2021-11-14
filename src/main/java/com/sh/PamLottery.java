package com.sh;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Represents single (NB!) round of lottery
 */
@NotNull
public interface PamLottery {
  default String name() {
    return "Name";
  }

  /**
   * @return money available in the pot
   */
  BigDecimal info();

  /**
   * Purchase a ticket for a random ball for 10 CHF
   *
   * @return ticket for a ball#
   * @throws IllegalStateException if draw is already made in this round
   */
  Integer purchase(String lastName);

  /**
   * @return map of balls with corresponding winners
   * @throws IllegalStateException if no draw is made in this round
   */
  Map<Integer, Winners> winners();

  /**
   * @return draw balls at random
   * @throws IllegalStateException if draw is already made in this round
   */
  List<Integer> draw();
}
