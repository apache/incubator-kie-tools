/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.widget.table.popovers;

import com.ait.lienzo.client.core.event.NodeMouseMoveEvent;
import com.ait.lienzo.client.core.event.NodeMouseMoveHandler;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.types.Point2D;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableModellerView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.util.CoordinateUtilities;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;

public class ColumnHeaderPopOverHandler implements NodeMouseMoveHandler {

    private final GuidedDecisionTableModellerView.Presenter modellerPresenter;
    private final ColumnHeaderPopOver columnPopOverPresenter;

    public ColumnHeaderPopOverHandler( final GuidedDecisionTableModellerView.Presenter modellerPresenter,
                                       final ColumnHeaderPopOver columnPopOverPresenter ) {
        this.modellerPresenter = modellerPresenter;
        this.columnPopOverPresenter = columnPopOverPresenter;
    }

    @Override
    public void onNodeMouseMove( final NodeMouseMoveEvent event ) {
        columnPopOverPresenter.hide();

        for ( GuidedDecisionTableView.Presenter dtPresenter : modellerPresenter.getAvailableDecisionTables() ) {
            final GuidedDecisionTableView dtView = dtPresenter.getView();
            if ( !dtView.isVisible() ) {
                continue;
            }

            final Point2D ap = CoordinateUtilities.convertDOMToGridCoordinate( dtView,
                                                                               new Point2D( event.getX(),
                                                                                            event.getY() ) );

            if ( !isMouseOverTableHeader( dtView,
                                          ap.getY() ) ) {
                continue;
            }

            final Integer uiColumnIndex = getUiColumn( dtView,
                                                       ap.getX() );
            if ( uiColumnIndex == null ) {
                continue;
            }

            columnPopOverPresenter.show( modellerPresenter.getView(),
                                         dtPresenter,
                                         uiColumnIndex );
        }
    }

    private boolean isMouseOverTableHeader( final GridWidget gridWidget,
                                            final double cy ) {
        final Group header = gridWidget.getHeader();
        final GridRenderer renderer = gridWidget.getRenderer();
        final double headerHeight = renderer.getHeaderHeight();
        final double headerRowsYOffset = getHeaderRowsYOffset( gridWidget );
        final double headerMinY = ( header == null ? headerRowsYOffset : header.getY() + headerRowsYOffset );
        final double headerMaxY = ( header == null ? headerHeight : headerHeight + header.getY() );

        return headerMinY < cy && cy < headerMaxY;
    }

    private double getHeaderRowsYOffset( final GridWidget gridWidget ) {
        final GridData model = gridWidget.getModel();
        final int headerRowCount = model.getHeaderRowCount();
        final GridRenderer renderer = gridWidget.getRenderer();
        final double headerHeight = renderer.getHeaderHeight();
        final double headerRowHeight = renderer.getHeaderRowHeight();
        final double headerRowsHeight = headerRowHeight * headerRowCount;
        final double headerRowsYOffset = headerHeight - headerRowsHeight;

        return headerRowsYOffset;
    }

    private Integer getUiColumn( final GridWidget gridWidget,
                                 final double cx ) {
        final Integer uiColumnIndex = CoordinateUtilities.getUiColumnIndex( gridWidget,
                                                                            cx );
        return uiColumnIndex;
    }

}
