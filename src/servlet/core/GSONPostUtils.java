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
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
  * Static utility functions for submitting custom POST requests to a servlet.
  * 
  * @author phillips
  */
public final class GSONPostUtils {
   
   private final static Logger
      logger = LoggerFactory.getLogger(GSONPostUtils.class);
   
   /**
     * Submit a POST request to a servlet.<br><br>
     * 
     * This utility method submits a POST request to a servlet. The POST request contains
     * data formatted in JSON.
     * 
     * @param url <br>
     *        The URL of the servlet to which to send a POST request. This argument must be
     *        non-<code>null</code>.
     * @param params <br>
     *        A list of {@link NameValuePair} objects specifying the data to include in the
     *        JSON POST packet. This argument must be non-<code>null</code>.
     * @return
     *        A {@link Map} of servlet parameter responses.
     * @throws POSTException 
     *        If the data in the <code>params</code> argument cannot be serialized as JSON
     *        data, or if the servlet was not found, or incommunicable, or if the servlet
     *        responded but it was not possible to close HTTP resources associated with the
     *        POST request.
     * @throws IllegalArgumentException
     *        If either of the input arguments are <code>null</code>.
     */
   static public Map<String, String> POST(
      final String url,
      final List<NameValuePair> params
      ) throws POSTException {
      if(url == null || params == null)
         throw new IllegalArgumentException();
      final CloseableHttpClient
         httpclient = HttpClients.createDefault();
      CloseableHttpResponse
         response = null;
      final Gson
         gson = new GsonBuilder().create();
      Map<String, String>
         result = null;
      try {
         final HttpPost
            httppost = new HttpPost(url);
         httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
         response = httpclient.execute(httppost);
         final String
            json = EntityUtils.toString(response.getEntity());
         result =  gson.fromJson(json, new TypeToken<Map<String, String>>() {}.getType());
      }
      catch(final Exception e) {
         logger.error("POST failed: {}", e);
         throw new POSTException(e);
      }
      finally {
         try {
            httpclient.close();
            if(response != null)
               response.close();
            logger.info("post success");
         } catch (IOException e) {
            logger.error("post succeeded, but could not close HTTP resources");
            throw new POSTException(e);
         }
      }
      return result;
   }
   
   private GSONPostUtils() { }
   
   /**
     * A simple exception indicating a problem with a HTTP POST request.
     * 
     * @author phillips
     */
   public final static class POSTException extends Exception {
      
      /**
        * Create a {@link POSTException} with no exception message.
        */
      public POSTException() { super(); }
      
      /**
        * Create a {@link POSTException} with an exception message.
        * 
        * @param message <br>
        *        The exception message. If this argument is <code>null</code>, the
        *        exception message is taken to be the empty {@link String}.
         */
      public POSTException(final String message) {
         super(message == null ? "" : message);
      }
      
       /**
        * Create a {@link POSTException} with a {@link Throwable} cause.
        * 
        * @param cause <br>
        *        The cause of the {@link DataSourceException}. This argument should
        *        be non-<code>null</code>.
        */
      public POSTException(Throwable cause) {
         super(cause);
      }
      
      private static final long serialVersionUID = -845340135222973252L;
   }
}
