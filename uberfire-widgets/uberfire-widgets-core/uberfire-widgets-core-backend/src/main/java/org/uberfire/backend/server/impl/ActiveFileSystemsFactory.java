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

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.file.FileSystem;
import org.kie.commons.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.repositories.RepositoryService;
import org.uberfire.backend.vfs.ActiveFileSystems;
import org.uberfire.backend.vfs.FileSystemFactory;
import org.uberfire.backend.vfs.impl.ActiveFileSystemsImpl;

@ApplicationScoped
public class ActiveFileSystemsFactory {

    @Inject
    private RepositoryService repositoryService;

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    private final ActiveFileSystems fileSystems = new ActiveFileSystemsImpl();

    @PostConstruct
    public void onStartup() {
        //Add other repositories
        final Collection<Repository> repositories = repositoryService.getRepositories();
        for ( final Repository repository : repositories ) {
            if ( repository.isValid() ) {
                final Map<String, Object> env = new HashMap<String, Object>();
                for ( Map.Entry<String, Object> e : repository.getEnvironment().entrySet() ) {
                    env.put( e.getKey(),
                             e.getValue() );
                }

                addFileSystem( repository,
                               env );
            }
        }
    }

    private void addFileSystem( final Repository repository,
                                final Map<String, Object> env ) {
        FileSystem fs = null;
        final URI fsURI = repository.getUri();
        final String scheme = repository.getScheme();
        final String alias = repository.getAlias();

        try {
            fs = ioService.newFileSystem( fsURI,
                                          env );

        } catch ( FileSystemAlreadyExistsException ex ) {
            fs = ioService.getFileSystem( fsURI );
        }

        fileSystems.addFileSystem( FileSystemFactory.newFS( new HashMap<String, String>() {{
            put( scheme + "://" + alias,
                 alias );
        }}, fs.supportedFileAttributeViews() ) );
    }

    @Produces
    @Named("fs")
    public ActiveFileSystems fileSystems() {
        return fileSystems;
    }

}
