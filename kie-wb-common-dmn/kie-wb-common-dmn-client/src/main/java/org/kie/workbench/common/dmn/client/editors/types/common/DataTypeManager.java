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

package org.kie.workbench.common.dmn.client.editors.types.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.v1_1.ConstraintType;
import org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeStore;
import org.kie.workbench.common.dmn.client.editors.types.persistence.ItemDefinitionRecordEngine;
import org.kie.workbench.common.dmn.client.editors.types.persistence.ItemDefinitionStore;
import org.kie.workbench.common.dmn.client.editors.types.persistence.validation.DataTypeNameValidator;
import org.uberfire.commons.uuid.UUID;

import static java.util.stream.Collectors.toList;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;
import static org.kie.workbench.common.dmn.client.editors.types.common.BuiltInTypeUtils.isDefault;
import static org.kie.workbench.common.dmn.client.editors.types.common.DataType.TOP_LEVEL_PARENT_UUID;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeManager_None;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeManager_Structure;

/**
 * Manages all operations that change a Data Type.
 * <p>
 * A Data Type is a plain entity that must be simple as possible.
 * Thus, the 'DataTypeManager' encapsulate every interaction.
 */
@Dependent
public class DataTypeManager {

    private final TranslationService translationService;

    private final ItemDefinitionRecordEngine recordEngine;

    private final ItemDefinitionStore itemDefinitionStore;

    private final DataTypeStore dataTypeStore;

    private final ItemDefinitionUtils itemDefinitionUtils;

    private final ManagedInstance<DataTypeManager> dataTypeManagers;

    private final DataTypeNameValidator dataTypeNameValidator;

    private final DataTypeManagerStackStore typeStack;

    private DataType dataType;

    private ItemDefinition itemDefinition;

    @Inject
    public DataTypeManager(final TranslationService translationService,
                           final ItemDefinitionRecordEngine recordEngine,
                           final ItemDefinitionStore itemDefinitionStore,
                           final DataTypeStore dataTypeStore,
                           final ItemDefinitionUtils itemDefinitionUtils,
                           final ManagedInstance<DataTypeManager> dataTypeManagers,
                           final DataTypeNameValidator dataTypeNameValidator,
                           final DataTypeManagerStackStore typeStack) {
        this.translationService = translationService;
        this.recordEngine = recordEngine;
        this.itemDefinitionStore = itemDefinitionStore;
        this.dataTypeStore = dataTypeStore;
        this.itemDefinitionUtils = itemDefinitionUtils;
        this.dataTypeManagers = dataTypeManagers;
        this.dataTypeNameValidator = dataTypeNameValidator;
        this.typeStack = typeStack;
    }

    public DataTypeManager fromNew() {
        return this
                .newDataType()
                .withUUID()
                .withParentUUID(TOP_LEVEL_PARENT_UUID)
                .withNoName()
                .withNoConstraint()
                .asList(false)
                .withDefaultType();
    }

    public DataTypeManager from(final ItemDefinition itemDefinition) {
        return this
                .newDataType()
                .withUUID()
                .withParentUUID(TOP_LEVEL_PARENT_UUID)
                .withItemDefinition(itemDefinition)
                .withItemDefinitionName()
                .withItemDefinitionType()
                .withItemDefinitionConstraint()
                .withItemDefinitionCollection()
                .withItemDefinitionSubDataTypes()
                .withIndexedItemDefinition();
    }

    public DataTypeManager from(final BuiltInType builtInType) {
        return this
                .newDataType()
                .withUUID()
                .withName(none())
                .withBuiltInType(builtInType);
    }

    public DataTypeManager from(final DataType dataType) {
        return this
                .withDataType(dataType)
                .withItemDefinition(getItemDefinition(dataType));
    }

    public DataTypeManager withParentUUID(final String parentUUID) {
        dataType.setParentUUID(parentUUID);
        return this;
    }

    public DataTypeManager withName(final String name) {
        dataType.setName(name);
        return this;
    }

    public DataTypeManager withType(final String type) {
        dataType.setType(type);
        return this;
    }

    public DataTypeManager withConstraint(final String constraint) {
        dataType.setConstraint(constraint);
        return this;
    }

    public DataTypeManager withConstraintType(final String constraintType) {
        dataType.setConstraintType(ConstraintType.fromString(constraintType));
        return this;
    }

    public DataTypeManager asList(final boolean isCollection) {
        dataType.setAsList(isCollection);
        return this;
    }

    public DataTypeManager withNoConstraint() {
        return withConstraint("");
    }

    public DataTypeManager withDataType(final DataType dataType) {
        this.dataType = dataType;
        return this;
    }

    private DataTypeManager withDefaultType() {
        return withType(BuiltInType.ANY.getName());
    }

    DataTypeManager newDataType() {
        return withDataType(makeDataType());
    }

    public DataTypeManager withItemDefinition(final ItemDefinition itemDefinition) {
        this.itemDefinition = checkNotNull("itemDefinition", itemDefinition);
        return this;
    }

    private DataTypeManager withBuiltInType(final BuiltInType builtInType) {
        final String type = checkNotNull("builtInType", builtInType).getName();
        return withType(type);
    }

    public DataTypeManager withRefreshedSubDataTypes(final String newType) {
        return withSubDataTypes(makeExternalDataTypes(newType));
    }

    public DataTypeManager withSubDataTypes(final List<DataType> dataTypes) {
        dataType.getSubDataTypes().forEach(dataType -> {
            dataTypeStore.unIndex(dataType.getUUID());
            itemDefinitionStore.unIndex(dataType.getUUID());
        });
        dataType.setSubDataTypes(dataTypes);
        return this;
    }

    DataTypeManager withUUID() {
        dataType.setUUID(UUID.uuid());
        return this;
    }

    public DataTypeManager withNoName() {
        return withUniqueName(none());
    }

    DataTypeManager withUniqueName(final String name) {
        return withUniqueName(name, 1);
    }

    private DataTypeManager withUniqueName(final String name,
                                           final int nameSuffix) {

        withName(nameSuffix == 1 ? name : name + " - " + nameSuffix);

        if (dataTypeNameValidator.isNotUnique(get())) {
            return withUniqueName(name, nameSuffix + 1);
        } else {
            return this;
        }
    }

    DataTypeManager withItemDefinitionName() {
        return withName(itemDefinitionName(itemDefinition));
    }

    DataTypeManager withItemDefinitionConstraint() {
        final DataTypeManager dt = withConstraint(itemDefinitionUtils.getConstraintText(itemDefinition));
        if (itemDefinition.getAllowedValues() != null && itemDefinition.getAllowedValues().getConstraintType() != null) {
            return dt.withConstraintType(itemDefinition.getAllowedValues().getConstraintType().value());
        }

        return dt;
    }

    DataTypeManager withItemDefinitionCollection() {
        return asList(itemDefinition.isIsCollection());
    }

    DataTypeManager withItemDefinitionType() {
        return withType(itemDefinitionType(itemDefinition));
    }

    public DataTypeManager withItemDefinitionSubDataTypes() {
        return withSubDataTypes(createSubDataTypesFromItemDefinition());
    }

    public DataTypeManager withIndexedItemDefinition() {

        itemDefinitionStore.index(dataType.getUUID(), itemDefinition);
        dataTypeStore.index(dataType.getUUID(), dataType);

        return this;
    }

    public DataType get() {
        return dataType;
    }

    private String itemDefinitionName(final ItemDefinition itemDefinition) {
        final Optional<Name> name = Optional.ofNullable(itemDefinition.getName());
        return name.isPresent() ? name.get().getValue() : none();
    }

    private String itemDefinitionType(final ItemDefinition itemDefinition) {
        final Optional<QName> typeRef = Optional.ofNullable(itemDefinition.getTypeRef());
        return typeRef.isPresent() ? typeRef.get().getLocalPart() : structure();
    }

    private ItemDefinition getItemDefinition(final DataType dataType) {
        final Optional<ItemDefinition> itemDefinition = Optional.ofNullable(itemDefinitionStore.get(dataType.getUUID()));
        return itemDefinition.orElseThrow(() -> new UnsupportedOperationException("The data type must have an indexed ItemDefinition."));
    }

    private List<DataType> createSubDataTypesFromItemDefinition() {

        final List<ItemDefinition> itemComponent = itemDefinition.getItemComponent();
        final String type = itemDefinitionType(itemDefinition);
        final Optional<ItemDefinition> existingItemDefinition = itemDefinitionUtils.findByName(type);
        final boolean existingDataType = existingItemDefinition.isPresent();

        if (isTypeAlreadyRepresented(type)) {
            return new ArrayList<>();
        }

        if (itemComponent.isEmpty()) {
            return createSubDataTypes(existingDataType ? existingItemDefinition.get().getItemComponent() : new ArrayList<>());
        } else {
            return createSubDataTypes(itemComponent);
        }
    }

    public List<DataType> makeExternalDataTypes(final String typeName) {

        final Optional<ItemDefinition> existingItemDefinition = itemDefinitionUtils.findByName(typeName);

        if (isTypeAlreadyRepresented(typeName) || !existingItemDefinition.isPresent()) {
            return new ArrayList<>();
        } else {
            return existingItemDefinition
                    .get()
                    .getItemComponent()
                    .stream()
                    .map(this::createSubDataType)
                    .collect(toList());
        }
    }

    ItemDefinition getItemDefinitionWithItemComponent(final ItemDefinition itemDefinition) {
        if (itemDefinition.getTypeRef() != null) {
            final Optional<ItemDefinition> definition = findByName(itemDefinitionType(itemDefinition));
            if (definition.isPresent()) {
                return getItemDefinitionWithItemComponent(definition.get());
            }
        }
        return itemDefinition;
    }

    public String structure() {
        return translationService.format(DataTypeManager_Structure);
    }

    private List<DataType> createSubDataTypes(final List<ItemDefinition> itemComponent) {
        return itemComponent.stream().map(this::createSubDataType).collect(Collectors.toList());
    }

    DataType createSubDataType(final ItemDefinition itemDefinition) {
        return anotherManager()
                .newDataType()
                .withUUID()
                .withParentUUID(getDataTypeUUID().orElseThrow(() -> new UnsupportedOperationException("A parent data type must have an UUID.")))
                .withItemDefinition(itemDefinition)
                .withItemDefinitionName()
                .withItemDefinitionType()
                .withItemDefinitionConstraint()
                .withItemDefinitionCollection()
                .withTypeStack(getSubDataTypeStack())
                .withItemDefinitionSubDataTypes()
                .withIndexedItemDefinition()
                .get();
    }

    private String none() {
        return translationService.format(DataTypeManager_None);
    }

    public DataTypeManager asStructure() {
        return withType(structure());
    }

    DataTypeManager anotherManager() {
        return dataTypeManagers.get();
    }

    Optional<String> getDataTypeUUID() {
        return Optional.ofNullable(getDataType().getUUID());
    }

    /**
     * It identifies if a given type were already represented in the current stack.
     * See {@link DataTypeManagerStackStore}
     */
    boolean isTypeAlreadyRepresented(final String type) {
        return getTypeStack().contains(type);
    }

    List<String> getSubDataTypeStack() {
        final List<String> subDataTypeStack = new ArrayList<>(getTypeStack());
        getStackType().ifPresent(subDataTypeStack::add);
        return subDataTypeStack;
    }

    DataTypeManager withTypeStack(final List<String> typeStack) {
        final String dataTypeUUID = getDataTypeUUID().orElseThrow(() -> new UnsupportedOperationException("A data type must have an UUID to be inserted in the type stack."));
        this.typeStack.put(dataTypeUUID, typeStack);
        return this;
    }

    Optional<String> getStackType() {

        final String type = getDataType().getType();
        final String name = getDataType().getName();

        if (getDataType().isTopLevel()) {
            return Optional.ofNullable(name);
        } else {
            if (!Objects.equals(type, structure()) && !isDefault(type)) {
                return Optional.ofNullable(type);
            }
        }
        return Optional.empty();
    }

    private List<String> getTypeStack() {
        final String dataTypeUUID = getDataTypeUUID().orElseThrow(() -> new UnsupportedOperationException("A data type must have an UUID to be access the type stack."));
        return typeStack.get(dataTypeUUID);
    }

    DataType getDataType() {
        return dataType;
    }

    private DataType makeDataType() {
        return new DataType(recordEngine);
    }

    private Optional<ItemDefinition> findByName(final String typeName) {
        return itemDefinitionUtils.findByName(typeName);
    }
}
