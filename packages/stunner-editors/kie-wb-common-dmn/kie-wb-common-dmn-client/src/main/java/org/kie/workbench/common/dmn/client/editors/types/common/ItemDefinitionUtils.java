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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.model.ConstraintType;
import org.kie.workbench.common.dmn.api.definition.model.Definitions;
import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.api.definition.model.UnaryTests;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;

@Dependent
public class ItemDefinitionUtils {

    private final DMNGraphUtils dmnGraphUtils;

    @Inject
    public ItemDefinitionUtils(final DMNGraphUtils dmnGraphUtils) {
        this.dmnGraphUtils = dmnGraphUtils;
    }

    public Optional<ItemDefinition> findByName(final String name) {
        return all()
                .stream()
                .filter(itemDefinition -> itemDefinition.getName().getValue().equals(name))
                .findFirst();
    }

    public List<ItemDefinition> all() {
        if (dmnGraphUtils.getModelDefinitions() != null) {
            return dmnGraphUtils.getModelDefinitions().getItemDefinition();
        } else {
            return Collections.emptyList();
        }
    }

    public void addItemDefinitions(final List<ItemDefinition> newItemDefinitions) {
        final List<ItemDefinition> itemDefinitions = dmnGraphUtils.getModelDefinitions().getItemDefinition();
        itemDefinitions.addAll(newItemDefinitions);
    }

    public String getConstraintText(final ItemDefinition itemDefinition) {
        return Optional
                .ofNullable(itemDefinition.getAllowedValues())
                .map(UnaryTests::getText)
                .orElse(new Text())
                .getValue();
    }

    public ConstraintType getConstraintType(final ItemDefinition itemDefinition) {
        return Optional
                .ofNullable(itemDefinition.getAllowedValues())
                .map(UnaryTests::getConstraintType)
                .orElse(ConstraintType.NONE);
    }

    public QName normaliseTypeRef(final QName typeRef) {

        final String namespace = typeRef.getNamespaceURI();
        final String localPart = typeRef.getLocalPart();
        final String typeRefPrefix = typeRef.getPrefix();
        final Optional<String> nsPrefix = getPrefixForNamespaceURI(namespace);

        return nsPrefix
                .map(prefix -> new QName("", localPart, prefix))
                .orElseGet(() -> new QName(namespace, localPart, typeRefPrefix));
    }

    Optional<String> getPrefixForNamespaceURI(final String namespace) {
        final Definitions definitions = dmnGraphUtils.getModelDefinitions();
        return definitions == null ? Optional.empty() : definitions.getPrefixForNamespaceURI(namespace);
    }
}
