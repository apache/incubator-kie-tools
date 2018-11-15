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

import org.drools.workbench.screens.scenariosimulation.client.factories.ScenarioCellTextAreaSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.client.metadata.ScenarioHeaderMetaData;
import org.drools.workbench.screens.scenariosimulation.client.values.ScenarioGridCellValue;
import org.drools.workbench.screens.scenariosimulation.model.FactIdentifier;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.GridColumnRenderer;

public class ScenarioGridColumn extends BaseGridColumn<String> {

    private final ScenarioCellTextAreaSingletonDOMElementFactory factory;

    protected final ScenarioHeaderMetaData informationHeaderMetaData;
    protected final ScenarioHeaderMetaData propertyHeaderMetaData;

    /**
     * flag to know if an <b>instance</b> has been already assigned to this column; <code>false</code> on instantiation
     */
    protected boolean instanceAssigned = false;
    /**
     * flag to know if a <b>property</b> has been already assigned to this column; <code>false</code> on instantiation
     */
    protected boolean propertyAssigned = false;

    /**
     * The <code>FactIdentifier</code> mapped to this column; default to <code>FactIdentifier.EMPTY</code>
     */
    protected FactIdentifier factIdentifier = FactIdentifier.EMPTY;

    public ScenarioGridColumn(HeaderMetaData headerMetaData, GridColumnRenderer<String> columnRenderer, double width, boolean isMovable, ScenarioCellTextAreaSingletonDOMElementFactory factory, String placeHolder) {
        super(headerMetaData, columnRenderer, width);
        this.informationHeaderMetaData = (ScenarioHeaderMetaData) headerMetaData;
        propertyHeaderMetaData = null;
        this.setMovable(isMovable);
        this.factory = factory;
        this.placeHolder = placeHolder;
    }

    public ScenarioGridColumn(List<HeaderMetaData> headerMetaData, GridColumnRenderer<String> columnRenderer, double width, boolean isMovable, ScenarioCellTextAreaSingletonDOMElementFactory factory, String placeHolder) {
        super(headerMetaData, columnRenderer, width);
        this.informationHeaderMetaData = (ScenarioHeaderMetaData) headerMetaData.stream().filter(hdm -> ((ScenarioHeaderMetaData) hdm).isInstanceHeader()).findFirst().orElse(headerMetaData.get(0));
        this.propertyHeaderMetaData = (ScenarioHeaderMetaData) headerMetaData.stream().filter(hdm -> ((ScenarioHeaderMetaData) hdm).isPropertyHeader()).findFirst().orElse(null);
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

    /**
     * Dynamically evaluated status to know if the values of the given column are editable or not
     * @return
     */
    public boolean isReadOnly() {
        if (FactIdentifier.INDEX.equals(factIdentifier) || FactIdentifier.EMPTY.equals(factIdentifier)) {
            return true;
        }
        if (FactIdentifier.DESCRIPTION.equals(factIdentifier)) {
            return false;
        } else {
            return !isPropertyAssigned();
        }
    }

    public boolean isInstanceAssigned() {
        return instanceAssigned;
    }

    /**
     * Set to <code>true</code> if an <b>instance</b> has been assigned, <code>false</code> otherwise.
     * Setting this to <code>false</code> automatically set to <code>false</code> also <code>propertyAssigned</code>
     * @param instanceAssigned
     */
    public void setInstanceAssigned(boolean instanceAssigned) {
        this.instanceAssigned = instanceAssigned;
        if (!instanceAssigned) {
            propertyAssigned = false;
        }
    }

    public boolean isPropertyAssigned() {
        return propertyAssigned;
    }

    /**
     * Set to <code>true</code> if a <b>property</b> has been assigned, <code>false</code> otherwise.
     * Setting this to <code>true</code> automatically set to <code>true</code> also <code>instanceAssigned</code>
     * @param propertyAssigned
     */
    public void setPropertyAssigned(boolean propertyAssigned) {
        this.propertyAssigned = propertyAssigned;
        if (propertyAssigned) {
            instanceAssigned = true;
        }
    }

    public void setPlaceHolder(String placeHolder) {
        this.placeHolder = placeHolder;
    }

    public ScenarioHeaderMetaData getInformationHeaderMetaData() {
        return informationHeaderMetaData;
    }

    public ScenarioHeaderMetaData getPropertyHeaderMetaData() {
        return propertyHeaderMetaData;
    }

    public String getPlaceHolder() {
        return placeHolder;
    }

    public FactIdentifier getFactIdentifier() {
        return factIdentifier;
    }

    public void setFactIdentifier(FactIdentifier factIdentifier) {
        this.factIdentifier = factIdentifier;
    }

    @Override
    public String toString() {
        return "ScenarioGridColumn{" +
                "informationHeaderMetaData=" + informationHeaderMetaData +
                ", propertyHeaderMetaData=" + propertyHeaderMetaData +
                '}';
    }

    private GridCell<String> assertCell(final GridCell<String> cell) {
        if (cell != null) {
            return cell;
        }
        return new ScenarioGridCell(new ScenarioGridCellValue("", placeHolder));
    }
}