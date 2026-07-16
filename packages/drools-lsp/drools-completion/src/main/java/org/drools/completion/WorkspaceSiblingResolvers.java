/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.drools.completion;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Process-wide registry for the active {@link WorkspaceSiblingResolver}.
 * Defaults to directory grouping: all other {@code .drl} files in the same
 * directory as the current document, in stable (sorted) order.
 */
public final class WorkspaceSiblingResolvers {

    private static final Logger logger = Logger.getLogger(WorkspaceSiblingResolvers.class.getName());

    private static final WorkspaceSiblingResolver SAME_DIRECTORY =
            WorkspaceSiblingResolvers::sameDirectorySiblings;

    private static volatile WorkspaceSiblingResolver active = SAME_DIRECTORY;

    private WorkspaceSiblingResolvers() {
    }

    public static WorkspaceSiblingResolver active() {
        return active;
    }

    /**
     * Installs {@code resolver}, or restores the same-directory default when
     * {@code null}.
     */
    public static void setActive(WorkspaceSiblingResolver resolver) {
        active = (resolver == null) ? SAME_DIRECTORY : resolver;
    }

    private static List<Path> sameDirectorySiblings(Path currentFile) {
        if (currentFile == null) {
            return Collections.emptyList();
        }
        Path dir = currentFile.toAbsolutePath().getParent();
        if (dir == null || !Files.isDirectory(dir)) {
            return Collections.emptyList();
        }
        Path normalizedCurrent = currentFile.toAbsolutePath().normalize();
        List<Path> siblings = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.drl")) {
            for (Path candidate : stream) {
                if (!candidate.toAbsolutePath().normalize().equals(normalizedCurrent)) {
                    siblings.add(candidate);
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to resolve sibling DRL files for " + currentFile, e);
            return Collections.emptyList();
        }
        siblings.sort(Path::compareTo);
        return siblings;
    }
}
