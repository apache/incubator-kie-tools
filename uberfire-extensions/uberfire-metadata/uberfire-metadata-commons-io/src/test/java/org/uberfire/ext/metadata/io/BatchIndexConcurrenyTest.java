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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.ext.metadata.backend.lucene.LuceneConfigBuilder;
import org.uberfire.ext.metadata.engine.Observer;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;

public class BatchIndexConcurrenyTest extends BaseIndexTest {

    private static final String REPO_NAME  = "temp-repo-batch-index-test";

    private static class ConcurrencyObserver implements Observer {

        private List<String> informationMessages = new ArrayList<String>();
        private List<String> warningMessages = new ArrayList<String>();
        private List<String> errorMessages = new ArrayList<String>();

        @Override
        public void information( final String message ) {
            informationMessages.add( message );
        }

        @Override
        public void warning( final String message ) {
            warningMessages.add( message );
        }

        @Override
        public void error( final String message ) {
            errorMessages.add( message );
        }

        public List<String> getInformationMessages() {
            return informationMessages;
        }

        public List<String> getWarningMessages() {
            return warningMessages;
        }

        public List<String> getErrorMessages() {
            return errorMessages;
        }

    }

    private ConcurrencyObserver observer = new ConcurrencyObserver();

    
    @Override
    @SuppressWarnings("unchecked")
    protected IOService ioService() {
        if ( ioService == null ) {
            config = new LuceneConfigBuilder()
                    .withInMemoryMetaModelStore()
                    .useDirectoryBasedIndex()
                    .useInMemoryDirectory()
                    .build();
            ioService = new IOServiceIndexedImpl( config.getIndexEngine(),
                                                  observer );
        }
        return ioService;
    }

    @Override
    protected String[] getRepositoryNames() {
        return new String[]{ REPO_NAME  };
    }

    @Before
    public void setup() throws IOException {
        super.setup();
        ioService.createDirectory( getBasePath( REPO_NAME ) );
    }
    
    @After
    public void tearDown() throws IOException {
        ioService().deleteIfExists( getBasePath( REPO_NAME ) );
    }
    
    @Test
    //See https://bugzilla.redhat.com/show_bug.cgi?id=1288132
    public void testSingleBatchIndexExecution() throws IOException, InterruptedException {
        //Make multiple requests for the FileSystem. We should only have one batch index operation
        final FileSystem fs1 = ioService().getFileSystem( URI.create( "git://temp-repo-batch-index-test/file1" ) );
        assertNotNull( fs1 );

        final FileSystem fs2 = ioService().getFileSystem( URI.create( "git://temp-repo-batch-index-test/file1" ) );
        assertNotNull( fs2 );

        final FileSystem fs3 = ioService().getFileSystem( URI.create( "git://temp-repo-batch-index-test/file1" ) );
        assertNotNull( fs3 );

        Thread.sleep( 5000 ); //wait for events to be consumed from jgit -> (notify changes -> watcher -> index) -> lucene index

        assertEvents();
    }
    
    @Test
    //See https://bugzilla.redhat.com/show_bug.cgi?id=1288132
    public void testSingleConcurrentBatchIndexExecution() throws IOException, InterruptedException {
        //Make multiple requests for the FileSystem. We should only have one batch index operation
        final CountDownLatch startSignal= new CountDownLatch(1);
        for ( int i = 0; i < 3; i++ ) {
            Runnable r = new Runnable() {

                @Override
                public void run() {
                    try {
                        startSignal.await();
                        ioService().getFileSystem( URI.create( "git://temp-repo-batch-index-test/file1" ) );
                    } 
                    catch ( InterruptedException e ) {
                        fail(e.getMessage());
                    }
                }
            };
            new Thread( r ).start();
        }
        startSignal.countDown();

        Thread.sleep( 5000 ); //wait for events to be consumed from jgit -> (notify changes -> watcher -> index) -> lucene index

        assertEvents();
    }

    private void assertEvents() {
        System.out.println(observer.getInformationMessages());
        
        assertEquals( 2, observer.getInformationMessages().size() );
        assertEquals( 0, observer.getWarningMessages().size() );
        assertEquals( 0, observer.getErrorMessages().size() );

        assertContains( "Starting indexing of git://master@temp-repo-batch-index-test/ ...", observer.getInformationMessages() );
        assertContains( "Completed indexing of git://master@temp-repo-batch-index-test/", observer.getInformationMessages() );
    }

    private void assertContains( final String expected,
                                 final List<String> actual ) {
        for ( String msg : actual ) {
            if ( msg.equals( expected ) ) {
                return;
            }
        }
        final StringBuilder sb = new StringBuilder();
        for ( String msg : actual ) {
            sb.append( "'" ).append( msg ).append( "'\n" );
        }
        fail( "Expected '" + expected + "' was not found in " + sb.toString() );
    }
    
}