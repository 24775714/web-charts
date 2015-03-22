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
  * Create a dygraph linechart object. This object is rendered into a div container.
  * This creates a dygraph object with default options. The chart is not rendered into
  * the container until redraw() is called.
  * 
  * @param graphName <br>
  *        The (uniquely-identifying) name of the chart.
  * @param containerID <br>
  *        The ID of the <div> into which this chart should be drawn.
  * @param multichart <br>
  *        A datatable containing chart data.
  * @param title <br>
  *        The title for this chart.
  * @param xLabel <br>
  *        The x-axis label for this chart.
  * @param yLabel <br>
  *        The y-axis label for this chart.
  */
function DygraphLinechart(graphName, containerID, multichart, title, xLabel, yLabel) {
 this.graphName = graphName;
 this.containerID = containerID;
 this.multichart = multichart;
 this.title = title;
 this.xLabel = xLabel;
 this.yLabel = yLabel;
 this.options = {
  rollPeriod: 1,
  showRoller: false,
  title: this.title + '<br>',
  legend: 'always',
  labelsDivStyles: { 'textAlign': 'right' },
  showRangeSelector: true,
  logscale: false,
  xlabel: xLabel,
  ylabel: yLabel,
  rightGap: 50,
  digitsAfterDecimal: 4,
  titleHeight: 22,
  xRangePad: 1,
  yRangePad: 1,
  animatedZooms: false,
  axisLabelFontSize: 12,
  xLabelHeight: 16,
  yLabelWidth: 16,
  fillGraph: false,
  drawPoints: false,
  strokeWidth: 1,
  strokeBorderWidth: 0,
  fillAlpha: 0.1
 };
 this.highlightEnabled = false;
 this.loaded = false;
}

/*
 * Implementation detail. A callback method when chart lines are clicked.
 */
DygraphLinechart.prototype.clickCallback = function() {
 if(this.graph.isSeriesLocked())
  this.graph.clearSelection();
 else
  this.graph.setSelection(this.graph.getSelection(), this.graph.getHighlightSeries(), true);
};

/**
  * Completely redraw this chart and all associated controls into its container.
  */
DygraphLinechart.prototype.redraw = function() {
 var ref = this;
 $('#' + this.containerID).html('');
 $('<div/>', {
  id: this.containerID + 'DygraphSeparator',
  style: 'width:100%; height: 10px'
 }).appendTo('#' + this.containerID);
 $('<div/>', {
  id: this.containerID + 'DygraphContainer',
  style: 'width:calc(100% - 10px); height: calc(100% - 40px); margin-left:10px'
 }).appendTo('#' + this.containerID);
 $('<div/>', {
  id: this.containerID + 'DygraphButtons',
  style: 'width:100%; height: 30px; padding-top: 2px'
 }).appendTo('#' + this.containerID);
 $('#' + this.containerID + 'DygraphButtons')
  .append($('<div style="float:right; width:50px; height: 1px">'));
 $('#' + this.containerID + 'DygraphButtons')
  .append($('<button title="reset chart zoom" style="float:right; margin-right: 2px"/>')
  .append("Reset Zoom").click(function() {
  ref.graph.resetZoom();
 }).button());
 $('#' + this.containerID + 'DygraphButtons')
  .append($('<button title="enable/disable logscale" id="ToggleLogscaleDygraphButton"'
  + ' style="float:right; margin-right: 2px"/>')
  .append("Toggle Logscale").click(function() {
  ref.options['logscale'] = !ref.options['logscale'];
  if(ref.options['logscale'])
   $('#ToggleLogscaleDygraphButton').addClass('ButtonGlowSubtle');
  else
   $('#ToggleLogscaleDygraphButton').removeClass('ButtonGlowSubtle');
  ref.graph.updateOptions({'logscale' : ref.options['logscale']});
 }).button());
 $('#' + this.containerID + 'DygraphButtons')
  .append($('<button title="fill below graph" id="FillDygraphButton"' +
  ' style="float:right; margin-right: 2px"/>')
  .append("Fill").click(function() {
  ref.options['fillGraph'] = !ref.options['fillGraph'];
  if(ref.options['fillGraph'])
   $('#FillDygraphButton').addClass('ButtonGlowSubtle');
  else
   $('#FillDygraphButton').removeClass('ButtonGlowSubtle');
  ref.graph.updateOptions({'fillGraph' : ref.options['fillGraph']});
 }).button());
 $('#' + this.containerID + 'DygraphButtons')
  .append($('<button title="draw data points" id="DrawPointsDygraphButton"' + 
   ' style="float:right; margin-right: 2px"/>')
  .append("Draw Points").click(function() {
  ref.options['drawPoints'] = !ref.options['drawPoints'];
  if(ref.options['drawPoints'])
   $('#DrawPointsDygraphButton').addClass('ButtonGlowSubtle');
  else
   $('#DrawPointsDygraphButton').removeClass('ButtonGlowSubtle');
  ref.graph.updateOptions({'drawPoints' : ref.options['drawPoints']});
 }).button());
 $('#' + this.containerID + 'DygraphButtons')
  .append($('<button title="show range selector" id="ShowRangeSelectorDygraphButton"' + 
    ' style="float:right; margin-right: 2px"/>')
  .append("Range Selector").click(function() {
  $('#' + this.containerID + 'DygraphContainer').html('');
  ref.options['showRangeSelector'] = !ref.options['showRangeSelector'];
  ref.options['animatedZooms'] = !ref.options['animatedZooms'];
  if(ref.options['showRangeSelector'])
   $('#ShowRangeSelectorDygraphButton').addClass('ButtonGlowSubtle');
  else
   $('#ShowRangeSelectorDygraphButton').removeClass('ButtonGlowSubtle');
  ref.graph = new Dygraph(
   document.getElementById(ref.containerID + 'DygraphContainer'),
   ref.multichart.table(),
   ref.options
   );
  ref.graph.updateOptions({clickCallback: function(e) { ref.clickCallback(); }}, true);
 }).button());
 
 $('#' + ref.containerID + 'DygraphButtons').append(
 $('<button/>', {
  id: 'DygraphSmootingInputBox',
  style: 'float: right; width: 40px; margin-right: 2px'
  })
  .addClass('LightButton').html(ref.options.rollPeriod.toString())
  .intInputFieldWithOverlay(
   ref.options.rollPeriod.toString(),
   function(value) { ref.updateSmoothingValue(value); })
   );
 $('#' + this.containerID + 'DygraphButtons')
 .append('<div class="BigFont"' + 
 ' style="padding-top: 5px; margin-right: 2px; margin-left:5px; float:right">Smoothing:</div>');
 
 $('#' + ref.containerID + 'DygraphButtons').append(
 $('<button/>', {
  id: 'DygraphAlphaInputBox',
  style: 'float: right; width: 40px; margin-right: 2px'
  })
  .addClass('LightButton').html(ref.options.fillAlpha.toString())
  .floatInputFieldWithOverlay(
   ref.options.fillAlpha.toString(),
   function(value) { ref.updateAlphaValue(value); })
   );
 $('#' + this.containerID + 'DygraphButtons')
 .append('<div class="BigFont"' + 
 ' style="padding-top: 5px; margin-right: 2px; float:right">Alpha:</div>');
 
 $('#' + this.containerID + 'DygraphButtons')
 .append($('<button title="highlight focused graphs" id="HighlightDygraphButton"' +
 ' style="float:right; margin-right: 7px"/>')
 .append("Highlighting").click(function() {
  ref.toggleHighligher();
  if(ref.highlightEnabled)
   $('#HighlightDygraphButton').addClass('ButtonGlowSubtle');
  else
   $('#HighlightDygraphButton').removeClass('ButtonGlowSubtle');
  ref.graph.updateOptions({
   'highlightSeriesOpts' : ref.options['highlightSeriesOpts'],
   'highlightSeriesBackgroundAlpha' : ref.options['highlightSeriesBackgroundAlpha']});
 }).button());
 
 if(ref.options['logscale'])
  $('#ToggleLogscaleDygraphButton').addClass('ButtonGlowSubtle');
 if(ref.options['fillGraph'])
  $('#FillDygraphButton').addClass('ButtonGlowSubtle');
 if(ref.options['drawPoints'])
  $('#DrawPointsDygraphButton').addClass('ButtonGlowSubtle');
 if(ref.options['showRangeSelector'])
  $('#ShowRangeSelectorDygraphButton').addClass('ButtonGlowSubtle');
 if(ref.highlightEnabled)
  $('#HighlightDygraphButton').addClass('ButtonGlowSubtle');
 if(!this.loaded) {
  this.graph = new Dygraph(
   document.getElementById(this.containerID + 'DygraphContainer'),
   this.multichart.table(),
   this.options
   );
  var ref = this;
  this.graph.updateOptions({clickCallback: function() { ref.clickCallback(); }}, true);
  this.loaded = true;
 }
 this.refresh();
}

/*
 * Implementation detail.
 */
DygraphLinechart.prototype.updateSmoothingValue = function(value) {
 this.options['rollPeriod'] = parseInt(value);
 if(this.loaded)
  this.graph.updateOptions({'rollPeriod': this.options['rollPeriod']});
}

/*
 * Implementation detail.
 */
DygraphLinechart.prototype.updateAlphaValue = function(value) {
 this.options['fillAlpha'] = parseFloat(value);
 if(this.loaded)
  this.graph.updateOptions({'fillAlpha': this.options['fillAlpha']});
}

/*
 * Implementation detail.
 */
DygraphLinechart.prototype.toggleHighligher = function() {
 if(!this.highlightEnabled) {
  this.options['highlightSeriesOpts'] = {
   strokeWidth: 1,
   strokeBorderWidth: 0,
   highlightCircleSize: 1
  };
  this.options['highlightSeriesBackgroundAlpha'] = 0.7;
  this.highlightEnabled = true;
 }
 else {
  delete this.options.highlightSeriesOpts;
  delete this.options.highlightSeriesBackgroundAlpha;
  this.highlightEnabled = false;
 }
}

/**
  * Update this chart. This method assumes that the underlying chart has been drawn
  * into the container.
  */
DygraphLinechart.prototype.refresh = function () {
 if(this.loaded)
  this.graph.updateOptions({'file': this.multichart.table()});
}

/**
  * Unload this chart from its container. When this method is called, the container
  * for this chart is emptied and all controls associated with this chart are removed.
  */
DygraphLinechart.prototype.unload = function() {
 $('#' + this.containerID).html('');
 this.graph = null;
 this.loaded = false;
}