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

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.services.backend.service.KieService;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.definition.service.DefinitionSetService;
import org.kie.workbench.common.stunner.core.registry.BackendRegistryFactory;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.kie.workbench.common.stunner.project.diagram.ProjectMetadata;
import org.kie.workbench.common.stunner.project.service.ProjectDiagramService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.events.ResourceOpenedEvent;

@Service
@ApplicationScoped
public class ProjectDiagramServiceImpl extends KieService<ProjectDiagram>
        implements ProjectDiagramService {

    private static final Logger LOG =
            LoggerFactory.getLogger(ProjectDiagramServiceImpl.class.getName());

    private final SessionInfo sessionInfo;
    private final Event<ResourceOpenedEvent> resourceOpenedEvent;
    private final CommentedOptionFactory commentedOptionFactory;
    private final ProjectDiagramServiceController controller;

    protected ProjectDiagramServiceImpl() {
        this(null,
             null,
             null,
             null,
             null,
             null,
             null,
             null,
             null);
    }

    @Inject
    public ProjectDiagramServiceImpl(final DefinitionManager definitionManager,
                                     final FactoryManager factoryManager,
                                     final Instance<DefinitionSetService> definitionSetServiceInstances,
                                     final BackendRegistryFactory registryFactory,
                                     final @Named("ioStrategy") IOService ioService,
                                     final SessionInfo sessionInfo,
                                     final Event<ResourceOpenedEvent> resourceOpenedEvent,
                                     final CommentedOptionFactory commentedOptionFactory,
                                     final KieModuleService moduleService) {
        this.ioService = ioService;
        this.sessionInfo = sessionInfo;
        this.resourceOpenedEvent = resourceOpenedEvent;
        this.commentedOptionFactory = commentedOptionFactory;
        this.controller = buildController(definitionManager,
                                          factoryManager,
                                          definitionSetServiceInstances,
                                          registryFactory,
                                          ioService,
                                          moduleService);
    }

    @PostConstruct
    public void init() {
        // Initialize caches.
        controller.initialize();
    }

    @Override
    public ProjectDiagram getDiagramByPath(final Path path) {
        return controller.getDiagramByPath(path);
    }

    @Override
    public boolean accepts(final Path path) {
        return controller.accepts(path);
    }

    public Path create(final Path path,
                       final String name,
                       final String defSetId,
                       final String projName,
                       final Package projPkg) {
        return controller.create(path,
                                 name,
                                 defSetId,
                                 projName,
                                 projPkg);
    }

    @Override
    public Path create(final Path path,
                       final String name,
                       final String defSetId) {
        return controller.create(path,
                                 name,
                                 defSetId);
    }

    @Override
    protected ProjectDiagram constructContent(final Path path,
                                              final Overview overview) {
        ProjectDiagram diagram = getDiagramByPath(path);
        if (null != diagram) {
            resourceOpenedEvent.fire(new ResourceOpenedEvent(path,
                                                             sessionInfo));
            return diagram;
        }
        LOG.error("Failed to construct diagram content for path [" + path + "].");
        return null;
    }

    @Override
    public Path save(final Path path,
                     final ProjectDiagram content,
                     final Metadata metadata,
                     final String comment) {
        LOG.debug("Saving diagram with UUID [" + content.getName() + "] into path [" + path + "].");
        return controller.save(path,
                               content,
                               metadataService.setUpAttributes(path,
                                                               metadata),
                               commentedOptionFactory.makeCommentedOption(comment));
    }

    @Override
    public ProjectMetadata saveOrUpdate(final ProjectDiagram diagram) {
        return controller.saveOrUpdate(diagram);
    }

    @Override
    public boolean delete(final ProjectDiagram diagram) {
        return controller.delete(diagram);
    }

    @Override
    public void delete(final Path path,
                       final String comment) {
        controller.delete(path,
                          comment);
    }

    @Override
    public String getRawContent(ProjectDiagram diagram) {
        return controller.getRawContent(diagram);
    }

    protected ProjectDiagramServiceController buildController(final DefinitionManager definitionManager,
                                                              final FactoryManager factoryManager,
                                                              final Instance<DefinitionSetService> definitionSetServiceInstances,
                                                              final BackendRegistryFactory registryFactory,
                                                              final IOService ioService,
                                                              final KieModuleService moduleService) {
        return new ProjectDiagramServiceController(definitionManager,
                                                   factoryManager,
                                                   definitionSetServiceInstances,
                                                   ioService,
                                                   registryFactory,
                                                   moduleService);
    }
}
