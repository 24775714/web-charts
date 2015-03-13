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

function Multichart(multichartName, componentCharts) {
 this.multichartName = multichartName;
 this.componentCharts = componentCharts;
}

Multichart.prototype.table = function() {
 if(this.componentCharts.length == 1)
  return window.chartData[this.componentCharts[0]];
 else {
  var data = window.chartData[this.componentCharts[0]];
  for(var i = 1; i< this.componentCharts.length; ++i) {
    var indices = [];
    for(var j = 1; j<= i; ++j) 
     indices.push(j);
    data = google.visualization.data.join(
     data,
     window.chartData[this.componentCharts[i]],
     'full',
     [[0,0]], indices, [1]
     );
  }
  return data;
 }
}

Multichart.prototype.components = function() {
 return this.componentCharts;
}