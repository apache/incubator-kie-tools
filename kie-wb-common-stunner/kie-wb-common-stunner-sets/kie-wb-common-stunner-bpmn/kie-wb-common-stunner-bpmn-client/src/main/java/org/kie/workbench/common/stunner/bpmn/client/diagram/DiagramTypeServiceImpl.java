/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.diagram;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.stunner.bpmn.service.BPMNDiagramService;
import org.kie.workbench.common.stunner.bpmn.service.ProjectType;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDestroyedEvent;
import org.kie.workbench.common.stunner.core.diagram.Metadata;

@ApplicationScoped
public class DiagramTypeServiceImpl implements DiagramTypeService {

    private final Caller<BPMNDiagramService> bpmnDiagramService;
    private static final Map<String, ProjectType> projectTypeRegistry = new HashMap<>();

    DiagramTypeServiceImpl() {
        this(null);
    }

    @Inject
    public DiagramTypeServiceImpl(Caller<BPMNDiagramService> bpmnDiagramService) {
        this.bpmnDiagramService = bpmnDiagramService;
    }

    @Override
    public void loadDiagramType(Metadata metadata) {
        bpmnDiagramService
                .call((r) -> setProjectType(metadata, (ProjectType) r))
                .getProjectType(metadata.getRoot());
    }

    void setProjectType(final Metadata metadata, final ProjectType projectType) {
        projectTypeRegistry.put(getDiagramId(metadata), projectType);
    }

    private String getDiagramId(Metadata metadata) {
        return metadata.getCanvasRootUUID();
    }

    @Override
    public ProjectType getProjectType(final Metadata metadata) {
        return Optional.ofNullable(projectTypeRegistry.get(getDiagramId(metadata)))
                .orElse(ProjectType.BPMN);
    }

    protected void onSessionClosed(@Observes final SessionDestroyedEvent event) {
        projectTypeRegistry.remove(getDiagramId(event.getMetadata()));
    }
}
