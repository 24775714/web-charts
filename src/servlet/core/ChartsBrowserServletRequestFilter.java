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
import java.util.concurrent.Semaphore;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
  * A semaphore filter restricting the number of concurrent servlet requests. This
  * filter is configured by web.xml.<br><br>
  * 
  * web.xml parameters:
  * 
  * <ul>
  *   <li> <b><code>maxConcurrentRequests</code></b><br>
  *        This parameter must be parseable as a non-negative nonzero integer. 
  *        <code>maxConcurrentRequests</code> is the size of the semaphore gate for this filter. 
  *        If <code>maxConcurrentRequests = 2</code>, then at most two concurrent requests will 
  *        be processed by the servlet simultaneously.
  * </ul>
  * 
  * @author phillips
  */
public final class ChartsBrowserServletRequestFilter implements Filter {
   
   private Semaphore
      gate;
   
   private final static Logger
      logger = LoggerFactory.getLogger(ChartsBrowserServletRequestFilter.class);
   
   /**
     * @param request
     * @param response
     * @param chain
     * @throws IOException
     * @throws ServletException
     */
   @Override
   public void doFilter(
      ServletRequest request,
      ServletResponse response,
      FilterChain chain
      ) throws IOException, ServletException {
      try {
         try {
            this.gate.acquire();
         } catch(final InterruptedException e) {
            return;
         }
         chain.doFilter(request, response);
      } finally {
         this.gate.release();
      }
   }
   
   @Override
   public void destroy() {
      // No action
   }
   
   @Override
   public void init(FilterConfig config) throws ServletException {
      final String
         gateSizeSz = config.getInitParameter("maxConcurrentRequests");
      try {
         if(gateSizeSz == null)
            throw new IllegalArgumentException();
         final int
            maxConcurrentRequests = Integer.parseInt(gateSizeSz);
         if(maxConcurrentRequests <= 0)
            throw new IllegalArgumentException();
         this.gate = new Semaphore(maxConcurrentRequests);
         logger.info("servlet gate size is " + maxConcurrentRequests + ".");
      }
      catch(final NumberFormatException e) {
         logger.error("the expression '" + gateSizeSz + "' is not a valid integer.");
         throw new ServletException(e);
      }
      catch(final IllegalArgumentException e) {
         logger.error("the gate size must be strictly positive (value is " + gateSizeSz + ").");
         throw new ServletException(e);
      }
   }
}
