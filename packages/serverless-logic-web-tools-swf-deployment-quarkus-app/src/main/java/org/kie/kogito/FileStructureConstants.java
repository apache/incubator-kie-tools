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

import java.nio.file.Path;
import java.nio.file.Paths;

public interface FileStructureConstants {

    String UPLOADED_ZIP_FILE_NAME = "file.zip";
    String APPLICATION_PROPERTIES_FILE_NAME = "application.properties";

    Path WORK_FOLDER_PATH = Paths.get("/tmp/serverless-logic");
    Path BACKUP_FOLDER_PATH = WORK_FOLDER_PATH.resolve("backup");
    Path UNZIP_FOLDER_PATH = WORK_FOLDER_PATH.resolve("unzip");
    Path PROJECT_FOLDER_PATH = Paths.get(".").toAbsolutePath().normalize();
    Path PROJECT_RESOURCES_FOLDER_PATH = PROJECT_FOLDER_PATH.resolve("src/main/resources");

    Path UPLOADED_ZIP_FILE_PATH = UNZIP_FOLDER_PATH.resolve(UPLOADED_ZIP_FILE_NAME);
    Path BACKUP_APPLICATION_PROPERTIES_FILE_PATH = BACKUP_FOLDER_PATH.resolve(APPLICATION_PROPERTIES_FILE_NAME);
    Path APPLICATION_PROPERTIES_FILE_PATH = PROJECT_RESOURCES_FOLDER_PATH.resolve(APPLICATION_PROPERTIES_FILE_NAME);
}
