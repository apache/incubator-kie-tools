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

    void cleanUpFolder(final String folderPath, final List<FileType> blockList) throws IOException;

    FileType getFileType(final Path filePath);

    void deleteDirectory(final File directory);

    void mergePropertiesFiles(final String pathA, final String pathB, final String mergedPath) throws IOException;

    List<String> validateFiles(final List<String> filePaths);

    void copyResources(final List<String> filePaths) throws IOException;
}
