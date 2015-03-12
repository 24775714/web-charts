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
package servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * Servlet implementation class ChartsBrowserServlet
 */
@WebServlet(
   asyncSupported = true,
   description = "Browser for data charts",
   urlPatterns = { "/ChartsBrowserServlet" }
   )
public final class ChartsBrowserServlet extends HttpServlet {
   
   private static final long serialVersionUID = 8026415575589209128L;
   
   final static Logger logger = LoggerFactory.getLogger(ChartsBrowserServlet.class);
   
   private Map<String, LineChartData>
      lineChartData;
   
   private final Gson
      gson;
   
   /**
     * Create a {@link ChartsBrowserServlet} object.
     * 
     * @see HttpServlet#HttpServlet()
     */
   public ChartsBrowserServlet() {
      super();
      // Dummy data
      this.lineChartData = new HashMap<String, LineChartData>();
      for(int i = 0; i< 5; ++i) {
         final LineChartData
            lineChart = ChartDataUtils.createRandomLineChart(200, i, "Random Data " + (i + 1));
         this.lineChartData.put(lineChart.name(), lineChart);
      }
      this.gson = new Gson();
      final Random
         random = new Random(1L);
      (new Timer()).scheduleAtFixedRate(new TimerTask() {
         @Override
         public void run() {
            for(LineChartData lineChartData : ChartsBrowserServlet.this.lineChartData.values()) {
               if(lineChartData.size() >= 2000)
                  return;
               final int
                  numNewElements = random.nextInt(20);
               for(int i = 0; i< numNewElements; ++i) {
                  final Entry<Double, Double>
                     lastEntry = lineChartData.lastEntry();
                  lineChartData.put(
                     lastEntry.getKey() + 1.0,
                     lastEntry.getValue() * 0.9 + random.nextGaussian() + 10.0
                     );
               }
            }
         }
      }, 1000L, 1000L);
   }
   
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
     * @param results <br>
     *        The map to which responses are to be inserted.
     */
   private void listKnownCharts(Map<String, String> responseMap) {
      final Gson
         gson = new Gson();
      List<ChartInformation>
         chartInfoList = new ArrayList<ChartInformation>();
      for(final LineChartData dataset : this.lineChartData.values())
         chartInfoList.add(
            new ChartInformation(
               dataset.name(),
               "Line",
               dataset.size()
               )
            );
      responseMap.put("known_charts", gson.toJson(chartInfoList));
   }
   
   private void processGetDataNameRequest(Map<String, String> responseMap) {
      responseMap.put("data_name", "Server 1");
   }
   
   private static class DownloadDataRequest {
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
     * @param results <br>
     *        The map to which responses are to be inserted.
     */
   private void processDownloadDataRequest(
      final String requestString,
      final Map<String, String> results
      ) {
      final DownloadDataRequest
         request = this.gson.fromJson(requestString, DownloadDataRequest.class);
      final List<TimestampedDatum>
         result = new ArrayList<TimestampedDatum>();
      for(Entry<Double, Double> record : 
          this.lineChartData.get(request.chartName).tailMap(
             request.timeOfInterest, false).entrySet())
         result.add(TimestampedDatum.create(record.getKey(), record.getValue()));
      results.put(request.chartName, this.gson.toJson(result));
   }
   
   /**
     * The POST response generator. This servelet responds as follows:
     * 
     * <ul>
     *    <li> For parameters named <code>list_known_charts</code> with no value
     *         the response is an ordered {@link List} of strings in JSON format.
     *         This list may or may not be empty, keyed by <code>known-charts</code>.
     *         
     *    <li> For parameters named <code>get_data_name</code> with no value
     *         the response is a single {@link String} keyed by <code>data_name</code>.
     *         This {@link String} is an identifying name for the data stream processed
     *         by this servlet.
     *         
     *    <li> For parameters named <code>download_data</code> the expected format 
     *         of the value array is <code>X</code> followed by <code>T</code>.
     *         <code>X</code> is the name of a data chart known to this servlet.
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
      System.out.println(gson.toJson(responseMap));
      response.getWriter().write(gson.toJson(responseMap));
   }
}
