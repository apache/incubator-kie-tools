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

package org.kie.workbench.common.dmn.client.editors.types.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.client.editors.common.persistence.RecordEngine;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.DataTypeCreateHandler;
import org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.DataTypeDestroyHandler;
import org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.DataTypeUpdateHandler;
import org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.ItemDefinitionCreateHandler;
import org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.ItemDefinitionDestroyHandler;
import org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.ItemDefinitionUpdateHandler;
import org.kie.workbench.common.dmn.client.editors.types.persistence.validation.DataTypeNameValidator;

import static org.kie.workbench.common.dmn.client.editors.types.persistence.CreationType.NESTED;

/**
 * Implements the {@link RecordEngine} to record a {@link DataType} as a {@link ItemDefinition}.
 */
@ApplicationScoped
public class ItemDefinitionRecordEngine implements DataTypeRecordEngine {

    private final ItemDefinitionStore itemDefinitionStore;

    private final ItemDefinitionDestroyHandler itemDefinitionDestroyHandler;

    private final ItemDefinitionUpdateHandler itemDefinitionUpdateHandler;

    private final ItemDefinitionCreateHandler itemDefinitionCreateHandler;

    private final DataTypeDestroyHandler dataTypeDestroyHandler;

    private final DataTypeUpdateHandler dataTypeUpdateHandler;

    private final DataTypeCreateHandler dataTypeCreateHandler;

    private final DataTypeNameValidator dataTypeNameValidator;

    @Inject
    public ItemDefinitionRecordEngine(final ItemDefinitionStore itemDefinitionStore,
                                      final ItemDefinitionDestroyHandler itemDefinitionDestroyHandler,
                                      final ItemDefinitionUpdateHandler itemDefinitionUpdateHandler,
                                      final ItemDefinitionCreateHandler itemDefinitionCreateHandler,
                                      final DataTypeDestroyHandler dataTypeDestroyHandler,
                                      final DataTypeUpdateHandler dataTypeUpdateHandler,
                                      final DataTypeCreateHandler dataTypeCreateHandler,
                                      final DataTypeNameValidator dataTypeNameValidator) {
        this.itemDefinitionStore = itemDefinitionStore;
        this.itemDefinitionDestroyHandler = itemDefinitionDestroyHandler;
        this.itemDefinitionUpdateHandler = itemDefinitionUpdateHandler;
        this.itemDefinitionCreateHandler = itemDefinitionCreateHandler;
        this.dataTypeDestroyHandler = dataTypeDestroyHandler;
        this.dataTypeUpdateHandler = dataTypeUpdateHandler;
        this.dataTypeCreateHandler = dataTypeCreateHandler;
        this.dataTypeNameValidator = dataTypeNameValidator;
    }

    @PostConstruct
    public void init() {
        dataTypeCreateHandler.init(this);
        dataTypeDestroyHandler.init(this);
        dataTypeUpdateHandler.init(this);
    }

    @Override
    public List<DataType> update(final DataType dataType) {

        if (!dataType.isValid()) {
            throw new UnsupportedOperationException("An invalid Data Type cannot be updated.");
        }

        final ItemDefinition itemDefinition = itemDefinitionStore.get(dataType.getUUID());
        final String itemDefinitionBeforeUpdate = itemDefinition.getName().getValue();

        doUpdate(dataType, itemDefinition);

        return refreshDependentDataTypesFromUpdateOperation(dataType, itemDefinitionBeforeUpdate);
    }

    @Override
    public List<DataType> destroy(final DataType dataType) {
        doDestroy(dataType);

        return refreshDependentDataTypesFromDestroyOperation(dataType);
    }

    @Override
    public List<DataType> destroyWithoutDependentTypes(final DataType dataType) {

        final List<DataType> affectedDataTypes = new ArrayList<>();
        affectedDataTypes.add(dataType);

        doDestroy(dataType, false);

        return affectedDataTypes;
    }

    @Override
    public List<DataType> create(final DataType dataType) {
        return dataTypeCreateHandler.append(dataType, itemDefinitionCreateHandler.appendItemDefinition());
    }

    @Override
    public List<DataType> create(final DataType record,
                                 final DataType reference,
                                 final CreationType creationType) {

        if (creationType == NESTED) {
            return insertNested(record, reference);
        } else {
            return insertSibling(record, reference, creationType);
        }
    }

    @Override
    public boolean isValid(final DataType dataType) {
        return dataTypeNameValidator.isValid(dataType);
    }

    public void doUpdate(final DataType dataType,
                         final ItemDefinition itemDefinition) {

        dataTypeUpdateHandler.update(dataType);
        itemDefinitionUpdateHandler.update(dataType, itemDefinition);
    }

    public void doDestroy(final DataType dataType) {

        doDestroy(dataType, true);
    }

    private void doDestroy(final DataType dataType, final boolean notifyPropertiesPanel) {

        dataTypeDestroyHandler.destroy(dataType);
        itemDefinitionDestroyHandler.destroy(dataType, notifyPropertiesPanel);
    }

    private List<DataType> refreshDependentDataTypesFromUpdateOperation(final DataType dataType,
                                                                        final String itemDefinitionName) {
        return dataTypeUpdateHandler.refreshDependentDataTypes(dataType, itemDefinitionName);
    }

    private List<DataType> refreshDependentDataTypesFromDestroyOperation(final DataType dataType) {
        return dataTypeDestroyHandler.refreshDependentDataTypes(dataType);
    }

    private List<DataType> insertNested(final DataType record,
                                        final DataType reference) {

        final ItemDefinition nestedItemDefinition = itemDefinitionCreateHandler.insertNested(record, reference);

        return dataTypeCreateHandler.insertNested(record, reference, nestedItemDefinition);
    }

    private List<DataType> insertSibling(final DataType record,
                                         final DataType reference,
                                         final CreationType creationType) {

        final ItemDefinition siblingItemDefinition = itemDefinitionCreateHandler.insertSibling(record, reference, creationType);

        return dataTypeCreateHandler.insertSibling(record, reference, creationType, siblingItemDefinition);
    }
}
