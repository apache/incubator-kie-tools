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

import java.util.Objects;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManager;
import org.kie.workbench.common.dmn.client.editors.types.common.ItemDefinitionUtils;

import static org.kie.workbench.common.dmn.api.definition.v1_1.DMNModelInstrumentedBase.Namespace.FEEL;
import static org.kie.workbench.common.dmn.api.property.dmn.QName.NULL_NS_URI;
import static org.kie.workbench.common.dmn.client.editors.types.common.BuiltInTypeUtils.isDefault;

@Dependent
public class ItemDefinitionUpdateHandler {

    private final DataTypeManager dataTypeManager;

    private final ItemDefinitionUtils itemDefinitionUtils;

    @Inject
    public ItemDefinitionUpdateHandler(final DataTypeManager dataTypeManager,
                                       final ItemDefinitionUtils itemDefinitionUtils) {
        this.dataTypeManager = dataTypeManager;
        this.itemDefinitionUtils = itemDefinitionUtils;
    }

    public void update(final DataType dataType,
                       final ItemDefinition itemDefinition) {

        if (isStructure(dataType)) {
            itemDefinition.setTypeRef(null);
        } else {
            itemDefinition.setTypeRef(makeQName(dataType));
            itemDefinition.getItemComponent().clear();
        }

        itemDefinition.setName(makeName(dataType));
    }

    Name makeName(final DataType dataType) {
        return new Name(dataType.getName());
    }

    QName makeQName(final DataType dataType) {
        if (isDefault(dataType.getType())) {
            return normaliseTypeRef(new QName(FEEL.getUri(), dataType.getType()));
        } else {
            return normaliseTypeRef(new QName(NULL_NS_URI, dataType.getType()));
        }
    }

    QName normaliseTypeRef(final QName typeRef) {
        return itemDefinitionUtils.normaliseTypeRef(typeRef);
    }

    private boolean isStructure(final DataType dataType) {
        return Objects.equals(dataType.getType(), dataTypeManager.structure());
    }
}
