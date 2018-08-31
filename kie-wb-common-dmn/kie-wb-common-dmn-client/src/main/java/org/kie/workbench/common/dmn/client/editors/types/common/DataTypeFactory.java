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
import java.util.stream.Stream;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.client.editors.types.DataType;
import org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeStore;
import org.kie.workbench.common.dmn.client.editors.types.persistence.ItemDefinitionRecordEngine;
import org.kie.workbench.common.dmn.client.editors.types.persistence.ItemDefinitionStore;
import org.uberfire.commons.uuid.UUID;

import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeFactory_None;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeFactory_Structure;

@Dependent
public class DataTypeFactory {

    private static final String ROOT_UUID = "";

    private final ItemDefinitionUtils itemDefinitionUtils;

    private final TranslationService translationService;

    private final ItemDefinitionRecordEngine recordEngine;

    private final ItemDefinitionStore itemDefinitionStore;

    private final DataTypeStore dataTypeStore;

    @Inject
    public DataTypeFactory(final ItemDefinitionUtils itemDefinitionUtils,
                           final TranslationService translationService,
                           final ItemDefinitionRecordEngine recordEngine,
                           final ItemDefinitionStore itemDefinitionStore,
                           final DataTypeStore dataTypeStore) {
        this.itemDefinitionUtils = itemDefinitionUtils;
        this.translationService = translationService;
        this.recordEngine = recordEngine;
        this.itemDefinitionStore = itemDefinitionStore;
        this.dataTypeStore = dataTypeStore;
    }

    public DataType makeStandardDataType(final ItemDefinition itemDefinition) {
        final boolean isExternal = false;
        return makeDataType(ROOT_UUID, itemDefinition, isExternal);
    }

    public DataType makeStandardDataType(final String parentUUID,
                                         final ItemDefinition itemDefinition) {
        final boolean isExternal = false;
        return makeDataType(parentUUID, itemDefinition, isExternal);
    }

    public DataType makeExternalDataType(final String parentUUID,
                                         final ItemDefinition itemDefinition) {
        final boolean isExternal = true;
        return makeDataType(parentUUID, itemDefinition, isExternal);
    }

    public DataType makeDefaultDataType(final BuiltInType builtInType) {
        return new DataType(none(), builtInType.getName());
    }

    private DataType makeDataType(final String parentUUID,
                                  final ItemDefinition itemDefinition,
                                  final boolean isExternal) {

        final String uuid = UUID.uuid();
        final String name = extractName(itemDefinition);
        final String type = extractType(itemDefinition);
        final List<DataType> subTypes = extractSubDataTypes(uuid, itemDefinition);
        final boolean isBasic = isBasic(itemDefinition);
        final boolean isDefault = isDefault(itemDefinition);
        final DataType dataType = new DataType(uuid, parentUUID, name, type, subTypes, isBasic, isExternal, isDefault, recordEngine);

        itemDefinitionStore.index(dataType.getUUID(), itemDefinition);
        dataTypeStore.index(dataType.getUUID(), dataType);

        return dataType;
    }

    private String extractName(final ItemDefinition itemDefinition) {
        final Optional<Name> name = Optional.ofNullable(itemDefinition.getName());
        return name.isPresent() ? name.get().getValue() : none();
    }

    private String extractType(final ItemDefinition itemDefinition) {
        final Optional<QName> typeRef = Optional.ofNullable(itemDefinition.getTypeRef());
        return typeRef.isPresent() ? typeRef.get().getLocalPart() : structure();
    }

    private boolean isBasic(final ItemDefinition itemDefinition) {
        return Optional.ofNullable(itemDefinition.getTypeRef()).isPresent();
    }

    public boolean isDefault(final ItemDefinition itemDefinition) {
        final String type = extractType(itemDefinition);
        return Stream
                .of(BuiltInType.values())
                .anyMatch(dataType -> Objects.equals(dataType.getName(), type));
    }

    private List<DataType> extractSubDataTypes(final String patternUUID,
                                               final ItemDefinition itemDefinition) {

        final List<ItemDefinition> itemComponent = itemDefinition.getItemComponent();
        final Optional<ItemDefinition> existingItemDefinition = findItemDefinition(itemDefinition);
        final boolean existingDataType = existingItemDefinition.isPresent();

        if (itemComponent.isEmpty()) {
            if (existingDataType) {
                return existingItemDefinition
                        .get()
                        .getItemComponent()
                        .stream()
                        .map(item -> makeExternalDataType(patternUUID, item))
                        .collect(Collectors.toList());
            } else {
                return new ArrayList<>();
            }
        } else {
            return itemComponent
                    .stream()
                    .map(definition -> makeStandardDataType(patternUUID, definition))
                    .collect(Collectors.toList());
        }
    }

    private Optional<ItemDefinition> findItemDefinition(final ItemDefinition itemDefinition) {
        final String itemDefinitionName = extractType(itemDefinition);
        return itemDefinitionUtils.findByName(itemDefinitionName);
    }

    private String none() {
        return translationService.format(DataTypeFactory_None);
    }

    private String structure() {
        return translationService.format(DataTypeFactory_Structure);
    }
}
