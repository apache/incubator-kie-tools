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
package org.uberfire.ext.wires.core.grids.client.widget.layer.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ait.lienzo.client.core.event.NodeMouseDownEvent;
import com.ait.lienzo.client.core.event.NodeMouseMoveEvent;
import com.ait.lienzo.client.core.event.NodeMouseUpEvent;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Line;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.shared.core.types.ColorName;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.AbsolutePanel;
import org.uberfire.ext.wires.core.grids.client.model.Bounds;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseBounds;
import org.uberfire.ext.wires.core.grids.client.widget.dnd.GridWidgetDnDHandlersState;
import org.uberfire.ext.wires.core.grids.client.widget.dnd.GridWidgetDnDMouseDownHandler;
import org.uberfire.ext.wires.core.grids.client.widget.dnd.GridWidgetDnDMouseMoveHandler;
import org.uberfire.ext.wires.core.grids.client.widget.dnd.GridWidgetDnDMouseUpHandler;
import org.uberfire.ext.wires.core.grids.client.widget.dom.single.HasSingletonDOMElementResource;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.animation.GridWidgetScrollIntoViewAnimation;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.GridWidgetConnector;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridWidgetRegistry;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.TransformMediator;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.impl.BoundaryTransformMediator;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.impl.DefaultPinnedModeManager;

/**
 * Default implementation of GridLayer
 */
public class DefaultGridLayer extends Layer implements GridLayer,
                                                       GridWidgetRegistry {

    //This is helpful when debugging rendering issues to set the bounds smaller than the Viewport
    private static final int PADDING = 0;
    private final GridWidgetDnDMouseDownHandler mouseDownHandler;
    private final GridWidgetDnDMouseMoveHandler mouseMoveHandler;
    private final GridWidgetDnDMouseUpHandler mouseUpHandler;
    private final GridWidgetDnDHandlersState state = new GridWidgetDnDHandlersState();
    private final TransformMediator defaultTransformMediator = new BoundaryTransformMediator();
    private final DefaultPinnedModeManager pinnedModeManager = new DefaultPinnedModeManager(this);
    private Set<GridWidget> explicitGridWidgets = new LinkedHashSet<>();
    private Set<GridWidget> registeredGridWidgets = new LinkedHashSet<>();
    private Map<GridWidgetConnector, Line> gridWidgetConnectors = new HashMap<>();
    private final GridLayerRedrawManager.PrioritizedCommand REDRAW = new GridLayerRedrawManager.PrioritizedCommand(Integer.MIN_VALUE) {
        @Override
        public void execute() {
            DefaultGridLayer.this.draw();
        }
    };
    private AbsolutePanel domElementContainer;
    private Bounds bounds;

    public DefaultGridLayer() {
        this.bounds = new BaseBounds(0,
                                     0,
                                     0,
                                     0);

        //Column DnD handlers
        this.mouseDownHandler = getGridWidgetDnDMouseDownHandler();
        this.mouseMoveHandler = getGridWidgetDnDMouseMoveHandler();
        this.mouseUpHandler = getGridWidgetDnDMouseUpHandler();
        addNodeMouseDownHandler(mouseDownHandler);
        addNodeMouseMoveHandler(mouseMoveHandler);
        addNodeMouseUpHandler(mouseUpHandler);

        //Destroy SingletonDOMElements on MouseDownEvents to ensure they're hidden:-
        // 1) When moving columns
        // 2) When resizing columns
        // 3) When the User clicks outside of a GridWidget
        // We do this rather than setFocus on GridPanel as the FocusImplSafari implementation of
        // FocusPanel sets focus at unpredictable times which can lead to SingletonDOMElements
        // loosing focus after they've been attached to the DOM and hence disappearing.
        addNodeMouseDownHandler((event) -> {
            for (GridWidget gridWidget : getGridWidgets()) {
                for (GridColumn<?> gridColumn : gridWidget.getModel().getColumns()) {
                    if (gridColumn instanceof HasSingletonDOMElementResource) {
                        ((HasSingletonDOMElementResource) gridColumn).flush();
                        ((HasSingletonDOMElementResource) gridColumn).destroyResources();
                        batch();
                    }
                }
            }
        });
    }

    protected GridWidgetDnDMouseDownHandler getGridWidgetDnDMouseDownHandler() {
        return new GridWidgetDnDMouseDownHandler(this,
                                                 state);
    }

    protected GridWidgetDnDMouseMoveHandler getGridWidgetDnDMouseMoveHandler() {
        return new GridWidgetDnDMouseMoveHandler(this,
                                                 state);
    }

    protected GridWidgetDnDMouseUpHandler getGridWidgetDnDMouseUpHandler() {
        return new GridWidgetDnDMouseUpHandler(this,
                                               state);
    }

    @Override
    public void onNodeMouseDown(final NodeMouseDownEvent event) {
        mouseDownHandler.onNodeMouseDown(event);
    }

    @Override
    public void onNodeMouseMove(final NodeMouseMoveEvent event) {
        mouseMoveHandler.onNodeMouseMove(event);
    }

    @Override
    public void onNodeMouseUp(final NodeMouseUpEvent event) {
        mouseUpHandler.onNodeMouseUp(event);
    }

    @Override
    public Layer draw() {
        //Clear all transient registrations added as the Layer is rendered
        registeredGridWidgets.clear();
        registeredGridWidgets.addAll(explicitGridWidgets);

        //We use Layer.batch() to ensure rendering is tied to the browser's requestAnimationFrame()
        //however this calls back into Layer.draw() so update dependent Shapes here.
        updateGridWidgetConnectors();
        return super.draw();
    }

    @Override
    public Layer batch() {
        return batch(REDRAW);
    }

    @Override
    public Layer batch(final GridLayerRedrawManager.PrioritizedCommand command) {
        GridLayerRedrawManager.get().schedule(command);
        return this;
    }

    @Override
    public Set<IPrimitive<?>> getGridWidgetConnectors() {
        return Collections.unmodifiableSet(new HashSet<>(gridWidgetConnectors.values()));
    }

    private void updateGridWidgetConnectors() {
        for (Map.Entry<GridWidgetConnector, Line> e : gridWidgetConnectors.entrySet()) {
            final GridWidgetConnector connector = e.getKey();
            final Line line = e.getValue();
            final GridColumn<?> sourceGridColumn = connector.getSourceColumn();
            final GridColumn<?> targetGridColumn = connector.getTargetColumn();
            final GridWidget sourceGridWidget = getLinkedGridWidget(sourceGridColumn);
            final GridWidget targetGridWidget = getLinkedGridWidget(targetGridColumn);

            final Point2D sp = new Point2D(sourceGridWidget.getX() + sourceGridWidget.getWidth() / 2,
                                           sourceGridWidget.getY() + sourceGridWidget.getHeight() / 2);
            final Point2D ep = new Point2D(targetGridWidget.getX() + targetGridWidget.getWidth() / 2,
                                           targetGridWidget.getY() + targetGridWidget.getHeight() / 2);

            line.setPoints(new Point2DArray(sp,
                                            ep));
        }
    }

    /**
     * Add a child to this Layer. If the child is a GridWidget then also add
     * a Connector between the Grid Widget and any "linked" GridWidgets.
     * @param child Primitive to add to the Layer
     * @return The Layer
     */
    @Override
    public Layer add(final IPrimitive<?> child) {
        addGridWidget(child);
        return super.add(child);
    }

    private void addGridWidget(final IPrimitive<?> child,
                               final IPrimitive<?>... children) {
        final List<IPrimitive<?>> all = new ArrayList<IPrimitive<?>>();
        all.add(child);
        all.addAll(Arrays.asList(children));
        for (IPrimitive<?> c : all) {
            if (c instanceof GridWidget) {
                final GridWidget gridWidget = (GridWidget) c;
                register(gridWidget);
                explicitGridWidgets.add(gridWidget);
                addGridWidgetConnectors();
            }
        }
    }

    @Override
    public void register(final GridWidget gridWidget) {
        registeredGridWidgets.add(gridWidget);
    }

    @Override
    public void refreshGridWidgetConnectors() {
        for (Line line : gridWidgetConnectors.values()) {
            remove(line);
        }
        gridWidgetConnectors.clear();
        addGridWidgetConnectors();
    }

    private void addGridWidgetConnectors() {
        for (GridWidget gridWidget : explicitGridWidgets) {
            final GridData gridModel = gridWidget.getModel();
            for (GridColumn<?> gridColumn : gridModel.getColumns()) {
                if (gridColumn.isVisible()) {
                    if (gridColumn.isLinked()) {
                        final GridWidget linkedGridWidget = getLinkedGridWidget(gridColumn.getLink());
                        if (linkedGridWidget != null) {
                            final Point2D sp = new Point2D(gridWidget.getX() + gridWidget.getWidth() / 2,
                                                           gridWidget.getY() + gridWidget.getHeight() / 2);
                            final Point2D ep = new Point2D(linkedGridWidget.getX() + linkedGridWidget.getWidth() / 2,
                                                           linkedGridWidget.getY() + linkedGridWidget.getHeight() / 2);

                            final GridWidgetConnector connector = new GridWidgetConnector(gridColumn,
                                                                                          gridColumn.getLink());

                            if (!gridWidgetConnectors.containsKey(connector)) {
                                final Line line = new Line(sp,
                                                           ep)
                                        .setVisible(!isGridPinned())
                                        .setStrokeColor(ColorName.DARKGRAY)
                                        .setFillColor(ColorName.TAN)
                                        .setStrokeWidth(2.0);
                                gridWidgetConnectors.put(connector,
                                                         line);
                                super.add(line);
                                line.moveToBottom();
                            }
                        }
                    }
                }
            }
        }
    }

    private GridWidget getLinkedGridWidget(final GridColumn<?> linkedGridColumn) {
        GridWidget linkedGridWidget = null;
        for (GridWidget gridWidget : explicitGridWidgets) {
            final GridData gridModel = gridWidget.getModel();
            if (gridModel.getColumns().contains(linkedGridColumn)) {
                linkedGridWidget = gridWidget;
                break;
            }
        }
        return linkedGridWidget;
    }

    /**
     * Add a child and other children to this Layer. If the child or any children is a GridWidget
     * then also add a Connector between the Grid Widget and any "linked" GridWidgets.
     * @param child Primitive to add to the Layer
     * @param children Additional primitive(s) to add to the Layer
     * @return The Layer
     */
    @Override
    public Layer add(final IPrimitive<?> child,
                     final IPrimitive<?>... children) {
        addGridWidget(child,
                      children);
        return super.add(child,
                         children);
    }

    /**
     * Remove a child from this Layer. if the child is a GridWidget also remove
     * any Connectors that have been added between the GridWidget being removed
     * and any of GridWidgets.
     * @param child Primitive to remove from the Layer
     * @return The Layer
     */
    @Override
    public Layer remove(final IPrimitive<?> child) {
        removeGridWidget(child);
        return super.remove(child);
    }

    private void removeGridWidget(final IPrimitive<?> child,
                                  final IPrimitive<?>... children) {
        final List<IPrimitive<?>> all = new ArrayList<IPrimitive<?>>();
        all.add(child);
        all.addAll(Arrays.asList(children));
        for (IPrimitive<?> c : all) {
            if (c instanceof GridWidget) {
                final GridWidget gridWidget = (GridWidget) c;
                deregister(gridWidget);
                explicitGridWidgets.remove(gridWidget);
                removeGridWidgetConnectors(gridWidget);
            }
        }
    }

    @Override
    public void deregister(final GridWidget gridWidget) {
        registeredGridWidgets.remove(gridWidget);
    }

    private void removeGridWidgetConnectors(final GridWidget gridWidget) {
        final GridData gridModel = gridWidget.getModel();
        final List<GridWidgetConnector> removedConnectors = new ArrayList<GridWidgetConnector>();
        for (Map.Entry<GridWidgetConnector, Line> e : gridWidgetConnectors.entrySet()) {
            if (gridModel.getColumns().contains(e.getKey().getSourceColumn()) || gridModel.getColumns().contains(e.getKey().getTargetColumn())) {
                remove(e.getValue());
                removedConnectors.add(e.getKey());
            }
        }
        //Remove Connectors from HashMap after iteration of EntrySet to avoid ConcurrentModificationException
        for (GridWidgetConnector c : removedConnectors) {
            gridWidgetConnectors.remove(c);
        }
    }

    @Override
    public Layer removeAll() {
        explicitGridWidgets.clear();
        gridWidgetConnectors.clear();
        registeredGridWidgets.clear();
        return super.removeAll();
    }

    @Override
    public void select(final GridWidget selectedGridWidget) {
        boolean selectionChanged = false;
        for (GridWidget gridWidget : getGridWidgets()) {
            if (gridWidget.isSelected()) {
                if (!gridWidget.equals(selectedGridWidget)) {
                    selectionChanged = true;
                    gridWidget.deselect();
                }
            } else if (gridWidget.equals(selectedGridWidget)) {
                selectionChanged = true;
                gridWidget.select();
            }
        }
        if (selectionChanged) {
            batch();
        }
    }

    @Override
    public void selectLinkedColumn(final GridColumn<?> selectedGridColumn) {
        final GridWidget gridWidget = getLinkedGridWidget(selectedGridColumn);
        if (gridWidget == null) {
            return;
        }

        if (isGridPinned()) {
            flipToGridWidget(gridWidget);
        } else {
            scrollToGridWidget(gridWidget);
        }
    }

    @Override
    public void flipToGridWidget(final GridWidget gridWidget) {
        if (!isGridPinned()) {
            return;
        }
        for (GridWidget gw : explicitGridWidgets) {
            gw.setAlpha(gw.equals(gridWidget) ? 1.0 : 0.0);
            gw.setVisible(gw.equals(gridWidget));
        }

        final Point2D translation = new Point2D(gridWidget.getX(),
                                                gridWidget.getY()).mul(-1.0);
        final Viewport vp = gridWidget.getViewport();
        final Transform transform = vp.getTransform();
        transform.reset();
        transform.translate(translation.getX(),
                            translation.getY());

        updatePinnedContext(gridWidget);

        batch(new GridLayerRedrawManager.PrioritizedCommand(0) {
            @Override
            public void execute() {
                select(gridWidget);
            }
        });
    }

    @Override
    public void scrollToGridWidget(final GridWidget gridWidget) {
        if (isGridPinned()) {
            return;
        }
        final GridWidgetScrollIntoViewAnimation a = new GridWidgetScrollIntoViewAnimation(gridWidget,
                                                                                          new Command() {
                                                                                              @Override
                                                                                              public void execute() {
                                                                                                  select(gridWidget);
                                                                                              }
                                                                                          });
        a.run();
    }

    @Override
    public Set<GridWidget> getGridWidgets() {
        return Collections.unmodifiableSet(registeredGridWidgets);
    }

    @Override
    public void enterPinnedMode(final GridWidget gridWidget,
                                final Command onStartCommand) {
        pinnedModeManager.enterPinnedMode(gridWidget,
                                          onStartCommand);
    }

    @Override
    public void exitPinnedMode(final Command onCompleteCommand) {
        pinnedModeManager.exitPinnedMode(onCompleteCommand);
    }

    @Override
    public void updatePinnedContext(final GridWidget gridWidget) throws IllegalStateException {
        pinnedModeManager.updatePinnedContext(gridWidget);
    }

    @Override
    public PinnedContext getPinnedContext() {
        return pinnedModeManager.getPinnedContext();
    }

    @Override
    public boolean isGridPinned() {
        return pinnedModeManager.isGridPinned();
    }

    @Override
    public TransformMediator getDefaultTransformMediator() {
        return defaultTransformMediator;
    }

    @Override
    public void addOnEnterPinnedModeCommand(final Command command) {
        getPinnedModeManager().addOnEnterPinnedModeCommand(command);
    }

    @Override
    public void addOnExitPinnedModeCommand(final Command command) {
        getPinnedModeManager().addOnExitPinnedModeCommand(command);
    }

    DefaultPinnedModeManager getPinnedModeManager() {
        return pinnedModeManager;
    }

    @Override
    public Bounds getVisibleBounds() {
        updateVisibleBounds();
        return bounds;
    }

    private void updateVisibleBounds() {
        final Viewport viewport = getViewport();
        Transform transform = viewport.getTransform();
        if (transform == null) {
            viewport.setTransform(transform = new Transform());
        }
        final double x = (PADDING - transform.getTranslateX()) / transform.getScaleX();
        final double y = (PADDING - transform.getTranslateY()) / transform.getScaleY();
        bounds.setX(x);
        bounds.setY(y);
        bounds.setHeight(Math.max(0,
                                  (viewport.getHeight() - PADDING * 2) / transform.getScaleX()));
        bounds.setWidth(Math.max(0,
                                 (viewport.getWidth() - PADDING * 2) / transform.getScaleY()));
    }

    @Override
    public GridWidgetDnDHandlersState getGridWidgetHandlersState() {
        return state;
    }

    @Override
    public AbsolutePanel getDomElementContainer() {
        return domElementContainer;
    }

    @Override
    public void setDomElementContainer(final AbsolutePanel domElementContainer) {
        this.domElementContainer = domElementContainer;
    }
}
