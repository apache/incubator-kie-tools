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

package org.kie.workbench.common.dmn.client.editors.types.listview;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManager;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeUtils;
import org.uberfire.client.mvp.UberElemental;

@Dependent
public class DataTypeSelect {

    private final View view;

    private final DataTypeUtils dataTypeUtils;

    private final DataTypeManager dataTypeManager;

    private DataType dataType;

    private DataTypeListItem listItem;

    @Inject
    public DataTypeSelect(final View view,
                          final DataTypeUtils dataTypeUtils,
                          final DataTypeManager dataTypeManager) {
        this.view = view;
        this.dataTypeUtils = dataTypeUtils;
        this.dataTypeManager = dataTypeManager;
    }

    @PostConstruct
    void setup() {
        view.init(this);
    }

    public HTMLElement getElement() {
        return view.getElement();
    }

    public void init(final DataTypeListItem listItem,
                     final DataType dataType) {
        this.listItem = listItem;
        this.dataType = dataType;
        this.view.setDataType(dataType);
    }

    void refresh() {
        view.setupDropdown();
    }

    DataType getDataType() {
        return dataType;
    }

    void enableEditMode() {
        refresh();
        view.enableEditMode();
    }

    void disableEditMode() {
        view.disableEditMode();
    }

    String structure() {
        return dataTypeManager.structure();
    }

    List<DataType> getDefaultDataTypes() {
        return dataTypeUtils.defaultDataTypes();
    }

    List<DataType> getCustomDataTypes() {
        return dataTypeUtils
                .customDataTypes()
                .stream()
                .filter(dataType -> !Objects.equals(dataType.getName(), getDataType().getName()))
                .collect(Collectors.toList());
    }

    void clearDataTypesList() {
        listItem.cleanSubDataTypes();
        listItem.refreshConstraintComponent();
    }

    public String getValue() {
        return view.getValue();
    }

    public interface View extends UberElemental<DataTypeSelect>,
                                  IsElement {

        void setupDropdown();

        void enableEditMode();

        void disableEditMode();

        void setDataType(final DataType type);

        String getValue();
    }
}
