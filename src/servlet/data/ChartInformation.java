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

import com.google.common.base.Preconditions;

/**
  * Information about the type of a chart, and properties about the data in the
  * chart.
  * 
  * @author phillips
  */
public final class ChartInformation {
    
   private String
      id,
      type;
   private int
      size;
   
   /**
     * Create a {@link ChartInformation} object.
     * 
     * @param id <br>
     *        The identifying ID of the chart described by this object. This argument
     *        must be non-<code>null</code>.
     * @param type <br>
     *        The type of the chart described by this object. This argument must be
     *        non-<code>null</code>.
     * @param size <br>
     *        The size of the chart described by this object. This argument may (depending
     *        on the underlying chart) represent the number of data points on the chart. This
     *        argument is expected to be non-negative, but this is not required.
     */
   public ChartInformation(
      final String id,
      final String type,
      final int size) {
      this.id = Preconditions.checkNotNull(id);
      this.type = Preconditions.checkNotNull(type);
      this.size = Preconditions.checkNotNull(size);
   }
   
   /**
     * @return
     *    Get the identifying ID of the chart described by this class.
     */
   public String getId() {
      return this.id;
   }
   
   /**
     * @param id
     *    Set the identifying ID of the chart described by this class. The argument
     *    must be non-<code>null</code>. 
     */
   public void setId(
      final String id) {
      this.id = Preconditions.checkNotNull(id);
   }
   
   /**
     * @return type
     *    Get the type of the chart described by this class. This method cannot return
     *    <code>null</code>.
     */
   public String getType() {
      return this.type;
   }
   
   /**
     * @param type
     *    Set the type of the chart described by this class. The argument must be
     *    non-<code>null</code>.
     */  
   public void setType(
      final String type) {
      this.type = Preconditions.checkNotNull(type);
   }
   
   /**
     * @return
     *    Get the size of the underlying chart described by this class.
     */
   public int getSize() {
      return this.size;
   }
   
   /**
     * @param size
     *   The size of the underlying chart described by this class.  This
     *   argument is expected to be non-negative, but this is not required.
     */
   public void setSize(
      int size) {
      this.size = size;
   }
   
   /**
     * Returns a brief description of this object. The exact details of the
     * string are subject to change, and should not be regarded as fixed.
     */
   @Override
   public String toString() {
      return getClass().getSimpleName() + ", id:" + this.id + ", type" + this.type 
            + ", size=" + this.size + ".";
   }
}
