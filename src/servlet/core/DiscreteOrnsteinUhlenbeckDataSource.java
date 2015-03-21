package servlet.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;

import servlet.data.ChartDataUtils;
import servlet.data.ChartInformation;
import servlet.data.LineChartData;
import servlet.data.TimestampedDatum;

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
public final class DiscreteOrnsteinUhlenbeckDataSource extends AbstractDataSourceConnector {
   
   private Map<String, LineChartData>
      lineChartData;
   
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
      super("Random Discrete Ornstein Uhlenbeck Data Source");
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
               DiscreteOrnsteinUhlenbeckDataSource.this.lineChartData.values()) {
               if(lineChartData.size() >= 2000)
                  return;
               final int
                  numNewElements = random.nextInt(20);
               for(int i = 0; i< numNewElements; ++i) {
                  final Entry<Double, Double>
                     lastEntry = lineChartData.lastEntry();
                  lineChartData.put(
                     lastEntry.getKey() + 1.0,
                     lastEntry.getValue() * 0.9 + random.nextGaussian() + 10.0
                     );
               }
            }
         }
      }, updateIntervalMilliseconds, updateIntervalMilliseconds);
      
      // Dummy data
      this.lineChartData = new HashMap<String, LineChartData>();
      for(int i = 0; i< numberOfChartsToGenerate; ++i) {
         final LineChartData
            lineChart = ChartDataUtils.createRandomLineChart(200, i, "Random Data " + (i + 1));
         this.lineChartData.put(lineChart.name(), lineChart);
      }
   }
   
   @Override
   public List<ChartInformation> getKnownCharts() {
      final List<ChartInformation>
         result = new ArrayList<ChartInformation>();
      for(final LineChartData chartData : this.lineChartData.values())
         result.add(new ChartInformation(chartData.name(), "Line", chartData.size()));
      return result;
   }
   
   @Override
   public List<TimestampedDatum> getData(
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
