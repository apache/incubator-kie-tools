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
package org.dashbuilder.displayer.client;

import java.util.Map;

import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.group.DataSetGroup;
import org.dashbuilder.dataset.group.Interval;
import org.dashbuilder.dataset.sort.SortOrder;
import org.dashbuilder.displayer.client.export.ExportCallback;
import org.dashbuilder.displayer.client.export.ExportFormat;

/**
 * Interface addressed to issue lookup requests over a data set instance.
 */
public interface DataSetHandler {

    /**
     * Retrieves any group operation present in the current data set lookup for the target column specified.
     * @param columnId The column id. to look for.
     *
     * @return The group operation that matches the given column id. Or null if no operation is found.
     */
    DataSetGroup getGroupOperation(String columnId);

    /**
     * Get the interval at the given row for the column specified.
     *
     * <p>In case of grouped data sets, the interval may contain information
     * related to the group operation. For instance, for a data set grouped
     * by month, will return an interval containing the min/max dates of such month.</p>
     *
     * <p>For non-grouped or grouped by label data sets, will
     * return only an interval with the value of the row/column selected.</p>
     *
     * The interval information is useful for filtering purposes as the data provider needs
     * all the information related to the selected interval.
     *
     * @param columnId The column id.
     * @param row The row which interval we want to retrieve.
     * @return An interval with information related to the target row/column. Or null, if
     * <ul>
     *     <li>the column does not exist,</li>
     *     <li>the row index is out of bounds,</li>
     *     <li>or the value is null.</li>
     * </ul>
     */
    Interval getInterval(String columnId, int row);

    /**
     * Forces the underlying data set to be updated according the group interval selection filter.
     *
     * @param op The group interval selection operation to apply <i>op.getSelectedIntervalNames()</i> MUST NOT BE EMPTY.
     * @return false, if the target interval selection has already been applied - true, otherwise.
     */
    boolean filter(DataSetGroup op);

    /**
     * Reverts the changes applied by a previous <i>filter</i> operation.
     *
     * @param op The operation to remove.
     * @return false, if no filter has been applied for the target operation - true, otherwise.
     */
    boolean unfilter(DataSetGroup op);

    /**
     * Forces the underlying data set to be updated according the specified filter.
     *
     * @param op The filter operation to apply.
     * @return false, if the filter requested has already been applied - true, otherwise.
     */
    boolean filter(DataSetFilter op);

    /**
     * Reverts the changes applied by a previous <i>filter</i> operation.
     *
     * @param op The operation to remove.
     * @return false, if no filter has been applied for the target operation - true, otherwise.
     */
    boolean unfilter(DataSetFilter op);

    /**
     * Applies the specified group interval selection operation over the existing group op.
     *
     * @param op The group interval selection operation to apply <i>op.getSelectedIntervalNames()</i> MUST NOT BE EMPTY.
     * @return false, if drillDown is not applicable for the target operation - true, otherwise.
     */
    boolean drillDown(DataSetGroup op);

    /**
     * Reverts the changes applied by a previous <i>drillDown</i> operation.
     *
     * @param op The operation to remove.
     * @return false, if no drillDown has been applied for the target operation - true, otherwise.
     */
    boolean drillUp(DataSetGroup op);

    /**
     * Set the sort order operation to apply to the data set.
     *
     * @param columnId The name of the column to sort.
     * @param sortOrder The sort order.
     */
    void sort(String columnId, SortOrder sortOrder);

    /**
     * Forces the next data set lookup request to retrieve only the specified row sub set.
     *
     * @param offset The position where the row sub set starts.
     * @param rows The number of rows to get.
     */
    void limitDataSetRows(int offset, int rows);

    /**
     * Restore the current data set lookup instance to its base status.
     */
    void resetAllOperations();

    /**
     * Executes the current data set lookup request configured within this handler.
     *
     * @param callback The callback interface that is invoked right after the data is available.
     */
    void lookupDataSet(DataSetReadyCallback callback) throws Exception;

    /**
     * Get the data set get on the last lookup call (if any)
     */
    DataSet getLastDataSet();

    /**
     * Get the current data set lookup (if any)
     */
    DataSetLookup getCurrentDataSetLookup();

    /**
     * Export the current data set to a file in the specified output format.
     *
     * @param format The output format
     * @param maxRows Max rows to be exported.
     * @param callback The callback instance to be notified
     * @param columnNameMap A map containing the column header names for every column in the data set lookup
     */
    void exportCurrentDataSetLookup(ExportFormat format, int maxRows, ExportCallback callback, Map<String,String> columnNameMap);
}