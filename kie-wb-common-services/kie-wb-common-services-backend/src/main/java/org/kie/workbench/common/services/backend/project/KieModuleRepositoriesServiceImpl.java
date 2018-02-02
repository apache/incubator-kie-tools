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
import org.guvnor.common.services.project.backend.server.AbstractModuleRepositoriesServiceImpl;
import org.guvnor.common.services.project.backend.server.ModuleRepositoriesContentHandler;
import org.guvnor.common.services.project.service.ModuleRepositoryResolver;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

/**
 * CDI implementation for KIE Workbenches
 */
@Service
@ApplicationScoped
public class KieModuleRepositoriesServiceImpl
        extends AbstractModuleRepositoriesServiceImpl<KieModule> {

    protected KieResourceResolver resourceResolver;

    public KieModuleRepositoriesServiceImpl() {
        //WELD proxy
    }

    @Inject
    public KieModuleRepositoriesServiceImpl(final @Named("ioStrategy") IOService ioService,
                                            final ModuleRepositoryResolver repositoryResolver,
                                            final KieResourceResolver resourceResolver,
                                            final ModuleRepositoriesContentHandler contentHandler,
                                            final CommentedOptionFactory commentedOptionFactory) {
        super(ioService,
              repositoryResolver,
              contentHandler,
              commentedOptionFactory);
        this.resourceResolver = resourceResolver;
    }

    protected KieModule getModule(final Path path) {
        //We need to create the project.repositories file first otherwise Module resolution fails
        final org.uberfire.java.nio.file.Path nioPath = Paths.convert(path);
        if (!ioService.exists(nioPath)) {
            ioService.createFile(nioPath);
        }
        return resourceResolver.resolveModule(path);
    }
}
