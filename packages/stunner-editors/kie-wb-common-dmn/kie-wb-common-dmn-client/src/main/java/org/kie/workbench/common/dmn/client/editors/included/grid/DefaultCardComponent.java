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
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.client.api.included.legacy.DMNIncludeModelsClient;
import org.kie.workbench.common.dmn.client.docks.navigator.events.RefreshDecisionComponents;
import org.kie.workbench.common.dmn.client.editors.included.DefaultIncludedModelActiveRecord;
import org.kie.workbench.common.dmn.client.editors.included.commands.RemoveIncludedModelCommand;
import org.kie.workbench.common.dmn.client.editors.included.imports.persistence.ImportRecordEngine;
import org.kie.workbench.common.dmn.client.editors.types.common.events.RefreshDataTypesListEvent;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;

@Dependent
public class DefaultCardComponent extends BaseCardComponent<DefaultIncludedModelActiveRecord, BaseCardComponent.ContentView> {

    @Inject
    public DefaultCardComponent(final @Default BaseCardComponent.ContentView contentView,
                                final Event<RefreshDecisionComponents> refreshDecisionComponentsEvent,
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
    public void init() {
        contentView.init(this);
    }

    @Override
    RemoveIncludedModelCommand getRemoveCommand() {
        return new RemoveIncludedModelCommand<>(getGrid(),
                                                getIncludedModel(),
                                                client,
                                                refreshDecisionComponentsEvent,
                                                recordEngine,
                                                refreshDataTypesListEvent);
    }
}
