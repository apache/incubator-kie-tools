/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.services.backend.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.commons.io.FilenameUtils;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;

public class VFSScanner<TYPE> {

    private IOService ioService;
    private Path rootPath;
    private Collection<String> extensions;
    private Function<InputStream, TYPE> converter;
    private Predicate<TYPE> filter;
    private List<ScanResult<TYPE>> results = new ArrayList<>();

    private VFSScanner(IOService ioService, Path rootPath, Collection<String> extensions, Function<InputStream, TYPE> converter, Predicate<TYPE> filter) {
        this.ioService = ioService;
        this.rootPath = rootPath;
        this.extensions = extensions;
        this.converter = converter;
        this.filter = filter;
    }

    public static <TYPE> Collection<ScanResult<TYPE>> scan(IOService ioService, Path rootPath, Collection<String> extensions, Function<InputStream, TYPE> converter) {
        return scan(ioService, rootPath, extensions, converter, resource -> true);
    }

    public static <TYPE> Collection<ScanResult<TYPE>> scan(IOService ioService, Path rootPath, Collection<String> extensions, Function<InputStream, TYPE> converter, Predicate<TYPE> filter) {

        VFSScanner<TYPE> scanner = new VFSScanner<>(ioService, rootPath, extensions, converter, filter);

        return scanner.scanPath();
    }

    private Collection<ScanResult<TYPE>> scanPath() {

        scanPath(rootPath);

        return results;
    }

    private void scanPath(final Path path) {
        if (Files.isDirectory(path)) {
            ioService.newDirectoryStream(path).forEach(this::scanPath);
        } else {
            String filename = path.getFileName().toString();
            String extension = FilenameUtils.getExtension(filename);
            if (extensions.contains(extension)) {

                TYPE source = converter.apply(ioService.newInputStream(path));

                if (filter.test(source)) {
                    results.add(new ScanResult<>(path, source));
                }
            }
        }
    }

    public static class ScanResult<TYPE> {

        private Path assetPath;

        private TYPE resource;

        private ScanResult(Path assetPath, TYPE resource) {
            this.assetPath = assetPath;
            this.resource = resource;
        }

        public Path getAssetPath() {
            return assetPath;
        }

        public TYPE getResource() {
            return resource;
        }
    }
}
