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
import java.util.List;

/**
 * A set of interval indexes
 */
public class DataSetIntervalSetIndex extends DataSetIntervalIndex {

    List<DataSetIntervalIndex> intervalIndexList = new ArrayList<DataSetIntervalIndex>();

    DataSetIntervalSetIndex(DataSetGroupIndex parent, String intervalName) {
        super(parent, intervalName);
    }

    public List<DataSetIntervalIndex> getIntervalIndexes() {
        return intervalIndexList;
    }


    public void addIntervalIndex(DataSetIntervalIndex index) {
        intervalType = index.getIntervalType();
        Comparable min = (Comparable) index.getMinValue();
        Comparable max = (Comparable) index.getMaxValue();
        if (minValue == null || ((Comparable) minValue).compareTo(min) > 0) minValue = min;
        if (maxValue == null || ((Comparable) maxValue).compareTo(max) < 0) maxValue = max;
        intervalIndexList.add(index);
    }

    public List<Integer> getRows() {
        if (intervalIndexList == null || intervalIndexList.isEmpty()) {
            return null;
        }
        AggregatedList<Integer> result = new AggregatedList<Integer>();
        for (DataSetIntervalIndex intervalIndex : intervalIndexList) {
            result.addSubList(intervalIndex.getRows());
        }
        return result;
    }
}

