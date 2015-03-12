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
package servlet;

public class ChartInformation {
   private String
      id,
      type;
   private int
      size;
   
   public ChartInformation(
      final String id,
      final String type,
      final int size
      ) {
      this.id = id;
      this.type = type;
      this.size = size;
   }
   
   public String getId() {
      return id;
   }
   
   public void setId(
      String id) {
      this.id = id;
   }
   
   public String getType() {
      return type;
   }
   
   public void setType(
      String type) {
      this.type = type;
   }
   
   public int getSize() {
      return size;
   }
   
   public void setSize(
      int size) {
      this.size = size;
   }
}
