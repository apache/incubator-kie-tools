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

package org.kie.workbench.common.dmn.client.editors.types.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import org.kie.workbench.common.dmn.client.editors.types.DataTypesPage;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;

/**
 * Stores all Data Types loaded in the {@link DataTypesPage}.
 * <p>
 * All entries are indexed by their own UUID.
 */
@ApplicationScoped
public class DataTypeStore {

    private Map<String, DataType> dataTypes = new HashMap<>();

    public DataType get(final String uuid) {
        return dataTypes.get(uuid);
    }

    public void index(final String uuid,
                      final DataType dataType) {
        dataTypes.put(uuid, dataType);
    }

    public void clear() {
        dataTypes.clear();
    }

    public int size() {
        return dataTypes.size();
    }

    public List<DataType> getTopLevelDataTypes() {
        return all()
                .stream()
                .filter(DataType::isTopLevel)
                .collect(Collectors.toList());
    }

    public List<DataType> all() {
        return new ArrayList<>(dataTypes.values());
    }

    public void unIndex(final String uuid) {
        dataTypes.remove(uuid);
        subDataTypesUUID(uuid).forEach(this::unIndex);
    }

    private List<String> subDataTypesUUID(final String uuid) {
        return all()
                .stream()
                .filter(dataType -> Objects.equals(dataType.getParentUUID(), uuid))
                .map(DataType::getUUID)
                .collect(Collectors.toList());
    }
}
