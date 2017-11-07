/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import java.util.concurrent.ExecutorService;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.security.shared.service.AuthenticationService;
import org.uberfire.backend.server.security.IOSecurityAuth;
import org.uberfire.commons.concurrent.Unmanaged;
import org.uberfire.commons.lifecycle.PriorityDisposableRegistry;
import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.commons.services.cdi.StartupType;
import org.uberfire.io.IOService;
import org.uberfire.io.impl.IOServiceNio2WrapperImpl;
import org.uberfire.java.nio.file.FileSystem;

@ApplicationScoped
@Startup(StartupType.BOOTSTRAP)
public class ConfigIOServiceProducer {

    private static ConfigIOServiceProducer instance;

    private ExecutorService executorService;

    private Instance<AuthenticationService> applicationProvidedConfigIOAuthService;

    private IOService configIOService;
    private FileSystem configFileSystem;

    public static ConfigIOServiceProducer getInstance() {
        if (instance == null) {
            throw new IllegalStateException(ConfigIOServiceProducer.class.getName() + " not initialized on startup");
        }
        return instance;
    }

    public ConfigIOServiceProducer() {
    }

    @Inject
    public ConfigIOServiceProducer(@Unmanaged ExecutorService executorService,
                                   @IOSecurityAuth Instance<AuthenticationService> applicationProvidedConfigIOAuthService) {
        this.executorService = executorService;
        this.applicationProvidedConfigIOAuthService = applicationProvidedConfigIOAuthService;
    }

    @PostConstruct
    public void setup() {
        instance = this;
        configIOService = new IOServiceNio2WrapperImpl("config");
        configFileSystem = (FileSystem) PriorityDisposableRegistry.get("systemFS");
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
        if (configFileSystem == null) {
            configFileSystem = (FileSystem) PriorityDisposableRegistry.get("systemFS");
        }
        return configFileSystem;
    }
}
