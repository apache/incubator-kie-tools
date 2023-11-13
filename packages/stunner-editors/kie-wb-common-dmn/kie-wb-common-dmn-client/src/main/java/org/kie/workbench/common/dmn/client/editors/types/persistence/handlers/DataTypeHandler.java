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

package org.kie.workbench.common.dmn.client.editors.types.persistence.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManager;
import org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeStore;
import org.kie.workbench.common.dmn.client.editors.types.persistence.ItemDefinitionRecordEngine;

public class DataTypeHandler {

    final DataTypeStore dataTypeStore;

    final DataTypeManager dataTypeManager;

    ItemDefinitionRecordEngine recordEngine;

    DataTypeHandler(final DataTypeStore dataTypeStore,
                    final DataTypeManager dataTypeManager) {
        this.dataTypeStore = dataTypeStore;
        this.dataTypeManager = dataTypeManager;
    }

    public void init(final ItemDefinitionRecordEngine recordEngine) {
        this.recordEngine = recordEngine;
    }

    Optional<DataType> getClosestTopLevelDataType(final DataType dataType) {

        final Optional<DataType> closestTopLevelDataType = getClosestTopLevel(parent(dataType));

        if (closestTopLevelDataType.isPresent()) {

            final DataType topLevel = closestTopLevelDataType.get();
            final String type = topLevel.isTopLevel() ? topLevel.getName() : topLevel.getType();

            return findTopLevelDataTypeByName(type);
        }
        return Optional.empty();
    }

    List<DataType> getSubDataTypesByType(final String type) {
        return new DataTypeQuery().where(dataType -> Objects.equals(dataType.getType(), type)).collect();
    }

    List<DataType> forEachSubDataTypesByType(final String type,
                                             final Consumer<DataType> consumer) {
        return new DataTypeQuery().where(dataType -> Objects.equals(dataType.getType(), type)).apply(consumer).collect();
    }

    List<DataType> forEachSubDataTypesByTypeOrName(final String typeOrName,
                                                   final Consumer<DataType> consumer) {
        return new DataTypeQuery()
                .where(dataType -> Objects.equals(dataType.getType(), typeOrName) || Objects.equals(dataType.getName(), typeOrName))
                .apply(consumer)
                .collect();
    }

    DataType parent(final DataType dataType) {
        return dataTypeStore.get(dataType.getParentUUID());
    }

    ItemDefinitionRecordEngine getRecordEngine() {
        return recordEngine;
    }

    boolean isStructure(final DataType dataType) {
        return Objects.equals(dataType.getType(), dataTypeManager.structure());
    }

    private Optional<DataType> findTopLevelDataTypeByName(final String name) {
        final List<DataType> dataTypes = topLevelDataTypes();
        return dataTypes
                .stream()
                .filter(dataType -> Objects.equals(dataType.getName(), name))
                .findFirst();
    }

    private Optional<DataType> getClosestTopLevel(final DataType dataType) {
        if (dataType == null) {
            return Optional.empty();
        } else if (isClosestTopLevel(dataType)) {
            return Optional.of(dataType);
        } else {
            return getClosestTopLevel(parent(dataType));
        }
    }

    private boolean isClosestTopLevel(final DataType dataType) {

        final boolean isTopLevel = dataType.isTopLevel();
        final boolean isTopLevelField = topLevelDataTypes()
                .stream()
                .anyMatch(topLevel -> Objects.equals(topLevel.getName(), dataType.getType()));

        return isTopLevel || isTopLevelField;
    }

    private List<DataType> topLevelDataTypes() {
        return dataTypeStore.getTopLevelDataTypes();
    }

    class DataTypeQuery {

        private Function<DataType, Boolean> condition = (dataType) -> true;

        private Consumer<DataType> consumer = (dataType) -> {/* Default consumer */};

        DataTypeQuery where(final Function<DataType, Boolean> condition) {
            this.condition = condition;
            return this;
        }

        DataTypeQuery apply(final Consumer<DataType> consumer) {
            this.consumer = consumer;
            return this;
        }

        List<DataType> collect() {
            return collect(topLevelDataTypes());
        }

        private List<DataType> collect(final List<DataType> dataTypes) {
            final List<DataType> updated = new ArrayList<>();

            for (final DataType dataType : dataTypes) {
                if (condition.apply(dataType)) {
                    updated.add(dataType);
                    consumer.accept(dataType);
                }
                updated.addAll(collect(dataType.getSubDataTypes()));
            }

            return updated;
        }
    }
}
