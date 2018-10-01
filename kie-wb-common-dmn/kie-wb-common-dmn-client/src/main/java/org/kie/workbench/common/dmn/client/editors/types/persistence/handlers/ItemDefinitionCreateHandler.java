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

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManager;
import org.kie.workbench.common.dmn.client.editors.types.common.ItemDefinitionUtils;

@Dependent
public class ItemDefinitionCreateHandler {

    private final DataTypeManager dataTypeManager;

    private final ItemDefinitionUtils itemDefinitionUtils;

    @Inject
    public ItemDefinitionCreateHandler(final DataTypeManager dataTypeManager,
                                       final ItemDefinitionUtils itemDefinitionUtils) {
        this.dataTypeManager = dataTypeManager;
        this.itemDefinitionUtils = itemDefinitionUtils;
    }

    public DataType create(final DataType dataType) {

        final ItemDefinition itemDefinition = createItemDefinition();

        return dataTypeManager
                .withDataType(dataType)
                .withItemDefinition(itemDefinition)
                .withIndexedItemDefinition()
                .get();
    }

    ItemDefinition createItemDefinition() {
        final ItemDefinition itemDefinition = new ItemDefinition();
        itemDefinitions().add(itemDefinition);
        return itemDefinition;
    }

    private List<ItemDefinition> itemDefinitions() {
        return itemDefinitionUtils.all();
    }
}
