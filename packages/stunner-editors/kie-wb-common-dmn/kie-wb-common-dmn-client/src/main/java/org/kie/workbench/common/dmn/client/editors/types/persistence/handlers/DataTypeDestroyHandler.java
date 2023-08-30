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
import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManager;
import org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeStore;

@Dependent
public class DataTypeDestroyHandler extends DataTypeHandler {

    @Inject
    public DataTypeDestroyHandler(final DataTypeStore dataTypeStore,
                                  final DataTypeManager dataTypeManager) {
        super(dataTypeStore, dataTypeManager);
    }

    public void destroy(final DataType dataType) {

        final Optional<DataType> dataTypeParent = Optional.ofNullable(parent(dataType));

        dataTypeParent.ifPresent(parent -> {
            parent.getSubDataTypes().remove(dataType);
        });

        unIndex(dataType);
    }

    public List<DataType> refreshDependentDataTypes(final DataType dataType) {

        final List<DataType> affectedDataTypes = new ArrayList<>();

        affectedDataTypes.addAll(handleTopLevelDataTypes(dataType));
        affectedDataTypes.addAll(handleNestedDataTypes(dataType));

        return affectedDataTypes;
    }

    List<DataType> handleTopLevelDataTypes(final DataType dataType) {

        final List<DataType> affected = new ArrayList<>();

        if (!dataType.isTopLevel()) {
            return affected;
        }

        final List<DataType> dataTypesByType = getSubDataTypesByType(dataType.getName());

        for (final DataType dt : dataTypesByType) {
            final Optional<DataType> dataTypeParent = Optional.ofNullable(parent(dt));

            recordEngine.doDestroy(dt);
            affected.add(dataTypeParent.orElse(dt));
        }

        affected.add(dataType);

        return affected;
    }

    List<DataType> handleNestedDataTypes(final DataType dataType) {

        final List<DataType> affectedDataTypes = new ArrayList<>();

        getClosestTopLevelDataType(dataType).ifPresent(topLevel -> {
            final String type = topLevel.getName();
            affectedDataTypes.add(topLevel);
            if (!isStructure(topLevel)) {
                forEachSubDataTypesByTypeOrName(topLevel.getType(), affectedDataTypes::add);
            }
            forEachSubDataTypesByType(type, affectedDataTypes::add);
        });

        return affectedDataTypes;
    }

    private void unIndex(final DataType dataType) {

        final String uuid = dataType.getUUID();
        final List<DataType> subDataTypes = dataType.getSubDataTypes();

        dataTypeStore.unIndex(uuid);

        subDataTypes.forEach(this::unIndex);
    }
}
