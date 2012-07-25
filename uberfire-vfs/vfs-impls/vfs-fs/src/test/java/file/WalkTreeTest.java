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

package file;

import java.util.ArrayList;
import java.util.List;

import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.FileVisitResult;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.junit.Ignore;
import org.junit.Test;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Paths;
import org.uberfire.java.nio.file.SimpleFileVisitor;

import static org.junit.Assert.*;

@Ignore
public class WalkTreeTest {

    private Path setup() {
        final Path top = Files.createTempDirectory("tree");
        List<Path> dirs = new ArrayList<Path>();

        {//0 files, 2 subdirs
            final String name = "dir1";
            final Path subdir = Files.createDirectory(top.resolve(name));

            { //3 files, 2 subdirs
                final String inner_name = "dir2";
                final Path inner_subdir = Files.createDirectory(subdir.resolve(inner_name));
                Files.createFile(inner_subdir.resolve("file1.txt"));
                Files.createFile(inner_subdir.resolve("file2.txt"));
                Files.createFile(inner_subdir.resolve("file3.txt"));

                { //empty
                    final String inner_inner_name = "dir3";
                    Files.createDirectory(inner_subdir.resolve(inner_inner_name));
                }
                { //2 files
                    final String inner_inner_name = "dir4";
                    final Path inner_inner_subdir = Files.createDirectory(inner_subdir.resolve(inner_inner_name));
                    Files.createFile(inner_inner_subdir.resolve("file4.txt"));
                    Files.createFile(inner_inner_subdir.resolve("file5.txt"));
                }
            }
            {//1 file
                final String inner_name = "dir5";
                final Path inner_subdir = Files.createDirectory(subdir.resolve(inner_name));
                Files.createFile(inner_subdir.resolve("file6.txt"));
            }
        }
        {//1 file, 1 subdir
            final String name = "dir6";
            final Path subdir = Files.createDirectory(top.resolve(name));

            Files.createFile(subdir.resolve("file7.txt"));

            {//empty
                final String inner_name = "dir7";
                Files.createDirectory(subdir.resolve(inner_name));
            }
        }
        return top;
    }

    @Test
    @Ignore
    public void testStructure() {
        final Path top = setup();
        assertNotNull(top);
        assertTrue(Files.exists(top));
        assertTrue(Files.isDirectory(top));

        {//0 files, 2 subdirs
            final Path dir1 = top.resolve("dir1");
            assertNotNull(dir1);
            assertTrue(Files.exists(dir1));
            assertTrue(Files.isDirectory(dir1));

            { //3 files, 2 subdirs
                final Path dir2 = dir1.resolve("dir2");
                assertNotNull(dir2);
                assertTrue(Files.exists(dir2));
                assertTrue(Files.isDirectory(dir2));

                final Path file1 = dir2.resolve("file1.txt");
                assertNotNull(file1);
                assertTrue(Files.exists(file1));
                assertTrue(Files.isRegularFile(file1));

                final Path file2 = dir2.resolve("file2.txt");
                assertNotNull(file2);
                assertTrue(Files.exists(file2));
                assertTrue(Files.isRegularFile(file2));

                final Path file3 = dir2.resolve("file3.txt");
                assertNotNull(file3);
                assertTrue(Files.exists(file3));
                assertTrue(Files.isRegularFile(file3));

                { //empty
                    final Path dir3 = dir2.resolve("dir3");
                    assertNotNull(dir3);
                    assertTrue(Files.exists(dir3));
                    assertTrue(Files.isDirectory(dir3));
                }
                { //2 files
                    final Path dir4 = dir2.resolve("dir4");
                    assertNotNull(dir4);
                    assertTrue(Files.exists(dir4));
                    assertTrue(Files.isDirectory(dir4));

                    final Path file4 = dir4.resolve("file4.txt");
                    assertNotNull(file4);
                    assertTrue(Files.exists(file4));
                    assertTrue(Files.isRegularFile(file4));

                    final Path file5 = dir4.resolve("file4.txt");
                    assertNotNull(file5);
                    assertTrue(Files.exists(file5));
                    assertTrue(Files.isRegularFile(file5));
                }
            }
            {//1 file
                final Path dir5 = dir1.resolve("dir5");
                assertNotNull(dir5);
                assertTrue(Files.exists(dir5));
                assertTrue(Files.isDirectory(dir5));

                final Path file6 = dir5.resolve("file6.txt");
                assertNotNull(file6);
                assertTrue(Files.exists(file6));
                assertTrue(Files.isRegularFile(file6));
            }
        }
        {//1 file, 1 subdir
            final Path dir6 = top.resolve("dir6");
            assertNotNull(dir6);
            assertTrue(Files.exists(dir6));
            assertTrue(Files.isDirectory(dir6));

            final Path file7 = dir6.resolve("file7.txt");
            assertNotNull(file7);
            assertTrue(Files.exists(file7));
            assertTrue(Files.isRegularFile(file7));

            {//empty
                final Path dir7 = dir6.resolve("dir7");
                assertNotNull(dir7);
                assertTrue(Files.exists(dir7));
                assertTrue(Files.isDirectory(dir7));
            }
        }

        Files.walkFileTree(top, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                System.out.println("preVisitDirectory:" + dir.toString());
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println("visitFile:" + file.toString());
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                System.out.println("visitFileFailed:" + file.toString());
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                System.out.println("postVisitDirectory:" + dir.toString());
                if (exc == null) {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                } else {
                    throw exc;
                }
            }
        });

        assertFalse(Files.exists(top));
    }

    @Test
    @Ignore
    public void simpleWalkerOverUserDir() {

        final Path top = Paths.get(System.getProperty("user.home"));

        for (Path paths : top) {
            System.out.println("out:" + paths.toString());
        }

        Files.walkFileTree(top, null, 2, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                System.out.println("preVisitDirectory:" + dir.toUri().toString());
                System.out.println("preVisitDirectory:getFileName:" + dir.getFileName().toString());
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println("visitFile:" + file.toUri().toString());
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                System.out.println("visitFileFailed:" + file.toUri().toString());
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                System.out.println("postVisitDirectory:" + dir.toUri().toString());
                return FileVisitResult.CONTINUE;
            }
        });
    }

}
