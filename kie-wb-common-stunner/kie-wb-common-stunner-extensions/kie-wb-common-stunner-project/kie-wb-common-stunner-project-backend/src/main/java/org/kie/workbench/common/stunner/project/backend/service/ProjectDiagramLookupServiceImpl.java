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
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.stunner.core.backend.service.AbstractDiagramLookupService;
import org.kie.workbench.common.stunner.core.lookup.diagram.DiagramLookupRequest;
import org.kie.workbench.common.stunner.core.service.BaseDiagramService;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.kie.workbench.common.stunner.project.diagram.ProjectMetadata;
import org.kie.workbench.common.stunner.project.service.ProjectDiagramLookupService;
import org.kie.workbench.common.stunner.project.service.ProjectDiagramService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;

@ApplicationScoped
@Service
public class ProjectDiagramLookupServiceImpl
        extends AbstractDiagramLookupService<ProjectMetadata, ProjectDiagram>
        implements ProjectDiagramLookupService {

    private final IOService ioService;
    private final ProjectDiagramService diagramService;

    protected ProjectDiagramLookupServiceImpl() {
        this(null,
             null);
    }

    @Inject
    public ProjectDiagramLookupServiceImpl(final @Named("ioStrategy") IOService ioService,
                                           final ProjectDiagramService diagramService) {
        this.ioService = ioService;
        this.diagramService = diagramService;
    }

    @PostConstruct
    public void init() {
        initialize(ioService);
    }

    @Override
    protected BaseDiagramService<ProjectMetadata, ProjectDiagram> getDiagramService() {
        return diagramService;
    }

    @Override
    protected List<ProjectDiagram> getItems(final DiagramLookupRequest request) {
        return getVFSLookupManager().getItemsByPath(Paths.convert(request.getPath()));
    }

    @Override
    protected boolean matches(String criteria, ProjectDiagram item) {
        return true;
    }
}
