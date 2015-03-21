package servlet.core;

import java.util.List;

import servlet.data.ChartInformation;
import servlet.data.TimestampedDatum;

/**
  * An interface describing a connection to a data source.
  * 
  * @author phillips
  */
public interface DataSourceConnector {
   
   /**
     * @return
     *    Get the (pretty-print) name of the data source. The {@link String} returned by
     *    this method does not necessarily uniquely identify the data source. This method
     *    cannot return <code>null</code> and cannot return the empty {@link String}. 
     *  
     */
   public String getDataSourceName();
   
   /**
     * @return
     *    Get the uniquely identifying name of the data source. The {@link String} returned by
     *    this method does uniquely identify the data source. This method cannot return 
     *    <code>null</code> and cannot return the empty {@link String}. 
     */
   public String getDataSourceID();
   
   /**
     * @return
     *    Get an ordered {@link List} of {@link ChartInformation} objects for charts known
     *    to this {@link DataSourceConnector}. This method may return an empty {@link List}
     *    but cannot return <code>null</code>.
     */
   public List<ChartInformation> getKnownCharts();
   
   
   /** 
     * Get an ordered data array for the chart with the specified name. If no such chart
     * is known to this data source, this method returns an empty non-<code>null</code>
     * {@link List}. Otherwise this method returns all data (in order of increasing time
     * stamps) on or after the time of interest for the specified chart. 
     * 
     * @param chartName <br>
     *        The name of the chart data set to query. This argument must be non-<code>null</code>.
     *        If this argument is not known to this data source, this method returns an 
     *        empty {@link List}.
     * @param fromTimeOfInterest (<code>T</code>) <br> 
     *        The time after which data is required for this chart.
     * @param inclusive <br>
     *        Whether or not <code>T</code> is to be considered inclusive or
     *        exclusive in the resulting data.
     * @return
     *    A valid {@link List} of {@link TimestampedDatum} objects.
     */
   public List<TimestampedDatum> getData(
      final String chartName,
      final double fromTimeOfInterest,
      final boolean inclusive
      );
}
