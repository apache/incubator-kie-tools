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

import org.drools.workbench.screens.scenariosimulation.client.factories.ScenarioHeaderTextBoxSingletonDOMElementFactory;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseHeaderMetaData;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellEditContext;

public class ScenarioHeaderMetaData extends BaseHeaderMetaData {

    final ScenarioHeaderTextBoxSingletonDOMElementFactory factory;
    final String columnId;
    final boolean readOnly;
    // true if this header contains the column' main informations (group, title, id)
    final boolean informationHeader;

    public ScenarioHeaderMetaData(String columnId, String columnTitle, String columnGroup, final ScenarioHeaderTextBoxSingletonDOMElementFactory factory, boolean readOnly, boolean informationHeader) {
        super(columnTitle, columnGroup);
        this.columnId = columnId;
        this.factory = factory;
        this.readOnly = readOnly;
        this.informationHeader = informationHeader;
    }

    public ScenarioHeaderMetaData(String columnId, String columnTitle, String columnGroup, final ScenarioHeaderTextBoxSingletonDOMElementFactory factory, boolean informationHeader) {
        this(columnId, columnTitle, columnGroup, factory, false, informationHeader);
    }

    public void edit(final GridBodyCellEditContext context) {
        if (readOnly) {
            throw new IllegalStateException("A read only header cannot be edited");
        }
        factory.attachDomElement(context,
                                 (e) -> e.getWidget().setText(getTitle()),
                                 (e) -> e.getWidget().setFocus(true));
    }

    public String getColumnId() {
        return columnId;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public boolean isInformationHeader() {
        return informationHeader;
    }
}