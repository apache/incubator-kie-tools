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
package org.dashbuilder.displayer;

import org.dashbuilder.dataset.sort.SortOrder;

/**
 * A displayer settings builder for tables
 *
 * <pre>
 *   DisplayerSettingsFactory.newTableSettings()
 *   .title("List of Opportunities")
 *   .tablePageSize(20)
 *   .tableOrderEnabled(true)
 *   .tableOrderDefault("amount", "desc")
 *   .buildSettings()
 * </pre>
 */
public interface TableDisplayerSettingsBuilder<T extends TableDisplayerSettingsBuilder> extends DisplayerSettingsBuilder<T> {

    /**
     * Sets the page size (i.e. the number of rows per page) for this table displayer.
     * @param pageSize The page size.
     * @return The DisplayerSettingsBuilder instance that is being used to configure a Table data displayer.
     */
    T tablePageSize(int pageSize);

    /**
     * If true, it enables the table columns to be ordered.
     * @param enabled True to enable, false to disable.
     * @return The DisplayerSettingsBuilder instance that is being used to configure a Table data displayer.
     */
    T tableOrderEnabled(boolean enabled);

    /**
     * Set the default ordering column.
     * @param columnId The identifier of the column by which the table should by default be ordered.
     * @param order The sort order to apply on the specified column.
     * @return The DisplayerSettingsBuilder instance that is being used to configure a Table data displayer.
     * @see org.dashbuilder.dataset.sort.SortOrder
     */
    T tableOrderDefault(String columnId, SortOrder order);

    /**
     * Set the default ordering column.
     * @param columnId The identifier of the column by which the table should by default be ordered.
     * @param order The sort order to apply on the specified column, as a String (accepted values are "asc" and "desc".
     * @return The DisplayerSettingsBuilder instance that is being used to configure a Table data displayer.
     */
    T tableOrderDefault(String columnId, String order);

    /**
     * Set the table total width.
     * @param tableWidth The total table width, in pixels.
     * @return The DisplayerSettingsBuilder instance that is being used to configure a Table data displayer.
     */
    T tableWidth(int tableWidth);

    /**
     * If true, it enables to show/hide the table columns by means of a column picker widget.
     * @param enabled True to enable, false to disable.
     * @return The DisplayerSettingsBuilder instance that is being used to configure a Table data displayer.
     */
    T tableColumnPickerEnabled(boolean enabled);
}
