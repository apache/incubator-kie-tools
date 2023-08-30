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


package org.kie.workbench.common.stunner.client.lienzo.canvas.controls;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.wires.DefaultSelectionListener;
import com.ait.lienzo.client.core.shape.wires.SelectionListener;
import com.ait.lienzo.client.core.shape.wires.SelectionManager;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.tools.client.event.HandlerRegistration;
import elemental2.dom.MouseEvent;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.WiresConnectorView;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.WiresShapeView;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.AbstractSelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.MapSelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.MultipleSelection;
import org.kie.workbench.common.stunner.core.client.canvas.event.ShapeLocationsChangedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.graph.Element;

/**
 * Default implementation of a {@link SelectionControl} that supports selection of multiple {@link Element}.
 *
 * @param <H> {@link AbstractCanvasHandler}
 */
@Dependent
@MultipleSelection
@Default
public class LienzoMultipleSelectionControl<H extends AbstractCanvasHandler>
        extends AbstractSelectionControl<H> {

    private final CursoredSelectionShapeProvider selectionShapeProvider;

    @Inject
    public LienzoMultipleSelectionControl(final Event<CanvasSelectionEvent> canvasSelectionEvent,
                                          final Event<CanvasClearSelectionEvent> clearSelectionEvent) {
        super(canvasSelectionEvent,
              clearSelectionEvent);
        this.selectionShapeProvider = new CursoredSelectionShapeProvider(this::getCanvasHandler);
    }

    LienzoMultipleSelectionControl(final MapSelectionControl<H> selectionControl,
                                   final Event<CanvasSelectionEvent> canvasSelectionEvent,
                                   final Event<CanvasClearSelectionEvent> clearSelectionEvent,
                                   final CursoredSelectionShapeProvider selectionShapeProvider) {
        super(selectionControl,
              canvasSelectionEvent,
              clearSelectionEvent);
        this.selectionShapeProvider = selectionShapeProvider;
    }

    @Override
    protected void onEnable(final H canvasHandler) {
        super.onEnable(canvasHandler);
        getWiresManager()
                .enableSelectionManager()
                .setSelectionShapeProvider(selectionShapeProvider)
                .setSelectionListener(selectionListener);
    }

    private final SelectionListener selectionListener = new SelectionListener() {

        private final DefaultSelectionListener defaultSelectionListener = new DefaultSelectionListener();

        @Override
        public void onChanged(final SelectionManager.SelectedItems selectedItems) {
            final SelectionManager.ChangedItems changedItems = selectedItems.getChanged();
            final int added = selectedItems.getChanged().addedSize();
            final int removed = selectedItems.getChanged().removedSize();
            if (added > 0 || removed > 0) {
                List<String> deselectList = new ArrayList<>(shapesToIdentifiers(changedItems.getRemovedShapes().toList()));
                deselectList.addAll(shapesToIdentifiers(changedItems.getRemovedConnectors().toList()));
                getSelectionControl().deselect(deselectList);
                List<String> selectList = new ArrayList<>(shapesToIdentifiers(changedItems.getAddedShapes().toList()));
                selectList.addAll(shapesToIdentifiers(changedItems.getAddedConnectors().toList()));
                getSelectionControl().select(selectList);
                defaultSelectionListener.onChanged(selectedItems);

                if (getSelectionControl().getSelectedItems().isEmpty()) {
                    canvasClearSelectionEvent.fire(new CanvasClearSelectionEvent(getCanvasHandler()));
                }
            }
        }
    };

    private void rebuildSelectionArea() {
        if (null != selectionShapeProvider.getShape()) {
            getSelectionManager().getSelectedItems().rebuildBoundingBox();
            getSelectionManager().drawSelectionShapeForSelection();
        }
    }

    protected void onShapeLocationsChanged(@Observes ShapeLocationsChangedEvent event) {
        if (isSameCanvas(event.getCanvasHandler()) && areSelected(event.getUuids())) {
            rebuildSelectionArea();
        }
    }

    private boolean isSameCanvas(final CanvasHandler handler) {
        CanvasHandler canvasHandler = getCanvasHandler();
        return null != canvasHandler && canvasHandler.equals(handler);
    }

    private boolean areSelected(Collection<String> uuids) {
        Collection<String> selectedItems = getSelectedItems();

        if (uuids == null) {
            return false;
        }

        return uuids.stream().anyMatch(selectedItems::contains);
    }

    @Override
    protected void onSelect(final Collection<String> uuids) {
        super.onSelect(uuids);
        final Collection<ShapeView> shapeViews = uuids.stream()
                .map(uuid -> getCanvasHandler().getCanvas().getShape(uuid))
                .filter(Objects::nonNull)
                .map(org.kie.workbench.common.stunner.core.client.shape.Shape::getShapeView)
                .collect(Collectors.toList());
        shapeViews.stream()
                .filter(view -> view instanceof WiresShapeView)
                .forEach(view -> getSelectionManager().getSelectedItems().add((WiresShape) view));
        shapeViews.stream()
                .filter(view -> view instanceof WiresConnectorView)
                .forEach(view -> getSelectionManager().getSelectedItems().add((WiresConnector) view));
    }

    @Override
    protected void onClearSelection() {
        super.onClearSelection();
        if (Objects.nonNull(getSelectionControl().getCanvasHandler())) {
            getSelectionManager().clearSelection();
        }
    }

    @Override
    protected void onDestroy() {
        getSelectionManager().destroy();
        selectionShapeProvider.destroy();
        super.onDestroy();
    }

    private AbstractCanvasHandler getCanvasHandler() {
        return getSelectionControl().getCanvasHandler();
    }

    private WiresManager getWiresManager() {
        return ((WiresCanvas) getSelectionControl().getCanvasHandler().getCanvas())
                .getWiresManager();
    }

    private SelectionManager getSelectionManager() {
        return getWiresManager().getSelectionManager();
    }

    void onCanvasSelection(final @Observes CanvasSelectionEvent event) {
        Objects.requireNonNull(event, "Parameter named 'event' should be not null!");
        if (Objects.equals(getCanvasHandler(), event.getCanvasHandler())) {
            selectionShapeProvider.moveShapeToTop();
        }
    }

    /**
     * As in any other stunner draggable shape, the mouse cursor is being
     * changed for targeting different operations, This selection shape provider just reuses the
     * default one from Lienzo but adds the cursor changing behavior on top of it.
     */
    static class CursoredSelectionShapeProvider
            implements SelectionManager.SelectionShapeProvider<CursoredSelectionShapeProvider> {

        private final SelectionManager.SelectionShapeProvider provider;
        private final Supplier<AbstractCanvasHandler> canvasHandlerSupplier;
        private HandlerRegistration mouseEnterHandlerReg;
        private HandlerRegistration mouseExitHandlerReg;

        private CursoredSelectionShapeProvider(final Supplier<AbstractCanvasHandler> canvasHandlerSupplier) {
            this(new SelectionManager.RectangleSelectionProvider(),
                 canvasHandlerSupplier);
        }

        CursoredSelectionShapeProvider(final SelectionManager.SelectionShapeProvider provider,
                                       final Supplier<AbstractCanvasHandler> canvasHandlerSupplier) {
            this.provider = provider;
            this.canvasHandlerSupplier = canvasHandlerSupplier;
        }

        @Override
        public Shape<?> getShape() {
            return provider.getShape();
        }

        @Override
        public CursoredSelectionShapeProvider build() {
            provider.build();
            clearHandlerRegs();
            mouseEnterHandlerReg =
                    provider.getShape()
                            .addNodeMouseEnterHandler(event -> canvasHandlerSupplier.get().getAbstractCanvas().getView().setCursor(AbstractCanvas.Cursors.MOVE));
            mouseExitHandlerReg =
                    provider.getShape()
                            .addNodeMouseExitHandler(event -> canvasHandlerSupplier.get().getAbstractCanvas().getView().setCursor(AbstractCanvas.Cursors.DEFAULT));
            return this;
        }

        @Override
        public CursoredSelectionShapeProvider setLocation(final Point2D location) {
            provider.setLocation(location);
            return this;
        }

        @Override
        public CursoredSelectionShapeProvider setSize(final double width,
                                                      final double height) {
            provider.setSize(width,
                             height);
            moveShapeToTop();
            return this;
        }

        @Override
        public boolean isMultipleSelection(MouseEvent event) {
            return provider.isMultipleSelection(event);
        }

        @Override
        public CursoredSelectionShapeProvider clear() {
            clearHandlerRegs();
            provider.clear();
            return this;
        }

        public void destroy() {
            clear();
        }

        public CursoredSelectionShapeProvider moveShapeToTop() {
            if (null != getShape()) {
                getShape().moveToTop();
            }
            return this;
        }

        private void clearHandlerRegs() {
            if (null != mouseEnterHandlerReg) {
                mouseEnterHandlerReg.removeHandler();
            }
            if (null != mouseExitHandlerReg) {
                mouseExitHandlerReg.removeHandler();
            }

            mouseExitHandlerReg = null;
            mouseEnterHandlerReg = null;
        }
    }
}
