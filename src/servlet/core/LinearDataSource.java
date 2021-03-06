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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import servlet.data.LineChartData;

/**
  * A mock data connector that generates linear data charts. This data connector can
  * be customised to:
  * 
  * <ul>
  *   <li> Generate any number of charts.
  *   <li> Update the data contained in these charts with a specified frequency.
  * </ul>
  * 
  * See also {@link LinearDataSource#LinearDataSource(
  * int, long)} and {@link DataSourceConnector}.<br><br>
  * 
  * @author phillips
  */
public final class LinearDataSource extends AbstractMockLineChartDataSource {
   
   private final Map<String, Double>
      gradients;
   final Random
      random = new Random(1L);
   
   /**
     * Create a {@link LinearDataSource} object with custom parameters.
     * <br><br>
     * 
     * See also {@link DataSourceConnector}.
     * 
     * @param numberOfChartsToGenerate <br>
     *        The number of line to be generated. This argument must be non-negative.
     * @param maximumNumberOfDataPointsPerChart <br>
     *        The maximum number of data points per chart. This argument must be
     *        non-negative.
     * @param maximumNumberOfNewDataPointsPerCycle <br>
     *        The maximum number of new data points to add to each existing line
     *        chart data set per cycle. This argument must be non-negative.
     * @param updateIntervalMilliseconds <br>
     *        The rate at which to update charts in milliseconds. This argument should
     *        be greater than or equal to <code>1</code>. Whenever this interval elapses,
     *        new data is added to each line chart being generated by this class.
     */
   protected LinearDataSource(
      final int numberOfChartsToGenerate,
      final int maximumNumberOfDataPointsPerChart,
      final int maximumNumberOfNewDataPointsPerCycle,
      final long updateIntervalMilliseconds
      ) {
      super(
         "Linear Data Generator Data Source",
         numberOfChartsToGenerate,
         maximumNumberOfDataPointsPerChart,
         maximumNumberOfNewDataPointsPerCycle,
         updateIntervalMilliseconds
         );
      this.gradients = new HashMap<String, Double>();
   }
   
   @Override
   protected void updateChart(final LineChartData chart, int numberOfNewDatums) {
      if(chart.isEmpty()) {
         this.gradients.put(chart.name(), this.random.nextDouble());
         chart.put(0.0, 0.0);
         numberOfNewDatums--;
      }
      final double
         g = this.gradients.get(chart.name());
      for(int i = 0; i< numberOfNewDatums; ++i) {
         final Entry<Double, Double>
            lastEntry = chart.lastEntry();
         chart.put(
            lastEntry.getKey() + 1.0,
            lastEntry.getValue() + g
            );
      }
   }
}