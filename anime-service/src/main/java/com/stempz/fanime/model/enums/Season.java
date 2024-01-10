package com.stempz.fanime.model.enums;

import static java.time.Month.APRIL;
import static java.time.Month.AUGUST;
import static java.time.Month.DECEMBER;
import static java.time.Month.FEBRUARY;
import static java.time.Month.JANUARY;
import static java.time.Month.JULY;
import static java.time.Month.JUNE;
import static java.time.Month.MARCH;
import static java.time.Month.MAY;
import static java.time.Month.NOVEMBER;
import static java.time.Month.OCTOBER;
import static java.time.Month.SEPTEMBER;

import java.time.Month;
import java.util.List;

public enum Season {
  WINTER(List.of(DECEMBER, JANUARY, FEBRUARY)),
  SPRING(List.of(MARCH, APRIL, MAY)),
  SUMMER(List.of(JUNE, JULY, AUGUST)),
  FALL(List.of(SEPTEMBER, OCTOBER, NOVEMBER));

  private final List<Month> months;

  Season(List<Month> months) {
    this.months = months;
  }

  public boolean contains(Month month) {
    return months.contains(month);
  }
}
