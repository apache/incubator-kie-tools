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

import java.util.Map.Entry;

import org.kie.workbench.common.dmn.api.definition.v1_1.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.v1_1.Definitions;
import org.kie.workbench.common.dmn.api.definition.v1_1.Import;
import org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.kie.workbench.common.stunner.core.util.StringUtils;
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
        result.setNamespace(new Text(namespace));
        result.getNsContext().put(DMNModelInstrumentedBase.Namespace.DEFAULT.getPrefix(),
                                  namespace);
        result.setDescription(description);
        for (Entry<String, String> kv : dmn.getNsContext().entrySet()) {
            String mappedURI = kv.getValue();
            switch (mappedURI) {
                case org.kie.dmn.model.v1_1.KieDMNModelInstrumentedBase.URI_DMN:
                    mappedURI = org.kie.dmn.model.v1_2.KieDMNModelInstrumentedBase.URI_DMN;
                    break;
                case org.kie.dmn.model.v1_1.KieDMNModelInstrumentedBase.URI_FEEL:
                    mappedURI = org.kie.dmn.model.v1_2.KieDMNModelInstrumentedBase.URI_FEEL;
                    break;
                case org.kie.dmn.model.v1_1.KieDMNModelInstrumentedBase.URI_KIE:
                    mappedURI = org.kie.dmn.model.v1_2.KieDMNModelInstrumentedBase.URI_KIE;
                    break;
            }
            result.getNsContext().put(kv.getKey(), mappedURI);
        }

        for (org.kie.dmn.model.api.ItemDefinition itemDef : dmn.getItemDefinition()) {
            ItemDefinition itemDefConverted = ItemDefinitionPropertyConverter.wbFromDMN(itemDef);
            if (itemDefConverted != null) {
                itemDefConverted.setParent(result);
            }
            result.getItemDefinition().add(itemDefConverted);
        }

        for (org.kie.dmn.model.api.Import i : dmn.getImport()) {
            Import importConverted = ImportConverter.wbFromDMN(i);
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
        org.kie.dmn.model.api.Definitions result = new org.kie.dmn.model.v1_2.TDefinitions();

        // TODO currently DMN wb UI does not offer feature to set these required DMN properties, setting some hardcoded defaults for now.
        String defaultId = (wb.getId() != null) ? wb.getId().getValue() : UUID.uuid();
        String defaultName = (wb.getName() != null) ? wb.getName().getValue() : UUID.uuid(8);
        String defaultNamespace = !StringUtils.isEmpty(wb.getNamespace().getValue())
                ? wb.getNamespace().getValue()
                : DMNModelInstrumentedBase.Namespace.DEFAULT.getUri() + UUID.uuid();

        result.setId(defaultId);
        result.setName(defaultName);
        result.setNamespace(defaultNamespace);
        result.setDescription(DescriptionPropertyConverter.dmnFromWB(wb.getDescription()));
        result.getNsContext().putAll(wb.getNsContext());
        result.getNsContext().put(DMNModelInstrumentedBase.Namespace.DEFAULT.getPrefix(),
                                  defaultNamespace);

        for (ItemDefinition itemDef : wb.getItemDefinition()) {
            org.kie.dmn.model.api.ItemDefinition itemDefConverted = ItemDefinitionPropertyConverter.dmnFromWB(itemDef);
            if (itemDefConverted != null) {
                itemDefConverted.setParent(result);
            }
            result.getItemDefinition().add(itemDefConverted);
        }

        for (Import i : wb.getImport()) {
            org.kie.dmn.model.api.Import importConverted = ImportConverter.dmnFromWb(i);
            if (importConverted != null) {
                importConverted.setParent(result);
            }
            result.getImport().add(importConverted);
        }

        return result;
    }
}