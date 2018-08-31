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

package org.kie.workbench.common.dmn.client.editors.types.persistence;

import java.util.Objects;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.client.editors.types.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeUtils;
import org.kie.workbench.common.dmn.client.editors.types.common.ItemDefinitionUtils;

import static org.kie.workbench.common.dmn.api.definition.v1_1.DMNModelInstrumentedBase.Namespace.FEEL;
import static org.kie.workbench.common.dmn.api.property.dmn.QName.NULL_NS_URI;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeFactory_Structure;

@ApplicationScoped
public class ItemDefinitionRecordEngine implements RecordEngine<DataType> {

    private final ItemDefinitionStore itemDefinitionStore;

    private final ItemDefinitionUtils itemDefinitionUtils;

    private final TranslationService translationService;

    private final DataTypeUtils dataTypeUtils;

    @Inject
    public ItemDefinitionRecordEngine(final ItemDefinitionStore itemDefinitionStore,
                                      final ItemDefinitionUtils itemDefinitionUtils,
                                      final TranslationService translationService,
                                      final DataTypeUtils dataTypeUtils) {
        this.itemDefinitionStore = itemDefinitionStore;
        this.itemDefinitionUtils = itemDefinitionUtils;
        this.translationService = translationService;
        this.dataTypeUtils = dataTypeUtils;
    }

    @Override
    public void update(final DataType record) {

        final ItemDefinition itemDefinition = itemDefinitionStore.get(record.getUUID());

        updateDataType(record, itemDefinition);
        updateItemDefinition(record, itemDefinition);
    }

    void updateDataType(final DataType record,
                        final ItemDefinition itemDefinition) {

        final boolean isBasic = !record.getType().equals(structure());

        if (!isBasic || itemDefinition.getTypeRef() != null) {
            record.getSubDataTypes().clear();
        }

        record.setBasic(isBasic);

        if (isExistingItemDefinition(record)) {
            record.getSubDataTypes().addAll(dataTypeUtils.externalDataTypes(record, record.getType()));
        }

        record.setDefault(isDefault(record.getType()));
    }

    boolean isExistingItemDefinition(final DataType record) {
        return itemDefinitionUtils.findByName(record.getType()).isPresent();
    }

    void updateItemDefinition(final DataType record,
                              final ItemDefinition itemDefinition) {

        if (record.isBasic()) {
            itemDefinition.setTypeRef(makeQName(record));
            itemDefinition.getItemComponent().clear();
        } else {
            itemDefinition.setTypeRef(null);
        }

        itemDefinition.setName(makeName(record));
    }

    Name makeName(final DataType record) {
        return new Name(record.getName());
    }

    QName makeQName(final DataType record) {
        if (record.isDefault()) {
            return new QName(FEEL.getUri(), record.getType(), FEEL.getPrefix());
        } else {
            return new QName(NULL_NS_URI, record.getType());
        }
    }

    public boolean isDefault(final String type) {
        return Stream
                .of(BuiltInType.values())
                .anyMatch(dataType -> Objects.equals(dataType.getName(), type));
    }

    private String structure() {
        return translationService.format(DataTypeFactory_Structure);
    }

    @Override
    public void destroy(final DataType record) {
        // TODO: https://issues.jboss.org/browse/DROOLS-2881
    }

    @Override
    public void create(final DataType record) {
        // TODO: https://issues.jboss.org/browse/DROOLS-2762
    }
}
