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
package servlet.core;

import java.util.NavigableMap;
import java.util.Random;
import java.util.Map.Entry;

import servlet.data.ChartDataUtils;
import servlet.data.LineChartData;

/**
  * A mock data connector that generates random charts from a discrete Ornstein-Uhlenbeck
  * process. This data connector can be customised to:
  * 
  * <ul>
  *   <li> Generate any number of charts.
  *   <li> Update the data contained in these charts with a specified frequency.
  * </ul>
  * 
  * See also {@link DiscreteOrnsteinUhlenbeckDataSource#DiscreteOrnsteinUhlenbeckDataSource(
  * int, long)} and {@link DataSourceConnector}.<br><br>
  * 
  * @author phillips
  */
public final class DiscreteOrnsteinUhlenbeckDataSource extends AbstractMockLineChartDataSource {
   
   final Random
      random = new Random(1L);
   
   /** 
     * Create a {@link DiscreteOrnsteinUhlenbeckDataSource} object with custom parameters.
     * <br><br>
     * 
     * See also {@link DataSourceConnector}.
     * 
     * @param numberOfChartsToGenerate <br>
     *        The number of distinct charts to randomly generate.
     * @param updateIntervalMilliseconds <br>
     *        The rate at which to update charts in milliseconds. This argument should
     *        be greater than or equal to <code>1</code>.
     */
   public DiscreteOrnsteinUhlenbeckDataSource(
      final int numberOfChartsToGenerate,
      final long updateIntervalMilliseconds
      ) {
      super("Random Discrete Ornstein Uhlenbeck Generator Data Source",
         numberOfChartsToGenerate, updateIntervalMilliseconds);
   }
   
   @Override
   protected void updateChart(LineChartData chart, int numberOfNewDatums) {
      /*
       * Discrete OU processes are mean reverting, so a small amount of data needs to be
       * generated to examine the long-term mean:
       */
      double
         s;
      if(chart.isEmpty()) {
         final NavigableMap<Double, Double>
            initialValues = ChartDataUtils.createRandomDiscreteOrnsteinUhlenbeckSeries(
               200, this.random.nextLong());
         s = initialValues.lastEntry().getValue();
         chart.put(0.0, s);
         numberOfNewDatums--;
      }
      else
         s = chart.lastEntry().getValue();
      for(int i = 0; i< numberOfNewDatums; ++i) {
         final Entry<Double, Double>
            lastEntry = chart.lastEntry();
         chart.put(
            lastEntry.getKey() + 1.0,
            lastEntry.getValue() * 0.9 + this.random.nextGaussian() + 10.0
            );
      }
   }
}
