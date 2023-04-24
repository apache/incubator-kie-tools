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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.kie.kogito.api.ZipService;

import static org.junit.jupiter.api.Assertions.*;

public class ZipServiceTest {

    private final ZipService zipService = new ZipServiceImpl();

    private static final Path ZIP_FILE_PATH = Path.of("src/test/resources/test.zip");

    @Test
    public void unzip_validZipFile() throws IOException {
        Path destinationFolderPath = Files.createTempDirectory("unzip");

        List<Path> unzippedFiles = zipService.unzip(ZIP_FILE_PATH, destinationFolderPath);

        assertNotNull(unzippedFiles);
        assertEquals(4, unzippedFiles.size());
    }

    @Test
    public void unzip_invalidZipFile() {
        Path invalidZipFilePath = Path.of("src/test/resources/invalid.zip");
        Path destinationFolderPath = Path.of("test");

        assertThrows(IOException.class, () -> zipService.unzip(invalidZipFilePath, destinationFolderPath));
    }
}
