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

package org.kie.kogito.api;

import org.kie.kogito.model.FileType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface FileService {
    void createFolder(String folderPath);

    void cleanUpFolder(String folderPath) throws IOException;

    FileType getFileType(Path filePath);

    void deleteDirectory(File directory);

    void mergePropertiesFiles(String pathA, String pathB, String mergedPath) throws IOException;

    List<String> validateFiles(List<String> filePaths);

    void copyResources(List<String> filePaths) throws IOException;

    boolean exists(String path);
}
