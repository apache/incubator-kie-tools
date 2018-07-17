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

package org.uberfire.io.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.hooks.PostCommitHook;
import org.eclipse.jgit.hooks.PreCommitHook;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.util.FS_POSIX;
import org.eclipse.jgit.util.ProcessResult;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.uberfire.commons.lifecycle.PriorityDisposableRegistry;
import org.uberfire.io.CommonIOServiceDotFileTest;
import org.uberfire.io.IOService;
import org.uberfire.io.lock.BatchLockControl;
import org.uberfire.java.nio.base.WatchContext;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.base.version.VersionAttributeView;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.StandardWatchEventKind;
import org.uberfire.java.nio.file.WatchEvent;
import org.uberfire.java.nio.file.WatchService;
import org.uberfire.java.nio.file.api.FileSystemProviders;
import org.uberfire.java.nio.file.spi.FileSystemProvider;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemImpl;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProvider;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProxy;
import org.uberfire.java.nio.fs.jgit.ws.JGitWatchEvent;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class BatchTest {

    final static IOService ioService = new IOServiceDotFileImpl();
    static FileSystem fs1;
    static JGitFileSystemImpl fs1Batch;
    static FileSystem fs2;
    static JGitFileSystemImpl fs2Batch;
    static FileSystem fs3;
    static JGitFileSystemImpl fs3Batch;
    private static File path = null;

    @BeforeClass
    public static void setup() throws IOException {
        assertTrue(PriorityDisposableRegistry.getDisposables().contains(ioService));
        path = CommonIOServiceDotFileTest.createTempDirectory();

        // XXX this is shaky at best: FileSystemProviders bootstraps the JGit FS in a static initializer.
        //     if anything has referenced it before now, setting this system property will have no effect.
        System.setProperty("org.uberfire.nio.git.dir",
                           path.getAbsolutePath());
        System.out.println(".niogit: " + path.getAbsolutePath());

        final URI newRepo = URI.create("git://amend-repo-test");

        fs1 = ioService.newFileSystem(newRepo,
                                      new HashMap<>());
        fs1Batch = (JGitFileSystemImpl) ((JGitFileSystemProxy) fs1).getRealJGitFileSystem();
        Path init = ioService.get(URI.create("git://amend-repo-test/init.file"));
        ioService.write(init,
                        "setupFS!");

        final URI newRepo2 = URI.create("git://check-amend-repo-test");

        fs2 = ioService.newFileSystem(newRepo2,
                                      new HashMap<String, Object>() {{
                                          put("init",
                                              "true");
                                      }});
        fs2Batch = (JGitFileSystemImpl) ((JGitFileSystemProxy) fs2).getRealJGitFileSystem();
        init = ioService.get(URI.create("git://check-amend-repo-test/init.file"));
        ioService.write(init,
                        "setupFS!");

        final URI newRepo3 = URI.create("git://check-amend-repo-test-2");
        fs3 = ioService.newFileSystem(newRepo3,
                                      new HashMap<String, Object>() {{
                                          put("init",
                                              "true");
                                      }});
        fs3Batch = (JGitFileSystemImpl) ((JGitFileSystemProxy) fs3).getRealJGitFileSystem();
        init = ioService.get(URI.create("git://check-amend-repo-test-2/init.file"));
        ioService.write(init,
                        "setupFS!");
    }

    @AfterClass
    public static void cleanup() {
        FileUtils.deleteQuietly(path);
        JGitFileSystemProvider gitFsProvider = (JGitFileSystemProvider) FileSystemProviders.resolveProvider(URI.create("git://whatever"));
        gitFsProvider.shutdown();
        FileUtils.deleteQuietly(gitFsProvider.getGitRepoContainerDir());
    }

    @Test
    public void deleteOnBatchEventShouldKeepUserInfo() {
        final Path init = ioService.get(URI.create("git://amend-repo-test/file.txt"));

        final WatchService ws = init.getFileSystem().newWatchService();
        String user = "dora";
        String message = "message";
        ioService.write(init,
                        "init!",
                        new CommentedOption(user,
                                            message));
        {
            List<WatchEvent<?>> events = ws.poll().pollEvents();
        }

        ioService.startBatch(init.getFileSystem());
        ioService.delete(init, new CommentedOption(user, message));
        ioService.endBatch();
        {
            List<WatchEvent<?>> events = ws.poll().pollEvents();
            JGitWatchEvent event = (JGitWatchEvent) events.get(0);
            WatchContext context = (WatchContext) event.context();
            assertEquals(user, context.getUser());
        }
    }

    @Test
    public void testMoveAndAddOnBatchShouldTriggerRenameAndModifyEvent() {
        final Path init = ioService.get(URI.create("git://amend-repo-test/file.txt"));
        final Path initMoved = ioService.get(URI.create("git://amend-repo-test/fileMoved.txt"));

        final WatchService ws = init.getFileSystem().newWatchService();

        ioService.write(init,
                        "init!",
                        new CommentedOption("User Tester",
                                            "message1"));
        {
            List<WatchEvent<?>> events = ws.poll().pollEvents();
            assertEquals(1,
                         events.size());
            assertEquals(StandardWatchEventKind.ENTRY_CREATE, ((JGitWatchEvent) events.get(0)).kind());
        }

        ioService.move(init, initMoved, new CommentedOption("moved"));

        {
            List<WatchEvent<?>> events = ws.poll().pollEvents();
            assertEquals(1,
                         events.size());
            assertEquals(StandardWatchEventKind.ENTRY_RENAME, ((JGitWatchEvent) events.get(0)).kind());
        }

        final Path aNewFile = ioService.get(URI.create("git://amend-repo-test/aNewFile.txt"));
        final Path aNewFileMoved = ioService.get(URI.create("git://amend-repo-test/aNewFileMoved.txt"));

        ioService.write(aNewFile,
                        "init!",
                        new CommentedOption("User Tester",
                                            "message1"));

        {
            List<WatchEvent<?>> events = ws.poll().pollEvents();
            assertEquals(1,
                         events.size());
            assertEquals(StandardWatchEventKind.ENTRY_CREATE, ((JGitWatchEvent) events.get(0)).kind());
        }

        ioService.startBatch(aNewFile.getFileSystem());
        ioService.move(aNewFile, aNewFileMoved, new CommentedOption("moved"));

        ioService.write(aNewFileMoved,
                        "aNewFileMoved!",
                        new CommentedOption("User Tester",
                                            "message1"));

        ioService.endBatch();

        assertEquals("aNewFileMoved!", ioService.readAllString(aNewFileMoved));
        {
            List<WatchEvent<?>> events = ws.poll().pollEvents();

            assertEquals(2,
                         events.size());
            assertEquals(StandardWatchEventKind.ENTRY_RENAME, ((JGitWatchEvent) events.get(0)).kind());
            assertEquals(StandardWatchEventKind.ENTRY_MODIFY, ((JGitWatchEvent) events.get(1)).kind());
        }
    }

    @Test
    public void testBatch() throws IOException, InterruptedException {
        final Path init = ioService.get(URI.create("git://amend-repo-test/readme.txt"));
        final WatchService ws = init.getFileSystem().newWatchService();

        ioService.write(init,
                        "init!",
                        new CommentedOption("User Tester",
                                            "message1"));
        ioService.write(init,
                        "init 2!",
                        new CommentedOption("User Tester",
                                            "message2"));
        {
            List<WatchEvent<?>> events = ws.poll().pollEvents();
            assertEquals(1,
                         events.size());//modify readme
        }

        final Path init2 = ioService.get(URI.create("git://amend-repo-test/readme2.txt"));
        ioService.write(init2,
                        "init 3!",
                        new CommentedOption("User Tester",
                                            "message3"));
        {
            List<WatchEvent<?>> events = ws.poll().pollEvents();
            assertEquals(1,
                         events.size()); // add file
        }
        ioService.write(init2,
                        "init 4!",
                        new CommentedOption("User Tester",
                                            "message4"));
        {
            List<WatchEvent<?>> events = ws.poll().pollEvents();
            assertEquals(1,
                         events.size());// modify file
        }

        final VersionAttributeView vinit = ioService.getFileAttributeView(init,
                                                                          VersionAttributeView.class);
        final VersionAttributeView vinit2 = ioService.getFileAttributeView(init,
                                                                           VersionAttributeView.class);

        assertEquals("init 2!",
                     ioService.readAllString(init));

        assertNotNull(vinit);
        assertEquals(2,
                     vinit.readAttributes().history().records().size());
        assertNotNull(vinit2);
        assertEquals(2,
                     vinit2.readAttributes().history().records().size());

        ioService.startBatch(init.getFileSystem());
        final Path path = ioService.get(URI.create("git://amend-repo-test/mybatch" + new Random(10L).nextInt() + ".txt"));
        final Path path2 = ioService.get(URI.create("git://amend-repo-test/mybatch2" + new Random(10L).nextInt() + ".txt"));
        ioService.write(path,
                        "ooooo!");
        //init.file event
        assertNotNull(ws.poll());
        ioService.write(path,
                        "ooooo wdfs fg sdf!");
        assertNull(ws.poll());
        ioService.write(path2,
                        "ooooo222!");
        assertNull(ws.poll());
        ioService.write(path2,
                        " sdfsdg sdg ooooo222!");
        assertNull(ws.poll());
        ioService.endBatch();
        {
            List<WatchEvent<?>> events = ws.poll().pollEvents();
            assertEquals(4,
                         events.size()); //adds files
        }

        final VersionAttributeView v = ioService.getFileAttributeView(path,
                                                                      VersionAttributeView.class);
        final VersionAttributeView v2 = ioService.getFileAttributeView(path2,
                                                                       VersionAttributeView.class);

        assertNotNull(v);
        assertNotNull(v2);
        assertEquals(1,
                     v.readAttributes().history().records().size());
        assertEquals(1,
                     v2.readAttributes().history().records().size());

        assertProperBatchCleanup();
    }

    @Test
    public void testBatch2() throws IOException, InterruptedException {
        final Path f1 = ioService.get(URI.create("git://check-amend-repo-test/f1.txt"));
        final Path f2 = ioService.get(URI.create("git://check-amend-repo-test/f2.txt"));
        final Path f3 = ioService.get(URI.create("git://check-amend-repo-test/f3.txt"));
        // XXX: Workaround for UF-70: amend-test-repo has to contain something so it can receive the BATCH
        ioService.write(f1,
                        "init f1!");
        ioService.write(f2,
                        "init f2!");
        // END workaround

        final WatchService ws = f1.getFileSystem().newWatchService();

        ioService.startBatch(f1.getFileSystem());
        ioService.write(f1,
                        "f1-u1!");
        assertNull(ws.poll());
        ioService.write(f2,
                        "f2-u1!");
        assertNull(ws.poll());
        ioService.write(f3,
                        "f3-u1!");
        assertNull(ws.poll());
        ioService.endBatch();

        {
            List<WatchEvent<?>> events = ws.poll().pollEvents();
            assertEquals(3,
                         events.size()); //adds files

            final VersionAttributeView v = ioService.getFileAttributeView(f1,
                                                                          VersionAttributeView.class);
            assertNotNull(v);
            assertEquals(2,
                         v.readAttributes().history().records().size());

            final VersionAttributeView v2 = ioService.getFileAttributeView(f2,
                                                                           VersionAttributeView.class);
            assertNotNull(v2);
            assertEquals(2,
                         v2.readAttributes().history().records().size());

            final VersionAttributeView v3 = ioService.getFileAttributeView(f3,
                                                                           VersionAttributeView.class);
            assertNotNull(v3);
            assertEquals(1,
                         v3.readAttributes().history().records().size());
        }

        ioService.startBatch(f1.getFileSystem());
        ioService.write(f1,
                        "f1-u1!");
        assertNull(ws.poll());
        ioService.write(f2,
                        "f2-u2!");
        assertNull(ws.poll());
        ioService.write(f3,
                        "f3-u2!");
        assertNull(ws.poll());
        ioService.endBatch();

        {
            List<WatchEvent<?>> events = ws.poll().pollEvents();
            assertEquals(2,
                         events.size()); //adds files

            final VersionAttributeView v = ioService.getFileAttributeView(f1,
                                                                          VersionAttributeView.class);
            assertNotNull(v);
            assertEquals(2,
                         v.readAttributes().history().records().size());

            final VersionAttributeView v2 = ioService.getFileAttributeView(f2,
                                                                           VersionAttributeView.class);
            assertNotNull(v2);
            assertEquals(3,
                         v2.readAttributes().history().records().size());

            final VersionAttributeView v3 = ioService.getFileAttributeView(f3,
                                                                           VersionAttributeView.class);
            assertNotNull(v3);
            assertEquals(2,
                         v3.readAttributes().history().records().size());
        }
        assertProperBatchCleanup();
    }

    @Test
    public void batchTest() throws IOException, InterruptedException {
        final Path init = ioService.get(URI.create("git://amend-repo-test/readme.txt"));
        ioService.write(init,
                        "init!",
                        new CommentedOption("User Tester",
                                            "message1"));

        ioService.startBatch(fs1);
        assertTrue(fs1Batch.isOnBatch());
        ioService.endBatch();
        assertFalse(fs1Batch.isOnBatch());
        assertProperBatchCleanup();
    }

    @Test
    public void justOneFSOnBatchTest() throws IOException, InterruptedException {
        Path init = ioService.get(URI.create("git://amend-repo-test/readme.txt"));
        ioService.write(init,
                        "init!",
                        new CommentedOption("User Tester",
                                            "message1"));

        init = ioService.get(URI.create("git://check-amend-repo-test/readme.txt"));
        ioService.write(init,
                        "init!",
                        new CommentedOption("User Tester",
                                            "message1"));

        ioService.startBatch(fs1);
        assertTrue(fs1Batch.isOnBatch());
        assertFalse(fs2Batch.isOnBatch());
        ioService.endBatch();
        assertProperBatchCleanup();
    }

    @Test
    public void testInnerBatch() throws IOException, InterruptedException {
        Path init = ioService.get(URI.create("git://amend-repo-test/readme.txt"));
        ioService.write(init,
                        "init!",
                        new CommentedOption("User Tester",
                                            "message1"));

        init = ioService.get(URI.create("git://check-amend-repo-test/readme.txt"));
        ioService.write(init,
                        "init!",
                        new CommentedOption("User Tester",
                                            "message1"));

        ioService.startBatch(fs1);
        assertTrue(fs1Batch.isOnBatch());
        ioService.startBatch(fs1);
        assertTrue(fs1Batch.isOnBatch());
        ioService.endBatch();
        assertTrue(fs1Batch.isOnBatch());
        ioService.endBatch();
        assertProperBatchCleanup();
    }

    @Test
    public void innerBatchShouldOnlyBeCalledOnTheSameFS() throws IOException, InterruptedException {
        Path repo1 = ioService.get(URI.create("git://amend-repo-test/readme.txt"));
        ioService.write(repo1,
                        "init!",
                        new CommentedOption("User Tester",
                                            "message1"));

        Path repo2 = ioService.get(URI.create("git://check-amend-repo-test/readme.txt"));
        ioService.write(repo2,
                        "init!",
                        new CommentedOption("User Tester",
                                            "message1"));

        ioService.startBatch(fs1);
        assertTrue(fs1Batch.isOnBatch());
        try {
            ioService.startBatch(fs2);
            fail();
        } catch (BatchLockControl.BatchRuntimeException e) {
            //We already have a batch process running on another FS : git://amend-repo-test
        }
        assertTrue(fs1Batch.isOnBatch());
        ioService.endBatch();
        assertProperBatchCleanup();
    }

    @Test
    public void assertNumberOfCommitsOnInnerBatch() throws IOException, InterruptedException {
        final Path f11 = ioService.get(URI.create("git://check-amend-repo-test/f11.txt"));
        // XXX: Workaround for UF-70: amend-test-repo has to contain something so it can receive the BATCH
        ioService.write(f11,
                        "init f1!");
        // END workaround

        ioService.startBatch(f11.getFileSystem());
        ioService.write(f11,
                        "f1-u1!");
        ioService.endBatch();

        VersionAttributeView v = ioService.getFileAttributeView(f11,
                                                                VersionAttributeView.class);
        assertNotNull(v);
        assertEquals(2,
                     v.readAttributes().history().records().size());

        ioService.startBatch(f11.getFileSystem());

        ioService.write(f11,
                        "f2-u2!");

        //inner batch (samme commit)
        ioService.startBatch(f11.getFileSystem());
        ioService.write(f11,
                        "f2-u2 - inner batch!");
        ioService.write(f11,
                        "f2-u2 - inner 2 batch!");
        ioService.endBatch();
        ioService.write(f11,
                        "f2-u2 - inner batch! last");

        ioService.endBatch();

        assertEquals("f2-u2 - inner batch! last",
                     ioService.readAllString(f11));

        v = ioService.getFileAttributeView(f11,
                                           VersionAttributeView.class);
        assertNotNull(v);
        assertEquals(4,
                     v.readAttributes().history().records().size());
        assertProperBatchCleanup();
    }

    @Test
    public void testTwoStartedFsOnBatchByTheSameThread() throws IOException, InterruptedException {
        Path init = ioService.get(URI.create("git://amend-repo-test/readme.txt"));
        ioService.write(init,
                        "init!",
                        new CommentedOption("User Tester",
                                            "message1"));

        init = ioService.get(URI.create("git://check-amend-repo-test/readme.txt"));
        ioService.write(init,
                        "init!",
                        new CommentedOption("User Tester",
                                            "message1"));

        ioService.startBatch(fs1);
        try {
            ioService.startBatch(fs1);
        } catch (final Exception e) {
            fail();
        }

        ioService.endBatch();
        ioService.endBatch();

        try {
            ioService.endBatch();
            fail();
        } catch (final Exception e) {
        }
        assertProperBatchCleanup();
    }

    @Test
    public void testTwoFsOnBatchByTheSameThread() throws IOException, InterruptedException {
        Path init = ioService.get(URI.create("git://amend-repo-test/readme.txt"));
        ioService.write(init,
                        "init!",
                        new CommentedOption("User Tester",
                                            "message1"));

        init = ioService.get(URI.create("git://check-amend-repo-test/readme.txt"));
        ioService.write(init,
                        "init!",
                        new CommentedOption("User Tester",
                                            "message1"));

        ioService.startBatch(fs1);
        assertTrue(fs1Batch.isOnBatch());
        ioService.endBatch();
        ioService.startBatch(fs2);
        assertTrue(fs2Batch.isOnBatch());
        ioService.endBatch();
        assertProperBatchCleanup();
    }

    @Test
    public void testDifferentThreads() throws IOException, InterruptedException {

        final Path init = ioService.get(URI.create("git://amend-repo-test/readme.txt"));
        ioService.write(init,
                        "init!");

        ioService.startBatch(fs1);
        System.out.println("After start batch");
        Thread thread = new Thread("second") {
            @Override
            public void run() {
                try {
                    System.out.println("Inner starting");
                    ioService.startBatch(fs1);
                    System.out.println("Inner after batch");
                    final OutputStream innerOut = ioService.newOutputStream(init);
                    for (int i = 0; i < 100; i++) {
                        innerOut.write(("sss" + i).getBytes());
                    }
                    System.out.println("Inner after write");
                    innerOut.close();
                    System.out.println("Inner after close");
                    ioService.endBatch();
                    System.out.println("Inner after end batch");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        thread.start();
        System.out.println("After start 2nd Thread");
        for (int i = 0; i < 100; i++) {
            if (i % 20 == 0) {
                Thread.sleep(10);
            }
            ioService.write(init,
                            ("sss" + i).getBytes());
        }
        System.out.println("After writes");
        ioService.endBatch();
        System.out.println("After end batch");
        thread.join();
        assertProperBatchCleanup();
    }

    @Test
    public void testDifferentThreadsWithoutBatch() throws IOException, InterruptedException {
        final Path init = ioService.get(URI.create("git://amend-repo-test/readme.txt"));
        ioService.write(init,
                        "init!");

        Thread thread = new Thread("second") {
            @Override
            public void run() {
                try {
                    System.out.println("Inner starting");
                    final OutputStream innerOut = ioService.newOutputStream(init);
                    for (int i = 0; i < 100; i++) {
                        innerOut.write(("sss" + i).getBytes());
                    }
                    System.out.println("Inner after write");
                    innerOut.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        thread.start();
        System.out.println("After start 2nd Thread");
        for (int i = 0; i < 100; i++) {
            if (i % 20 == 0) {
                Thread.sleep(10);
            }
            ioService.write(init,
                            ("sss" + i).getBytes());
        }
        System.out.println("After writes");
        thread.join();
        assertProperBatchCleanup();
    }

    @Test
    public void testDifferentThreads3() throws IOException, InterruptedException {
        final Path init = ioService.get(URI.create("git://amend-repo-test/readme.txt"));
        ioService.write(init,
                        "init!");

        ioService.startBatch(fs1);
        System.out.println("After start batch");

        final Runnable runnable = new Runnable() {

            @Override
            public void run() {
                try {
                    System.out.println("Inner starting");
                    ioService.startBatch(fs1);
                    System.out.println("Inner after batch");
                    final OutputStream innerOut = ioService.newOutputStream(init);
                    for (int i = 0; i < 100; i++) {
                        innerOut.write(("sss" + i).getBytes());
                    }
                    System.out.println("Inner after write");
                    innerOut.close();
                    System.out.println("Inner after close");
                    ioService.endBatch();
                    System.out.println("Inner after end batch");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        final Thread thread = new Thread(runnable,
                                         "second");
        final Thread thread2 = new Thread(runnable,
                                          "third");
        thread.start();
        Thread.sleep(100);
        thread2.start();
        Thread.sleep(100);

        System.out.println("After start 2nd Thread");
        for (int i = 0; i < 100; i++) {
            if (i % 20 == 0) {
                Thread.sleep(10);
            }
            ioService.write(init,
                            ("sss" + i).getBytes());
        }
        System.out.println("After writes");
        ioService.endBatch();
        System.out.println("After end batch");
        thread.join();
        thread2.join();
        assertProperBatchCleanup();
    }

    @Test
    public void testDifferentThreadsNotBatchInners() throws IOException, InterruptedException {
        final Path init = ioService.get(URI.create("git://amend-repo-test/readme.txt"));
        ioService.write(init,
                        "init!");

        ioService.startBatch(fs1);
        System.out.println("After start batch");

        final Runnable runnable = new Runnable() {

            @Override
            public void run() {
                try {
                    System.out.println("Inner starting");
                    final OutputStream innerOut = ioService.newOutputStream(init);
                    for (int i = 0; i < 100; i++) {
                        innerOut.write(("sss" + i).getBytes());
                    }
                    System.out.println("Inner after write");
                    innerOut.close();
                    System.out.println("Inner after end batch");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        final Thread thread = new Thread(runnable,
                                         "second");
        final Thread thread2 = new Thread(runnable,
                                          "third");
        thread.start();
        Thread.sleep(100);
        thread2.start();
        Thread.sleep(100);

        System.out.println("After start 2nd Thread");
        for (int i = 0; i < 100; i++) {
            if (i % 20 == 0) {
                Thread.sleep(10);
            }
            ioService.write(init,
                            ("sss" + i).getBytes());
        }
        System.out.println("After writes");
        ioService.endBatch();
        System.out.println("After end batch");
        thread.join();
        thread2.join();
        assertProperBatchCleanup();
    }

    @Test
    public void testDifferentThreadsNotBatchOuter() throws IOException, InterruptedException {
        final Path init = ioService.get(URI.create("git://amend-repo-test/readme.txt"));
        ioService.write(init,
                        "init!");

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("Inner starting");
                    ioService.startBatch(fs1);
                    System.out.println("Inner after batch");
                    final OutputStream innerOut = ioService.newOutputStream(init);
                    for (int i = 0; i < 100; i++) {
                        ioService.write(init,
                                        ("sss" + i).getBytes());
                    }
                    System.out.println("Inner after write");
                    innerOut.close();
                    System.out.println("Inner after close");
                    ioService.endBatch();
                    System.out.println("Inner after end batch");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        final Thread thread = new Thread(runnable,
                                         "second");
        final Thread thread2 = new Thread(runnable,
                                          "third");
        thread.start();
        Thread.sleep(100);
        thread2.start();
        Thread.sleep(100);

        System.out.println("After start 2nd Thread");
        for (int i = 0; i < 100; i++) {
            if (i % 20 == 0) {
                Thread.sleep(10);
            }
            ioService.write(init,
                            ("sss" + i).getBytes());
        }
        System.out.println("After writes");
        thread.join();
        thread2.join();
        assertProperBatchCleanup();
    }

    @Test
    public void exceptionOnCleanUpAndUnsetBatchModeOnFileSystemsShouldReleaseLock() throws IOException, InterruptedException {
        IOServiceDotFileImpl ioServiceSpy = spy((IOServiceDotFileImpl) ioService);

        Mockito.doThrow(new RuntimeException()).when(ioServiceSpy).unsetBatchModeOn(fs1Batch);

        final Path init = ioService.get(URI.create("git://amend-repo-test/readme.txt"));
        ioServiceSpy.write(init,
                           "init!",
                           new CommentedOption("User Tester",
                                               "message1"));

        ioServiceSpy.startBatch(fs1);
        assertTrue(ioServiceSpy.getLockControl().isLocked());
        try {
            ioServiceSpy.endBatch();
        } catch (Exception e) {

        }
        assertFalse(ioServiceSpy.getLockControl().isLocked());
    }

    @Test
    public void hooksShouldBeCalledOnBatch() throws IOException {

        final File hooksDir = CommonIOServiceDotFileTest.createTempDirectory();
        System.setProperty("org.uberfire.nio.git.hooks",
                           hooksDir.getAbsolutePath());

        writeMockHook(hooksDir,
                      "post-commit");

        JGitFileSystemProvider provider = (JGitFileSystemProvider) FileSystemProviders.resolveProvider(URI.create("git://hook-fs/"));

        final AtomicInteger postCommitHookCalled = new AtomicInteger();

        provider.setDetectedFS(new FS_POSIX() {
            @Override
            public ProcessResult runHookIfPresent(Repository repox,
                                                  String hookName,
                                                  String[] args) throws JGitInternalException {
                if (hookName.equals(PostCommitHook.NAME)) {
                    postCommitHookCalled.incrementAndGet();
                }
                return null;
            }
        });

        ioService.newFileSystem(URI.create("git://hook-fs/"),
                                new HashMap<>());

        Path init = ioService.get(URI.create("git://hook-fs/init.file"));

        ioService.write(init,
                        "setupFS!");

        ioService.startBatch(init.getFileSystem());

        ioService.write(init,
                        "onBatch!");

        ioService.endBatch();

        assertEquals(2, postCommitHookCalled.get());
    }

    static void writeMockHook(final File hooksDirectory,
                              final String hookName)
            throws FileNotFoundException, UnsupportedEncodingException {
        final PrintWriter writer = new PrintWriter(new File(hooksDirectory,
                                                            hookName),
                                                   "UTF-8");
        writer.println("# hook data");
        writer.close();
    }

    private void assertProperBatchCleanup() {
        assertFalse(fs1Batch.isOnBatch());
        assertFalse(fs1Batch.isLocked());
        assertFalse(fs2Batch.isOnBatch());
        assertFalse(fs2Batch.isLocked());
        assertFalse(fs3Batch.isOnBatch());
        assertFalse(fs3Batch.isLocked());
    }
}