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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import servlet.core.AbstractDataSourceConnector;
import servlet.core.DataSourceConnector;
import servlet.core.DataSourceConnector.DataSourceException;

/**
  * A simple implementation of the {@link LiveDataBuffer} interface. This implementation
  * provides a data buffer with permanent (application scope) memory/no flushing.
  * 
  * @author phillips
  */
public final class TreeSetLiveDataBuffer
   extends AbstractDataSourceConnector implements LiveDataBuffer {
   
   private final static Logger
      logger = LoggerFactory.getLogger(TreeSetLiveDataBuffer.class);
   
   private final HashMap<String, NavigableSet<TimestampedDatum>>
      buffer;
   
   /**
     * Create a {@link TreeSetLiveDataBuffer} object with custom parameters.<br><br>
     * 
     * See also {@link LiveDataBuffer} and {@link AbstractDataSourceConnector}.
     * 
     * @param simpleName <br>
     *        A nickname for this {@link DataSourceConnector}. This argument must be
     *        non-<code>null</code> and non-empty.
     */
   public TreeSetLiveDataBuffer(final String simpleName) {
      super(Preconditions.checkNotNull(simpleName));
      logger.info("loading live data buffer..");
      this.buffer = new HashMap<String, NavigableSet<TimestampedDatum>>();
      logger.info("live data buffer loaded successfully.");
   }
   
   @Override
   public List<ChartInformation> getKnownCharts() throws DataSourceException {
      final List<ChartInformation>
         knownCharts = new ArrayList<ChartInformation>();
      for(final String record : this.buffer.keySet())
         knownCharts.add(new ChartInformation(record, "Line", this.buffer.get(record).size()));
      return knownCharts;
   }
   
   @Override
   public List<TimestampedDatum> getData(
      final String chartName,
      final double fromTimeOfInterest,
      final boolean inclusive)
      throws DataSourceException {
      if(chartName == null || chartName.isEmpty()) {
         logger.error("get_data: chart name is null or empty [{}]", chartName);
         throw new IllegalArgumentException(
            getClass().getSimpleName() + ": has_chart: chart name is null or empty.");
      }
      if(!hasChart(chartName)) {
         logger.error("create_chart: chart name is null or empty [{}]", chartName);
         throw new DataSourceException(
            getClass().getSimpleName() + ": get_data: chart name is null or empty.");
      }
      return new ArrayList<TimestampedDatum>(this.buffer.get(chartName));
   }
   
   @Override
   public boolean createChart(final String name) {
      if(name == null || name.isEmpty()) {
         logger.error("create_chart: chart name is null or empty [{}]", name);
         throw new IllegalArgumentException(
            getClass().getSimpleName() + ": create_chart: chart name is null or empty.");
      }
      if(hasChart(name))
         return false;
      this.buffer.put(name, new TreeSet<TimestampedDatum>());
      return true;
   }
   
   @Override
   public boolean hasChart(String name) {
      if(name == null || name.isEmpty()) {
         logger.error("has_chart: chart name is null or empty [{}]", name);
         throw new IllegalArgumentException(
            getClass().getSimpleName() + ": has_chart: chart name is null or empty.");
      }
      return this.buffer.containsKey(name);
   }
   
   @Override
   public void uploadData(
      final String name,
      final List<TimestampedDatum> data
      ) throws DataUploadException {
      if(name == null || name.isEmpty()) {
         logger.error("upload_data: chart name is null or empty [{}]", name);
         throw new IllegalArgumentException(
            getClass().getSimpleName() + ": upload_data: chart name is null or empty.");
      }
      if(!this.hasChart(name)) {
         logger.error("upload_data: no chart with name {} exists", name);
         throw new DataUploadException(
            getClass().getSimpleName() + ": upload_data: chart not found.");
      }
      this.buffer.get(name).addAll(data);
   }
}
