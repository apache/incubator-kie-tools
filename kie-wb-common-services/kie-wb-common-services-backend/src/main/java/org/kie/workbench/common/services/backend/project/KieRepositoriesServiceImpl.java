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
import org.guvnor.common.services.project.backend.server.AbstractProjectRepositoriesService;
import org.guvnor.common.services.project.backend.server.ProjectRepositoriesContentHandler;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.uberfire.io.IOService;

/**
 * Concrete implementation for KIE
 */
@Service
@ApplicationScoped
public class KieRepositoriesServiceImpl extends AbstractProjectRepositoriesService<KieProject> {

    public KieRepositoriesServiceImpl() {
        //WELD proxy
    }

    @Inject
    public KieRepositoriesServiceImpl( final @Named("ioStrategy") IOService ioService,
                                       final KieRepositoryResolver repositoryResolver,
                                       final ProjectRepositoriesContentHandler contentHandler,
                                       final CommentedOptionFactory commentedOptionFactory ) {
        super( ioService,
               repositoryResolver,
               contentHandler,
               commentedOptionFactory );
    }

}
