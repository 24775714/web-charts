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
  * Create dropdown box menu with a label. The label text, the size of the dropdown menu,
  * the values in the dropdown box and the initial (selected) value are customizable.
  * This control is built inside an existing jQuery div reference.
  * 
  * @param labelText <br>
  *        The text to display next to the dropdown box.
  * @param menuWidthPx <br>
  *        The width of the dropdown menu in pixels. This argument should be strictly positive.
  * @param values <br>
  *        An array of strings. Each element in this array is an element in the dropdown box.
  * @param initialValue <br>
  *        The initial entry selected from the dropdown box. This string must be a member of
  *        <code>values</code>.
  * @param callback <br>
  *        A callback method accepting one argument. This method is called with the string
  *        name of the dropdown box element selected by the user whenever such a selection
  *        event happens.
  */
$.fn.dropDownWithLabel = function(labelText, menuWidthPx, values, initialValue, callback) {
 var menuId = this.attr('id') + '_menu';
 this.append(
  $('<div/>', {style: 'float:right; height:100%; wigth:' + menuWidthPx 
   + 'px; margin: 0px'}).append(
   $('<select style="width: inherit; height:100%" name="' + menuId + '" id="' + menuId + '">')
    ));
 for(var i = 0; i< values.length; ++i) {
  var option = $('<option>').html(values[i]);
  if(values[i] == initialValue)
   option.attr('selected', 'selected');
  $('#' + menuId).append(option);
 }
 $('#' + menuId).selectmenu({select: function(event, ui) {
  if(callback != null)
   callback(ui.item.value);
 }});
 this.append(
   $('<div/>', {style: 'float:right; height:100%; margin-right: 5px; padding-top: 5px'})
    .addClass('BigFont').html(labelText));
 return this;
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