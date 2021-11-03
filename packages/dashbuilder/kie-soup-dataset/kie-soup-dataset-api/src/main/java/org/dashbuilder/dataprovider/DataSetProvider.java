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
package org.dashbuilder.dataprovider;

import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataset.def.DataSetDef;

/**
 * This service provider interface is designed to provide access to different data set storage implementations with
 * the main goal of providing a unified interface for the data set fetch & lookup operations.
 */
public interface DataSetProvider {

    /**
     * The type of the provider.
     */
    DataSetProviderType getType();

    /**
     * Retrieves the metadata for a given data set definition
     *
     * @param def The data set definition lookup request
     * @return The data set metadata
     */
    DataSetMetadata getDataSetMetadata(DataSetDef def) throws Exception;

    /**
     * Fetch a data set and optionally apply several operations (filter, sort, group, ...) on top of it.
     *
     * <p>For group operations on date columns the grouped column values must stick to the following format:</p>
     *
     * <pre>
     * GroupStrategy   IntervalType       Output
     * ---------------------------------------------------
     *   DYNAMIC         SECOND        yyyy-MM-dd HH:mm:ss
     *   DYNAMIC         MINUTE        yyyy-MM-dd HH:mm
     *   DYNAMIC         HOUR          yyyy-MM-dd HH
     *   DYNAMIC         DAY           yyyy-MM-dd
     *   DYNAMIC         MONTH         yyyy-MM
     *   DYNAMIC         YEAR          yyyy
     *
     *   yyyy=year                 HH=hour from 00 to 23,
     *   MM=month from 01 to 12    mm=minutes from 00 to 59
     *   dd=day from 01 to 31      ss=seconds from 00 to 59
     *
     *   FIXED           SECOND        From 0 to 59
     *   FIXED           MINUTE        From 0 to 59
     *   FIXED           HOUR          From 0 to 23
     *   FIXED           DAY_OF_WEEK   From 1 to 7 (1=Sunday, 7=Saturday)
     *   FIXED           MONTH         From 1 to 12 (1=January, 12=December)
     *   FIXED           QUARTER       From 1 to 4 (1=Ene,Feb,Mar, 4=Oct,Nov,Dec)
     * </pre>
     *
     * @param def The data set definition lookup request
     * @param lookup The lookup request over the data set. If null then return the data set as is.
     * @return The resulting data set instance
     */
    DataSet lookupDataSet(DataSetDef def, DataSetLookup lookup) throws Exception;

    /**
     * Check if the specified data set definition is outdated. This means that the data set might have been updated at
     * origin. When this happens, any data hold by the provider can be considered stale and a refresh is needed.
     *
     * @param def The data set definition to check for
     * @return true if the data set has become stale. false otherwise.
     */
    boolean isDataSetOutdated(DataSetDef def);
}
