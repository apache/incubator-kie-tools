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

import java.util.Collection;

import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.Path;

/**
 * Service to discover files in a given Path
 */
public interface FileDiscoveryService {

    /**
     * Discover files
     * @param pathToSearch The root Path to search. Sub-folders are not included.
     * @param filter A filter to restrict the matched files.
     * @param recursive True is sub-folders are to be scanned
     * @return
     */
    Collection<Path> discoverFiles(final Path pathToSearch,
                                   final DirectoryStream.Filter<org.uberfire.java.nio.file.Path> filter,
                                   final boolean recursive);

    /**
     * Discover files. Convenience method excluding sub-folders
     * @param pathToSearch The root Path to search. Sub-folders are not included.
     * @param filter A filter to restrict the matched files.
     * @return
     */
    Collection<Path> discoverFiles(final Path pathToSearch,
                                   final DirectoryStream.Filter<org.uberfire.java.nio.file.Path> filter);
}
