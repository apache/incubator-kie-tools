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

package org.uberfire.ext.wires.backend.server.impl;

import java.util.concurrent.ExecutorService;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.service.AuthenticationService;
import org.uberfire.backend.server.IOWatchServiceNonDotImpl;
import org.uberfire.commons.concurrent.Unmanaged;
import org.uberfire.io.IOService;
import org.uberfire.io.impl.IOServiceDotFileImpl;

@ApplicationScoped
public class ApplicationScopedProducer {

    IOWatchServiceNonDotImpl watchService;

    private AuthenticationService authenticationService;

    private IOService ioService;

    private ExecutorService executorService;

    public ApplicationScopedProducer() {
    }

    @Inject
    public ApplicationScopedProducer(IOWatchServiceNonDotImpl watchService,
                                     AuthenticationService authenticationService,
                                     @Unmanaged ExecutorService executorService) {
        this.watchService = watchService;
        this.authenticationService = authenticationService;
        this.executorService = executorService;
    }

    @PostConstruct
    public void setup() {
        ioService = new IOServiceDotFileImpl(watchService);
    }

    @Produces
    @Named("ioStrategy")
    public IOService ioService() {
        return ioService;
    }

    @Produces
    @RequestScoped
    public User getIdentity() {
        return authenticationService.getUser();
    }
}
