/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ZipServiceImpl implements ZipService {

    @Override
    public List<String> unzip(final String zipFile, final String destinationPath) throws IOException {
        var filePaths = new ArrayList<String>();
        var destinationFolder = new File(destinationPath);
        var buffer = new byte[1024];
        try (var zis = new ZipInputStream(new FileInputStream(zipFile))) {
            var zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                var newFile = newFile(destinationFolder, zipEntry);
                if (zipEntry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new IOException("Failed to create directory " + newFile);
                    }
                } else {
                    filePaths.add(newFile.getAbsolutePath());
                    var parent = newFile.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("Failed to create directory " + parent);
                    }
                    try (var fos = new FileOutputStream(newFile)) {
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
        }
        return filePaths;
    }

    private File newFile(final File destinationFolder, final ZipEntry zipEntry) throws IOException {
        var file = new File(destinationFolder, zipEntry.getName());

        if (!file.getCanonicalPath().startsWith(destinationFolder.getCanonicalPath() + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return file;
    }
}
