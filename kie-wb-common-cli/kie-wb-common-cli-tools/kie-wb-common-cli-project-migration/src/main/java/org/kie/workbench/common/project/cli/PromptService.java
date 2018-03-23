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

package org.kie.workbench.common.project.cli;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;

import org.kie.workbench.common.migration.cli.SystemAccess;
import org.kie.workbench.common.migration.cli.ToolConfig;

public class PromptService {

    private SystemAccess system;
    private ToolConfig config;
    private Path niogitDir;

    public PromptService(final SystemAccess system,
                         final ToolConfig config) {
        this.system = system;
        this.config = config;
        this.niogitDir = config.getTarget();
    }

    public boolean maybePromptForBackup() {
        return config.isBatch() || promptForBackup();
    }

    private boolean promptForBackup() {
        SystemAccess.Console console = system.console();
        console.format("WARNING: Please ensure that you have made backups of the directory [%s] before proceeding.\n", niogitDir);
        Collection<String> validResponses = Arrays.asList("yes", "no");
        String response;
        do {
            response = console.readLine("Do you wish to continue? [yes/no]: ").toLowerCase();
        } while (!validResponses.contains(response));

        return "yes".equals(response);
    }
}
