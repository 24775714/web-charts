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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import servlet.core.AdminConfigurationInstruction.AdminConfigurationInstructionException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
  * Servlet implementation class DataBrowserServlet
  */
@WebServlet(
   asyncSupported = true,
   description = "Web Charts Admin",
   urlPatterns = { "/Admin" }
   )
public final class AdminServlet extends HttpServlet {
   
   private static final long serialVersionUID = 5298040988677483321L;
   
   private final static Logger
      logger = LoggerFactory.getLogger(AdminServlet.class);
   
   private final Gson
      gson;
   
   private boolean
      isConfigured;
      
   
   /**
     * Create a {@link AdminServlet} object.
     * 
     * @see HttpServlet#HttpServlet()
     */
   public AdminServlet() {
      logger.info("loading admin servlet..");
      this.gson = new GsonBuilder().create();
      this.isConfigured = false;
      logger.info("admin servlet loaded successfully.");
   }
   
   @Override
   public void init() throws ServletException {
      super.init();
   }
   
   
   /**
     * Process a GET HTTP request.
     * 
     * In the current implementation all requests are handled via POST.
     */
   @Override
   protected void doGet(
      final HttpServletRequest request,
      final HttpServletResponse response
      ) throws ServletException, IOException {
      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");
   }
   
   /**
     * A struct class used by GSON to parse a JSON <code>set_configuration</code> request.<br><br>
     * 
     * See also {@link AdminServlet#doPost(HttpServletRequest, HttpServletResponse)}.
     * 
     * @author phillips
     */
   private static class ConfigurationRequest {
      private String
         mode,
         configuration;
      
      AdminConfigurationInstruction createConfiguration() {
         switch(this.mode) {
         case "live":
            return (new Gson()).fromJson(
               this.configuration, ConfigureLiveReceiverInstruction.class);
         case "csv":
            return (new Gson()).fromJson(
               this.configuration, ConfigureCSVDataReaderInstruction.class);
         case "random":
            return (new Gson()).fromJson(
               this.configuration, ConfigureRandomDataGeneratorInstruction.class);
         default:
            {
            final String
               errMsg = this.mode + " is not a valid operation mode.";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
            }
         }
      }
   }
   
   /**
     * The POST response generator. This Servlet responds as follows:
     * 
     * <ul>
     *    <li> For requests containing a single key-value pair whose key is <code>
     *         is_configured</code> with no value, the response of
     *         this servlet is a boolean keyed with <code>configuration_state</code>. The
     *         value of the boolean is <code>true</code> or <code>false</code> depending on
     *         whether a configuration instruction has already been sent to, and processed by,
     *         this servlet.
     * 
     *    <li> For requests containing a single key-value pair whose key is <code>
     *         set_configuration</code>, the response is a string keyed by
     *         <code>configuration_result</code> whose value is a boolean specifying
     *         whether or not the configuration request was successful. The configuration
     *         request will fail if configuration has already occurred. If the servlet
     *         responds to the request with <code>configuration_result: false</code>,
     *         then the response will also contain a key <code>configuration_error</code>
     *         whose value is a verbose string indicating the nature of the configuration
     *         failure. This string may or may not be empty.
     * 
     *    <li> For requests containing a single key-value pair whose key is <code>
     *         get_operation_type</code> with no value, the response of this
     *         servlet is a single key-value pair whose key is <code>operation_type</code>
     *         and whose value is a single string specifying the data connection operation
     *         mode of the application, unless configuration has not yet happened,
     *         in which case a single key value pair with key <code>not_configured</code>
     *         and no value is returned.
     * 
     *    <li> For requests containing a single key-value pair whose key is <code>
     *         get_configuration</code> and whose value is empty, the response of this
     *         servlet is a single key-value pair whose key is <code>configuration</code>
     *         and whose value is a JSON string representing the configuration state,
     *         unless configuration has not yet happened, in which case a single key value
     *         pair with key <code>not_configured</code> and no value is returned.
     *  </ul>
     * </ul>
     * 
     * The response is UTF-8 JSON.<br><br>
     * 
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
   @Override
   protected void doPost(
      final HttpServletRequest request,
      final HttpServletResponse response
      ) throws ServletException, IOException {
      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");
      
      final Map<String, String[]>
         parameters = request.getParameterMap();
      final Map<String, Object>
         responseMap = new HashMap<String, Object>();
      
      if(parameters.isEmpty() || parameters.size() != 1) {
         logger.error("request is malformed.");
         return;
      }
      
      final Entry<String, String[]>
         record = parameters.entrySet().iterator().next();
      final String
         name = record.getKey();
      final String[]
         values = record.getValue();
      
      switch(name) {
      case "is_configured":
         logger.info("ask: is_configured, response: " + this.isConfigured);
         responseMap.put("configuration_state", this.isConfigured);
         break;
      case "set_configuration":
         final ConfigurationRequest
            configurationRequest = this.gson.fromJson(values[0], ConfigurationRequest.class);
         try {
            final AdminConfigurationInstruction
               instruction = configurationRequest.createConfiguration();
            instruction.configure(super.getServletContext());
            responseMap.put("configuration_result", true);
            this.isConfigured = true;
         }
         catch(final IllegalArgumentException | AdminConfigurationInstructionException e) {
            logger.error(
               "failed to create and execute configuration instruction from "
             + "set_configuration request: " + ExceptionUtils.getRootCauseMessage(e) + ".");
            responseMap.put("configuration_result", false);
            responseMap.put("configuration_error", e.getMessage());
         }
         break;
      default:
         
      }
      
      response.getWriter().write(this.gson.toJson(responseMap));
   }
}
