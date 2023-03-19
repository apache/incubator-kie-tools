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
package org.drools.workbench.screens.scenariosimulation.client.metadata;

import org.drools.workbench.screens.scenariosimulation.client.domelements.ScenarioHeaderTextAreaDOMElement;
import org.drools.workbench.screens.scenariosimulation.client.factories.ScenarioHeaderTextBoxSingletonDOMElementFactory;
import org.uberfire.ext.wires.core.grids.client.model.GridCellEditAction;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseHeaderMetaData;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellEditContext;

public class ScenarioHeaderMetaData extends BaseHeaderMetaData {

    public enum MetadataType {

        GROUP, // ex GIVEN, EXPECTED
        INSTANCE,
        PROPERTY,
        UNDEFINED
    }

    /*
    `HeaderMetaData` now has a `CellSelectionStrategy` method (default is to select single cells)
    you'd be able to add your own `CellSelectionStrategy` to your `HeaderMetaData` and do was you wish (e.g. select whole column)
     */
    final ScenarioHeaderTextBoxSingletonDOMElementFactory factory;
    final String columnId;
    private boolean readOnly;
    // true if this header contains the column' informations (group, title, id) and it is an instance header
    final boolean instanceHeader;
    // true if this header contains the column' informations (group, title, id) and it is a property header
    final boolean propertyHeader;
    private boolean isEditingMode = false;
    private final MetadataType metadataType;

    /**
     * Constructor for ScenarioHeaderMetaData
     * @param columnId
     * @param columnTitle
     * @param columnGroup
     * @param factory
     * @param readOnly
     * @param instanceHeader Set <code>true</code> for <i>instance</i>' header <b>or</b> the description/id ones, <code>false</code>
     * @param propertyHeader Set <code>true</code> for <i>property</i>' header <b>or</b> the description/id ones, <code>false</code>
     * @throws IllegalStateException if both <code>instanceHeader</code> and <code>propertyHeader</code> are <code>true</code>
     */
    public ScenarioHeaderMetaData(String columnId, String columnTitle, String columnGroup, final ScenarioHeaderTextBoxSingletonDOMElementFactory factory, boolean readOnly, boolean instanceHeader, boolean propertyHeader) throws IllegalStateException {
        super(columnTitle, columnGroup);
        if (instanceHeader && propertyHeader) {
            throw new IllegalStateException("A ScenarioHeaderMetaData can not be both InstanceHeader and PropertyHeader");
        }
        this.columnId = columnId;
        this.factory = factory;
        this.readOnly = readOnly;
        this.instanceHeader = instanceHeader;
        this.propertyHeader = propertyHeader;
        if (isInstanceHeader()) {
            metadataType = MetadataType.INSTANCE;
        } else if (isPropertyHeader()) {
            metadataType = MetadataType.PROPERTY;
        } else {
            metadataType = MetadataType.UNDEFINED;
        }
    }

    /**
     * Constructor for ScenarioHeaderMetaData - readonly default to <code>false</code>
     * @param columnId
     * @param columnTitle
     * @param columnGroup
     * @param factory
     * @param instanceHeader Set <code>true</code> for <i>instance</i>' header <b>or</b> the description/id ones, <code>false</code>
     * @param propertyHeader Set <code>true</code> for <i>property</i>' header <b>or</b> the description/id ones, <code>false</code>
     * @throws IllegalStateException if both <code>instanceHeader</code> and <code>propertyHeader</code> are <code>true</code>
     */
    public ScenarioHeaderMetaData(String columnId, String columnTitle, String columnGroup, final ScenarioHeaderTextBoxSingletonDOMElementFactory factory, boolean instanceHeader, boolean propertyHeader) throws IllegalStateException {
        this(columnId, columnTitle, columnGroup, factory, false, instanceHeader, propertyHeader);
    }

    public void edit(final GridBodyCellEditContext context) {
        if (readOnly) {
            throw new IllegalStateException("A read only header cannot be edited");
        }
        if (!isEditingMode) {
            factory.attachDomElement(context,
                                     (e) -> {
                                         e.getWidget().setText(getTitle());
                                         if (e instanceof ScenarioHeaderTextAreaDOMElement) {
                                             ((ScenarioHeaderTextAreaDOMElement) e).setScenarioHeaderMetaData(this);
                                         }
                                     },
                                     (e) -> e.getWidget().setFocus(true));
            isEditingMode = true;
        }
    }

    public String getColumnId() {
        return columnId;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public boolean isInstanceHeader() {
        return instanceHeader;
    }

    public boolean isPropertyHeader() {
        return propertyHeader;
    }

    public boolean isEditingMode() {
        return isEditingMode;
    }

    public void setEditingMode(boolean editingMode) {
        isEditingMode = editingMode;
    }

    public GridCellEditAction getSupportedEditAction() {
        return GridCellEditAction.DOUBLE_CLICK;
    }

    public MetadataType getMetadataType() {
        return metadataType;
    }

    @Override
    public String toString() {
        return "ScenarioHeaderMetaData{" +
                "columnId='" + columnId + '\'' +
                ", readOnly=" + readOnly +
                ", instanceHeader=" + instanceHeader +
                ", propertyHeader=" + propertyHeader +
                ", isEditingMode=" + isEditingMode +
                ", metadataType=" + metadataType +
                '}';
    }
}