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
package org.dashbuilder.dataset.engine.sort;

import java.util.List;

import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.sort.ColumnSort;

/**
 * Data set sort algorithm interface.
 */
public interface DataSetSortAlgorithm {

    /**
     * Sort the specified data set according the sort criteria list specified.
     * @param dataSet The data set to sort
     * @param columnSortList The sort operations to apply
     * @return A list of ordered row numbers reflecting the sort results.
     */
    List<Integer> sort(DataSet dataSet, List<ColumnSort> columnSortList);

    /**
     * Sort the specified data set according the sort criteria list specified.
     * @param dataSet The data set to sort
     * @param rowNumbers The subset of rows to sort.
     * @param columnSortList The sort operations to apply
     * @return A list of ordered row numbers reflecting the sort results.
     */
    List<Integer> sort(DataSet dataSet, List<Integer> rowNumbers, List<ColumnSort> columnSortList);
}
