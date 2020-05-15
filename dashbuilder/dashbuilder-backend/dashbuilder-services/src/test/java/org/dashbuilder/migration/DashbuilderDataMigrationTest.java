/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dashbuilder.migration;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.io.IOService;
import org.uberfire.io.impl.IOServiceDotFileImpl;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileSystemMetadata;
import org.uberfire.java.nio.file.FileVisitResult;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.SimpleFileVisitor;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DashbuilderDataMigrationTest {

    private DashbuilderDataMigration dashbuilderDataMigration;
    private IOService ioService;
    private FileSystem sourceFS;
    private FileSystem targetFS;

    @Before
    public void setup() {
        ioService = new IOServiceDotFileImpl();

        sourceFS = createFileSystem();
        targetFS = createFileSystem();

        dashbuilderDataMigration = spy(
                new DashbuilderDataMigration(
                        ioService,
                        null,
                        null,
                        null,
                        null));
    }

    @Test
    public void testMigrateNull() {
        checkInitialCondition();

        dashbuilderDataMigration.migrateDatasets(null, null);
        dashbuilderDataMigration.migratePerspectives(null, null);
        dashbuilderDataMigration.migrateNavigation(null, null);

        dashbuilderDataMigration.migrateDatasets(sourceFS, null);
        checkInitialCondition();
        dashbuilderDataMigration.migrateDatasets(null, targetFS);
        checkInitialCondition();

        dashbuilderDataMigration.migratePerspectives(sourceFS, null);
        checkInitialCondition();
        dashbuilderDataMigration.migratePerspectives(null, targetFS);
        checkInitialCondition();

        dashbuilderDataMigration.migrateNavigation(sourceFS, null);
        checkInitialCondition();
        dashbuilderDataMigration.migrateNavigation(null, targetFS);
        checkInitialCondition();
    }

    @Test
    public void testGitIsDefaultFileSystem() {
        doNothing().when(dashbuilderDataMigration).runWithLock(any());
        doReturn(true).when(dashbuilderDataMigration).isMigrationEnabled();
        dashbuilderDataMigration.init();
        verify(dashbuilderDataMigration, times(1)).runWithLock(any());
    }

    @Test
    public void testGitIsNotDefaultFileSystem() {


        doReturn(false).when(dashbuilderDataMigration).isMigrationEnabled();
        dashbuilderDataMigration.init();
        verify(dashbuilderDataMigration, never()).runWithLock(any());
    }

    @Test
    public void testMigrateEmpty() {
        checkInitialCondition();

        dashbuilderDataMigration.migrateDatasets(sourceFS, targetFS);
        checkInitialCondition();

        dashbuilderDataMigration.migratePerspectives(sourceFS, targetFS);
        checkInitialCondition();

        dashbuilderDataMigration.migrateNavigation(sourceFS, targetFS);
        checkInitialCondition();
    }

    @Test
    public void testMigrateDatasets() {
        checkInitialCondition();

        createFiles(sourceFS);

        dashbuilderDataMigration.migrateDatasets(sourceFS, targetFS);

        List<String> sourceFiles = getFiles(sourceFS);
        List<String> targetFiles = getFiles(targetFS);

        assertEquals(1, sourceFiles.size());
        assertEquals(8, targetFiles.size());
        assertEquals(new ArrayList<String>() {{
            add("/readme.md");
        }}, sourceFiles);
        assertEquals(new ArrayList<String>() {{
            add("/bbb.txt");
            add("/dataset1.csv");
            add("/definitions/dataset2.dset");
            add("/navtree.json");
            add("/page-abc/perspective_layout.json");
            add("/perspective_layout2.txt");
            add("/perspective_layouts/aaa.txt");
            add("/readme.md");
        }}, targetFiles);
    }

    @Test
    public void testMigratePerspectives() {
        checkInitialCondition();

        createFiles(sourceFS);

        dashbuilderDataMigration.migratePerspectives(sourceFS, targetFS);

        List<String> sourceFiles = getFiles(sourceFS);
        List<String> targetFiles = getFiles(targetFS);

        assertEquals(6, sourceFiles.size());
        assertEquals(3, targetFiles.size());
        assertEquals(new ArrayList<String>() {{
            add("/bbb.txt");
            add("/dataset1.csv");
            add("/definitions/dataset2.dset");
            add("/navtree.json");
            add("/perspective_layouts/aaa.txt");
            add("/readme.md");
        }}, sourceFiles);
        assertEquals(new ArrayList<String>() {{
            add("/page-abc/perspective_layout.json");
            add("/perspective_layout2.txt");
            add("/readme.md");
        }}, targetFiles);
    }

    @Test
    public void testMigrateNavigation() {
        checkInitialCondition();

        createFiles(sourceFS);

        dashbuilderDataMigration.migrateNavigation(sourceFS, targetFS);

        List<String> sourceFiles = getFiles(sourceFS);
        List<String> targetFiles = getFiles(targetFS);

        assertEquals(7, sourceFiles.size());
        assertEquals(2, targetFiles.size());
        assertEquals(new ArrayList<String>() {{
            add("/bbb.txt");
            add("/dataset1.csv");
            add("/definitions/dataset2.dset");
            add("/page-abc/perspective_layout.json");
            add("/perspective_layout2.txt");
            add("/perspective_layouts/aaa.txt");
            add("/readme.md");
        }}, sourceFiles);
        assertEquals(new ArrayList<String>() {{
            add("/navtree.json");
            add("/readme.md");
        }}, targetFiles);
    }

    private void checkInitialCondition() {
        List<String> sourceFiles = getFiles(sourceFS);
        List<String> targetFiles = getFiles(targetFS);

        assertEquals(1, sourceFiles.size());
        assertEquals(1, targetFiles.size());
        assertEquals(new ArrayList<String>() {{
            add("/readme.md");
        }}, sourceFiles);
        assertEquals(new ArrayList<String>() {{
            add("/readme.md");
        }}, targetFiles);
    }

    private List<String> getFiles(FileSystem fs) {
        List<String> files = new ArrayList<>();
        Path root = fs.getRootDirectories().iterator().next();

        Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(final Path path, final BasicFileAttributes attrs) throws IOException {
                files.add(path.toString());
                return FileVisitResult.CONTINUE;
            }
        });

        return files;
    }

    private void createFiles(FileSystem fs) {
        Path path = fs.getRootDirectories().iterator().next();
        ioService.write(path.resolve("dataset1.csv"), "test");
        ioService.write(path.resolve("definitions").resolve("dataset2.dset"), "test");
        ioService.write(path.resolve("navtree.json"), "test");
        ioService.write(path.resolve("perspective_layouts").resolve("aaa.txt"), "test");
        ioService.write(path.resolve("bbb.txt"), "test");
        ioService.write(path.resolve("page-abc").resolve("perspective_layout.json"), "test");
        ioService.write(path.resolve("perspective_layout2.txt"), "test");
    }

    private FileSystem createFileSystem() {
        return ioService.newFileSystem(
                URI.create("git://migration/temp" + new Date().getTime()),
                new HashMap<String, Object>() {{
                    put("init", Boolean.TRUE);
                }});
    }

    @After
    public void cleanup() {
        for (FileSystemMetadata fsm : ioService.getFileSystemMetadata()) {
            URI uri = URI.create(fsm.getUri());
            if (uri.getScheme().equals("git")) {
                JGitFileSystem fs = (JGitFileSystem) ioService.getFileSystem(uri);
                fs.getGit().getRepository().getDirectory().delete();
                deleteFileSystem(fs);
            }
        }
    }

    private void deleteFileSystem(FileSystem fs) {
        try {
            FileUtils.deleteDirectory(
                    ((JGitFileSystem) fs).getGit()
                            .getRepository()
                            .getDirectory()
                            .getAbsoluteFile()
                            .getParentFile());
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}
