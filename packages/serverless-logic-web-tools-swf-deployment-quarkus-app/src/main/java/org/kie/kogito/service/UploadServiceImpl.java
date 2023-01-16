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

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;

@Startup
@ApplicationScoped
public class UploadServiceImpl implements UploadService {

    private static final Logger LOGGER = Logger.getLogger(UploadServiceImpl.class);
    private static final String WORK_FOLDER = "/tmp/serverless-logic";
    private static final String UNZIP_FOLDER = Paths.get(WORK_FOLDER, "unzip").toString();
    private static final String META_INF_RESOURCES_FOLDER = "src/main/resources/META-INF/resources";
    private static final String UPLOADED_ZIP_FILE = "file.zip";

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
        LOGGER.info("Upload file ...");
        var zipPath = Paths.get(WORK_FOLDER, UPLOADED_ZIP_FILE);
        try {
            Files.copy(inputStream, zipPath, StandardCopyOption.REPLACE_EXISTING);
            zipService.unzip(zipPath.toString(), UNZIP_FOLDER);

            validateIncomingFiles();

            cleanUpFolder(META_INF_RESOURCES_FOLDER);

            copyResources();

            cleanUpFolder(UNZIP_FOLDER);

            LOGGER.info("Upload file ... done");
        } catch (Exception e) {
            LOGGER.error("Error when processing the uploaded file", e);
        }
    }

    private void createFolder(final String folderPath) {
        LOGGER.info("Create folder: " + folderPath);
        var folder = new File(folderPath);
        folder.mkdirs();
    }

    private void validateIncomingFiles() {
        LOGGER.info("Validate incoming files");
        // TODO CAPONETTO: validate SWF file before copying to resources
    }

    private void cleanUpFolder(final String folderPath) throws IOException {
        LOGGER.info("Clean up folder: " + folderPath);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(folderPath))) {
            for (Path path : stream) {
                var file = path.toFile();
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
    }

    private void copyResources() throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(UNZIP_FOLDER))) {
            for (Path path : stream) {
                var source = path.toFile();
                var target = Paths.get(META_INF_RESOURCES_FOLDER, source.getName()).toFile();

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
        }
    }

    private boolean deleteDirectory(final File file) {
        var childFiles = file.listFiles();
        if (childFiles != null) {
            for (File childFile : childFiles) {
                deleteDirectory(childFile);
            }
        }
        return file.delete();
    }
}
