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

package org.kie.workbench.common.system.configuration;

import java.nio.file.Path;

import org.jboss.weld.environment.se.Weld;
import org.kie.workbench.common.migration.cli.ContainerHandler;
import org.kie.workbench.common.migration.cli.MigrationConstants;
import org.kie.workbench.common.migration.cli.MigrationSetup;
import org.kie.workbench.common.migration.cli.MigrationTool;
import org.kie.workbench.common.migration.cli.SystemAccess;
import org.kie.workbench.common.migration.cli.ToolConfig;
import org.kie.workbench.common.project.cli.PromptService;

public class SystemConfigurationMigrationTool implements MigrationTool {

    public static final String NAME = "System configuration directory structure migration";

    private SystemAccess system;
    private ToolConfig config;

    @Override
    public String getTitle() {
        return NAME;
    }

    @Override
    public String getDescription() {
        return "Moves old system configuration directory structure to the new one";
    }

    @Override
    public Integer getPriority() {
        return 1;
    }

    @Override
    public boolean isSystemMigration() {
        return true;
    }

    @Override
    public void run(final ToolConfig config,
                    final SystemAccess system) {
        this.config = config;
        this.system = system;

        final PromptService promptService = new PromptService(system,
                                                              config);

        system.out().println("Starting system configuration directory structure migration");

        if (validateTarget() && promptService.maybePromptForBackup()) {
            final Path niogitDir = config.getTarget();
            MigrationSetup.configureProperties(system,
                                               niogitDir);
            migrate();
        }
    }

    private void migrate() {
        ContainerHandler container = new ContainerHandler(() -> new Weld().initialize());
        container.run(ConfigGroupsMigrationService.class,
                      service -> service.groupSystemConfigGroups(),
                      error -> {
                          system.err().println("Error during migration: ");
                          error.printStackTrace(system.err());
                      });
    }

    private boolean validateTarget() {
        if (!config.getTarget().resolve("system").resolve(MigrationConstants.SYSTEM_GIT).toFile().exists()) {
            system.err().println(String.format("The PROJECT STRUCTURE MIGRATION must be ran before this one."));
            return false;
        }

        return true;
    }
}
