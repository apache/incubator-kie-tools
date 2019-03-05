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

package org.kie.workbench.common.stunner.bpmn.project.client.handlers.util;

import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.Callback;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.stunner.bpmn.service.BPMNDiagramService;
import org.kie.workbench.common.stunner.bpmn.service.ProjectType;

@Dependent
public class CaseHelper {

    private final Caller<BPMNDiagramService> bpmnDiagramService;
    private final WorkspaceProjectContext projectContext;

    @Inject
    public CaseHelper(final Caller<BPMNDiagramService> bpmnDiagramService,
                      final WorkspaceProjectContext projectContext) {
        this.bpmnDiagramService = bpmnDiagramService;
        this.projectContext = projectContext;
    }

    public void acceptContext(final Callback<Boolean, Void> callback) {
        projectContext.getActiveWorkspaceProject()
                .map(WorkspaceProject::getRootPath)
                .ifPresent(path -> bpmnDiagramService.call(
                        projectType -> Optional.ofNullable(projectType)
                                .filter(ProjectType.CASE::equals)
                                .ifPresent(p -> callback.onSuccess(true)))
                        .getProjectType(path));
    }
}
