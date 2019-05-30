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
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.kie.workbench.common.dmn.api.definition.v1_1.ConstraintType;
import org.kie.workbench.common.dmn.client.editors.types.DataTypeChangedEvent;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManager;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.DataTypeEditModeToggleEvent;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.SmallSwitchComponent;
import org.kie.workbench.common.dmn.client.editors.types.listview.confirmation.DataTypeConfirmation;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraint;
import org.kie.workbench.common.dmn.client.editors.types.listview.validation.DataTypeNameFormatValidator;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.mvp.Command;

import static org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType.BOOLEAN;
import static org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType.CONTEXT;
import static org.kie.workbench.common.dmn.client.editors.types.persistence.CreationType.ABOVE;
import static org.kie.workbench.common.dmn.client.editors.types.persistence.CreationType.BELOW;
import static org.kie.workbench.common.dmn.client.editors.types.persistence.CreationType.NESTED;
import static org.kie.workbench.common.stunner.core.util.StringUtils.isEmpty;

@Dependent
public class DataTypeListItem {

    private final View view;

    private final DataTypeSelect dataTypeSelectComponent;

    private final DataTypeConstraint dataTypeConstraintComponent;

    private final SmallSwitchComponent dataTypeListComponent;

    private final DataTypeManager dataTypeManager;

    private final DataTypeConfirmation confirmation;

    private final Event<DataTypeEditModeToggleEvent> editModeToggleEvent;

    private final DataTypeNameFormatValidator nameFormatValidator;

    private final Event<DataTypeChangedEvent> dataTypeChangedEvent;

    private DataType dataType;

    private int level;

    private DataTypeList dataTypeList;

    private String oldName;

    private String oldType;

    private String oldConstraint;

    private boolean oldIsList;

    private ConstraintType oldConstraintType;

    @Inject
    public DataTypeListItem(final View view,
                            final DataTypeSelect dataTypeSelectComponent,
                            final DataTypeConstraint dataTypeConstraintComponent,
                            final SmallSwitchComponent dataTypeListComponent,
                            final DataTypeManager dataTypeManager,
                            final DataTypeConfirmation confirmation,
                            final DataTypeNameFormatValidator nameFormatValidator,
                            final Event<DataTypeEditModeToggleEvent> editModeToggleEvent,
                            final Event<DataTypeChangedEvent> dataTypeChangedEvent) {
        this.view = view;
        this.dataTypeSelectComponent = dataTypeSelectComponent;
        this.dataTypeConstraintComponent = dataTypeConstraintComponent;
        this.dataTypeListComponent = dataTypeListComponent;
        this.dataTypeManager = dataTypeManager;
        this.confirmation = confirmation;
        this.nameFormatValidator = nameFormatValidator;
        this.editModeToggleEvent = editModeToggleEvent;
        this.dataTypeChangedEvent = dataTypeChangedEvent;
        this.dataTypeListComponent.setOnValueChanged(value -> refreshConstraintComponent());
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

    void setupDataType(final DataType dataType,
                       final int level) {

        this.dataType = dataType;
        this.level = level;

        setupSelectComponent();
        setupListComponent();
        setupConstraintComponent();
        setupView();
    }

    void setupListComponent() {
        dataTypeListComponent.setValue(getDataType().isList());
        refreshListYesLabel();
    }

    void setupConstraintComponent() {
        dataTypeConstraintComponent.init(this);
        refreshConstraintComponent();
    }

    void setupSelectComponent() {
        dataTypeSelectComponent.init(this, getDataType());
    }

    void setupView() {
        view.setupSelectComponent(dataTypeSelectComponent);
        view.setupConstraintComponent(dataTypeConstraintComponent);
        view.setupListComponent(dataTypeListComponent);
        view.setDataType(getDataType());
    }

    void refresh() {
        dataTypeSelectComponent.refresh();
        dataTypeSelectComponent.init(this, getDataType());
        dataTypeConstraintComponent.refreshView();
        view.setName(getDataType().getName());
        setupListComponent();
        setupConstraintComponent();
    }

    public DataType getDataType() {
        return dataType;
    }

    public boolean isReadOnly() {
        return getDataType().isReadOnly();
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

    public void expand() {
        view.expand();
    }

    public void collapse() {
        view.collapse();
    }

    void refreshSubItems(final List<DataType> dataTypes) {

        dataTypeList.refreshSubItemsFromListItem(this, dataTypes);

        view.enableFocusMode();
        view.toggleArrow(!dataTypes.isEmpty());
    }

    public void enableEditMode() {

        if (view.isOnFocusMode()) {
            return;
        }

        oldName = getDataType().getName();
        oldType = getDataType().getType();
        oldConstraint = getDataType().getConstraint();
        oldIsList = getDataType().isList();
        oldConstraintType = getDataType().getConstraintType();

        view.showSaveButton();
        view.showDataTypeNameInput();
        view.showListContainer();
        view.hideKebabMenu();
        view.hideListYesLabel();
        view.enableFocusMode();

        dataTypeSelectComponent.enableEditMode();
        dataTypeConstraintComponent.enableEditMode();

        editModeToggleEvent.fire(new DataTypeEditModeToggleEvent(true, this));
    }

    public void disableEditMode() {
        if (view.isOnFocusMode()) {
            discardNewDataType();
            closeEditMode();
        }
    }

    public void saveAndCloseEditMode() {

        final DataType updatedDataType = updateProperties(getDataType());

        if (updatedDataType.isValid()) {
            confirmation.ifDataTypeDoesNotHaveLostSubDataTypes(updatedDataType, doValidateDataTypeNameAndSave(updatedDataType), doDisableEditMode());
        } else {
            discardDataTypeProperties();
        }
    }

    Command doDisableEditMode() {
        return this::disableEditMode;
    }

    Command doValidateDataTypeNameAndSave(final DataType dataType) {
        return () -> nameFormatValidator.ifIsValid(dataType, doSaveAndCloseEditMode(dataType));
    }

    Command doSaveAndCloseEditMode(final DataType dataType) {
        return () -> {
            final String referenceDataTypeHash = dataTypeList.calculateParentHash(dataType);
            dataTypeList.refreshItemsByUpdatedDataTypes(persist(dataType));
            closeEditMode();

            final String newDataTypeHash = getNewDataTypeHash(dataType, referenceDataTypeHash);
            dataTypeList.fireOnDataTypeListItemUpdateCallback(newDataTypeHash);
            insertNewFieldIfDataTypeIsStructure(newDataTypeHash);
            fireDataChangedEvent();
        };
    }

    void fireDataChangedEvent() {
        dataTypeChangedEvent.fire(new DataTypeChangedEvent());
    }

    void insertNewFieldIfDataTypeIsStructure(final String hash) {
        if (isStructureType() && getDataType().getSubDataTypes().isEmpty()) {
            dataTypeList.insertNestedField(hash);
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

        final DataType oldDataType = discardDataTypeProperties();

        view.setDataType(oldDataType);

        setupListComponent();
        setupSelectComponent();
        setupConstraintComponent();
        refreshSubItems(oldDataType.getSubDataTypes());
    }

    DataType discardDataTypeProperties() {
        return dataTypeManager
                .withDataType(getDataType())
                .withName(getOldName())
                .withType(getOldType())
                .withConstraint(getOldConstraint())
                .withConstraintType(getOldConstraintType())
                .asList(getOldIsList())
                .get();
    }

    void closeEditMode() {

        view.hideDataTypeNameInput();
        view.hideListContainer();
        view.showEditButton();
        view.showKebabMenu();
        view.disableFocusMode();

        refreshListYesLabel();

        dataTypeSelectComponent.disableEditMode();
        dataTypeConstraintComponent.disableEditMode();

        editModeToggleEvent.fire(new DataTypeEditModeToggleEvent(false, this));
    }

    void refreshListYesLabel() {
        if (getDataType().isList()) {
            view.showListYesLabel();
        } else {
            view.hideListYesLabel();
        }
    }

    public void remove() {
        confirmation.ifIsNotReferencedDataType(getDataType(), doRemove());
    }

    Command doRemove() {
        return () -> {

            final List<DataType> destroyedDataTypes = getDataType().destroy();
            final List<DataType> removedDataTypes = removeTopLevelDataTypes(destroyedDataTypes);

            destroyedDataTypes.removeAll(removedDataTypes);

            dataTypeList.refreshItemsByUpdatedDataTypes(destroyedDataTypes);

            fireDataChangedEvent();
        };
    }

    List<DataType> removeTopLevelDataTypes(final List<DataType> destroyedDataTypes) {
        return destroyedDataTypes.stream()
                .filter(dataType -> dataType.isTopLevel() && (isDestroyedDataType(dataType) || isAReferenceToDestroyedDataType(dataType)))
                .peek(dataTypeList::removeItem)
                .collect(Collectors.toList());
    }

    private boolean isDestroyedDataType(final DataType dataType) {
        return Objects.equals(dataType.getUUID(), getDataType().getUUID());
    }

    private boolean isAReferenceToDestroyedDataType(final DataType dataType) {
        return getDataType().isTopLevel() && Objects.equals(dataType.getType(), getDataType().getName());
    }

    DataType updateProperties(final DataType dataType) {
        return dataTypeManager
                .from(dataType)
                .withName(getName())
                .withType(getType())
                .withConstraint(getConstraint())
                .withConstraintType(getConstraintType())
                .asList(isList())
                .get();
    }

    private String getConstraintType() {
        final ConstraintType constraint = dataTypeConstraintComponent.getConstraintType();
        if (constraint == null) {
            return "";
        }
        return constraint.value();
    }

    private String getName() {
        return view.getName();
    }

    public String getType() {
        return dataTypeSelectComponent.getValue();
    }

    private String getConstraint() {
        return dataTypeConstraintComponent.getValue();
    }

    private boolean isList() {
        return dataTypeListComponent.getValue();
    }

    String getOldName() {
        return oldName;
    }

    String getOldType() {
        return oldType;
    }

    String getOldConstraint() {
        return oldConstraint;
    }

    String getOldConstraintType() {
        if (oldConstraintType == null) {
            return "";
        }
        return oldConstraintType.value();
    }

    boolean getOldIsList() {
        return oldIsList;
    }

    DataTypeList getDataTypeList() {
        return dataTypeList;
    }

    public void insertFieldAbove() {

        closeEditMode();

        final DataType newDataType = newDataType();
        final String referenceDataTypeHash = dataTypeList.calculateParentHash(getDataType());
        final List<DataType> updatedDataTypes = newDataType.create(getDataType(), ABOVE);

        if (newDataType.isTopLevel()) {
            dataTypeList.insertAbove(newDataType, getDataType());
        } else {
            dataTypeList.refreshItemsByUpdatedDataTypes(updatedDataTypes);
        }

        enableEditModeAndUpdateCallbacks(getNewDataTypeHash(newDataType, referenceDataTypeHash));
    }

    public void insertFieldBelow() {

        closeEditMode();

        final DataType newDataType = newDataType();
        final String referenceDataTypeHash = dataTypeList.calculateParentHash(getDataType());
        final List<DataType> updatedDataTypes = newDataType.create(getDataType(), BELOW);

        if (newDataType.isTopLevel()) {
            dataTypeList.insertBelow(newDataType, getDataType());
        } else {
            dataTypeList.refreshItemsByUpdatedDataTypes(updatedDataTypes);
        }

        enableEditModeAndUpdateCallbacks(getNewDataTypeHash(newDataType, referenceDataTypeHash));
    }

    public void insertNestedField() {

        closeEditMode();
        expand();

        final DataType newDataType = newDataType();
        final String referenceDataTypeHash = dataTypeList.calculateHash(getDataType());
        final List<DataType> updatedDataTypes = newDataType.create(getDataType(), NESTED);
        dataTypeList.refreshItemsByUpdatedDataTypes(updatedDataTypes);

        enableEditModeAndUpdateCallbacks(getNewDataTypeHash(newDataType, referenceDataTypeHash));
    }

    void enableEditModeAndUpdateCallbacks(final String dataTypeHash) {
        dataTypeList.enableEditMode(dataTypeHash);
        dataTypeList.fireOnDataTypeListItemUpdateCallback(dataTypeHash);
    }

    String getNewDataTypeHash(final DataType newDataType,
                              final String referenceDataTypeHash) {
        return Stream
                .of(referenceDataTypeHash, newDataType.getName())
                .filter(s -> !isEmpty(s))
                .collect(Collectors.joining("."));
    }

    private DataType newDataType() {
        return dataTypeManager.fromNew().get();
    }

    void refreshConstraintComponent() {
        if (isBooleanType() || isStructureType() || isContextType() || isList()) {
            dataTypeConstraintComponent.disable();
        } else {
            dataTypeConstraintComponent.enable();
        }
    }

    private boolean isBooleanType() {
        return Objects.equals(BOOLEAN.getName(), getType());
    }

    private boolean isStructureType() {
        return Objects.equals(dataTypeManager.structure(), getType());
    }

    private boolean isContextType() {
        return Objects.equals(CONTEXT.getName(), getType());
    }

    public interface View extends UberElemental<DataTypeListItem> {

        void setDataType(final DataType dataType);

        void toggleArrow(final boolean show);

        void expand();

        void collapse();

        void showEditButton();

        void showSaveButton();

        void setupSelectComponent(final DataTypeSelect typeSelect);

        void setupConstraintComponent(final DataTypeConstraint dataTypeConstraintComponent);

        void setupListComponent(final SmallSwitchComponent dataTypeListComponent);

        void showListContainer();

        void hideListContainer();

        void showListYesLabel();

        void hideListYesLabel();

        boolean isCollapsed();

        void hideDataTypeNameInput();

        void showDataTypeNameInput();

        void enableFocusMode();

        void disableFocusMode();

        boolean isOnFocusMode();

        String getName();

        void setName(final String name);

        void hideKebabMenu();

        void showKebabMenu();
    }
}
