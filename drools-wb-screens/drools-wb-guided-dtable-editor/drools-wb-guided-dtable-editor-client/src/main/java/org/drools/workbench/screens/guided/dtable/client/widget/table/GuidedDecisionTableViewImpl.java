/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.client.widget.table;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.event.Event;

import com.ait.lienzo.client.core.event.INodeXYEvent;
import com.ait.lienzo.client.core.event.NodeDragMoveHandler;
import com.ait.lienzo.client.core.event.NodeMouseDoubleClickHandler;
import com.ait.lienzo.client.core.shape.BoundingBoxPathClipper;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPathClipper;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.table.themes.GuidedDecisionTableRenderer;
import org.drools.workbench.screens.guided.dtable.client.widget.table.themes.GuidedDecisionTableTheme;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.util.CoordinateUtilities;
import org.uberfire.ext.wires.core.grids.client.widget.grid.NodeMouseEventHandler;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.BaseGridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridSelectionManager;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.GridPinnedModeManager;
import org.uberfire.workbench.events.NotificationEvent;

public class GuidedDecisionTableViewImpl
        extends BaseGridWidget
        implements GuidedDecisionTableView{

    public static final int HEADER_CAPTION_WIDTH = 200;

    public static final int HEADER_CAPTION_HEIGHT = 32;

    private final GuidedDecisionTableView.Presenter presenter;

    private final GuidedDecisionTable52 model;

    private final Event<NotificationEvent> notificationEvent;

    private Group headerCaption;

    public GuidedDecisionTableViewImpl(final GridData uiModel,
                                       final GridRenderer renderer,
                                       final Presenter presenter,
                                       final GuidedDecisionTable52 model,
                                       final Event<NotificationEvent> notificationEvent) {
        super(uiModel,
              presenter,
              presenter,
              renderer);

        this.presenter = presenter;
        this.model = model;
        this.notificationEvent = notificationEvent;
        this.headerCaption = makeHeaderCaption();
    }

    @Override
    public void registerNodeDragMoveHandler(final NodeDragMoveHandler handler) {
        addNodeDragMoveHandler(handler);
    }

    @Override
    public void registerNodeMouseDoubleClickHandler(final NodeMouseDoubleClickHandler handler) {
        addNodeMouseDoubleClickHandler(handler);
    }

    @Override
    protected List<NodeMouseEventHandler> getNodeMouseDoubleClickEventHandlers(final GridSelectionManager selectionManager,
                                                                               final GridPinnedModeManager pinnedModeManager) {
        final List<NodeMouseEventHandler> handlers = new ArrayList<>();
        handlers.add(new GuidedDecisionTableSortGridWidgetMouseEventHandler(new Callback<GridColumn, Void>() {
            @Override
            public void onFailure(final Void unused) {
                // Not implemented
            }

            @Override
            public void onSuccess(final GridColumn column) {
                presenter.onSort(column);
            }
        }));
        return handlers;
    }

    private Group makeHeaderCaption() {
        final Group g = GWT.create(Group.class);
        final double captionWidth = ((GuidedDecisionTableRenderer) renderer).getHeaderCaptionWidth();
        final GuidedDecisionTableTheme theme = (GuidedDecisionTableTheme) renderer.getTheme();
        final Rectangle r = theme.getBaseRectangle(GuidedDecisionTableTheme.ModelColumnType.CAPTION);
        r.setWidth(captionWidth);
        r.setHeight(HEADER_CAPTION_HEIGHT);

        final MultiPath border = theme.getBodyGridLine();
        border.M(0.5,
                 HEADER_CAPTION_HEIGHT + 0.5);
        border.L(0.5,
                 0.5);
        border.L(captionWidth + 0.5,
                 0.5);
        border.L(captionWidth + 0.5,
                 HEADER_CAPTION_HEIGHT + 0.5);
        border.L(0.5,
                 HEADER_CAPTION_HEIGHT + 0.5);

        final Text caption = theme.getHeaderText();
        caption.setText(model.getTableName());
        caption.setX(captionWidth / 2);
        caption.setY(HEADER_CAPTION_HEIGHT / 2);

        //Clip Caption Group
        final BoundingBox bb = new BoundingBox(0,
                                               0,
                                               captionWidth + border.getStrokeWidth(),
                                               HEADER_CAPTION_HEIGHT + 0.5);
        final IPathClipper clipper = getPathClipper(bb);
        g.setPathClipper(clipper);
        clipper.setActive(true);

        g.add(r);
        g.add(caption);
        g.add(border);

        return g;
    }

    //Allow overriding in Unit Tests as BoundingBoxPathClipper cannot be mocked
    IPathClipper getPathClipper(final BoundingBox bb) {
        return new BoundingBoxPathClipper(bb);
    }

    @Override
    public boolean onDragHandle(final INodeXYEvent event) {
        return isNodeMouseEventOverCaption(event);
    }

    @Override
    public boolean isNodeMouseEventOverCaption(final INodeXYEvent event) {
        final Point2D ap = CoordinateUtilities.convertDOMToGridCoordinate(this,
                                                                          new Point2D(event.getX(),
                                                                                      event.getY()));
        final double cx = ap.getX();
        final double cy = ap.getY();

        if (cx > headerCaption.getX() && cx < headerCaption.getX() + ((GuidedDecisionTableRenderer) renderer).getHeaderCaptionWidth()) {
            if (cy > headerCaption.getY() && cy < headerCaption.getY() + HEADER_CAPTION_HEIGHT) {
                return true;
            }
        }
        return false;
    }

    @Override
    public GuidedDecisionTableView.Presenter getPresenter() {
        return presenter;
    }

    @Override
    public void setLocation(final double x,
                            final double y) {
        setLocation(new Point2D(x,
                                y));
    }

    @Override
    protected void drawHeader(final BaseGridRendererHelper.RenderingInformation renderingInformation) {
        super.drawHeader(renderingInformation);

        headerCaption = makeHeaderCaption();

        if (!floatingColumns.isEmpty()) {
            if (floatingHeader != null) {
                addCommandToRenderQueue(floatingHeader,
                                        (GridRenderer.RenderHeaderBackgroundCommand) (rc) -> {
                                            rc.getGroup().add(headerCaption);
                                        });
            }
        } else if (isRowNumberAndDescriptionColumnsShown()) {
            if (header != null) {
                addCommandToRenderQueue(header,
                                        (GridRenderer.RenderHeaderBackgroundCommand) (rc) -> {
                                            rc.getGroup().add(headerCaption);
                                        });
            }
        }
    }

    private boolean isRowNumberAndDescriptionColumnsShown() {
        final List<BaseColumn> allModelColumns = model.getExpandedColumns();
        final int rowNumberColumnIndex = allModelColumns.indexOf(model.getRowNumberCol());
        final int descriptionColumnIndex = allModelColumns.indexOf(model.getDescriptionCol());
        return bodyColumns.contains(allColumns.get(rowNumberColumnIndex)) && bodyColumns.contains(allColumns.get(descriptionColumnIndex));
    }

    @Override
    public void showDataCutNotificationEvent() {
        notificationEvent.fire(new NotificationEvent(GuidedDecisionTableConstants.INSTANCE.DataCutToClipboardMessage()));
    }

    @Override
    public void showDataCopiedNotificationEvent() {
        notificationEvent.fire(new NotificationEvent(GuidedDecisionTableConstants.INSTANCE.DataCopiedToClipboardMessage()));
    }

    @Override
    public void showBusyIndicator(final String message) {
        BusyPopup.showMessage(message);
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }
}
