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

import java.util.List;
import org.dashbuilder.dataset.def.DataSetPreprocessor;

/**
 * Main interface for handling data sets.
 */
public interface DataSetManager {

    /**
     * Create a brand new data set instance.
     * @param uuid The UUID to assign to the new data set.
     */
    DataSet createDataSet(String uuid);

    /**
     * Retrieve (load if required) a data set.
     * @param uuid The UUID of the data set.
     * @return null, if the data set can be retrieved.
     */
    DataSet getDataSet(String uuid);

    /**
     * Registers the specified data set instance.
     */
    void registerDataSet(DataSet dataSet);

    /**
     * Registers the specified data set instance with a list of preprocessors
     */
    void registerDataSet(DataSet dataSet, List<DataSetPreprocessor> preprocessors);

    /**
     * Removes the specified data set instance.
     * @param uuid The UUID of the data set.
     * @return The data set removed. null - if the data set does not exists.
     */
    DataSet removeDataSet(String uuid);

    /**
     * Load a data set and apply several operations (filter, sort, group, ...) on top of it.
     * @return null, if the data set can be retrieved.
     */
    DataSet lookupDataSet(DataSetLookup lookup);

    /**
     * Process multiple data set lookup request in a single shot.
     */
    DataSet[] lookupDataSets(DataSetLookup[] lookup);

    /**
     * Retrieves the metadata for a given data set
     * @return A DataSetMetadata instance containing general information about the data set, or null if the data set can be retrieved.
     */
    DataSetMetadata getDataSetMetadata(String uuid);
}
