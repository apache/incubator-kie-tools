/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.guvnor.common.services.backend.file;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.kie.soup.commons.validation.PortablePreconditions;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;

/**
 * Default implementation of FileDiscoveryService
 */
@ApplicationScoped
public class FileDiscoveryServiceImpl implements FileDiscoveryService {

    @Override
    public Collection<Path> discoverFiles(final Path pathToSearch,
                                          final DirectoryStream.Filter<org.uberfire.java.nio.file.Path> filter,
                                          final boolean recursive) {
        PortablePreconditions.checkNotNull("pathToSearch",
                                           pathToSearch);
        PortablePreconditions.checkNotNull("filter",
                                           filter);

        final List<Path> discoveredFiles = new ArrayList<Path>();

        //The pathToSearch could be a file, and of the type we need
        if (Files.isRegularFile(pathToSearch)) {
            if (filter.accept(pathToSearch)) {
                discoveredFiles.add(pathToSearch);
                return discoveredFiles;
            }
        }

        //This check should never match, but it's included as a safe-guard
        if (!Files.isDirectory(pathToSearch)) {
            return discoveredFiles;
        }

        //Path represents a Folder, so check and recursively add it's content, if applicable
        try (final DirectoryStream<Path> paths = Files.newDirectoryStream(pathToSearch)) {
            for (final Path path : paths) {
                if (Files.isRegularFile(path)) {
                    if (filter.accept(path)) {
                        discoveredFiles.add(path);
                    }
                } else if (recursive && Files.isDirectory(path)) {
                    discoveredFiles.addAll(discoverFiles(path,
                                                         filter,
                                                         recursive));
                }
            }
        }

        return discoveredFiles;
    }

    @Override
    public Collection<Path> discoverFiles(final Path pathToSearch,
                                          final DirectoryStream.Filter<org.uberfire.java.nio.file.Path> filter) {
        return discoverFiles(pathToSearch,
                             filter,
                             false);
    }
}
