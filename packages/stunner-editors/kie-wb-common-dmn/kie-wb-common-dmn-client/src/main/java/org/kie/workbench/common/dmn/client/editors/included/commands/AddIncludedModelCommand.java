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

package org.kie.workbench.common.dmn.client.editors.included.commands;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import javax.enterprise.event.Event;

import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.api.editors.included.DMNImportTypes;
import org.kie.workbench.common.dmn.client.api.included.legacy.DMNIncludeModelsClient;
import org.kie.workbench.common.dmn.client.docks.navigator.events.RefreshDecisionComponents;
import org.kie.workbench.common.dmn.client.editors.included.BaseIncludedModelActiveRecord;
import org.kie.workbench.common.dmn.client.editors.included.DMNIncludedModelActiveRecord;
import org.kie.workbench.common.dmn.client.editors.included.DefaultIncludedModelActiveRecord;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModelsPagePresenter;
import org.kie.workbench.common.dmn.client.editors.included.PMMLIncludedModelActiveRecord;
import org.kie.workbench.common.dmn.client.editors.included.imports.persistence.ImportRecordEngine;
import org.kie.workbench.common.dmn.client.editors.types.common.events.RefreshDataTypesListEvent;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.AbstractCanvasCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.widgets.client.assets.dropdown.KieAssetsDropdownItem;

import static org.kie.workbench.common.dmn.api.editors.included.DMNImportTypes.determineImportType;
import static org.kie.workbench.common.dmn.client.editors.included.modal.dropdown.DMNAssetsDropdownItemsProvider.DRG_ELEMENT_COUNT_METADATA;
import static org.kie.workbench.common.dmn.client.editors.included.modal.dropdown.DMNAssetsDropdownItemsProvider.IMPORT_TYPE_METADATA;
import static org.kie.workbench.common.dmn.client.editors.included.modal.dropdown.DMNAssetsDropdownItemsProvider.ITEM_DEFINITION_COUNT_METADATA;
import static org.kie.workbench.common.dmn.client.editors.included.modal.dropdown.DMNAssetsDropdownItemsProvider.PATH_METADATA;
import static org.kie.workbench.common.dmn.client.editors.included.modal.dropdown.DMNAssetsDropdownItemsProvider.PMML_MODEL_COUNT_METADATA;

public class AddIncludedModelCommand extends AbstractCanvasCommand {

    private final Event<RefreshDecisionComponents> refreshDecisionComponentsEvent;
    private final Event<RefreshDecisionComponents> refreshPMMLComponentsEvent;

    private final KieAssetsDropdownItem value;
    private final Event<RefreshDataTypesListEvent> refreshDataTypesListEvent;
    private final ImportRecordEngine recordEngine;
    private final DMNIncludeModelsClient client;
    private final String modelName;
    private final IncludedModelsPagePresenter presenter;

    private BaseIncludedModelActiveRecord created;

    public AddIncludedModelCommand(final KieAssetsDropdownItem value,
                                   final IncludedModelsPagePresenter presenter,
                                   final Event<RefreshDecisionComponents> refreshDecisionComponentsEvent,
                                   final Event<RefreshDecisionComponents> refreshPMMLComponentsEvent,
                                   final Event<RefreshDataTypesListEvent> refreshDataTypesListEvent,
                                   final ImportRecordEngine recordEngine,
                                   final DMNIncludeModelsClient client,
                                   final String modelName) {
        this.value = value;
        this.presenter = presenter;
        this.refreshDecisionComponentsEvent = refreshDecisionComponentsEvent;
        this.refreshPMMLComponentsEvent = refreshPMMLComponentsEvent;
        this.refreshDataTypesListEvent = refreshDataTypesListEvent;
        this.recordEngine = recordEngine;
        this.client = client;
        this.modelName = modelName;
    }

    public BaseIncludedModelActiveRecord getCreated() {
        return created;
    }

    public String getModelName() {
        return modelName;
    }

    public Event<RefreshDecisionComponents> getRefreshDecisionComponentsEvent() {
        return refreshDecisionComponentsEvent;
    }

    public KieAssetsDropdownItem getValue() {
        return value;
    }

    public Event<RefreshDataTypesListEvent> getRefreshDataTypesListEvent() {
        return refreshDataTypesListEvent;
    }

    public ImportRecordEngine getRecordEngine() {
        return recordEngine;
    }

    public DMNIncludeModelsClient getClient() {
        return client;
    }

    public Optional<IncludedModelsPagePresenter> getPresenter() {
        return Optional.ofNullable(presenter);
    }

    @Override
    public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler context) {
        this.created = createIncludedModel(value);
        refreshPresenter();
        refreshDecisionComponents(created instanceof PMMLIncludedModelActiveRecord ? DMNImportTypes.PMML : DMNImportTypes.DMN);
        refreshDataTypesList(getCreated());
        return CanvasCommandResultBuilder.SUCCESS;
    }

    @Override
    public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context) {
        getCreated().destroy();
        refreshPresenter();
        refreshDecisionComponents(created instanceof PMMLIncludedModelActiveRecord ? DMNImportTypes.PMML : DMNImportTypes.DMN);
        return CanvasCommandResultBuilder.SUCCESS;
    }

    void refreshDataTypesList(final BaseIncludedModelActiveRecord includedModel) {
        client.loadItemDefinitionsByNamespace(includedModel.getName(),
                                              includedModel.getNamespace(),
                                              getItemDefinitionConsumer());
    }

    Consumer<List<ItemDefinition>> getItemDefinitionConsumer() {
        return itemDefinitions -> refreshDataTypesListEvent.fire(new RefreshDataTypesListEvent(itemDefinitions));
    }

    BaseIncludedModelActiveRecord createIncludedModel(final KieAssetsDropdownItem value) {
        final Map<String, String> metaData = value.getMetaData();
        final BaseIncludedModelActiveRecord includedModel = createIncludedModel(metaData);
        includedModel.setName(modelName);
        includedModel.setNamespace(value.getValue());
        includedModel.setImportType(metaData.get(IMPORT_TYPE_METADATA));
        includedModel.setPath(metaData.get(PATH_METADATA));
        includedModel.create();
        return includedModel;
    }

    BaseIncludedModelActiveRecord createIncludedModel(final Map<String, String> metaData) {
        final String importType = metaData.get(IMPORT_TYPE_METADATA);
        if (Objects.equals(DMNImportTypes.DMN, determineImportType(importType))) {
            final DMNIncludedModelActiveRecord dmnIncludedModel = new DMNIncludedModelActiveRecord(recordEngine);
            dmnIncludedModel.setDrgElementsCount(Integer.valueOf(metaData.get(DRG_ELEMENT_COUNT_METADATA)));
            dmnIncludedModel.setDataTypesCount(Integer.valueOf(metaData.get(ITEM_DEFINITION_COUNT_METADATA)));
            return dmnIncludedModel;
        } else if (Objects.equals(DMNImportTypes.PMML, determineImportType(importType))) {
            final PMMLIncludedModelActiveRecord pmmlIncludedModel = new PMMLIncludedModelActiveRecord(recordEngine);
            pmmlIncludedModel.setModelCount(Integer.valueOf(metaData.get(PMML_MODEL_COUNT_METADATA)));
            return pmmlIncludedModel;
        }
        return new DefaultIncludedModelActiveRecord(recordEngine);
    }

    void refreshDecisionComponents(DMNImportTypes dmnImportTypes) {
        if (Objects.equals(dmnImportTypes, DMNImportTypes.PMML)) {
            this.refreshPMMLComponentsEvent.fire(new RefreshDecisionComponents());
        } else {
            this.refreshDecisionComponentsEvent.fire(new RefreshDecisionComponents());
        }
    }

    void refreshPresenter() {
        getPresenter().ifPresent(IncludedModelsPagePresenter::refresh);
    }
}
