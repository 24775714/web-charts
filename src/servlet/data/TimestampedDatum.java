/**
  * This file is part of web-charts, an interactive web charts program.
  *
  * Copyright (C) 2015 John Kieran Phillips
  * 
  * web-charts is free software: you can redistribute it and/or modify
  * it under the terms of the GNU General Public License as published by
  * the Free Software Foundation, either version 3 of the License, or
  * (at your option) any later version.
  * 
  * web-charts is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU General Public License for more details.
  * 
  * You should have received a copy of the GNU General Public License
  * along with web-charts.  If not, see <http://www.gnu.org/licenses/>.
  */
package servlet.data;

/**
  * A simple immutable representation of a single time-stamped record. Possible applications
  * of this class include Y(X) line chart data records and time stamped histogram data.
  * 
  * @author phillips
  */
public final class TimestampedDatum implements Comparable<TimestampedDatum> {
   
   private final double
      time,
      value;
   
   TimestampedDatum(
      final double time,
      final double value
      ) {
      super();
      this.time = time;
      this.value = value;
   }
   
   /** 
     * Create a {@link TimestampedDatum} object with custom parameters.
     * 
     * @param time <br>
     *        The time of this record.
     * @param value <br>
     *        The value of this record.
     * @return
     *    A valid {@link TimestampedDatum} object.
     */
   static public TimestampedDatum create(
      final double time,
      final double value
      ) {
      return new TimestampedDatum(time, value);
   }
   
   /**
     * @return
     *    Get the time stamp value of this record.
     */
   public double getTime() {
      return this.time;
   }
   
   /**
     * @return
     *    Get the value of this record.
     */
   public double getValue() {
      return this.value;
   }

   @Override
   public int compareTo(final TimestampedDatum o) {
      return Double.compare(this.time, o.time);
   }
}
