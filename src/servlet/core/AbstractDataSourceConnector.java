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
package servlet.core;

import com.google.common.base.Preconditions;

/**
  * A skeletal implementation of the {@link DataSourceConnector} interface. This
  * implementation provides the following methods:
  * 
  * <ul>
  *   <li> {@link #getDataSourceID()}
  *   <li> {@link #getDataSourceName()}
  * </ul>
  * 
  * All other methods specified by the {@link DataSourceConnector} interface must
  * be provided by the implementing class.<br><br>
  * 
  * The unique ID of this object is generated randomly.<br><br>
  * 
  * See also {@link AbstractDataSourceConnector#AbstractDataSourceConnector(String)}
  * and {@link DataSourceConnector}.
  * 
  * @author phillips
  */
public abstract class AbstractDataSourceConnector implements DataSourceConnector {
   
   private final String
      uid,
      simpleName;
   
   /**
     * Create an {@link AbstractDataSourceConnector} object with custom parameters.<br><br>
     * 
     * See also {@link AbstractDataSourceConnector}.
     * 
     * @param simpleName <br>
     *        The simple name (display name) of this data connection. This argument
     *        must be non-<code>null</code> and non-empty.
     */
   protected AbstractDataSourceConnector(
      final String simpleName
      ) {
      this.uid = java.util.UUID.randomUUID().toString();
      this.simpleName = Preconditions.checkNotNull(simpleName);
   }
   
   @Override
   public final String getDataSourceName() {
      return this.simpleName;
   }

   @Override
   public final String getDataSourceID() {
      return this.uid;
   }
}
