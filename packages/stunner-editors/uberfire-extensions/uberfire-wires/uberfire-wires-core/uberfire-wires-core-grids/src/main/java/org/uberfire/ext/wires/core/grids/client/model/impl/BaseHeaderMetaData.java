/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.ext.wires.core.grids.client.model.impl;

import java.util.Objects;

import org.uberfire.ext.wires.core.grids.client.model.GridColumn;

public class BaseHeaderMetaData implements GridColumn.HeaderMetaData {

    private static final String DEFAULT_COLUMN_GROUP = "";

    private String columnTitle;
    private String columnGroup;

    public BaseHeaderMetaData(final String columnTitle) {
        this(columnTitle,
             DEFAULT_COLUMN_GROUP);
    }

    public BaseHeaderMetaData(final String columnTitle,
                              final String columnGroup) {
        this.columnTitle = Objects.requireNonNull(columnTitle, "columnTitle");
        this.columnGroup = Objects.requireNonNull(columnGroup, "columnGroup");
    }

    @Override
    public String getTitle() {
        return this.columnTitle;
    }

    @Override
    public void setTitle(final String columnTitle) {
        this.columnTitle = columnTitle;
    }

    @Override
    public String getColumnGroup() {
        return this.columnGroup;
    }

    @Override
    public void setColumnGroup(final String columnGroup) {
        this.columnGroup = columnGroup;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BaseHeaderMetaData)) {
            return false;
        }

        BaseHeaderMetaData that = (BaseHeaderMetaData) o;

        if (!columnTitle.equals(that.columnTitle)) {
            return false;
        }
        return columnGroup.equals(that.columnGroup);
    }

    @Override
    public int hashCode() {
        int result = columnTitle.hashCode();
        result = ~~result;
        result = 31 * result + columnGroup.hashCode();
        result = ~~result;
        return result;
    }
}
