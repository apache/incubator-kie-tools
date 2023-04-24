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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.logging.Logger;
import org.kie.kogito.FileStructureConstants;
import org.kie.kogito.api.FileService;
import org.kie.kogito.api.UploadService;
import org.kie.kogito.api.ZipService;
import org.kie.kogito.model.FileType;
import org.kie.kogito.model.FileValidationResult;
import org.kie.kogito.model.UploadException;
import org.kie.kogito.model.ValidationException;

@ApplicationScoped
public class UploadServiceImpl implements UploadService {

    private static final Logger LOGGER = Logger.getLogger(UploadService.class);

    @Inject
    ZipService zipService;

    @Inject
    FileService fileService;

    @Override
    public List<String> upload(final InputStream inputStream) throws IOException, UploadException, ValidationException {
        LOGGER.info("Upload files ...");
        Files.copy(inputStream, FileStructureConstants.UPLOADED_ZIP_FILE_PATH, StandardCopyOption.REPLACE_EXISTING);

        final List<Path> unzippedFilePaths = zipService.unzip(FileStructureConstants.UPLOADED_ZIP_FILE_PATH,
                                                              FileStructureConstants.UNZIP_FOLDER_PATH);

        if (unzippedFilePaths.isEmpty()) {
            throw new UploadException("No file has been found in the provided zip file.");
        }

        final List<FileValidationResult> fileValidationResults = fileService.validateFiles(unzippedFilePaths);

        final List<String> errors = fileValidationResults
                .stream()
                .filter(r -> !r.isValid())
                .map(r -> r.getErrorMessage() + " (" + r.getFilePath().getFileName() + ")")
                .collect(Collectors.toList());

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        final boolean hasAnySwf = fileValidationResults
                .stream()
                .anyMatch(r -> fileService.getFileType(r.getFilePath()) == FileType.SERVERLESS_WORKFLOW);

        if (!hasAnySwf) {
            throw new UploadException("No valid Serverless Workflow file has been found.");
        }

        LOGGER.info("Uploading " + fileValidationResults.size() + " validated file(s).");

        fileService.cleanUpFolder(FileStructureConstants.PROJECT_RESOURCES_FOLDER_PATH);

        final Map<Path, Path> sourceTargetMap = fileValidationResults.stream()
                .map(FileValidationResult::getFilePath)
                .filter(path -> !path.getFileName().toString().equals(FileStructureConstants.APPLICATION_PROPERTIES_FILE_NAME))
                .collect(Collectors.toMap(
                        Function.identity(),
                        path -> {
                            final String relativePathStr =
                                    path.toString().replace(FileStructureConstants.UNZIP_FOLDER_PATH.toString(), "");
                            return Path.of(FileStructureConstants.PROJECT_RESOURCES_FOLDER_PATH.toString(), relativePathStr);
                        }));

        final Optional<Path> applicationPropertiesPath = fileValidationResults
                .stream()
                .map(FileValidationResult::getFilePath)
                .filter(path -> fileService.getFileType(path) == FileType.APPLICATION_PROPERTIES)
                .findFirst();

        if (applicationPropertiesPath.isPresent()) {
            Path mergedPath = Files.createTempFile("merged", FileStructureConstants.APPLICATION_PROPERTIES_FILE_NAME);
            fileService.mergePropertiesFiles(applicationPropertiesPath.get(),
                                             FileStructureConstants.BACKUP_APPLICATION_PROPERTIES_FILE_PATH,
                                             mergedPath);
            sourceTargetMap.put(mergedPath,
                                FileStructureConstants.APPLICATION_PROPERTIES_FILE_PATH);
            LOGGER.info("Merging incoming application.properties with default file.");
        } else {
            sourceTargetMap.put(FileStructureConstants.BACKUP_APPLICATION_PROPERTIES_FILE_PATH,
                                FileStructureConstants.APPLICATION_PROPERTIES_FILE_PATH);
            LOGGER.info("Using default application.properties file since no one was sent.");
        }

        fileService.copyFiles(sourceTargetMap);

        fileService.cleanUpFolder(FileStructureConstants.UNZIP_FOLDER_PATH);

        LOGGER.info("Upload files ... done");

        return fileValidationResults
                .stream()
                .map(r -> r.getFilePath()
                        .toAbsolutePath()
                        .toString()
                        .replace(FileStructureConstants.UNZIP_FOLDER_PATH + "/", ""))
                .collect(Collectors.toList());
    }
}
