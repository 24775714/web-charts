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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;

import servlet.data.ChartInformation;
import servlet.data.LineChartData;
import servlet.data.TimestampedDatum;

/**
  * A skeletal base class for mock numerical line chart data sources. This class provides
  * the following methods:
  * 
  * <ul>
  *   <li> {@link DataSourceConnector#getKnownCharts}
  *   <li> {@link DataSourceConnector#getData}
  * </ul>
  * 
  * Implementing class must provide the method {@link #updateChart(LineChartData, int)},
  * which should install new data in the specified chart as required.
  * 
  * This class extends {@link AbstractDataSourceConnector}.<br><br>
  * 
  * See also {@link AbstractMockLineChartDataSource#AbstractMockLineChartDataSource(
  * String, int, long)} and {@link DataSourceConnector}.<br><br>
  * 
  * @author phillips
  */
abstract class AbstractMockLineChartDataSource extends AbstractDataSourceConnector {
   
   private Map<String, LineChartData>
      lineChartData;
   
   /** 
     * Create a {@link AbstractMockLineChartDataSource} object with custom parameters.
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
   protected AbstractMockLineChartDataSource(
      final String displayName,
      final int numberOfChartsToGenerate,
      final long updateIntervalMilliseconds
      ) {
      super(displayName);
      if(numberOfChartsToGenerate < 0)
         throw new IllegalArgumentException(
            getClass().getSimpleName() + ": number of charts to generate is negative (value: "
            + numberOfChartsToGenerate + ")");
      if(updateIntervalMilliseconds <= 1L)
         throw new IllegalArgumentException(
            getClass().getSimpleName() + ": update interval is less than 1 millisecond (value: "
            + updateIntervalMilliseconds + "ms)");
      final Random
         random = new Random(1L);
      (new Timer()).scheduleAtFixedRate(new TimerTask() {
         @Override
         public void run() {
            for(LineChartData lineChartData : 
               AbstractMockLineChartDataSource.this.lineChartData.values()) {
               if(lineChartData.size() >= 800)
                  return;
               final int
                  numDatumsToInsert = random.nextInt(20);
               if(numDatumsToInsert == 0)
                  return;
               AbstractMockLineChartDataSource.this.updateChart(
                  lineChartData, numDatumsToInsert);
            }
         }
      }, updateIntervalMilliseconds, updateIntervalMilliseconds);
      this.lineChartData = new HashMap<String, LineChartData>();
      for(int i = 0; i< numberOfChartsToGenerate; ++i) {
         final String
            chartName = "Series " + (i + 1);
         this.lineChartData.put(chartName, new LineChartData(chartName));
      }
   }
   
   /**
     * This method is called whenever this object requires new data to be inserted into
     * the specified {@link LineChartData}. This method should generate, and insert, the
     * required number of datums.
     * 
     * @param chart (<code>C</code>) <br>
     *        The chart into which new data should be inserted. This argument cannot be
     *        <code>null</code>.
     * @param numberOfNewDatums <br>
     *        The number of new data points to add to <code>C</code>. This argument is
     *        positive and cannot be zero.
     */
   protected abstract void updateChart(
      LineChartData chart,
      final int numberOfNewDatums
      );
   
   @Override
   public final List<ChartInformation> getKnownCharts() {
      final List<ChartInformation>
         result = new ArrayList<ChartInformation>();
      for(final LineChartData chartData : this.lineChartData.values())
         result.add(new ChartInformation(chartData.name(), "Line", chartData.size()));
      return result;
   }
   
   @Override
   public final List<TimestampedDatum> getData(
      String chartName,
      final double fromTimeOfInterest,
      final boolean inclusive
      ) {
      final List<TimestampedDatum>
         result = new ArrayList<TimestampedDatum>();
      if(!this.lineChartData.containsKey(chartName))
         return result;
      for(Entry<Double, Double> record : 
         this.lineChartData.get(chartName).tailMap(
            fromTimeOfInterest, inclusive).entrySet())
        result.add(TimestampedDatum.create(record.getKey(), record.getValue()));
      return result;
   }
}