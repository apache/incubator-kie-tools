/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.dmn.showcase.client.screens;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasExport;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Layer;
import org.kie.workbench.common.stunner.core.client.service.ClientDiagramService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientFullSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.lookup.LookupManager;
import org.kie.workbench.common.stunner.core.lookup.diagram.DiagramLookupRequest;
import org.kie.workbench.common.stunner.core.lookup.diagram.DiagramRepresentation;
import org.uberfire.backend.vfs.Path;

@ApplicationScoped
public class ShowcaseDiagramService {

    private final ClientDiagramService clientDiagramServices;
    private final CanvasExport<AbstractCanvasHandler> canvasExport;

    protected ShowcaseDiagramService() {
        this(null,
             null);
    }

    @Inject
    public ShowcaseDiagramService(final ClientDiagramService clientDiagramServices,
                                  final CanvasExport<AbstractCanvasHandler> canvasExport) {
        this.clientDiagramServices = clientDiagramServices;
        this.canvasExport = canvasExport;
    }

    public void loadByName(final String name,
                           final ServiceCallback<Diagram> callback) {
        final DiagramLookupRequest request = new DiagramLookupRequest.Builder().withName(name).build();
        clientDiagramServices.lookup(request,
                                     new ServiceCallback<LookupManager.LookupResponse<DiagramRepresentation>>() {
                                         @Override
                                         public void onSuccess(LookupManager.LookupResponse<DiagramRepresentation> diagramRepresentations) {
                                             if (null != diagramRepresentations && !diagramRepresentations.getResults().isEmpty()) {
                                                 final Path path = diagramRepresentations.getResults().get(0).getPath();
                                                 loadByPath(path,
                                                            callback);
                                             }
                                         }

                                         @Override
                                         public void onError(final ClientRuntimeError error) {
                                             callback.onError(error);
                                         }
                                     });
    }

    @SuppressWarnings("unchecked")
    public void loadByPath(final Path path,
                           final ServiceCallback<Diagram> callback) {
        clientDiagramServices.getByPath(path,
                                        new ServiceCallback<Diagram<Graph, Metadata>>() {
                                            @Override
                                            public void onSuccess(final Diagram<Graph, Metadata> diagram) {
                                                callback.onSuccess(diagram);
                                            }

                                            @Override
                                            public void onError(final ClientRuntimeError error) {
                                                callback.onError(error);
                                            }
                                        });
    }

    @SuppressWarnings("unchecked")
    public void save(final Diagram diagram,
                     final ServiceCallback<Diagram<Graph, Metadata>> diagramServiceCallback) {
        // Perform update operation remote call.
        clientDiagramServices.saveOrUpdate(diagram,
                                           diagramServiceCallback);
    }

    public void save(final AbstractClientFullSession session,
                     final ServiceCallback<Diagram<Graph, Metadata>> diagramServiceCallback) {
        // Update diagram's image data as thumbnail.
        final String thumbData = toImageData(session);
        final CanvasHandler canvasHandler = session.getCanvasHandler();
        final Diagram diagram = canvasHandler.getDiagram();
        diagram.getMetadata().setThumbData(thumbData);
        save(diagram,
             diagramServiceCallback);
    }

    private String toImageData(final AbstractClientFullSession session) {
        return canvasExport.toImageData(session.getCanvasHandler(),
                                        Layer.URLDataType.JPG);
    }
}
