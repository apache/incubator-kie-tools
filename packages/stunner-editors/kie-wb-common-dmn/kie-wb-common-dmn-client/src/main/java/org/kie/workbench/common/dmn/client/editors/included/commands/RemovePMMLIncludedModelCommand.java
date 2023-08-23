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

import javax.enterprise.event.Event;

import org.kie.workbench.common.dmn.client.api.included.legacy.DMNIncludeModelsClient;
import org.kie.workbench.common.dmn.client.docks.navigator.events.RefreshDecisionComponents;
import org.kie.workbench.common.dmn.client.editors.included.PMMLIncludedModelActiveRecord;
import org.kie.workbench.common.dmn.client.editors.included.grid.DMNCardsGridComponent;
import org.kie.workbench.common.dmn.client.editors.included.imports.persistence.ImportRecordEngine;
import org.kie.workbench.common.dmn.client.editors.types.common.events.RefreshDataTypesListEvent;

public class RemovePMMLIncludedModelCommand extends RemoveIncludedModelCommand<PMMLIncludedModelActiveRecord> {

    private Integer modelCount;

    public RemovePMMLIncludedModelCommand(final DMNCardsGridComponent grid,
                                          final PMMLIncludedModelActiveRecord includedModel,
                                          final DMNIncludeModelsClient client,
                                          final Event<RefreshDecisionComponents> refreshPMMLComponentsEvent,
                                          final ImportRecordEngine recordEngine,
                                          final Event<RefreshDataTypesListEvent> refreshDataTypesListEvent) {
        super(grid, includedModel, client, refreshPMMLComponentsEvent, recordEngine, refreshDataTypesListEvent);
    }

    public Integer getModelCount() {
        return modelCount;
    }

    public void setModelCount(final Integer modelCount) {
        this.modelCount = modelCount;
    }

    @Override
    protected PMMLIncludedModelActiveRecord restoreDeletedModel() {
        final PMMLIncludedModelActiveRecord pmmlIncludedModel = new PMMLIncludedModelActiveRecord(getRecordEngine());
        pmmlIncludedModel.setModelCount(getModelCount());
        return pmmlIncludedModel;
    }

    @Override
    protected void saveDeletedIncludedModelData() {
        setModelCount(getIncludedModel().getModelCount());
        superSaveDeletedIncludedModelData();
    }

    void superSaveDeletedIncludedModelData() {
        super.saveDeletedIncludedModelData();
    }
}
