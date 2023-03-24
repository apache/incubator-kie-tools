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
import io.serverlessworkflow.api.Workflow;
import org.apache.commons.io.FileUtils;
import org.jboss.logging.Logger;
import org.kie.kogito.model.FileType;
import org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.lang.model.SourceVersion;
import java.io.*;
import java.nio.file.*;
import java.util.*;

// Very simple logic since this code will be thrown away
@Startup
@ApplicationScoped
public class UploadServiceImpl implements UploadService {

    private static final Logger LOGGER = Logger.getLogger(UploadServiceImpl.class);
    private static final String WORK_FOLDER = "/tmp/serverless-logic";
    private static final String UNZIP_FOLDER = Paths.get(WORK_FOLDER, "unzip").toString();
    private static final String RESOURCES_FOLDER = "src/main/resources";
    private static final String UPLOADED_ZIP_FILE = "file.zip";

    private static final String APPLICATION_PROPERTIES_FILE_NAME = "application.properties";
    private static final String APPLICATION_PROPERTIES_FILE_PATH = Paths.get(RESOURCES_FOLDER, APPLICATION_PROPERTIES_FILE_NAME).toString();

    @Inject
    ZipService zipService;

    @PostConstruct
    public void postConstruct() {
        LOGGER.info("PostConstruct");

        createFolder(WORK_FOLDER);
        createFolder(UNZIP_FOLDER);
    }

    @Override
    public void upload(final InputStream inputStream) {
        LOGGER.info("Upload files ...");
        final Path zipPath = Paths.get(WORK_FOLDER, UPLOADED_ZIP_FILE);
        try {
            Files.copy(inputStream, zipPath, StandardCopyOption.REPLACE_EXISTING);
            final List<String> unzippedFilePaths = zipService.unzip(zipPath.toString(), UNZIP_FOLDER);
            final List<String> validFilePaths = validateFiles(unzippedFilePaths);

            if (validFilePaths.isEmpty()) {
                LOGGER.warn("No valid file has been found. Upload skipped.");
                return;
            }

            LOGGER.info("Uploading " + validFilePaths.size() + " validated file(s).");

            var blockList = new ArrayList<FileType>();
            blockList.add(FileType.APPLICATION_PROPERTIES);

            final boolean hasAnySwf = validFilePaths.stream()
                    .map(Paths::get)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .map(this::getFileType)
                    .anyMatch(t -> t == FileType.SERVERLESS_WORKFLOW);

            if (!hasAnySwf) {
                blockList.add(FileType.SERVERLESS_WORKFLOW);
            }

            cleanUpFolder(RESOURCES_FOLDER, blockList);

            copyResources(validFilePaths);

            cleanUpFolder(UNZIP_FOLDER, Collections.emptyList());

            zipPath.toFile().delete();

            LOGGER.info("Upload files ... done");
        } catch (Exception e) {
            LOGGER.error("Error when processing the uploaded file", e);
        }
    }

    private void createFolder(final String folderPath) {
        LOGGER.info("Create folder: " + folderPath);
        final File folder = new File(folderPath);
        folder.mkdirs();
    }

    private FileType getFileType(final String fileName) {
        if (fileName.endsWith(".sw.json") || fileName.endsWith(".sw.yaml") || fileName.endsWith(".sw.yml")) {
            return FileType.SERVERLESS_WORKFLOW;
        } else if (fileName.equals(APPLICATION_PROPERTIES_FILE_NAME)) {
            return FileType.APPLICATION_PROPERTIES;
        }
        return FileType.UNKNOWN;
    }

    private boolean isServerlessWorkflowFileValid(final Path path) {
        try {
            final String format = path.getFileName().endsWith(".sw.json") ? "json" : "yml";
            final Workflow workflow = ServerlessWorkflowUtils.getWorkflow(new InputStreamReader(new FileInputStream(path.toAbsolutePath().toString())), format);
            if (SourceVersion.isName(workflow.getId())) {
                LOGGER.info("Serverless Workflow file validated: " + workflow.getId());
                return true;
            } else {
                LOGGER.error("Error when validating serverless workflow file. " + workflow.getId() + " is not a valid id.");
                return false;
            }
        } catch (IOException e) {
            LOGGER.error("Error when validating serverless workflow file: " + e.getMessage());
            return false;
        }
    }

    private boolean isPropertiesFileValid(final Path path) {
        try {
            final Properties properties = new Properties();
            try (var inputStream = Files.newInputStream(path)) {
                properties.load(inputStream);
            }
            LOGGER.info("Properties file validated with " + properties.size() + " elements");
            return true;
        } catch (IOException e) {
            LOGGER.error("Error when validating properties file: " + e.getMessage());
            return false;
        }
    }

    private List<String> validateFiles(final List<String> filePaths) {
        LOGGER.info("Validate " + filePaths.size() + " incoming file(s) ...");
        List<String> validFilePaths = new ArrayList<>();

        for (String filePath : filePaths) {
            final Path path = Paths.get(filePath);
            try {
                final FileType fileType = getFileType(path.getFileName().toString());

                if (fileType == FileType.SERVERLESS_WORKFLOW && isServerlessWorkflowFileValid(path)) {
                    validFilePaths.add(filePath);
                } else if (fileType == FileType.APPLICATION_PROPERTIES && isPropertiesFileValid(path)) {
                    validFilePaths.add(filePath);
                } else {
                    LOGGER.warn("Skipping upload of the file " + path.getFileName());
                }
            } catch (Exception e) {
                LOGGER.error("Error when validating file: " + e.getMessage());
            }
        }
        LOGGER.info("Validate " + filePaths.size() + " incoming file(s) ... done");
        return validFilePaths;
    }


    private void cleanUpFolder(final String folderPath, final List<FileType> blockList) throws IOException {
        LOGGER.info("Clean up folder: " + folderPath + "...");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(folderPath))) {
            for (Path path : stream) {
                if (blockList.contains(getFileType(path.getFileName().toString()))) {
                    continue;
                }
                final File file = path.toFile();
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        LOGGER.info("Clean up folder: " + folderPath + "... done");
    }

    private void copyResources(List<String> filePaths) throws IOException {
        LOGGER.info("Copying resources ...");
        for (String filePath : filePaths) {
            final Path path = Paths.get(filePath);
            final FileType fileType = getFileType(path.getFileName().toString());

            final File source = path.toFile();
            final File target = Paths.get(RESOURCES_FOLDER, source.getName()).toFile();

            if (fileType == FileType.APPLICATION_PROPERTIES) {
                mergePropertiesFiles(filePath, APPLICATION_PROPERTIES_FILE_PATH, filePath);
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

    private boolean deleteDirectory(final File file) {
        final File[] childFiles = file.listFiles();
        if (childFiles != null) {
            for (File childFile : childFiles) {
                deleteDirectory(childFile);
            }
        }
        return file.delete();
    }

    private void mergePropertiesFiles(final String pathA, final String pathB, final String mergedPath) throws IOException {
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
}