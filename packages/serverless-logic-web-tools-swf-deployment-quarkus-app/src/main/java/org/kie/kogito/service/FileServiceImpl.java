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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.io.FileUtils;
import org.jboss.logging.Logger;
import org.kie.kogito.FileStructureConstants;
import org.kie.kogito.api.FileService;
import org.kie.kogito.api.FileValidation;
import org.kie.kogito.model.FileType;
import org.kie.kogito.model.FileValidationResult;
import org.kie.kogito.validation.OpenApiValidation;
import org.kie.kogito.validation.PropertiesValidation;
import org.kie.kogito.validation.ServerlessWorkflowValidation;

@ApplicationScoped
public class FileServiceImpl implements FileService {

    private static final Logger LOGGER = Logger.getLogger(FileService.class);

    private static final String SW_REGEX = ".*\\.sw\\.(json|ya?ml)";
    private static final String SPEC_REGEX = ".*\\.(json|ya?ml)";

    private final Map<FileType, FileValidation> VALIDATION_MAP =
            Map.ofEntries(Map.entry(FileType.SERVERLESS_WORKFLOW, new ServerlessWorkflowValidation()),
                          Map.entry(FileType.APPLICATION_PROPERTIES, new PropertiesValidation()),
                          Map.entry(FileType.SPEC, new OpenApiValidation()));

    @Override
    public void createFolder(final Path folderPath) {
        if (exists(folderPath)) {
            return;
        }
        LOGGER.info("Create folder: " + folderPath);
        if (!folderPath.toFile().mkdirs()) {
            LOGGER.error("The folder could not be created on path " + folderPath);
        }
    }

    @Override
    public void cleanUpFolder(final Path folderPath) throws IOException {
        if (!exists(folderPath)) {
            return;
        }
        LOGGER.info("Clean up folder: " + folderPath + "...");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folderPath)) {
            for (Path path : stream) {
                final File file = path.toFile();
                if (file.isDirectory()) {
                    deleteFolder(path);
                } else if (!file.delete()) {
                    LOGGER.warn("File cannot be deleted: " + path);
                }
            }
        }
        LOGGER.info("Clean up folder: " + folderPath + "... done");
    }

    @Override
    public FileType getFileType(final Path filePath) {
        final String fileName = filePath.getFileName().toString();
        if (Pattern.matches(SW_REGEX, fileName)) {
            return FileType.SERVERLESS_WORKFLOW;
        } else if (fileName.equals(FileStructureConstants.APPLICATION_PROPERTIES_FILE_NAME)) {
            return FileType.APPLICATION_PROPERTIES;
        } else if (Pattern.matches(SPEC_REGEX, fileName)) {
            return FileType.SPEC;
        }
        return FileType.UNKNOWN;
    }

    @Override
    public void deleteFolder(final Path folderPath) {
        final File folder = folderPath.toFile();
        if (!folder.exists()) {
            return;
        }
        final File[] childFiles = folder.listFiles();
        if (childFiles != null) {
            for (File childFile : childFiles) {
                deleteFolder(childFile.toPath());
            }
        }
        if (!folder.delete()) {
            LOGGER.warn("Could not delete file at " + folder.toPath());
        }
    }

    @Override
    public void mergePropertiesFiles(final Path pathA, final Path pathB, final Path mergedPath) throws IOException {
        Properties properties = new Properties();

        try (var inputStream = new FileInputStream(pathA.toFile())) {
            properties.load(inputStream);
        }

        // Overwrite duplicates
        try (var inputStream = new FileInputStream(pathB.toFile())) {
            properties.load(inputStream);
        }

        try (var outputStream = new FileOutputStream(mergedPath.toFile())) {
            properties.store(outputStream, null);
        }
    }

    @Override
    public List<FileValidationResult> validateFiles(final List<Path> filePaths) {
        LOGGER.info("Validate " + filePaths.size() + " incoming file(s) ...");

        final List<Path> supportedFiles = filePaths
                .stream()
                .filter(path -> getFileType(path) != FileType.UNKNOWN)
                .collect(Collectors.toList());

        if (supportedFiles.isEmpty()) {
            LOGGER.warn("No supported files have been found to validate");
            return Collections.emptyList();
        }

        LOGGER.info(supportedFiles.size() + " supported file(s) have been found to be validated");
        List<FileValidationResult> results = new ArrayList<>();
        for (Path filePath : supportedFiles) {
            LOGGER.info("Validating file '" + filePath + "'...");
            final FileType fileType = getFileType(filePath);
            results.add(VALIDATION_MAP.get(fileType).isValid(filePath));
        }
        LOGGER.info("Validate " + filePaths.size() + " incoming file(s) ... done");

        return results;
    }

    @Override
    public void copyFiles(final Map<Path, Path> sourceTargetMap) throws IOException {
        LOGGER.info("Copying resources ...");
        for (var entry : sourceTargetMap.entrySet()) {
            final File source = entry.getKey().toFile();
            final File target = entry.getValue().toFile();

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

    @Override
    public boolean exists(final Path path) {
        return path.toFile().exists();
    }
}
