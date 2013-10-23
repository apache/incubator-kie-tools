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

package org.uberfire.backend;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.inject.Singleton;

import org.uberfire.backend.repositories.Repository;
import org.uberfire.io.IOService;
import org.uberfire.io.impl.IOServiceDotFileImpl;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.rpc.impl.SessionInfoImpl;

import static org.uberfire.backend.server.repositories.SystemRepository.*;

@Singleton
@Alternative
public class TestAppSetup {

    private final IOService ioService = new IOServiceDotFileImpl();
    private SessionInfo sessionInfo = new SessionInfoImpl();

    @Produces
    @Named("ioStrategy")
    public IOService ioService() {
        return ioService;
    }

    @Produces
    @Named("system")
    public Repository systemRepository() {
        return SYSTEM_REPO;
    }

    @Produces
    public SessionInfo getSessionInfo() {
        return this.sessionInfo;
    }

}
