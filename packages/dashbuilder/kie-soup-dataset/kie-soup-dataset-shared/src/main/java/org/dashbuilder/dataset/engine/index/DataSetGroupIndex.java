/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.dataset.engine.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.dashbuilder.dataset.group.Interval;
import org.dashbuilder.dataset.engine.group.IntervalList;
import org.dashbuilder.dataset.engine.index.visitor.DataSetIndexVisitor;
import org.dashbuilder.dataset.group.ColumnGroup;

/**
 * A DataSet group operation index
 */
public class DataSetGroupIndex extends DataSetIndexNode implements DataSetIntervalIndexHolder {

    // The group index is composed by a set of interval indexes.
    ColumnGroup columnGroup = null;
    List<DataSetIntervalIndex> intervalIndexList = null;
    String intervalType = null;
    Object minValue = null;
    Object maxValue = null;

    // And can (optionally) contains a subset of interval selections.
    List<DataSetGroupIndex> selectIndexList = null;

    // When the group represents a selection it has a selection key.
    String selectKey = null;

    public DataSetGroupIndex(ColumnGroup columnGroup) {
        super();
        this.columnGroup = columnGroup;
        this.intervalIndexList = new ArrayList<DataSetIntervalIndex>();
        this.intervalType = columnGroup != null ? columnGroup.getIntervalSize() : null;
    }

    public DataSetGroupIndex(ColumnGroup columnGroup, IntervalList intervalList) {
        this(columnGroup);
        intervalType = intervalList.getIntervalType();
        minValue = intervalList.getMinValue();
        maxValue = intervalList.getMaxValue();
        for (Interval interval : intervalList) {
            intervalIndexList.add(new DataSetIntervalIndex(this, interval));
        }
    }

    public DataSetGroupIndex(String selectKey, List<DataSetIntervalIndex> intervalIndexes) {
        this(null);
        this.selectKey = selectKey;
        for (DataSetIntervalIndex index : intervalIndexes) {
            addIntervalIndex(index);
        }
    }

    public void addIntervalIndex(DataSetIntervalIndex index) {
        intervalType = index.getIntervalType();
        Comparable min = (Comparable) index.getMinValue();
        Comparable max = (Comparable) index.getMaxValue();
        if (minValue == null || ((Comparable) minValue).compareTo(min) > 0) minValue = min;
        if (maxValue == null || ((Comparable) maxValue).compareTo(max) < 0) maxValue = max;
        intervalIndexList.add(index);
    }

    public String getIntervalType() {
        return intervalType;
    }

    public Object getMinValue() {
        return minValue;
    }

    public Object getMaxValue() {
        return maxValue;
    }

    public void setIntervalType(String intervalType) {
        this.intervalType = intervalType;
    }

    public void setMinValue(Object minValue) {
        this.minValue = minValue;
    }

    public void setMaxValue(Object maxValue) {
        this.maxValue = maxValue;
    }

    public List<DataSetIntervalIndex> getIntervalIndexes() {
        return intervalIndexList;
    }

    public List<DataSetIntervalIndex> getIntervalIndexes(List<Interval> intervalList) {
        List<DataSetIntervalIndex> result = new ArrayList<DataSetIntervalIndex>();
        for (Interval interval : intervalList) {
            DataSetIntervalIndex idx = getIntervalIndex(interval.getName());
            if (idx != null) {
                result.add(idx);
            }
        }
        return result;
    }

    public DataSetIntervalIndex getIntervalIndex(String name) {
        for (DataSetIntervalIndex idx : intervalIndexList) {
            String idxName = idx.getName();
            if (idxName != null && idxName.equals(name)) {
                return idx;
            }
            if (idxName == null && name == null) {
                return idx;
            }
        }
        return null;
    }

    public int indexOfIntervalIndex(DataSetIntervalIndex target) {
        for (int i = 0; i < intervalIndexList.size(); i++) {
            DataSetIntervalIndex idx = intervalIndexList.get(i);
            String idxName = idx.getName();
            String targetName = target.getName();
            if (idxName != null && idxName.equals(targetName)) {
                return i;
            }
            if (idxName == null && targetName == null) {
                return i;
            }
        }
        return -1;
    }

    public DataSetGroupIndex getSelectionIndex(List<Interval> intervalList) {
        if (selectIndexList == null) {
            return null;
        }
        String targetKey = buildSelectKey(intervalList);
        for (DataSetGroupIndex idx : selectIndexList) {
            if (idx.selectKey.equals(targetKey)) {
                idx.reuseHit();
                return idx;
            }
        }
        return null;
    }

    public DataSetGroupIndex indexSelection(List<Interval> intervalList, List<DataSetIntervalIndex> intervalIndexes) {
        if (selectIndexList == null) {
            selectIndexList = new ArrayList<DataSetGroupIndex>();
        }
        String key = buildSelectKey(intervalList);
        DataSetGroupIndex index = new DataSetGroupIndex(key, intervalIndexes);
        index.setParent(this);
        index.setBuildTime(buildTime);
        selectIndexList.add(index);
        return index;
    }

    protected String buildSelectKey(List<Interval> intervalList) {
        StringBuilder out = new StringBuilder();
        for (int i=0; i<intervalList.size(); i++) {
            if (i > 0) out.append(", ");
            out.append(intervalList.get(i).getName());
        }
        return out.toString();
    }

    public List<Integer> getRows() {
        if (intervalIndexList == null || intervalIndexList.isEmpty()) {
            return null;
        }
        List<Integer> results = new ArrayList<Integer>();
        for (DataSetIntervalIndex intervalIndex : intervalIndexList) {
            results.addAll(intervalIndex.getRows());
        }
        return results;
    }

    public void indexIntervals(Collection<DataSetIntervalIndex> intervalsIdxs) {
        for (DataSetIntervalIndex idx : intervalsIdxs) {
            indexInterval(idx);
        }
    }

    public void indexInterval(DataSetIntervalIndex intervalIdx) {
        String intervalName = intervalIdx.getName();
        DataSetIntervalIndex existing = getIntervalIndex(intervalName);
        if (existing == null) {
            addIntervalIndex(intervalIdx);
        } else {
            if (existing instanceof DataSetIntervalSetIndex) {
                ((DataSetIntervalSetIndex) existing).addIntervalIndex(intervalIdx);
            }
            else if (existing != intervalIdx){
                int i = indexOfIntervalIndex(existing);
                DataSetIntervalSetIndex indexSet = new DataSetIntervalSetIndex(this, intervalName);
                indexSet.addIntervalIndex(existing);
                indexSet.addIntervalIndex(intervalIdx);
                intervalIndexList.set(i, indexSet);
            }
        }
    }

    public void acceptVisitor(DataSetIndexVisitor visitor) {
        super.acceptVisitor(visitor);

        for (DataSetIntervalIndex index : intervalIndexList) {
            index.acceptVisitor(visitor);
        }
    }

    public String toString() {
        StringBuilder out = new StringBuilder(super.toString());
        if (columnGroup != null) out.append(" column=").append(columnGroup.getColumnId());
        if (selectKey != null) out.append(" select=").append(selectKey);
        return out.toString();
    }
}

