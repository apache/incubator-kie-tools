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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.GridColumnRenderer;

/**
 * Base implementation of a grid column to avoid boiler-plate for more specific implementations.
 */
public class BaseGridColumn<T> implements GridColumn<T> {

    //Default minimum width of a column.
    private static final double COLUMN_MIN_WIDTH = 100;

    private double width;
    private boolean isResizable = true;
    private boolean isMovable = true;
    private boolean isFloatable = false;
    private boolean isVisible = true;
    private Double minimumWidth = COLUMN_MIN_WIDTH;
    private Double maximumWidth = null;
    private GridColumn<?> link;
    private int index = -1;
    private List<HeaderMetaData> headerMetaData = new ArrayList<HeaderMetaData>();
    private GridColumnRenderer<T> columnRenderer;
    private ColumnWidthMode columnWidthMode = ColumnWidthMode.FIXED;

    protected String placeHolder = null;

    public BaseGridColumn(final HeaderMetaData headerMetaData,
                          final GridColumnRenderer<T> columnRenderer,
                          final double width) {
        Objects.requireNonNull(headerMetaData, "headerMetaData");
        Objects.requireNonNull(columnRenderer, "columnRenderer");
        this.headerMetaData.add(headerMetaData);
        this.columnRenderer = columnRenderer;
        this.width = width;
    }

    public BaseGridColumn(final List<HeaderMetaData> headerMetaData,
                          final GridColumnRenderer<T> columnRenderer,
                          final double width) {
        Objects.requireNonNull(headerMetaData, "headerMetaData");
        Objects.requireNonNull(columnRenderer, "columnRenderer");
        this.headerMetaData.addAll(headerMetaData);
        this.columnRenderer = columnRenderer;
        this.width = width;
    }

    public BaseGridColumn(final HeaderMetaData headerMetaData,
                          final GridColumnRenderer<T> columnRenderer,
                          final double width, final String placeHolder) {
        this(headerMetaData, columnRenderer, width);
        this.placeHolder = placeHolder;
    }

    public BaseGridColumn(final List<HeaderMetaData> headerMetaData,
                          final GridColumnRenderer<T> columnRenderer,
                          final double width, final String placeHolder) {
        this(headerMetaData, columnRenderer, width);
        this.placeHolder = placeHolder;
    }

    @Override
    public List<HeaderMetaData> getHeaderMetaData() {
        return headerMetaData;
    }

    @Override
    public GridColumnRenderer<T> getColumnRenderer() {
        return columnRenderer;
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public void setWidth(final double width) {
        this.width = width;
    }

    @Override
    public boolean isLinked() {
        return link != null;
    }

    @Override
    public GridColumn<?> getLink() {
        return link;
    }

    @Override
    public void setLink(final GridColumn<?> link) {
        this.link = link;
    }

    @Override
    public int getIndex() {
        if (index == -1) {
            throw new IllegalStateException("Column has not been added to a Grid and hence has no index.");
        }
        return index;
    }

    @Override
    public void setIndex(final int index) {
        this.index = index;
    }

    @Override
    public boolean isResizable() {
        return isResizable;
    }

    @Override
    public void setResizable(final boolean isResizable) {
        this.isResizable = isResizable;
    }

    @Override
    public boolean isMovable() {
        return this.isMovable;
    }

    @Override
    public void setMovable(final boolean isMovable) {
        this.isMovable = isMovable;
    }

    @Override
    public boolean isFloatable() {
        return isFloatable;
    }

    @Override
    public void setFloatable(final boolean isFloatable) {
        this.isFloatable = isFloatable;
    }

    @Override
    public boolean isVisible() {
        return isVisible;
    }

    @Override
    public void setVisible(final boolean isVisible) {
        this.isVisible = isVisible;
    }

    @Override
    public Double getMinimumWidth() {
        return minimumWidth;
    }

    @Override
    public void setMinimumWidth(final Double minimumWidth) {
        this.minimumWidth = minimumWidth;
    }

    @Override
    public Double getMaximumWidth() {
        return maximumWidth;
    }

    @Override
    public void setMaximumWidth(final Double maximumWidth) {
        this.maximumWidth = maximumWidth;
    }

    public String getPlaceHolder() {
        return placeHolder;
    }

    public void setPlaceHolder(String placeHolder) {
        this.placeHolder = placeHolder;
    }

    @Override
    public ColumnWidthMode getColumnWidthMode() {
        return columnWidthMode;
    }

    @Override
    public void setColumnWidthMode(ColumnWidthMode columnWidthMode) {
        this.columnWidthMode = columnWidthMode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BaseGridColumn)) {
            return false;
        }

        BaseGridColumn that = (BaseGridColumn) o;

        if (Double.compare(that.width,
                           width) != 0) {
            return false;
        }
        if (isResizable != that.isResizable) {
            return false;
        }
        if (isMovable != that.isMovable) {
            return false;
        }
        if (isVisible != that.isVisible) {
            return false;
        }
        if (index != that.index) {
            return false;
        }
        if (minimumWidth != null ? !minimumWidth.equals(that.minimumWidth) : that.minimumWidth != null) {
            return false;
        }
        if (maximumWidth != null ? !maximumWidth.equals(that.maximumWidth) : that.maximumWidth != null) {
            return false;
        }
        if (link != null ? !link.equals(that.link) : that.link != null) {
            return false;
        }
        return getHeaderMetaData().equals(that.getHeaderMetaData());
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(width);
        result = (int) (temp ^ (temp >>> 32));
        result = ~~result;
        result = 31 * result + (isResizable ? 1 : 0);
        result = ~~result;
        result = 31 * result + (isMovable ? 1 : 0);
        result = ~~result;
        result = 31 * result + (isVisible ? 1 : 0);
        result = ~~result;
        result = 31 * result + (minimumWidth != null ? minimumWidth.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (maximumWidth != null ? maximumWidth.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (link != null ? link.hashCode() : 0);
        result = ~~result;
        result = 31 * result + index;
        result = ~~result;
        result = 31 * result + getHeaderMetaData().hashCode();
        result = ~~result;
        return result;
    }
}
