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

package org.drools.workbench.screens.guided.dtable.client.widget.table.themes;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Line;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.shared.core.types.ColorName;
import org.drools.verifier.api.reporting.Severity;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableViewImpl;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.GuidedDecisionTableUiModel;
import org.uberfire.ext.wires.core.grids.client.model.Bounds;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseBounds;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;

public class GuidedDecisionTableRenderer extends BaseGridRenderer {

    private static final int HEADER_HEIGHT = 96;
    private final GuidedDecisionTable52 model;
    private Severity rowHighlightSeverity;
    private Set<Integer> rowHighlightRowIndexes;

    public GuidedDecisionTableRenderer(final GuidedDecisionTableUiModel uiModel,
                                       final GuidedDecisionTable52 model) {
        super(new GuidedDecisionTableTheme(uiModel,
                                           model));
        this.model = model;
    }

    @Override
    public double getHeaderHeight() {
        return HEADER_HEIGHT;
    }

    @Override
    public List<RendererCommand> renderBody(final GridData model,
                                            final GridBodyRenderContext context,
                                            final BaseGridRendererHelper rendererHelper,
                                            final BaseGridRendererHelper.RenderingInformation renderingInformation) {
        final List<RendererCommand> commands = super.renderBody(model,
                                                                context,
                                                                rendererHelper,
                                                                renderingInformation);
        if (!(rowHighlightRowIndexes == null || rowHighlightRowIndexes.isEmpty())) {
            commands.addAll(renderRowHighlights(model,
                                                context,
                                                rendererHelper,
                                                renderingInformation));
        }

        return commands;
    }

    @Override
    public RendererCommand renderSelector(final double width,
                                          final double height,
                                          final BaseGridRendererHelper.RenderingInformation renderingInformation) {
        return (RenderSelectorCommand) (rc) -> {
            if (!rc.isSelectionLayer()) {
                final double captionWidth = getHeaderCaptionWidth();
                final Bounds bounds = getSelectorBounds(width,
                                                        height,
                                                        renderingInformation);
                final MultiPath selector = theme.getSelector()
                        .M(bounds.getX() + 0.5,
                           bounds.getY() + 0.5)
                        .L(bounds.getX() + 0.5,
                           height)
                        .L(width,
                           height)
                        .L(width,
                           bounds.getY() + GuidedDecisionTableViewImpl.HEADER_CAPTION_HEIGHT)
                        .L(bounds.getX() + captionWidth,
                           bounds.getY() + GuidedDecisionTableViewImpl.HEADER_CAPTION_HEIGHT)
                        .L(bounds.getX() + captionWidth,
                           bounds.getY() + 0.5)
                        .L(bounds.getX() + GuidedDecisionTableViewImpl.HEADER_CAPTION_WIDTH,
                           bounds.getY() + 0.5)
                        .L(bounds.getX() + 0.5,
                           bounds.getY() + 0.5)
                        .setListening(false);
                rc.getGroup().add(selector);
            }
        };
    }

    private double getHeaderCaptionWidth() {
        return Math.max(GuidedDecisionTableViewImpl.HEADER_CAPTION_WIDTH,
                        model.getRowNumberCol().getWidth() + model.getDescriptionCol().getWidth());
    }

    private Bounds getSelectorBounds(final double width,
                                     final double height,
                                     final BaseGridRendererHelper.RenderingInformation renderingInformation) {
        final BaseGridRendererHelper.RenderingBlockInformation bodyBlockInformation = renderingInformation.getBodyBlockInformation();
        final BaseGridRendererHelper.RenderingBlockInformation floatingBlockInformation = renderingInformation.getFloatingBlockInformation();
        double boundsX = 0.0;
        double boundsY = 0.0;
        double boundsWidth = width;
        double boundsHeight = height;
        if (!floatingBlockInformation.getColumns().isEmpty()) {
            boundsX = floatingBlockInformation.getX();
            boundsWidth = boundsWidth - floatingBlockInformation.getX();
        }
        if (renderingInformation.isFloatingHeader()) {
            boundsY = bodyBlockInformation.getHeaderY();
            boundsHeight = boundsHeight - bodyBlockInformation.getHeaderY();
        }
        return new BaseBounds(boundsX,
                              boundsY,
                              boundsWidth,
                              boundsHeight);
    }

    @Override
    public RendererCommand renderHeaderBodyDivider(final double width) {
        return (RenderHeaderGridLinesCommand) (rc) -> {
            if (!rc.isSelectionLayer()) {
                final Group g = new Group();
                final Line dividerLine1 = theme.getGridHeaderBodyDivider();
                final Line dividerLine2 = theme.getGridHeaderBodyDivider();
                dividerLine1.setPoints(new Point2DArray(new Point2D(0,
                                                                    getHeaderHeight() - 1.5),
                                                        new Point2D(width,
                                                                    getHeaderHeight() - 1.5)));
                dividerLine2.setPoints(new Point2DArray(new Point2D(0,
                                                                    getHeaderHeight() + 0.5),
                                                        new Point2D(width,
                                                                    getHeaderHeight() + 0.5)));
                g.add(dividerLine1);
                g.add(dividerLine2);
                rc.getGroup().add(g);
            }
        };
    }

    public void highlightRows(final Severity rowHighlightSeverity,
                              final Set<Integer> rowHighlightRowIndexes) {
        this.rowHighlightSeverity = rowHighlightSeverity;
        this.rowHighlightRowIndexes = rowHighlightRowIndexes;
    }

    public void clearHighlights() {
        this.rowHighlightSeverity = null;
        this.rowHighlightRowIndexes = null;
    }

    List<RendererCommand> renderRowHighlights(final GridData model,
                                              final GridBodyRenderContext context,
                                              final BaseGridRendererHelper rendererHelper,
                                              final BaseGridRendererHelper.RenderingInformation renderingInformation) {
        final List<RendererCommand> commands = new ArrayList<>();
        rowHighlightRowIndexes.stream().forEach((rowIndex) -> {
            final int _rowIndex = rowIndex - 1;
            final int _visibleRowIndex = _rowIndex - renderingInformation.getMinVisibleRowIndex();
            if (_rowIndex >= 0 && _rowIndex < model.getRowCount()) {
                if (_visibleRowIndex >= 0 && _visibleRowIndex < model.getRowCount()) {
                    commands.add((RenderSelectedCellsCommand) (rc) -> {
                        if (!rc.isSelectionLayer()) {
                            rc.getGroup().add(makeRowHighlight(_rowIndex,
                                                               _visibleRowIndex,
                                                               model,
                                                               context,
                                                               rendererHelper));
                        }
                    });
                }
            }
        });
        return commands;
    }

    Rectangle makeRowHighlight(final int _rowIndex,
                               final int _visibleRowIndex,
                               final GridData model,
                               final GridBodyRenderContext context,
                               final BaseGridRendererHelper rendererHelper) {
        final Rectangle r = new Rectangle(0,
                                          0).setAlpha(0.3).setFillColor(ColorName.ORANGE).setListening(false);
        r.setY(rendererHelper.getRowOffset(_visibleRowIndex));
        r.setWidth(rendererHelper.getWidth(context.getBlockColumns()));
        r.setHeight(model.getRow(_rowIndex).getHeight());

        switch (rowHighlightSeverity) {
            case NOTE:
                r.setFillColor(ColorName.LIGHTBLUE);
                break;
            case WARNING:
                r.setFillColor(ColorName.ORANGE);
                break;
            case ERROR:
                r.setFillColor(ColorName.RED);
                break;
        }
        return r;
    }
}
