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
  
  <link rel="stylesheet" type="text/css" href="css/index.css">
  
  <script type="text/javascript" src="js/Dialogues.js"></script>
  <script type="text/javascript" src="js/Queries.js"></script>
  <script type="text/javascript" src="js/Multichart.js"></script>
  
  <script type="text/javascript" src="external/jquery-1.11.2/jquery-1.11.2.min.js"></script>
  
  <link rel="stylesheet" type="text/css" href="external/jquery-ui-1.11.3/jquery-ui.css">
  <script type="text/javascript" src="external/jquery-ui-1.11.3/jquery-ui.js"></script>
  
  <script type="text/javascript" src="https://www.google.com/jsapi"></script>
   
  <script type="text/javascript" src="external/dygraphs/dygraph-combined.js"></script>
  <script type="text/javascript" src="js/DygraphLinechart.js"></script>
  
  <link rel="stylesheet" type="text/css" href="external/w2ui-1.4.2/w2ui-1.4.2.css">
  <script type="text/javascript" src="external/w2ui-1.4.2/w2ui-1.4.2.js"></script>
 </head>
 
 <body>
  <div id="Site">
   <div id="MenuBar">
    <div class="MenuBarRightDivider"></div>
    <div id="SynchronizationRadioButtons" class="MenuBarControl">
     <input type="radio" id="radio1" name="radio" onclick="selectOnDemandSynchronization()">
      <label id="OnDemandRadioButton" for="radio1" title="download data only when asked to do so">
       &nbsp;On Demand&nbsp;</label>
     <input type="radio" id="radio2" name="radio"
      checked="checked"
      onclick="selectContinuousUpdate()">
      <label id="ContinuousUpdateRadioButton" for="radio2" title="download new data continuously">
       &nbsp;Continuous Update&nbsp;</label>
    </div>
    <div id="SynchronizationRadioText" class="BigFont MenuBarControl" style="margin-top: 9px">
     Synchronization:&nbsp;</div>
    <div id="DataStreamNameText" class="BigFont MenuBarControl"
     style="margin-top: 9px; margin-left: 50px; float:left"></div>
   </div>
   <script type="text/javascript">
    $('#SynchronizationRadioButtons').buttonset();
    
    // true when and only when the 'On Demand' radio button option is selected
    var synchronizationIsOnDemand = true;
    
    // update timescale for 'Continuous Update' mode
    var
     updateTimescale = 1;
     updateTimescaleInMilliseconds = window.updateTimescale * 1000.0;
    
    // time units for 'Continuous Update' mode
    var updateTimeUnits = 's';
    
    /*
     * Called when the 'On Demand' radio button is selected. This method removes
     * any UI fields associated with the 'Continuous Update' radio button and installs
     * a set of UI fields appropriate for the 'On Demand' mode.
     */
    function selectOnDemandSynchronization() {
     if(window.synchronizationIsOnDemand)
      return;
     ContinuousUpdatePauseButtonClicked();
     $('#OnDemandRadioButton').addClass('ButtonGlowSubtle');
     $('#ContinuousUpdateRadioButton').removeClass('ButtonGlowSubtle');
     window.synchronizationIsOnDemand = true;
     $('#ContinuousUpdatePlayButton').remove();
     $('#ContinuousUpdatePauseButton').remove();
     $('#UpdateTimescaleInputBox').remove();
     $('#TimescaleRadioButtons').remove();
     $('<div/>', {
      id: 'DownloadOnDemandButton',
      onclick: 'DownloadOnDemandButtonClicked()',
      style: 'float:right; margin-left: 10px',
      title: 'download data now' })
     .html('Download').button({
      icons: { primary: 'ui-icon-signal-diag' },
      text: true
      }).prependTo('#SynchronizationRadioButtons');
    }
    
    /*
     * Validate the content of the input box with id=UpdateTimescaleInputBox. If the
     * value of the input box is not (float) numeric, a small tag to this effect is
     * displayed next to the input box.
     */
    function validateTimescale(value) {
     var isValid =
      w2utils.isFloat(value);
     if(!isValid) return false;
     window.updateTimescale = parseFloat(value);
     window.updateTimescaleInMilliseconds = window.updateTimescale;
     switch(window.updateTimeUnits) {
     case 'ms':
      break;
     case 's':
      window.updateTimescaleInMilliseconds *= 1000.0;
      break;
     case 'min':
      window.updateTimescaleInMilliseconds *= 1000.0 * 60.0;
      break;
     default:
      console.log('error: update timescale units are not ms, s or min as expected (value: "'
       + window.updateTimescaleInMilliseconds + '")');
     }
     return true;
    }
    
    /*
     * Called when the 'Continuous Update' radio button is selected. This method removes
     * any UI fields associated with the 'On Demand' radio button and installs a set of 
     * UI fields appropriate for the 'Continuous Update' mode.
     */
    function selectContinuousUpdate() {
     if(!window.synchronizationIsOnDemand)
      return;
     $('#OnDemandRadioButton').removeClass('ButtonGlowSubtle');
     $('#ContinuousUpdateRadioButton').addClass('ButtonGlowSubtle');
     window.synchronizationIsOnDemand = false;
     $('#DownloadOnDemandButton').remove();
     /*
      * Why is the following a <button> with an <input> overlay?
      * FF8 seems to have !important line-height css properties for <input> fields.
      * The result is that <input> fields are larger in some FF8 browsers than the
      * height of non-<input> controls in this menu bar. If the <input> control is 
      * instead drawn into an overlay, this problem doesn't affect the layout of the
      * rest of this menu.
      */
     $('<button/>', {
      id: 'UpdateTimescaleInputBox',
      style: 'float:right; margin-left: 15px; width: 50px',
      }).html(updateTimescale.toString()).button().prependTo('#SynchronizationRadioButtons');
     $('#UpdateTimescaleInputBox').click(function() {
       $('#UpdateTimescaleInputBox').w2overlay({
        html: '<input id="UpdateTimescaleInputBoxImpl" value="' + 
        updateTimescale.toString() + 
        '" onfocus="this.selectionStart = this.selectionEnd = this.value.length;" ' +
        'style="width:60px; border: 0; outline-color: transparent; outline-style: none;"' +
        '>',
        name: 'UpdateTimescaleInputBoxOverlay'
       });
       $('#UpdateTimescaleInputBoxImpl').focus();
       $('#UpdateTimescaleInputBoxImpl').keyup(function(e) {
        if(e.keyCode == '13') {
         var
          input = $('#UpdateTimescaleInputBoxImpl')[0].value,
          result = validateTimescale(input);
         if(!result)
          $('#UpdateTimescaleInputBoxImpl').w2tag('value must be numeric');
         else {
          $('#UpdateTimescaleInputBox').children('.ui-button-text').html(input);
          $('#w2ui-overlay-UpdateTimescaleInputBoxOverlay').hide();
          window.updateIntervalInMiliseconds = window.updateTimescaleInMilliseconds;
         }
        };
       })
     });
     $('<div/>', {
      id: 'TimescaleRadioButtons',
      style: 'float:right; margin-left:2px'
      })
      .append($(
        '<input ' + (window.updateTimeUnits == 'ms' ? 'checked="checked"' : '') 
      + ' onclick="selectMsTimescale()" type="radio" name="timescaleRadio" id="timescaleRadio1">'
      + ' <label title="milliseconds" for="timescaleRadio1">ms</label>'
      + '</input>'
      + '<input ' + (window.updateTimeUnits == 's' ? 'checked="checked"' : '') 
      + ' onclick="selectSecTimescale()" type="radio" name="timescaleRadio" id="timescaleRadio2">'
      + ' <label title="seconds" for="timescaleRadio2">s</label>'
      + '</input>'
      + '<input ' + (window.updateTimeUnits == 'min' ? 'checked="checked"' : '') 
      + ' onclick="selectMinTimescale()" type="radio" name="timescaleRadio" id="timescaleRadio3">'
      + ' <label title="minutes" for="timescaleRadio3">min</label>'
      + '</input>'))
      .buttonset().prependTo('#SynchronizationRadioButtons');
     $('<div/>', {
      id: 'ContinuousUpdatePlayButton',
      onclick: 'ContinuousUpdatePlayButtonClicked()',
      style: 'float:right; margin-left: 10px',
      title: 'continue downloading data from the server'
      })
     .html('Continue').button({
      icons: { primary: 'ui-icon-play' },
      text: true
      }).prependTo('#SynchronizationRadioButtons');
     $('<div/>', {
      id: 'ContinuousUpdatePauseButton',
      class: 'ButtonGlowDefault',
      onclick: 'ContinuousUpdatePauseButtonClicked()',
      style: 'float:right; margin-left: 10px',
      title: 'pause interaction with server' })
     .html('Pause').button({
      icons: { primary: 'ui-icon-pause' },
      text: true
      }).prependTo('#SynchronizationRadioButtons');
    }
    
    /*
     * Onclick callbacks for timescale radio buttons
     */
    function selectMsTimescale() {
     window.updateTimeUnits = 'ms';
     console.log('using millisecond update timescale');
     validateTimescale();
    }
    function selectSecTimescale() {
     window.updateTimeUnits = 's';
     console.log('using second update timescale');
     validateTimescale();
    }
    function selectMinTimescale() {
     window.updateTimeUnits = 'min';
     console.log('using minute update timescale');
     validateTimescale();
    }
    
    // initial radio button choice is 'Continuous Update'
    selectContinuousUpdate();
    
    function DownloadOnDemandButtonClicked() {
     console.log('downloading data...');
     downloadOnce();
     console.log('operation complete');
    }
    
    function ContinuousUpdatePlayButtonClicked() {
     console.log('requesting continuous update mode');
     $('#ContinuousUpdatePlayButton').addClass('ButtonGlowDefault');
     $('#ContinuousUpdatePauseButton').removeClass('ButtonGlowDefault');
     downloadRepeatedly(this.updateTimescaleInMilliseconds);
    }
    
    function ContinuousUpdatePauseButtonClicked() {
     console.log('pause');
     $('#ContinuousUpdatePlayButton').removeClass('ButtonGlowDefault');
     $('#ContinuousUpdatePauseButton').addClass('ButtonGlowDefault');
     stopDownloadingRepeatedly();
    }
    
    /*
     * Set the name of the incoming data stream.
     */
    function setDataStreamNames(dataStreamName) {
     $('#DataStreamNameText').html('<b>' + dataStreamName + '</b>');
    }
   </script>
   <div id="Content">
    <div id="Tabs" style="width:100%; height:30px"></div>
    <div id="TabContent" style="width: 100%; height: calc(100% - 30px); border: 1px solid #ddd;
      border-top: 0px;"></div>
    <script type="text/javascript">
     var activeTabRecID = -1;
     
     function redrawActiveChart() {
      if(window.activeTabRecID == -1)
       return;
      redrawChart(w2ui['ActiveChartsGrid'].get(window.activeTabRecID).name);
     }
     
     function createTab(recid) {
      if(window.activeTabRecID == -1)
       $('#Tabs').w2tabs({
        name: 'tabs',
        onClick: function (event) {
         displayTab(event.target);
        }
       });
      $('#TabContent').css('background-color', 'white');
      var chartName = w2ui['ActiveChartsGrid'].get(recid).name;
      w2ui['tabs'].add({
       id: recid,
       caption: chartName,
       closable: true
       });
      window.charts[chartName] = new DygraphLinechart(
       chartName,
       'TabContent',
       window.multicharts[chartName],
       chartName,
       'Time',
       'Value'
       );
      displayTab(recid);
      w2ui.tabs.click(recid.toString());
     }
     
     function displayTab(recid) {
      if(recid == window.activeTabRecID)
       return;
      if(window.activeTabRecID != -1)
       window.charts[w2ui['ActiveChartsGrid'].get(window.activeTabRecID).name].unload();
      window.activeTabRecID = recid;
      window.charts[w2ui['ActiveChartsGrid'].get(recid).name].redraw();
     }
    </script>
   </div>
   <div id="ControlBar">
    <div id="AllChartsGridControlContainer" class="ControlBarConsoleLeft">
    </div>
    <div id="SubscriptionsGridControlContainer" class="ControlBarConsoleMiddle">
    </div>
    <div id="ActiveChartsGridControlContainer" class="ControlBarConsoleRight">
    </div>
    <script type="text/javascript">
     function alignConsolePanels() {
      var
       windowWidth = $('#ControlBar').width(),
       panelWidth = Math.floor(windowWidth / 3),
       paddedPanelWidth = windowWidth - 2 * panelWidth - 2;
      $('#AllChartsGridControlContainer, #SubscriptionsGridControlContainer')
       .css('width', panelWidth);
      $('#ActiveChartsGridControlContainer').css('width', paddedPanelWidth);
     }
     resizeDelay =  setTimeout(alignConsolePanels(), 100);
     $(window).resize(function() { 
      clearTimeout(resizeDelay);
      resizeDelay = setTimeout(alignConsolePanels(), 100);
     });
    </script>
    <!-- 
       Console 1 (LHS)
       Console 1 contains a list of all charts known to the server. This console
       provides a refresh button and checkboxes for each known chart.
    -->
    <script type="text/javascript">
      $(function() {
       $('#AllChartsGridControlContainer').w2grid({
        name : 'AvailableChartsGrid',
        header: 'Available',
        method: 'GET',
        
        show : {
         toolbar: true,
         header: true,
         selectColumn: true,
         lineNumbers: true
        },
        
        multiSelect: true,
        
        searches : [ {
         field : 'id',
         caption : 'ID',
         type : 'text'
        }, {
         field : 'type',
         caption : 'Type',
         type : 'text'
        }, {
         field : 'size',
         caption : 'Size',
         type : 'int'
        }, ],
        
        columns : [ {
         field : 'id',
         caption : 'ID',
         size : '60%',
         attr : 'align=left'
        }, {
         field : 'type',
         caption : 'Type',
         size : '20%',
         sortable : true,
         attr : 'align=center'
        }, {
         field : 'size',
         caption : 'Size',
         size : '20%',
         sortable : true,
         attr : 'align=center'
        } ],
        
        onReload : function(event) {
          $('#AllChartsGridControlContainer').w2overlay({ 
           html: 
             '<div style="padding: 10px; line-height: 150%">' +
             (this.total == 0 ?
               'No charts were found.' : 'Found a total of ' + this.total + ' charts') +
             '</div>',
           name: 'LoadedNewChartsOverlay'
         });
        }
       });
      });
      
      /**
        * Add a chart with the specified name to the 'available charts' display.
        */
      function addChartToAvailableChartsList(chartInfo) {
       var grid = w2ui['AvailableChartsGrid'];
       grid.add({recid: grid.total, id: chartInfo.id, type: chartInfo.type, size: chartInfo.size });
      }
     </script>
     <!--
       Console 2 Population
     -->
     <script type="text/javascript">
       var chartData = {};
       
       function redrawChart(chartName) {
        if(window.charts.hasOwnProperty(chartName))
         window.charts[chartName].refresh();
       }
       
       $(function() {
        $('#SubscriptionsGridControlContainer').w2grid({
         name : 'SubscribedChartsGrid',
         header: 'Subscribed',
         method: 'GET',
         
         show : {
          toolbar: true,
          header: true,
          selectColumn: true,
          lineNumbers: true,
          toolbarReload: false
         },
         
         multiSelect: true,
         
         searches : [ {
          field : 'id',
          caption : 'ID',
          type : 'text'
         }, {
          field : 'type',
          caption : 'Type',
          type : 'text'
         } ],
         
         columns : [ {
          field : 'id',
          caption : 'ID',
          size : '80%',
          attr : 'align=left'
         }, {
          field : 'type',
          caption : 'Type',
          size : '20%',
          sortable : true,
          attr : 'align=center'
         } ],
         
         onSave : function(event) {
          w2alert('save');
         },
        });
       });
      </script>
      <!--
       Console 3 (RHS) 
       This console contains chart combinations. A chart combination is a set of
       primitive charts, for example 3 combined primitive line charts.
       
       The name of each element in this grid (the name of the combined chart) 
       must be unique. A local window variable called multicharts is a table of
       lists of primitive chart elements. For example, a combined chart called
       X may consist of primitive charts Y1, Y2 and Y3. The element
       window.multicharts[X1] is { Y1: true, Y2: true, Y3: true }.
       -->
     <script type="text/javascript">
       var multicharts = {};
       
       /**
         * Create a new multichart with the specified name. If a multichart with the
         * specified name already exists, this function opens a (dismissable) popup
         * window. multichartName is the name of the multichart. components is an
         * array of component chart ids.
         * 
         * Returns True when and only when the operation succeeded.
         */
       function addMultichart(multichartName, components) {
        if(multichartName == '') {
         popup(
           'Empty Plot Name',
           'You must specify a name to create a new plot'
          );
         return false;
        }
        if(window.multicharts.hasOwnProperty(multichartName)) {
         popup(
          'Plot Already Exists',
          'A plot with the name <b>' + multichartName + '</b> already exists<br><br>'
        + 'Please select a different name'
          );
         return false;
        }
        window.multicharts[multichartName] = new Multichart(multichartName, components);
        addChartToActiveChartsList({name: multichartName, type: 'line'});
        return true;
       }
       
       /**
         * Delete a multichart and remove it from the active grid control.
         */
       function removeMultichart(multichartName) {
        delete window.multicharts[multichartName];
        var grid = w2ui['ActiveChartsGrid'];
        grid.remove(grid.find({name: multichartName}));
       }
       
       /**
         * Add a chart with the specified name to the 'active charts' display.
         */
       function addChartToActiveChartsList(chartInfo) {
        var grid = w2ui['ActiveChartsGrid'];
        var recid = grid.total;
        grid.add({recid: recid, name: chartInfo.name, type: chartInfo.type });
        createTab(recid);
       }
       
       $(function() {
        $('#ActiveChartsGridControlContainer').w2grid({
         name : 'ActiveChartsGrid',
         header: 'Gallery',
         method: 'GET',
         
         show : {
          toolbar      : true,
          header       : true,
          lineNumbers  : true,
          columnExpand : true,
          toolbarReload: false
         },
         
         searches : [ {
          field    : 'name',
          caption  : 'Name',
          type     : 'text'
         }, {
          field    : 'type',
          caption  : 'Type',
          type     : 'text'
         } ],
         
         columns : [ {
          field    : 'name',
          caption  : 'Name',
          size     : '80%',
          sortable : true,
          attr     : 'align=left'
         }, {
          field    : 'type',
          caption  : 'Type',
          size     : '20%',
          sortable : true,
          attr     : 'align=center'
         } ],
         
         onSave : function(event) {
          w2alert('save');
         },
         
         onExpand: function (event) {
          var
           activeChartsGrid = w2ui['ActiveChartsGrid'],
           recordID = activeChartsGrid.get(event.recid).name,
           componentsArray = window.multicharts[recordID].components(),
           content = '',
           i = 0;
          for(; i< componentsArray.length; ++i) {
           content += componentsArray[i] +
            (i == (componentsArray.length - 1) ? '' : ',&nbsp;');
          }
          $('#'+event.box_id).html('<div style="padding: 10px">' + 
            content + '</div>')
           .animate({ 'height': 100 }, 100);
         }
        });
       });
      </script>
   </div>
   
   <div id="FooterButtons">
    <div id="AllChartsGridButtonsOuter" class="FooterButtonsOuter">
     <div id="AllChartsGridButtons" class="FooterButtonsLeft">
     </div>
    </div>
    <script type="text/javascript">
     $(function () {
      $('#AllChartsGridButtons').w2toolbar({
       name: 'AllChartsGridButtons',
       items: [ {
         type: 'button',
         id: 'refreshAvailableChartsButton',
         caption: 'Refresh From Server',
         img: 'icon-search',
         hint: 'Refresh'
        }, {
         type: 'button',
         id: 'subscribe',
         caption: 'Subscribe',
         img: 'icon-tick',
         hint: 'Subscribe To Selected'
        }
       ],
       
       onClick: function (event) {
        switch (event.target) {
         case 'refreshAvailableChartsButton':
          w2ui['AvailableChartsGrid'].onReload(event);
          break;
         case 'subscribe':
          if(w2ui['AvailableChartsGrid'].getSelection().length == 0) {
           $('#tb_AllChartsGridButtons_item_subscribe').w2overlay({ 
            html: 
              '<div style="padding: 10px; line-height: 150%">'
            + 'No charts are selected.'
            + '</div>',
            name: 'SubscribeToChartsNoneSelectedOverlay'
           });
           return;
          }
          setTimeout(function () {
           var
            availableCharts = w2ui['AvailableChartsGrid'],
            subscriptionGrid = w2ui['SubscribedChartsGrid'],
            selections = availableCharts.getSelection(),
            i = 0;
           for(; i< selections.length; ++i) {
            var id = availableCharts.get(selections[i]).id;
            if(subscriptionGrid.find({ id : id }).length == 0)
             subscriptionGrid.add(availableCharts.get(selections[i]));
            availableCharts.selectNone();
           }
          }, 150);
          break;
        }
       }
      })
     });
    </script>
    <div id="SubscriptionsGridButtonsOuter" class="FooterButtonsOuter">
     <div id="SubscriptionsGridButtons" class="FooterButtonsMiddle">
     </div>
    </div>
    <script type="text/javascript">
     $(function () {
      $('#SubscriptionsGridButtons').w2toolbar({
       name: 'SubscriptionsGridButtons',
       style: 'text-align: right',
       items: [
        { 
          type    : 'button', 
          id      : 'createNewPlot',
          caption : 'Create New Plot',
          icon    : 'icon-add',
          hint    : 'Create New Plot'
        }],
       onClick: function (event) {
        if(w2ui['SubscribedChartsGrid'].getSelection().length == 0) {
         $('#SubscriptionsGridButtons').w2overlay({ 
          html: 
            '<div style="padding: 10px; line-height: 150%">'
          + 'No components are selected.'
          + '</div>',
          name: 'CreateChartsNoneSelectedOverlay'
         });
         return;
        }
        openCreateMultichartDialogue();
       }
      });
     });
    </script>
    <div id="ActiveChartsGridButtonsOuter" class="FooterButtonsOuter">
     <div id="ActiveChartsGridButtons" class="FooterButtonsRight">
     </div>
    </div>
    <script type="text/javascript">
     $(function () {
      $('#ActiveChartsGridButtons').w2toolbar({
       name: 'ActiveChartsGridButtons',
       style: 'text-align: right',
       items: [
        { 
          type    : 'button', 
          id      : 'delete',
          caption : 'Delete',
          icon    : 'icon-cross',
          hint    : 'Delete'
        }],
       onClick: function (event) {
        var
         grid = w2ui['ActiveChartsGrid'],
         selections = grid.getSelection(),
         i = 0;
        if(selections.length == 0) {
         $('#ActiveChartsGridButtons').w2overlay({ 
          html: 
            '<div style="padding: 10px; line-height: 150%">'
          + 'No charts are selected.'
          + '</div>',
          name: 'DeleteChartsNoneSelectedOverlay'
         });
         return;
        }
        for(; i< selections.length; ++i)
         removeMultichart(grid.get(selections[i]).name);
        grid.refresh();
       }
      });
     });
    </script>
    <script type="text/javascript">
     function alignConsoleButtons() {
      var
       windowWidth = $('#FooterButtons').width(),
       panelWidth = Math.floor(windowWidth / 3),
       paddedPanelWidth = windowWidth - 2 * panelWidth - 2;
      $('#AllChartsGridButtonsOuter, #SubscriptionsGridButtonsOuter')
       .css('width', panelWidth);
      $('#ActiveChartsGridButtonsOuter').css('width', paddedPanelWidth);
     }
     alignConsoleButtons();
     $(window).resize(alignConsoleButtons);
    </script>
    <script type="text/javascript">
    </script>
   </div>
   
  </div>
  
  <script type="text/javascript">
   /*
    * Data structures, constants and references
    */
   var servlet = 'http://localhost:8080/web-charts/ChartsBrowserServlet';
   var charts = {};
  </script>
  <script type="text/javascript">
   /*
    * Servlet interaction
    */
   var
    isRepeatingMode = false;
    updateIntervalInMiliseconds = 1000;
   
   function downloadRepeatedly(intervalInMiliseconds) {
    window.updateIntervalInMiliseconds = intervalInMiliseconds;
    console.log('adjusting update interval to ' + intervalInMiliseconds + 'ms');
    if(window.isRepeatingMode)
     return;
    window.isRepeatingMode = true;
    (function repeater() {
     downloadOnce();
     if(window.isRepeatingMode)
      setTimeout(repeater, window.updateIntervalInMiliseconds);
    })();
   }
   
   /*
    * Signal that downloadOnce should no longer be called repeatedly.
    */
   function stopDownloadingRepeatedly() {
    window.isRepeatingMode = false;
   }
   
   /*
    * Perform one full interaction cycle with the server.
    */
   function downloadOnce() {
    try {
     getChartNamesFromServer();
     getChartDataFromServer();
     redrawActiveChart();
    }
    catch(err) {
     console.log(err);
     stopDownloadingRepeatedly();
     ContinuousUpdatePauseButtonClicked();
     popup('Failed To Interact With Server', err.toString());
    }
   }
  </script>
  <script type="text/javascript">
   /*
    * Last load instructions and polish
    */
   google.load("visualization", "1", {packages:["corechart"]});
   $(document).tooltip();
   setDataStreamNames(getDataStreamNameFromServer());
  </script>
 </body>
</html>