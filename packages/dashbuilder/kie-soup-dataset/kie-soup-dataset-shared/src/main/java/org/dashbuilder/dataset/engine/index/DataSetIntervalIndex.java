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

import java.util.Arrays;
import java.util.List;

import org.dashbuilder.dataset.group.Interval;
import org.dashbuilder.dataset.impl.MemSizeEstimator;

/**
 * An interval index
 */
public class DataSetIntervalIndex extends DataSetIndexNode implements DataSetIntervalIndexHolder {

    String intervalName = null;
    String intervalType = null;
    Object minValue = null;
    Object maxValue = null;

    public DataSetIntervalIndex(DataSetGroupIndex parent, String intervalName) {
        super(parent, null, 0);
        this.intervalName = intervalName;
    }

    public DataSetIntervalIndex(DataSetGroupIndex parent, Interval interval) {
        super(parent, interval.getRows(), 0);
        this.intervalName = interval.getName();
        intervalType = interval.getType();
        minValue = interval.getMinValue();
        maxValue = interval.getMaxValue();
    }

    public List<DataSetIntervalIndex> getIntervalIndexes() {
        return Arrays.asList(this);
    }

    public String getName() {
        return intervalName;
    }

    public long getEstimatedSize() {
        long result = super.getEstimatedSize();
        if (intervalName != null) {
            result += MemSizeEstimator.sizeOfString(intervalName);
        }
        return result;
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

    public String toString() {
        StringBuilder out = new StringBuilder(intervalName);
        out.append(" ").append(super.toString());
        return out.toString();
    }
}

