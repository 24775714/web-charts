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
import java.util.List;
import java.util.Map;
import java.util.Set;

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
     * @throws DataSourceException if the underlying data connection has failed, or if the
     *         request made to this method was valid but the data connection could not 
     *         provide the result because of the state of the connection.
     */
   public List<ChartInformation> getKnownCharts()
      throws DataSourceException;
   
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
     * @throws DataSourceException if the underlying data connection has failed, or if the
     *         request made to this method was valid but the data connection could not 
     *         provide the result because of the state of the connection.
     */
   public List<TimestampedDatum> getData(
      final String chartName,
      final double fromTimeOfInterest,
      final boolean inclusive
      ) throws DataSourceException;
   
   /**
     * Get ordered data arrays for the charts with the specified names. This method returns a
     * {@link Map} whose keys are the elements in the first argument. If, for any such key,
     * no such chart is known to this data source, this method returns an empty
     * non-<code>null</code> {@link List} in the corresponding key value slot. Otherwise this
     * method returns all data (in order of increasing time stamps) on or after the time of 
     * interest for the specified chart in the corresponding key value slot.
     * 
     * @param chartNames <br>
     *        The names of the charts to query. This argument must be non-<code>null</code>.
     * @param fromTimeOfInterest (<code>T</code>) <br> 
     *        The time after which data is required for this chart.
     * @param inclusive <br>
     *        Whether or not <code>T</code> is to be considered inclusive or
     *        exclusive in the resulting data.
     * @return
     *    A valid {@link List} of {@link TimestampedDatum} objects.
     * @throws DataSourceException if the underlying data connection has failed, or if the
     *         request made to this method was valid but the data connection could not 
     *         provide the result because of the state of the connection.
     */
   public default Map<String, List<TimestampedDatum>> getData(
      final Set<String> chartNames,
      final double fromTimeOfInterest,
      final boolean inclusive
      ) throws DataSourceException {
      final Map<String, List<TimestampedDatum>>
         result = new HashMap<String, List<TimestampedDatum>>();
      for(final String record : chartNames)
         result.put(record, getData(record, fromTimeOfInterest, inclusive));
      return result;
   }
   
   /**
     * A simple exception indicating a problem with the data connection.
     * 
     * @author phillips
     */
   public final class DataSourceException extends Exception {
      
      /**
        * Create a {@link DataSourceException} with no exception message.
        */
      public DataSourceException() { super(); }
      
      /**
        * Create a {@link DataSourceException} with an exception message.
        * 
        * @param message <br>
        *        The exception message. If this argument is <code>null</code>, the
        *        exception message is taken to be the empty {@link String}.
        */
      public DataSourceException(final String message) {
         super(message == null ? "" : message);
      }
      
      /**
        * Create a {@link DataSourceException} with a {@link Throwable} cause.
        * 
        * @param cause <br>
        *        The cause of the {@link DataSourceException}. This argument should
        *        be non-<code>null</code>.
        */
      public DataSourceException(Throwable cause) {
         super(cause);
      }
      
      private static final long serialVersionUID = 3444706161307784699L;
   }
}
