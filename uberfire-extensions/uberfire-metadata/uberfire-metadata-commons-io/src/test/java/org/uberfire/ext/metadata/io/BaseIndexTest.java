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

package org.uberfire.ext.metadata.io;

import static org.uberfire.ext.metadata.backend.lucene.util.KObjectUtil.toKObject;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.uberfire.ext.metadata.backend.lucene.LuceneConfig;
import org.uberfire.ext.metadata.backend.lucene.LuceneConfigBuilder;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.io.IOService;
import org.uberfire.io.attribute.DublinCoreView;
import org.uberfire.java.nio.base.version.VersionAttributeView;
import org.uberfire.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.java.nio.file.Path;

public abstract class BaseIndexTest {

    private int seed = new Random( 10L ).nextInt();

    protected boolean created = false;
    protected static final Map<String, Path> basePaths = new HashMap<String, Path>();

    protected LuceneConfig config;
    protected IOService ioService = null;

    protected static final List<File> tempFiles = new ArrayList<File>();

    @AfterClass
    @BeforeClass
    public static void cleanup() {
        for ( final File tempFile : tempFiles ) {
            FileUtils.deleteQuietly( tempFile );
        }
    }

    protected IOService ioService() {
        if ( ioService == null ) {
            config = new LuceneConfigBuilder()
                    .withInMemoryMetaModelStore()
                    .useDirectoryBasedIndex()
                    .useInMemoryDirectory()
                    .build();

            ioService = new IOServiceIndexedImpl( config.getIndexEngine(),
                                                  DublinCoreView.class,
                                                  VersionAttributeView.class );
        }
        return ioService;
    }

    protected static File createTempDirectory() throws IOException {
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

    @Before
    public void setup() throws IOException {
        IndexersFactory.clear();
        if ( !created ) {
            final String path = createTempDirectory().getAbsolutePath();
            System.setProperty( "org.uberfire.nio.git.dir",
                                path );
            System.out.println( ".niogit: " + path );

            for ( String repositoryName : getRepositoryNames() ) {

                final URI newRepo = URI.create( "git://" + repositoryName );

                try {
                    ioService().newFileSystem( newRepo,
                                               new HashMap<String, Object>() );

                    final Path basePath = getDirectoryPath( repositoryName ).resolveSibling( "root" );
                    basePaths.put( repositoryName,
                                   basePath );

                } 
                catch ( final FileSystemAlreadyExistsException ex ) {
                    // ignored
                } 
                finally {
                    created = true;
                }
            }
        }
    }
    
    protected abstract String[] getRepositoryNames();

    protected Path getBasePath( final String repositoryName ) {
        return basePaths.get( repositoryName );
    }

    protected void listHitPaths( final IndexSearcher searcher,
                                 final ScoreDoc[] hits ) throws IOException {
        for ( int i = 0; i < hits.length; i++ ) {
            final KObject ko = toKObject( searcher.doc( hits[ i ].doc ) );
            System.out.println( ko.getKey() );
        }
    }

    private Path getDirectoryPath( final String repositoryName ) {
        final Path dir = ioService().get( URI.create( "git://" + repositoryName + "/_someDir" + seed ) );
        ioService().deleteIfExists( dir );
        return dir;
    }

}
