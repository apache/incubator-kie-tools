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


package org.kie.workbench.common.stunner.core.client.service;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDiagramSavedEvent;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.service.BaseDiagramService;
import org.kie.workbench.common.stunner.core.service.DiagramLookupService;
import org.kie.workbench.common.stunner.core.service.DiagramService;

/**
 * A wrapper util class for handling different diagram services from client side.
 */
@Dependent
public class ClientDiagramServiceImpl<M extends Metadata, D extends Diagram<Graph, M>, S extends BaseDiagramService<M, D>> extends AbstractClientDiagramService<M, D, S> {

    protected ClientDiagramServiceImpl() {
        super(null, null, null, null, null);
    }

    public ClientDiagramServiceImpl(final ShapeManager shapeManager,
                                    final SessionManager sessionManager,
                                    final Caller<S> diagramServiceCaller,
                                    final Caller<DiagramLookupService> diagramLookupServiceCaller,
                                    final Event<SessionDiagramSavedEvent> saveEvent) {
        super(shapeManager,
              sessionManager,
              diagramServiceCaller,
              diagramLookupServiceCaller,
              saveEvent);
    }

    @Inject
    public ClientDiagramServiceImpl(final ShapeManager shapeManager,
                                    final SessionManager sessionManager,
                                    final Caller<DiagramLookupService> diagramLookupServiceCaller,
                                    final Event<SessionDiagramSavedEvent> saveEvent,
                                    final Caller<DiagramService> diagramServiceCaller) {
        super(shapeManager,
              sessionManager,
              (Caller<S>) diagramServiceCaller,
              diagramLookupServiceCaller,
              saveEvent);
    }
}