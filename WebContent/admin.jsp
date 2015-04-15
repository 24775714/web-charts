<!--
 This file is part of web-charts, an interactive web charts program.
 
 Copyright (C) 2015 John Kieran Phillips
 
 web-charts is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.
 
 web-charts is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 
 You should have received a copy of the GNU General Public License
 along with web-charts.  If not, see <http://www.gnu.org/licenses/>.
-->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
 <head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <title>Control Panel</title>
  
  <script type="text/javascript" src="external/jquery-1.11.2/jquery-1.11.2.min.js"></script>
  
  <link rel="stylesheet" type="text/css" href="external/jquery-ui-1.11.3/jquery-ui.css">
  <script type="text/javascript" src="external/jquery-ui-1.11.3/jquery-ui.js"></script>
  
  <link rel="stylesheet" type="text/css" href="external/w2ui-1.4.2/w2ui-1.4.2.css">
  <script type="text/javascript" src="external/w2ui-1.4.2/w2ui-1.4.2.js"></script>
  
  <link rel="stylesheet" type="text/css" href="external/font-awesome-4.3.0/css/font-awesome.css">
  
  <link rel="stylesheet" type="text/css" href="css/index.css">
  <link rel="stylesheet" type="text/css" href="admin/admin.css">
 </head>
<body>
 <div id="Site" style="min-height:500px; min-width:680px; overflow: scroll">
  <div id="Limiter" style="height:500px; width:100%; float:left">
   <div id="sidebar" style="height:100%; width:180px; float:left;"></div>
   
   <script type="text/javascript">
   $(document).ready(function () {
    $('#sidebar').w2sidebar({
     name: 'sidebar',
     topHTML    :
      '<div style="background-color: #eee; height: 75px; padding: 10px 5px; border-bottom:' 
    +' 1px solid silver">'
    + ' <i class="fa fa-cog fa-spin spin-slow spin-reverse fa-4x" style="opacity:0.7; '
    + 'position: absolute; left: 10px; top: 10px"></i>'
    + ' <i class="fa fa-cog fa-spin spin-fast fa-2x" style="opacity:0.6;'
    + '  position: absolute; left: 40px; top: 40px"></i>'
    + ' <div class="Centering" style="float:left; margin-left: 50px; width:120px; height:100%">'
    + ' <div><b>Configuration</b></div>'
    + '</div></div>',
     bottomHTML :
      '<div style="background-color: #eee; padding: 10px 5px; border-top: 1px solid silver">'
    + '<div class="Centering">Web Charts</div></div>',
     style: 'border-right: 1px solid silver',
     nodes: [{
      id: 'DataReceiver',
      text: 'Data Receiver',
      img: 'fa fa-cog fa-spin',
      expanded: true,
      group: true,
      nodes: [
       { id: 'LiveReceiver',  text: 'Live Receiver',   icon: 'fa fa-wifi' },
       { id: 'CSVFileParser', text: 'CSV File Parser', icon: 'fa fa-file-text-o' },
       { id: 'RandomData',    text: 'Random Data',     icon: 'fa fa-bar-chart-o' }
      ]}
     ],
     onClick: function (event) {
      loadAdminPaneWith(event.target);
     }
    });
   });
   </script>
   <div id="DisplayPane" style="width:calc(100% - 185px); height:calc(100% - 45px);
        background-color:white; margin-left:5px; margin-top:5px; float:left;">
   </div>
   <div id="SubmitButtonContainer" style="width:750px; height:40px; 
        margin-left:0; margin-top:0; float:left;">
    <button id="StartServerButton" type="submit" class="not-selectable submitButton"
        style="float:right; margin-top:5px">
     <i class="fa fa-play"></i> Start Server</button>
   </div>
  </div>
  <div style="float:left; height:calc(100% - 500px); width: 179px; background-color: #eee;
       border-right: 1px solid silver;"></div>
 </div>
 <script type="text/javascript">
  var servlet;
  {
   var
    href = window.location.href;
   window.servlet = href.substr(0, href.lastIndexOf('/')) + '/admin';
   delete href;
  }
 </script>
 
 <script type="text/javascript">
 function loadAdminPaneWith(type) {
  var currentPane = getOperationMode();
  switch(type) {
  case 'LiveReceiver':
   if(currentPane != 'live') {
    unloadPane();
    $('#DisplayPane').empty();
    $('#DisplayPane').load('admin/live-receiver-config.html');
   }
   break;
  case 'CSVFileParser':
   if(currentPane != 'csv') {
    unloadPane();
    $('#DisplayPane').empty();
    $('#DisplayPane').load('admin/csv-reader-config.html');
   }
   break;
  case 'RandomData':
   if(currentPane != 'random') {
    unloadPane();
    $('#DisplayPane').empty();
    $('#DisplayPane').load('admin/random-data-config.html');
   }
   break;
  }
 }
 
 /**
   * Respond to a #StartServerButton click event:
   */
 function handleStartServerButtonClick() {
  var isValidConfiguration = validateSettings();
  if(!isValidConfiguration)
   return;
  submitServletConfiguration(getConfigurationParameterMap());
 }
 </script>
 
 <script type="text/javascript">
 /**
   * Lock the site container with an error message.
   */
 function lockWithErrorMessage(message) {
  w2utils.lock($('#Site'), { spinner: false, msg: message, opacity : 0.6 });
 }
 
 /**
   * Lock/unlock the site container with a message.
   */
 function tempLockWithMessage(message) {
  w2utils.lock($('#Site'), { spinner: true, msg: message, opacity : 0.6 });
 }
 function unlockSiteContainer() {
  w2utils.unlock($('#Site'));
 }
 
 function POST(data, onSuccess, onError) {
  $.ajax({
   type: 'POST',
   url: servlet,
   data: data,
   traditional: true,
   async: false,
   success: function(response) { onSuccess(response); },
   error: function(x,e){ onError(e); } 
  });
 }
 
 function setStateServletIsUnconfigured() {
  console.log('server is unconfigured.');
  unlockSiteContainer();
 }
 
 function setStateServletIsConfigured() {
  console.log('server is configured.');
  lockWithErrorMessage('Server is running.');
 }
 
 /**
   * Query the servlet with is_configured. The result of this query will be
   * true or false. In the event of an error, the site container will lock.
   * If this POST returns false, the 'start servlet' button will be disabled.
   * Otherwise, the 'start servlet' button will be enabled.
   */
 function queryIsServletConfigured() {
  POST(
   {'is_configured':''},
   function(response) {
    var isConfigured = response.configuration_state;
    if(isConfigured == true)
     setStateServletIsConfigured();
    else
     setStateServletIsUnconfigured();
   },
   function() { lockWithErrorMessage("servlet failed to respond properly"); }
  );
 }
 
 /**
   * POST a parameter map containing configuration details to the servlet.
   */
 function submitServletConfiguration(parameterMap) {
  POST(
   { set_configuration: JSON.stringify({ 
    mode: getOperationMode(), 
    configuration: JSON.stringify(parameterMap) 
   })},
   function(response) {
    if(response.hasOwnProperty('configuration_result') && response.configuration_result == true)
     tempLockWithMessage('server is running');
    else
     w2popup.open({
      title: 'Configuration Failed',
      body: '<div class="w2ui-centered">Invalid configuration</div>'
     });
   },
   function() { lockWithErrorMessage("servlet did not accept configuration"); }
  );
 }
 </script>
 
 <!-- Initial loadup -->
 <script type="text/javascript">
  tempLockWithMessage('&nbsp;&nbsp;Loading...');
   
  queryIsServletConfigured();
  
  $('#StartServerButton').click(handleStartServerButtonClick);
 </script>
 
 <!--
  The following methods must be overridden by any content injected into $('#DisplayPane'):
   unloadPane(): this method is called once when any unloading operations should be performed
                 on the current content of $('#DisplayPane'). Immediately after calling this
                 method, the HTML and DOM content of $('#DisplayPane') will be emptied.
   getOperationmode():
                 get a string describing the current content of $('#DisplayPane').
   validateSettings():
                 returns true if and only if the user has selected valid settings.
   getConfigurationParameterMap():
                 get a JSON string describing the configuration settings specified by the user.
 -->
 <script type="text/javascript">
  function unloadPane() { }
  
  function getOperationMode() {
   return 'none';
  }
  
  function getConfigurationParameterMap() {
   return {};
  }
 </script>
</body>
</html>