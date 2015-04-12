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
  <title>Browse Charts</title>
  
  <script type="text/javascript" src="external/jquery-1.11.2/jquery-1.11.2.min.js"></script>
  
  <link rel="stylesheet" type="text/css" href="external/jquery-ui-1.11.3/jquery-ui.css">
  <script type="text/javascript" src="external/jquery-ui-1.11.3/jquery-ui.js"></script>
  
  <link rel="stylesheet" type="text/css" href="external/w2ui-1.4.2/w2ui-1.4.2.css">
  <script type="text/javascript" src="external/w2ui-1.4.2/w2ui-1.4.2.js"></script>
  
  <link rel="stylesheet" type="text/css" href="external/font-awesome-4.3.0/css/font-awesome.css">
  
  <link rel="stylesheet" type="text/css" href="css/index.css">
 </head>
<body>
 <div id="Site" style="min-height:500px; overflow: scroll">
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
       { id: 'LiveReveiver',  text: 'Live Receiver',   icon: 'fa fa-wifi' },
       { id: 'CSVFileParser', text: 'CSV File Parser', icon: 'fa fa-file-text-o' },
       { id: 'RandomData',    text: 'Random Data',     icon: 'fa fa-bar-chart-o' }
      ]}
     ]
    });
   });
   </script>
  </div>
  <div style="float:left; height:calc(100% - 500px); width: 179px; background-color: #eee;
       border-right: 1px solid silver;"></div>
 </div>
</body>
</html>