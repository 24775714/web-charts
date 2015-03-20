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

import java.util.Random;

import com.google.common.base.Preconditions;

/**
  * Static utility functions for chart data.
  * 
  * @author phillips
  */
public final class ChartDataUtils {
   /**
     * Create a {@link LineChartData} containing random data.
     * 
     * @param length <br>
     *        The total number of data points to generate. This argument
     *        must be non-negative.
     * @param seed <br>
     *        A {@link Long} used to seed a random number generator.
     * @param chartName <br>
     *        The name of the chart to generate. This argument must be non-<code>null</code>.
     * @return
     *        A fully formed {@link LineChartData} object.
     */
   public static LineChartData createRandomLineChart(
      final int length,
      final long seed,
      final String chartName
      ) {
      Preconditions.checkArgument(
         length >= 0, "createRandomLineChart: the length of the line chart to"
       + " generate is negative.");
      final Random
         random = new Random(seed);
      double
         value = 0.0,
         t = 0.0;
      final LineChartData
         result = new LineChartData(Preconditions.checkNotNull(chartName));
      for(int i = 0; i< 300 + length; ++i) {
         value = 0.9 * value + random.nextGaussian() + 10.;
         t += 1.0;
         if(i > 300)
            result.put(t, value);
      }
      return result;
   }
   
   private ChartDataUtils () { }
}
