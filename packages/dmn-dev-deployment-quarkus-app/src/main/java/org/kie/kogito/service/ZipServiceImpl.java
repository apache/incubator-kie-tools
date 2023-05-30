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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.Enumeration;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ZipServiceImpl implements ZipService {

    @Override
    public List<String> unzip(final String zipFilePath, final String destinationPath) throws IOException {
        var filePaths = new ArrayList<String>();
        var destinationFolder = new File(destinationPath);
        var buffer = new byte[1024];

        try (ZipFile zipFile = new ZipFile(zipFilePath)) {
            Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();

            while (zipEntries.hasMoreElements()) {
                var zipEntry = zipEntries.nextElement();
                
                if (zipEntry != null) {
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
                            var inputStream = zipFile.getInputStream(zipEntry);
                            while ((len = inputStream.read(buffer)) > 0) {
                                fos.write(buffer, 0, len);
                            }
                        }
                    }
                }
            }
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
