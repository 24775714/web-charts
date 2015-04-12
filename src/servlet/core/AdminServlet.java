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
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
   
   /**
     * Create a {@link AdminServlet} object.
     * 
     * @see HttpServlet#HttpServlet()
     */
   public AdminServlet() {
      logger.info("loading admin servlet..");
      this.gson = new GsonBuilder().create();
      logger.info("admin servlet loaded successfully.");
   }
   
   @Override
   public void init() throws ServletException {
      super.init();
      final AtomicBoolean
         dataBrowserServletLock =
            (AtomicBoolean) super.getServletContext().getAttribute("data-browser-initialized");
      dataBrowserServletLock.set(true);
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
     *         request will fail if configuration has already occurred.
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
      
      // TODO
   }
}
