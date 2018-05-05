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

package org.kie.workbench.common.stunner.client.lienzo.components.toolbox.actions;

import java.util.function.Consumer;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.client.lienzo.components.glyph.LienzoGlyphRenderers;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ActionsToolbox;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ActionsToolboxView;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseClickEvent;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.lienzo.toolbox.items.ButtonItem;
import org.kie.workbench.common.stunner.lienzo.toolbox.items.DecoratorItem;
import org.kie.workbench.common.stunner.lienzo.toolbox.items.impl.ToolboxFactory;
import org.kie.workbench.common.stunner.lienzo.toolbox.items.impl.WiresShapeToolbox;
import org.kie.workbench.common.stunner.lienzo.toolbox.items.tooltip.ToolboxTextTooltip;

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
    public V addButton(Glyph glyph,
                       String title,
                       Consumer<MouseClickEvent> clickEventConsumer) {
        final ButtonItem button =
                toolboxFactory.buttons()
                        .button(renderGlyph(glyph,
                                            getGlyphSize()))
                        .decorate(createDecorator())
                        .tooltip(tooltip.createItem(title))
                        .onMouseEnter(event -> onMouseEnter())
                        .onMouseExit(event -> onMouseExit())
                        .onClick(event -> clickEventConsumer.accept(new MouseClickEvent(event.getX(),
                                                                                        event.getY(),
                                                                                        event.getMouseEvent()
                                                                                                .getClientX(),
                                                                                        event.getMouseEvent()
                                                                                                .getClientY())));
        addButton(button);
        return cast();
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

    @Override
    public void destroy() {
        toolboxView.destroy();
        if (null != tooltip) {
            tooltip.destroy();
            tooltip = null;
        }
        toolboxView = null;
        canvas = null;
    }

    protected V init(final ActionsToolbox toolbox,
                     final WiresCanvas canvas,
                     final WiresShape shape) {
        this.canvas = canvas;
        final Layer topLayer = canvas.getView().getTopLayer();
        this.toolboxView =
                toolboxFactory
                        .forWiresShape(shape)
                        .attachTo(topLayer);
        configure(toolbox);
        tooltip = createTooltip(toolbox);
        return cast();
    }

    protected void addButton(final ButtonItem buttonItem) {
        toolboxView.add(buttonItem);
    }

    protected Group renderGlyph(final Glyph glyph,
                                final double size) {
        return glyphRenderers
                .render(glyph,
                        size,
                        size);
    }

    protected DecoratorItem<?> createDecorator() {
        return getToolboxFactory()
                .decorators()
                .box();
    }

    protected Consumer<Text> defaultTextConsumer() {
        return text -> text
                .setFontSize(10)
                .setFontFamily("Verdana");
    }

    protected WiresShapeToolbox getToolboxView() {
        return toolboxView;
    }

    protected void onMouseEnter() {
        if (null != canvas) {
            canvas.getView().setCursor(AbstractCanvas.Cursors.POINTER);
        }
    }

    protected void onMouseExit() {
        if (null != canvas) {
            canvas.getView().setCursor(AbstractCanvas.Cursors.AUTO);
        }
    }

    protected ToolboxFactory getToolboxFactory() {
        return toolboxFactory;
    }

    @SuppressWarnings("unchecked")
    private V cast() {
        return (V) this;
    }
}
