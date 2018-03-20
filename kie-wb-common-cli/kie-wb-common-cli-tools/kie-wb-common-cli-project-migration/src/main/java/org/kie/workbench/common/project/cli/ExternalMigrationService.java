/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.project.cli;


import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

import org.kie.workbench.common.migration.cli.MigrationConstants;
import org.kie.workbench.common.migration.cli.SystemAccess;

import static org.kie.workbench.common.migration.cli.MigrationConstants.systemRepos;

/**
 * <p>
 * Performs external migration required on repositories so that target repositories can be properly read and migrated to separated projects by
 * the internal service.
 */
public class ExternalMigrationService {

    private final SystemAccess system;

    public ExternalMigrationService(SystemAccess system) {
        this.system = system;
    }

    public void moveSystemRepos(Path niogitDir) throws SystemAccess.HaltingException {
        Path systemSpace = niogitDir.resolve(MigrationConstants.SYSTEM_SPACE);
        ensureSystemSpaceOrExit(systemSpace);
        system.out().println("Moving built-in repositories to system space...");
        Arrays
        .stream(systemRepos)
        .forEach(oldRepoRelPath -> {
            Path oldRepoAbsPath = niogitDir.resolve(oldRepoRelPath);
            if (oldRepoAbsPath.toFile().exists()) {
                system.out().printf("Moving %s...\n", oldRepoRelPath);
                tryMovingRepo(systemSpace, oldRepoRelPath, oldRepoAbsPath);
            }
        });
        system.out().println("Finished moving built-in repositories.");
    }

    private void ensureSystemSpaceOrExit(Path systemSpace) throws SystemAccess.HaltingException {
        if (!systemSpace.toFile().exists()) {
            try {
                system.createDirectory(systemSpace);
            } catch (IOException e) {
                e.printStackTrace(system.err());
                system.exit(1);
            }
        }
        else if (!systemSpace.toFile().isDirectory()) {
            new RuntimeException("Cannot create system space because of file: " + systemSpace).printStackTrace(system.err());
            system.exit(1);
        }
    }

    private void tryMovingRepo(Path systemSpace, String oldRepoRelPath, Path oldRepoAbsPath) {
        try {
            system.move(oldRepoAbsPath, systemSpace.resolve(oldRepoRelPath));
        } catch (IOException e) {
            system.err().println("Unable to move " + oldRepoAbsPath);
            e.printStackTrace(system.err());
        }
    }

}
