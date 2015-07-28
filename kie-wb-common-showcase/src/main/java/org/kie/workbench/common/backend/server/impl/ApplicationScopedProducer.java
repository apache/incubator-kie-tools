/*
 * Copyright 2014 JBoss Inc
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

package org.kie.workbench.common.backend.server.impl;

import java.net.URI;
import java.util.HashMap;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.metadata.attribute.OtherMetaView;
import org.guvnor.messageconsole.backend.DefaultIndexEngineObserver;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.service.AuthenticationService;
import org.uberfire.backend.server.IOWatchServiceAllImpl;
import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.commons.services.cdi.StartupType;
import org.uberfire.ext.metadata.backend.lucene.LuceneConfig;
import org.uberfire.ext.metadata.io.IOSearchIndex;
import org.uberfire.ext.metadata.io.IOServiceIndexedImpl;
import org.uberfire.io.IOSearchService;
import org.uberfire.io.IOService;
import org.uberfire.io.attribute.DublinCoreView;
import org.uberfire.io.impl.IOServiceNio2WrapperImpl;
import org.uberfire.java.nio.base.version.VersionAttributeView;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.security.impl.authz.RuntimeAuthorizationManager;

@Startup( StartupType.BOOTSTRAP )
@ApplicationScoped
public class ApplicationScopedProducer {

    @Inject
    @Named( "luceneConfig" )
    private LuceneConfig config;

    private IOService ioService;
    private IOSearchService ioSearchService;
    private FileSystem systemFS;

    @Inject
    private AuthenticationService authenticationService;

    @Inject
    private IOWatchServiceAllImpl watchService;

    @Inject
    private DefaultIndexEngineObserver defaultIndexEngineObserver;

    @Inject
    @Named( "configIO" )
    private IOService configIO;

    @PostConstruct
    public void setup() {
        ioService = new IOServiceIndexedImpl( watchService,
                config.getIndexEngine(),
                defaultIndexEngineObserver,
                DublinCoreView.class,
                VersionAttributeView.class,
                OtherMetaView.class );

        ioSearchService = new IOSearchIndex( config.getSearchIndex(), ioService );
        final URI system = URI.create( "git://system" );
        try {
            systemFS = configIO.newFileSystem( system,
                    new HashMap<String, Object>() {{
                        put( "init", Boolean.TRUE );
                        put( "internal", Boolean.TRUE );
                    }} );
        } catch ( FileSystemAlreadyExistsException f ) {
            systemFS = configIO.getFileSystem( system );
        }
    }

    @Produces
    @Named( "ioStrategy" )
    public IOService ioService() {
        return ioService;
    }

    @Produces
    @Named( "ioSearchStrategy" )
    public IOSearchService ioSearchService() {
        return ioSearchService;
    }

    @Produces
    @Named( "systemFS" )
    public FileSystem systemFS() {
        return systemFS;
    }

    @Produces
    @RequestScoped
    public User getIdentity() {
        return authenticationService.getUser();
    }

    @Produces
    public AuthorizationManager getAuthManager() {
        return new RuntimeAuthorizationManager();
    }
}
