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

import java.util.Collections;
import java.util.List;

import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.engine.DataSetHandler;
import org.dashbuilder.dataset.group.ColumnGroup;
import org.dashbuilder.dataset.group.DateIntervalType;
import org.dashbuilder.dataset.group.Interval;

import static org.dashbuilder.dataset.group.DateIntervalType.*;

/**
 * Interval builder for date columns which generates a fixed number of intervals for a given interval size.
 * <p>The only intervals sizes supported are: QUARTER, MONTH, DAY_OF_WEEK, HOUR, MINUTE & SECOND.</p>
 */
public class IntervalBuilderFixedDate implements IntervalBuilder {

    public IntervalList build(DataSetHandler ctx, ColumnGroup columnGroup) {
        IntervalList intervalList = _build(columnGroup);

        // Index the values
        String columnId = columnGroup.getSourceId();
        List values = ctx.getDataSet().getColumnById(columnId).getValues();
        List<Integer> rows = ctx.getRows();
        intervalList.indexValues(values, rows);
        return intervalList;
    }

    public IntervalList build(DataColumn dataColumn) {
        ColumnGroup columnGroup = dataColumn.getColumnGroup();
        return _build(columnGroup);
    }

    protected IntervalList _build(ColumnGroup columnGroup) {
        IntervalList intervalList = createIntervalList(columnGroup);

        // Reverse intervals if requested
        boolean asc = columnGroup.isAscendingOrder();
        if (!asc) {
            Collections.reverse(intervalList);
            intervalList.add( 0, intervalList.remove( intervalList.size() - 1));
        }
        return intervalList;
    }

    public IntervalList createIntervalList(ColumnGroup columnGroup) {
        DateIntervalType type = DateIntervalType.getByName(columnGroup.getIntervalSize());
        if (QUARTER.equals(type)) {
            return new IntervalListQuarter(columnGroup);
        }
        if (MONTH.equals(type)) {
            return new IntervalListMonth(columnGroup);
        }
        if (DAY_OF_WEEK.equals(type)) {
            return new IntervalListDayOfWeek(columnGroup);
        }
        if (HOUR.equals(type)) {
            return new IntervalListHour(columnGroup);
        }
        if (MINUTE.equals(type)) {
            return new IntervalListMinute(columnGroup);
        }
        if (SECOND.equals(type)) {
            return new IntervalListSecond(columnGroup);
        }
        throw new IllegalArgumentException("Interval size '" + columnGroup.getIntervalSize() + "' not supported for " +
                "fixed date intervals. The only supported sizes are: " + join(DateIntervalType.FIXED_INTERVALS_SUPPORTED, ","));
    }

    public String join(List array, String separator) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < array.size(); i++) {
            if (i > 0) builder.append(separator);
            builder.append(array.get(i));
        }
        return builder.toString();
    }
}
