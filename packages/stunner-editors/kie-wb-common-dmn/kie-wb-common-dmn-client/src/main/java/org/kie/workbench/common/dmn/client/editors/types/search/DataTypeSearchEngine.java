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

package org.kie.workbench.common.dmn.client.editors.types.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeStore;

import static java.util.Collections.emptyList;

@ApplicationScoped
public class DataTypeSearchEngine {

    private final DataTypeStore dataTypeStore;

    @Inject
    public DataTypeSearchEngine(final DataTypeStore dataTypeStore) {
        this.dataTypeStore = dataTypeStore;
    }

    public List<DataType> search(final String keyword) {

        final List<DataType> results = new ArrayList<>();

        for (final DataType dataType : findByName(keyword)) {
            results.addAll(getStack(dataType));
        }

        return results;
    }

    private List<DataType> getStack(final DataType dataType) {
        return getStack(new ArrayList<>(), dataType);
    }

    private List<DataType> getStack(final List<DataType> stack,
                                    final DataType dataType) {

        final DataType parent = parent(dataType);

        stack.add(dataType);

        if (dataType.isTopLevel() || Objects.isNull(parent)) {
            return stack;
        }

        return getStack(stack, parent);
    }

    private DataType parent(final DataType dataType) {
        return dataTypeStore.get(dataType.getParentUUID());
    }

    private List<DataType> findByName(final String keyword) {

        final String keywordUpCase = upCase(keyword);

        if (keywordUpCase.isEmpty()) {
            return emptyList();
        }

        return dataTypeStore
                .all()
                .stream()
                .filter(dataType -> upCase(dataType.getName()).contains(keywordUpCase))
                .collect(Collectors.toList());
    }

    private String upCase(final String value) {
        return Objects.isNull(value) ? "" : value.toUpperCase();
    }
}
