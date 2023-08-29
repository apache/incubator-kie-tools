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
import java.util.function.Consumer;

import javax.enterprise.event.Event;

import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.client.api.included.legacy.DMNIncludeModelsClient;
import org.kie.workbench.common.dmn.client.docks.navigator.events.RefreshDecisionComponents;
import org.kie.workbench.common.dmn.client.editors.included.BaseIncludedModelActiveRecord;
import org.kie.workbench.common.dmn.client.editors.included.DefaultIncludedModelActiveRecord;
import org.kie.workbench.common.dmn.client.editors.included.grid.DMNCardsGridComponent;
import org.kie.workbench.common.dmn.client.editors.included.imports.persistence.ImportRecordEngine;
import org.kie.workbench.common.dmn.client.editors.types.common.events.RefreshDataTypesListEvent;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.AbstractCanvasCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.CommandResult;

public class RemoveIncludedModelCommand<R extends BaseIncludedModelActiveRecord> extends AbstractCanvasCommand {

    private final DMNCardsGridComponent grid;
    private final DMNIncludeModelsClient client;
    private final Event<RefreshDataTypesListEvent> refreshDataTypesListEvent;
    private final Event<RefreshDecisionComponents> refreshDecisionComponentsEvent;
    private final ImportRecordEngine recordEngine;
    private R includedModel;

    private String name;
    private String namespace;
    private String importType;
    private String path;

    public RemoveIncludedModelCommand(final DMNCardsGridComponent grid,
                                      final R includedModel,
                                      final DMNIncludeModelsClient client,
                                      final Event<RefreshDecisionComponents> refreshDecisionComponentsEvent,
                                      final ImportRecordEngine recordEngine,
                                      final Event<RefreshDataTypesListEvent> refreshDataTypesListEvent) {
        this.grid = grid;
        this.includedModel = includedModel;
        this.client = client;
        this.refreshDecisionComponentsEvent = refreshDecisionComponentsEvent;
        this.recordEngine = recordEngine;
        this.refreshDataTypesListEvent = refreshDataTypesListEvent;
    }

    public R getIncludedModel() {
        return includedModel;
    }

    void setIncludedModel(R includedModel) {
        this.includedModel = includedModel;
    }

    public String getName() {
        return name;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getImportType() {
        return importType;
    }

    public String getPath() {
        return path;
    }

    public DMNCardsGridComponent getGrid() {
        return grid;
    }

    public DMNIncludeModelsClient getClient() {
        return this.client;
    }

    public Event<RefreshDecisionComponents> getRefreshDecisionComponentsEvent() {
        return this.refreshDecisionComponentsEvent;
    }

    public ImportRecordEngine getRecordEngine() {
        return this.recordEngine;
    }

    public Event<RefreshDataTypesListEvent> getRefreshDataTypesListEvent() {
        return this.refreshDataTypesListEvent;
    }

    void refreshDecisionComponents() {
        getRefreshDecisionComponentsEvent().fire(new RefreshDecisionComponents());
    }

    @Override
    public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler context) {
        saveDeletedIncludedModelData();
        getIncludedModel().destroy();
        getGrid().refresh();
        refreshDecisionComponents();
        return CanvasCommandResultBuilder.SUCCESS;
    }

    protected void saveDeletedIncludedModelData() {
        this.name = getIncludedModel().getName();
        this.namespace = getIncludedModel().getNamespace();
        this.importType = getIncludedModel().getImportType();
        this.path = getIncludedModel().getPath();
    }

    @Override
    public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context) {
        setIncludedModel(restoreDeletedModel());
        getIncludedModel().setName(getName());
        getIncludedModel().setNamespace(getNamespace());
        getIncludedModel().setImportType(getImportType());
        getIncludedModel().setPath(getPath());
        getIncludedModel().create();

        getGrid().refresh();
        refreshDecisionComponents();
        refreshDataTypesList(getIncludedModel());
        return CanvasCommandResultBuilder.SUCCESS;
    }

    void refreshDataTypesList(final BaseIncludedModelActiveRecord includedModel) {
        getClient().loadItemDefinitionsByNamespace(includedModel.getName(),
                                                   includedModel.getNamespace(),
                                                   getItemDefinitionConsumer());
    }

   protected R restoreDeletedModel() {
        return (R) new DefaultIncludedModelActiveRecord(recordEngine);
    }

    Consumer<List<ItemDefinition>> getItemDefinitionConsumer() {
        return itemDefinitions -> getRefreshDataTypesListEvent().fire(new RefreshDataTypesListEvent(itemDefinitions));
    }
}
