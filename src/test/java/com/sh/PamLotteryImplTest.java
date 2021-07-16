package com.sh;


import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PamLotteryImplTest {
  private static final Logger logger = LoggerFactory.getLogger(PamLotteryImplTest.class);

  @Test(expected = IllegalStateException.class)
  public void noDrawNoWinners() {
    PamLottery lottery = new PamLotteryImpl(10, BigDecimal.valueOf(100));
    lottery.winners();
  }

  @Test
  public void noDrawTwice() {
    PamLottery lottery = new PamLotteryImpl(10, BigDecimal.valueOf(100));
    lottery.draw();
    assertThatThrownBy(lottery::draw).hasMessage("Draw is already made");
  }

  @Test
  public void noBallsSelectedTwice() {
    PamLottery lottery = new PamLotteryImpl(10, BigDecimal.valueOf(100));
    assertThat(lottery.draw()).isNotEmpty();
  }

  @Test
  public void example() {
    PamLottery lottery = new PamLotteryImpl(10, BigDecimal.valueOf(100));
    Integer jeanTicket = lottery.purchase("Jean");
    Integer tomTicket = lottery.purchase("Tom");
    logger.info("tickets are {}", Arrays.asList(jeanTicket, tomTicket));

    List<Integer> draw = lottery.draw();
    assertThat(draw).hasSize(3);
    logger.info("Draw is {}", draw);

    Map<Integer, Winners> winners = lottery.winners();
    assertThat(winners.keySet()).hasSize(3);

    assertThat(winners.get(draw.get(0)).stake()).isEqualTo(BigDecimal.valueOf(66));
    assertThat(winners.get(draw.get(1)).stake()).isEqualTo(BigDecimal.valueOf(33));
    assertThat(winners.get(draw.get(2)).stake()).isEqualTo(BigDecimal.valueOf(1));

    assertThat(winners.get(draw.get(0)).owners()).containsExactly("Jean");
    assertThat(winners.get(draw.get(1)).owners()).containsExactly("Tom");
    assertThat(winners.get(draw.get(2)).owners()).isEmpty();

    assertThat(lottery.info()).isEqualTo(BigDecimal.valueOf(1));
  }
}