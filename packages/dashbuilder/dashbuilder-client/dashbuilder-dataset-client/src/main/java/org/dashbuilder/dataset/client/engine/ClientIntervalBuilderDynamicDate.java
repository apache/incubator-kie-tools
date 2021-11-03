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
package org.dashbuilder.dataset.client.engine;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.DataSet;
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
@ApplicationScoped
public class ClientIntervalBuilderDynamicDate implements IntervalBuilder {

    private ClientDateFormatter dateFormatter;

    public ClientIntervalBuilderDynamicDate() {
    }

    @Inject
    public ClientIntervalBuilderDynamicDate(ClientDateFormatter dateFormatter) {
        this.dateFormatter = dateFormatter;
    }

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

        // Get the lower & upper limits.
        SortedList sortedValues = new SortedList(values, sortedRows);
        Date minDate = null;
        Date maxDate = null;
        for (int i = 0; minDate == null && i < sortedValues.size(); i++) {
            minDate = (Date) sortedValues.get(i);
        }
        for (int i = sortedValues.size()-1; maxDate == null && i >= 0; i--) {
            maxDate = (Date) sortedValues.get(i);
        }

        // If min/max are equals then create a single interval.
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
        Date intervalMinDate = firstIntervalDate(intervalType, minDate, columnGroup);
        int index = 0;
        int counter = 0;
        while (intervalMinDate.compareTo(maxDate) <= 0) {

            // Go to the next interval
            Date intervalMaxDate = nextIntervalDate(intervalMinDate, intervalType, 1);

            // Create the interval.
            IntervalDateRange interval = new IntervalDateRange(counter++, intervalType, intervalMinDate, intervalMaxDate);
            results.add(interval);

            // Add the target rows to the interval.
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
            // Move to the next interval.
            intervalMinDate = intervalMaxDate;
        }

        // Reverse intervals if requested
        boolean asc = columnGroup.isAscendingOrder();
        if (!asc) Collections.reverse( results );

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
        Date intervalMinDate = firstIntervalDate(intervalType, minDate, columnGroup);
        int counter = 0;
        while (intervalMinDate.compareTo(maxDate) <= 0) {

            // Go to the next interval
            Date intervalMaxDate = nextIntervalDate(intervalMinDate, intervalType, 1);

            // Create the interval.
            IntervalDateRange interval = new IntervalDateRange(counter++, intervalType, intervalMinDate, intervalMaxDate);
            results.add(interval);

            // Move to the next interval.
            intervalMinDate = intervalMaxDate;
        }

        // Reverse intervals if requested
        boolean asc = columnGroup.isAscendingOrder();
        if (!asc) Collections.reverse( results );

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
        String preferredSize = columnGroup.getIntervalSize();
        if (preferredSize != null && preferredSize.trim().length() > 0) {
            intervalSize = getByName(columnGroup.getIntervalSize());
        }
        if (intervalSize != null && compare(intervalType, intervalSize) == -1) {
            intervalType = intervalSize;
        }
        return intervalType;
    }

    protected Date firstIntervalDate(DateIntervalType intervalType, Date minDate, ColumnGroup columnGroup) {
        Date intervalMinDate = new Date(minDate.getTime());
        if (YEAR.equals(intervalType)) {
            intervalMinDate.setMonth(0);
            intervalMinDate.setDate(1);
            intervalMinDate.setHours(0);
            intervalMinDate.setMinutes(0);
            intervalMinDate.setSeconds(0);
        }
        if (QUARTER.equals(intervalType)) {
            int currentMonth = intervalMinDate.getMonth();
            int firstMonthYear = columnGroup.getFirstMonthOfYear().getIndex();
            int rest = Quarter.getPositionInQuarter(firstMonthYear, currentMonth + 1);
            intervalMinDate.setMonth(currentMonth - rest);
            intervalMinDate.setDate(1);
            intervalMinDate.setHours(0);
            intervalMinDate.setMinutes(0);
            intervalMinDate.setSeconds(0);
        }
        if (MONTH.equals(intervalType)) {
            intervalMinDate.setDate(1);
            intervalMinDate.setHours(0);
            intervalMinDate.setMinutes(0);
            intervalMinDate.setSeconds(0);
        }
        if (DAY.equals(intervalType) || DAY_OF_WEEK.equals(intervalType) || WEEK.equals(intervalType)) {
            intervalMinDate.setHours(0);
            intervalMinDate.setMinutes(0);
            intervalMinDate.setSeconds(0);
        }
        if (HOUR.equals(intervalType)) {
            intervalMinDate.setMinutes(0);
            intervalMinDate.setSeconds(0);
        }
        if (MINUTE.equals(intervalType)) {
            intervalMinDate.setSeconds(0);
        }
        return intervalMinDate;
    }

    protected Date nextIntervalDate(Date intervalMinDate, DateIntervalType intervalType, int intervals) {
        Date intervalMaxDate = new Date(intervalMinDate.getTime());

        if (MILLENIUM.equals(intervalType)) {
            intervalMaxDate.setYear(intervalMinDate.getYear() + 1000 * intervals);
        }
        else if (CENTURY.equals(intervalType)) {
            intervalMaxDate.setYear(intervalMinDate.getYear() + 100 * intervals);
        }
        else if (DECADE.equals(intervalType)) {
            intervalMaxDate.setYear(intervalMinDate.getYear() + 10 * intervals);
        }
        else if (YEAR.equals(intervalType)) {
            intervalMaxDate.setYear(intervalMinDate.getYear() +  intervals);
        }
        else if (QUARTER.equals(intervalType)) {
            intervalMaxDate.setMonth(intervalMinDate.getMonth() + 3 * intervals);
        }
        else if (MONTH.equals(intervalType)) {
            intervalMaxDate.setMonth(intervalMinDate.getMonth() + intervals);
        }
        else if (WEEK.equals(intervalType)) {
            intervalMaxDate.setDate(intervalMinDate.getDate() + 7 * intervals);
        }
        else if (DAY.equals(intervalType) || DAY_OF_WEEK.equals(intervalType)) {
            intervalMaxDate.setDate(intervalMinDate.getDate() + intervals);
        }
        else if (HOUR.equals(intervalType)) {
            intervalMaxDate.setHours(intervalMinDate.getHours() + intervals);
        }
        else if (MINUTE.equals(intervalType)) {
            intervalMaxDate.setMinutes(intervalMinDate.getMinutes() + intervals);
        }
        else if (SECOND.equals(intervalType)) {
            intervalMaxDate.setSeconds(intervalMinDate.getSeconds() + intervals);
        }
        else {
            // Default to year to avoid infinite loops
            intervalMaxDate.setYear(intervalMinDate.getYear() + intervals);
        }
        return intervalMaxDate;
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

    public String calculateName(DateIntervalType intervalType, Date d) {
        if (MILLENIUM.equals(intervalType) || CENTURY.equals(intervalType)
                || DECADE.equals(intervalType) || YEAR.equals(intervalType)) {
            return dateFormatter.format(d, "yyyy");
        }
        if (QUARTER.equals(intervalType) || MONTH.equals(intervalType)) {
            return dateFormatter.format(d, "yyyy-MM");
        }
        if (WEEK.equals(intervalType) || DAY.equals(intervalType) || DAY_OF_WEEK.equals(intervalType)) {
            return dateFormatter.format(d, "yyyy-MM-dd");
        }
        if (HOUR.equals(intervalType)) {
            return dateFormatter.format(d, "yyyy-MM-dd HH");
        }
        if (MINUTE.equals(intervalType)) {
            return dateFormatter.format(d, "yyyy-MM-dd HH:mm");
        }
        if (SECOND.equals(intervalType)) {
            return dateFormatter.format(d, "yyyy-MM-dd HH:mm:ss");
        }
        return null;
    }
}
