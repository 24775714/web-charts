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

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.google.common.base.Preconditions;

public final class LineChartData implements NavigableMap<Double, Double> {
   
   private final NavigableMap<Double, Double>
      values;
   private final String
      chartName;
   
   public LineChartData(final String chartName) {
      this.values = new TreeMap<Double, Double>();
      this.chartName = Preconditions.checkNotNull(chartName);
   }
   
   @Override
   public int size() {
      return values.size();
   }
   
   @Override
   public boolean isEmpty() {
      return values.isEmpty();
   }
   
   @Override
   public boolean containsKey(
      final Object key) {
      return values.containsKey(key);
   }
   
   @Override
   public boolean containsValue(final Object value) {
      return values.containsValue(value);
   }
   
   @Override
   public Double get(final Object key) {
      return values.get(key);
   }
   
   @Override
   public Double put(
      Double key,
      Double value
      ) {
      return values.put(
         key,
         value);
   }
   
   @Override
   public Double remove(Object key) {
      return values.remove(key);
   }
   
   @Override
   public void putAll(
      Map<? extends Double, ? extends Double> m) {
      values.putAll(m);
   }
   
   @Override
   public void clear() {
      values.clear();
   }
   
   @Override
   public Set<Double> keySet() {
      return values.keySet();
   }
   
   @Override
   public Collection<Double> values() {
      return values.values();
   }
   
   @Override
   public Set<java.util.Map.Entry<Double, Double>> entrySet() {
      return values.entrySet();
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
      if (chartName == null) {
         if (other.chartName != null)
            return false;
      } else if (!chartName.equals(other.chartName))
         return false;
      return true;
   }
   
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result
         + ((chartName == null) ? 0 : chartName.hashCode());
      return result;
   }
   
   @Override
   public java.util.Map.Entry<Double, Double> lowerEntry(
      Double key) {
      return values.lowerEntry(key);
   }
   
   @Override
   public Double lowerKey(
      Double key) {
      return values.lowerKey(key);
   }
   
   @Override
   public java.util.Map.Entry<Double, Double> floorEntry(
      Double key) {
      return values.floorEntry(key);
   }
   
   @Override
   public Comparator<? super Double> comparator() {
      return values.comparator();
   }
   
   @Override
   public Double floorKey(
      Double key) {
      return values.floorKey(key);
   }
   
   @Override
   public java.util.Map.Entry<Double, Double> ceilingEntry(
      Double key) {
      return values.ceilingEntry(key);
   }
   
   @Override
   public Double ceilingKey(
      Double key) {
      return values.ceilingKey(key);
   }
   
   @Override
   public java.util.Map.Entry<Double, Double> higherEntry(
      Double key) {
      return values.higherEntry(key);
   }
   
   @Override
   public Double higherKey(
      Double key) {
      return values.higherKey(key);
   }
   
   @Override
   public java.util.Map.Entry<Double, Double> firstEntry() {
      return values.firstEntry();
   }
   
   @Override
   public java.util.Map.Entry<Double, Double> lastEntry() {
      return values.lastEntry();
   }
   
   @Override
   public java.util.Map.Entry<Double, Double> pollFirstEntry() {
      return values.pollFirstEntry();
   }
   
   @Override
   public java.util.Map.Entry<Double, Double> pollLastEntry() {
      return values.pollLastEntry();
   }
   
   @Override
   public NavigableMap<Double, Double> descendingMap() {
      return values.descendingMap();
   }
   
   @Override
   public Double firstKey() {
      return values.firstKey();
   }
   
   @Override
   public NavigableSet<Double> navigableKeySet() {
      return values.navigableKeySet();
   }
   
   @Override
   public Double lastKey() {
      return values.lastKey();
   }
   
   @Override
   public NavigableSet<Double> descendingKeySet() {
      return values.descendingKeySet();
   }
   
   @Override
   public NavigableMap<Double, Double> subMap(
      Double fromKey,
      boolean fromInclusive,
      Double toKey,
      boolean toInclusive) {
      return values.subMap(
         fromKey,
         fromInclusive,
         toKey,
         toInclusive);
   }
   
   @Override
   public NavigableMap<Double, Double> headMap(
      Double toKey,
      boolean inclusive) {
      return values.headMap(
         toKey,
         inclusive);
   }
   
   @Override
   public NavigableMap<Double, Double> tailMap(
      Double fromKey,
      boolean inclusive) {
      return values.tailMap(
         fromKey,
         inclusive);
   }
   
   @Override
   public SortedMap<Double, Double> subMap(
      Double fromKey,
      Double toKey) {
      return values.subMap(
         fromKey,
         toKey);
   }
   
   @Override
   public SortedMap<Double, Double> headMap(
      Double toKey) {
      return values.headMap(toKey);
   }
   
   @Override
   public SortedMap<Double, Double> tailMap(
      Double fromKey) {
      return values.tailMap(fromKey);
   }
   
   /**
     * Get the name of this chart. Different charts are required to have different names.
     */
   public String name() {
      return this.chartName;
   }
}
