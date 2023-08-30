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


package org.kie.workbench.common.stunner.core.client.session.event;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.diagram.Diagram;

@ApplicationScoped
public class SessionEventObserver {

    private List<SessionDiagramOpenedHandler> sessionDiagramOpenedHandlers = new ArrayList<>();
    private List<SessionDiagramSavedHandler> sessionDiagramSavedHandlers = new ArrayList<>();

    public SessionEventObserver() {
        //proxying constructor
    }

    @Inject
    public SessionEventObserver(@Any final Instance<SessionDiagramOpenedHandler> sessionDiagramOpenedHandlersInstance,
                                @Any final Instance<SessionDiagramSavedHandler> sessionDiagramSavedHandlersInstance) {
        sessionDiagramOpenedHandlersInstance.iterator().forEachRemaining(handler -> this.sessionDiagramOpenedHandlers.add(handler));
        sessionDiagramSavedHandlersInstance.iterator().forEachRemaining(handler -> this.sessionDiagramSavedHandlers.add(handler));
    }

    void onSessionDiagramOpenedEvent(@Observes final SessionDiagramOpenedEvent event) {
        final Diagram currentDiagram = event.getSession().getCanvasHandler().getDiagram();
        sessionDiagramOpenedHandlers.stream()
                .filter(handler -> handler.accepts(currentDiagram))
                .forEach(handler -> handler.onSessionDiagramOpened(event.getSession()));
    }

    void onSessionDiagramSavedEvent(@Observes final SessionDiagramSavedEvent event) {
        final Diagram currentDiagram = event.getSession().getCanvasHandler().getDiagram();
        sessionDiagramSavedHandlers.stream()
                .filter(handler -> handler.accepts(currentDiagram))
                .forEach(handler -> handler.onSessionDiagramSaved(event.getSession()));
    }
}
