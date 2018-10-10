/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.scenariosimulation.client.widgets;

import java.util.List;
import java.util.function.Consumer;

import org.drools.workbench.screens.scenariosimulation.client.factories.ScenarioCellTextBoxSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.client.metadata.ScenarioHeaderMetaData;
import org.drools.workbench.screens.scenariosimulation.client.values.ScenarioGridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.GridColumnRenderer;

public class ScenarioGridColumn extends BaseGridColumn<String> {

    private final ScenarioCellTextBoxSingletonDOMElementFactory factory;

    final ScenarioHeaderMetaData informationHeaderMetaData;

    boolean readOnly = false;

    final String placeHolder;

    public ScenarioGridColumn(HeaderMetaData headerMetaData, GridColumnRenderer<String> columnRenderer, double width, boolean isMovable, ScenarioCellTextBoxSingletonDOMElementFactory factory, String placeHolder) {
        super(headerMetaData, columnRenderer, width);
        this.informationHeaderMetaData = (ScenarioHeaderMetaData) headerMetaData;
        this.setMovable(isMovable);
        this.factory = factory;
        this.placeHolder = placeHolder;
    }

    public ScenarioGridColumn(List<HeaderMetaData> headerMetaData, GridColumnRenderer<String> columnRenderer, double width, boolean isMovable, ScenarioCellTextBoxSingletonDOMElementFactory factory, String placeHolder) {
        super(headerMetaData, columnRenderer, width);
        this.informationHeaderMetaData = (ScenarioHeaderMetaData) headerMetaData.stream().filter(hdm -> ((ScenarioHeaderMetaData) hdm).isInformationHeader()).findFirst().orElse(headerMetaData.get(0));
        this.setMovable(isMovable);
        this.factory = factory;
        this.placeHolder = placeHolder;
    }

    @Override
    public void edit(GridCell<String> cell, GridBodyCellRenderContext context, Consumer<GridCellValue<String>> callback) {
        factory.attachDomElement(context,
                                 e -> e.getWidget().setValue(assertCell(cell).getValue().getValue()),
                                 e -> e.getWidget().setFocus(true));
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public ScenarioHeaderMetaData getInformationHeaderMetaData() {
        return informationHeaderMetaData;
    }

    public String getPlaceHolder() {
        return placeHolder;
    }

    private GridCell<String> assertCell(final GridCell<String> cell) {
        if (cell != null) {
            return cell;
        }
        return new ScenarioGridCell(new ScenarioGridCellValue("", placeHolder));
    }
}