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


package org.kie.workbench.common.stunner.client.lienzo.components.toolbox.actions;

import java.util.Optional;
import java.util.function.Consumer;

import javax.enterprise.event.Observes;

import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseMoveEvent;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.shape.toolbox.items.ButtonItem;
import com.ait.lienzo.client.core.shape.toolbox.items.decorator.BoxDecorator;
import com.ait.lienzo.client.core.shape.toolbox.items.impl.ToolboxFactory;
import com.ait.lienzo.client.core.shape.toolbox.items.impl.WiresShapeToolbox;
import com.ait.lienzo.client.core.shape.toolbox.items.tooltip.ToolboxTextTooltip;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.widget.panel.impl.LienzoPanelScrollEvent;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.client.lienzo.components.glyph.LienzoGlyphRenderers;
import org.kie.workbench.common.stunner.client.lienzo.util.ToolboxRefreshEvent;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ActionsToolbox;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ActionsToolboxView;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.IsToolboxActionDraggable;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ToolboxAction;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseClickEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseMoveEvent;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;

import static org.kie.workbench.common.stunner.client.lienzo.shape.view.ViewEventHandlerManager.getClientX;
import static org.kie.workbench.common.stunner.client.lienzo.shape.view.ViewEventHandlerManager.getClientY;

public abstract class AbstractActionsToolboxView<V extends AbstractActionsToolboxView>
        implements ActionsToolboxView<V> {

    public static final double BUTTON_SIZE = 15;
    public static final double BUTTON_PADDING = 5;

    private final LienzoGlyphRenderers glyphRenderers;
    private final ToolboxFactory toolboxFactory;
    private WiresShapeToolbox toolboxView;
    private ToolboxTextTooltip tooltip;
    private WiresCanvas canvas;

    public AbstractActionsToolboxView(final LienzoGlyphRenderers glyphRenderers,
                                      final ToolboxFactory toolboxFactory) {
        this.glyphRenderers = glyphRenderers;
        this.toolboxFactory = toolboxFactory;
    }

    protected abstract void configure(final ActionsToolbox toolbox);

    protected abstract ToolboxTextTooltip createTooltip(ActionsToolbox toolbox);

    protected abstract double getGlyphSize();

    @Override
    public V init(final ActionsToolbox toolbox) {
        return init(toolbox,
                (WiresCanvas) toolbox.getCanvas(),
                (WiresShape) toolbox.getShape().getShapeView());
    }

    @Override
    public V show() {
        toolboxView.show();
        return cast();
    }

    @Override
    public V hide() {
        toolboxView.hide();
        return cast();
    }

    public void onToolboxRefreshEvent(final @Observes ToolboxRefreshEvent event) {
        drawTopLayer();
    }

    @Override
    public void destroy() {
        Optional.ofNullable(toolboxView).ifPresent(WiresShapeToolbox::destroy);
        Optional.ofNullable(tooltip).ifPresent(ToolboxTextTooltip::destroy);

        drawTopLayer();

        toolboxView = null;
        tooltip = null;
        canvas = null;
    }

    public void onScrollEvent(@Observes LienzoPanelScrollEvent scrollEvent) {
        drawTopLayer();
    }

    protected void drawTopLayer() {
        if (canvas != null) {
            canvas.getView().getLayer().getTopLayer().setVisible(true);
            canvas.getView().getLayer().getTopLayer().draw();
        }
    }

    @Override
    public void hideAndDestroy() {
        Optional.ofNullable(toolboxView).ifPresent(WiresShapeToolbox::hideAndDestroy);
        toolboxView = null;
        destroy();
    }

    @SuppressWarnings("unchecked")
    protected V init(final ActionsToolbox toolbox,
                     final WiresCanvas canvas,
                     final WiresShape shape) {
        this.canvas = canvas;
        final Layer topLayer = canvas.getView().getLayer().getTopLayer();

        this.toolboxView =
                toolboxFactory
                        .forWiresShape(shape)
                        .attachTo(topLayer);
        configure(toolbox);
        tooltip = createTooltip(toolbox);
        initButtons(toolbox);
        return cast();
    }

    @SuppressWarnings("unchecked")
    private void initButtons(final ActionsToolbox<ActionsToolboxView<?>> toolbox) {
        for (ToolboxAction toolboxAction : toolbox) {
            final ButtonItem button = addButton(toolbox.getGlyph(toolboxAction),
                    toolbox.getTitle(toolboxAction));
            button.onClick(event -> {
                onButtonClick(toolbox, toolboxAction, button, event);
            });
            if (toolboxAction instanceof IsToolboxActionDraggable) {
                button.onMoveStart(event -> {
                    onButtonMoveStart(toolbox, (IsToolboxActionDraggable) toolboxAction, button, event);
                });
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected void onButtonClick(final ActionsToolbox<ActionsToolboxView<?>> toolbox,
                                 final ToolboxAction toolboxAction,
                                 final ButtonItem button,
                                 final NodeMouseClickEvent event) {
        toolboxAction.onMouseClick(toolbox.getCanvasHandler(),
                toolbox.getElementUUID(),
                new MouseClickEvent(event.getX(),
                        event.getY(),
                        getClientX(event),
                        getClientY(event)));
    }

    @SuppressWarnings("unchecked")
    protected void onButtonMoveStart(final ActionsToolbox<ActionsToolboxView<?>> toolbox,
                                     final IsToolboxActionDraggable toolboxAction,
                                     final ButtonItem button,
                                     final NodeMouseMoveEvent event) {
        button.disable();
        toolboxAction.onMoveStart(toolbox.getCanvasHandler(),
                toolbox.getElementUUID(),
                new MouseMoveEvent(event.getX(),
                        event.getY(),
                        getClientX(event),
                        getClientY(event)));
    }

    ButtonItem addButton(final Glyph glyph,
                         final String title) {
        final ButtonItem button =
                toolboxFactory.buttons()
                        .button(renderGlyph(glyph,
                                getGlyphSize()))
                        .decorate(createDecorator())
                        .tooltip(tooltip.createItem(title))
                        .onMouseEnter(event -> onMouseEnter())
                        .onMouseExit(event -> onMouseExit());
        addButton(button);
        return button;
    }

    protected void addButton(final ButtonItem buttonItem) {
        toolboxView.add(buttonItem);
    }

    protected Consumer<Text> defaultTextConsumer() {
        return text -> text
                .setFontSize(10)
                .setFontFamily("Verdana");
    }

    protected WiresShapeToolbox getToolboxView() {
        return toolboxView;
    }

    protected ToolboxFactory getToolboxFactory() {
        return toolboxFactory;
    }

    protected Group renderGlyph(final Glyph glyph,
                                final double size) {
        return glyphRenderers
                .render(glyph,
                        size,
                        size);
    }

    protected BoxDecorator createDecorator() {
        return getToolboxFactory()
                .decorators()
                .box();
    }

    private void onMouseEnter() {
        if (null != canvas) {
            canvas.getView().setCursor(AbstractCanvas.Cursors.POINTER);
        }
    }

    private void onMouseExit() {
        if (null != canvas) {
            canvas.getView().setCursor(AbstractCanvas.Cursors.DEFAULT);
        }
    }

    @SuppressWarnings("unchecked")
    private V cast() {
        return (V) this;
    }
}
