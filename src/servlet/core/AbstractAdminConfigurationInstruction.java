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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

/**
  * A skeletal implementation of the {@link AdminConfigurationInstruction} interface.
  * This skeletal implementation provides a field and getter describing the name of
  * the charting application to be configured.<br><br>
  * 
  * This class should retain package-private visibility.<br><br>
  * 
  * See also {@link AdminConfigurationInstruction}.
  * 
  * @author phillips
  */
abstract class AbstractAdminConfigurationInstruction implements AdminConfigurationInstruction {
   
   private final static Logger
      logger = LoggerFactory.getLogger(AdminConfigurationInstruction.class);
   
   private final String
      applicationName;
   
   /**
     * Create an {@link AbstractAdminConfigurationInstruction} object.
     * 
     * @param applicationName <br>
     *        The name of the charting application. This string is required by the user
     *        browser. This argument must be non-<code>null</code> and non-empty.
     */
   protected AbstractAdminConfigurationInstruction(
      final String applicationName) {
      this.applicationName = Preconditions.checkNotNull(applicationName);
   }
   
   /**
     * When overriding this method, call super.configure as a first instruction
     */
   @Override
   public void configure(final ServletContext context)
      throws AdminConfigurationInstructionException {
      final AtomicBoolean
      dataBrowserServletLock =
         (AtomicBoolean) context.getAttribute("data-browser-initialized");
      dataBrowserServletLock.set(true);
      logger.info("enabled client access to data browser servlet.");
   }
   
   public final String getApplicationName() {
      return this.applicationName;
   }
}
