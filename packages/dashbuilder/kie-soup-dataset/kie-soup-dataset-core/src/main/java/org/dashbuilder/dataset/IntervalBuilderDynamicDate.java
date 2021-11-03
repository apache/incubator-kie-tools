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
package org.dashbuilder.dataset;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.dashbuilder.dataset.date.Quarter;
import org.dashbuilder.dataset.engine.DataSetHandler;
import org.dashbuilder.dataset.engine.group.IntervalBuilder;
import org.dashbuilder.dataset.engine.group.IntervalList;
import org.dashbuilder.dataset.group.ColumnGroup;
import org.dashbuilder.dataset.group.DateIntervalType;
import org.dashbuilder.dataset.group.Interval;
import org.dashbuilder.dataset.sort.ColumnSort;
import org.dashbuilder.dataset.sort.DataSetSort;
import org.dashbuilder.dataset.sort.SortOrder;
import org.dashbuilder.dataset.sort.SortedList;

import static org.dashbuilder.dataset.group.DateIntervalType.*;

/**
 * Interval builder for date columns which generates intervals depending on the underlying data available.
 */
public class IntervalBuilderDynamicDate implements IntervalBuilder {

    private static SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

    public IntervalList build(DataSetHandler handler, ColumnGroup columnGroup) {
        IntervalDateRangeList results = new IntervalDateRangeList(columnGroup);
        DataSet dataSet = handler.getDataSet();
        List values = dataSet.getColumnById(columnGroup.getSourceId()).getValues();
        if (values.isEmpty()) {
            return results;
        }

        // Sort the column dates.
        DataSetSort sortOp = new DataSetSort();
        sortOp.addSortColumn(new ColumnSort(columnGroup.getSourceId(), SortOrder.ASCENDING));
        DataSetHandler sortResults = handler.sort(sortOp);
        List<Integer> sortedRows = sortResults.getRows();
        if (sortedRows == null || sortedRows.isEmpty()) {
            return results;
        }

        // Get the lower & upper limits (discard nulls).
        SortedList sortedValues = new SortedList(values, sortedRows);
        Date minDate = null;
        Date maxDate = null;
        for (int i = 0; minDate == null && i < sortedValues.size(); i++) {
            minDate = (Date) sortedValues.get(i);
        }
        for (int i = sortedValues.size()-1; maxDate == null && i >= 0; i--) {
            maxDate = (Date) sortedValues.get(i);
        }

        // If min/max are equals then return a single interval.
        DateIntervalType intervalType = calculateIntervalSize(minDate, maxDate, columnGroup);
        if (minDate == null || minDate.compareTo(maxDate) == 0) {

            IntervalDateRange interval = new IntervalDateRange(0, intervalType, minDate, maxDate);
            for (int row = 0; row < sortedValues.size(); row++) interval.getRows().add(row);

            results.add(interval);
            results.setIntervalType(columnGroup.getIntervalSize());
            results.setMinValue(minDate);
            results.setMaxValue(maxDate);
            return results;
        }

        // Create the intervals according to the min/max dates.
        Calendar c = firstIntervalDate(intervalType, minDate, columnGroup);
        int index = 0;
        int counter = 0;
        while (c.getTime().compareTo(maxDate) <= 0) {
            Date intervalMinDate = c.getTime();

            // Create the next interval
            nextIntervalDate(c, intervalType, 1);
            Date intervalMaxDate = c.getTime();
            IntervalDateRange interval = new IntervalDateRange(counter++, intervalType, intervalMinDate, intervalMaxDate);
            results.add(interval);

            // Add the target rows
            boolean stop = false;
            while (!stop) {
                if (index >= sortedValues.size()) {
                    stop = true;
                } else {
                    Date dateValue = (Date) sortedValues.get(index);
                    Integer row = sortedRows.get(index);
                    if (dateValue == null) {
                        index++;
                    } else if (dateValue.before(intervalMaxDate)) {
                        interval.getRows().add(row);
                        index++;
                    } else {
                        stop = true;
                    }
                }
            }
        }

        // Reverse intervals if requested
        boolean asc = columnGroup.isAscendingOrder();
        if (!asc) Collections.reverse( results );

        // Return the results
        results.setIntervalType(intervalType.toString());
        results.setMinValue(minDate);
        results.setMaxValue(maxDate);
        return results;
    }

    public IntervalList build(DataColumn dataColumn) {
        ColumnGroup columnGroup = dataColumn.getColumnGroup();
        Date minDate = (Date) dataColumn.getMinValue();
        Date maxDate = (Date) dataColumn.getMaxValue();

        IntervalDateRangeList results = new IntervalDateRangeList(columnGroup);
        if (minDate == null || maxDate == null) {
            return results;
        }
        DateIntervalType intervalType = DateIntervalType.getByName(dataColumn.getIntervalType());
        if (intervalType == null) {
            intervalType = DateIntervalType.YEAR;
        }
        Calendar c = firstIntervalDate(intervalType, minDate, columnGroup);
        int counter = 0;
        while (c.getTime().compareTo(maxDate) <= 0) {
            Date intervalMinDate = c.getTime();

            // Create the next interval
            nextIntervalDate(c, intervalType, 1);
            Date intervalMaxDate = c.getTime();
            IntervalDateRange interval = new IntervalDateRange(counter++, intervalType, intervalMinDate, intervalMaxDate);
            results.add(interval);
        }

        // Reverse intervals if requested
        boolean asc = columnGroup.isAscendingOrder();
        if (!asc) Collections.reverse( results );

        // Return the results
        results.setIntervalType(intervalType.toString());
        results.setMinValue(minDate);
        results.setMaxValue(maxDate);
        return results;
    }

    public DateIntervalType calculateIntervalSize(Date minDate, Date maxDate, ColumnGroup columnGroup) {

        DateIntervalType intervalType = DateIntervalType.getByName(columnGroup.getIntervalSize());
        if (intervalType == null) {
            intervalType = YEAR;
        }

        if (minDate == null || maxDate == null) {
            return intervalType;
        }

        long millis = (maxDate.getTime() - minDate.getTime());
        if (millis <= 0) {
            return intervalType;
        }

        // Calculate the interval type used according to the constraints set.
        int maxIntervals = columnGroup.getMaxIntervals();
        if (maxIntervals < 1) maxIntervals = 15;
        for (DateIntervalType type : values()) {
            long nintervals = millis / getDurationInMillis(type);
            if (nintervals < maxIntervals) {
                intervalType = type;
                break;
            }
        }

        // Ensure the interval mode obtained is always greater or equals than the preferred interval size.
        DateIntervalType intervalSize = null;
        if (!StringUtils.isBlank(columnGroup.getIntervalSize())) {
            intervalSize = getByName(columnGroup.getIntervalSize());
        }
        if (intervalSize != null && compare(intervalType, intervalSize) == -1) {
            intervalType = intervalSize;
        }
        return intervalType;
    }

    protected Calendar firstIntervalDate(DateIntervalType intervalType, Date minDate, ColumnGroup columnGroup) {
        Calendar c = GregorianCalendar.getInstance();
        c.setLenient(false);
        c.setTime(minDate);
        if (YEAR.equals(intervalType)) {
            c.set(Calendar.MONTH, 0);
            c.set(Calendar.DAY_OF_MONTH, 1);
            c.set(Calendar.HOUR, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
        }
        if (QUARTER.equals(intervalType)) {
            int currentMonth = c.get(Calendar.MONTH);
            int firstMonthYear = columnGroup.getFirstMonthOfYear().getIndex();
            int rest = Quarter.getPositionInQuarter(firstMonthYear, currentMonth + 1);
            c.add(Calendar.MONTH, rest * -1);
            c.set(Calendar.DAY_OF_MONTH, 1);
            c.set(Calendar.HOUR, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
        }
        if (MONTH.equals(intervalType)) {
            c.set(Calendar.DAY_OF_MONTH, 1);
            c.set(Calendar.HOUR, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
        }
        if (DAY.equals(intervalType) || DAY_OF_WEEK.equals(intervalType) || WEEK.equals(intervalType)) {
            c.set(Calendar.HOUR, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
        }
        if (HOUR.equals(intervalType)) {
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
        }
        if (MINUTE.equals(intervalType)) {
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
        }
        if (SECOND.equals(intervalType)) {
            c.set(Calendar.MILLISECOND, 0);
        }
        return c;
    }

    protected void nextIntervalDate(Calendar c, DateIntervalType intervalType, int intervals) {

        if (MILLENIUM.equals(intervalType)) {
            c.add(Calendar.YEAR, 1000 * intervals);
        }
        else if (CENTURY.equals(intervalType)) {
            c.add(Calendar.YEAR, 100 * intervals);
        }
        else if (DECADE.equals(intervalType)) {
            c.add(Calendar.YEAR, 10 * intervals);
        }
        else if (YEAR.equals(intervalType)) {
            c.add(Calendar.YEAR, intervals);
        }
        else if (QUARTER.equals(intervalType)) {
            c.add(Calendar.MONTH, 3 * intervals);
        }
        else if (MONTH.equals(intervalType)) {
            c.add(Calendar.MONTH, intervals);
        }
        else if (WEEK.equals(intervalType)) {
            c.add(Calendar.DAY_OF_MONTH, 7 * intervals);
        }
        else if (DAY.equals(intervalType) || DAY_OF_WEEK.equals(intervalType)) {
            c.add(Calendar.DAY_OF_MONTH, intervals);
        }
        else if (HOUR.equals(intervalType)) {
            c.add(Calendar.HOUR_OF_DAY, intervals);
        }
        else if (MINUTE.equals(intervalType)) {
            c.add(Calendar.MINUTE, intervals);
        }
        else if (SECOND.equals(intervalType)) {
            c.add(Calendar.SECOND, intervals);
        }
        else {
            // Default to year to avoid infinite loops
            c.add(Calendar.YEAR, intervals);
        }
    }

    /**
     * A list containing date range intervals.
     */
    public class IntervalDateRangeList extends IntervalList {

        public IntervalDateRangeList(ColumnGroup columnGroup) {
            super(columnGroup);
        }

        public Interval locateInterval(Object value) {
            Date d = (Date) value;
            for (Interval interval : this) {
                IntervalDateRange dateRange = (IntervalDateRange) interval;
                if (d.equals(dateRange.getMinDate()) || (d.after(dateRange.getMinDate()) && d.before(dateRange.getMaxDate()))) {
                    return interval;
                }
            }
            return null;
        }
    }

    /**
     * A date interval holding dates belonging to a given range.
     */
    public class IntervalDateRange extends Interval {

        public IntervalDateRange(int index, DateIntervalType intervalType, Date minDate, Date maxDate) {
            super(calculateName(intervalType, minDate));
            super.setMinValue(minDate);
            super.setMaxValue(maxDate);
            super.setIndex(index);
            super.setType(intervalType != null ? intervalType.toString() : null);
        }

        public Date getMinDate() {
            return (Date) minValue;
        }

        public Date getMaxDate() {
            return (Date) maxValue;
        }
    }

    public static String calculateName(DateIntervalType intervalType, Date d) {
        if (d == null) return null;

        Locale l = Locale.getDefault();
        if (MILLENIUM.equals(intervalType) || CENTURY.equals(intervalType)
                || DECADE.equals(intervalType) || YEAR.equals(intervalType)) {
            SimpleDateFormat format  = new SimpleDateFormat("yyyy", l);
            return format.format(d);
        }
        if (QUARTER.equals(intervalType) || MONTH.equals(intervalType)) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM", l);
            return format.format(d);
        }
        if (WEEK.equals(intervalType) || DAY.equals(intervalType) || DAY_OF_WEEK.equals(intervalType)) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            return format.format(d);
        }
        if (HOUR.equals(intervalType)) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH");
            return format.format(d);
        }
        if (MINUTE.equals(intervalType)) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            return format.format(d);
        }
        if (SECOND.equals(intervalType)) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return format.format(d);
        }
        return format.format(d);
    }
}
