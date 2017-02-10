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

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CountDownLatch;

import org.jboss.byteman.contrib.bmunit.BMScript;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.java.nio.file.Path;

import static org.junit.Assert.*;

@RunWith(org.jboss.byteman.contrib.bmunit.BMUnitRunner.class)
@BMScript(value = "byteman/index.btm")
public class BatchIndexConcurrencyTest extends BaseIndexTest {

    @Override
    protected String[] getRepositoryNames() {
        return new String[]{ this.getClass().getSimpleName() };
    }

    @Test
    //See https://bugzilla.redhat.com/show_bug.cgi?id=1288132
    public void testSingleConcurrentBatchIndexExecution() throws IOException, InterruptedException {
        final Path path1 = getBasePath( this.getClass().getSimpleName() ).resolve( "xxx" );
        ioService().write( path1,
                           "xxx!" );

        setupCountDown( 3 );

        final URI fsURI = URI.create( "git://" + this.getClass().getSimpleName() + "/file1" );

        //Make multiple requests for the FileSystem. We should only have one batch index operation
        final CountDownLatch startSignal = new CountDownLatch( 1 );
        for ( int i = 0; i < 3; i++ ) {
            Runnable r = () -> {
                try {
                    startSignal.await();
                    ioService().getFileSystem( fsURI );
                } catch ( InterruptedException e ) {
                    fail( e.getMessage() );
                }
            };
            new Thread( r ).start();
        }
        startSignal.countDown();

        waitForCountDown( 5000 );

        assertEquals( 1, getStartBatchCount() );
    }

}