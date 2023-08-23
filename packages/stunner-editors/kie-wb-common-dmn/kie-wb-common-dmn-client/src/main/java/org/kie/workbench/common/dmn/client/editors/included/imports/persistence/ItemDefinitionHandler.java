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

package org.kie.workbench.common.dmn.client.editors.included.imports.persistence;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.api.definition.model.NamedElement;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.editors.types.common.ItemDefinitionUtils;
import org.kie.workbench.common.dmn.client.editors.types.common.events.RefreshDataTypesListEvent;
import org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.common.PropertiesPanelNotifier;

@Dependent
public class ItemDefinitionHandler implements DRGElementHandler {

    private final ItemDefinitionUtils itemDefinitionUtils;

    private final Event<RefreshDataTypesListEvent> refreshDataTypesListEvent;

    private final PropertiesPanelNotifier panelNotifier;

    @Inject
    public ItemDefinitionHandler(final ItemDefinitionUtils itemDefinitionUtils,
                                 final Event<RefreshDataTypesListEvent> refreshDataTypesListEvent,
                                 final PropertiesPanelNotifier panelNotifier) {
        this.itemDefinitionUtils = itemDefinitionUtils;
        this.refreshDataTypesListEvent = refreshDataTypesListEvent;
        this.panelNotifier = panelNotifier;
    }

    @Override
    public void update(final String oldModelName,
                       final String newModelName) {

        final List<ItemDefinition> updatedItemDefinitionsByName = findItemDefinitionsByOldName(oldModelName);
        final List<ItemDefinition> updatedItemDefinitionsByType = findItemDefinitionsByUpdatedItemDefinitions(updatedItemDefinitionsByName);

        updatedItemDefinitionsByName
                .forEach(itemDefinition -> {
                    final String oldName = itemDefinition.getName().getValue();
                    final String newName = oldName.replaceAll("^" + oldModelName, newModelName);
                    final Name name = new Name(newName);
                    itemDefinition.setName(name);
                    notifyPropertiesPanel(oldName, newName);
                });

        updatedItemDefinitionsByType
                .forEach(itemDefinition -> {
                    final QName oldType = itemDefinition.getTypeRef();
                    final String newLocalPart = oldType.getLocalPart().replaceAll("^" + oldModelName, newModelName);
                    final QName qName = new QName(oldType.getNamespaceURI(), newLocalPart, oldType.getPrefix());
                    itemDefinition.setTypeRef(qName);
                });

        refreshDataTypesList();
    }

    @Override
    public void destroy(final String oldModelName) {

        final List<ItemDefinition> updatedItemDefinitionsByName = findItemDefinitionsByOldName(oldModelName);
        final List<ItemDefinition> updatedItemDefinitionsByType = findItemDefinitionsByUpdatedItemDefinitions(updatedItemDefinitionsByName);

        itemDefinitionUtils.all().removeIf(itemDefinition -> {
            return updatedItemDefinitionsByName.contains(itemDefinition) || updatedItemDefinitionsByType.contains(itemDefinition);
        });

        refreshDataTypesList();
    }

    private QName makeQName(final String value) {
        return new QName(QName.NULL_NS_URI, value);
    }

    private QName normaliseTypeRef(final QName typeRef) {
        return itemDefinitionUtils.normaliseTypeRef(typeRef);
    }

    void notifyPropertiesPanel(final String oldLocalPart,
                               final String newLocalPart) {
        panelNotifier
                .withOldLocalPart(oldLocalPart)
                .withNewQName(normaliseTypeRef(makeQName(newLocalPart)))
                .notifyPanel();
    }

    private List<ItemDefinition> findItemDefinitionsByOldName(final String oldModelName) {
        return itemDefinitionUtils
                .all()
                .stream()
                .filter(itemDefinition -> itemDefinitionNameStartsWith(oldModelName, itemDefinition))
                .filter(ItemDefinition::isAllowOnlyVisualChange)
                .collect(Collectors.toList());
    }

    private List<ItemDefinition> findItemDefinitionsByUpdatedItemDefinitions(final List<ItemDefinition> updatedItemDefinitions) {

        final List<String> updatedTypes = collectNames(updatedItemDefinitions);

        return itemDefinitionUtils
                .all()
                .stream()
                .filter(itemDefinition -> isItemDefinitionUpdatedType(itemDefinition, updatedTypes))
                .collect(Collectors.toList());
    }

    private boolean isItemDefinitionUpdatedType(final ItemDefinition itemDefinition,
                                                final List<String> updatedTypes) {

        if (Objects.isNull(itemDefinition.getTypeRef())) {
            return false;
        }

        return updatedTypes.contains(itemDefinition.getTypeRef().getLocalPart());
    }

    private boolean itemDefinitionNameStartsWith(final String oldModelName,
                                                 final ItemDefinition itemDefinition) {
        return itemDefinition.getName().getValue().startsWith(oldModelName + ".");
    }

    private List<String> collectNames(final List<ItemDefinition> updatedItemDefinitions) {
        return updatedItemDefinitions
                .stream()
                .map(NamedElement::getName)
                .map(Name::getValue)
                .collect(Collectors.toList());
    }

    private void refreshDataTypesList() {
        refreshDataTypesListEvent.fire(new RefreshDataTypesListEvent());
    }
}
