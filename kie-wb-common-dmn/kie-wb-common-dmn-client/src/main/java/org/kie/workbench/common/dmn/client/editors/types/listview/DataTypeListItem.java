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

package org.kie.workbench.common.dmn.client.editors.types.listview;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.kie.workbench.common.dmn.client.editors.types.DataType;
import org.uberfire.client.mvp.UberElemental;

public class DataTypeListItem {

    private final View view;

    private final DataTypeSelect dataTypeSelectComponent;

    private DataType dataType;

    private int level;

    private DataTypeList dataTypeList;

    @Inject
    public DataTypeListItem(final View view,
                            final DataTypeSelect dataTypeSelectComponent) {
        this.view = view;
        this.dataTypeSelectComponent = dataTypeSelectComponent;
    }

    @PostConstruct
    void setup() {
        view.init(this);
    }

    public void init(final DataTypeList dataTypeList) {
        this.dataTypeList = dataTypeList;
    }

    public HTMLElement getElement() {
        return view.getElement();
    }

    DataTypeListItem setupDataType(final DataType dataType,
                                   final int level) {
        this.dataType = dataType;
        this.level = level;

        setupSelectComponent();
        setupView();

        return this;
    }

    void setupSelectComponent() {
        dataTypeSelectComponent.init(this, getDataType());
    }

    void setupView() {
        view.setupSelectComponent(dataTypeSelectComponent);
        view.setDataType(getDataType());
    }

    DataType getDataType() {
        return dataType;
    }

    public int getLevel() {
        return level;
    }

    void expandOrCollapseSubTypes() {
        if (view.isCollapsed()) {
            expand();
        } else {
            collapse();
        }
    }

    void expand() {
        view.expand();
    }

    void collapse() {
        view.collapse();
    }

    public void refreshSubItems(final List<DataType> dataTypes) {

        dataTypeList.refreshSubItems(this, dataTypes);

        view.enableFocusMode();
        view.toggleArrow(!dataTypes.isEmpty());
    }

    void enableEditMode() {

        view.showSaveButton();
        view.showDataTypeNameInput();
        view.enableFocusMode();

        dataTypeSelectComponent.enableEditMode();
    }

    void disableEditMode() {
        discardNewDataType();
        closeEditMode();
    }

    void saveAndCloseEditMode() {
        saveNewDataType();
        closeEditMode();
    }

    void discardNewDataType() {

        view.setDataType(getDataType());

        setupSelectComponent();
        refreshSubItems(getDataType().getSubDataTypes());
    }

    void closeEditMode() {

        view.showEditButton();
        view.hideDataTypeNameInput();
        view.disableFocusMode();

        dataTypeSelectComponent.disableEditMode();
    }

    void saveNewDataType() {

        final DataType dataType = getDataType();

        dataType.setName(view.getName());
        dataType.setType(dataTypeSelectComponent.getValue());
        dataType.getSubDataTypes().clear();
        dataType.getSubDataTypes().addAll(dataTypeSelectComponent.getSubDataTypes());

        dataType.update();
    }

    public interface View extends UberElemental<DataTypeListItem> {

        void setDataType(final DataType dataType);

        void toggleArrow(final boolean show);

        void expand();

        void collapse();

        void showEditButton();

        void showSaveButton();

        void setupSelectComponent(final DataTypeSelect typeSelect);

        boolean isCollapsed();

        void hideDataTypeNameInput();

        void showDataTypeNameInput();

        void enableFocusMode();

        void disableFocusMode();

        String getName();
    }
}
