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

import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.ServletContext;

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
final class ConfigureLiveReceiverInstruction extends AbstractAdminConfigurationInstruction {
   
   private final static Logger
      logger = LoggerFactory.getLogger(AdminServlet.class);
   
   /**
     * Create a {@link ConfigureLiveReceiverInstruction} object.
     * 
     * @param applicationName <br>
     *        The name of the charting application. This string is required by the user
     *        browser. This argument must be non-<code>null</code> and non-empty.
     */
   public ConfigureLiveReceiverInstruction(final String applicationName) {
      super(applicationName);
   }

   @Override
   public void configure(final ServletContext context)
      throws AdminConfigurationInstructionException {
      super.configure(context);
      try {
         final AtomicBoolean
            liveReceiverServletLock =
               (AtomicBoolean) context.getAttribute("live-data-receiver-initialized");
         liveReceiverServletLock.set(true);
         logger.info("enabled live data receiver servlet.");
         final DataSourceConnector
            dataSourceConnector = (DataSourceConnector)
               context.getAttribute("live-receiver-data-buffer");
         if(dataSourceConnector == null)
            throw new NullPointerException("live data receiver has no data buffer.");
         context.setAttribute("data-source-connector", dataSourceConnector);
         logger.info("connected live data receiver buffer to charting servlet.");
      }
      catch(final Exception e) {
         final String
            errMsg = "failed to configure server as live data receiver: " +
               ExceptionUtils.getRootCauseMessage(e);
         logger.error(errMsg);
         throw new AdminConfigurationInstructionException(errMsg);
      }
   }
}
