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
  * Open and process a dialogue for creating a new multichart.
  */
function openCreateMultichartDialogue() {
 if (!w2ui.NewMultichartSpecifyNameForm) {
  var
   componentChartsSz = '',
   listOfComponentCharts = [],
   subscriptionGrid = w2ui['SubscribedChartsGrid'],
   selections = subscriptionGrid.getSelection(),
   i = 0;
  for(; i< selections.length; ++i) {
   var id = subscriptionGrid.get(selections[i]).id;
   componentChartsSz += id + (i == (selections.length - 1) ? '' : ',&nbsp;');
   listOfComponentCharts.push(id);
  }
  var
   htmlListOfComponentCharts = '<div>' + componentChartsSz + '</div>';
  $().w2form(
  {
   name : 'NewMultichartSpecifyNameForm',
   style : 'border: 0px; background-color: transparent;',
   formHTML :
    '<div class="w2ui-page page-0">'
  + ' <div class="BigFont" style="text-align:center; margin-bottom: 15px">'
  + '  What is the name of this plot?<br>'
  + ' </div>'
  + ' <div class="BigFont w2ui-field">'
  + '  <label>Name:&nbsp;&nbsp;&nbsp;&nbsp;</label>'
  + '  <div>'
  + '   <input name="MultichartName" type="text" maxlength="100" style="width: 250px"/>'
  + '  </div>'
  + ' </div>'
  + ' <br>'
  + ' <div class="BigFont" style="text-align:center; margin-bottom: 15px">'
  + '  The components of this chart are:<br>'
  + ' </div>'
  + ' <div class="BigFont" style="text-align:center; margin-bottom: 15px">'
  + '  ' + htmlListOfComponentCharts
  + ' </div>'
  + '</div>'
  + '<div class="w2ui-buttons">'
  + ' <button class="btn" name="Create">Create</button>'
  + ' <button class="btn" name="Cancel">Cancel</button>'
  + '</div>',
  
   fields : [ {
    field    : 'MultichartName',
    type     : 'text',
    required : true
   } ],
   
   record : {
    MultichartName : ''
   },
   
   actions : {
    "Create" : function() {
     if(this.validate().length == 0) {
      if(addMultichart($('#MultichartName').val(), listOfComponentCharts)) {
       w2popup.close();
       $('#ActiveChartsGridControlContainer').w2overlay({ 
        html: 
         '<div style="padding: 10px; line-height: 150%">' +
         'Added <b>' + $('#MultichartName').val().toString() + '</b> to active charts' +
         '</div>',
        name: 'LoadedNewChartsOverlay'
       });
      }
      w2ui.NewMultichartSpecifyNameForm.destroy();
     }
    },
    
    "Cancel" : function() {
     w2popup.close();
     w2ui.NewMultichartSpecifyNameForm.destroy();
    }
   }
  });
 }
 $().w2popup('open', {
  name     : 'createNewMultiplotPopup',
  title    : 'Create New Plot',
  body     : '<div id="form" style="width: 100%; height: 100%;"></div>',
  style    : 'padding: 15px 0px 0px 0px',
  width    : 500,
  height   : 300,
  showMax  : false,
  onToggle : function(event) {
   $(w2ui.NewMultichartSpecifyNameForm.box).hide();
   event.onComplete = function() {
    $(w2ui.NewMultichartSpecifyNameForm.box).show();
    w2ui.NewMultichartSpecifyNameForm.resize();
   }
  },
  onOpen : function(event) {
   event.onComplete = function() {
    $('#w2ui-popup #form').w2render('NewMultichartSpecifyNameForm');
   }
  }
 });
}

/**
  * Common w2-ui popup code.<br><br>
  * 
  * Calling popup(T, B) will create a popup UI window with title T and
  * body HTML B.
  */
function popup(title, bodyHTML) {
 w2popup.open({
  title     : title,
  body      : '<div class="w2ui-centered">' + bodyHTML + '</div>',
  buttons   : '<button class="btn" onclick="w2popup.close();">Close</button>',
  width     : 500,
  height    : 300,
  overflow  : 'hidden',
  color     : '#333',
  speed     : '0.3',
  opacity   : '0.8',
  modal     : true,
  showClose : true,
  showMax   : false
 });
}

/**
  * Create a button with a text-input overlay. The input field accepts only
  * float values, and a callback is executed whenever such a valid float
  * is specified by the user (and the Enter key is pressed).
  * 
  * @param initialValue<br>
  *        The initial value of the input field.
  * @param callback<br>
  *        A method with signature f(x), where x is a string. This method is
  *        called once every time the user specifies a valid input (an integer).
  *        x is the value the user specifies (a string).
  */
$.fn.floatInputFieldWithOverlay = function(initialValue, callback) {
 var
  ref = this,
  inputID = (ref.attr('id') + 'Impl');
 this.getValue = function() {
  return ref.children('.ui-button-text').html();
 };
 this.setValue = function(szValue) {
  return ref.children('.ui-button-text').html(szValue);
 };
 this.button();
 this.setValue(initialValue.toString());
 this.click(function() {
  ref.w2overlay({
   html: '<input id="' + inputID + '" value="' + ref.getValue() + 
   '" onfocus="this.selectionStart = this.selectionEnd = this.value.length;" ' +
   'style="width:60px; border: 0; outline-color: transparent; outline-style: none;"' +
   '>',
   name: (ref.attr('id') + 'Overlay')
  });
  $('#' + inputID).focus();
  $('#' + inputID).keyup(function(e) {
   if(e.keyCode == '13') {
    var
     input = $('#' + inputID)[0].value,
     isValid = w2utils.isFloat(input);
    if(!isValid)
     $('#' + inputID).w2tag('value must be numeric');
    else if(parseFloat(input) == 0.0)
     $('#' + inputID).w2tag('value must be nonzero');
    else {
     var floatValue = parseFloat(input);
     $('#' + ref.attr('id')).children('.ui-button-text').html(floatValue.toString());
     $('#w2ui-overlay-' + ref.attr('id') + 'Overlay').hide();
     callback(floatValue);
    }
   };
  })
 });
 return this;
}

/**
  * Create a button with a text-input overlay. The input field accepts only
  * integer values, and a callback is executed whenever such a valid integer
  * is specified by the user (and the Enter key is pressed).
  * 
  * @param initialValue<br>
  *        The initial value of the input field.
  * @param callback<br>
  *        A method with signature f(x), where x is a string. This method is
  *        called once every time the user specifies a valid input (an integer).
  *        x is the value the user specifies (a string).
  */
$.fn.intInputFieldWithOverlay = function(initialValue, callback) {
 var
  ref = this,
  inputID = (ref.attr('id') + 'Impl');
 this.getValue = function() {
  return ref.children('.ui-button-text').html();
 };
 this.setValue = function(szValue) {
  return ref.children('.ui-button-text').html(szValue);
 };
 this.button();
 this.setValue(initialValue.toString());
 this.click(function() {
  ref.w2overlay({
   html: '<input id="' + inputID + '" value="' + ref.getValue() + 
   '" onfocus="this.selectionStart = this.selectionEnd = this.value.length;" ' +
   'style="width:60px; border: 0; outline-color: transparent; outline-style: none;"' +
   '>',
   name: (ref.attr('id') + 'Overlay')
  });
  $('#' + inputID).focus();
  $('#' + inputID).keyup(function(e) {
   if(e.keyCode == '13') {
    var
     input = $('#' + inputID)[0].value,
     isValid = w2utils.isInt(input);
    if(!isValid)
     $('#' + inputID).w2tag('value must be numeric');
    else {
     var intValue = parseInt(input);
     $('#' + ref.attr('id')).children('.ui-button-text').html(intValue.toString());
     $('#w2ui-overlay-' + ref.attr('id') + 'Overlay').hide();
     callback(intValue);
    }
   };
  })
 });
 return this;
}