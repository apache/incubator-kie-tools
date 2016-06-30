/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.client.widget.table.columns;

import java.util.List;

import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTablePresenter;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.GridColumnRenderer;

/**
 * Base column for Decision Tables.
 * @param <T> The Type of value presented by this column
 */
public abstract class BaseUiColumn<T> extends BaseGridColumn<T> {

    private GuidedDecisionTablePresenter.Access access;
    private ColumnResizeListener columnResizeListener;

    public BaseUiColumn( final List<HeaderMetaData> headerMetaData,
                         final GridColumnRenderer<T> columnRenderer,
                         final double width,
                         final boolean isResizable,
                         final boolean isVisible,
                         final GuidedDecisionTablePresenter.Access access ) {
        super( headerMetaData,
               columnRenderer,
               width );
        setResizable( isResizable );
        setVisible( isVisible );
        this.access = access;
    }

    public boolean isEditable() {
        return access.isEditable();
    }

    public void setColumnResizeListener( final ColumnResizeListener columnResizeListener ) {
        this.columnResizeListener = columnResizeListener;
    }

    @Override
    public void setWidth( final double width ) {
        super.setWidth( width );
        if ( columnResizeListener != null ) {
            columnResizeListener.onResizeColumn( width );
        }
    }

    public interface ColumnResizeListener {

        void onResizeColumn( final double width );

    }

}
