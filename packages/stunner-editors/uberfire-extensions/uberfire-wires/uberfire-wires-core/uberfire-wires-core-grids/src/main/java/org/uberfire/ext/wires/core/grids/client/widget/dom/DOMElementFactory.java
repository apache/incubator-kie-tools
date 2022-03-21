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
package org.uberfire.ext.wires.core.grids.client.widget.dom;

import java.util.function.Consumer;

import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

/**
 * Definition of a Factor that can create DOMElements for GWT Widget based cell content.
 * DOMElements are transient in nature and only exist when required, such as when a column
 * and row is visible or when a cell is being edited.
 * @param <W> The Widget to be wrapped by the DOMElement.
 * @param <E> The DOMElement type that this Factory generates.
 */
public interface DOMElementFactory<W, E> extends HasDOMElementResources {

    /**
     * Creates a Widget to be wrapped by the DOMElement
     * @return
     */
    W createWidget();

    /**
     * Creates a DOMElement.
     * @param gridLayer The Lienzo layer on which the Grid Widget is attached. DOMElements may need to redraw the Layer when their state changes.
     * @param gridWidget The GridWidget to which this DOMElement is to be associated.
     * @return
     */
    E createDomElement(final GridLayer gridLayer,
                       final GridWidget gridWidget);

    /**
     * Register handlers for the widget. The set of handlers may differ per each Factory. Common handlers
     * registered in this method are KeyDownHandler, BlurHandler, ... .
     * @param widget
     * @param widgetDomElement
     */
    void registerHandlers(final W widget, final E widgetDomElement);

    /**
     * Initialises a DOMElement for a cell and attach it to the GWT container.
     * @param context The render context of the cell.
     * @param onCreation A callback that is invoked after the cell has been initialised, allowing content etc to be pre-set
     * @param onDisplay A callback that is invoked after the cell has been attached to the DOM and displayed.
     */
    void attachDomElement(final GridBodyCellRenderContext context,
                          final Consumer<E> onCreation,
                          final Consumer<E> onDisplay);
}
