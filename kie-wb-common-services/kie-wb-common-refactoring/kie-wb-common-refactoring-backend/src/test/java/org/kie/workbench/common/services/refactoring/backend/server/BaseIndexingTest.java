/*
 * Copyright 2014 JBoss, by Red Hat, Inc
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
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.uberfire.java.nio.file.Path;
import org.uberfire.workbench.type.ResourceTypeDefinition;

public abstract class BaseIndexingTest<T extends ResourceTypeDefinition> extends IndexingTest<T> {

    private int seed = new Random( 10L ).nextInt();

    protected boolean created = false;
    protected Path basePath;

    @Before
    public void setup() throws IOException {
        if ( !created ) {
            final String repositoryName = getRepositoryName();
            final String path = createTempDirectory().getAbsolutePath();
            System.setProperty( "org.uberfire.nio.git.dir",
                                path );
            System.setProperty( "org.uberfire.nio.git.daemon.enabled",
                                "false" );
            System.setProperty( "org.uberfire.nio.git.ssh.enabled",
                                "false" );
            System.setProperty( "org.uberfire.sys.repo.monitor.disabled",
                                "true" );
            System.out.println( ".niogit: " + path );

            final URI newRepo = URI.create( "git://" + repositoryName );

            try {
                ioService().newFileSystem( newRepo,
                                           new HashMap<String, Object>() );

                //Don't ask, but we need to write a single file first in order for indexing to work
                basePath = getDirectoryPath().resolveSibling( "someNewOtherPath" );
                ioService().write( basePath.resolve( "dummy" ),
                                   "<none>" );

            } catch ( final Exception ex ) {
                ex.fillInStackTrace();
                System.out.println( ex.getMessage() );
            } finally {
                created = true;
            }
        }
    }

    @After
    public void dispose() {
        super.dispose();
        created = false;
    }

    protected abstract String getRepositoryName();

    protected Path getDirectoryPath() {
        final String repositoryName = getRepositoryName();
        final Path dir = ioService().get( URI.create( "git://" + repositoryName + "/_someDir" + seed ) );
        ioService().deleteIfExists( dir );
        return dir;
    }

}
