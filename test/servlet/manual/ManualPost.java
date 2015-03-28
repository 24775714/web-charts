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
package servlet.manual;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import servlet.core.GSONPostUtils;
import servlet.core.GSONPostUtils.POSTException;
import servlet.data.TimestampedDatum;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author phillips
 *
 */
public class ManualPost {
   private final static Logger
      logger = LoggerFactory.getLogger(ManualPost.class);
   
   static final class UploadDataRequest {
      String chartName;
      List<TimestampedDatum> packet = new ArrayList<TimestampedDatum>();
   }
   
   /**
    * @param args
    */
   public static void main(String[] args) {
      final Gson
         gson = new GsonBuilder().create();
      
      try {
         List<NameValuePair>
            params = new ArrayList<NameValuePair>();
         params.add(new BasicNameValuePair("create_chart", "First Manual Chart"));
         params.add(new BasicNameValuePair("create_chart", "Second Manual Chart"));
         
         Map<String, String>
            response = GSONPostUtils.POST(
               "http://localhost:8080/web-charts/receiver", params);
         
         for(final Entry<String, String> record : response.entrySet()) {
            System.out.println(record.getKey() + ": " + record.getValue());
         }
      }
      catch(final POSTException e) {
         System.out.println("fail");
      }
      
      try {
         UploadDataRequest
            first = new UploadDataRequest();
         
         first.chartName = "First Manual Chart";
         for(int i = 0; i< 10; ++i)
            first.packet.add(TimestampedDatum.create(i, i));
         
         final List<UploadDataRequest>
            allUploadRequests = new ArrayList<ManualPost.UploadDataRequest>();
         allUploadRequests.add(first);
         
         List<NameValuePair>
            params = new ArrayList<NameValuePair>();
         params.add(new BasicNameValuePair("upload_data", gson.toJson(allUploadRequests)));
         
         Map<String, String>
            response = GSONPostUtils.POST(
               "http://localhost:8080/web-charts/receiver", params);
         
         for(final Entry<String, String> record : response.entrySet()) {
            System.out.println(record.getKey() + ": " + record.getValue());
         }
      }
      catch(final POSTException e) {
         System.out.println("fail");
      }
   }
}
