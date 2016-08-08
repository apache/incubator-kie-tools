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

package org.drools.workbench.screens.guided.dtable.client.widget.table.popovers;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.types.Transform;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableModellerView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.popovers.definitions.ColumnDefinitionFactory;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

@ApplicationScoped
public class ColumnHeaderPopOverImpl implements ColumnHeaderPopOver {

    private PopOverView view;
    private ColumnDefinitionFactory columnDefinitionFactory;

    @Inject
    public ColumnHeaderPopOverImpl( final PopOverView view,
                                    final ColumnDefinitionFactory columnDefinitionFactory ) {
        this.view = view;
        this.columnDefinitionFactory = columnDefinitionFactory;
    }

    @Override
    public void show( final GuidedDecisionTableModellerView modellerView,
                      final GuidedDecisionTableView.Presenter dtPresenter,
                      final int uiColumnIndex ) {
        PortablePreconditions.checkNotNull( "modellerView",
                                            modellerView );
        PortablePreconditions.checkNotNull( "dtPresenter",
                                            dtPresenter );

        showSource( modellerView,
                    dtPresenter,
                    uiColumnIndex );
    }

    private void showSource( final GuidedDecisionTableModellerView modellerView,
                             final GuidedDecisionTableView.Presenter dtPresenter,
                             final int uiColumnIndex ) {
        final BaseColumn column = dtPresenter.getModel().getExpandedColumns().get( uiColumnIndex );

        final int screenX = getScreenX( modellerView,
                                        dtPresenter,
                                        uiColumnIndex );
        final int screenY = getScreenY( modellerView,
                                        dtPresenter );

        view.show( ( Callback<PopOverView.Content> callback ) ->
                           columnDefinitionFactory.generateColumnDefinition( dtPresenter,
                                                                             column,
                                                                             ( String definition ) ->
                                                                                     callback.callback( new PopOverView.Content() {
                                                                                         @Override
                                                                                         public String getContent() {
                                                                                             return definition;
                                                                                         }

                                                                                         @Override
                                                                                         public int getX() {
                                                                                             return screenX;
                                                                                         }

                                                                                         @Override
                                                                                         public int getY() {
                                                                                             return screenY;
                                                                                         }
                                                                                     } )
                                                                           ) );
    }

    @Override
    public void hide() {
        view.hide();
    }

    private int getScreenX( final GuidedDecisionTableModellerView modellerView,
                            final GuidedDecisionTableView.Presenter dtPresenter,
                            final int uiColumnIndex ) {
        final GridWidget gridWidget = dtPresenter.getView();
        final GridColumn<?> uiColumn = gridWidget.getModel().getColumns().get( uiColumnIndex );

        final double gx = gridWidget.getX();
        final GridLayer layer = modellerView.getGridLayerView();
        final int containerX = layer.getDomElementContainer().getAbsoluteLeft();
        final double vx = layer.getVisibleBounds().getX();
        final Transform t = layer.getViewport().getTransform();

        final BaseGridRendererHelper rendererHelper = gridWidget.getRendererHelper();
        final BaseGridRendererHelper.RenderingInformation ri = rendererHelper.getRenderingInformation();
        final BaseGridRendererHelper.RenderingBlockInformation floatingBlockInformation = ri.getFloatingBlockInformation();
        final double offsetX = floatingBlockInformation.getColumns().contains( uiColumn ) ? floatingBlockInformation.getX() : 0;

        final int screenX = containerX + (int) ( ( gx - vx + offsetX + rendererHelper.getColumnOffset( uiColumn ) + uiColumn.getWidth() / 2 ) * t.getScaleX() );
        return screenX;
    }

    private int getScreenY( final GuidedDecisionTableModellerView modellerView,
                            final GuidedDecisionTableView.Presenter dtPresenter ) {
        final GridWidget gridWidget = dtPresenter.getView();

        final double gy = gridWidget.getY();
        final GridLayer layer = modellerView.getGridLayerView();
        final int containerY = layer.getDomElementContainer().getAbsoluteTop();
        final double vy = layer.getVisibleBounds().getY();
        final Transform t = layer.getViewport().getTransform();

        final Group header = gridWidget.getHeader();
        final double headerHeight = gridWidget.getRenderer().getHeaderHeight();
        final double headerRowHeight = gridWidget.getRenderer().getHeaderRowHeight();
        final double offsetY = header == null ? 0 : header.getY();

        final int screenY = containerY + (int) ( ( gy - vy + offsetY + headerHeight - headerRowHeight / 2 ) * t.getScaleX() );
        return screenY;
    }

}
