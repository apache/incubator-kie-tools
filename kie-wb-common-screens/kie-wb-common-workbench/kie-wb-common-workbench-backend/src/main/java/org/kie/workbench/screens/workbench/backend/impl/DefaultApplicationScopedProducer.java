/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.screens.workbench.backend.impl;

import java.util.concurrent.ExecutorService;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.metadata.attribute.OtherMetaView;
import org.guvnor.messageconsole.backend.DefaultIndexEngineObserver;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.jboss.errai.security.shared.service.AuthenticationService;
import org.kie.workbench.screens.workbench.backend.ApplicationScopedProducer;
import org.uberfire.backend.server.IOWatchServiceNonDotImpl;
import org.uberfire.commons.cluster.ClusterServiceFactory;
import org.uberfire.commons.concurrent.Unmanaged;
import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.commons.services.cdi.StartupType;
import org.uberfire.ext.metadata.backend.lucene.LuceneConfig;
import org.uberfire.ext.metadata.io.IOSearchServiceImpl;
import org.uberfire.ext.metadata.io.IOServiceIndexedImpl;
import org.uberfire.ext.metadata.search.IOSearchService;
import org.uberfire.io.IOService;
import org.uberfire.io.attribute.DublinCoreView;
import org.uberfire.io.impl.cluster.IOServiceClusterImpl;
import org.uberfire.java.nio.base.version.VersionAttributeView;

/**
 * This class contains all default ApplicationScoped producers
 * required by the application, and can be replaced by using CDI alternatives.
 */
@Startup(value = StartupType.BOOTSTRAP, priority = -1)
@ApplicationScoped
public class DefaultApplicationScopedProducer implements ApplicationScopedProducer {

    private IOService ioService;
    private IOSearchService ioSearchService;
    private LuceneConfig config;
    private ClusterServiceFactory clusterServiceFactory;
    private IOWatchServiceNonDotImpl watchService;
    private AuthenticationService authenticationService;
    private DefaultIndexEngineObserver defaultIndexEngineObserver;
    private ExecutorService executorService;

    public DefaultApplicationScopedProducer() {
        if (System.getProperty("org.uberfire.watcher.autostart") == null) {
            System.setProperty("org.uberfire.watcher.autostart",
                               "false");
        }

        if (System.getProperty("org.kie.deployment.desc.location") == null) {
            System.setProperty("org.kie.deployment.desc.location",
                               "classpath:META-INF/kie-wb-deployment-descriptor.xml");
        }
    }

    @Inject
    public DefaultApplicationScopedProducer(@Named("luceneConfig") LuceneConfig config,
                                            @Named("clusterServiceFactory") ClusterServiceFactory clusterServiceFactory,
                                            IOWatchServiceNonDotImpl watchService,
                                            AuthenticationService authenticationService,
                                            DefaultIndexEngineObserver defaultIndexEngineObserver,
                                            @Unmanaged ExecutorService executorService) {
        this();
        this.config = config;
        this.clusterServiceFactory = clusterServiceFactory;
        this.watchService = watchService;
        this.authenticationService = authenticationService;
        this.defaultIndexEngineObserver = defaultIndexEngineObserver;
        this.executorService = executorService;
    }

    @PostConstruct
    public void setup() {
        final IOService service = new IOServiceIndexedImpl(watchService,
                                                           config.getIndexEngine(),
                                                           defaultIndexEngineObserver,
                                                           executorService,
                                                           DublinCoreView.class,
                                                           VersionAttributeView.class,
                                                           OtherMetaView.class);

        if (clusterServiceFactory == null) {
            ioService = service;
        } else {
            ioService = new IOServiceClusterImpl(service,
                                                 clusterServiceFactory,
                                                 false,
                                                 executorService);
        }

        this.ioSearchService = new IOSearchServiceImpl(config.getSearchIndex(),
                                                       ioService);
    }

    @Produces
    @Named("ioStrategy")
    public IOService ioService() {
        return ioService;
    }

    @Produces
    @Named("ioSearchStrategy")
    public IOSearchService ioSearchService() {
        return ioSearchService;
    }

    @Produces
    @RequestScoped
    public User getIdentity() {
        try {
            return authenticationService.getUser();
        } catch (final IllegalStateException ex) {
            return new UserImpl("system");
        }
    }
}
