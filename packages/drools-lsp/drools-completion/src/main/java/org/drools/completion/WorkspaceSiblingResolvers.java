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
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Process-wide registry for the active {@link WorkspaceSiblingResolver}.
 *
 * <p>Resolution order at class-init: a {@link ServiceLoader} provider from
 * outside this module, then the shipped {@link ConfiguredGroupingResolver}, then
 * same-directory grouping. A third-party provider is preferred over the shipped
 * one so that installing a resolver takes nothing more than putting a jar on the
 * server's classpath — the extension point is of no use if exercising it means
 * running custom code inside the server process.
 */
public final class WorkspaceSiblingResolvers {

    private static final Logger logger = Logger.getLogger(WorkspaceSiblingResolvers.class.getName());

    private static final WorkspaceSiblingResolver SAME_DIRECTORY =
            WorkspaceSiblingResolvers::sameDirectorySiblings;

    private static volatile WorkspaceSiblingResolver active = discover();

    private WorkspaceSiblingResolvers() {
    }

    public static WorkspaceSiblingResolver active() {
        return active;
    }

    /**
     * Installs {@code resolver}, or restores the discovered default when
     * {@code null}. Takes precedence over any {@link ServiceLoader} provider.
     */
    public static void setActive(WorkspaceSiblingResolver resolver) {
        active = (resolver == null) ? discover() : resolver;
    }

    private static WorkspaceSiblingResolver discover() {
        WorkspaceSiblingResolver shipped = null;
        try {
            for (WorkspaceSiblingResolver provider : ServiceLoader.load(
                    WorkspaceSiblingResolver.class, WorkspaceSiblingResolvers.class.getClassLoader())) {
                if (provider instanceof ConfiguredGroupingResolver) {
                    shipped = provider;
                    continue;
                }
                logger.info("Using DRL workspace sibling resolver: " + provider.getClass().getName());
                return provider;
            }
        } catch (Exception | ServiceConfigurationError e) {
            logger.log(Level.WARNING, "Failed to load a WorkspaceSiblingResolver provider", e);
        }
        return (shipped != null) ? shipped : SAME_DIRECTORY;
    }

    static List<Path> sameDirectorySiblings(Path currentFile) {
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
