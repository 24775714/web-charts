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

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.google.common.base.Preconditions;

/**
  * A local {@link NavigableMap} representation of line chart data.
  * 
  * @author phillips
  */
public final class LineChartData implements NavigableMap<Double, Double> {
   
   private final NavigableMap<Double, Double>
      values;
   private final String
      chartName;
   
   /**
     * Create a {@link LineChartData} object.
     * 
     * @param chartName <br>
     *        The identifying ID of this chart. This argument must be non-<code>null</code>
     *        and non-empty.
     */
   public LineChartData(final String chartName) {
      this.values = new TreeMap<Double, Double>();
      if(chartName.isEmpty())
         throw new IllegalArgumentException(getClass().getSimpleName() + ": chart name is empty.");
      this.chartName = Preconditions.checkNotNull(chartName);
   }
   
   @Override
   public int size() {
      return this.values.size();
   }
   
   @Override
   public boolean isEmpty() {
      return this.values.isEmpty();
   }
   
   @Override
   public boolean containsKey(
      final Object key) {
      return this.values.containsKey(key);
   }
   
   @Override
   public boolean containsValue(final Object value) {
      return this.values.containsValue(value);
   }
   
   @Override
   public Double get(final Object key) {
      return this.values.get(key);
   }
   
   @Override
   public Double put(
      Double key,
      Double value
      ) {
      return this.values.put(
         key,
         value);
   }
   
   @Override
   public Double remove(Object key) {
      return this.values.remove(key);
   }
   
   @Override
   public void putAll(
      Map<? extends Double, ? extends Double> m) {
      this.values.putAll(m);
   }
   
   @Override
   public void clear() {
      this.values.clear();
   }
   
   @Override
   public Set<Double> keySet() {
      return this.values.keySet();
   }
   
   @Override
   public Collection<Double> values() {
      return this.values.values();
   }
   
   @Override
   public Set<java.util.Map.Entry<Double, Double>> entrySet() {
      return this.values.entrySet();
   }
   
   @Override
   public boolean equals(
      Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (!(obj instanceof LineChartData))
         return false;
      LineChartData other = (LineChartData) obj;
      if (this.chartName == null) {
         if (other.chartName != null)
            return false;
      } else if (!this.chartName.equals(other.chartName))
         return false;
      return true;
   }
   
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result
         + ((this.chartName == null) ? 0 : this.chartName.hashCode());
      return result;
   }
   
   @Override
   public java.util.Map.Entry<Double, Double> lowerEntry(
      Double key) {
      return this.values.lowerEntry(key);
   }
   
   @Override
   public Double lowerKey(
      Double key) {
      return this.values.lowerKey(key);
   }
   
   @Override
   public java.util.Map.Entry<Double, Double> floorEntry(
      Double key) {
      return this.values.floorEntry(key);
   }
   
   @Override
   public Comparator<? super Double> comparator() {
      return this.values.comparator();
   }
   
   @Override
   public Double floorKey(
      Double key) {
      return this.values.floorKey(key);
   }
   
   @Override
   public java.util.Map.Entry<Double, Double> ceilingEntry(
      Double key) {
      return this.values.ceilingEntry(key);
   }
   
   @Override
   public Double ceilingKey(
      Double key) {
      return this.values.ceilingKey(key);
   }
   
   @Override
   public java.util.Map.Entry<Double, Double> higherEntry(
      Double key) {
      return this.values.higherEntry(key);
   }
   
   @Override
   public Double higherKey(
      Double key) {
      return this.values.higherKey(key);
   }
   
   @Override
   public java.util.Map.Entry<Double, Double> firstEntry() {
      return this.values.firstEntry();
   }
   
   @Override
   public java.util.Map.Entry<Double, Double> lastEntry() {
      return this.values.lastEntry();
   }
   
   @Override
   public java.util.Map.Entry<Double, Double> pollFirstEntry() {
      return this.values.pollFirstEntry();
   }
   
   @Override
   public java.util.Map.Entry<Double, Double> pollLastEntry() {
      return this.values.pollLastEntry();
   }
   
   @Override
   public NavigableMap<Double, Double> descendingMap() {
      return this.values.descendingMap();
   }
   
   @Override
   public Double firstKey() {
      return this.values.firstKey();
   }
   
   @Override
   public NavigableSet<Double> navigableKeySet() {
      return this.values.navigableKeySet();
   }
   
   @Override
   public Double lastKey() {
      return this.values.lastKey();
   }
   
   @Override
   public NavigableSet<Double> descendingKeySet() {
      return this.values.descendingKeySet();
   }
   
   @Override
   public NavigableMap<Double, Double> subMap(
      Double fromKey,
      boolean fromInclusive,
      Double toKey,
      boolean toInclusive) {
      return this.values.subMap(
         fromKey,
         fromInclusive,
         toKey,
         toInclusive);
   }
   
   @Override
   public NavigableMap<Double, Double> headMap(
      Double toKey,
      boolean inclusive) {
      return this.values.headMap(
         toKey,
         inclusive);
   }
   
   @Override
   public NavigableMap<Double, Double> tailMap(
      Double fromKey,
      boolean inclusive) {
      return this.values.tailMap(
         fromKey,
         inclusive);
   }
   
   @Override
   public SortedMap<Double, Double> subMap(
      Double fromKey,
      Double toKey) {
      return this.values.subMap(
         fromKey,
         toKey);
   }
   
   @Override
   public SortedMap<Double, Double> headMap(
      Double toKey) {
      return this.values.headMap(toKey);
   }
   
   @Override
   public SortedMap<Double, Double> tailMap(
      Double fromKey) {
      return this.values.tailMap(fromKey);
   }
   
   /**
     * @return
     *   Get the name of this chart. Different charts are required to have different names.
     *   This method cannot return null and cannot return the empty {@link String}.
     */
   public String name() {
      return this.chartName;
   }
}
