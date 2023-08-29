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

package org.kie.workbench.common.dmn.client.editors.types.listview.common;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeStore;

@ApplicationScoped
public class DataTypeStackHash {

    private final DataTypeStore dataTypeStore;

    @Inject
    public DataTypeStackHash(final DataTypeStore dataTypeStore) {
        this.dataTypeStore = dataTypeStore;
    }

    public String calculateHash(final DataType dataType) {
        return calculateHash(dataType, dataType.getName());
    }

    public String calculateParentHash(final DataType reference) {
        return getParent(reference)
                .map(dataType -> calculateHash(dataType, dataType.getName()))
                .orElse("");
    }

    private String calculateHash(final DataType dataType,
                                 final String hash) {

        final Optional<DataType> parent = getParent(dataType);

        if (!parent.isPresent()) {
            return hash;
        }

        return calculateHash(parent.get(), parent.get().getName() + "." + hash);
    }

    private Optional<DataType> getParent(final DataType dataType) {
        return Optional.ofNullable(dataTypeStore.get(dataType.getParentUUID()));
    }
}
