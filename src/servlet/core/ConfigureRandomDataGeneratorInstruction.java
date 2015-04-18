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

import javax.servlet.ServletContext;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
  * An implementation of the {@link AdminConfigurationInstruction} interface.<br><br>
  * 
  * This instruction configures the data browser servlet to display randomly
  * generated data. The type of the random series is customisable.
  * 
  * @author phillips
  */
final class ConfigureRandomDataGeneratorInstruction extends AbstractAdminConfigurationInstruction {
   
   private final static Logger
      logger = LoggerFactory.getLogger(AdminServlet.class);
   
   private final String
      dataType;
   private final int
      numberOfChartsToGenerate,
      maximumNumberOfDataPointsPerChart,
      maximumNumberOfNewDataPointsPerCycle;
   private final long
      updateIntervalMilliseconds;
   
   /**
     * Create a {@link ConfigureRandomDataGeneratorInstruction} object.
     * 
     * @param applicationName <br>
     *        The name of the charting application. This string is required by the user
     *        browser. This argument must be non-<code>null</code> and non-empty.
     * @param dataType <br>
     *        A {@link String} specifying the type of random series to produce.<br/><br/>
     *        Possibilities for this argument include:
     *        <ul>
     *         <li> <code>"Ornstein-Uhlenbeck"</code> - a random mean reverting
     *              Ornstein-Uhlenbeck timeseries.
     *         <li> <code>"Linear"</code> - a linear timeseries with a random positive
     *              gradient.
     *        </ul>
     *        This argument must be non-<code>null</code>.
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
     * @throws IllegalArgumentException If any of the above conditions are not true of the
     *        arguments.
     */
   public ConfigureRandomDataGeneratorInstruction(
      final String applicationName,
      final String dataType,
      final int numberOfChartsToGenerate,
      final int maximumNumberOfDataPointsPerChart,
      final int maximumNumberOfNewDataPointsPerCycle,
      final long updateIntervalMilliseconds
      ) {
      super(applicationName);
      if(numberOfChartsToGenerate < 0)
         throw new IllegalArgumentException(
            getClass().getSimpleName() + ": number of charts to generate is negative (value: "
            + numberOfChartsToGenerate + ")");
      if(updateIntervalMilliseconds <= 1L)
         throw new IllegalArgumentException(
            getClass().getSimpleName() + ": update interval is less than 1 millisecond (value: "
            + updateIntervalMilliseconds + "ms)");
      if(maximumNumberOfDataPointsPerChart < 0)
         throw new IllegalArgumentException(
            getClass().getSimpleName() + ": maximum number of data points per chart is negative.");
      if(maximumNumberOfNewDataPointsPerCycle < 0)
         throw new IllegalArgumentException(
            getClass().getSimpleName() + ": maximum number of new data points per cycle is"
               + " negative.");
      if(dataType == null)
         throw new IllegalArgumentException("data type specification is null.");
      if(dataType.equals("Ornstein-Uhlenbeck") || dataType.equals("Linear"))
         throw new IllegalArgumentException("data type (" + dataType + ") is unrecognized.");
      this.dataType = dataType;
      this.numberOfChartsToGenerate = numberOfChartsToGenerate;
      this.maximumNumberOfDataPointsPerChart = maximumNumberOfDataPointsPerChart;
      this.maximumNumberOfNewDataPointsPerCycle = maximumNumberOfNewDataPointsPerCycle;
      this.updateIntervalMilliseconds = updateIntervalMilliseconds;
   }
   
   @Override
   public void configure(final ServletContext context)
      throws AdminConfigurationInstructionException {
      super.configure(context);
      try {
         final DataSourceConnector
            dataSourceConnector;
         switch(this.dataType) {
         case "Ornstein-Uhlenbeck":
            dataSourceConnector = new DiscreteOrnsteinUhlenbeckDataSource(
               this.numberOfChartsToGenerate,
               this.maximumNumberOfDataPointsPerChart,
               this.maximumNumberOfNewDataPointsPerCycle,
               this.updateIntervalMilliseconds
               );
            break;
         case "Linear":
            dataSourceConnector = new LinearDataSource(
               this.numberOfChartsToGenerate,
               this.maximumNumberOfDataPointsPerChart,
               this.maximumNumberOfNewDataPointsPerCycle,
               this.updateIntervalMilliseconds
               );
            break;
         default:
            throw new AdminConfigurationInstructionException(
               "data type '" + this.dataType + "' is not recognized.");
         }
         context.setAttribute("data-source-connector", dataSourceConnector);
         logger.info("connected random data generator, type: " + this.dataType);
      }
      catch(final Exception e) {
         final String
            errMsg = "failed to configure server as live data receiver: " +
               ExceptionUtils.getRootCauseMessage(e);
         logger.error(errMsg);
         throw new AdminConfigurationInstructionException(errMsg);
      }
      super.configure(context);
   }
}
