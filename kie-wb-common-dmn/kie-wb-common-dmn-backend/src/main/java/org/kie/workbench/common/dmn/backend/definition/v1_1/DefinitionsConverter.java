/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.backend.definition.v1_1;

import org.kie.workbench.common.dmn.api.definition.v1_1.Definitions;
import org.kie.workbench.common.dmn.api.definition.v1_1.Import;
import org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.backend.definition.v1_1.dd.DMNDiagram;
import org.kie.workbench.common.stunner.core.util.UUID;

public class DefinitionsConverter {

    public static Definitions wbFromDMN(final org.kie.dmn.model.api.Definitions dmn) {
        if (dmn == null) {
            return null;
        }
        Id id = new Id(dmn.getId());
        Name name = new Name(dmn.getName());
        String namespace = dmn.getNamespace();
        Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
        Definitions result = new Definitions();
        result.setId(id);
        result.setName(name);
        result.setNamespace(namespace);
        result.setDescription(description);
        result.getNsContext().putAll(dmn.getNsContext());

        for (org.kie.dmn.model.api.ItemDefinition itemDef : dmn.getItemDefinition()) {
            ItemDefinition itemDefConverted = ItemDefinitionPropertyConverter.wbFromDMN(itemDef);
            if (itemDefConverted != null) {
                itemDefConverted.setParent(result);
            }
            result.getItemDefinition().add(itemDefConverted);
        }

        for (org.kie.dmn.model.api.Import i : dmn.getImport()) {
            Import importConverted = ImportConverter.nodeFromDMN(i);
            if (importConverted != null) {
                importConverted.setParent(result);
            }
            result.getImport().add(importConverted);
        }

        return result;
    }

    public static org.kie.dmn.model.api.Definitions dmnFromWB(final Definitions wb) {
        if (wb == null) {
            return null;
        }
        org.kie.dmn.model.api.Definitions result = new org.kie.dmn.model.v1_1.TDefinitions();

        // TODO currently DMN wb UI does not offer feature to set these required DMN properties, setting some hardcoded defaults for now.
        String defaultId = (wb.getId() != null) ? wb.getId().getValue() : UUID.uuid();
        String defaulName = (wb.getName() != null) ? wb.getName().getValue() : UUID.uuid(8);
        String defaultNamespace = (wb.getNamespace() != null) ? wb.getNamespace() : "https://github.com/kiegroup/drools/kie-dmn";

        result.setId(defaultId);
        result.setName(defaulName);
        result.setNamespace(defaultNamespace);
        result.setDescription(DescriptionPropertyConverter.dmnFromWB(wb.getDescription()));
        result.getNsContext().putAll(wb.getNsContext());

        for (ItemDefinition itemDef : wb.getItemDefinition()) {
            org.kie.dmn.model.api.ItemDefinition itemDefConvered = ItemDefinitionPropertyConverter.dmnFromWB(itemDef);
            result.getItemDefinition().add(itemDefConvered);
        }

        // Need manually setup custom namespace URI if the diagram was created from the UI.
        if (!result.getPrefixForNamespaceURI(DMNDiagram.DMNV11_DD).isPresent()) {
            result.getNsContext().put("dmndi", DMNDiagram.DMNV11_DD);
        }
        if (!result.getPrefixForNamespaceURI(DMNDiagram.DMNV11_DC).isPresent()) {
            result.getNsContext().put("dc", DMNDiagram.DMNV11_DC);
        }
        if (!result.getPrefixForNamespaceURI(DMNDiagram.DMNV11_DI).isPresent()) {
            result.getNsContext().put("di", DMNDiagram.DMNV11_DI);
        }

        for (Import i : wb.getImport()) {
            result.getImport().add(ImportConverter.dmnFromNode(i));
        }

        return result;
    }
}