package com.sh;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class WinnersImpl implements Winners {
  private final List<String> owners;
  private final BigDecimal stake;

  public WinnersImpl(BigDecimal stake, String ... owners) {
    this(stake, Arrays.asList(owners));
  }

  public WinnersImpl(BigDecimal stake, List<String> owners) {
    this.owners = owners;
    this.stake = stake;
  }

  @Override
  public BigDecimal stake() {
    return stake;
  }

  @Override
  public List<String> owners() {
    return Collections.unmodifiableList(owners);
  }

  @Override
  public String toString() {
    return "{" +
        "stake=" + stake +
        ", owners=" + owners +
        '}';
  }
}
