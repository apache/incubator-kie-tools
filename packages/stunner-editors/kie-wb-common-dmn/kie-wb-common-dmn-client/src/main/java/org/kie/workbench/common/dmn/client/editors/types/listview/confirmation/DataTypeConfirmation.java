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

package org.kie.workbench.common.dmn.client.editors.types.listview.confirmation;

import java.util.Objects;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessage;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeStore;
import org.kie.workbench.common.dmn.client.editors.types.persistence.ItemDefinitionStore;
import org.uberfire.mvp.Command;

import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeManager_Structure;

/**
 * Fires warning messages to get confirmation in potentially destructive Data Type operations.
 */
@Dependent
public class DataTypeConfirmation {

    private final DataTypeStore dataTypeStore;

    private final ItemDefinitionStore itemDefinitionStore;

    private final Event<FlashMessage> flashMessageEvent;

    private final DataTypeHasFieldsWarningMessage dataTypeHasFieldsWarningMessage;

    private final ReferencedDataTypeWarningMessage referencedDataTypeWarningMessage;

    private final TranslationService translationService;

    @Inject
    public DataTypeConfirmation(final DataTypeStore dataTypeStore,
                                final ItemDefinitionStore itemDefinitionStore,
                                final Event<FlashMessage> flashMessageEvent,
                                final DataTypeHasFieldsWarningMessage dataTypeHasFieldsWarningMessage,
                                final ReferencedDataTypeWarningMessage referencedDataTypeWarningMessage,
                                final TranslationService translationService) {

        this.dataTypeStore = dataTypeStore;
        this.itemDefinitionStore = itemDefinitionStore;
        this.flashMessageEvent = flashMessageEvent;
        this.dataTypeHasFieldsWarningMessage = dataTypeHasFieldsWarningMessage;
        this.referencedDataTypeWarningMessage = referencedDataTypeWarningMessage;
        this.translationService = translationService;
    }

    public void ifDataTypeDoesNotHaveLostSubDataTypes(final DataType dataType,
                                                      final Command onSuccess,
                                                      final Command onError) {

        if (hasLostSubDataTypes(dataType)) {
            flashMessageEvent.fire(dataTypeHasFieldsWarningMessage.getFlashMessage(dataType, onSuccess, onError));
        } else {
            onSuccess.execute();
        }
    }

    public void ifIsNotReferencedDataType(final DataType dataType,
                                          final Command onSuccess) {

        if (isReferencedByAnotherDataType(dataType)) {
            flashMessageEvent.fire(referencedDataTypeWarningMessage.getFlashMessage(dataType, onSuccess, () -> { /* Nothing. */ }));
        } else {
            onSuccess.execute();
        }
    }

    private boolean isReferencedByAnotherDataType(final DataType dataType) {
        return dataTypeStore.all().stream().anyMatch(dt -> Objects.equals(dt.getType(), dataType.getName()));
    }

    private boolean hasLostSubDataTypes(final DataType dataType) {

        final ItemDefinition itemDefinition = itemDefinitionStore.get(dataType.getUUID());
        final boolean isDataTypeNotStructure = !isStructure(dataType);
        final boolean hasItemDefinitionSubItemDefinitions = !itemDefinition.getItemComponent().isEmpty();

        return isDataTypeNotStructure && hasItemDefinitionSubItemDefinitions;
    }

    private boolean isStructure(final DataType dataType) {
        return Objects.equals(dataType.getType(), translationService.format(DataTypeManager_Structure));
    }
}
