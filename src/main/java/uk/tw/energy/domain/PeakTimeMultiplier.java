package uk.tw.energy.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Objects;
import uk.tw.energy.util.CustomLocalDateTimeDeserializer;

/**
 * Represents a peak time multiplier.
 *
 * <p>A peak time multiplier is a multiplier that is applied to electricity readings during a
 * specific period and day of the week.
 */
public class PeakTimeMultiplier {

  /** The period of the multiplier. */
  public PeriodType period;

  /** The day of the week when the multiplier is applicable. */
  public DayOfWeek dayOfWeek;

  /** The multiplier value. */
  public BigDecimal multiplier;

  /** The start date and time of the multiplier. */
  @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
  public LocalDateTime startDateTime;

  /** The end date and time of the multiplier. */
  @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
  public LocalDateTime endDateTime;

  /**
   * Constructs a peak time multiplier.
   *
   * @param period the period of the multiplier
   * @param dayOfWeek the day of the week when the multiplier is applicable
   * @param multiplier the multiplier value
   * @param startDateTime the start date and time of the multiplier
   * @param endDateTime the end date and time of the multiplier
   */
  public PeakTimeMultiplier(
      PeriodType period,
      DayOfWeek dayOfWeek,
      BigDecimal multiplier,
      LocalDateTime startDateTime,
      LocalDateTime endDateTime) {
    this.period = period;
    this.dayOfWeek = dayOfWeek;
    this.multiplier = multiplier;
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
  }

  /**
   * Returns the multiplier value.
   *
   * @return the multiplier value
   */
  public BigDecimal getMultiplier() {
    return multiplier;
  }

  /**
   * A description of the entire Java function.
   *
   * @param dateTime description of parameter
   * @return description of return value
   */
  public boolean isActiveDuring(LocalDateTime dateTime) {
    return (period == PeriodType.PEAK || period == PeriodType.OFF_PEAK)
        && dateTime.getDayOfWeek() == dayOfWeek
        && !dateTime.isBefore(startDateTime)
        && !dateTime.isAfter(endDateTime);
  }

  /**
   * Checks if this object is equal to the given object.
   *
   * @param o the object to compare with
   * @return true if the objects are equal, false otherwise
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PeakTimeMultiplier that = (PeakTimeMultiplier) o;
    return dayOfWeek == that.dayOfWeek
        && multiplier.equals(that.multiplier)
        && startDateTime.equals(that.startDateTime)
        && endDateTime.equals(that.endDateTime)
        && period == that.period;
  }

  /** A description of the entire Java function. */
  @Override
  public int hashCode() {
    return Objects.hash(dayOfWeek, multiplier, startDateTime, endDateTime, period);
  }
}
