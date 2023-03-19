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
package org.dashbuilder.dataset.group;

import java.util.ArrayList;
import java.util.List;

/**
 * An interval represent a grouped subset of a data values.
 */
public class Interval {

    /**
     * A name that identifies the interval and it's different of other intervals belonging to the same group.
     */
    protected String name = null;

    /**
     * The position of this interval (starting at 0) within the group operation interval list.
     */
    protected int index;

    /**
     * The row indexes of the values that belong to this interval.
     */
    protected List<Integer> rows = new ArrayList<Integer>();

    /**
     * The interval type
     */
    protected String type;

    /**
     * The min. within the interval (Only for date columns)
     */
    protected Object minValue;

    /**
     * The max. date within the interval (Only for date columns)
     */
    protected Object maxValue;

    public Interval() {
    }

    public Interval(String name) {
        this.name = name;
    }

    public Interval(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getRows() {
        return rows;
    }

    public void setRows(List<Integer> rows) {
        this.rows = rows;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public Interval cloneInstance() {
        Interval clone = new Interval(name);
        clone.index = index;
        clone.type = type;
        clone.minValue = minValue;
        clone.maxValue = maxValue;
        return clone;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Interval other = (Interval) obj;
        if (name == null) {
            return other.name == null;
        } else {
            return name.equals(other.name);
        }
    }

    @Override
    public int hashCode() {
        if (name == null) return 0;
        return name.hashCode();
    }
}
