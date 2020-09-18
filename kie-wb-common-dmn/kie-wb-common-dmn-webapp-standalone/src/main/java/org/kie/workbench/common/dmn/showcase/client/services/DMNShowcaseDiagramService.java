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
package org.kie.workbench.common.dmn.showcase.client.services;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.dmn.api.DMNContentService;
import org.kie.workbench.common.dmn.client.marshaller.DMNMarshallerService;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.export.CanvasExport;
import org.kie.workbench.common.stunner.core.client.canvas.export.CanvasURLExportSettings;
import org.kie.workbench.common.stunner.core.client.service.ClientDiagramServiceImpl;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.lookup.LookupManager;
import org.kie.workbench.common.stunner.core.lookup.diagram.DiagramLookupRequest;
import org.kie.workbench.common.stunner.core.lookup.diagram.DiagramRepresentation;
import org.uberfire.backend.vfs.Path;

@ApplicationScoped
public class DMNShowcaseDiagramService {

    private final ClientDiagramServiceImpl clientDiagramServices;
    private final CanvasExport<AbstractCanvasHandler> canvasExport;
    private final DMNMarshallerService dmnMarshallerService;
    private final Caller<DMNContentService> dmnContentServiceCaller;

    @Inject
    public DMNShowcaseDiagramService(final ClientDiagramServiceImpl clientDiagramServices,
                                     final CanvasExport<AbstractCanvasHandler> canvasExport,
                                     final DMNMarshallerService dmnMarshallerService,
                                     final Caller<DMNContentService> dmnContentServiceCaller) {
        this.clientDiagramServices = clientDiagramServices;
        this.canvasExport = canvasExport;
        this.dmnMarshallerService = dmnMarshallerService;
        this.dmnContentServiceCaller = dmnContentServiceCaller;
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
        dmnContentServiceCaller.call((final String xml) -> {
            dmnMarshallerService.unmarshall(path, xml, callback);
        }).getContent(path);
    }

    @SuppressWarnings("unchecked")
    public void save(final Diagram diagram,
                     final ServiceCallback<Diagram<Graph, Metadata>> diagramServiceCallback) {

        dmnMarshallerService.marshall(diagram, new ServiceCallback<String>() {
            @Override
            public void onSuccess(final String xml) {
                final Metadata metadata = diagram.getMetadata();
                final Path path = metadata.getPath();
                dmnContentServiceCaller.call(
                        (o) -> diagramServiceCallback.onSuccess(diagram),
                        (message, throwable) -> {
                            diagramServiceCallback.onError(new ClientRuntimeError(throwable));
                            return false;
                        })
                        .saveContent(path, xml, null, "Saved");
            }

            @Override
            public void onError(final ClientRuntimeError e) {
                diagramServiceCallback.onError(e);
            }
        });
    }

    public void save(final EditorSession session,
                     final ServiceCallback<Diagram<Graph, Metadata>> diagramServiceCallback) {
        // Update diagram's image data as thumbnail.
        final String thumbData = toImageData(session);
        final CanvasHandler canvasHandler = session.getCanvasHandler();
        final Diagram diagram = canvasHandler.getDiagram();
        diagram.getMetadata().setThumbData(thumbData);
        save(diagram,
             diagramServiceCallback);
    }

    private String toImageData(final EditorSession session) {
        return canvasExport.toImageData(session.getCanvasHandler(),
                                        CanvasURLExportSettings.build(CanvasExport.URLDataType.JPG));
    }
}
