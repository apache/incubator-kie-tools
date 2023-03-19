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

import org.dashbuilder.dataset.date.DayOfWeek;
import org.dashbuilder.dataset.date.Month;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.group.AggregateFunctionType;
import org.dashbuilder.dataset.group.DateIntervalType;
import org.dashbuilder.dataset.sort.SortOrder;

/**
 * A DataSetLookupBuilder allows for the assembly of a DataSetLookup instance (i.e. a DataSet lookup request)
 * in a friendly manner. It allows the issuing of a request to obtain a view over a specific DataSet.
 *
 * <pre>
 *   DataSetFactory.newLookupBuilder()
 *   .dataset("target-dataset-uuid")
 *   .group("department")
 *   .column("id", COUNT, "occurrences")
 *   .column("amount", SUM, "totalAmount")
 *   .sort("totalAmount", "asc")
 *   .buildLookup();
 * </pre>
 *
 */
public interface DataSetLookupBuilder<T> {

    /**
     * The UUID reference to the source data set.
     */
    T dataset(String uuid);

    /**
     * Set a row offset for the data set.
     * @param offset The row offset for the resulting data set (starting at 0).
     * @return The DataSetLookupBuilder instance that is being used to configure a DataSetLookup request.
     */
    T rowOffset(int offset);

    /**
     * Set the number of rows for the data set.
     * @param rows The number of rows for the resulting data set.
     * @return The DataSetLookupBuilder instance that is being used to configure a DataSetLookup request.
     */
    T rowNumber(int rows);

    /**
     * Group the data set by one of the columns
     * @param columnId The column identifier of the column to be grouped
     * @return The DataSetLookupBuilder instance that is being used to configure a DataSetLookup request.
     */
    T group(String columnId);

    /**
     * Group the data set by one of the columns. The resulting group will be given the new column identifier.
     * @param columnId The column identifier
     * @param newColumnId The identifier for the group column
     * @return The DataSetLookupBuilder instance that is being used to configure a DataSetLookup request.
     */
    T group(String columnId, String newColumnId);

    T group(String columnId, String newColumnId, boolean b);

    /**
     * This call will operate only on a previously grouped data set (i.e. one of the group() methods has been called
     * previously on the data set lookup), and it's used to indicate that the group results must be joined with the
     * group results of a previous group operation (if any). Example:
     *
     *  <ul>
     *  <li>.group(PIPELINE)</li>
     *  <li>.group(COUNTRY).join()</li>
     *  <li>.column(PIPELINE)</li>
     *  <li>.column(COUNTRY)</li>
     *  <li>.column(AMOUNT, SUM, "TOTAL")</li>
     *  </ul>
     *
     * <p>Group by PIPELINE:</p>
     * <pre>
     *
     *   --------------------------
     *   | PIPELINE   | TOTAL     |
     *   --------------------------
     *   | EARLY      | 369.09    |
     *   | ADVANCED   | 246.06    |
     *   --------------------------
     * </pre>
     *
     * <p>Group by COUNTRY:</p>
     * <pre>
     *
     *   ------------------------
     *   | COUNTRY  | TOTAL     |
     *   ------------------------
     *   | USA      | 369.09    |
     *   | UK       | 246.06    |
     *   | Spain    | 369.09    |
     *   ------------------------
     * </pre>
     *
     * <p>Result:</p>
     * <pre>
     *
     *   ---------------------------------------
     *   | PIPELINE   |  COUNTRY   | TOTAL     |
     *   ---------------------------------------
     *   | EARLY      |  USA       | 123.03    |
     *   | EARLY      |  UK        | 123.03    |
     *   | EARLY      |  Spain     | 123.03    |
     *   | ADVANCED   |  USA       | 123.03    |
     *   | ADVANCED   |  Spain     | 123.03    |
     *   | STANDBY    |  USA       | 123.03    |
     *   | STANDBY    |  UK        | 123.03    |
     *   | STANDBY    |  Spain     | 123.03    |
     *   ---------------------------------------
     * </pre>
     * <p>A joined data set grouped by PIPELINE/COUNTRY is returned.
     *
     * @return The DataSetLookupBuilder instance that is being used to configure a DataSetLookup request.
     */
    T join();

    /**
     * This call will operate only on a previously grouped data set (i.e. one of the group() methods has been called
     * previously on the data set lookup), and will result in that the grouped column is ordered in ascending order.
     * @return The DataSetLookupBuilder instance that is being used to configure a DataSetLookup request.
     */
    T asc();

    /**
     * This call will operate only on a previously grouped data set (i.e. one of the group() methods has been called
     * previously on the data set lookup), and will result in that the grouped column is ordered in descending order.
     * @return The DataSetLookupBuilder instance that is being used to configure a DataSetLookup request.
     */
    T desc();

    /**
     * Group the data set by one of the columns, of type ColumnType.Date, specifying the size of the date interval
     * by which the column should be grouped. By default the DYNAMIC GroupStrategy will be applied.
     * @see org.dashbuilder.dataset.group.GroupStrategy
     * @param maxIntervals The maximum number of date intervals that should appear on the graph. The DYNAMIC GroupStrategy
     * implies that if, after grouping, more intervals are generated than the specified amount, a 'greater' DateIntervalType
     * will be applied.
     * For example:
     * <pre>
     *   DataSetFactory.newDataSetLookupBuilder()
     *   .dataset(SALES_OPPS)
     *   .group(CLOSING_DATE).dynamic(80, MONTH)
     * </pre>
     * will group the data set by its closing date column, in monthly intervals, up to a maximum 80 months. If this
     * dataset's time-span exceeds this number of months, then the next bigger DateIntervalType (i.e. QUARTER) will be applied.
     * @param intervalSize The size of the date interval.
     * @param emptyAllowed If true then empty intervals will be also considered part of the resulting data set.
     * @see org.dashbuilder.dataset.group.DateIntervalType
     * @return The DataSetLookupBuilder instance that is being used to configure a DataSetLookup request.
     */
    T dynamic(int maxIntervals, DateIntervalType intervalSize, boolean emptyAllowed);

    /**
     * Same as &quot;dynamic(int maxIntervals, DateIntervalType intervalSize)&quot; but in this case the
     * &quot;intervalSize&quot; is dynamically calculated to the minimum size that generates less intervals
     * than the &quot;maxIntervals&quot; specified.
     */
    T dynamic(int maxIntervals, boolean emptyAllowed);

    /**
     * Same as &quot;dynamic(int maxIntervals, DateIntervalType intervalSize)&quot; but taking
     * &quot;maxIntervals=15&quot; as default.
     */
    T dynamic(DateIntervalType intervalSize, boolean emptyAllowed);

    /**
     * Set the grouping strategy to a fixed date interval on a previously defined date group operation.
     *
     * Example:
     * <pre>
     *   DataSetFactory.newDataSetLookupBuilder()
     *   .dataset(SALES_OPPS)
     *   .group(CLOSING_DATE)
     *   .fixed(MONTH).firstMonth(JANUARY)
     * </pre>
     * will group the data set by a column identified by 'CLOSING_DATE', into a fixed monthly interval, starting with
     * January as the first month interval.
     *
     * @param type The size of the date interval. Only the following types are supported: QUARTER, MONTH,
     * DAY_OF_WEEK, HOUR, MINUTE, SECOND
     * @param emptyAllowed If true then empty intervals will be also considered part of the resulting data set.
     * @see org.dashbuilder.dataset.group.DateIntervalType
     * @return The DataSetLookupBuilder instance that is being used to configure a DataSetLookup request.
     */
    T fixed(DateIntervalType type, boolean emptyAllowed);

    /**
     * This call requires a previously grouped data set with fixed DateIntervalType.DAY_OF_WEEK intervals, i.e. both
     * group() and fixed(DateIntervalType.DAY_OF_WEEK) have to be invoked previously. It will indicate the resulting
     * data set that it has to show the specified day of the week as the first interval.
     * @see org.dashbuilder.dataset.group.DateIntervalType
     * @param dayOfWeek The day of the week that should be shown as the graph's first interval.
     * @return The DataSetLookupBuilder instance that is being used to configure a DataSetLookup request.
     */
    T firstDay(DayOfWeek dayOfWeek);

    /**
     * This call requires a previously grouped data set with fixed DateIntervalType.MONTH intervals, i.e. both
     * group() and fixed(DateIntervalType.MONTH) have to be invoked previously. It will indicate the resulting
     * data set that it has to show the specified month as the first interval.
     * @see org.dashbuilder.dataset.group.DateIntervalType
     * @param month The month that should be shown as the graph's first interval.
     * @return The DataSetLookupBuilder instance that is being used to configure a DataSetLookup request.
     */
    T firstMonth(Month month);

    /**
     * The function will reduce the generated data set by selecting some of the intervals that were previously generated
     * through a group operation.
     *
     * For example:
     * <pre>
     *   DataSetFactory.newDataSetLookupBuilder()
     *   .dataset(EXPENSE_REPORTS)
     *   .group("department", "Department")
     *   .select("Services", "Engineering", "Support")
     *   .count( "Occurrences" )
     *   .buildLookup());
     * </pre>
     * Will group the expense reports data set by department, select only the "Services", "Engineering" and "Support"
     * intervals, and count how many times each of those occurs respectively.
     * @param intervalNames The interval names that should be preserved in the data set that is being generated.
     * @return The DataSetLookupBuilder instance that is being used to configure a DataSetLookup request.
     */
    T select(String... intervalNames);

    /**
     * Filter the data set according to the specified column filters. All column filters will need to explicitly reference
     * the column (through its corresponding identifier) that they need to be applied on.
     *
     * For example:
     * <pre>
     *   DataSetFactory.newDataSetLookupBuilder()
     *   .dataset(EXPENSE_REPORTS)
     *   .filter(AND(
     *               equalsTo("department", "Sales"),
     *               OR(
     *                 NOT(lowerThan("amount", 300)),
     *                 equalsTo("city", "Madrid")
     *               )
     *           )
     *   )
     *   .buildLookup());
     * </pre>
     * Will limit the expense reports data set such that for all obtained records, the department will always equal "Sales",
     * and either the amount will not be lower than 300, or the city will be equal to "Madrid".
     * @see org.dashbuilder.dataset.filter.ColumnFilter
     * @see org.dashbuilder.dataset.filter.FilterFactory
     * @param filter The filters to be applied on the data set's column
     * @return The DataSetLookupBuilder instance that is being used to configure a DataSetLookup request.
     */
    T filter(ColumnFilter... filter);

    /**
     * Filter the data set according to the specified column filters. The specified column identifier allows to specify
     * column filters without explicitly passing in the column identifier with them, they will simply 'inherit'.
     *
     * For example:
     * <pre>
     *   DataSetFactory.newDataSetLookupBuilder()
     *   .dataset(EXPENSE_REPORTS)
     *   .filter("amount",
     *           AND(
     *               equalsTo("department", "Sales"),
     *               OR(
     *                 NOT(lowerThan(300)),
     *                 equalsTo("city", "Madrid")
     *               )
     *           )
     *   )
     *   .buildLookup());
     * </pre>
     * Will limit the expense reports data set such that for all obtained records, the department will always equal "Sales",
     * and either the amount will not be lower than 300, or the city will be equal to "Madrid". Since the lowerThan filter
     * does not reference a column, it implicitly refers to the amount column.
     * @see org.dashbuilder.dataset.filter.ColumnFilter
     * @see org.dashbuilder.dataset.filter.FilterFactory
     * @param columnId The identifier of the column that the filter array should be applied on
     * @param filter The filters to be applied on the data set's column
     * @return The DataSetLookupBuilder instance that is being used to configure a DataSetLookup request.
     */
    T filter(String columnId, ColumnFilter... filter);

    /**
     * Will apply the specified sort order over the indicated data set column.
     * @param columnId The identifier of the column that should be sorted.
     * @param order The sort order, specified as a String. Accepted values are "asc" and "desc".
     * @return The DataSetLookupBuilder instance that is being used to configure a DataSetLookup request.
     */
    T sort(String columnId, String order);

    /**
     * Will apply the specified sort order over the indicated data set column.
     * @param columnId The identifier of the column that should be sorted.
     * @param order The sort order.
     * @see org.dashbuilder.dataset.sort.SortOrder
     * @return The DataSetLookupBuilder instance that is being used to configure a DataSetLookup request.
     */
    T sort(String columnId, SortOrder order);

    /**
     * Select the specified column as part of the resulting data set.
     *
     * @param columnId The identifier of the source column.
     * @return The DisplayerSettingsBuilder instance that is being used to configure a DisplayerSettings.
     */
    T column(String columnId);

    /**
     * Select the specified column as part of the resulting data set.
     *
     * @param columnId The identifier of the source column.
     * @param newColumnId A new identifier for the column into the resulting data set.
     * @return The DisplayerSettingsBuilder instance that is being used to configure a DisplayerSettings.
     */
    T column(String columnId, String newColumnId);

    /**
     * Generates a new column on the resulting data set by which values will be the result of applying the specified
     * aggregation function on the source data set column.
     *
     * @param columnId The identifier of the source column.
     * @param function The AggregationFunction for calculating the column values.
     * @return The DisplayerSettingsBuilder instance that is being used to configure a DisplayerSettings.
     */
    T column(String columnId, AggregateFunctionType function);

    /**
     * Generates a new column on the resulting data set by which values will be the result of applying the specified
     * aggregation function on the source data set column.
     * <p>This variant requires a previous group() method invocation as it's not possible to apply aggregation
     * functions on non-grouped data sets.
     *
     * @param columnId The identifier of the source column.
     * @param function The aggregation function for calculating the column values.
     * @param newColumnId A new identifier for the column into the resulting data set.
     * @return The DisplayerSettingsBuilder instance that is being used to configure a DisplayerSettings.
     */
    T column(String columnId, AggregateFunctionType function, String newColumnId);

    /**
     * Generates a new column on the resulting data set by which values will be the result of applying the specified
     * aggregation function on the source data set column.
     * <p>This variant does not require a source column which is fine for some aggregation functions like
     * AggregateFunctionType.COUNT</p>
     *
     * @param function The aggregation function for calculating the column values.
     * @param newColumnId A new identifier for the column into the resulting data set.
     * @return The DisplayerSettingsBuilder instance that is being used to configure a DisplayerSettings.
     */
    T column(AggregateFunctionType function, String newColumnId);

    /**
     * @return The DataSetLookup request instance that has been configured.
     */
    DataSetLookup buildLookup();

}
