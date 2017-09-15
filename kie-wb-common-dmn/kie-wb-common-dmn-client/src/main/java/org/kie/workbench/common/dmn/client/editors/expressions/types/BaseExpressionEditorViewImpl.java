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

package org.kie.workbench.common.dmn.client.editors.expressions.types;

import java.util.Optional;

import com.ait.lienzo.client.core.types.Transform;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.client.widgets.grid.BoundaryTransformMediator;
import org.kie.workbench.common.dmn.client.widgets.grid.model.BaseUIModelMapper;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.DefaultGridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLienzoPanel;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.TransformMediator;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.impl.RestrictedMousePanMediator;

public abstract class BaseExpressionEditorViewImpl<P extends BaseExpressionEditorView.Editor, E extends Expression> implements BaseExpressionEditorView<P, E> {

    private static final double VP_SCALE = 1.0;

    public static final int LIENZO_PANEL_WIDTH = 1000;

    public static final int LIENZO_PANEL_HEIGHT = 450;

    protected TranslationService ts;
    protected SessionManager sessionManager;
    protected SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    protected Editor editor;

    protected GridWidget gridWidget;

    protected Optional<HasName> hasName = Optional.empty();
    protected Optional<E> expression = Optional.empty();

    protected TransformMediator defaultTransformMediator;

    protected final GridLienzoPanel gridPanel = defaultGridPanel();

    protected final DefaultGridLayer gridLayer = defaultGridLayer();

    protected final RestrictedMousePanMediator mousePanMediator = new RestrictedMousePanMediator(gridLayer);

    protected BaseUIModelMapper<E> uiModelMapper;

    public GridLienzoPanel defaultGridPanel() {
        return new GridLienzoPanel(LIENZO_PANEL_WIDTH,
                                   LIENZO_PANEL_HEIGHT) {

            @Override
            public void onResize() {
                Scheduler.get().scheduleDeferred(() -> {
                    updatePanelSize();

                    final TransformMediator restriction = mousePanMediator.getTransformMediator();
                    final Transform transform = restriction.adjust(gridLayer.getViewport().getTransform(),
                                                                   gridLayer.getVisibleBounds());
                    gridLayer.getViewport().setTransform(transform);
                    gridLayer.draw();
                });
            }
        };
    }

    public DefaultGridLayer defaultGridLayer() {
        return new DefaultGridLayer() {

            @Override
            public TransformMediator getDefaultTransformMediator() {
                return defaultTransformMediator;
            }
        };
    }

    protected BaseExpressionEditorViewImpl(final TranslationService ts,
                                           final SessionManager sessionManager,
                                           final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager) {
        this.ts = ts;
        this.sessionManager = sessionManager;
        this.sessionCommandManager = sessionCommandManager;
        this.uiModelMapper = makeUiModelMapper();
        setupGridPanel();
        setupGridWidget();
        setupGridWidgetPanControl();
    }

    protected void setupGridPanel() {
        final Transform transform = new Transform().scale(VP_SCALE);
        gridPanel.getViewport().setTransform(transform);
        gridPanel.add(gridLayer);

        gridPanel.getElement().setId("dmn_container_" + Document.get().createUniqueId());
    }

    protected void setupGridWidget() {
        gridWidget = makeGridWidget();
        gridLayer.add(gridWidget);
        gridLayer.select(gridWidget);
        gridLayer.enterPinnedMode(gridWidget,
                                  () -> {/*Nothing*/});
    }

    protected void setupGridWidgetPanControl() {
        defaultTransformMediator = new BoundaryTransformMediator(gridWidget);
        mousePanMediator.setTransformMediator(defaultTransformMediator);
        mousePanMediator.setBatchDraw(true);
        gridPanel.getViewport().getMediators().push(mousePanMediator);
    }

    @Override
    public void setHasName(final Optional<HasName> hasName) {
        this.hasName = hasName;
        this.hasName.ifPresent(hn -> gridLayer.batch());
    }

    @Override
    public HandlerRegistration addContextMenuHandler(final ContextMenuHandler handler) {
        return gridPanel.addDomHandler(handler,
                                       ContextMenuEvent.getType());
    }

    @Override
    public HandlerRegistration addKeyDownHandler(final KeyDownHandler handler) {
        return gridPanel.addKeyDownHandler(handler);
    }

    @Override
    public HandlerRegistration addMouseDownHandler(final MouseDownHandler handler) {
        return gridPanel.addMouseDownHandler(handler);
    }

    @Override
    public void fireEvent(final GwtEvent<?> event) {
        gridPanel.fireEvent(event);
    }

    @Override
    public void onResize() {
        gridPanel.onResize();
    }
}
