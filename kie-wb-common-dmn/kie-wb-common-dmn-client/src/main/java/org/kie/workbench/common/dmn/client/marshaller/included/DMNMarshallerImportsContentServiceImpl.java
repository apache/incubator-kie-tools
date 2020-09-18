/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.marshaller.included;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import elemental2.promise.Promise;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.dmn.api.DMNContentService;
import org.kie.workbench.common.dmn.api.editors.included.PMMLDocumentMetadata;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.promise.Promises;

@ApplicationScoped
public class DMNMarshallerImportsContentServiceImpl implements DMNMarshallerImportsContentService {

    private final Caller<DMNContentService> dmnContentServiceCaller;

    private final Promises promises;

    private final WorkspaceProjectContext projectContext;

    @Inject
    public DMNMarshallerImportsContentServiceImpl(final Caller<DMNContentService> dmnContentServiceCaller,
                                                  final Promises promises,
                                                  final WorkspaceProjectContext projectContext) {
        this.dmnContentServiceCaller = dmnContentServiceCaller;
        this.promises = promises;
        this.projectContext = projectContext;
    }

    @Override
    public Promise<String> loadFile(final String fileUri) {
        return promises.create((success, failure) -> {
            dmnContentServiceCaller.call(
                    (RemoteCallback<String>) success::onInvoke,
                    (message, throwable) -> {
                        failure.onInvoke(new ClientRuntimeError(throwable));
                        return false;
                    }).getContent(makePath(fileUri));
        });
    }

    @Override
    public Promise<String[]> getModelsURIs() {
        return promises.create((success, failure) -> {
            getContentServiceCaller(success, failure).getModelsPaths(getWorkspaceProject());
        });
    }

    @Override
    public Promise<String[]> getModelsDMNFilesURIs() {
        return promises.create((success, failure) -> {
            getContentServiceCaller(success, failure).getDMNModelsPaths(getWorkspaceProject());
        });
    }

    @Override
    public Promise<String[]> getModelsPMMLFilesURIs() {
        return promises.create((success, failure) -> {
            getContentServiceCaller(success, failure).getPMMLModelsPaths(getWorkspaceProject());
        });
    }

    @Override
    public Promise<PMMLDocumentMetadata> getPMMLDocumentMetadata(final String fileUri) {
        return promises.create((success, failure) -> {
            dmnContentServiceCaller.call(
                    (RemoteCallback<PMMLDocumentMetadata>) success::onInvoke,
                    (message, throwable) -> {
                        failure.onInvoke(new ClientRuntimeError(throwable));
                        return false;
                    }).loadPMMLDocumentMetadata(makePath(fileUri));
        });
    }

    private Path makePath(final String fileUri) {
        return PathFactory.newPath(".", fileUri);
    }

    private DMNContentService getContentServiceCaller(final Promise.PromiseExecutorCallbackFn.ResolveCallbackFn<String[]> success,
                                                      final Promise.PromiseExecutorCallbackFn.RejectCallbackFn failure) {
        return dmnContentServiceCaller.call(
                (List<Path> paths) -> {
                    success.onInvoke(paths.stream().map(Path::toURI).toArray(String[]::new));
                },
                (message, throwable) -> {
                    failure.onInvoke(new ClientRuntimeError(throwable));
                    return false;
                });
    }

    private WorkspaceProject getWorkspaceProject() {
        return projectContext.getActiveWorkspaceProject().orElse(null);
    }
}
