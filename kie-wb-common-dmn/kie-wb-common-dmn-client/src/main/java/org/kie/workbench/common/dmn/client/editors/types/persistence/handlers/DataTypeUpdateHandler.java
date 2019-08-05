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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManager;
import org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeStore;
import org.kie.workbench.common.dmn.client.editors.types.persistence.ItemDefinitionStore;

import static org.kie.workbench.common.dmn.api.editors.types.BuiltInTypeUtils.isBuiltInType;

@Dependent
public class DataTypeUpdateHandler extends DataTypeHandler {

    private final ItemDefinitionStore itemDefinitionStore;

    @Inject
    public DataTypeUpdateHandler(final ItemDefinitionStore itemDefinitionStore,
                                 final DataTypeStore dataTypeStore,
                                 final DataTypeManager dataTypeManager) {
        super(dataTypeStore, dataTypeManager);

        this.itemDefinitionStore = itemDefinitionStore;
    }

    public void update(final DataType dataType) {

        final String type = dataType.getType();

        if (!isBuiltInType(type)) {
            dataTypeManager
                    .from(dataType)
                    .withRefreshedSubDataTypes(type);
        }
    }

    public List<DataType> refreshDependentDataTypes(final DataType dataType,
                                                    final String oldItemDefinitionName) {

        final List<DataType> affectedDataTypes = new ArrayList<>();

        affectedDataTypes.addAll(handleTopLevelDataTypeUpdate(dataType, oldItemDefinitionName));
        affectedDataTypes.addAll(handleNestedDataTypeFieldUpdate(dataType));

        return affectedDataTypes;
    }

    List<DataType> handleTopLevelDataTypeUpdate(final DataType dataType,
                                                final String oldItemDefinitionName) {
        return updateAllChildrenWithTheNewTypeName(dataType, oldItemDefinitionName);
    }

    List<DataType> handleNestedDataTypeFieldUpdate(final DataType dataType) {

        final List<DataType> affectedDataTypes = new ArrayList<>();

        getClosestTopLevelDataType(dataType)
                .ifPresent(topLevelUpdate -> {

                    refreshSubDataTypes(topLevelUpdate, topLevelUpdate.getName());

                    if (!isStructure(topLevelUpdate)) {
                        forEachSubDataTypesByTypeOrName(topLevelUpdate.getType(), subDataType -> {
                            refreshSubDataTypes(subDataType, topLevelUpdate.getType());
                            affectedDataTypes.add(subDataType);
                        });
                    } else {
                        affectedDataTypes.addAll(handleTopLevelDataTypeUpdate(topLevelUpdate, topLevelUpdate.getName()));
                    }

                    affectedDataTypes.add(topLevelUpdate);
                });

        return affectedDataTypes;
    }

    List<DataType> updateAllChildrenWithTheNewTypeName(final DataType dataType,
                                                       final String oldTypeName) {

        final List<DataType> affectedDataTypes = new ArrayList<>();

        if (dataType.isTopLevel()) {

            refreshSubDataTypes(dataType);

            affectedDataTypes.addAll(forEachSubDataTypesByType(oldTypeName, subDataType -> {
                refreshSubDataType(subDataType, dataType.getName());
            }));

            affectedDataTypes.add(dataType);
        }

        return affectedDataTypes;
    }

    void refreshSubDataType(final DataType dataType,
                            final String newType) {

        final ItemDefinition itemDefinition = itemDefinitionStore.get(dataType.getUUID());

        dataTypeManager.from(dataType).withType(newType);
        recordEngine.doUpdate(dataType, itemDefinition);

        refreshSubDataTypes(dataType, dataType.getType());
    }
}
