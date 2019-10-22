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

package org.kie.workbench.common.stunner.project.backend.service;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.stunner.core.backend.lookup.impl.VFSLookupManager;
import org.kie.workbench.common.stunner.core.backend.service.AbstractDiagramLookupService;
import org.kie.workbench.common.stunner.core.lookup.diagram.DiagramLookupRequest;
import org.kie.workbench.common.stunner.core.service.BaseDiagramService;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.kie.workbench.common.stunner.project.diagram.ProjectMetadata;
import org.kie.workbench.common.stunner.project.service.ProjectDiagramLookupService;
import org.kie.workbench.common.stunner.project.service.ProjectDiagramService;

@ApplicationScoped
@Service
public class ProjectDiagramLookupServiceImpl
        extends AbstractDiagramLookupService<ProjectMetadata, ProjectDiagram>
        implements ProjectDiagramLookupService {

    private final VFSLookupManager<ProjectDiagram> vfsLookupManager;
    private final ProjectDiagramService diagramService;

    protected ProjectDiagramLookupServiceImpl() {
        this(null,
             null);
    }

    @Inject
    public ProjectDiagramLookupServiceImpl(final VFSLookupManager<ProjectDiagram> vfsLookupManager,
                                           final ProjectDiagramService diagramService) {
        this.vfsLookupManager = vfsLookupManager;
        this.diagramService = diagramService;
    }

    @PostConstruct
    public void init() {
        initialize(vfsLookupManager);
    }

    @Override
    protected BaseDiagramService<ProjectMetadata, ProjectDiagram> getDiagramService() {
        return diagramService;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected List<ProjectDiagram> getItems(final DiagramLookupRequest request) {
        return vfsLookupManager.getItemsByPath(request.getPath());
    }

    @Override
    protected boolean matches(final String criteria,
                              final ProjectDiagram item) {
        return true;
    }
}
