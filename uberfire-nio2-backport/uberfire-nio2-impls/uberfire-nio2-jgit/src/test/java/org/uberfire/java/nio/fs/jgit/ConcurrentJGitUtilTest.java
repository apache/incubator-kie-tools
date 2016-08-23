/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.java.nio.fs.jgit;

import static org.junit.Assert.assertEquals;
import static org.uberfire.java.nio.fs.jgit.util.JGitUtil.commit;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.eclipse.jgit.api.Git;
import org.junit.Test;
import org.uberfire.java.nio.fs.jgit.util.JGitUtil;
import org.uberfire.java.nio.fs.jgit.util.JGitUtil.JGitPathInfo;

public class ConcurrentJGitUtilTest extends AbstractTestInfra {

    @Test
    public void testCommitAndResolve() throws IOException {

        // RHBPMS-4105
        File parentFolder = createTempDirectory();
        File gitFolder = new File( parentFolder, "mytest.git" );

        System.out.println( gitFolder.getAbsolutePath() );

        Git git = JGitUtil.newRepository( gitFolder, true );

        final Set<Integer> failureSet = new HashSet<Integer>();

        final long start = System.currentTimeMillis();

        commit( git, "master", "name", "name@example.com", "1st commit", null, new Date(), false, new HashMap<String, File>() {
            {
                put( "path/to/file1.txt", tempFile( "temp2222" ) );
            }
        } );
        commit( git, "master", "name", "name@example.com", "2nd commit", null, new Date(), false, new HashMap<String, File>() {
            {
                put( "path/to/file2.txt", tempFile( "temp2222" ) );
            }
        } );

        Runnable commiter = new Runnable() {
            @Override
            public void run() {
                System.out.println( "commiter start" );
                try {
                    for ( int i = 0; i < 200; i++ ) {
                        final int fileNum = i;
                        commit( git, "master", "name", "name@example.com", "commit with amend", null, new Date(), true, new HashMap<String, File>() {
                            {
                                put( "path/to/additional-file" + fileNum + ".txt", tempFile( "temp2222" ) );
                            }
                        } );
                    }
                } catch ( IOException ioe ) {
                    ioe.printStackTrace();
                }
                System.out.println( "commiter finished : elapsedTime = " + (System.currentTimeMillis() - start) + " ms" );
            }
        };

        Runnable reader = new Runnable() {
            @Override
            public void run() {
                System.out.println( "reader start" );
                for ( int i = 0; i < 2000; i++ ) {
                    // Just want to get an existing file
                    JGitPathInfo info = JGitUtil.resolvePath( git, "master", "path/to/file1.txt" );
                    if ( info == null ) {
                        System.out.println( "info == null, i = " + i );
                        failureSet.add( i );
                    } else if ( info.getPath() == null ) {
                        System.out.println( "info.getPath() == null, i = " + i );
                        failureSet.add( i );
                    } else if ( !info.getPath().equals( "path/to/file1.txt" ) ) {
                        System.out.println( "info.getPath() == " + info.getPath() + ", i = " + i );
                        failureSet.add( i );
                    }
                }
                System.out.println( "reader finished : elapsedTime = " + (System.currentTimeMillis() - start) + " ms" );
            }
        };

        ExecutorService executor = Executors.newFixedThreadPool( 2 );
        executor.execute( commiter );
        executor.execute( reader );
        executor.shutdown();
        try {
            executor.awaitTermination( 300, TimeUnit.SECONDS );
        } catch ( InterruptedException e ) {
            e.printStackTrace();
        }

        assertEquals( 0, failureSet.size() );
    }
}