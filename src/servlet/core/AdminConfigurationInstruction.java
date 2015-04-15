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

import javax.servlet.ServletContext;

/**
  * An interface describing a configuration instruction sent to an
  * {@link AdminServlet}.
  * 
  * @author phillips
  */
public interface AdminConfigurationInstruction {
   
   /**
     * Perform any configuration actions associated with this instruction, and return
     * a {@link String} describing the result.
     * 
     * @param context <br>
     *        The {@link ServletContext} in which the parent web application is processed.
     * @throws AdminConfigurationInstructionException If for any reason the configuration
     *         described by this instruction failed.
     */
   public void configure(
      final ServletContext context) throws AdminConfigurationInstructionException;
   
   /**
     * An {@link Exception} indicating that an {@link AdminConfigurationInstruction} failed.
     * 
     * @author phillips
     */
   public final static class AdminConfigurationInstructionException extends Exception {
      
      /**
        * Create an {@link AdminConfigurationInstructionException}.
        * 
        * @param message
        *    A message describing the exception.
        */
      public AdminConfigurationInstructionException(final String message) {
          super(message);
      }
      
      /**
        * Create an {@link AdminConfigurationInstructionException}.
        * 
        * @param message <br>
        *        A message describing the exception.
        * @param cause <br>
        *        The underlying {@link Throwable} cause of the exeption, if any.
        */
      public AdminConfigurationInstructionException(final String message, final Throwable cause) {
          super(message, cause);
      }
      
      private static final long serialVersionUID = 3099881080861511504L;
   }
}
