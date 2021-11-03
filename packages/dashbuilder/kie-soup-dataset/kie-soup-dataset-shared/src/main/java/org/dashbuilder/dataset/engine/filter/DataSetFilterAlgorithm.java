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
package org.dashbuilder.dataset.engine.filter;

import java.util.List;

import org.dashbuilder.dataset.engine.DataSetHandler;
import org.dashbuilder.dataset.filter.ColumnFilter;

/**
 * Data set filter algorithm interface.
 */
public interface DataSetFilterAlgorithm {

    /**
     * Filter the specified data set according the filter criteria.
     *
     * @param ctx The data set context to filter
     * @param columnFilter The column filter to apply.
     * @return A list of ordinals containing only the rows that surpass the filter.
     */
    List<Integer> filter(DataSetHandler ctx, ColumnFilter columnFilter);
}
