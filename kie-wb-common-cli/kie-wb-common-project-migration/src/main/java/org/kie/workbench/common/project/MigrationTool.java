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

package org.kie.workbench.common.project;

import java.io.File;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.LogManager;

import org.apache.commons.cli.ParseException;
//import org.apache.commons.io.output.NullOutputStream;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.kie.workbench.common.project.cli.ExternalMigrationService;
import org.kie.workbench.common.project.cli.InternalMigrationService;
import org.kie.workbench.common.project.cli.MigrationConstants;
import org.kie.workbench.common.project.cli.RealSystemAccess;
import org.kie.workbench.common.project.cli.SystemAccess;
import org.kie.workbench.common.project.cli.SystemAccess.Console;
import org.kie.workbench.common.project.cli.ToolConfig;
import org.kie.workbench.common.project.cli.ToolConfig.ToolConfigFactory;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProviderConfiguration;

public class MigrationTool {

    public static void main(String[] args) {
        disableLogging();
        SystemAccess system = new RealSystemAccess();
        new MigrationTool(new ExternalMigrationService(system),
                          new ToolConfig.DefaultFactory(),
                          system,
                          () -> {
                            /*
                             * Work around log4j-api message printed from missing configuration.
                             */
                            PrintStream err = System.err;
//                            System.setErr(new PrintStream(new NullOutputStream()));
                            try {
                                WeldContainer container = new Weld().initialize();
                                return container;
                            } finally {
                                System.setErr(err);
                            }
                        }).run(args);
    }

    private static void disableLogging() {
        LogManager.getLogManager().reset();
    }

    private final ToolConfigFactory configFactory;
    private final SystemAccess system;
    private Supplier<WeldContainer> containerFactory;
    private ExternalMigrationService externalService;

    public MigrationTool(ExternalMigrationService externalService,
                         ToolConfig.ToolConfigFactory parser,
                         SystemAccess system,
                         Supplier<WeldContainer> containerFactory) {
        this.externalService = externalService;
        this.configFactory = parser;
        this.system = system;
        this.containerFactory = containerFactory;
    }

    public void run(String[] args) {
        ToolConfig config = parseToolConfigOrExit(args);
        Path niogitDir = config.getTarget();

        validateTarget(niogitDir);
        maybePromptForBackupAndExit(config, niogitDir);

        externalService.moveSystemRepos(niogitDir);

        configureProperties(niogitDir);
        migrateAndExit(niogitDir);
    }

    private void maybePromptForBackupAndExit(ToolConfig config, Path niogitDir) {
        if (!config.isBatch() && !promptForBackup(niogitDir)) {
            system.exit(0);
        }
    }

    private boolean promptForBackup(Path niogitDir) {
        Console console = system.console();
        console.format("WARNING: Please ensure that you have made backups of the directory [%s] before proceeding.\n", niogitDir);
        Collection<String> validResponses = Arrays.asList("yes", "no");
        String response;
        do {
            response = console.readLine("Do you wish to continue? [yes/no]: ").toLowerCase();
        } while (!validResponses.contains(response));

        return "yes".equals(response);
    }

    private void validateTarget(Path niogitDir) {
        Optional<String> errorMessage = Optional.empty();
        try {
            File dirFile = niogitDir.toFile();
            if (!dirFile.exists()) {
                errorMessage = Optional.of(String.format("The target path does not exist: %s", niogitDir));
            }
            else if (!dirFile.isDirectory()) {
                errorMessage = Optional.of(String.format("The target path is not a directory: %s", niogitDir));
            }
            else if (niogitDir.resolve("system").resolve(MigrationConstants.SYSTEM_GIT).toFile().exists()) {
                errorMessage = Optional.of(String.format("The target path looks like it already contains an updated filesystem: %s", niogitDir));
            }
        } catch (UnsupportedOperationException e) {
            errorMessage = Optional.of(String.format("The target path must be a file: %s", niogitDir));
        }

        errorMessage.ifPresent(msg -> {
            system.err().println(msg);
            system.exit(1);
        });
    }

    private ToolConfig parseToolConfigOrExit(String[] args) {
        ToolConfig config = null;
        try {
            config = configFactory.parse(args);
        } catch (ParseException e) {
            system.err().printf("Could not parse arguments: %s\n", e.getMessage());
            configFactory.printHelp(system.err(), MigrationConstants.MIGRATION_TOOL_NAME);
            system.exit(1);
        }
        return config;
    }

    private void migrateAndExit(Path niogitDir) {
        int exitStatus = 0;
        WeldContainer container = null;
        try {
            container = containerFactory.get();
            InternalMigrationService internalService = loadInternalService(container);
            internalService.migrateAllProjects(niogitDir);
        } catch (Throwable t) {
            exitStatus = 1;
            t.printStackTrace(system.err());
        } finally {
            if (container != null && container.isRunning()) {
                quietShutdown(container);
            }
        }

        system.exit(exitStatus);
    }

    private void quietShutdown(WeldContainer container) {
        try {
            container.shutdown();
        } catch (Throwable ignore) {
            // Suppress exceptions from bad shutdown
        }
    }

    private void configureProperties(Path niogitDir) {
        system.setProperty(JGitFileSystemProviderConfiguration.GIT_NIO_DIR, niogitDir.getParent().toString());
        system.setProperty(JGitFileSystemProviderConfiguration.GIT_NIO_DIR_NAME, niogitDir.getFileName().toString());
        system.setProperty(JGitFileSystemProviderConfiguration.GIT_DAEMON_ENABLED, "false");
        system.setProperty(JGitFileSystemProviderConfiguration.GIT_SSH_ENABLED, "false");
    }

    private static InternalMigrationService loadInternalService(WeldContainer container) {
        return container.instance().select(InternalMigrationService.class).get();
    }

}
