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
import java.util.Arrays;
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

import servlet.data.TimestampedDatum;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

/**
  * A servlet dedicated to processing live data requests.
  * 
  * @author phillips
  */
@WebServlet(
   asyncSupported = true,
   description = "Live chart data receiver",
   urlPatterns = { "/LiveDataReceiver" }
   )
public final class LiveDataReceiver extends HttpServlet {
   
   private static final long serialVersionUID = 8026415575589209128L;
   
   private final static Logger
      logger = LoggerFactory.getLogger(LiveDataReceiver.class);
   
   private final Gson
      gson;
   
   private final LiveDataBuffer
      dataBuffer;
   
   /**
     * Create a {@link LiveDataReceiver} object.<br><br>
     * 
     * See also {@link LiveDataReceiver} and {@link HttpServlet}.
     */
   public LiveDataReceiver() {
      logger.info("creating live data latch receiver.");
      this.gson = new GsonBuilder().create();
      this.dataBuffer = new TreeSetLiveDataBuffer("Live Data Receiver");
      logger.info("live data servlet loaded successfully.");
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
     * Process a <code>create_chart</code> request.<br><br>
     * 
     * This method will insert a key <code>"create_chart: [chart name]"</code> (where
     * <code>[chart name]</code> is the name of the chart in the first argument)
     * into the response map. The value of this key is either:
     * 
     * <ul>
     *   <li> <code>success</code> if the chart was created successfully, or
     *   <li> <code>failure</code> if the chart was not created successfully or if the
     *        the name of the chart was <code>null</code> or the empty {@link String}.
     * </ul>
     * 
     * @param chartName <br>
     *        The name of the chart to create. If this argument is <code>null</code> or was the
     *        empty string, this method creates no chart and does nothing.
     * @param responseMap <br>
     *        The map to which responses are to be inserted. This argument must be
     *        non-<code>null</code>.
     */
   private void processCreateChartRequest(final String chartName, Map<String, String> responseMap) {
      boolean
         result = false;
      if(chartName == null || chartName.isEmpty())
         logger.info("create_chart: chart name [{}] is null or empty", chartName);
      else {
         logger.info("create_chart: name: {}", chartName);
         result = this.dataBuffer.createChart(chartName);
      }
      responseMap.put("create_chart: " + chartName, result ? "success" : "failure");
   }
   
   private static final class UploadDataRequest {
      private String
         chartName;
      private List<TimestampedDatum>
         packet;
   }
   
   /**
     * Process an <code>upload_data</code> request.
     * 
     * @param json <br>
     *        A JSON string representation of a {@link List} of {@link UploadDataRequest}
     *        objects. If this argument is empty or <code>null</code>, or is a malformed
     *        JSON string, then this method prints an error the logger and otherwise returns
     *        and does nothing.<br><br>
     *        
     *        For each {@link UploadDataRequest} parsed from the JSON, this method will
     *        insert a key <code>"upload_data: [chart name]"</code> (where
     *        <code>[chart name]</code> is the name of the chart) into the response map if
     *        the chart name is non-<code>null</code> and non-empty.<br><br>
     *        
     *        The value of this key is either:
     *        <ul>
     *         <li> <code>success</code> if the chart data was uploaded successfully, or
     *         <li> <code>failure</code> if the chart data was not uploaded successfully (eg.
     *             no such chart is known to this data connector).
     *        </ul>
     * @param responseMap <br>
     *        The map to which responses are to be inserted. This argument must be
     *        non-<code>null</code>.
     */
   private void processUploadDataRequest(final String json, Map<String, String> responseMap) {
      if(json == null || json.isEmpty()) {
         logger.error("upload_data: JSON string is empty.");
         return;
      }
      final List<UploadDataRequest>
         uploadRequests;
      try {
         uploadRequests = this.gson.fromJson(json,
            new TypeToken<List<UploadDataRequest>>() {
               private static final long serialVersionUID = 1L;
               }.getType());
      }
      catch(final JsonSyntaxException e) {
         logger.error("upload_data: request string is malformed JSON.");
         return;
      }
      for(final UploadDataRequest request : uploadRequests) {
         boolean
            result = false;
         String
            chartName = null;
         try {
            chartName = request.chartName;
            final List<TimestampedDatum>
               packet = request.packet;
            logger.info("upload_data: data packet of size {} for chart {}", chartName, packet.size());
            logger.trace("upload_data: detail: {}",
               Arrays.toString(packet.toArray(new TimestampedDatum[0])));
            this.dataBuffer.uploadData(request.chartName, request.packet);
            result = true;
         }
         catch(final Exception e) {
            logger.error("upload_data: exception raised: {}" + e);
         }
         finally {
            if(chartName != null)
               responseMap.put("upload_data: " + chartName, result ? "success" : "failure");
         }
      }
   }
   
   /**
     * The POST response generator. This Servlet responds as follows:
     * 
     * <ul>
     *    <li> For each parameter with value <code>N</code> keyed by <code>create_chart</code>
     *         the response of the servlet is a name-value pair as follows:<br>
     *         <center>
     *          <code>{ key: "create_chart: X", value: V }</code>
     *         </center><br><br>
     *         where <code>X</code> is the value of <code>N</code> and <code>V</code> is
     *         a {@link String} with value <code>"success"</code> or <code>"failure"</code>
     *         depending of the outcome of the <code>create_chart</code> request (unless
     *         <code>N</code> is the empty {@link String}, in which case there is no response).
     *         <br><br>
     *         
     *    <li> For each parameter with value <code>D</code> keyed by <code>upload_data</code>
     *         the response of the servlet is as follows:<br><br>
     *         
     *         If <code>D</code> is not a JSON {@link String} representation of a {@link List} of
     *         classes containing exactly two fields:
     *         <ul>
     *          <li> {@link String} <code>chartName</code>, and
     *          <li> </code>List&lt;TimestampedDatum&gt;</code> packet,
     *         </ul>
     *         then there is no response.<br><br> 
     *         
     *         Otherwise, the response of this servlet is a name-value pair of the form:<br><br>
     *         <center>
     *          <code>{ key: "upload_data: X", value: V }</code>
     *         </center><br>
     *         where <code>X</code> is the value of <code>D.chartName</code> (above) and 
     *         <code>V</code> is a {@link String} with value <code>"success"</code> or
     *         <code>"failure"</code> depending of the outcome of the <code>upload_data</code>
     *         request.
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
         case "create_chart":
            for(final String data : record.getValue())
               processCreateChartRequest(data, responseMap);
            break;
         case "upload_data":
            processUploadDataRequest(record.getValue()[0], responseMap);
            break;
         default:
            logger.error("unknown request: {}", name);
         } 
      }
      response.getWriter().write(this.gson.toJson(responseMap));
   }
}
