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

package org.kie.workbench.common.services.refactoring.backend.server;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.uberfire.java.nio.file.Path;
import org.uberfire.workbench.type.ResourceTypeDefinition;

public abstract class MultipleRepositoryBaseIndexingTest<T extends ResourceTypeDefinition> extends IndexingTest<T> {

    private int seed = new Random( 10L ).nextInt();

    protected boolean created = false;
    private Map<String, Path> basePaths = new HashMap<String, Path>();

    @Before
    public void setup() throws IOException {
        if ( !created ) {
            final String path = createTempDirectory().getAbsolutePath();
            System.setProperty("org.uberfire.nio.git.dir",
                               path);
            System.setProperty("org.uberfire.nio.git.daemon.enabled",
                               "false");
            System.setProperty("org.uberfire.nio.git.ssh.enabled",
                               "false");
            logger.debug( ".niogit: " + path );

            for ( String repositoryName : getRepositoryNames() ) {

                final URI newRepo = URI.create( "git://" + repositoryName );

                try {
                    ioService().newFileSystem( newRepo,
                                               new HashMap<String, Object>() );

                    //Don't ask, but we need to write a single file first in order for indexing to work
                    final Path basePath = getDirectoryPath( repositoryName ).resolveSibling( "someNewOtherPath" );
                    ioService().write( basePath.resolve( "dummy" ),
                                       "<none>" );
                    basePaths.put( repositoryName,
                                   basePath );

                } catch ( final Exception e ) {
                    e.fillInStackTrace();
                    logger.warn( "Test setup failed: " + e.getMessage(), e );
                } finally {
                    created = true;
                }
            }

        }
    }

    @After
    public void dispose() {
        super.dispose();
        System.clearProperty("org.uberfire.nio.git.ssh.enabled");
        System.clearProperty("org.uberfire.nio.git.daemon.enabled");
    }

    protected abstract String[] getRepositoryNames();

    protected Path getBasePath( final String repositoryName ) {
        return basePaths.get( repositoryName );
    }

    protected Path getDirectoryPath( final String repositoryName ) {
        final Path dir = ioService().get( URI.create( "git://" + repositoryName + "/_someDir" + seed ) );
        ioService().deleteIfExists( dir );
        return dir;
    }

}
