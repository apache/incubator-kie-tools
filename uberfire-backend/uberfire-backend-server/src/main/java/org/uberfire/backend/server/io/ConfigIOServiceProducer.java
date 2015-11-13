/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.backend.server.io;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.security.shared.service.AuthenticationService;
import org.uberfire.backend.server.security.IOSecurityAuth;
import org.uberfire.commons.cluster.ClusterServiceFactory;
import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.commons.services.cdi.StartupType;
import org.uberfire.io.IOService;
import org.uberfire.io.impl.IOServiceNio2WrapperImpl;
import org.uberfire.io.impl.cluster.IOServiceClusterImpl;
import org.uberfire.java.nio.file.FileSystem;

@ApplicationScoped
@Startup(StartupType.BOOTSTRAP)
public class ConfigIOServiceProducer {

    private static ConfigIOServiceProducer instance;

    @Inject
    @Named("clusterServiceFactory")
    private ClusterServiceFactory clusterServiceFactory;
    
    @Inject
    @Named("systemFS")
    private FileSystem fileSystem;

    @Inject
    @IOSecurityAuth
    private Instance<AuthenticationService> applicationProvidedConfigIOAuthService;

    private IOService configIOService;

    @PostConstruct
    public void setup() {
        instance = this;
        if ( clusterServiceFactory == null ) {
            configIOService = new IOServiceNio2WrapperImpl( "config" );
        } else {
            configIOService = new IOServiceClusterImpl( new IOServiceNio2WrapperImpl( "config" ), clusterServiceFactory, clusterServiceFactory.isAutoStart() );
        }
    }
    
    public void destroy() {
        instance = null;
    }

    @Produces
    @Named("configIO")
    public IOService configIOService() {
        return configIOService;
    }
    
    public FileSystem configFileSystem() {
        return fileSystem;
    }
    
    public static ConfigIOServiceProducer getInstance() {
        if (instance == null) {
            throw new IllegalStateException(ConfigIOServiceProducer.class.getName() + " not initialized on startup");
        }
        return instance;
    }

}
