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

package org.drools.workbench.jcr2vfsmigration.vfs;

import java.net.URI;
import java.util.HashMap;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.uberfire.io.FileSystemType;
import org.uberfire.io.IOService;
import org.uberfire.io.impl.IOServiceDotFileImpl;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.server.repositories.SystemRepository;

import static org.drools.workbench.jcr2vfsmigration.vfs.IOServiceFactory.Migration.*;

@ApplicationScoped
public class IOServiceFactory {

    private final IOService ioService = new IOServiceDotFileImpl();
    private FileSystem fs;

    public static String DEFAULT_MIGRATION_FILE_SYSTEM = "guvnor-jcr2vfs-migration";

    public static enum Migration implements FileSystemType {

        MIGRATION_INSTANCE;

        public String toString() {
            return "MIGRATION";
        }
    }

    @PostConstruct
    public void onStartup() {
        URI uri = URI.create( "git://" + DEFAULT_MIGRATION_FILE_SYSTEM );
        this.fs = ioService.newFileSystem( uri, new HashMap<String, Object>(), MIGRATION_INSTANCE );
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

    @Produces
    @Named("migrationFS")
    public FileSystem migrationFS() {
        return fs;
    }

    @Produces
    @Named("system")
    public Repository systemRepository() {
        return SystemRepository.SYSTEM_REPO;
    }

}
