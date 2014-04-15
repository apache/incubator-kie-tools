/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.backend.server.impl;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.backend.server.IOWatchServiceNonDotImpl;
import org.uberfire.backend.server.repositories.RepositoryServiceImpl;
import org.uberfire.backend.server.security.RepositoryAuthorizationManager;
import org.uberfire.commons.cluster.ClusterServiceFactory;
import org.uberfire.io.IOService;
import org.uberfire.io.impl.IOServiceDotFileImpl;
import org.uberfire.io.impl.cluster.IOServiceClusterImpl;

@ApplicationScoped
public class ApplicationScopedProducer {

    @Inject
    private IOWatchServiceNonDotImpl watchService;

    @Inject
    private RepositoryServiceImpl repositoryService;

    @Inject
    @Named("debug")
    private ResourceUpdateDebugger debug;

    @Inject
    @Named("clusterServiceFactory")
    private ClusterServiceFactory clusterServiceFactory;

    private IOService ioService;

    @PostConstruct
    public void setup() {
        if ( clusterServiceFactory == null ) {
            ioService = new IOServiceDotFileImpl( watchService );
        } else {
            ioService = new IOServiceClusterImpl( new IOServiceDotFileImpl( watchService ), clusterServiceFactory );
        }
        ioService.setAuthorizationManager( new RepositoryAuthorizationManager( repositoryService ) );
    }

    @PreDestroy
    public void onShutdown() {
        ioService.dispose();
    }

    @Produces
    @Named("ioStrategy")
    public IOService ioService() {
        return ioService;
    }
}
