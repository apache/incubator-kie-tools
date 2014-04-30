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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;
import org.uberfire.metadata.backend.lucene.LuceneConfig;
import org.uberfire.metadata.backend.lucene.LuceneConfigBuilder;
import org.uberfire.metadata.engine.Indexer;
import org.uberfire.metadata.io.IOServiceIndexedImpl;
import org.uberfire.metadata.model.KObject;
import org.uberfire.workbench.type.ResourceTypeDefinition;

import static org.junit.Assert.*;

public abstract class BaseIndexingTest<T extends ResourceTypeDefinition> {

    private IOService ioService = null;
    private static LuceneConfig config;
    private static final List<File> tempFiles = new ArrayList<File>();
    private int seed = new Random( 10L ).nextInt();
    private boolean created = false;

    @Before
    public void setup() throws IOException {
        if ( !created ) {
            final String repositoryName = getRepositoryName();
            final String path = createTempDirectory().getAbsolutePath();
            System.setProperty( "org.uberfire.nio.git.dir",
                                path );
            System.out.println( ".niogit: " + path );

            final URI newRepo = URI.create( "git://" + repositoryName );

            try {
                ioService().newFileSystem( newRepo,
                                           new HashMap<String, Object>() );
            } catch ( final Exception ex ) {
            } finally {
                created = true;
            }
        }
    }

    @AfterClass
    @BeforeClass
    public static void cleanup() {
        for ( final File tempFile : tempFiles ) {
            FileUtils.deleteQuietly( tempFile );
        }
    }

    protected static LuceneConfig getConfig() {
        return config;
    }

    protected abstract String getRepositoryName();

    protected abstract TestIndexer<T> getIndexer();

    protected abstract Map<String, Analyzer> getAnalyzers();

    protected abstract T getResourceTypeDefinition();

    protected Path getDirectoryPath() {
        final String repositoryName = getRepositoryName();
        final Path dir = ioService().get( URI.create( "git://" + repositoryName + "/_someDir" + seed ) );
        ioService().deleteIfExists( dir );
        return dir;
    }

    protected void loadProperties( final String fileName,
                                   final Path basePath ) throws IOException {
        final Path path = basePath.resolve( fileName );
        final Properties properties = new Properties();
        properties.load( this.getClass().getResourceAsStream( fileName ) );
        ioService().write( path,
                           propertiesToString( properties ) );
    }

    protected String loadText( final String fileName ) throws IOException {
        final BufferedReader br = new BufferedReader( new InputStreamReader( this.getClass().getResourceAsStream( fileName ) ) );
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while ( line != null ) {
                sb.append( line );
                sb.append( System.getProperty( "line.separator" ) );
                line = br.readLine();
            }
            return sb.toString();
        } finally {
            br.close();
        }
    }

    protected String propertiesToString( final Properties properties ) {
        final StringBuilder sb = new StringBuilder();
        for ( String name : properties.stringPropertyNames() ) {
            sb.append( name ).append( "=" ).append( properties.getProperty( name ) ).append( "\n" );
        }
        return sb.toString();
    }

    protected IOService ioService() {
        if ( ioService == null ) {
            final TestIndexer indexer = getIndexer();
            final Map<String, Analyzer> analyzers = getAnalyzers();
            config = new LuceneConfigBuilder()
                    .withInMemoryMetaModelStore()
                    .usingIndexers( new HashSet<Indexer>() {{
                        add( indexer );
                    }} )
                    .usingAnalyzers( analyzers )
                    .useDirectoryBasedIndex()
                    .useInMemoryDirectory()
                    .build();

            //Mock CDI injection and setup
            ioService = new IOServiceIndexedImpl( config.getIndexEngine(),
                                                  config.getIndexers() );
            indexer.setIOService( ioService );
            indexer.setResourceTypeDefinition( getResourceTypeDefinition() );
        }
        return ioService;
    }

    protected void assertContains( final List<KObject> results,
                                   final Path path ) {
        for ( KObject kObject : results ) {
            final String key = kObject.getKey();
            final String fileName = path.getFileName().toString();
            if ( key.endsWith( fileName ) ) {
                return;
            }
        }
        fail( "Results do not contain expected Path '" + path.toUri().toString() );
    }

    private static File createTempDirectory() throws IOException {
        final File temp = File.createTempFile( "temp", Long.toString( System.nanoTime() ) );
        if ( !( temp.delete() ) ) {
            throw new IOException( "Could not delete temp file: " + temp.getAbsolutePath() );
        }
        if ( !( temp.mkdir() ) ) {
            throw new IOException( "Could not create temp directory: " + temp.getAbsolutePath() );
        }
        tempFiles.add( temp );
        return temp;
    }

}
