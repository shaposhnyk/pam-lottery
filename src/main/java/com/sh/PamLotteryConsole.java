package com.sh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Scanner;

/**
 * Lottery console wrapper
 */
public class PamLotteryConsole {
  private static final Logger logger = LoggerFactory.getLogger(PamLotteryConsole.class);
  private static final String PURCHASE = "purchase";
  private final Scanner scanner = new Scanner(System.in);

  public static void main(String[] args) {
    new PamLotteryConsole().run();
  }

  private PamLottery lottery = new PamLotteryImpl(1, BigDecimal.valueOf(100));

  private void run() {
    while (true) {
      String input = readInput();
      if ("exit".equals(input)) {
        return;
      }
      try {
        processInputCommand(input);
      } catch (RuntimeException e) {
        logger.error("Error", e);
        print("Invalid input: " + e.getMessage());
      }
    }

  }

  private void processInputCommand(String input) {
    switch (input.toLowerCase()) {
      case "nextMonth":
        lottery = new PamLotteryImpl(1, lottery.info());
      case "info":
        print(String.format("The PAM Lottery pot is %s EUR", lottery.info()));
        break;
      case "draw":
        print("Lottery draw is: " + lottery.draw());
        break;
      case "winners":
        Map<Integer, Winners> m = lottery.winners();
        print("Winners: " + m.values());
        break;
      default:
        if (!input.startsWith(PURCHASE)) {
          print("Unknown command: " + input);
        } else {
          final String buyer = input.substring(PURCHASE.length() + 1).trim();
          final Integer ticket = lottery.purchase(buyer);
          print(String.format("Ticket #%s was bought by %s", ticket, buyer));
        }
    }
  }

  private void print(String message) {
    System.out.println(message);
  }

  private String readInput() {
    return scanner.nextLine()
        .trim();
  }
}
