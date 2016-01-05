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

package org.uberfire.java.nio.fs.jgit.daemon.git;

import static org.junit.Assert.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;


public class DaemonTest {

    @Test
    public void testShutdownByStop() throws Exception {
        ExecutorService executor = Executors.newCachedThreadPool();
        Daemon d = new Daemon( null, executor );
        d.start();
        assertTrue( d.isRunning() );

        d.stop();

        assertFalse( d.isRunning() );
    }

    @Test
    public void testShutdownByThreadPoolTermination() throws Exception {
        ExecutorService executor = Executors.newCachedThreadPool();
        Daemon d = new Daemon( null, executor );
        d.start();
        assertTrue( d.isRunning() );

        executor.shutdownNow();
        executor.awaitTermination( 10, TimeUnit.SECONDS );

        assertFalse( d.isRunning() );
    }
}
