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
package org.uberfire.ext.wires.core.grids.client.widget.grid.columns;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.dom.multiple.HasMultipleDOMElementResources;
import org.uberfire.ext.wires.core.grids.client.widget.dom.multiple.impl.CheckBoxDOMElementFactory;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.multiple.impl.BooleanColumnDOMElementRenderer;

public class BooleanDOMElementColumn extends BaseGridColumn<Boolean> implements HasMultipleDOMElementResources {

    private CheckBoxDOMElementFactory factory;

    public BooleanDOMElementColumn(final GridColumn.HeaderMetaData headerMetaData,
                                   final CheckBoxDOMElementFactory factory,
                                   final double width) {
        this(new ArrayList<HeaderMetaData>() {{
                 add(headerMetaData);
             }},
             factory,
             width);
    }

    public BooleanDOMElementColumn(final List<GridColumn.HeaderMetaData> headerMetaData,
                                   final CheckBoxDOMElementFactory factory,
                                   final double width) {
        super(headerMetaData,
              new BooleanColumnDOMElementRenderer(factory),
              width);
        this.factory = factory;
    }

    @Override
    public void initialiseResources() {
        factory.initialiseResources();
    }

    @Override
    public void destroyResources() {
        factory.destroyResources();
    }

    @Override
    public void freeUnusedResources() {
        factory.freeUnusedResources();
    }

    @Override
    public void edit(final GridCell<Boolean> cell,
                     final GridBodyCellRenderContext context,
                     final Consumer<GridCellValue<Boolean>> callback) {
        callback.accept(new BaseGridCellValue<>(!cell.getValue().getValue()));
    }
}