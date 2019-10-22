/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.standalone.client.editor;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.annotation.DiagramEditor;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.standalone.client.services.BPMNStandaloneClientDiagramServiceImpl;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;

@ApplicationScoped
public class BPMNStandaloneDiagramWrapper {

    @Inject
    @DiagramEditor
    private BPMNStandaloneDiagramEditor diagramEditor;

    private PlaceManager placeManager;
    private BPMNStandaloneClientDiagramServiceImpl clientDiagramService;

    public BPMNStandaloneDiagramWrapper() {
        //CDI proxy
    }

    @Inject
    public BPMNStandaloneDiagramWrapper(final PlaceManager placeManager,
                                        final BPMNStandaloneClientDiagramServiceImpl clientDiagramService) {
        this.placeManager = placeManager;
        this.clientDiagramService = clientDiagramService;
    }

    public void newFile() {
        placeManager.registerOnOpenCallback(BPMNDiagramsNavigatorScreen.DIAGRAM_EDITOR,
                                            () -> {
                                                diagramEditor.setContent("");
                                                placeManager.unregisterOnOpenCallbacks(BPMNDiagramsNavigatorScreen.DIAGRAM_EDITOR);
                                            });

        placeManager.goTo(BPMNDiagramsNavigatorScreen.DIAGRAM_EDITOR);
    }

    public void openFile(final Path path) {
        placeManager.registerOnOpenCallback(BPMNDiagramsNavigatorScreen.DIAGRAM_EDITOR,
                                            () -> {
                                                clientDiagramService.loadAsXml(path,
                                                                               new ServiceCallback<String>() {
                                                                                   @Override
                                                                                   public void onSuccess(final String xml) {
                                                                                       diagramEditor.setContent(xml);
                                                                                       placeManager.unregisterOnOpenCallbacks(BPMNDiagramsNavigatorScreen.DIAGRAM_EDITOR);
                                                                                   }

                                                                                   @Override
                                                                                   public void onError(final ClientRuntimeError error) {
                                                                                       placeManager.unregisterOnOpenCallbacks(BPMNDiagramsNavigatorScreen.DIAGRAM_EDITOR);
                                                                                   }
                                                                               });
                                            });

        placeManager.goTo(BPMNDiagramsNavigatorScreen.DIAGRAM_EDITOR);
    }

    @SuppressWarnings("unchecked")
    public void saveFile(final ServiceCallback<String> callback) {
        final Path path = diagramEditor.getCanvasHandler().getDiagram().getMetadata().getPath();
        diagramEditor.getContent().then(xml -> {
            clientDiagramService.saveAsXml(path,
                                           (String) xml,
                                           callback);
            return null;
        });
    }
}
