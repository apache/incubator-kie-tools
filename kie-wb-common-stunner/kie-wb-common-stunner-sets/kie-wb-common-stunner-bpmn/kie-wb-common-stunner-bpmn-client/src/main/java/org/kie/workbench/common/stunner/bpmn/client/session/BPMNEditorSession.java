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

package org.kie.workbench.common.stunner.bpmn.client.session;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.client.workitem.WorkItemDefinitionClientRegistry;
import org.kie.workbench.common.stunner.bpmn.qualifiers.BPMN;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.Request;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.preferences.StunnerPreferencesRegistry;
import org.kie.workbench.common.stunner.core.client.session.Session;
import org.kie.workbench.common.stunner.core.client.session.impl.DefaultEditorSession;
import org.kie.workbench.common.stunner.core.client.session.impl.ManagedSession;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.registry.RegistryFactory;
import org.uberfire.mvp.Command;

@Dependent
@BPMN
public class BPMNEditorSession
        extends DefaultEditorSession {

    private final WorkItemDefinitionClientRegistry workItemDefinitionService;

    @Inject
    public BPMNEditorSession(final ManagedSession session,
                             final RegistryFactory registryFactory,
                             final CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager,
                             final @Session SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                             final @Request SessionCommandManager<AbstractCanvasHandler> requestCommandManager,
                             final StunnerPreferencesRegistry stunnerPreferencesRegistry,
                             final WorkItemDefinitionClientRegistry workItemDefinitionService) {
        super(session,
              registryFactory,
              canvasCommandManager,
              sessionCommandManager,
              requestCommandManager,
              stunnerPreferencesRegistry);
        this.workItemDefinitionService = workItemDefinitionService;
    }

    @Override
    public void init(final Metadata metadata,
                     final Command callback) {
        super.init(metadata,
                   () -> {
                       workItemDefinitionService.load(metadata,
                                                      callback);
                   });
    }
}