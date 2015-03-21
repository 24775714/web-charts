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
public final class LinearDataSource extends AbstractDataSourceConnector {
   
   private final Map<String, LineChartData>
      lineChartData;
   private final Map<String, Double>
      gradients;
   
   /** 
     * Create a {@link LinearDataSource} object with custom parameters.
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
   public LinearDataSource(
      final int numberOfChartsToGenerate,
      final long updateIntervalMilliseconds
      ) {
      super("Linear Data Generator Data Source");
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
               LinearDataSource.this.lineChartData.values()) {
               if(lineChartData.size() >= 2000)
                  return;
               final int
                  numNewElements = random.nextInt(20);
               for(int i = 0; i< numNewElements; ++i) {
                  final Entry<Double, Double>
                     lastEntry = lineChartData.lastEntry();
                  lineChartData.put(
                     lastEntry.getKey() + 1.0,
                     lastEntry.getValue() + 
                        LinearDataSource.this.gradients.get(lineChartData.name())
                     );
               }
            }
         }
      }, updateIntervalMilliseconds, updateIntervalMilliseconds);
      
      // Dummy data
      this.lineChartData = new HashMap<String, LineChartData>();
      this.gradients = new HashMap<String, Double>();
      for(int i = 0; i< numberOfChartsToGenerate; ++i) {
         final LineChartData
            lineChart = new LineChartData("Linear Data " + (i + 1));
         lineChart.put(0.0, 0.0);
         this.lineChartData.put(lineChart.name(), lineChart);
         this.gradients.put(lineChart.name(), random.nextDouble());
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
