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

package org.kie.workbench.common.dmn.client.editors.included.grid;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.client.api.included.legacy.DMNIncludeModelsClient;
import org.kie.workbench.common.dmn.client.docks.navigator.events.RefreshDecisionComponents;
import org.kie.workbench.common.dmn.client.editors.included.PMMLIncludedModelActiveRecord;
import org.kie.workbench.common.dmn.client.editors.included.commands.RemoveIncludedModelCommand;
import org.kie.workbench.common.dmn.client.editors.included.commands.RemovePMMLIncludedModelCommand;
import org.kie.workbench.common.dmn.client.editors.included.imports.persistence.ImportRecordEngine;
import org.kie.workbench.common.dmn.client.editors.types.common.events.RefreshDataTypesListEvent;
import org.kie.workbench.common.dmn.client.events.IncludedPMMLModelUpdate;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;

public class PMMLCardComponent extends BaseCardComponent<PMMLIncludedModelActiveRecord, PMMLCardComponent.ContentView> {

    @Inject
    public PMMLCardComponent(final @PMMLCard PMMLCardComponent.ContentView contentView,
                             final @IncludedPMMLModelUpdate Event<RefreshDecisionComponents> refreshDecisionComponentsEvent,
                             final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                             final SessionManager sessionManager,
                             final ImportRecordEngine recordEngine,
                             final DMNIncludeModelsClient client,
                             final Event<RefreshDataTypesListEvent> refreshDataTypesListEvent) {
        super(contentView,
              refreshDecisionComponentsEvent,
              sessionCommandManager,
              sessionManager,
              recordEngine,
              client,
              refreshDataTypesListEvent);
    }

    @PostConstruct
    @Override
    public void init() {
        contentView.init(this);
    }

    @Override
    protected void refreshView() {
        super.refreshView();
        contentView.setModelCount(getModelCount());
    }

    @Override
    RemoveIncludedModelCommand getRemoveCommand() {
        return new RemovePMMLIncludedModelCommand(getGrid(),
                                                  getIncludedModel(),
                                                  client,
                                                  refreshDecisionComponentsEvent,
                                                  recordEngine,
                                                  refreshDataTypesListEvent);
    }

    private Integer getModelCount() {
        return getIncludedModel().getModelCount();
    }

    public interface ContentView extends DefaultCardComponent.ContentView {

        void setModelCount(final Integer modelCount);
    }
}
