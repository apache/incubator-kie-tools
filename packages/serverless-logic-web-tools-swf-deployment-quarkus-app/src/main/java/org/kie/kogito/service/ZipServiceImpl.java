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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.IOUtils;
import org.kie.kogito.api.ZipService;

@ApplicationScoped
public class ZipServiceImpl implements ZipService {

    @Override
    public List<Path> unzip(final Path zipFilePath, final Path destinationFolderPath) throws IOException {
        var filePaths = new ArrayList<Path>();
        try (ZipFile zipFile = new ZipFile(zipFilePath)) {
            Enumeration<? extends ZipArchiveEntry> entries = zipFile.getEntries();
            while (entries.hasMoreElements()) {
                ZipArchiveEntry entry = entries.nextElement();
                String fileName = entry.getName();
                File newFile = new File(destinationFolderPath.toString(), fileName);
                if (entry.isDirectory()) {
                    newFile.mkdirs();
                } else {
                    filePaths.add(newFile.toPath());
                    newFile.getParentFile().mkdirs();
                    try (OutputStream os = new FileOutputStream(newFile)) {
                        IOUtils.copy(zipFile.getInputStream(entry), os);
                    }
                }
            }
        }
        return filePaths;
    }
}
