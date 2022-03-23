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
package org.uberfire.ext.wires.core.grids.client.widget.dom.single.impl;

import java.util.function.Consumer;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.dom.impl.BaseDOMElement;
import org.uberfire.ext.wires.core.grids.client.widget.dom.single.SingletonDOMElementFactory;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.keyboard.KeyDownHandlerCommon;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLayerRedrawManager;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLienzoPanel;

/**
 * Base Factory for single-instance DOMElements, i.e. there can only be one instance "on screen" at any given time,
 * for example to handle "in cell" editing; when a DOMElement is required to "edit" the cell but not when the cell
 * is rendered ordinarily. This implementation keeps a single DOMElement that is detached from the GWT container
 * when not needed.
 * @param <T> The data-type of the cell
 * @param <W> The Widget to be wrapped by the DOMElement.
 * @param <E> The DOMElement type that this Factory generates.
 */
public abstract class BaseSingletonDOMElementFactory<T, W extends Widget, E extends BaseDOMElement<T, W>> implements SingletonDOMElementFactory<W, E> {

    protected final GridLienzoPanel gridPanel;
    protected final GridLayer gridLayer;
    protected final GridWidget gridWidget;

    protected W widget;
    protected E e;

    public BaseSingletonDOMElementFactory(final GridLienzoPanel gridPanel,
                                          final GridLayer gridLayer,
                                          final GridWidget gridWidget) {
        this.gridPanel = gridPanel;
        this.gridLayer = gridLayer;
        this.gridWidget = gridWidget;
    }

    @Override
    public void attachDomElement(final GridBodyCellRenderContext context,
                                 final Consumer<E> onCreation,
                                 final Consumer<E> onDisplay) {
        gridLayer.batch(new GridLayerRedrawManager.PrioritizedCommand(Integer.MAX_VALUE) {
            @Override
            public void execute() {
                final E domElement = createDomElement(gridLayer,
                                                      gridWidget);
                registerHandlers(widget, domElement);

                domElement.setContext(context);
                domElement.initialise(context);
                onCreation.accept(domElement);

                domElement.attach();
                onDisplay.accept(domElement);
            }
        });
    }

    @Override
    public E createDomElement(final GridLayer gridLayer,
                              final GridWidget gridWidget) {
        widget = createWidget();
        e = createDomElementInternal(widget, gridLayer, gridWidget);

        return e;
    }

    @Override
    public void registerHandlers(final W widget, final E widgetDomElement) {
        widget.addDomHandler(destroyOrFlushKeyDownHandler(),
                             KeyDownEvent.getType());
        widget.addDomHandler((e) -> e.stopPropagation(),
                             KeyDownEvent.getType());
        widget.addDomHandler((e) -> e.stopPropagation(),
                             MouseDownEvent.getType());

        if (widget instanceof Focusable) {
            widget.addDomHandler((e) ->
                                 {
                                     flush();
                                     gridLayer.batch();
                                     gridPanel.setFocus(true);
                                 }, BlurEvent.getType());
        }
    }

    @Override
    public void destroyResources() {
        if (e != null) {
            e.detach();
            widget = null;
            e = null;
        }
    }

    @Override
    public void flush() {
        if (e != null) {
            if (widget != null) {
                e.flush(getValue());
            }
            e.detach();
            widget = null;
            e = null;
        }
    }

    protected KeyDownHandlerCommon destroyOrFlushKeyDownHandler() {
        return new KeyDownHandlerCommon(gridPanel, gridLayer, gridWidget, this);
    }

    protected abstract T getValue();

    protected abstract E createDomElementInternal(final W widget,
                                                  final GridLayer gridLayer,
                                                  final GridWidget gridWidget);
}
