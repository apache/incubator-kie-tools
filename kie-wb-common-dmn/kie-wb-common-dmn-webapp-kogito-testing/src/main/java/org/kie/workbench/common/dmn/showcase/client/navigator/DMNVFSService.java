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

package org.kie.workbench.common.dmn.showcase.client.navigator;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.dmn.showcase.client.editor.DMNDiagramEditor;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.commons.uuid.UUID;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

@ApplicationScoped
public class DMNVFSService {

    private PlaceManager placeManager;
    private Caller<VFSService> vfsServiceCaller;

    public DMNVFSService() {
        //CDI proxy
    }

    @Inject
    public DMNVFSService(final PlaceManager placeManager,
                         final Caller<VFSService> vfsServiceCaller) {
        this.placeManager = placeManager;
        this.vfsServiceCaller = vfsServiceCaller;
    }

    public void newFile() {
        final PlaceRequest placeRequest = new DefaultPlaceRequest(DMNDiagramEditor.EDITOR_ID);
        placeRequest.addParameter(DMNDiagramEditor.FILE_NAME_PARAMETER_NAME, UUID.uuid() + ".dmn");
        placeManager.goTo(placeRequest);
    }

    public void openFile(final Path path) {
        vfsServiceCaller.call((String xml) -> {
            final PlaceRequest placeRequest = new DefaultPlaceRequest(DMNDiagramEditor.EDITOR_ID);
            placeRequest.addParameter(DMNDiagramEditor.FILE_NAME_PARAMETER_NAME, path.getFileName());
            placeRequest.addParameter(DMNDiagramEditor.CONTENT_PARAMETER_NAME, xml);
            placeManager.goTo(placeRequest);
        }).readAllString(path);
    }

    public void openFile(final String fileName, final String xml){
        final PlaceRequest placeRequest = new DefaultPlaceRequest(DMNDiagramEditor.EDITOR_ID);
        placeRequest.addParameter(DMNDiagramEditor.FILE_NAME_PARAMETER_NAME, fileName);
        placeRequest.addParameter(DMNDiagramEditor.CONTENT_PARAMETER_NAME, xml);
        placeManager.goTo(placeRequest);
    }

    @SuppressWarnings("unchecked")
    public void saveFile(final Path path,
                         final String xml,
                         final ServiceCallback<String> callback) {
        vfsServiceCaller.call((Path p) -> callback.onSuccess(xml)).write(path, xml);
    }
}
