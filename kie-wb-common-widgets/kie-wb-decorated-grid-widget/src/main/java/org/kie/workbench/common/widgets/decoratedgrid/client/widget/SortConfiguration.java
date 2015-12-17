/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.widgets.decoratedgrid.client.widget;

/**
 * Container for sort information. Encapsulated in a single class to avoid the
 * need for multiple ValueChangeHandlers for all attributes affecting sorting on
 * a Column.
 */
public class SortConfiguration {

    private SortDirection sortDirection = SortDirection.NONE;
    private Boolean       isSortable    = true;
    private int           columnIndex   = 0;
    private int           sortIndex     = -1;

    public int getColumnIndex() {
        return columnIndex;
    }

    public SortDirection getSortDirection() {
        return sortDirection;
    }

    public int getSortIndex() {
        return sortIndex;
    }

    public Boolean isSortable() {
        return isSortable;
    }

    public void setColumnIndex( int columnIndex ) {
        this.columnIndex = columnIndex;
    }

    public void setSortable( Boolean isSortable ) {
        this.isSortable = isSortable;
    }

    public void setSortDirection( SortDirection sortDirection ) {
        this.sortDirection = sortDirection;
    }

    public void setSortIndex( int sortIndex ) {
        this.sortIndex = sortIndex;
    }

}
