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

package org.kie.workbench.common.dmn.client.widgets.grid.columns;

import java.util.ArrayList;
import java.util.List;

import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.TextAreaSingletonDOMElementFactory;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.renderers.TextAreaColumnDOMElementSingletonRenderer;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCell;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.dom.single.HasSingletonDOMElementResource;

public class TextAreaDOMElementSingletonColumn extends BaseGridColumn<String> implements HasSingletonDOMElementResource {

    private final TextAreaSingletonDOMElementFactory factory;

    public TextAreaDOMElementSingletonColumn(final HeaderMetaData headerMetaData,
                                             final TextAreaSingletonDOMElementFactory factory,
                                             final double width) {
        this(new ArrayList<HeaderMetaData>() {{
                 add(headerMetaData);
             }},
             factory,
             width);
    }

    public TextAreaDOMElementSingletonColumn(final List<HeaderMetaData> headerMetaData,
                                             final TextAreaSingletonDOMElementFactory factory,
                                             final double width) {
        super(headerMetaData,
              new TextAreaColumnDOMElementSingletonRenderer(factory),
              width);
        this.factory = PortablePreconditions.checkNotNull("factory",
                                                          factory);
    }

    @Override
    public void edit(final GridCell<String> cell,
                     final GridBodyCellRenderContext context,
                     final Callback<GridCellValue<String>> callback) {
        factory.attachDomElement(context,
                                 (e) -> e.getWidget().setValue(assertCell(cell).getValue().getValue()),
                                 (e) -> e.getWidget().setFocus(true));
    }

    private GridCell<String> assertCell(final GridCell<String> cell) {
        if (cell != null) {
            return cell;
        }
        return new BaseGridCell<>(new BaseGridCellValue<>(""));
    }

    @Override
    public void flush() {
        factory.flush();
    }

    @Override
    public void destroyResources() {
        factory.destroyResources();
    }
}
