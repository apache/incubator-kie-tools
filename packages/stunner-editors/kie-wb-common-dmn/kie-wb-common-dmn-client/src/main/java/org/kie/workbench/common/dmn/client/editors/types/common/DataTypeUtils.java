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

package org.kie.workbench.common.dmn.client.editors.types.common;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeStore;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;

import static java.util.stream.Collectors.toList;
import static org.kie.workbench.common.dmn.client.editors.types.common.DataTypeKind.BUILT_IN;
import static org.kie.workbench.common.dmn.client.editors.types.common.DataTypeKind.CUSTOM;
import static org.kie.workbench.common.dmn.client.editors.types.common.DataTypeKind.INCLUDED;
import static org.kie.workbench.common.dmn.client.editors.types.common.DataTypeKind.STRUCTURE;

@Dependent
public class DataTypeUtils {

    private final DataTypeStore dataTypeStore;

    private final DataTypeManager dataTypeManager;

    private final DMNGraphUtils dmnGraphUtils;

    @Inject
    public DataTypeUtils(final DataTypeStore dataTypeStore,
                         final DataTypeManager dataTypeManager,
                         final DMNGraphUtils dmnGraphUtils) {
        this.dataTypeStore = dataTypeStore;
        this.dataTypeManager = dataTypeManager;
        this.dmnGraphUtils = dmnGraphUtils;
    }

    public DataType getTopLevelParent(final DataType dataType) {
        final String parentUUID = dataType.getParentUUID();
        final Optional<DataType> parent = Optional.ofNullable(dataTypeStore.get(parentUUID));
        return parent.map(this::getTopLevelParent).orElse(dataType);
    }

    public List<DataType> defaultDataTypes() {
        return Stream
                .of(BuiltInType.values())
                .filter(builtInType -> !builtInType.equals(BuiltInType.UNDEFINED))
                .map(bit -> dataTypeManager.from(bit).get())
                .sorted(Comparator.comparing(DataType::getType))
                .collect(toList());
    }

    public List<DataType> customDataTypes() {
        return dataTypeStore
                .getTopLevelDataTypes()
                .stream()
                .sorted(Comparator.comparing(DataType::getName))
                .collect(Collectors.toList());
    }

    public DataTypeKind getDataTypeKind(final String typeName) {
        if (isIncludedType(typeName)) {
            return INCLUDED;
        }
        return findDataTypeByName(typeName)
                .map(dataType -> isStructure(dataType) ? STRUCTURE : CUSTOM)
                .orElse(BUILT_IN);
    }

    private Optional<DataType> findDataTypeByName(final String typeName) {
        return dataTypeStore
                .getTopLevelDataTypes()
                .stream()
                .filter(dt -> dt.getName().equals(typeName))
                .findFirst();
    }

    private boolean isIncludedType(final String typeName) {
        return getImportNames().stream().anyMatch(typeName::startsWith);
    }

    private boolean isStructure(final DataType dataType) {
        return Objects.equals(dataTypeManager.structure(), dataType.getType());
    }

    private List<String> getImportNames() {
        return dmnGraphUtils
                .getModelDefinitions()
                .getImport()
                .stream().map(i -> i.getName().getValue())
                .collect(toList());
    }
}
