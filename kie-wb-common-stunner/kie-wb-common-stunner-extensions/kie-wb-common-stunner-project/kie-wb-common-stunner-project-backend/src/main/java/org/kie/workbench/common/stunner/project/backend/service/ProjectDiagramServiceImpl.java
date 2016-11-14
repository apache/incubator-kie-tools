/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.project.backend.service;

import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.security.shared.api.identity.User;
import org.kie.workbench.common.services.backend.service.KieService;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.definition.service.DefinitionSetService;
import org.kie.workbench.common.stunner.core.factory.diagram.DiagramFactory;
import org.kie.workbench.common.stunner.core.registry.BackendRegistryFactory;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.kie.workbench.common.stunner.project.service.ProjectDiagramService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.events.ResourceOpenedEvent;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

@Service
@ApplicationScoped
public class ProjectDiagramServiceImpl extends KieService<ProjectDiagram>
        implements ProjectDiagramService {

    private static final Logger LOG =
            LoggerFactory.getLogger( ProjectDiagramServiceImpl.class.getName() );

    private final User identity;
    private final SessionInfo sessionInfo;
    private final Event<ResourceOpenedEvent> resourceOpenedEvent;
    private final KieProjectService projectService;
    private final CommentedOptionFactory commentedOptionFactory;
    private final ProjectDiagramServiceController controller;

    protected ProjectDiagramServiceImpl() {
        this( null, null, null, null, null, null, null, null, null, null );
    }

    @Inject
    public ProjectDiagramServiceImpl( DefinitionManager definitionManager,
                                      FactoryManager factoryManager,
                                      Instance<DefinitionSetService> definitionSetServiceInstances,
                                      BackendRegistryFactory registryFactory,
                                      @Named( "ioStrategy" ) IOService ioService,
                                      User identity,
                                      SessionInfo sessionInfo,
                                      Event<ResourceOpenedEvent> resourceOpenedEvent,
                                      KieProjectService projectService,
                                      CommentedOptionFactory commentedOptionFactory ) {
        this.ioService = ioService;
        this.identity = identity;
        this.sessionInfo = sessionInfo;
        this.resourceOpenedEvent = resourceOpenedEvent;
        this.projectService = projectService;
        this.commentedOptionFactory = commentedOptionFactory;
        this.controller =
                new ProjectDiagramServiceController( definitionManager,
                        factoryManager, definitionSetServiceInstances,
                        ioService, registryFactory );
    }

    @PostConstruct
    public void init() {
        // Initialize caches.
        controller.initialize();
    }

    @Override
    public ProjectDiagram getDiagramByPath( Path path ) {
        return controller.getDiagramByPath( path );
    }

    @Override
    public boolean accepts( Path path ) {
        return controller.accepts( path );
    }

    public Path create( Path path, String name, String defSetId, String projName, String projPkg ) {
        return controller.create( path, name, defSetId, projName, projPkg );
    }

    @Override
    public Path create( Path path, String name, String defSetId ) {
        return controller.create( path, name, defSetId );
    }

    @Override
    protected ProjectDiagram constructContent( Path path,
                                               Overview overview ) {
        ProjectDiagram diagram = getDiagramByPath( path );
        if ( null != diagram ) {
            resourceOpenedEvent.fire( new ResourceOpenedEvent( path, sessionInfo ) );
            return diagram;
        }
        LOG.error( "Failed to construct diagram content for path [" + path + "]." );
        return null;
    }

    @Override
    public Path save( Path path, ProjectDiagram content, Metadata metadata, String comment ) {
        LOG.warn( "Saving diagram with UUID [" + content.getName() + "] into path [" + path + "]." );
        return controller
                .save(
                        path,
                        content,
                        metadataService.setUpAttributes( path, metadata ),
                        commentedOptionFactory.makeCommentedOption( comment ) );
    }

    @Override
    public void saveOrUpdate( ProjectDiagram diagram ) {
        controller.saveOrUpdate( diagram );
    }

    @Override
    public boolean delete( ProjectDiagram diagram ) {
        return controller.delete( diagram );
    }

    @Override
    public void delete( Path path, String comment ) {
        controller.delete( path, comment );
    }

}
