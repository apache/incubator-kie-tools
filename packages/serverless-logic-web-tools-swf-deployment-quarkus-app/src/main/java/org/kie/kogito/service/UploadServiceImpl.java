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
        final Path zipPath = Paths.get(FileStructureConstants.WORK_FOLDER,
                FileStructureConstants.UPLOADED_ZIP_FILE);
        try {
            Files.copy(inputStream, zipPath, StandardCopyOption.REPLACE_EXISTING);
            final List<String> unzippedFilePaths = zipService.unzip(zipPath.toString(),
                    FileStructureConstants.UNZIP_FOLDER);
            final List<String> validFilePaths = fileService.validateFiles(unzippedFilePaths);

            if (validFilePaths.isEmpty()) {
                LOGGER.warn("No valid file has been found. Upload skipped.");
                return;
            }

            LOGGER.info("Uploading " + validFilePaths.size() + " validated file(s).");

            var blockList = new ArrayList<FileType>();
            blockList.add(FileType.APPLICATION_PROPERTIES);

            final boolean hasAnySwf = validFilePaths
                    .stream()
                    .map(Paths::get)
                    .map(path -> fileService.getFileType(path))
                    .anyMatch(t -> t == FileType.SERVERLESS_WORKFLOW);

            if (!hasAnySwf) {
                blockList.add(FileType.SERVERLESS_WORKFLOW);
            }

            fileService.cleanUpFolder(FileStructureConstants.PROJECT_RESOURCES_FOLDER, blockList);

            fileService.copyResources(validFilePaths);

            fileService.cleanUpFolder(FileStructureConstants.UNZIP_FOLDER, Collections.emptyList());

            if (!zipPath.toFile().delete()) {
                LOGGER.warn("Could not delete file at " + zipPath);
            }

            LOGGER.info("Upload files ... done");
        } catch (Exception e) {
            LOGGER.error("Error when processing the uploaded file", e);
        }
    }
}