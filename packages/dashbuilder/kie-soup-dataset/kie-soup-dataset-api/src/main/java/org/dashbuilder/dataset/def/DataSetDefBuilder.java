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
package org.dashbuilder.dataset.def;

import org.dashbuilder.dataset.ColumnType;

/**
 * Interface for the assembly of a DataSetDef instance in a friendly manner.
 *
 * <pre>
 *   DataSetDef providerSettings = DataSetDefFactory.newSQLDataSetDef()
 *     .uuid("all_employees")
 *     .datasource("jndi/mydatasource")
 *     .query("SELECT * FROM employee")
 *     .label("id")
 *     .build();
 * </pre>
 *
 * @see DataSetDef
 */
public interface DataSetDefBuilder<T> {

    /**
     * Set the DataSetDef UUID.
     *
     * @param uuid The UUID of the DataSetDef that is being assembled.
     * @return The DataSetDefBuilder instance that is being used to configure a DataSetDef.
     */
    T uuid(String uuid);

    /**
     * Set the DataSetDef name.
     *
     * @param name The name of the DataSetDef that is being assembled.
     * @return The DataSetDefBuilder instance that is being used to configure a DataSetDef.
     */
    T name(String name);

    /**
     * @return The DataSetDef instance that has been configured.
     * @see DataSetDef
     */
    DataSetDef buildDef();

    // Push settings

    /**
     * Enable the ability to push remote data sets from server.
     *
     * @param pushMaxSize The maximum size (in kbytes) a data set may have in order to be pushed to clients.
     * @return The DataSetDefBuilder instance that is being used to configure a DataSetDef.
     */
    T pushOn(int pushMaxSize);

    /**
     * Disable the ability to push remote data sets from server.
     *
     * @return The DataSetDefBuilder instance that is being used to configure a DataSetDef.
     */
    T pushOff();

    // Cache settings

    /**
     * Enables the cache for this data set
     *
     * @param maxRowsInCache Max. rows the cache is able to handle. For higher values the cache is automatically disabled.
     * @return The DataSetDefBuilder instance that is being used to configure a DataSetDef.
     */
    T cacheOn(int maxRowsInCache);

    /**
     * Disables the cache
     *
     * @return The DataSetDefBuilder instance that is being used to configure a DataSetDef.
     */
    T cacheOff();

    // Refresh settings

    /**
     * Turns on the data set refresh mechanism. Every time the refresh time is met the data set will be forced to refresh.
     * Any cache on the data set will be considered stale from that time on.
     *
     * @param refreshTime The amount of time between refresh intervals {@link org.dashbuilder.dataset.date.TimeAmount}
     * @param refreshAlways If false then the refresh will be only performed when the underlying data provider determines
     * that the data set has become stale. Otherwise the data set is always refreshed.
     * data set is outdated. This can be very useful to avoid refreshing unnecessarily.
     * @return The DataSetDefBuilder instance that is being used to configure a DataSetDef.
     */
    T refreshOn(String refreshTime, boolean refreshAlways);

    /**
     * Turns of the data set refresh mechanism.
     *
     * @return The DataSetDefBuilder instance that is being used to configure a DataSetDef.
     */
    T refreshOff();

    // Data set structure settings

    /**
     * Add an empty column of type label.
     */
    T label(String columnId);

    /**
     * Add an empty column of type text.
     */
    T text(String columnId);

    /**
     * Add an empty column of numeric type.
     *
     * @return The DataSetDefBuilder instance that is being used to configure a DataSetDef.
     */
    T number(String columnId);

    /**
     * Add an empty column of type date.
     *
     * @return The DataSetDefBuilder instance that is being used to configure a DataSetDef.
     */
    T date(String columnId);

    /**
     * Add an empty column of the specified type.
     *
     * @return The DataSetDefBuilder instance that is being used to configure a DataSetDef.
     */
    T column(String columnId, ColumnType type);

}
