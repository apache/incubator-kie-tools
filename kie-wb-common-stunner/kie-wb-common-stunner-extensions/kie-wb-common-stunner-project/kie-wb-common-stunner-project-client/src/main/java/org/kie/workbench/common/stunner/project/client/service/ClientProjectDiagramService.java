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

package org.kie.workbench.common.stunner.project.client.service;

import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.stunner.core.client.ShapeManager;
import org.kie.workbench.common.stunner.core.client.service.AbstractClientDiagramService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.service.DiagramLookupService;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.kie.workbench.common.stunner.project.service.ProjectDiagramService;
import org.uberfire.backend.vfs.Path;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * A wrapper util class for handling different diagram services for the current Guvnor Project from client side.
 */
@ApplicationScoped
public class ClientProjectDiagramService extends AbstractClientDiagramService<ProjectDiagram, ProjectDiagramService> {

    protected ClientProjectDiagramService() {
        this( null, null, null );
    }

    @Inject
    public ClientProjectDiagramService( final ShapeManager shapeManager,
                                        final Caller<ProjectDiagramService> diagramServiceCaller,
                                        final Caller<DiagramLookupService> diagramLookupServiceCaller ) {
        super( shapeManager, diagramServiceCaller, diagramLookupServiceCaller );
    }

    public void create( final Path path,
                        final String name,
                        final String defSetId,
                        final String projName,
                        final String projPkg,
                        final ServiceCallback<Path> callback ) {
        diagramServiceCaller.call( new RemoteCallback<Path>() {
            @Override
            public void callback( Path path ) {
                callback.onSuccess( path );
            }
        }, ( message, throwable ) -> {
            callback.onError( new ClientRuntimeError( throwable ) );
            return false;
        } ).create( path, name, defSetId, projName, projPkg );
    }

    public void saveOrUpdate( final Path path,
                              final ProjectDiagram diagram,
                              final Metadata metadata,
                              final String comment,
                              final ServiceCallback<ProjectDiagram> callback ) {
        diagramServiceCaller.call( v -> {
            updateClientMetadata( diagram );
            callback.onSuccess( diagram );
        }, ( message, throwable ) -> {
            callback.onError( new ClientRuntimeError( throwable ) );
            return false;
        } ).save( path, diagram, metadata, comment );
    }

}
