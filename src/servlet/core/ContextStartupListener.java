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

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.common.Properties;
import net.engio.mbassy.bus.config.BusConfiguration;
import net.engio.mbassy.bus.config.Feature;
import net.engio.mbassy.bus.error.IPublicationErrorHandler;
import net.engio.mbassy.bus.error.PublicationError;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
  * A {@link ServletContextListener}. This context listener adds attributes
  * to the global servlet context, including:
  * 
  * <ul>
  *  <li> a reference to a {@link DataSourceConnector}, and
  *  <li> an event bus.
  * </ul>
  * 
  * @author phillips
  */
public final class ContextStartupListener implements ServletContextListener {
   
   private final static Logger
      logger = LoggerFactory.getLogger(ContextStartupListener.class);
   
   @Override
   public void contextDestroyed(final ServletContextEvent event) {
      logger.info("servlet context is shutting down.");
   }
   
   private static final class BusPublicationErrorHandler implements IPublicationErrorHandler {
      private final static Logger
         logger = LoggerFactory.getLogger(IPublicationErrorHandler.class);
      
      @Override
      public void handleError(final PublicationError error) {
         logger.error(
            "failed to bus " + error.getPublishedMessage() + ": " + error.getMessage() + ".");
      }
   }
   
   @Override
   public void contextInitialized(final ServletContextEvent event) {
      logger.info("binding data to servlet context..");
      event.getServletContext().setAttribute("data-source-connector", null);
      final MBassador<Object>
         bus = new MBassador<Object>(new BusConfiguration()
            .addFeature(Feature.SyncPubSub.Default())
            .addFeature(Feature.AsynchronousHandlerInvocation.Default())
            .addFeature(Feature.AsynchronousMessageDispatch.Default())
            .setProperty(Properties.Common.Id, "web application bus")
            .setProperty(Properties.Handler.PublicationError, new BusPublicationErrorHandler()
            ));
      event.getServletContext().setAttribute("bus", bus);
      event.getServletContext().setAttribute("data-browser-initialized", new AtomicBoolean());
      event.getServletContext().setAttribute("live-data-receiver-initialized", new AtomicBoolean());
      logger.info("success.");
   }
}
