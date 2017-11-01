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
package org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.ext.wires.core.grids.client.widget.dom.impl.BaseDOMElement;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

/**
 * Base DOM Element Factory for the Guided Decision Table Editor for singleton overlays.
 * This implementation separates "flushing" the value from the Widget to the Model from
 * destroying the Widget.
 * @param <T> Data-type handled by this factory.
 * @param <W> Widget representing the data-type.
 */
public abstract class BaseDTDOMElement<T, W extends Widget> extends BaseDOMElement<T, W> {

    public BaseDTDOMElement(final W widget,
                            final GridLayer gridLayer,
                            final GridWidget gridWidget) {
        super(widget,
              gridLayer,
              gridWidget);
    }

    protected Style style(final Widget widget) {
        return widget.getElement().getStyle();
    }

    @Override
    protected void setupDelegatingMouseDownHandler() {
        //GDT has no need to delegate events to GridLayer
    }

    @Override
    protected void setupDelegatingMouseMoveHandler() {
        //GDT has no need to delegate events to GridLayer
    }

    @Override
    protected void setupDelegatingMouseUpHandler() {
        //GDT has no need to delegate events to GridLayer
    }

    @Override
    protected void setupDelegatingClickHandler() {
        //GDT has no need to delegate events to GridLayer
    }
}
