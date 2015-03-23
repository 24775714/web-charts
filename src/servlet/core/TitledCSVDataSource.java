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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.univocity.parsers.common.processor.RowListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import servlet.data.ChartInformation;
import servlet.data.TimestampedDatum;

/**
  * @author phillips
  */
public final class TitledCSVDataSource extends AbstractDataSourceConnector {
   
   Set<String>
      chartNames;
   int
      dataLength;
   final String
      timeColumnName;
   List<Double>
      timeKeys;
   
   /**
     * @param filename
     * @param timeColumnName 
     * 
     * @throws IllegalArgumentException if the datafile specified is empty (contains
     *         no header data) or if <code>filename</code> is empty.
     * @throws IOException if the underlying CSV file was missing, inaccessible, or
     *         not parseable.
     */
   public TitledCSVDataSource(
      final String filename,
      final String timeColumnName
      ) throws IOException {
      super(Preconditions.checkNotNull(filename));
      this.timeColumnName = Preconditions.checkNotNull(timeColumnName);
      if(filename.isEmpty())
         throw new IllegalArgumentException(
            getClass().getSimpleName() + ": csv filename is empty.");
      if(timeColumnName.isEmpty())
         throw new IllegalArgumentException(
            getClass().getSimpleName() + ": the name of the time column is empty.");
      
      {  // Count the number of lines
         BufferedReader
            reader = null;
         try {
            reader = Files.newBufferedReader(Paths.get(filename));
            this.dataLength = (int) reader.lines().count() - 1;
            reader.close();
         } catch (final Exception e) {
            if(reader != null)
               reader.close();
            throw e;
         }
         if(this.dataLength < 1)
            throw new IllegalArgumentException(
               getClass().getSimpleName() + ": the datafile contains no records.");
      }
      
      {
      CsvParser
         parser = createParser();
      
      {  // Extract headers
         FileReader
            reader = null;
         try {
            reader = new FileReader(filename);
            parser.beginParsing(reader);
            this.chartNames = new HashSet<String>();
            for(final String chartName : parser.parseNext())
               this.chartNames.add(chartName);
            parser.stopParsing();
            reader.close();
         } catch (final Exception e) {
            if(reader != null)
               reader.close();
            throw e;
         }
      }
      }
      
      {  // Extract the (abstract) 'time' column
         if(!this.chartNames.contains(timeColumnName))
            throw new IllegalArgumentException(
               getClass().getSimpleName() + ": csv file does not contain a column of data with "
             + "name '" + timeColumnName + "'");
         
         FileReader
            reader = null;
         try {
            reader = new FileReader(filename);
            final CsvParserSettings
               parserSettings = createParserSettings();
            parserSettings.selectFields(timeColumnName);
            parserSettings.setHeaderExtractionEnabled(true);
            final List<String[]>
               data = (new CsvParser(parserSettings)).parseAll(reader);
            this.timeKeys = new ArrayList<Double>();
            for(int i = 0; i< data.size(); ++i)
               this.timeKeys.add(Double.parseDouble(data.get(i)[0]));
            reader.close();
         } catch (final Exception e) {
            if(reader != null)
               reader.close();
            throw e;
         }
      }
   }
   
   private CsvParser createParser() {
      return new CsvParser(createParserSettings());
   }
   
   private CsvParserSettings createParserSettings() {
      final CsvParserSettings
         parserSettings = new CsvParserSettings();
      parserSettings.setSkipEmptyLines(true);
      parserSettings.setIgnoreLeadingWhitespaces(true);
      parserSettings.setIgnoreTrailingWhitespaces(true);
      parserSettings.setMaxColumns(5000);
      parserSettings.getFormat().setDelimiter('|');
      parserSettings.setHeaderExtractionEnabled(false);
      
      RowListProcessor
         rowProcessor = new RowListProcessor();
      parserSettings.setRowProcessor(rowProcessor);
      
      return parserSettings;
   }
   
   @Override
   public List<ChartInformation> getKnownCharts() {
      final List<ChartInformation>
         knownCharts = new ArrayList<ChartInformation>();
      for(final String chartName : this.chartNames)
         knownCharts.add(new ChartInformation(chartName, "Line", this.dataLength));
      return knownCharts;
   }
   
   @Override
   public synchronized List<TimestampedDatum> getData(
      String chartName,
      final double fromTimeOfInterest,
      final boolean inclusive
      ) throws DataSourceException {
      int
         rowIndexFrom = Collections.binarySearch(this.timeKeys, fromTimeOfInterest);
      if(rowIndexFrom < 0)
         rowIndexFrom = -(rowIndexFrom + 1);
      if(rowIndexFrom == this.dataLength)
         if(!inclusive)
            return new ArrayList<TimestampedDatum>();
      
      {  // Extract data
         FileReader
            reader = null;
         try {
            reader = new FileReader(getCSVFilename());
            final CsvParserSettings
               parserSettings = createParserSettings();
            parserSettings.selectFields(chartName);
            parserSettings.setHeaderExtractionEnabled(true);
            final List<String[]>
               data = (new CsvParser(parserSettings)).parseAll(reader);
            final List<TimestampedDatum>
               result = new ArrayList<TimestampedDatum>();
            {
               final double
                  leastKey = this.timeKeys.get(rowIndexFrom);
            if(leastKey > fromTimeOfInterest ||
               (Double.compare(leastKey, fromTimeOfInterest) == 0 && inclusive))
               result.add(TimestampedDatum.create(
                  leastKey, safeParseDouble(data.get(rowIndexFrom)[0]))); 
            }
            for(int i = rowIndexFrom + 1; i< this.dataLength; ++i)
               result.add(TimestampedDatum.create(
                  this.timeKeys.get(i), safeParseDouble(data.get(i)[0])));
            reader.close();
            return result;
         } catch (final Exception e) {
            if(reader != null)
               try {
                  reader.close();
               } catch (final IOException cannotCloseStream) {
                  throw new DataSourceException(cannotCloseStream);
               }
            throw new DataSourceException(e);
         }
      }
   }
   
   private Double safeParseDouble(final String input) {
      double
         result = Double.parseDouble(input);
      if(!Double.isFinite(result))
         return 0.0;
      else return result;
   }
   
   private String getCSVFilename() {
      return super.getDataSourceName();
   }
}
