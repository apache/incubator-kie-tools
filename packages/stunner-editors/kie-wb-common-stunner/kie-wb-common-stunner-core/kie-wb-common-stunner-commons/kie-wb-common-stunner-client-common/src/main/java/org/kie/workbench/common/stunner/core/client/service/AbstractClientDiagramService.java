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

import javax.enterprise.event.Event;

import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDiagramSavedEvent;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.lookup.LookupManager;
import org.kie.workbench.common.stunner.core.lookup.diagram.DiagramLookupRequest;
import org.kie.workbench.common.stunner.core.lookup.diagram.DiagramRepresentation;
import org.kie.workbench.common.stunner.core.service.BaseDiagramService;
import org.kie.workbench.common.stunner.core.service.DiagramLookupService;
import org.uberfire.backend.vfs.Path;

public abstract class AbstractClientDiagramService<M extends Metadata, D extends Diagram<Graph, M>, S extends BaseDiagramService<M, D>> implements ClientDiagramService<M, D, S> {

    private final ShapeManager shapeManager;
    protected final SessionManager sessionManager;
    protected final Caller<S> diagramServiceCaller;
    protected final Caller<DiagramLookupService> diagramLookupServiceCaller;
    private final Event<SessionDiagramSavedEvent> saveEvent;

    public AbstractClientDiagramService(final ShapeManager shapeManager,
                                        final SessionManager sessionManager,
                                        final Caller<S> diagramServiceCaller,
                                        final Caller<DiagramLookupService> diagramLookupServiceCaller,
                                        final Event<SessionDiagramSavedEvent> saveEvent) {
        this.shapeManager = shapeManager;
        this.sessionManager = sessionManager;
        this.diagramServiceCaller = diagramServiceCaller;
        this.diagramLookupServiceCaller = diagramLookupServiceCaller;
        this.saveEvent = saveEvent;
    }

    @Override
    public void create(final Path path,
                       final String name,
                       final String defSetId,
                       final ServiceCallback<Path> callback) {
        diagramServiceCaller.call(p -> callback.onSuccess(path),
                                  (message, throwable) -> {
                                      callback.onError(new ClientRuntimeError(throwable));
                                      return false;
                                  }).create(path,
                                            name,
                                            defSetId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void saveOrUpdate(final D diagram,
                             final ServiceCallback<D> callback) {
        diagramServiceCaller.call(serverMetadata -> {
                                      updateClientMetadata(diagram);
                                      diagram.getMetadata().setPath(((M) serverMetadata).getPath());
                                      callback.onSuccess(diagram);
                                      fireSavedEvent(sessionManager.getCurrentSession());
                                  },
                                  (message, throwable) -> {
                                      callback.onError(new ClientRuntimeError(throwable));
                                      return false;
                                  }).saveOrUpdate(diagram);
    }

    @Override
    public void saveOrUpdateSvg(Path diagramPath, String rawSvg, ServiceCallback<Path> callback) {
        diagramServiceCaller.call(res -> callback.onSuccess((Path) res)).saveOrUpdateSvg(diagramPath, rawSvg);
    }

    protected void fireSavedEvent(final ClientSession session) {
        saveEvent.fire(new SessionDiagramSavedEvent(session));
    }

    @Override
    public void add(final D diagram,
                    final ServiceCallback<D> callback) {
        diagramServiceCaller.call(v -> {
                                      updateClientMetadata(diagram);
                                      callback.onSuccess(diagram);
                                  },
                                  (message, throwable) -> {
                                      callback.onError(new ClientRuntimeError(throwable));
                                      return false;
                                  }).saveOrUpdate(diagram);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void getByPath(final Path path,
                          final ServiceCallback<D> callback) {
        diagramServiceCaller.call(diagram -> {
                                      updateClientMetadata((D) diagram);
                                      callback.onSuccess((D) diagram);
                                  },
                                  (message, throwable) -> {
                                      callback.onError(new ClientRuntimeError(throwable));
                                      return false;
                                  }).getDiagramByPath(path);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void lookup(final DiagramLookupRequest request,
                       final ServiceCallback<LookupManager.LookupResponse<DiagramRepresentation>> callback) {
        diagramLookupServiceCaller.call(response -> callback.onSuccess((LookupManager.LookupResponse<DiagramRepresentation>) response),
                                        (message, throwable) -> {
                                            callback.onError(new ClientRuntimeError(throwable));
                                            return false;
                                        }).lookup(request);
    }

    @Override
    public void getRawContent(final D diagram,
                              final ServiceCallback<String> callback) {
        diagramServiceCaller.call(rawContent -> {
                                      callback.onSuccess((String) rawContent);
                                  },
                                  (message, throwable) -> {
                                      callback.onError(new ClientRuntimeError(throwable));
                                      return false;
                                  }
        ).getRawContent(diagram);
    }

    protected void updateClientMetadata(final D diagram) {
        if (null != diagram) {
            final Metadata metadata = diagram.getMetadata();
            if (null != metadata && isEmpty(metadata.getShapeSetId())) {
                final String sId = shapeManager.getDefaultShapeSet(metadata.getDefinitionSetId()).getId();
                metadata.setShapeSetId(sId);
            }
        }
    }

    private static boolean isEmpty(final String s) {
        return s == null || s.trim().length() == 0;
    }
}
