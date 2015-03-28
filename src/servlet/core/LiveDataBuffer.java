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

import java.util.List;

import servlet.core.DataSourceConnector.DataSourceException;
import servlet.data.TimestampedDatum;

/**
  * A (non-servlet) interface for a live data buffer. Implementations of this interface
  * accept live incoming chart data and subsequently allow this stream to be accessed (read)
  * as a {@link DataSourceConnector}.<br><br>
  * 
  * See also {@link DataSourceConnector}.
  * 
  * @author phillips
  */
public interface LiveDataBuffer extends DataSourceConnector {
   
   /**
     * Register a chart with the specified name. This method creates a memory space for
     * new incoming live data belonging to the specified chart.
     * 
     * @param name <br>
     *    The name of the chart for which to process incoming data. This argument must be
     *    non-<code>null</code> and non-empty.
     * @return
     *    <code>True</code> when and only when the chart creation was successful.
     * @throws IllegalArgumentException if <code>name</code> is <code>null</code> or empty.
     */
   public boolean createChart(final String name);
   
   /**
     * Tell whether or not this {@link LiveDataBuffer} is processing chart data for a chart
     * with the specified name.
     * 
     * @param name <br>
     *     The name of the chart for which to perform the query. This argument must be
     *     non-<code>null</code> and non-empty.
     * @return
     *     <code>True</code> when and only when it is the case that this object is 
     *     processing data for a chart with the specified name.
     * @throws IllegalArgumentException if <code>name</code> is <code>null</code> or empty.
     */
   public boolean hasChart(final String name);
   
   /**
     * Upload data for the chart with the specified name.
     * 
     * @param chartName <br>
     *        The name of the chart for which to upload data. This argument must be
     *        non-<code>null</code> and non-empty.
     * @param data <br>
     *        An ordered (time-ascending) array of data to upload. This argument must be
     *        non-<code>null</code>.
     * @throws DataUploadException if no such chart is known to this object, or if the
     *        incoming data stream could not be processed.
     * @throws IllegalArgumentException if any argument is <code>null</code>, or of the
     *        argument <code>chartName</code> is an empty {@link String}.
     */
   public void uploadData(
      final String chartName,
      final List<TimestampedDatum> data
      ) throws DataUploadException;
   
   /**
     * A simple exception indicating a problem with a {@link LiveDataBuffer} object.
     * 
     * @author phillips
     */
   public final class DataUploadException extends Exception {
      
      /**
        * Create a {@link DataSourceException} with no exception message.
        */
      public DataUploadException() { super(); }
       
      /**
        * Create a {@link DataSourceException} with an exception message.
        * 
        * @param message <br>
        *        The exception message. If this argument is <code>null</code>, the
        *        exception message is taken to be the empty {@link String}.
        */
      public DataUploadException(final String message) {
         super(message == null ? "" : message);
      }
      
      /**
        * Create a {@link DataSourceException} with a {@link Throwable} cause.
        * 
        * @param cause <br>
        *        The cause of the {@link DataSourceException}. This argument should
        *        be non-<code>null</code>.
        */
      public DataUploadException(Throwable cause) {
         super(cause);
      }
      
      private static final long serialVersionUID = -6044319046928871831L;
   }
}
