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

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.ItemDefinitionUtils;
import org.kie.workbench.common.dmn.client.editors.types.persistence.CreationType;
import org.kie.workbench.common.dmn.client.editors.types.persistence.ItemDefinitionStore;

@Dependent
public class ItemDefinitionCreateHandler {

    private final ItemDefinitionUtils itemDefinitionUtils;

    private final ItemDefinitionStore itemDefinitionStore;

    @Inject
    public ItemDefinitionCreateHandler(final ItemDefinitionUtils itemDefinitionUtils,
                                       final ItemDefinitionStore itemDefinitionStore) {
        this.itemDefinitionUtils = itemDefinitionUtils;
        this.itemDefinitionStore = itemDefinitionStore;
    }

    public ItemDefinition appendItemDefinition() {
        final ItemDefinition itemDefinition = new ItemDefinition();
        itemDefinitions().add(itemDefinition);
        return itemDefinition;
    }

    public ItemDefinition insertNested(final DataType record,
                                       final DataType reference) {

        final ItemDefinition itemDefinition = getItemDefinition(record);
        final ItemDefinition relativeParent = getIndexedItemDefinition(reference.getUUID());
        final Optional<ItemDefinition> absoluteParent = lookupAbsoluteParent(reference.getUUID());
        final List<ItemDefinition> itemComponent;

        if (absoluteParent.isPresent()) {
            itemComponent = absoluteParent.get().getItemComponent();
        } else {
            itemComponent = relativeParent.getItemComponent();
            relativeParent.setTypeRef(null);
        }

        itemComponent.add(0, itemDefinition);

        return itemDefinition;
    }

    public ItemDefinition insertSibling(final DataType record,
                                        final DataType reference,
                                        final CreationType creationType) {

        final ItemDefinition itemDefinition = getItemDefinition(record);
        final ItemDefinition itemDefinitionReference = getIndexedItemDefinition(reference.getUUID());
        final List<ItemDefinition> siblings = getItemDefinitionSiblings(reference);

        siblings.add(siblings.indexOf(itemDefinitionReference) + creationType.getIndexIncrement(), itemDefinition);

        return itemDefinition;
    }

    List<ItemDefinition> getItemDefinitionSiblings(final DataType reference) {
        final Optional<ItemDefinition> parent = lookupAbsoluteParent(reference.getParentUUID());

        if (parent.isPresent()) {
            return parent.get().getItemComponent();
        } else {
            return itemDefinitions();
        }
    }

    Optional<ItemDefinition> lookupAbsoluteParent(final String parentUUID) {
        final Optional<ItemDefinition> optionalParent = Optional.ofNullable(getIndexedItemDefinition(parentUUID));

        if (optionalParent.isPresent()) {

            final ItemDefinition parent = optionalParent.get();
            final boolean isStructure = parent.getTypeRef() == null;

            if (isStructure) {
                return Optional.of(parent);
            } else {
                return findItemDefinitionByName(parent.getTypeRef().getLocalPart());
            }
        }

        return Optional.empty();
    }

    private Optional<ItemDefinition> findItemDefinitionByName(final String type) {
        return itemDefinitionUtils.findByName(type);
    }

    private List<ItemDefinition> itemDefinitions() {
        return itemDefinitionUtils.all();
    }

    private ItemDefinition getIndexedItemDefinition(final String uuid) {
        return itemDefinitionStore.get(uuid);
    }

    private ItemDefinition getItemDefinition(final DataType dataType) {
        return Optional
                .ofNullable(getIndexedItemDefinition(dataType.getUUID()))
                .orElse(new ItemDefinition());
    }
}
