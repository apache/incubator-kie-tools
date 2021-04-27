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

package org.kie.workbench.common.dmn.client.editors.types.persistence.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.common.base.Strings;
import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManager;
import org.kie.workbench.common.dmn.client.editors.types.persistence.CreationType;
import org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeStore;

import static org.kie.workbench.common.dmn.api.editors.types.BuiltInTypeUtils.isBuiltInType;
import static org.kie.workbench.common.dmn.client.editors.types.common.DataType.TOP_LEVEL_PARENT_UUID;

@Dependent
public class DataTypeCreateHandler extends DataTypeHandler {

    @Inject
    public DataTypeCreateHandler(final DataTypeStore dataTypeStore,
                                 final DataTypeManager dataTypeManager) {
        super(dataTypeStore, dataTypeManager);
    }

    public List<DataType> append(final DataType dataType,
                                 final ItemDefinition itemDefinition) {
        final DataType named;
        if (Strings.isNullOrEmpty(dataType.getName())) {
            named = withNoName(dataType);
        } else {
            named = dataType;
        }

        final DataType updateDataType = updateDataTypeProperties(named, TOP_LEVEL_PARENT_UUID, itemDefinition);
        return recordEngine.update(updateDataType);
    }

    public List<DataType> insertSibling(final DataType dataType,
                                        final DataType reference,
                                        final CreationType creationType,
                                        final ItemDefinition itemDefinition) {

        final Optional<DataType> parentOptional = lookupAbsoluteParent(reference);

        if (parentOptional.isPresent()) {

            final DataType parent = parentOptional.get();
            final List<DataType> siblings = parent.getSubDataTypes();
            final DataType updatedDataType = updateDataTypeProperties(dataType, parent.getUUID(), itemDefinition);
            final DataType parentReference = findParentReference(reference, siblings);

            siblings.add(siblings.indexOf(parentReference) + creationType.getIndexIncrement(), updatedDataType);

            recordEngine.doUpdate(dataType, itemDefinition);

            return recordEngine.update(parent);
        } else {

            final DataType updatedDataType = updateDataTypeProperties(dataType, reference.getParentUUID(), itemDefinition);

            recordEngine.doUpdate(updatedDataType, itemDefinition);

            return new ArrayList<>();
        }
    }

    Optional<DataType> lookupAbsoluteParent(final DataType reference) {

        final Optional<DataType> optionalParent = Optional.ofNullable(parent(reference));

        if (optionalParent.isPresent()) {
            return fetchTopLevelDataType(optionalParent.get());
        }

        return Optional.empty();
    }

    private Optional<DataType> fetchTopLevelDataType(final DataType dataType) {
        if (Objects.equals(dataType.getType(), dataTypeManager.structure())) {
            return Optional.of(dataType);
        } else {
            return dataTypeStore
                    .getTopLevelDataTypes()
                    .stream()
                    .filter(dt -> Objects.equals(dt.getName(), dataType.getType()))
                    .findFirst();
        }
    }

    public List<DataType> insertNested(final DataType dataType,
                                       final DataType reference,
                                       final ItemDefinition itemDefinition) {

        final String newParentUUID = reference.getUUID();
        final DataType updatedDataType = updateDataTypeProperties(dataType, newParentUUID, itemDefinition);
        final Optional<DataType> topLevelReference = fetchTopLevelDataType(reference);
        final DataType parent = topLevelReference.orElse(reference);

        if (isBuiltInType(reference.getType()) || topLevelReference.isPresent()) {
            dataTypeManager.withDataType(parent).asStructure();
        }

        parent.getSubDataTypes().add(0, updatedDataType);

        dataTypeManager.withDataType(updatedDataType).withUniqueName(dataType.getName());

        return recordEngine.update(updatedDataType);
    }

    DataType updateDataTypeProperties(final DataType dataType,
                                      final String newParentUUID,
                                      final ItemDefinition newItemDefinition) {
        return dataTypeManager
                .withDataType(dataType)
                .withParentUUID(newParentUUID)
                .withItemDefinition(newItemDefinition)
                .withIndexedItemDefinition()
                .withItemDefinitionSubDataTypes()
                .withUniqueName()
                .get();
    }

    private DataType withNoName(final DataType dataType) {
        return dataTypeManager
                .withDataType(dataType)
                .withNoName()
                .get();
    }

    private DataType findParentReference(final DataType reference,
                                         final List<DataType> siblings) {
        return siblings
                .stream()
                .filter(dataType -> Objects.equals(dataType.getName(), reference.getName()))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("The 'siblings' (from the 'AbsoluteParent') must have a Data Type with the same name as the 'reference' instance since they represent the same type."));
    }
}
