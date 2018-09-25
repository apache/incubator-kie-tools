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
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeStore;
import org.kie.workbench.common.dmn.client.editors.types.persistence.ItemDefinitionRecordEngine;
import org.kie.workbench.common.dmn.client.editors.types.persistence.ItemDefinitionStore;
import org.uberfire.commons.uuid.UUID;

import static java.util.stream.Collectors.toList;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;
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

    private DataType dataType;

    private ItemDefinition itemDefinition;

    private BuiltInType builtInType;

    @Inject
    public DataTypeManager(final TranslationService translationService,
                           final ItemDefinitionRecordEngine recordEngine,
                           final ItemDefinitionStore itemDefinitionStore,
                           final DataTypeStore dataTypeStore,
                           final ItemDefinitionUtils itemDefinitionUtils,
                           final ManagedInstance<DataTypeManager> dataTypeManagers) {
        this.translationService = translationService;
        this.recordEngine = recordEngine;
        this.itemDefinitionStore = itemDefinitionStore;
        this.dataTypeStore = dataTypeStore;
        this.itemDefinitionUtils = itemDefinitionUtils;
        this.dataTypeManagers = dataTypeManagers;
    }

    public DataTypeManager from(final ItemDefinition itemDefinition) {

        this.dataType = makeDataType();
        this.itemDefinition = checkNotNull("itemDefinition", itemDefinition);

        return asStandard();
    }

    public DataTypeManager from(final BuiltInType builtInType) {

        this.dataType = makeDataType();
        this.builtInType = checkNotNull("builtInType", builtInType);

        return asDefault();
    }

    public DataTypeManager from(final DataType dataType) {

        this.dataType = dataType;
        this.itemDefinition = getItemDefinition(dataType);

        return this;
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

    private DataTypeManager asDefault() {
        return this
                .withUUID()
                .withNoName()
                .withBuiltInType();
    }

    private DataTypeManager asStandard() {
        return this
                .withUUID()
                .withParentUUID(TOP_LEVEL_PARENT_UUID)
                .withItemDefinitionName()
                .withItemDefinitionType()
                .withItemDefinitionSubDataTypes()
                .withIndexedItemDefinition();
    }

    private DataTypeManager withUUID() {
        dataType.setUUID(UUID.uuid());
        return this;
    }

    private DataTypeManager withNoName() {
        dataType.setName(none());
        return this;
    }

    private DataTypeManager withBuiltInType() {
        dataType.setType(builtInType.getName());
        return this;
    }

    private DataTypeManager withItemDefinitionName() {
        return withName(itemDefinitionName(itemDefinition));
    }

    private DataTypeManager withItemDefinitionType() {
        return withType(itemDefinitionType(itemDefinition));
    }

    private DataTypeManager withItemDefinitionSubDataTypes() {
        return withSubDataTypes(createSubDataTypesFromItemDefinition());
    }

    private DataTypeManager withIndexedItemDefinition() {

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
        final Optional<ItemDefinition> existingItemDefinition = findByName(itemDefinitionType(itemDefinition));
        final boolean existingDataType = existingItemDefinition.isPresent();

        if (itemComponent.isEmpty()) {
            return createSubDataTypes(existingDataType ? existingItemDefinition.get().getItemComponent() : new ArrayList<>());
        } else {
            return createSubDataTypes(itemComponent);
        }
    }

    public List<DataType> makeExternalDataTypes(final String typeName) {

        final Optional<ItemDefinition> existingItemDefinition = findByName(typeName);

        if (existingItemDefinition.isPresent()) {
            return getItemDefinitionWithItemComponent(existingItemDefinition.get())
                    .getItemComponent()
                    .stream()
                    .map(this::createSubDataType)
                    .collect(toList());
        } else {
            return new ArrayList<>();
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
                .from(itemDefinition)
                .withParentUUID(getDataTypeUUID().orElseThrow(() -> new UnsupportedOperationException("A parent data type must have an UUID.")))
                .get();
    }

    private String none() {
        return translationService.format(DataTypeManager_None);
    }

    DataTypeManager anotherManager() {
        return dataTypeManagers.get();
    }

    Optional<String> getDataTypeUUID() {
        return Optional.ofNullable(dataType.getUUID());
    }

    private DataType makeDataType() {
        return new DataType(recordEngine);
    }

    private Optional<ItemDefinition> findByName(final String typeName) {
        return itemDefinitionUtils.findByName(typeName);
    }
}
