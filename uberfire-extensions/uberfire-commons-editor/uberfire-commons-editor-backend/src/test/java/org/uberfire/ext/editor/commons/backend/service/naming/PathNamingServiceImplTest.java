/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.editor.commons.backend.service.naming;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.ext.editor.commons.service.PathNamingService;
import org.uberfire.mocks.FileSystemTestingUtils;
import org.uberfire.workbench.category.Category;
import org.uberfire.workbench.category.Others;
import org.uberfire.workbench.type.ResourceTypeDefinition;

import static org.junit.Assert.*;

public class PathNamingServiceImplTest {

    private static final String PATH_PREFIX = "git://amend-repo-test/";

    private static FileSystemTestingUtils fileSystemTestingUtils = new FileSystemTestingUtils();

    private PathNamingService pathNamingService;

    private Collection<ResourceTypeDefinition> resourceTypeDefinitions;

    @Before
    public void setup() throws IOException {
        fileSystemTestingUtils.setup();
        resourceTypeDefinitions = createResourceTypeDefinitions();
        pathNamingService = createPathNamingService();
    }

    @After
    public void cleanupFileSystem() {
        fileSystemTestingUtils.cleanup();
    }

    @Test
    public void buildTargetPathForFolderInTheSameDirectoryTest() {
        assertEquals("newFolderName",
                     targetFolderName("originalFolderName",
                                      "newFolderName"));
        assertEquals("newFolderName",
                     targetFolderName("original.folder.name",
                                      "newFolderName"));
        assertEquals("newFolderName",
                     targetFolderName("originalFolder.name",
                                      "newFolderName"));
        assertEquals("new.folder.name",
                     targetFolderName("originalFolderName",
                                      "new.folder.name"));
        assertEquals("new.folder.name",
                     targetFolderName("original.folder.name",
                                      "new.folder.name"));
        assertEquals("new.folder.name",
                     targetFolderName("originalFolder.name",
                                      "new.folder.name"));
        assertEquals("newFolder.name",
                     targetFolderName("originalFolderName",
                                      "newFolder.name"));
        assertEquals("newFolder.name",
                     targetFolderName("original.folder.name",
                                      "newFolder.name"));
        assertEquals("newFolder.name",
                     targetFolderName("originalFolder.name",
                                      "newFolder.name"));
    }

    @Test
    public void buildTargetPathForFileInTheSameDirectoryTest() {
        assertEquals("newFileName",
                     targetFileName("originalFileName",
                                    "newFileName"));
        assertEquals("newFileName.extension2",
                     targetFileName("originalFileName.extension1.extension2",
                                    "newFileName"));
        assertEquals("newFileName.extension",
                     targetFileName("originalFileName.extension",
                                    "newFileName"));
        assertEquals("newFileName.extension1.extension2",
                     targetFileName("originalFileName",
                                    "newFileName.extension1.extension2"));
        assertEquals("newFileName.extension1.extension2.extension2",
                     targetFileName("originalFileName.extension1.extension2",
                                    "newFileName.extension1.extension2"));
        assertEquals("newFileName.extension1.extension2.extension",
                     targetFileName("originalFileName.extension",
                                    "newFileName.extension1.extension2"));
        assertEquals("newFileName.extension",
                     targetFileName("originalFileName",
                                    "newFileName.extension"));
        assertEquals("newFileName.extension.extension2",
                     targetFileName("originalFileName.extension1.extension2",
                                    "newFileName.extension"));
        assertEquals("newFileName.extension.extension",
                     targetFileName("originalFileName.extension",
                                    "newFileName.extension"));
    }

    @Test
    public void buildTargetPathForResourceTypeFileInTheSameDirectoryTest() {
        assertEquals("newFileName.resource",
                     targetFileName("originalFileName.resource",
                                    "newFileName"));
        assertEquals("newFileName.resource.xml",
                     targetFileName("originalFileName.resource.xml",
                                    "newFileName"));
        assertEquals("newFileName.resource.xml.txt",
                     targetFileName("originalFileName.resource.xml.txt",
                                    "newFileName"));
    }

    @Test
    public void buildTargetPathForFolderInAnotherDirectoryTest() {
        Path originalPath = createFolder("parent/folder");
        Path targetParentDirectory = createFolder("new-parent");
        String targetFileName = "new-folder";

        Path targetPath = pathNamingService.buildTargetPath(originalPath,
                                                            targetParentDirectory,
                                                            targetFileName);

        assertEquals(targetParentDirectory.toURI() + "/" + targetFileName,
                     targetPath.toURI());
    }

    @Test
    public void buildTargetPathForFileInAnotherDirectoryTest() {
        String extension = ".txt";

        Path originalPath = createFile("parent/file" + extension);
        Path targetParentDirectory = createFolder("new-parent");
        String targetFileName = "new-file";

        Path targetPath = pathNamingService.buildTargetPath(originalPath,
                                                            targetParentDirectory,
                                                            targetFileName);

        assertEquals(targetParentDirectory.toURI() + "/" + targetFileName + extension,
                     targetPath.toURI());
    }

    @Test
    public void buildTargetPathForResourceTypeFileInAnotherDirectoryTest() {
        String extension = ".resource.xml.txt";

        Path originalPath = createFile("parent/resource-file" + extension);
        Path targetParentDirectory = createFolder("new-parent");
        String targetFileName = "new-resource-file";

        Path targetPath = pathNamingService.buildTargetPath(originalPath,
                                                            targetParentDirectory,
                                                            targetFileName);

        assertEquals(targetParentDirectory.toURI() + "/" + targetFileName + extension,
                     targetPath.toURI());
    }

    private Path createFolder(final String folderName) {
        return Paths.convert(Paths.convert(PathFactory.newPath("file",
                                                               PATH_PREFIX + folderName + "/file")).getParent());
    }

    private Path createFile(final String fileName) {
        return PathFactory.newPath(fileName,
                                   PATH_PREFIX + fileName);
    }

    private String targetFolderName(final String originalFolderName,
                                    final String newFolderName) {
        final Path path = PathFactory.newPath("file",
                                              PATH_PREFIX + originalFolderName + "/file");
        fileSystemTestingUtils.getIoService().write(Paths.convert(path),
                                                    "content");
        return pathNamingService.buildTargetPath(Paths.convert(Paths.convert(path).getParent()),
                                                 newFolderName).getFileName();
    }

    private String targetFileName(final String originalFileName,
                                  final String newFileName) {
        final Path path = PathFactory.newPath(originalFileName,
                                              PATH_PREFIX + originalFileName);
        fileSystemTestingUtils.getIoService().write(Paths.convert(path),
                                                    "content");
        return pathNamingService.buildTargetPath(path,
                                                 newFileName).getFileName();
    }

    private PathNamingService createPathNamingService() {
        return new PathNamingServiceImpl() {

            @Override
            public Iterable<ResourceTypeDefinition> getResourceTypeDefinitions() {
                return resourceTypeDefinitions;
            }
        };
    }

    private Collection<ResourceTypeDefinition> createResourceTypeDefinitions() {
        List<ResourceTypeDefinition> resourceTypeDefinitions = new ArrayList<>();

        resourceTypeDefinitions.add(createResourceTypeDefinition("resource"));
        resourceTypeDefinitions.add(createResourceTypeDefinition("resource.xml"));
        resourceTypeDefinitions.add(createResourceTypeDefinition("resource.xml.txt"));

        return resourceTypeDefinitions;
    }

    private ResourceTypeDefinition createResourceTypeDefinition(String suffix) {
        return new ResourceTypeDefinition() {
            @Override
            public String getShortName() {
                return null;
            }

            @Override
            public String getDescription() {
                return null;
            }

            @Override
            public String getPrefix() {
                return null;
            }

            @Override
            public String getSuffix() {
                return suffix;
            }

            @Override
            public int getPriority() {
                return 0;
            }

            @Override
            public String getSimpleWildcardPattern() {
                return null;
            }

            @Override
            public boolean accept(final org.uberfire.backend.vfs.Path path) {
                return false;
            }

            @Override
            public Category getCategory() {
                return new Others();
            }
        };
    }
}
