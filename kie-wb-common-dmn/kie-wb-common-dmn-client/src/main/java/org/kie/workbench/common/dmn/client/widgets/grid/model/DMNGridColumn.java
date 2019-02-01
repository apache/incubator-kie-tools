/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.dmn.client.widgets.grid.model;

import java.util.List;

import org.kie.soup.commons.util.Lists;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.dom.HasDOMElementResources;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.GridColumnRenderer;

public abstract class DMNGridColumn<G extends GridWidget, T> extends BaseGridColumn<T> implements HasDOMElementResources {

    public static final double DEFAULT_WIDTH = 100.0;

    protected final G gridWidget;

    public DMNGridColumn(final HeaderMetaData headerMetaData,
                         final GridColumnRenderer<T> columnRenderer,
                         final G gridWidget) {
        this(new Lists.Builder<HeaderMetaData>()
                     .add(headerMetaData)
                     .build(),
             columnRenderer,
             gridWidget);
    }

    public DMNGridColumn(final List<HeaderMetaData> headerMetaData,
                         final GridColumnRenderer<T> columnRenderer,
                         final G gridWidget) {
        super(headerMetaData,
              columnRenderer,
              DEFAULT_WIDTH);
        this.gridWidget = gridWidget;
    }

    public void setWidthInternal(final double width) {
        super.setWidth(width);
    }

    public void updateWidthOfPeers() {
        if (gridWidget instanceof BaseExpressionGrid) {
            final BaseExpressionGrid beg = (BaseExpressionGrid) gridWidget;
            final int parentColumnIndex = beg.getParentInformation().getColumnIndex();
            final GridData parentGridData = beg.getParentInformation().getGridWidget().getModel();
            if (parentGridData != null) {
                if (parentColumnIndex < parentGridData.getColumnCount()) {
                    final GridColumn<?> parentColumn = parentGridData.getColumns().get(parentColumnIndex);
                    parentColumn.setWidth(beg.getWidth() + beg.getPadding() * 2);
                }
            }
        }
    }

    @Override
    public void destroyResources() {
        if (gridWidget instanceof BaseExpressionGrid) {
            ((BaseExpressionGrid) gridWidget).destroyResources();
        }
        getHeaderMetaData().stream()
                .filter(md -> md instanceof HasDOMElementResources)
                .map(md -> (HasDOMElementResources) md)
                .forEach(HasDOMElementResources::destroyResources);
    }
}
