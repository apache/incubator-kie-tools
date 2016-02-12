/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.backend.project;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.backend.server.AbstractProjectRepositoriesServiceImpl;
import org.guvnor.common.services.project.backend.server.ProjectRepositoriesContentHandler;
import org.guvnor.common.services.project.service.ProjectRepositoryResolver;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

/**
 * CDI implementation for KIE Workbenches
 */
@Service
@ApplicationScoped
public class KieProjectRepositoriesServiceImpl extends AbstractProjectRepositoriesServiceImpl<KieProject> {

    protected KieResourceResolver resourceResolver;

    public KieProjectRepositoriesServiceImpl() {
        //WELD proxy
    }

    @Inject
    public KieProjectRepositoriesServiceImpl( final @Named("ioStrategy") IOService ioService,
                                              final ProjectRepositoryResolver repositoryResolver,
                                              final KieResourceResolver resourceResolver,
                                              final ProjectRepositoriesContentHandler contentHandler,
                                              final CommentedOptionFactory commentedOptionFactory ) {
        super( ioService,
               repositoryResolver,
               contentHandler,
               commentedOptionFactory );
        this.resourceResolver = resourceResolver;
    }

    @Override
    protected KieProject getProject( final Path path ) {
        //We need to create the project.repositories file first otherwise Project resolution fails
        final org.uberfire.java.nio.file.Path nioPath = Paths.convert( path );
        if ( !ioService.exists( nioPath ) ) {
            ioService.createFile( nioPath );
        }
        return resourceResolver.resolveProject( path );
    }

}
