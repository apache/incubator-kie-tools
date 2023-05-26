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

package org.kie.kogito;

import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.quarkus.runtime.Startup;
import org.apache.commons.io.FileUtils;
import org.jboss.logging.Logger;
import org.kie.kogito.api.FileService;

@Startup
@ApplicationScoped
public class AppStartup {

    private static final Logger LOGGER = Logger.getLogger(AppStartup.class);

    @Inject
    FileService fileService;

    @PostConstruct
    public void postConstruct() {
        LOGGER.info("PostConstruct");

        fileService.createFolder(FileStructureConstants.WORK_FOLDER_PATH);

        fileService.deleteFolder(FileStructureConstants.UNZIP_FOLDER_PATH);
        fileService.createFolder(FileStructureConstants.UNZIP_FOLDER_PATH);

        createBackupFiles();
    }

    private void createBackupFiles() {
        try {
            final File applicationPropertiesBackup =
                    FileStructureConstants.BACKUP_APPLICATION_PROPERTIES_FILE_PATH.toFile();
            if (!applicationPropertiesBackup.exists()) {
                final File applicationProperties = FileStructureConstants.APPLICATION_PROPERTIES_FILE_PATH.toFile();
                FileUtils.copyFile(applicationProperties, applicationPropertiesBackup);
                LOGGER.info("Backup created for the default application.properties");
            } else {
                LOGGER.info("No need to create backup for the default application.properties");
            }
        } catch (IOException e) {
            LOGGER.error("Error when creating backup file for application.properties");
        }
    }
}
