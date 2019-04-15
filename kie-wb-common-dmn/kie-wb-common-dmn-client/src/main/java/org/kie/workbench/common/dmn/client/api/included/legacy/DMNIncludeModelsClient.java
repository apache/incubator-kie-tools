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

package org.kie.workbench.common.dmn.client.api.included.legacy;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.DomGlobal;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedModel;
import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedModelsService;
import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedNode;

@Dependent
public class DMNIncludeModelsClient {

    private final Caller<DMNIncludedModelsService> service;

    private final WorkspaceProjectContext projectContext;

    @Inject
    public DMNIncludeModelsClient(final Caller<DMNIncludedModelsService> service,
                                  final WorkspaceProjectContext projectContext) {
        this.service = service;
        this.projectContext = projectContext;
    }

    public void loadModels(final Consumer<List<DMNIncludedModel>> listConsumer) {
        service.call(onSuccess(listConsumer), onError(listConsumer)).loadModels(getWorkspaceProject());
    }

    public void loadNodesFromImports(final List<DMNIncludedModel> includeModels,
                                     final Consumer<List<DMNIncludedNode>> listConsumer) {
        service.call(onSuccess(listConsumer), onError(listConsumer)).loadNodesFromImports(getWorkspaceProject(), includeModels);
    }

    <T> ErrorCallback<Boolean> onError(final Consumer<List<T>> listConsumer) {
        return (message, throwable) -> {
            logWarning();
            listConsumer.accept(new ArrayList<>());
            return false;
        };
    }

    <T> RemoteCallback<List<T>> onSuccess(final Consumer<List<T>> listConsumer) {
        return listConsumer::accept;
    }

    private WorkspaceProject getWorkspaceProject() {
        return projectContext.getActiveWorkspaceProject().orElse(null);
    }

    private void logWarning() {
        warn("[WARNING] DMNIncludeModelsClient could not get the asset list.");
    }

    void warn(final String message) {
        DomGlobal.console.warn(message);
    }
}
