/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.guvnor.common.services.backend.archive;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.zip.ZipEntry;

import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;

public class Archiver {

    private Path originalPath;

    private ZipWriter zipWriter;

    private IOService ioService;

    public Archiver() {
    }

    @Inject
    public Archiver(@Named("ioStrategy") IOService ioService) {
        this.ioService = ioService;
    }

    public void archive(final ByteArrayOutputStream outputStream,
                        final String uri) throws IOException, URISyntaxException {

        init(outputStream,
             uri);
        zip();
    }

    private void zip() throws IOException {
        if (Files.isDirectory(originalPath)) {
            addPath(Files.newDirectoryStream(originalPath));
        } else {
            addFile(originalPath);
        }
        zipWriter.close();
    }

    private void init(final ByteArrayOutputStream outputStream,
                      final String uri) throws URISyntaxException {
        this.originalPath = ioService.get(new URI(uri));
        this.zipWriter = new ZipWriter(outputStream);
    }

    private void addPath(DirectoryStream<Path> directoryStream) throws IOException {
        for (Path subPath : directoryStream) {
            if (Files.isDirectory(subPath)) {
                addPath(Files.newDirectoryStream(subPath));
            } else {
                addFile(subPath);
            }
        }
    }

    private void addFile(final Path subPath) throws IOException {
        zipWriter.addFile(getZipEntry(subPath),
                          ioService.newInputStream(subPath));
    }

    private ZipEntry getZipEntry(final Path subPath) {
        return new ZipEntry(FileNameResolver.resolve(subPath.toUri().getPath(),
                                                     originalPath.toUri().getPath()));
    }

    static class FileNameResolver {

        static protected String resolve(final String subPath,
                                        final String originalPath) {

            final String fileName = resolveOriginalFileName(subPath,
                                                            originalPath);

            if (fileName.startsWith("/")) {
                return "project" + fileName;
            } else {
                return fileName;
            }
        }

        static private String resolveOriginalFileName(final String subPath,
                                                      final String originalPath) {
            if ("/".equals(originalPath)) {
                return subPath.substring(originalPath.length());
            } else {
                String fileSubPath = subPath.substring(originalPath.length());
                if (fileSubPath.charAt(0) == '/') {
                    fileSubPath = fileSubPath.substring(1);
                }

                return getBaseFolder(originalPath) + fileSubPath;
            }
        }

        private static String getBaseFolder(final String originalPath) {
            if (originalPath.contains("/")) {
                return originalPath.substring(originalPath.lastIndexOf("/") + 1) + "/";
            } else {
                return originalPath + "/";
            }
        }
    }
}
