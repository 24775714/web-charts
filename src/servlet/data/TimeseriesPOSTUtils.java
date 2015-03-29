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
package servlet.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import servlet.core.GSONPostUtils;
import servlet.core.GSONPostUtils.POSTException;

/**
  * A lightweight facade for executing data_upload POST requests to a live
  * data receiver servlet.
  * 
  * @author phillips
  */
public class TimeseriesPOSTUtils {
   
   static private final Gson
      gson = new GsonBuilder().create();
   
   static final class UploadDataRequest {
      String chartName;
      List<TimestampedDatum> packet = new ArrayList<TimestampedDatum>();
   }
   
   /**
     * Upload timeseries data via POST to a live data receiver servlet.<br><br>
     * 
     * See also {@link GSONPostUtils#POST(String, List)}.
     * 
     * @param url <br>
     *        The URL of the receiving servlet. This argument must be non-<code>null</code>
     *        and non-empty.
     * @param chartName <br> 
     *        The identifying name of the data stream. This argument must be non-<code>null</code>
     *        and non-empty.
     * @param data <br>
     *        A {@link List} or {@link TimestampedDatum} data to upload. This argument must be
     *        non-<code>null</code>.
     * @return
     *        A boolean indicating success/failure of the servlet to process the request.
     * @throws POSTException
     *        If the underlying (JSON) POST request failed or if the data could not be 
     *        Serialised in advance of making a POST request.
     * @throws IllegalArgumentException
     *        If any of the arguments do not satisfy the above requirements.
     */
   static public boolean POST(
      final String url,
      final String chartName,
      final List<TimestampedDatum> data
      ) throws POSTException {
      if(url == null || url.isEmpty())
         throw new IllegalArgumentException(
            "POST: URL is null or empry.");
      if(chartName == null || chartName.isEmpty())
         throw new IllegalArgumentException(
            "POST: chart name is null or empry.");
      if(data == null)
         throw new IllegalArgumentException(
            "POST: data object is null.");
      if(data.isEmpty())
         return true;
      final UploadDataRequest
         message = new UploadDataRequest();
      message.chartName = chartName;
      message.packet = data;
      
      final List<UploadDataRequest>
         allUploadRequests = new ArrayList<TimeseriesPOSTUtils.UploadDataRequest>();
      allUploadRequests.add(message);
      
      final List<NameValuePair>
         params = new ArrayList<NameValuePair>();
      params.add(new BasicNameValuePair("upload_data", gson.toJson(allUploadRequests)));
      
      final Map<String, String>
         response = GSONPostUtils.POST(url, params);
      
      final String
         expectedResponseKey = "upload_data: " + chartName;
      if(params.size() == 1 &&
         response.containsKey(expectedResponseKey) &&
         response.get(expectedResponseKey).equals("success"))
         return true;
      else
         return false;
   }
   
   private TimeseriesPOSTUtils() { }
}
