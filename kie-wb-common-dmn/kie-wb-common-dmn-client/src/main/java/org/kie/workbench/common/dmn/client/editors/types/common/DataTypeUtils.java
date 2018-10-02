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

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeStore;

import static java.util.stream.Collectors.toList;

@Dependent
public class DataTypeUtils {

    private final DataTypeStore dataTypeStore;

    private final DataTypeManager dataTypeManager;

    @Inject
    public DataTypeUtils(final DataTypeStore dataTypeStore,
                         final DataTypeManager dataTypeManager) {
        this.dataTypeStore = dataTypeStore;
        this.dataTypeManager = dataTypeManager;
    }

    public List<DataType> defaultDataTypes() {
        return Stream
                .of(BuiltInType.values())
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
}
