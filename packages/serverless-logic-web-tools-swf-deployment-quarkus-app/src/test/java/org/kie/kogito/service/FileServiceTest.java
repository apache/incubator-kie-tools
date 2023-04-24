/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.api.FileService;
import org.kie.kogito.model.FileType;
import org.kie.kogito.model.FileValidationResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class FileServiceTest {

    private FileService fileService;
    private Path tempFolder;

    @BeforeEach
    void setUp() throws IOException {
        fileService = new FileServiceImpl();
        tempFolder = Files.createTempDirectory("fileServiceTest");
    }

    @AfterEach
    void tearDown() {
        fileService.deleteFolder(tempFolder);
    }

    @Test
    void testCreateFolder() {
        Path folderPath = tempFolder.resolve("myFolder");

        fileService.createFolder(folderPath);

        assertTrue(Files.exists(folderPath));
    }

    @Test
    void testCleanUpFolder() throws IOException {
        Path folderPath = tempFolder.resolve("myFolder");
        fileService.createFolder(folderPath);
        Files.createFile(folderPath.resolve("foo.txt"));
        Files.createFile(folderPath.resolve("bar.txt"));
        Files.createFile(folderPath.resolve("baz.txt"));

        try (Stream<Path> files = Files.list(folderPath)) {
            assertEquals(3, files.count());
        }

        fileService.cleanUpFolder(folderPath);

        try (Stream<Path> files = Files.list(folderPath)) {
            assertEquals(0, files.count());
        }
    }

    @Test
    void testGetFileType() {
        Map<Path, FileType> pathFileTypeMap =
                Map.ofEntries(Map.entry(tempFolder.resolve("file.txt"), FileType.UNKNOWN),
                              Map.entry(tempFolder.resolve("model.sw.json"), FileType.SERVERLESS_WORKFLOW),
                              Map.entry(tempFolder.resolve("model.sw.yaml"), FileType.SERVERLESS_WORKFLOW),
                              Map.entry(tempFolder.resolve("model.sw.yml"), FileType.SERVERLESS_WORKFLOW),
                              Map.entry(tempFolder.resolve("application.properties"), FileType.APPLICATION_PROPERTIES),
                              Map.entry(tempFolder.resolve("specs/api.yaml"), FileType.SPEC),
                              Map.entry(tempFolder.resolve("api.json"), FileType.SPEC),
                              Map.entry(tempFolder.resolve("api/spec.yml"), FileType.SPEC));

        for (var entry : pathFileTypeMap.entrySet()) {
            FileType actualType = fileService.getFileType(entry.getKey());
            assertEquals(entry.getValue(), actualType);
        }
    }

    @Test
    void testDeleteFolder() {
        Path folderPath = tempFolder.resolve("myFolder");
        fileService.createFolder(folderPath);

        fileService.deleteFolder(folderPath);

        assertFalse(Files.exists(folderPath));
    }

    @Test
    void testMergePropertiesFiles() throws IOException {
        Path fileA = Files.createFile(tempFolder.resolve("fileA.properties"));
        Path fileB = Files.createFile(tempFolder.resolve("fileB.properties"));
        Path mergedPath = tempFolder.resolve("merged.properties");

        Properties fileAProps = new Properties();
        fileAProps.setProperty("customKey1", "customValue1");
        fileAProps.setProperty("customKey2", "customValue2");
        fileAProps.setProperty("defaultKey1", "customValue");
        try (var outputStream = new FileOutputStream(fileA.toFile())) {
            fileAProps.store(outputStream, null);
        }

        Properties fileBProps = new Properties();
        fileBProps.setProperty("defaultKey1", "defaultValue1");
        fileBProps.setProperty("defaultKey2", "defaultValue2");
        try (var outputStream = new FileOutputStream(fileB.toFile())) {
            fileBProps.store(outputStream, null);
        }

        fileService.mergePropertiesFiles(fileA, fileB, mergedPath);

        Properties mergedProps = new Properties();
        try (var inputStream = new FileInputStream(mergedPath.toFile())) {
            mergedProps.load(inputStream);

            assertTrue(Files.exists(mergedPath));
            assertEquals(4, mergedProps.size());
            assertEquals("customValue1", mergedProps.getProperty("customKey1"));
            assertEquals("customValue2", mergedProps.getProperty("customKey2"));
            assertEquals("defaultValue1", mergedProps.getProperty("defaultKey1"));
            assertEquals("defaultValue2", mergedProps.getProperty("defaultKey2"));
        }
    }

    @Test
    void testValidateFiles() throws IOException {
        Path zipFilePath = Path.of("src/test/resources/test.zip");
        Path destinationFolderPath = Files.createTempDirectory("unzip");
        List<Path> unzippedFilePaths = new ZipServiceImpl().unzip(zipFilePath, destinationFolderPath);

        List<FileValidationResult> results = fileService.validateFiles(unzippedFilePaths);
        assertEquals(4, unzippedFilePaths.size());
        assertEquals(3, results.size());
        assertEquals(3, (int) results.stream().filter(FileValidationResult::isValid).count());
    }

    @Test
    void testValidateFilesWithInvalid() throws IOException {
        Path zipFilePath = Path.of("src/test/resources/test-with-invalid.zip");
        Path destinationFolderPath = Files.createTempDirectory("unzip");
        List<Path> unzippedFilePaths = new ZipServiceImpl().unzip(zipFilePath, destinationFolderPath);

        List<FileValidationResult> results = fileService.validateFiles(unzippedFilePaths);
        assertEquals(5, unzippedFilePaths.size());
        assertEquals(4, results.size());
        assertEquals(3, (int) results.stream().filter(FileValidationResult::isValid).count());
    }

    @Test
    void testCopyFiles() throws IOException {
        Path subFolders = tempFolder.resolve("foo/bar/baz");
        Path modelFilePath = subFolders.resolve("model.sw.json");
        Files.createFile(Files.createDirectories(subFolders).resolve(modelFilePath.getFileName()));
        Path propertiesFilePath = Files.createFile(tempFolder.resolve("application.properties"));

        Path destinationFolder = Files.createTempDirectory("destination");
        Path modelDestinationPath = destinationFolder.resolve("foo/bar/baz/model.sw.json");
        Path propertiesDestinationPath = destinationFolder.resolve("application.properties");

        Map<Path, Path> sourceTargetMap = new HashMap<>();
        sourceTargetMap.put(modelFilePath, modelDestinationPath);
        sourceTargetMap.put(propertiesFilePath, propertiesDestinationPath);

        fileService.copyFiles(sourceTargetMap);

        assertTrue(Files.exists(destinationFolder.resolve("foo/bar/baz/model.sw.json")));
        assertTrue(Files.exists(destinationFolder.resolve(propertiesFilePath.getFileName())));
    }

    @Test
    void testExists() {
        Path path = tempFolder.resolve("file.txt");

        assertFalse(fileService.exists(path));

        try {
            Files.createFile(path);
        } catch (IOException e) {
            fail("Failed to create test file");
        }

        assertTrue(fileService.exists(path));
    }
}
