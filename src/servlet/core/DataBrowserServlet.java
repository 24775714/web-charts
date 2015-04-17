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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import servlet.core.DataSourceConnector.DataSourceException;
import servlet.data.TimestampedDatum;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
  * Servlet implementation class DataBrowserServlet
  */
@WebServlet(
   asyncSupported = true,
   description = "Browser for data charts",
   urlPatterns = { "/DataBrowserServlet" }
   )
public final class DataBrowserServlet extends HttpServlet {
   
   private static final long serialVersionUID = 8026415575589209128L;
   
   private final static Logger
      logger = LoggerFactory.getLogger(DataBrowserServlet.class);
   
   private DataSourceConnector
      dataSourceConnector;
   
   private final Gson
      gson;
   
   private AtomicBoolean
      isInitialized;
   
   /**
     * Create a {@link DataBrowserServlet} object.
     * 
     * @see HttpServlet#HttpServlet()
     */
   public DataBrowserServlet() {
      super();
      logger.info("loading data browser servlet..");
      this.dataSourceConnector = null;
      this.gson = new GsonBuilder().create();
      logger.info("data browser servlet loaded successfully.");
   }
   
   @Override
   public void init() throws ServletException {
      super.init();
      this.isInitialized =
         (AtomicBoolean) super.getServletContext().getAttribute("data-browser-initialized");
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
     * Process a <code>list_known_charts</code> request.
     * 
     * @param responseMap <br>
     *        The map to which responses are to be inserted. This argument must be
     *        non-<code>null</code>.
     */
   private void listKnownCharts(final Map<String, Object> responseMap) {
      try {
         responseMap.put("known_charts", this.gson.toJson(
            this.dataSourceConnector.getKnownCharts()));
      } catch (final DataSourceException e) {
         responseMap.put("known_charts", this.gson.toJson(""));
      }
   }
   
   /**
     * Process a <code>get_data_name</code> request.
     * 
     * @param responseMap <br>
     *        The map to which responses are to be inserted. This argument must be
     *        non-<code>null</code>.
     */
   private void processGetDataNameRequest(final Map<String, Object> responseMap) {
      responseMap.put("data_name", this.dataSourceConnector.getDataSourceName());
   }
   
   private static final class DownloadDataRequest {
      private String
         chartName;
      private double
         timeOfInterest;
   }
   
   /**
     * Process a <code>download-data</code> request.
     * 
     * @param values <br>
     *        The raw value list for this parameter.
     * @param responseMap <br>
     *        The map to which responses are to be inserted. This argument must be
     *        non-<code>null</code>.
     */
   private void processDownloadDataRequest(
      final String requestString,
      final Map<String, Object> results
      ) {
      final DownloadDataRequest
         request = this.gson.fromJson(requestString, DownloadDataRequest.class);
      try {
         final List<TimestampedDatum>
            result = this.dataSourceConnector.getData(
               request.chartName, request.timeOfInterest, false);
         results.put(request.chartName, this.gson.toJson(result));
      }
      catch(final DataSourceException e) {
         results.put(request.chartName, this.gson.toJson(""));
      }
   }
   
   /**
     * The POST response generator. This servlet responds as follows:
     * 
     * <ul>
     *    <li> For requests containing exactly one parameter <code>is_ready</code>
     *         with no value, the response is one string <code>true</code> or
     *         <code>false</code> keyed by <code>is_ready</code>.
     * 
     *    <li> For any other type of request, if this servlet has not been initialised
     *         then the response is an empty string keyed by <code>not_ready</code>.
     * 
     *    <li> For parameters named <code>list_known_charts</code> with no value
     *         the response is an ordered {@link List} of strings in JSON format.
     *         This list may or may not be empty, keyed by <code>known-charts</code>.
     * 
     *    <li> For parameters named <code>get_data_name</code> with no value
     *         the response is a single {@link String} keyed by <code>data_name</code>.
     *         This {@link String} is an identifying name for the data stream processed
     *         by this servlet.core.
     * 
     *    <li> For parameters named <code>download_data</code> the expected format
     *         of the value array is <code>X</code> followed by <code>T</code>.
     *         <code>X</code> is the name of a data chart known to this servlet.core.
     *         <code>T</code> is a data-time string parseable as a double.
     *         The request (<code>download-data</code>, [<code>X</code>, <code>T</code>])
     *         is a request for all data belonging to chart <code>X</code> strictly
     *         after (and not including) time <code>T</code>.<br><br>
     * 
     *         The response of the servlet is to insert a key <code>M</code> with
     *         name <code>X</code>. The value of this key is a JSON formatted
     *         array of double pairs <code>{ t1, v1 }, { t2, v2 }</code> with
     *         <code>t > T</code> for all <code>t1, t2 ...</code>.
     *  </ul>
     * </ul>
     * 
     * The servlet response is UTF-8 JSON.<br><br>
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
      
      // Lazy initialisation of data connector:
      if(this.dataSourceConnector == null && this.isInitialized.get())
         this.dataSourceConnector =
            (DataSourceConnector) super.getServletContext().getAttribute("data-source-connector");
      
      final Map<String, String[]>
         parameters = request.getParameterMap();
      final Map<String, Object>
         responseMap = new HashMap<String, Object>();
      
      if(parameters.size() == 1) {
         final Entry<String, String[]>
            record = parameters.entrySet().iterator().next();
         final String
            name = record.getKey();
         if(name.equals("is_ready")) {
            responseMap.put("is_ready", this.isInitialized.get());
            response.getWriter().write(this.gson.toJson(responseMap));
            return;
         }
      }
      
      if(!this.isInitialized.get()) {
         responseMap.put("not_ready", "");
         response.getWriter().write(this.gson.toJson(responseMap));
      }
      
      for(final Entry<String, String[]> record : parameters.entrySet()) {
         final String
            name = record.getKey();
         switch(name) {
         case "download_data":
            for(final String json : record.getValue())
               processDownloadDataRequest(json, responseMap);
            break;
         case "get_data_name":
            processGetDataNameRequest(responseMap);
            break;
         case "list_known_charts":
            listKnownCharts(responseMap);
            break;
         default:
            logger.error("unknown request: {}", name);
         }
      }
      
      response.getWriter().write(this.gson.toJson(responseMap));
   }
}
