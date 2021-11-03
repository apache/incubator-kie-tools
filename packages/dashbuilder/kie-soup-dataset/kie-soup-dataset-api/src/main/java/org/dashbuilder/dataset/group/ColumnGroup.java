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

import org.dashbuilder.dataset.date.DayOfWeek;
import org.dashbuilder.dataset.date.Month;

/**
 * A column group definition.
 */
public class ColumnGroup {

    protected String sourceId = null;
    protected String columnId = null;
    protected GroupStrategy strategy = GroupStrategy.DYNAMIC;
    protected int maxIntervals = 15;
    protected String intervalSize = null;
    protected boolean emptyIntervals = false;
    protected boolean ascendingOrder = true;
    protected Month firstMonthOfYear;
    protected DayOfWeek firstDayOfWeek;
    private boolean isPostEnabled = true;

    public ColumnGroup() {
    }

    public ColumnGroup(String columnId, String newColumnId) {
        this.sourceId = columnId;
        this.columnId = newColumnId;
    }

    public ColumnGroup(String columnId, String newColumnId, GroupStrategy strategy) {
        this.sourceId = columnId;
        this.columnId = newColumnId;
        this.strategy = strategy;
    }

    public ColumnGroup(String columnId, String newColumnId, GroupStrategy strategy, int maxIntervals, String intervalSize) {
        this.sourceId = columnId;
        this.columnId = newColumnId;
        this.strategy = strategy;
        this.maxIntervals = maxIntervals;
        this.intervalSize = intervalSize;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    public GroupStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(GroupStrategy strategy) {
        this.strategy = strategy;
    }

    public int getMaxIntervals() {
        return maxIntervals;
    }

    public void setMaxIntervals(int maxIntervals) {
        this.maxIntervals = maxIntervals;
    }

    public String getIntervalSize() {
        return intervalSize;
    }

    public void setIntervalSize(String intervalSize) {
        this.intervalSize = intervalSize;
    }

    public boolean areEmptyIntervalsAllowed() {
        return emptyIntervals;
    }

    public void setEmptyIntervalsAllowed(boolean emptyIntervals) {
        this.emptyIntervals = emptyIntervals;
    }

    public boolean isAscendingOrder() {
        return ascendingOrder;
    }

    public void setAscendingOrder(boolean ascending) {
        this.ascendingOrder = ascending;
    }

    public Month getFirstMonthOfYear() {
        return firstMonthOfYear == null ? (isAscendingOrder() ? Month.JANUARY : Month.DECEMBER) : firstMonthOfYear;
    }

    public void setFirstMonthOfYear(Month firstMonthOfYear) {
        this.firstMonthOfYear = firstMonthOfYear;
    }

    public DayOfWeek getFirstDayOfWeek() {
        return firstDayOfWeek == null ? (isAscendingOrder() ? DayOfWeek.MONDAY : DayOfWeek.SUNDAY) : firstDayOfWeek;
    }

    public void setFirstDayOfWeek(DayOfWeek firstDayOfWeek) {
        this.firstDayOfWeek = firstDayOfWeek;
    }

    public ColumnGroup cloneInstance() {
        ColumnGroup clone = new ColumnGroup();
        clone.columnId = columnId;
        clone.sourceId = sourceId;
        clone.strategy = strategy;
        clone.maxIntervals = maxIntervals;
        clone.intervalSize = intervalSize;
        clone.emptyIntervals = emptyIntervals;
        clone.ascendingOrder = ascendingOrder;
        clone.firstDayOfWeek = firstDayOfWeek;
        clone.firstMonthOfYear = firstMonthOfYear;
        return clone;
    }

    public boolean equals(Object obj) {
        try {
            ColumnGroup other = (ColumnGroup) obj;
            if (sourceId != null && !sourceId.equals(other.sourceId)) return false;
            if (columnId != null && !columnId.equals(other.columnId)) return false;
            if (strategy != null && !strategy.equals(other.strategy)) return false;
            if (intervalSize != null && !intervalSize.equals(other.intervalSize)) return false;
            if (emptyIntervals != other.emptyIntervals) return false;
            if (maxIntervals != other.maxIntervals) return false;
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }

    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append("column=").append(sourceId).append(" ");
        out.append("strategy=").append(strategy).append(" ");
        if (intervalSize != null) out.append("intervalSize=").append(intervalSize).append(" ");
        out.append("emptyIntervals=").append(emptyIntervals).append(" ");
        out.append("maxIntervals=").append(maxIntervals);
        return out.toString();
    }

    public boolean isPostEnabled() {
        return isPostEnabled;
    }

    public void setPostEnabled(boolean isPostEnabled) {
        this.isPostEnabled = isPostEnabled;
    }
}
