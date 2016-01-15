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

import org.guvnor.common.services.project.backend.server.RepositoryResolver;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.uberfire.io.IOService;

/**
 * Concrete implementation for KIE
 */
@ApplicationScoped
public class KieRepositoryResolverImpl extends RepositoryResolver<KieProject> implements KieRepositoryResolver {

    public KieRepositoryResolverImpl() {
        //WELD proxy
    }

    @Inject
    public KieRepositoryResolverImpl( final @Named("ioStrategy") IOService ioService ) {
        super( ioService );
    }

}
