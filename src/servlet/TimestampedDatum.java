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
package servlet;

public final class TimestampedDatum {
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
   
   static TimestampedDatum create(
      final double time,
      final double value
      ) {
      return new TimestampedDatum(time, value);
   }
   
   public double getTime() {
      return time;
   }
   
   public double getValue() {
      return value;
   }
}
