package uk.tw.energy.domain;

/**
 * Enumeration of the two periods of the day when electricity consumption is charged differently.
 */
public enum PeriodType {
  /** Period during which electricity consumption is charged at the peak rate. */
  PEAK,
  /** Period during which electricity consumption is charged at the off-peak rate. */
  OFF_PEAK
}
