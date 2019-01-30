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
import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.v1_1.Definitions;
import org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.persistence.ItemDefinitionStore;
import org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.common.PropertiesPanelNotifier;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;

@Dependent
public class ItemDefinitionDestroyHandler {

    private final ItemDefinitionStore itemDefinitionStore;

    private final DMNGraphUtils dmnGraphUtils;

    private final PropertiesPanelNotifier panelNotifier;

    @Inject
    public ItemDefinitionDestroyHandler(final ItemDefinitionStore itemDefinitionStore,
                                        final DMNGraphUtils dmnGraphUtils,
                                        final PropertiesPanelNotifier panelNotifier) {
        this.itemDefinitionStore = itemDefinitionStore;
        this.dmnGraphUtils = dmnGraphUtils;
        this.panelNotifier = panelNotifier;
    }

    public void destroy(final DataType dataType) {

        final ItemDefinition itemDefinition = findItemDefinition(dataType);
        final String destroyedItemDefinition = itemDefinition.getName().getValue();
        final Optional<ItemDefinition> itemDefinitionParent = findItemDefinitionParent(dataType);

        itemDefinitionParent.ifPresent(parent -> {
            parent.getItemComponent().remove(itemDefinition);
        });

        itemDefinitions().remove(itemDefinition);
        itemDefinitionStore.unIndex(dataType.getUUID());

        notifyPropertiesPanel(destroyedItemDefinition);
    }

    void notifyPropertiesPanel(final String destroyedItemDefinition) {
        panelNotifier
                .withOldLocalPart(destroyedItemDefinition)
                .withNewQName(new QName())
                .notifyPanel();
    }

    Optional<ItemDefinition> findItemDefinitionParent(final DataType dataType) {

        final Optional<ItemDefinition> itemDefinitionParent = Optional.ofNullable(itemDefinitionStore.get(dataType.getParentUUID()));

        if (itemDefinitionParent.isPresent()) {
            final ItemDefinition parent = itemDefinitionParent.get();

            if (parent.getTypeRef() == null) {
                return Optional.of(parent);
            } else {
                for (final ItemDefinition itemDefinition : itemDefinitions()) {
                    if (Objects.equals(itemDefinition.getName().getValue(), parent.getTypeRef().getLocalPart())) {
                        return Optional.of(itemDefinition);
                    }
                }
            }
        }

        return Optional.empty();
    }

    List<ItemDefinition> itemDefinitions() {
        final Optional<Definitions> definitions = Optional.ofNullable(dmnGraphUtils.getDefinitions());

        if (definitions.isPresent()) {
            return definitions.get().getItemDefinition();
        }

        return new ArrayList<>();
    }

    private ItemDefinition findItemDefinition(final DataType dataType) {
        return itemDefinitionStore.get(dataType.getUUID());
    }
}
