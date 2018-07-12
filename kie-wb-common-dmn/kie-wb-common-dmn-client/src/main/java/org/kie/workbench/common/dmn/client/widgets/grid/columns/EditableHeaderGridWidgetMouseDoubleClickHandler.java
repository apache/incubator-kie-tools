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

package org.kie.workbench.common.dmn.client.widgets.grid.columns;

import com.ait.lienzo.client.core.event.NodeMouseDoubleClickEvent;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.types.Point2D;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.util.CoordinateUtilities;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellEditContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.BaseGridWidgetMouseDoubleClickHandler;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridSelectionManager;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.GridPinnedModeManager;

public class EditableHeaderGridWidgetMouseDoubleClickHandler extends BaseGridWidgetMouseDoubleClickHandler {

    public EditableHeaderGridWidgetMouseDoubleClickHandler(final GridWidget gridWidget,
                                                           final GridSelectionManager selectionManager,
                                                           final GridPinnedModeManager pinnedModeManager,
                                                           final GridRenderer renderer) {
        super(gridWidget,
              selectionManager,
              pinnedModeManager,
              renderer);
    }

    @Override
    protected boolean handleHeaderCellDoubleClick(final NodeMouseDoubleClickEvent event) {
        //Convert Canvas co-ordinate to Grid co-ordinate
        final Point2D rp = CoordinateUtilities.convertDOMToGridCoordinate(gridWidget,
                                                                          new Point2D(event.getX(),
                                                                                      event.getY()));
        final double cx = rp.getX();
        final double cy = rp.getY();

        final Group header = gridWidget.getHeader();
        final double headerRowsYOffset = getHeaderRowsYOffset();
        final double headerMinY = (header == null ? headerRowsYOffset : header.getY() + headerRowsYOffset);
        final double headerMaxY = (header == null ? renderer.getHeaderHeight() : renderer.getHeaderHeight() + header.getY());

        if (cx < 0 || cx > gridWidget.getWidth()) {
            return false;
        }
        if (cy < headerMinY || cy > headerMaxY) {
            return false;
        }

        //Get column information
        final BaseGridRendererHelper rendererHelper = gridWidget.getRendererHelper();
        final BaseGridRendererHelper.RenderingInformation ri = rendererHelper.getRenderingInformation();
        if (ri == null) {
            return false;
        }
        final BaseGridRendererHelper.ColumnInformation ci = rendererHelper.getColumnInformation(cx);
        final GridColumn<?> column = ci.getColumn();
        if (column == null) {
            return false;
        }
        if (!EditableHeaderUtilities.hasEditableHeader(column)) {
            return false;
        }

        //Get row index
        final Integer uiHeaderRowIndex = EditableHeaderUtilities.getUiHeaderRowIndex(gridWidget,
                                                                                     column,
                                                                                     cy);
        if (uiHeaderRowIndex == null) {
            return false;
        }
        if (!EditableHeaderUtilities.isEditableHeader(column,
                                                      uiHeaderRowIndex)) {
            return false;
        }

        //Get rendering information
        final EditableHeaderMetaData headerMetaData = (EditableHeaderMetaData) column.getHeaderMetaData().get(uiHeaderRowIndex);
        final GridBodyCellEditContext context = EditableHeaderUtilities.makeRenderContext(gridWidget,
                                                                                          ri,
                                                                                          ci,
                                                                                          rp,
                                                                                          uiHeaderRowIndex);
        headerMetaData.edit(context);

        return true;
    }
}
