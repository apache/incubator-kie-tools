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
package org.uberfire.ext.wires.core.grids.client.widget.grid.impl;

import java.util.AbstractMap;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseClickHandler;
import com.ait.lienzo.client.core.event.NodeMouseDoubleClickHandler;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBoundaryRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridHeaderRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.dnd.GridWidgetDnDHandlersState;
import org.uberfire.ext.wires.core.grids.client.widget.dom.HasDOMElementResources;
import org.uberfire.ext.wires.core.grids.client.widget.dom.multiple.HasMultipleDOMElementResources;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.NodeMouseEventHandler;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.SelectionsTransformer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.DefaultSelectionsTransformer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.FloatingSelectionsTransformer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.SelectedRange;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.CellSelectionManager;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.SelectionExtension;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.impl.BaseCellSelectionManager;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridSelectionManager;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.DefaultGridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.GridPinnedModeManager;

import static org.uberfire.ext.wires.core.grids.client.util.Logging.log;

/**
 * The base of all GridWidgets.
 */
public class BaseGridWidget extends Group implements GridWidget {

    private static final Logger LOGGER = Logger.getLogger(BaseGridWidget.class.getName());

    protected final SelectionsTransformer bodyTransformer;
    protected final SelectionsTransformer floatingColumnsTransformer;
    protected final BaseGridRendererHelper rendererHelper;
    protected final Queue<Map.Entry<Group, List<GridRenderer.RendererCommand>>> renderQueue = new ArrayDeque<>();

    //These are final as a reference is held by the ISelectionsTransformers
    protected final List<GridColumn<?>> allColumns = new ArrayList<>();
    protected final List<GridColumn<?>> bodyColumns = new ArrayList<>();
    protected final List<GridColumn<?>> floatingColumns = new ArrayList<>();

    protected GridData model;
    protected GridRenderer renderer;
    protected Group header = null;
    protected Group headerSelections = null;
    protected Group floatingHeader = null;
    protected Group floatingHeaderSelections = null;

    protected Group body = null;
    protected Group bodySelections = null;
    protected Group floatingBody = null;
    protected Group floatingBodySelections = null;

    protected Group boundary = null;
    protected BaseGridRendererHelper.RenderingInformation renderingInformation;

    private Group selection = null;
    private boolean isSelected = false;
    private CellSelectionManager cellSelectionManager;

    private final BiFunction<SelectedRange, Integer, Double> headerSelectionYOffsetStrategy;
    private final Function<SelectedRange, Double> headerSelectionHeightStrategy;
    private final BiFunction<SelectedRange, Integer, Double> bodySelectionYOffsetStrategy;
    private final Function<SelectedRange, Double> bodySelectionHeightStrategy;

    public BaseGridWidget(final GridData model,
                          final GridSelectionManager selectionManager,
                          final GridPinnedModeManager pinnedModeManager,
                          final GridRenderer renderer) {
        this.model = model;
        this.renderer = renderer;
        this.bodyTransformer = new DefaultSelectionsTransformer(model,
                                                                bodyColumns);
        this.floatingColumnsTransformer = new FloatingSelectionsTransformer(model,
                                                                            floatingColumns);
        this.rendererHelper = getBaseGridRendererHelper();
        this.cellSelectionManager = getCellSelectionManager();

        //Click handlers
        addNodeMouseClickHandler(getGridMouseClickHandler(selectionManager));
        addNodeMouseDoubleClickHandler(getGridMouseDoubleClickHandler(selectionManager,
                                                                      pinnedModeManager));

        //NodeMouseUpEvent on GridLayer is not fired at a drag-end, so clear the state here.
        addNodeDragEndHandler((event) -> {
            final GridWidgetDnDHandlersState state = ((DefaultGridLayer) getLayer()).getGridWidgetHandlersState();
            state.reset();
            getViewport().getElement().getStyle().setCursor(state.getCursor());
        });

        headerSelectionYOffsetStrategy = getHeaderSelectionYOffsetStrategy();
        headerSelectionHeightStrategy = getHeaderSelectionHeightStrategy();
        bodySelectionYOffsetStrategy = getBodySelectionYOffsetStrategy();
        bodySelectionHeightStrategy = getBodySelectionHeightStrategy();
    }

    BiFunction<SelectedRange, Integer, Double> getHeaderSelectionYOffsetStrategy() {
        return (selectedRange, minVisibleUiRowIndex) -> {
            final double headerRowsYOffset = renderingInformation.getHeaderRowsYOffset();
            final double uiRowOffset = renderer.getHeaderRowHeight() * selectedRange.getUiRowIndex();
            return headerRowsYOffset + uiRowOffset;
        };
    }

    Function<SelectedRange, Double> getHeaderSelectionHeightStrategy() {
        return selectedRange -> {
            final double headerHeight = getRenderer().getHeaderHeight();
            final double headerRowsYOffset = renderingInformation.getHeaderRowsYOffset();
            final GridColumn<?> uiColumn = model.getColumns().get(selectedRange.getUiColumnIndex());
            final List<GridColumn.HeaderMetaData> headerMetaData = uiColumn.getHeaderMetaData();
            final double headerRowHeight = (headerHeight - headerRowsYOffset) / headerMetaData.size();
            return selectedRange.getHeight() * headerRowHeight;
        };
    }

    BiFunction<SelectedRange, Integer, Double> getBodySelectionYOffsetStrategy() {
        return (selectedRange, minVisibleUiRowIndex) -> {
            final double uiRowOffset = rendererHelper.getRowOffset(selectedRange.getUiRowIndex());
            final double uiMinVisibleRowOffset = rendererHelper.getRowOffset(minVisibleUiRowIndex);
            return uiRowOffset - uiMinVisibleRowOffset;
        };
    }

    Function<SelectedRange, Double> getBodySelectionHeightStrategy() {
        return selectedRange ->
                IntStream.range(0, selectedRange.getHeight())
                        .mapToObj(rowIndex -> model.getRow(selectedRange.getUiRowIndex() + rowIndex))
                        .mapToDouble(GridRow::getHeight)
                        .sum();
    }

    protected BaseGridRendererHelper getBaseGridRendererHelper() {
        return new BaseGridRendererHelper(this);
    }

    @Override
    public CellSelectionManager getCellSelectionManager() {
        if (cellSelectionManager == null) {
            cellSelectionManager = new BaseCellSelectionManager(this);
        }
        return cellSelectionManager;
    }

    protected NodeMouseClickHandler getGridMouseClickHandler(final GridSelectionManager selectionManager) {
        return new BaseGridWidgetMouseClickHandler(this, getNodeMouseClickEventHandlers(selectionManager));
    }

    protected List<NodeMouseEventHandler> getNodeMouseClickEventHandlers(final GridSelectionManager selectionManager) {
        final List<NodeMouseEventHandler> handlers = new ArrayList<>();
        handlers.add(new DefaultGridWidgetCellSelectorMouseEventHandler(selectionManager));
        handlers.add(new DefaultGridWidgetCollapsedCellMouseEventHandler(renderer));
        handlers.add(new DefaultGridWidgetLinkedColumnMouseEventHandler(selectionManager,
                                                                        renderer));
        return handlers;
    }

    protected NodeMouseDoubleClickHandler getGridMouseDoubleClickHandler(final GridSelectionManager selectionManager,
                                                                         final GridPinnedModeManager pinnedModeManager) {
        return new BaseGridWidgetMouseDoubleClickHandler(this, getNodeMouseDoubleClickEventHandlers(selectionManager, pinnedModeManager));
    }

    protected List<NodeMouseEventHandler> getNodeMouseDoubleClickEventHandlers(final GridSelectionManager selectionManager,
                                                                               final GridPinnedModeManager pinnedModeManager) {
        final List<NodeMouseEventHandler> handlers = new ArrayList<>();
        handlers.add(new DefaultGridWidgetEditCellMouseEventHandler());
        handlers.add(new DefaultGridWidgetPinnedModeMouseEventHandler(pinnedModeManager,
                                                                      renderer));
        return handlers;
    }

    @Override
    public GridData getModel() {
        return model;
    }

    @Override
    public GridRenderer getRenderer() {
        return this.renderer;
    }

    @Override
    public void setRenderer(final GridRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public BaseGridRendererHelper getRendererHelper() {
        return rendererHelper;
    }

    @Override
    public Group getBody() {
        return body;
    }

    @Override
    public Group getHeader() {
        return header;
    }

    @Override
    public double getWidth() {
        return rendererHelper.getWidth(model.getColumns());
    }

    @Override
    public double getHeight() {
        double height = renderer.getHeaderHeight();
        height = height + rendererHelper.getRowOffset(model.getRowCount());
        return height;
    }

    private double getHeight(final BaseGridRendererHelper.RenderingInformation renderingInformation) {
        double height = renderer.getHeaderHeight();
        for (double h : renderingInformation.getAllRowHeights()) {
            height = height + h;
        }
        return height;
    }

    @Override
    public void select() {
        isSelected = true;
    }

    @Override
    public void deselect() {
        isSelected = false;
    }

    @Override
    public boolean isSelected() {
        return isSelected;
    }

    /**
     * Intercept the normal Lienzo draw mechanism to calculate and hence draw only the visible
     * columns and rows for the Grid; being those within the bounds of the GridLayer. At the
     * start of this draw method all visible columns are given an opportunity to initialise
     * any resources they require (e.g. DOMElements). At the end of this method all visible
     * columns are given an opportunity to release any unused resources (e.g. DOMElements).
     * If a column is not visible it is given an opportunity to destroy all resources.
     * @param context
     * @param alpha
     */
    @Override
    protected void drawWithoutTransforms(Context2D context,
                                         double alpha,
                                         BoundingBox bb) {
        final boolean isSelectionLayer = context.isSelection();
        if (isSelectionLayer && (false == isListening())) {
            return;
        }
        alpha = alpha * getAttributes().getAlpha();

        if (alpha <= 0) {
            return;
        }
        if (model.getColumns().isEmpty()) {
            return;
        }

        //Clear existing content
        this.removeAll();

        long currentTimeMillis;
        if (!isSelectionLayer) {
            //If there's no RenderingInformation the GridWidget is not visible
            currentTimeMillis = log(LOGGER, " - Pre- prepare()");
            this.renderingInformation = prepare();
            log(LOGGER, " - Post- prepare()", currentTimeMillis);
            if (renderingInformation == null) {
                destroyDOMElementResources();
                return;
            }
            currentTimeMillis = log(LOGGER, " - Pre- makeRenderingCommands()");
            makeRenderingCommands();
            log(LOGGER, " - Post- makeRenderingCommands()", currentTimeMillis);
        }

        currentTimeMillis = log(LOGGER, " - Pre- layerRenderGroups()");
        layerRenderGroups();
        log(LOGGER, " - Post- layerRenderGroups()", currentTimeMillis);

        currentTimeMillis = log(LOGGER, " - Pre- executeRenderQueueCommands()");
        executeRenderQueueCommands(isSelectionLayer);
        log(LOGGER, " - Post- executeRenderQueueCommands()", currentTimeMillis);

        //Signal columns to free any unused resources
        if (!isSelectionLayer) {
            Stream.concat(bodyColumns.stream(),
                          floatingColumns.stream())
                    .filter(column -> column instanceof HasMultipleDOMElementResources)
                    .map(column -> (HasMultipleDOMElementResources) column)
                    .forEach(HasMultipleDOMElementResources::freeUnusedResources);
        }

        //Then render to the canvas
        currentTimeMillis = log(LOGGER, " - Pre- super.drawWithoutTransforms()");
        super.drawWithoutTransforms(context,
                                    alpha,
                                    bb);
        log(LOGGER, " - Post- super.drawWithoutTransforms()", currentTimeMillis);
    }

    private BaseGridRendererHelper.RenderingInformation prepare() {
        this.body = null;
        this.header = null;
        this.floatingBody = null;
        this.floatingHeader = null;
        this.bodySelections = null;
        this.headerSelections = null;
        this.floatingBodySelections = null;
        this.floatingHeaderSelections = null;
        this.boundary = null;
        this.selection = null;
        this.allColumns.clear();
        this.bodyColumns.clear();
        this.floatingColumns.clear();
        this.renderQueue.clear();

        //If there's no RenderingInformation the GridWidget is not visible
        long currentTimeMillis = log(LOGGER, " - Pre- getRenderingInformation()");
        final BaseGridRendererHelper.RenderingInformation renderingInformation = rendererHelper.getRenderingInformation();
        log(LOGGER, " - Post- getRenderingInformation()", currentTimeMillis);
        if (renderingInformation == null) {
            return null;
        }

        final BaseGridRendererHelper.RenderingBlockInformation bodyBlockInformation = renderingInformation.getBodyBlockInformation();
        final BaseGridRendererHelper.RenderingBlockInformation floatingBlockInformation = renderingInformation.getFloatingBlockInformation();
        final List<GridColumn<?>> allColumns = renderingInformation.getAllColumns();
        final List<GridColumn<?>> bodyColumns = bodyBlockInformation.getColumns();
        final List<GridColumn<?>> floatingColumns = floatingBlockInformation.getColumns();

        this.allColumns.addAll(allColumns);
        this.bodyColumns.addAll(bodyColumns);
        this.floatingColumns.addAll(floatingColumns);

        return renderingInformation;
    }

    private void destroyDOMElementResources() {
        for (GridColumn<?> column : model.getColumns()) {
            if (column.getColumnRenderer() instanceof HasDOMElementResources) {
                ((HasDOMElementResources) column.getColumnRenderer()).destroyResources();
            }
        }
    }

    private void makeRenderingCommands() {
        //Signal columns to attach or detach rendering support
        for (GridColumn<?> column : model.getColumns()) {
            if (bodyColumns.contains(column) || floatingColumns.contains(column)) {
                if (column instanceof HasMultipleDOMElementResources) {
                    ((HasMultipleDOMElementResources) column).initialiseResources();
                }
            } else if (column instanceof HasDOMElementResources) {
                ((HasDOMElementResources) column).destroyResources();
            }
        }

        //Draw if required
        if (bodyColumns.size() > 0) {

            boundary = new Group();

            drawHeader(renderingInformation);

            if (model.getRowCount() > 0) {
                drawBody(renderingInformation);
            }
        }

        final int minVisibleRowIndex = renderingInformation.getMinVisibleRowIndex();
        final int maxVisibleRowIndex = renderingInformation.getMaxVisibleRowIndex();
        final BaseGridRendererHelper.RenderingBlockInformation bodyBlockInformation = renderingInformation.getBodyBlockInformation();
        final BaseGridRendererHelper.RenderingBlockInformation floatingBlockInformation = renderingInformation.getFloatingBlockInformation();

        if (headerSelections != null) {
            addCommandToRenderQueue(headerSelections,
                                    renderSelectedRanges(bodyColumns,
                                                         bodyBlockInformation.getX(),
                                                         0,
                                                         model.getHeaderRowCount(),
                                                         bodyTransformer,
                                                         renderingInformation,
                                                         model.getSelectedHeaderCells(),
                                                         headerSelectionYOffsetStrategy,
                                                         headerSelectionHeightStrategy));
        }
        if (floatingHeaderSelections != null) {
            addCommandToRenderQueue(floatingHeaderSelections,
                                    renderSelectedRanges(floatingColumns,
                                                         floatingBlockInformation.getX(),
                                                         0,
                                                         model.getHeaderRowCount(),
                                                         floatingColumnsTransformer,
                                                         renderingInformation,
                                                         model.getSelectedHeaderCells(),
                                                         headerSelectionYOffsetStrategy,
                                                         headerSelectionHeightStrategy));
        }

        if (bodySelections != null) {
            addCommandToRenderQueue(bodySelections,
                                    renderSelectedRanges(bodyColumns,
                                                         bodyBlockInformation.getX(),
                                                         minVisibleRowIndex,
                                                         maxVisibleRowIndex,
                                                         bodyTransformer,
                                                         renderingInformation,
                                                         model.getSelectedCells(),
                                                         bodySelectionYOffsetStrategy,
                                                         bodySelectionHeightStrategy));
        }
        if (floatingBodySelections != null) {
            addCommandToRenderQueue(floatingBodySelections,
                                    renderSelectedRanges(floatingColumns,
                                                         floatingBlockInformation.getX(),
                                                         minVisibleRowIndex,
                                                         maxVisibleRowIndex,
                                                         floatingColumnsTransformer,
                                                         renderingInformation,
                                                         model.getSelectedCells(),
                                                         bodySelectionYOffsetStrategy,
                                                         bodySelectionHeightStrategy));
        }
        if (boundary != null) {
            addCommandToRenderQueue(boundary,
                                    renderGridBoundary(renderingInformation));
        }
        if (isSelected) {
            this.selection = new Group();
            addCommandToRenderQueue(selection,
                                    renderer.renderSelector(getWidth(),
                                                            getHeight(),
                                                            renderingInformation));
        }
    }

    private void layerRenderGroups() {
        //The order these are added ensures the parts overlap correctly
        if (body != null) {
            add(body);
        }
        if (bodySelections != null) {
            add(bodySelections);
        }
        if (header != null) {
            add(header);
        }
        if (headerSelections != null) {
            add(headerSelections);
        }

        if (floatingBody != null) {
            add(floatingBody);
        }
        if (floatingBodySelections != null) {
            add(floatingBodySelections);
        }
        if (floatingHeader != null) {
            add(floatingHeader);
        }
        if (floatingHeaderSelections != null) {
            add(floatingHeaderSelections);
        }

        if (boundary != null) {
            add(boundary);
        }

        //Include selection indicator if required
        if (selection != null) {
            add(selection);
        }
    }

    @Override
    public Group setVisible(final boolean visible) {
        if (!visible) {
            for (GridColumn<?> gc : getModel().getColumns()) {
                if (gc instanceof HasMultipleDOMElementResources) {
                    ((HasMultipleDOMElementResources) gc).destroyResources();
                }
            }
        }
        return super.setVisible(visible);
    }

    protected void drawHeader(final BaseGridRendererHelper.RenderingInformation renderingInformation) {
        final List<GridColumn<?>> allColumns = renderingInformation.getAllColumns();
        final BaseGridRendererHelper.RenderingBlockInformation bodyBlockInformation = renderingInformation.getBodyBlockInformation();
        final BaseGridRendererHelper.RenderingBlockInformation floatingBlockInformation = renderingInformation.getFloatingBlockInformation();

        final double headerX = bodyBlockInformation.getX();
        final double headerY = bodyBlockInformation.getHeaderY();
        final double floatingHeaderX = floatingBlockInformation.getX();
        final double floatingHeaderY = floatingBlockInformation.getHeaderY();

        //Add Header, if applicable
        final boolean addFixedHeader = renderingInformation.isFixedHeader();
        final boolean addFloatingHeader = renderingInformation.isFloatingHeader();

        if (addFixedHeader || addFloatingHeader) {
            //Render header for body columns, if required
            if (bodyColumns.size() > 0) {
                header = new Group();
                header.setX(headerX);
                headerSelections = new Group();
                headerSelections.setX(headerX).setY(headerY);
                addCommandsToRenderQueue(header,
                                         renderGridHeaderWidget(allColumns,
                                                                bodyColumns,
                                                                renderingInformation));

                if (addFloatingHeader) {
                    header.setY(headerY);
                }
            }

            //Render header for floating columns, if required
            if (floatingColumns.size() > 0) {
                floatingHeader = new Group();
                floatingHeader.setX(floatingHeaderX).setY(floatingHeaderY);
                floatingHeaderSelections = new Group();
                floatingHeaderSelections.setX(floatingHeaderX).setY(floatingHeaderY);
                addCommandsToRenderQueue(floatingHeader,
                                         renderGridHeaderWidget(floatingColumns,
                                                                floatingColumns,
                                                                renderingInformation));
            }
        }
    }

    protected void drawBody(final BaseGridRendererHelper.RenderingInformation renderingInformation) {
        final BaseGridRendererHelper.RenderingBlockInformation bodyBlockInformation = renderingInformation.getBodyBlockInformation();
        final BaseGridRendererHelper.RenderingBlockInformation floatingBlockInformation = renderingInformation.getFloatingBlockInformation();

        final double bodyX = bodyBlockInformation.getX();
        final double bodyY = bodyBlockInformation.getBodyY();
        final double floatingBodyX = floatingBlockInformation.getX();
        final double floatingBodyY = floatingBlockInformation.getBodyY();

        final int minVisibleRowIndex = renderingInformation.getMinVisibleRowIndex();
        final int maxVisibleRowIndex = renderingInformation.getMaxVisibleRowIndex();

        //Render body columns, if required
        if (bodyColumns.size() > 0) {
            body = new Group();
            body.setX(bodyX).setY(bodyY);
            bodySelections = new Group();
            bodySelections.setX(bodyX).setY(bodyY);

            addCommandsToRenderQueue(body,
                                     renderGridBodyWidget(bodyColumns,
                                                          bodyBlockInformation.getX(),
                                                          minVisibleRowIndex,
                                                          maxVisibleRowIndex,
                                                          bodyTransformer,
                                                          renderingInformation));
        }

        //Render floating columns, if required
        if (floatingColumns.size() > 0) {
            floatingBody = new Group();
            floatingBody.setX(floatingBodyX).setY(floatingBodyY);
            floatingBodySelections = new Group();
            floatingBodySelections.setX(floatingBodyX).setY(floatingBodyY);

            addCommandsToRenderQueue(floatingBody,
                                     renderGridBodyWidget(floatingColumns,
                                                          floatingBlockInformation.getX(),
                                                          minVisibleRowIndex,
                                                          maxVisibleRowIndex,
                                                          floatingColumnsTransformer,
                                                          renderingInformation));
        }
    }

    protected void addCommandToRenderQueue(final Group parent,
                                           final GridRenderer.RendererCommand command) {
        addCommandsToRenderQueue(parent, Collections.singletonList(command));
    }

    protected void addCommandsToRenderQueue(final Group parent,
                                            final List<GridRenderer.RendererCommand> commands) {
        renderQueue.add(new AbstractMap.SimpleEntry<>(parent, commands));
    }

    protected void executeRenderQueueCommands(final boolean isSelectionLayer) {
        renderQueue.stream()
                .forEach(p -> p.getValue()
                        .forEach(c -> c.execute(new GridRenderer.GridRendererContext() {
                            @Override
                            public Group getGroup() {
                                return p.getKey();
                            }

                            @Override
                            public boolean isSelectionLayer() {
                                return isSelectionLayer;
                            }
                        })));
    }

    /**
     * Render the Widget's Header and append to this Group.
     * @param allColumns All columns in the model.
     * @param blockColumns The columns to render for a block.
     */
    protected List<GridRenderer.RendererCommand> renderGridHeaderWidget(final List<GridColumn<?>> allColumns,
                                                                        final List<GridColumn<?>> blockColumns,
                                                                        final BaseGridRendererHelper.RenderingInformation renderingInformation) {
        final GridHeaderRenderContext context = new GridHeaderRenderContext(allColumns,
                                                                            blockColumns);
        return renderer.renderHeader(model,
                                     context,
                                     rendererHelper,
                                     renderingInformation);
    }

    /**
     * Render the Widget's Body and append to this Group.
     * @param blockColumns The columns to render.
     * @param absoluteColumnOffsetX Absolute offset from Grid's X co-ordinate to render first column in block.
     * @param minVisibleRowIndex The index of the first visible row.
     * @param maxVisibleRowIndex The index of the last visible row.
     * @param transformer SelectionTransformer in operation.
     */
    protected List<GridRenderer.RendererCommand> renderGridBodyWidget(final List<GridColumn<?>> blockColumns,
                                                                      final double absoluteColumnOffsetX,
                                                                      final int minVisibleRowIndex,
                                                                      final int maxVisibleRowIndex,
                                                                      final SelectionsTransformer transformer,
                                                                      final BaseGridRendererHelper.RenderingInformation renderingInformation) {
        final BaseGridRendererHelper.RenderingBlockInformation floatingBlockInformation = renderingInformation.getFloatingBlockInformation();
        final double floatingX = floatingBlockInformation.getX();
        final double floatingWidth = floatingBlockInformation.getWidth();

        final double clipMinY = getComputedLocation().getY() + (header == null ? 0.0 : header.getY() + getRenderer().getHeaderHeight());
        final double clipMinX = getComputedLocation().getX() + floatingX + floatingWidth;
        final GridBodyRenderContext context = new GridBodyRenderContext(getComputedLocation().getX(),
                                                                        getComputedLocation().getY(),
                                                                        absoluteColumnOffsetX,
                                                                        clipMinY,
                                                                        clipMinX,
                                                                        minVisibleRowIndex,
                                                                        maxVisibleRowIndex,
                                                                        blockColumns,
                                                                        getViewport().getTransform(),
                                                                        renderer,
                                                                        transformer);
        return renderer.renderBody(model,
                                   context,
                                   rendererHelper,
                                   renderingInformation);
    }

    /**
     * Render the selected ranges and append to the Body Group.
     * @param renderingInformation Calculated rendering information supporting rendering.
     * @return A Group containing the boundary.
     */
    protected GridRenderer.RendererCommand renderGridBoundary(final BaseGridRendererHelper.RenderingInformation renderingInformation) {
        double x = 0;
        final List<GridColumn<?>> allColumns = new ArrayList<>();
        final BaseGridRendererHelper.RenderingBlockInformation bodyBlockInformation = renderingInformation.getBodyBlockInformation();
        final BaseGridRendererHelper.RenderingBlockInformation floatingBlockInformation = renderingInformation.getFloatingBlockInformation();

        if (body != null || header != null) {
            allColumns.addAll(bodyColumns);
            x = bodyBlockInformation.getX();
        }
        if (floatingBody != null || floatingHeader != null) {
            allColumns.addAll(floatingColumns);
            x = floatingBlockInformation.getX();
        }
        final double headerYOffset = (header == null ? 0.0 : header.getY());
        final double headerRowsYOffset = renderingInformation.getHeaderRowsYOffset();
        final double y = headerRowsYOffset + headerYOffset;

        final double height = getHeight(renderingInformation) - headerRowsYOffset - headerYOffset;
        double width = rendererHelper.getWidth(allColumns);
        if (!floatingColumns.isEmpty()) {
            width = width - (floatingBlockInformation.getX() + rendererHelper.getWidth(floatingColumns) - bodyBlockInformation.getX());
        }

        final GridBoundaryRenderContext context = new GridBoundaryRenderContext(x,
                                                                                y,
                                                                                width,
                                                                                height);

        return renderer.renderGridBoundary(context);
    }

    /**
     * Render the selected ranges and append to the Body Group.
     * @param blockColumns The columns to render.
     * @param absoluteColumnOffsetX Absolute offset from Grid's X co-ordinate to render first column in block.
     * @param minVisibleRowIndex The index of the first visible row.
     * @param maxVisibleRowIndex The index of the last visible row.
     * @param transformer SelectionTransformer in operation.
     * @return
     */
    protected GridRenderer.RendererCommand renderSelectedRanges(final List<GridColumn<?>> blockColumns,
                                                                final double absoluteColumnOffsetX,
                                                                final int minVisibleRowIndex,
                                                                final int maxVisibleRowIndex,
                                                                final SelectionsTransformer transformer,
                                                                final BaseGridRendererHelper.RenderingInformation renderingInformation,
                                                                final List<GridData.SelectedCell> selectedCells,
                                                                final BiFunction<SelectedRange, Integer, Double> selectedCellsYOffsetStrategy,
                                                                final Function<SelectedRange, Double> selectedCellsHeightStrategy) {
        final BaseGridRendererHelper.RenderingBlockInformation floatingBlockInformation = renderingInformation.getFloatingBlockInformation();
        final double floatingX = floatingBlockInformation.getX();
        final double floatingWidth = floatingBlockInformation.getWidth();

        final double clipMinY = getComputedLocation().getY() + (header == null ? 0.0 : header.getY() + getRenderer().getHeaderHeight());
        final double clipMinX = getComputedLocation().getX() + floatingX + floatingWidth;
        final GridBodyRenderContext context = new GridBodyRenderContext(getComputedLocation().getX(),
                                                                        getComputedLocation().getY(),
                                                                        absoluteColumnOffsetX,
                                                                        clipMinY,
                                                                        clipMinX,
                                                                        minVisibleRowIndex,
                                                                        maxVisibleRowIndex,
                                                                        blockColumns,
                                                                        getViewport().getTransform(),
                                                                        renderer,
                                                                        transformer);
        return renderer.renderSelectedCells(model,
                                            context,
                                            rendererHelper,
                                            selectedCells,
                                            selectedCellsYOffsetStrategy,
                                            selectedCellsHeightStrategy);
    }

    @Override
    public void onNodeMouseClick(final NodeMouseClickEvent event) {
        fireEvent(event);
    }

    @Override
    public boolean onGroupingToggle(final double cellX,
                                    final double cellY,
                                    final double cellWidth,
                                    final double cellHeight) {
        return renderer.onGroupingToggle(cellX,
                                         cellY,
                                         cellWidth,
                                         cellHeight);
    }

    @Override
    public boolean selectCell(final Point2D rp,
                              final boolean isShiftKeyDown,
                              final boolean isControlKeyDown) {
        return cellSelectionManager.selectCell(rp,
                                               isShiftKeyDown,
                                               isControlKeyDown);
    }

    @Override
    public boolean selectCell(final int uiRowIndex,
                              final int uiColumnIndex,
                              final boolean isShiftKeyDown,
                              final boolean isControlKeyDown) {
        return cellSelectionManager.selectCell(uiRowIndex,
                                               uiColumnIndex,
                                               isShiftKeyDown,
                                               isControlKeyDown);
    }

    @Override
    public boolean selectHeaderCell(final Point2D rp,
                                    final boolean isShiftKeyDown,
                                    final boolean isControlKeyDown) {
        return cellSelectionManager.selectHeaderCell(rp,
                                                     isShiftKeyDown,
                                                     isControlKeyDown);
    }

    @Override
    public boolean selectHeaderCell(final int uiHeaderRowIndex,
                                    final int uiHeaderColumnIndex,
                                    final boolean isShiftKeyDown,
                                    final boolean isControlKeyDown) {
        return cellSelectionManager.selectHeaderCell(uiHeaderRowIndex,
                                                     uiHeaderColumnIndex,
                                                     isShiftKeyDown,
                                                     isControlKeyDown);
    }

    @Override
    public boolean adjustSelection(final SelectionExtension direction,
                                   final boolean isShiftKeyDown) {
        return cellSelectionManager.adjustSelection(direction,
                                                    isShiftKeyDown);
    }

    @Override
    public boolean startEditingCell(final int uiRowIndex,
                                    final int uiColumnIndex) {
        return cellSelectionManager.startEditingCell(uiRowIndex,
                                                     uiColumnIndex);
    }

    @Override
    public boolean startEditingCell(final Point2D rp) {
        return cellSelectionManager.startEditingCell(rp);
    }

    @Override
    public boolean showContextMenuForHeader(final int uiHeaderRowIndex,
                                            final int uiHeaderColumnIndex) {
        // no operation by default
        return false;
    }

    @Override
    public boolean showContextMenuForCell(final int uiRowIndex,
                                          final int uiColumnIndex) {
        // no operation by default
        return false;
    }
}
