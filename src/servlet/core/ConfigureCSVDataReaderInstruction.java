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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
  * An implementation of the {@link AdminConfigurationInstruction} interface.<br><br>
  * 
  * This instruction performs the following configuration:
  * 
  * <ul>
  *  <li> Enables the live receiver servlet (to receive and process incoming chart data)
  *  <li> Configures the data browser servlet with a data connection to the live
  *       receiver servlet.
  * </ul>
  * 
  * @author phillips
  */
final class ConfigureCSVDataReaderInstruction extends AbstractAdminConfigurationInstruction {
   
   private final static Logger
      logger = LoggerFactory.getLogger(AdminServlet.class);
   
   private final String
      fileName,
      nameOfTimeColumn;
   
   /**
     * Create a {@link ConfigureCSVDataReaderInstruction} object.
     * 
     * @param applicationName <br>
     *        The name of the charting application. This string is required by the user
     *        browser. This argument must be non-<code>null</code> and non-empty.
     * @param fileName <br>
     *        The name of the CSV file to parse. This argument must be non-<code>null</code>
     *        and must denote a valid readable path on the filesystem.
     * @param nameOfTimeColumn <br>
     *        The name of the time (t) column in the CSV file. This argument must be
     *        non-<code>null</code>.
     */
   public ConfigureCSVDataReaderInstruction(
      final String applicationName,
      final String fileName,
      final String nameOfTimeColumn
      ) {
      super(applicationName);
      this.fileName = fileName;
      this.nameOfTimeColumn = nameOfTimeColumn;
   }

   @Override
   public void configure(final ServletContext context)
      throws AdminConfigurationInstructionException {
      try {
         final DataSourceConnector
            dataSourceConnector = new TitledCSVDataSource(
               this.fileName, this.nameOfTimeColumn);
         context.setAttribute("data-source-connector", dataSourceConnector);
         logger.info("connected browser servlet to CSV data source.");
      }
      catch(final Exception e) {
         final String
            errMsg = "failed to configure server as CSV data reader: " +
               ExceptionUtils.getRootCauseMessage(e);
         logger.error(errMsg);
         throw new AdminConfigurationInstructionException(
            StringUtils.capitalize(errMsg));
      }
      super.configure(context);
   }
}
