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

import org.jboss.logging.Logger;
import org.kie.kogito.FileStructureConstants;
import org.kie.kogito.api.FileService;
import org.kie.kogito.api.UploadService;
import org.kie.kogito.api.ZipService;
import org.kie.kogito.model.FileType;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.*;
import java.nio.file.*;
import java.util.*;

@ApplicationScoped
public class UploadServiceImpl implements UploadService {
    private static final Logger LOGGER = Logger.getLogger(UploadService.class);

    @Inject
    ZipService zipService;

    @Inject
    FileService fileService;

    @Override
    public void upload(final InputStream inputStream) {
        LOGGER.info("Upload files ...");
        final Path zipPath = Paths.get(FileStructureConstants.UPLOADED_ZIP_FILE_PATH);
        try {
            Files.copy(inputStream, zipPath, StandardCopyOption.REPLACE_EXISTING);

            final List<String> unzippedFilePaths = zipService.unzip(zipPath.toString(),
                    FileStructureConstants.UNZIP_FOLDER);
            final List<String> validFilePaths = fileService.validateFiles(unzippedFilePaths);

            if (validFilePaths.isEmpty()) {
                LOGGER.warn("No valid file has been found. Upload skipped.");
                return;
            }

            final boolean hasAnySwf = validFilePaths
                    .stream()
                    .map(Paths::get)
                    .anyMatch(path -> fileService.getFileType(path) == FileType.SERVERLESS_WORKFLOW);

            if (!hasAnySwf) {
                LOGGER.warn("No valid serverless workflow file has been found. Upload skipped.");
                return;
            }

            LOGGER.info("Uploading " + validFilePaths.size() + " validated file(s).");

            fileService.cleanUpFolder(FileStructureConstants.PROJECT_RESOURCES_FOLDER);

            final Optional<Path> applicationPropertiesPath = validFilePaths
                    .stream()
                    .map(Paths::get)
                    .filter(path -> fileService.getFileType(path) == FileType.APPLICATION_PROPERTIES)
                    .findFirst();

            if (applicationPropertiesPath.isPresent()) {
                fileService.mergePropertiesFiles(applicationPropertiesPath.get().toString(),
                                                 FileStructureConstants.BACKUP_APPLICATION_PROPERTIES_FILE_PATH,
                                                 applicationPropertiesPath.get().toString());
                LOGGER.info("Merging incoming application.properties with default file.");
            } else {
                validFilePaths.add(FileStructureConstants.BACKUP_APPLICATION_PROPERTIES_FILE_PATH);
                LOGGER.info("Using default application.properties file since no one was sent.");
            }

            fileService.copyResources(validFilePaths);

            fileService.cleanUpFolder(FileStructureConstants.UNZIP_FOLDER);

            LOGGER.info("Upload files ... done");
        } catch (Exception e) {
            LOGGER.error("Error when processing the uploaded file", e);
        }
    }
}