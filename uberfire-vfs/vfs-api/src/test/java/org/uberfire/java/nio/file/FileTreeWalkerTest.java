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

package org.uberfire.java.nio.file;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Test;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;

import static org.fest.assertions.api.Assertions.*;

public class FileTreeWalkerTest extends AbstractBaseTest {

    final AtomicInteger preDir = new AtomicInteger();
    final AtomicInteger postDir = new AtomicInteger();
    final AtomicInteger fileC = new AtomicInteger();
    final AtomicInteger failFile = new AtomicInteger();

    final FileVisitor<Path> simple = new FileVisitor<Path>() {

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            preDir.addAndGet(1);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            fileC.addAndGet(1);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
            failFile.addAndGet(1);
            return FileVisitResult.TERMINATE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            postDir.addAndGet(1);
            return FileVisitResult.CONTINUE;
        }
    };

    @After
    public void cleanup() throws java.io.IOException {
        for (final File file : cleanupList) {
            FileUtils.deleteDirectory(file);
        }
    }

    @Test
    public void testWalker() {
        final FileTreeWalker walker = new FileTreeWalker(simple, 1);

        walker.walk(Paths.get("/some/path"));

        assertThat(preDir.get()).isEqualTo(0);
        assertThat(postDir.get()).isEqualTo(0);
        assertThat(fileC.get()).isEqualTo(0);
        assertThat(failFile.get()).isEqualTo(1);

        final Path dir = newTempDir(null);

        final Path file1 = Files.createTempFile(dir, "foo", "bar");
        final Path file2 = Files.createTempFile(dir, "foo", "bar");

        cleanupVisitor();
        walker.walk(dir);

        assertThat(preDir.get()).isEqualTo(1);
        assertThat(postDir.get()).isEqualTo(1);
        assertThat(fileC.get()).isEqualTo(2);
        assertThat(failFile.get()).isEqualTo(0);

        cleanupVisitor();
        walker.walk(file1);

        assertThat(preDir.get()).isEqualTo(0);
        assertThat(postDir.get()).isEqualTo(0);
        assertThat(fileC.get()).isEqualTo(1);
        assertThat(failFile.get()).isEqualTo(0);
    }

    @Test
    public void testWalkerDeep2() {
        final FileTreeWalker walker = new FileTreeWalker(simple, 2);

        final Path dir = newTempDir(null);
        final Path subDir = newTempDir(dir);
        final Path subSubDir = newTempDir(subDir);
        final Path subSubSubDir = newTempDir(subSubDir);

        cleanupVisitor();
        walker.walk(dir);

        assertThat(preDir.get()).isEqualTo(2);
        assertThat(postDir.get()).isEqualTo(2);
        assertThat(fileC.get()).isEqualTo(1);
        assertThat(failFile.get()).isEqualTo(0);
    }

    @Test
    public void testWalkerDeep1() {
        final FileTreeWalker walker = new FileTreeWalker(simple, 1);

        final Path dir = newTempDir(null);
        final Path subDir = newTempDir(dir);
        final Path subSubDir = newTempDir(subDir);
        final Path subSubSubDir = newTempDir(subSubDir);

        Files.createTempFile(dir, "foo", "bar");
        Files.createTempFile(dir, "foo", "bar");

        cleanupVisitor();
        walker.walk(dir);

        assertThat(preDir.get()).isEqualTo(1);
        assertThat(postDir.get()).isEqualTo(1);
        assertThat(fileC.get()).isEqualTo(3);
        assertThat(failFile.get()).isEqualTo(0);
    }

    @Test
    public void testWalkerDeep2ButTerminateOnDir() {

        final FileVisitor<Path> terminateOnDir = new FileVisitor<Path>() {

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                preDir.addAndGet(1);
                if (preDir.get() > 1) {
                    return FileVisitResult.TERMINATE;
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                fileC.addAndGet(1);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                failFile.addAndGet(1);
                return FileVisitResult.TERMINATE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                postDir.addAndGet(1);
                return FileVisitResult.CONTINUE;
            }
        };

        final FileTreeWalker walker = new FileTreeWalker(terminateOnDir, 2);

        final Path dir = newTempDir(null);
        final Path subDir = newTempDir(dir);
        final Path subSubDir = newTempDir(subDir);
        final Path subSubSubDir = newTempDir(subSubDir);

        Files.createTempFile(dir, "foo", "bar");
        Files.createTempFile(dir, "foo", "bar");

        cleanupVisitor();
        walker.walk(dir);

        assertThat(preDir.get()).isEqualTo(2);
        assertThat(postDir.get()).isEqualTo(0);
        assertThat(fileC.get()).isEqualTo(2);
        assertThat(failFile.get()).isEqualTo(0);
    }

    @Test
    public void testWalkerDeep2ButSkipSibling() {

        final FileVisitor<Path> terminateOnDir = new FileVisitor<Path>() {

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                preDir.addAndGet(1);
                if (preDir.get() > 1) {
                    return FileVisitResult.SKIP_SIBLINGS;
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                fileC.addAndGet(1);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                failFile.addAndGet(1);
                return FileVisitResult.TERMINATE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                postDir.addAndGet(1);
                return FileVisitResult.CONTINUE;
            }
        };

        final FileTreeWalker walker = new FileTreeWalker(terminateOnDir, 2);

        final Path dir = newTempDir(null);
        final Path subDir1 = newTempDir(dir);
        final Path subDir2 = newTempDir(dir);
        final Path subDir3 = newTempDir(dir);
        final Path subDir4 = newTempDir(dir);

        Files.createTempFile(dir, "foo", "bar");
        Files.createTempFile(dir, "foo", "bar");

        cleanupVisitor();
        walker.walk(dir);

        assertThat(preDir.get()).isEqualTo(2);
        assertThat(postDir.get()).isEqualTo(1);
        assertThat(fileC.get()).isEqualTo(2);
        assertThat(failFile.get()).isEqualTo(0);
    }

    @Test
    public void testWalkerDeep2ButThrowExceptionOnSibling() {

        final FileVisitor<Path> terminateOnDir = new FileVisitor<Path>() {

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                preDir.addAndGet(1);
                if (preDir.get() > 1) {
                    throw new IOException();
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                fileC.addAndGet(1);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                failFile.addAndGet(1);
                return FileVisitResult.TERMINATE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                postDir.addAndGet(1);
                if (exc == null) {
                    throw new RuntimeException();
                }
                return FileVisitResult.CONTINUE;
            }
        };

        final FileTreeWalker walker = new FileTreeWalker(terminateOnDir, 2);

        final Path dir = newTempDir(null);
        final Path subDir1 = newTempDir(dir);
        final Path subDir2 = newTempDir(dir);
        final Path subDir3 = newTempDir(dir);
        final Path subDir4 = newTempDir(dir);

        Files.createTempFile(dir, "foo", "bar");
        Files.createTempFile(dir, "foo", "bar");

        cleanupVisitor();
        walker.walk(dir);

        assertThat(preDir.get()).isEqualTo(2);
        assertThat(postDir.get()).isEqualTo(1);
        assertThat(fileC.get()).isEqualTo(2);
        assertThat(failFile.get()).isEqualTo(0);
    }

    @Test
    public void testWalkerDeep2ButReturnNull() {

        final FileVisitor<Path> terminateOnDir = new FileVisitor<Path>() {

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                preDir.addAndGet(1);
                if (preDir.get() > 1) {
                    return null;
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                fileC.addAndGet(1);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                failFile.addAndGet(1);
                return null;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                postDir.addAndGet(1);
                if (exc == null) {
                    throw new RuntimeException();
                }
                return FileVisitResult.CONTINUE;
            }
        };

        final FileTreeWalker walker = new FileTreeWalker(terminateOnDir, 2);

        final Path dir = newTempDir(null);
        final Path subDir1 = newTempDir(dir);
        final Path subDir2 = newTempDir(dir);
        final Path subDir3 = newTempDir(dir);
        final Path subDir4 = newTempDir(dir);

        Files.createTempFile(dir, "foo", "bar");
        Files.createTempFile(dir, "foo", "bar");

        cleanupVisitor();
        walker.walk(dir);

        assertThat(preDir.get()).isEqualTo(2);
        assertThat(postDir.get()).isEqualTo(0);
        assertThat(fileC.get()).isEqualTo(2);
        assertThat(failFile.get()).isEqualTo(0);
    }

    private void cleanupVisitor() {
        preDir.set(0);
        postDir.set(0);
        fileC.set(0);
        failFile.set(0);
    }

}
