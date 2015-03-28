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
   
   private final DataSourceConnector
      dataSourceConnector;
   
   private final Gson
      gson;
   
   /**
     * Create a {@link DataBrowserServlet} object.
     * 
     * @see HttpServlet#HttpServlet()
     */
   public DataBrowserServlet() {
      logger.info("loading data browser servlet..");
      this.dataSourceConnector = new DiscreteOrnsteinUhlenbeckDataSource(5, 1000L);
      this.gson = new GsonBuilder().create();
      logger.info("data browser servlet loaded successfully.");
   }
   
   /**
     * Process a GET HTTP request.
     * 
     * In the current implementation all requests are handled via POST.
     */
   protected void doGet(
      final HttpServletRequest request, 
      HttpServletResponse response
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
   private void listKnownCharts(Map<String, String> responseMap) {
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
   private void processGetDataNameRequest(Map<String, String> responseMap) {
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
      final Map<String, String> results
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
     * The POST response generator. This Servlet responds as follows:
     * 
     * <ul>
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
     *         The response of the servlet.core is to insert a key <code>M</code> with
     *         name <code>X</code>. The value of this key is a JSON formatted 
     *         array of double pairs <code>{ t1, v1 }, { t2, v2 }</code> with
     *         <code>t > T</code> for all <code>t1, t2 ...</code>.
     *  </ul>
     * </ul>
     * 
     * The servlet.core response is UTF-8 JSON.<br><br>
     * 
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
   protected void doPost(
      final HttpServletRequest request,
      HttpServletResponse response
      ) throws ServletException, IOException {
      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");
      
      final Map<String, String[]>
         parameters = request.getParameterMap();
      
      final Map<String, String>
         responseMap = new HashMap<String, String>();
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
