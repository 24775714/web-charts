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

/**
  * Get a list of chart names from the servlet.
  */
function getChartNamesFromServer() {
 /*
  * This query should be synchronous, as window.chartData must be updated before a
  * new download_data request is sent:
  */
 var availableCharts = w2ui['AvailableChartsGrid'];
 $.ajax({
  type: 'POST',
  url: servlet,
  data: { 'list_known_charts' : '' },
  traditional: true,
  async: false,
  success: function(response){
   addAllChartsToAvailableChartsList(JSON.parse(response['known_charts']));
  },
  error: function(x,e){
   w2ui['AvailableChartsGrid'].clear();
   throw "Could not retrieve chart list from server.";
   log("server did not respond to query: known_charts.");
  } 
 });
 availableCharts.refresh();
}

/**
  * Get the name of this data stream from the server.
  */
function getDataStreamNameFromServer() {
 var result = '';
 $.ajax({
  type: 'POST',
  url: servlet,
  data: { 'get_data_name' : '' },
  traditional: true,
  async: false,
  success: function(response){
   result = response['data_name'];
  },
  error: function(x,e){
   console.log("server did not respond to query: data_name.");
   result = '[no server connection]';
  } 
 });
 return result;
}

/**
  * Retrieve all subscribed chart data from the servlet.
  */
function getChartDataFromServerForAllSubscribedCharts() {
 var request = [];
 for(var i = 0; i< w2ui['SubscribedChartsGrid'].total; ++i) {
  var
   chartName = w2ui['SubscribedChartsGrid'].get(i).id;
  if(!window.chartData.hasOwnProperty(chartName)) {
   var data = new google.visualization.DataTable();
   data.addColumn('number', 'Time');
   data.addColumn('number', chartName);
   window.chartData[chartName] = data;
  }
  var
   dataTable = window.chartData[chartName],
   size = dataTable.getNumberOfRows();
  request.push(JSON.stringify(
   { chartName: chartName, timeOfInterest: size == 0. ? 
     -Number.MAX_VALUE : dataTable.getValue(size - 1, 0) }));
 }
 $.ajax({
  type: 'POST',
  url: servlet,
  data: { download_data : request },
  traditional: true,
  async: true,
  success: function(response){
   for(var key in response) {
    var
     data = window.chartData[key];
     packet = JSON.parse(response[key]);
    for(var i = 0; i< packet.length; ++i) {
     var datum = packet[i];
     data.addRow([datum.time, datum.value]);
    }
   };
  },
  error: function(x,e){
  } 
 });
}

/**
  * Retrieve data for one particular subscribed chart from the servlet. This method will
  * log an error and return if the name of the chart does not appear in the chart subscription
  * grid.
  * 
  * @param chartName <br>
  *        The name of the chart for which to download data.
  * @param callback <br>
  *        A function callback to execute when the ajax request is complete.
  */
function getChartDataFromServer(chartName, callback) {
 if(w2ui['SubscribedChartsGrid'].find({id: chartName}) == null) {
  console.log('cannot perform download_data request for chart "' + chartName + '" as this chart '
  + 'does not appear in the subscription list.');
  return;
 }
 if(!window.chartData.hasOwnProperty(chartName)) {
  var data = new google.visualization.DataTable();
  data.addColumn('number', 'Time');
  data.addColumn('number', chartName);
  window.chartData[chartName] = data;
 }
 var
  dataTable = window.chartData[chartName],
  size = dataTable.getNumberOfRows(),
  request = [];
 request.push(JSON.stringify(
  { chartName: chartName, timeOfInterest: size == 0. ? 
    -Number.MAX_VALUE : dataTable.getValue(size - 1, 0) }));
 var callback_ = callback;
 $.ajax({
  type: 'POST',
  url: servlet,
  data: { download_data : request },
  traditional: true,
  async: true,
  success: function(response){
   for(var key in response) {
    var
     data = window.chartData[key];
     packet = JSON.parse(response[key]);
    for(var i = 0; i< packet.length; ++i) {
     var datum = packet[i];
     data.addRow([datum.time, datum.value]);
    }
   };
   callback_(chartName);
  },
  error: function(x,e){
   console.log('servlet download_data request failed.');
  } 
 });
}