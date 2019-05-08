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
import java.util.Objects;
import java.util.function.Consumer;

import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasFocus;
import com.google.gwt.user.client.ui.Widget;
import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.workbench.screens.scenariosimulation.client.domelements.CollectionEditorDOMElement;
import org.drools.workbench.screens.scenariosimulation.client.domelements.ScenarioCellTextAreaDOMElement;
import org.drools.workbench.screens.scenariosimulation.client.metadata.ScenarioHeaderMetaData;
import org.drools.workbench.screens.scenariosimulation.client.values.ScenarioGridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.dom.impl.BaseDOMElement;
import org.uberfire.ext.wires.core.grids.client.widget.dom.single.impl.BaseSingletonDOMElementFactory;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.GridColumnRenderer;

public class ScenarioGridColumn extends BaseGridColumn<String> {

    private BaseSingletonDOMElementFactory<String, ? extends Widget, ? extends BaseDOMElement<String, ? extends Widget>> factory;

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
     * flag to know if the <b>headers</b> are editable or not (as in the DMN-scenario case); <code>true</code> on instantiation
     */
    protected boolean editableHeaders = true;

    /**
     * The <code>FactIdentifier</code> mapped to this column; default to <code>FactIdentifier.EMPTY</code>
     */
    protected FactIdentifier factIdentifier = FactIdentifier.EMPTY;

    public ScenarioGridColumn(List<HeaderMetaData> headerMetaData,
                              GridColumnRenderer<String> columnRenderer,
                              double width,
                              boolean isMovable,
                              BaseSingletonDOMElementFactory<String, ? extends Widget, ? extends BaseDOMElement<String, ? extends Widget>> factory,
                              String placeHolder) {
        super(headerMetaData, columnRenderer, width);
        this.informationHeaderMetaData = (ScenarioHeaderMetaData) headerMetaData.stream().filter(hdm -> Objects.equals(((ScenarioHeaderMetaData) hdm).getMetadataType(), ScenarioHeaderMetaData.MetadataType.INSTANCE)).findFirst().orElse(headerMetaData.get(0));
        this.propertyHeaderMetaData = (ScenarioHeaderMetaData) headerMetaData.stream().filter(hdm -> Objects.equals(((ScenarioHeaderMetaData) hdm).getMetadataType(), ScenarioHeaderMetaData.MetadataType.PROPERTY)).findFirst().orElse(null);
        this.setMovable(isMovable);
        this.factory = factory;
        this.placeHolder = placeHolder;
        this.setMinimumWidth(width);

        // by default scenario columns should be auto to have auto resize
        this.setColumnWidthMode(ColumnWidthMode.AUTO);
    }

    @Override
    public void edit(GridCell<String> cell, GridBodyCellRenderContext context, Consumer<GridCellValue<String>> callback) {
        factory.attachDomElement(context,
                                 e -> {
                                     try {
                                         final GridCell<String> stringGridCell = assertCell(cell);
                                         if (e instanceof ScenarioCellTextAreaDOMElement) {
                                             ((ScenarioCellTextAreaDOMElement) e).getWidget().setValue(stringGridCell.getValue().getValue());
                                             ((ScenarioCellTextAreaDOMElement) e).setScenarioGridCell((ScenarioGridCell) cell);
                                         } else if (e instanceof CollectionEditorDOMElement) {
                                             CollectionEditorDOMElement collectionEditorDOMElement = (CollectionEditorDOMElement) e;
                                             collectionEditorDOMElement.getWidget().setValue(stringGridCell.getValue().getValue());
                                             ((ScenarioGridCell) cell).setListMap(collectionEditorDOMElement.getWidget().isListWidget());
                                             collectionEditorDOMElement.setScenarioGridCell((ScenarioGridCell) cell);
                                         }
                                     } catch (Exception ex) {
                                         ((ScenarioGridCell) cell).setEditingMode(false);
                                         throw ex;
                                     }
                                 },
                                 e -> {
                                     if ((e.getWidget() instanceof HasFocus)) {
                                         ((FocusWidget) e.getWidget()).setFocus(true);
                                     }
                                 });
    }

    /**
     * Set the <b>factory</b> used for creation of the editing <code>DOMElement</code>
     * @param factory
     */
    public void setFactory(BaseSingletonDOMElementFactory<String, ? extends Widget, ? extends
            BaseDOMElement<String, ? extends Widget>> factory) {
        this.factory = factory;
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

    public boolean isEditableHeaders() {
        return editableHeaders;
    }

    /**
     * Set to <code>false</code> to prevent/avoid header editing
     * @param editableHeaders
     */
    public void setEditableHeaders(boolean editableHeaders) {
        this.editableHeaders = editableHeaders;
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