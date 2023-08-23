/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.widgets.grid.model;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseGrid;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.dom.HasDOMElementResources;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.GridColumnRenderer;

public abstract class DMNGridColumn<G extends BaseGrid<? extends Expression>, T> extends BaseGridColumn<T> implements HasDOMElementResources {

    public static final double DEFAULT_WIDTH = 100.0;

    protected final G gridWidget;

    protected DMNGridColumn(final HeaderMetaData headerMetaData,
                            final GridColumnRenderer<T> columnRenderer,
                            final double width,
                            final G gridWidget) {
        this(Stream.of(headerMetaData).collect(Collectors.toList()),
             columnRenderer,
             width,
             gridWidget);
    }

    protected DMNGridColumn(final List<HeaderMetaData> headerMetaData,
                            final GridColumnRenderer<T> columnRenderer,
                            final double width,
                            final G gridWidget) {
        super(headerMetaData,
              columnRenderer,
              width);
        this.gridWidget = gridWidget;
    }

    public G getGridWidget() {
        return gridWidget;
    }

    @Override
    public void setWidth(final double width) {
        setComponentWidth(width);
        super.setWidth(width);
    }

    public void setWidthInternal(final double width) {
        setComponentWidth(width);
        super.setWidth(width);
    }

    protected void setComponentWidth(final double width) {
        gridWidget.getExpression().get().ifPresent(e -> {
            final int index = gridWidget.getModel().getColumns().indexOf(DMNGridColumn.this);
            e.getComponentWidths().set(index, width);
        });
    }

    public void updateWidthOfPeers() {
        if (gridWidget instanceof BaseExpressionGrid) {
            final BaseExpressionGrid beg = (BaseExpressionGrid) gridWidget;
            final int parentColumnIndex = beg.getParentInformation().getColumnIndex();
            final GridData parentGridData = beg.getParentInformation().getGridWidget().getModel();
            if (parentGridData != null && parentColumnIndex < parentGridData.getColumnCount()) {
                final GridColumn<?> parentColumn = parentGridData.getColumns().get(parentColumnIndex);
                parentColumn.setWidth(beg.getWidth() + beg.getPadding() * 2);
            }
        }
    }

    @Override
    public void destroyResources() {
        getHeaderMetaData().stream()
                .filter(md -> md instanceof HasDOMElementResources)
                .map(md -> (HasDOMElementResources) md)
                .forEach(HasDOMElementResources::destroyResources);
    }
}
