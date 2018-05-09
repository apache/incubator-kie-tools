/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.migration.cli;

import java.nio.file.Path;

import org.uberfire.java.nio.fs.jgit.JGitFileSystemProviderConfiguration;

public class MigrationSetup {

    public static void configureProperties(final SystemAccess system,
                                           final Path niogitDir) {
        system.setProperty(JGitFileSystemProviderConfiguration.GIT_NIO_DIR, niogitDir.getParent().toString());
        system.setProperty(JGitFileSystemProviderConfiguration.GIT_NIO_DIR_NAME, niogitDir.getFileName().toString());
        system.setProperty(JGitFileSystemProviderConfiguration.GIT_DAEMON_ENABLED, "false");
        system.setProperty(JGitFileSystemProviderConfiguration.GIT_SSH_ENABLED, "false");
        system.setProperty("org.uberfire.watcher.autostart", "false");
    }
}
