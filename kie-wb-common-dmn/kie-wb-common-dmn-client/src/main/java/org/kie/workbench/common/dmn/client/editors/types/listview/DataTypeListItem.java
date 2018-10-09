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
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManager;
import org.uberfire.client.mvp.UberElemental;

public class DataTypeListItem {

    private final View view;

    private final DataTypeSelect dataTypeSelectComponent;

    private final DataTypeManager dataTypeManager;

    private DataType dataType;

    private int level;

    private DataTypeList dataTypeList;

    private String oldName;

    private String oldType;

    @Inject
    public DataTypeListItem(final View view,
                            final DataTypeSelect dataTypeSelectComponent,
                            final DataTypeManager dataTypeManager) {
        this.view = view;
        this.dataTypeSelectComponent = dataTypeSelectComponent;
        this.dataTypeManager = dataTypeManager;
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

    void refresh() {
        dataTypeSelectComponent.refresh();
        dataTypeSelectComponent.init(this, getDataType());
        view.setName(getDataType().getName());
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

        dataTypeList.refreshSubItemsFromListItem(this, dataTypes);

        view.enableFocusMode();
        view.toggleArrow(!dataTypes.isEmpty());
    }

    void enableEditMode() {

        oldName = getDataType().getName();
        oldType = getDataType().getType();

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

        final DataType updatedDataType = update(getDataType());

        if (updatedDataType.isValid()) {

            final List<DataType> updatedDataTypes = persist(updatedDataType);

            dataTypeList.refreshItemsByUpdatedDataTypes(updatedDataTypes);
            closeEditMode();
        }
    }

    List<DataType> persist(final DataType dataType) {
        return dataTypeManager
                .from(dataType)
                .withSubDataTypes(dataTypeSelectComponent.getSubDataTypes())
                .get()
                .update();
    }

    void discardNewDataType() {

        view.setDataType(dataTypeManager
                                 .withDataType(getDataType())
                                 .withName(getOldName())
                                 .withType(getOldType())
                                 .get());

        setupSelectComponent();
        refreshSubItems(getDataType().getSubDataTypes());
    }

    void closeEditMode() {

        view.showEditButton();
        view.hideDataTypeNameInput();
        view.disableFocusMode();

        dataTypeSelectComponent.disableEditMode();
    }

    public void remove() {

        final List<DataType> destroyedDataTypes = getDataType().destroy();
        final List<DataType> removedDataTypes = removeTopLevelDataTypes(destroyedDataTypes);
        destroyedDataTypes.removeAll(removedDataTypes);

        dataTypeList.refreshItemsByUpdatedDataTypes(destroyedDataTypes);
    }

    List<DataType> removeTopLevelDataTypes(final List<DataType> destroyedDataTypes) {

        final String destroyedType = getDataType().getName();

        return destroyedDataTypes
                .stream()
                .filter(dt -> dt.isTopLevel() && (Objects.equals(dt.getName(), destroyedType) || Objects.equals(dt.getType(), destroyedType)))
                .peek(dataTypeList::removeItem)
                .collect(Collectors.toList());
    }

    DataType update(final DataType dataType) {
        return dataTypeManager
                .from(dataType)
                .withName(view.getName())
                .withType(dataTypeSelectComponent.getValue())
                .get();
    }

    String getOldName() {
        return oldName;
    }

    String getOldType() {
        return oldType;
    }

    public DataTypeList getDataTypeList() {
        return dataTypeList;
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

        void setName(final String name);
    }
}
