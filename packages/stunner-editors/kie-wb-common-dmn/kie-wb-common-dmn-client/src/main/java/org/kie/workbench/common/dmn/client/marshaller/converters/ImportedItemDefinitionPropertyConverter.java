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

package org.kie.workbench.common.dmn.client.marshaller.converters;

import java.util.List;

import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.api.editors.types.BuiltInTypeUtils;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITItemDefinition;

public class ImportedItemDefinitionPropertyConverter {

    private ImportedItemDefinitionPropertyConverter() {
        // Private constructor to prevent instantiation
    }

    public static ItemDefinition wbFromDMN(final JSITItemDefinition dmnItemDefinition,
                                           final String prefix) {

        final JSITItemDefinition dmnItemDefinitionWithNamespace = withNamespace(dmnItemDefinition, prefix);
        final ItemDefinition wbItemDefinition = ItemDefinitionPropertyConverter.wbFromDMN(dmnItemDefinitionWithNamespace);

        allowOnlyVisualChange(wbItemDefinition);

        return wbItemDefinition;
    }

    public static JSITItemDefinition withNamespace(final JSITItemDefinition itemDefinition,
                                                   final String prefix) {

        final String nameWithPrefix = prefix + "." + itemDefinition.getName();
        final List<JSITItemDefinition> itemComponents = itemDefinition.getItemComponent();

        if (itemDefinition.getTypeRef() != null && !isBuiltInType(itemDefinition.getTypeRef())) {
            itemDefinition.setTypeRef(makeQNameWithPrefix(itemDefinition.getTypeRef(), prefix));
        }

        itemDefinition.setName(nameWithPrefix);
        setItemDefinitionsNamespace(itemComponents, prefix);

        return itemDefinition;
    }

    private static void allowOnlyVisualChange(final ItemDefinition itemDefinition) {
        itemDefinition.setAllowOnlyVisualChange(true);
        itemDefinition.getItemComponent().forEach(ImportedItemDefinitionPropertyConverter::allowOnlyVisualChange);
    }

    private static void setItemDefinitionsNamespace(final List<JSITItemDefinition> itemDefinitions,
                                                    final String prefix) {
        itemDefinitions.forEach(itemDefinition -> withNamespace(itemDefinition, prefix));
    }

    private static boolean isBuiltInType(final String typeRef) {
        return BuiltInTypeUtils.isBuiltInType(typeRef);
    }

    private static String makeQNameWithPrefix(final String qName,
                                              final String prefix) {

        return prefix + "." + qName;
    }
}
