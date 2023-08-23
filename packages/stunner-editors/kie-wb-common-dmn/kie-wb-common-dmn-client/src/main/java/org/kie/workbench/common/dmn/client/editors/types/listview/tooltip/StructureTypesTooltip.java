/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.editors.types.listview.tooltip;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeKind;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManager;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeUtils;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeList;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItem;
import org.uberfire.client.mvp.UberElemental;

import static java.util.Collections.emptyList;

@ApplicationScoped
public class StructureTypesTooltip {

    private final View view;

    private final DataTypeUtils dataTypeUtils;

    private final DataTypeList dataTypeList;

    private final DataTypeManager dataTypeManager;

    private String typeName;

    @Inject
    public StructureTypesTooltip(final View view,
                                 final DataTypeUtils dataTypeUtils,
                                 final DataTypeList dataTypeList,
                                 final DataTypeManager dataTypeManager) {
        this.view = view;
        this.dataTypeUtils = dataTypeUtils;
        this.dataTypeList = dataTypeList;
        this.dataTypeManager = dataTypeManager;
    }

    @PostConstruct
    void setup() {
        view.init(this);
    }

    public void show(final HTMLElement refElement,
                     final String typeName) {
        this.typeName = typeName;
        view.show(refElement);
    }

    HTMLElement getListItems() {
        return dataTypeList.getListItems();
    }

    List<DataType> getTypeFields() {
        return dataTypeManager
                .getTopLevelDataTypeWithName(getTypeName())
                .map(DataType::getSubDataTypes)
                .orElse(emptyList());
    }

    DataTypeKind getDataTypeKind() {
        return dataTypeUtils.getDataTypeKind(typeName);
    }

    void goToDataType() {
        dataTypeManager
                .getTopLevelDataTypeWithName(getTypeName())
                .flatMap(dataTypeList::findItem)
                .ifPresent(DataTypeListItem::enableShortcutsHighlight);
    }

    String getTypeName() {
        return typeName;
    }

    public interface View extends UberElemental<StructureTypesTooltip>,
                                  IsElement {

        void show(final HTMLElement refElement);
    }
}
