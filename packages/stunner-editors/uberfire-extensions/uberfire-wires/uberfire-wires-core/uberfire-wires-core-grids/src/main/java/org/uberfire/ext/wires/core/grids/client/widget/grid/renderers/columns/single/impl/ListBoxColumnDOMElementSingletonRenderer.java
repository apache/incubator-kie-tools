/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.single.impl;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Text;
import com.google.gwt.user.client.ui.ListBox;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.dom.impl.ListBoxDOMElement;
import org.uberfire.ext.wires.core.grids.client.widget.dom.single.impl.ListBoxSingletonDOMElementFactory;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.GridRendererTheme;

public class ListBoxColumnDOMElementSingletonRenderer extends BaseGridColumnSingletonDOMElementRenderer<String, ListBox, ListBoxDOMElement> {

    public ListBoxColumnDOMElementSingletonRenderer(final ListBoxSingletonDOMElementFactory factory) {
        super(factory);
    }

    @Override
    public Group renderCell(final GridCell<String> cell,
                            final GridBodyCellRenderContext context) {
        if (cell == null || cell.getValue() == null) {
            return null;
        }

        final GridRenderer renderer = context.getRenderer();
        final GridRendererTheme theme = renderer.getTheme();

        final Group g = new Group();
        final Text t = theme.getBodyText()
                .setText(cell.getValue().getValue())
                .setListening(false)
                .setX(context.getCellWidth() / 2)
                .setY(context.getCellHeight() / 2);
        g.add(t);
        return g;
    }
}
