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

import org.gwtbootstrap3.client.ui.ListBox;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

/**
 * A DOMElement for ListBoxes.
 */
public class ListBoxDOMElement<T, W extends ListBox> extends MultiValueDOMElement<T, W> {

    public ListBoxDOMElement(final W widget,
                             final GridLayer gridLayer,
                             final GridWidget gridWidget,
                             final boolean isMultipleSelect) {
        super(widget,
              gridLayer,
              gridWidget,
              true,
              !isMultipleSelect);
    }
}
