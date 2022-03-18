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
package org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.multiple.impl;

import com.ait.lienzo.client.core.shape.Group;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.dom.impl.CheckBoxDOMElement;
import org.uberfire.ext.wires.core.grids.client.widget.dom.multiple.impl.CheckBoxDOMElementFactory;

public class BooleanColumnDOMElementRenderer extends BaseGridColumnMultipleDOMElementRenderer<Boolean, CheckBox, CheckBoxDOMElement> {

    public BooleanColumnDOMElementRenderer(final CheckBoxDOMElementFactory factory) {
        super(factory);
    }

    @Override
    public Group renderCell(final GridCell<Boolean> cell,
                            final GridBodyCellRenderContext context) {
        if (cell == null || cell.getValue() == null) {
            return null;
        }
        final Group g = new Group();
        factory.attachDomElement(context,
                                 e -> e.getWidget().setValue(cell.getValue().getValue()),
                                 result -> {/*Nothing*/});
        return g;
    }
}
