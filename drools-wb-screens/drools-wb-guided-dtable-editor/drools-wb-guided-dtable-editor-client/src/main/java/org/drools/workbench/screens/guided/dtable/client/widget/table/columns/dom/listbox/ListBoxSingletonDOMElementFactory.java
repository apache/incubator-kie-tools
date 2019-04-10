/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.listbox;

import com.google.gwt.event.dom.client.MouseDownEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.gwtbootstrap3.client.ui.ListBox;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.keyboard.KeyDownHandlerCommon;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLienzoPanel;

/**
 * A DOMElement Factory for single-instance ListBoxes.
 */
public abstract class ListBoxSingletonDOMElementFactory<T, W extends ListBox> extends MultiValueSingletonDOMElementFactory<T, W, ListBoxDOMElement<T, W>> {

    public ListBoxSingletonDOMElementFactory(final GridLienzoPanel gridPanel,
                                             final GridLayer gridLayer,
                                             final GuidedDecisionTableView gridWidget) {
        super(gridPanel,
              gridLayer,
              gridWidget);
    }

    @Override
    public ListBoxDOMElement<T, W> createDomElement(final GridLayer gridLayer,
                                                    final GridWidget gridWidget,
                                                    final GridBodyCellRenderContext context) {
        this.widget = createWidget();
        this.widget.addMouseDownHandler(MouseDownEvent::stopPropagation);
        this.widget.addKeyDownHandler(new KeyDownHandlerCommon(gridPanel,
                                                               gridLayer,
                                                               gridWidget,
                                                               this,
                                                               context));
        this.widget.addBlurHandler((e) -> {
            flush();
            destroyResources();
            gridLayer.batch();
            gridPanel.setFocus(true);
        });

        this.e = new ListBoxDOMElement<>(widget,
                                         gridLayer,
                                         gridWidget,
                                         widget.isMultipleSelect());

        return e;
    }

    @Override
    protected T getValue() {
        if (widget != null) {
            return fromWidget(widget);
        }
        return null;
    }
}
