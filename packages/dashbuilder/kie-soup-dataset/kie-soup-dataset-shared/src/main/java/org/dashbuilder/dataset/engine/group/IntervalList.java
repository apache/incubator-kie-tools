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
package org.dashbuilder.dataset.engine.group;

import java.util.ArrayList;
import java.util.List;

import org.dashbuilder.dataset.group.ColumnGroup;
import org.dashbuilder.dataset.group.Interval;

/**
 * An list containing the intervals derived from an specific domain configuration.
 */
public abstract class IntervalList extends ArrayList<Interval> {

    protected ColumnGroup columnGroup = null;
    protected String intervalType = null;
    protected Object minValue = null;
    protected Object maxValue = null;

    public IntervalList(ColumnGroup columnGroup) {
        super();
        this.columnGroup = columnGroup;
        this.intervalType = columnGroup.getIntervalSize();
    }

    public ColumnGroup getColumnGroup() {
        return columnGroup;
    }

    public String getIntervalType() {
        return intervalType;
    }

    public void setIntervalType(String intervalType) {
        this.intervalType = intervalType;
    }

    public Object getMinValue() {
        return minValue;
    }

    public void setMinValue(Object minValue) {
        this.minValue = minValue;
    }

    public Object getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Object maxValue) {
        this.maxValue = maxValue;
    }

    /**
     * Creates and classify the list of specified values into intervals.
     */
    public IntervalList indexValues(List<Object> values, List<Integer> rows) {
        if (rows == null) {
            for (int row = 0; row < values.size(); row++) {
                Object value = values.get(row);
                indexValue(value, row);
            }
        } else {
            for (Integer row : rows) {
                Object value = values.get(row);
                indexValue(value, row);
            }
        }
        return this;
    }

    /**
     * Index the given value into the appropriate interval.
     * @param value The value to index
     * @param row The row index where the value is hold within the data set.
     */
    public void indexValue(Object value, int row) {
        if (value != null) {
            Interval interval = locateInterval(value);
            if (interval != null) {
                interval.getRows().add(row);
            }
        }
    }

    /**
     * Get the interval that holds the given value.
     * @param value The value we are asking for.
     * @return The interval which the value belongs to.
     */
    public abstract Interval locateInterval(Object value);
}
