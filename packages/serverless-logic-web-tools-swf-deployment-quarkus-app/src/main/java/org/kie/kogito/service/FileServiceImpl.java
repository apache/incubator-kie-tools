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

import io.quarkus.runtime.Startup;
import org.apache.commons.io.FileUtils;
import org.jboss.logging.Logger;
import org.kie.kogito.FileStructureConstants;
import org.kie.kogito.api.FileService;
import org.kie.kogito.api.FileValidation;
import org.kie.kogito.model.FileType;
import org.kie.kogito.validation.PropertiesValidation;
import org.kie.kogito.validation.ServerlessWorkflowValidation;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Startup
@ApplicationScoped
public class FileServiceImpl implements FileService {
    private static final Logger LOGGER = Logger.getLogger(FileService.class);

    private final Map<FileType, FileValidation> VALIDATION_MAP =
            Map.ofEntries(Map.entry(FileType.SERVERLESS_WORKFLOW, new ServerlessWorkflowValidation()),
                    Map.entry(FileType.APPLICATION_PROPERTIES, new PropertiesValidation()));

    @PostConstruct
    public void postConstruct() {
        LOGGER.info("PostConstruct");

        final Path workFolderPath = Paths.get(FileStructureConstants.WORK_FOLDER);
        final Path unzipFolder = Paths.get(FileStructureConstants.UNZIP_FOLDER);

        deleteDirectory(workFolderPath.toFile());
        deleteDirectory(unzipFolder.toFile());

        createFolder(workFolderPath.toString());
        createFolder(unzipFolder.toString());
    }

    @Override
    public void createFolder(final String folderPath) {
        LOGGER.info("Create folder: " + folderPath);
        final File folder = new File(folderPath);
        if (!folder.mkdirs()) {
            LOGGER.error("The folder could not be created on path " + folderPath);
        }
    }

    @Override
    public void cleanUpFolder(final String folderPath, final List<FileType> blockList) throws IOException {
        LOGGER.info("Clean up folder: " + folderPath + "...");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(folderPath))) {
            for (Path path : stream) {
                if (blockList.contains(getFileType(path))) {
                    continue;
                }
                final File file = path.toFile();
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    if (!file.delete()) {
                        LOGGER.warn("File cannot be deleted: " + path);
                    }
                }
            }
        }
        LOGGER.info("Clean up folder: " + folderPath + "... done");
    }

    @Override
    public FileType getFileType(final Path filePath) {
        final String fileName = filePath.getFileName().toString();
        if (fileName.endsWith(".sw.json") || fileName.endsWith(".sw.yaml") || fileName.endsWith(".sw.yml")) {
            return FileType.SERVERLESS_WORKFLOW;
        } else if (fileName.equals(FileStructureConstants.APPLICATION_PROPERTIES_FILE_NAME)) {
            return FileType.APPLICATION_PROPERTIES;
        }
        return FileType.UNKNOWN;
    }

    @Override
    public void deleteDirectory(final File file) {
        if (!file.exists()) {
            return;
        }
        final File[] childFiles = file.listFiles();
        if (childFiles != null) {
            for (File childFile : childFiles) {
                deleteDirectory(childFile);
            }
        }
        if (!file.delete()) {
            LOGGER.warn("Could not delete file at " + file.toPath());
        }
    }

    @Override
    public void mergePropertiesFiles(final String pathA, final String pathB, final String mergedPath) throws IOException {
        Properties properties = new Properties();

        try (var inputStream = new FileInputStream(pathA)) {
            properties.load(inputStream);
        }

        // Overwrite duplicates
        try (var inputStream = new FileInputStream(pathB)) {
            properties.load(inputStream);
        }

        try (var outputStream = new FileOutputStream(mergedPath)) {
            properties.store(outputStream, null);
        }
    }

    @Override
    public List<String> validateFiles(final List<String> filePaths) {
        LOGGER.info("Validate " + filePaths.size() + " incoming file(s) ...");

        List<String> validFilePaths = new ArrayList<>();
        for (String filePath : filePaths) {
            final Path path = Paths.get(filePath);
            try {
                final FileType fileType = getFileType(path);

                if (fileType == FileType.UNKNOWN) {
                    LOGGER.warn("Skipping upload of unsupported file " + path.getFileName());
                    continue;
                }

                if (VALIDATION_MAP.get(fileType).isValid(path)) {
                    validFilePaths.add(filePath);
                } else {
                    LOGGER.warn("Skipping upload of invalid file " + path.getFileName());
                }
            } catch (Exception e) {
                LOGGER.error("Error when validating file: " + e.getMessage());
            }
        }
        LOGGER.info("Validate " + filePaths.size() + " incoming file(s) ... done");
        return validFilePaths;
    }

    @Override
    public void copyResources(final List<String> filePaths) throws IOException {
        LOGGER.info("Copying resources ...");
        for (String filePath : filePaths) {
            final Path path = Paths.get(filePath);
            final FileType fileType = getFileType(path);

            final File source = path.toFile();
            final File target = Paths.get(FileStructureConstants.PROJECT_RESOURCES_FOLDER, source.getName()).toFile();

            if (fileType == FileType.APPLICATION_PROPERTIES) {
                mergePropertiesFiles(filePath, FileStructureConstants.APPLICATION_PROPERTIES_FILE_PATH, filePath);
            }

            LOGGER.info("Copy file: " + source.getPath() + " -> " + target.getPath());

            if (!source.exists()) {
                continue;
            }

            if (source.isDirectory()) {
                FileUtils.copyDirectory(source, target);
            } else {
                FileUtils.copyFile(source, target);
            }
        }
        LOGGER.info("Copying resources ... done");
    }
}
