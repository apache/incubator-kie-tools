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
package org.kie.workbench.common.project.migration.cli;

import java.io.File;

import org.apache.maven.model.Model;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.repositories.Repository;
import org.jboss.weld.environment.se.Weld;
import org.kie.workbench.common.migration.cli.ContainerHandler;
import org.kie.workbench.common.migration.cli.MigrationConstants;
import org.kie.workbench.common.migration.cli.MigrationServicesCDIWrapper;
import org.kie.workbench.common.migration.cli.MigrationSetup;
import org.kie.workbench.common.migration.cli.MigrationTool;
import org.kie.workbench.common.migration.cli.SystemAccess;
import org.kie.workbench.common.migration.cli.ToolConfig;
import org.kie.workbench.common.project.migration.cli.maven.PomMigrationEditor;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.FileVisitResult;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.SimpleFileVisitor;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;

public class PomMigrationTool implements MigrationTool {

    private final String POM_DOT_XML = "pom.xml";
    boolean systemMigrationCheck;
    private SystemAccess system;
    private ToolConfig config;

    @Override
    public String getTitle() {
        return "POMs migration";
    }

    @Override
    public String getDescription() {
        return "Migrates pom.xml files to format compatible with KIE Maven build.";
    }

    @Override
    public Integer getPriority() {
        return 3;
    }

    @Override
    public boolean isSystemMigration() {
        return false;
    }

    @Override
    public void run(ToolConfig config, SystemAccess system) {

        this.config = config;
        this.system = system;
        if (projectMigrationWasExecuted()) {
            system.out().println("Starting POMs migration");
            migrate();
            system.out().println("Finished POMs migration, detailed log available in " + System.getProperty("user.dir") + "/migration_tool.log");
        }
    }

    private void migrate() {
        MigrationSetup.configureProperties(system, config.getTarget());

        String jsonPath;

        if (config.isBatch()) {
            jsonPath = "";
        } else {
            PromptPomMigrationService promptPomMigrationService = new PromptPomMigrationService(system);
            jsonPath = promptPomMigrationService.promptForExternalConfiguration();
        }

        final ContainerHandler container = new ContainerHandler(() -> new Weld().initialize());
        container.run(MigrationServicesCDIWrapper.class,
                      cdiWrapper -> cdiWrapper.getWorkspaceProjectService().getAllWorkspaceProjects().forEach(pr -> processWorkspaceProject(pr, jsonPath, cdiWrapper)),
                      error -> {
                          system.err().println("Error during migration: ");
                          error.printStackTrace(system.err());
                      });
        container.close();
    }

    private void processWorkspaceProject(WorkspaceProject workspaceProject, String jsonPath, MigrationServicesCDIWrapper cdiWrapper) {
        if (systemMigrationWasExecuted(cdiWrapper)) {

            PomMigrationEditor editor = new PomMigrationEditor();
            final int[] counter = {0};
            Files.walkFileTree(Paths.convert(workspaceProject.getRootPath()), new SimpleFileVisitor<org.uberfire.java.nio.file.Path>() {
                @Override
                public FileVisitResult visitFile(org.uberfire.java.nio.file.Path visitedPath, BasicFileAttributes attrs) throws IOException {

                    org.uberfire.backend.vfs.Path visitedVFSPath = Paths.convert(visitedPath);
                    String fileName = visitedVFSPath.getFileName();
                    File file = visitedPath.toFile();

                    if (file.isFile() && fileName.equals(POM_DOT_XML)) {
                        try {
                            Model model;
                            if (jsonPath.isEmpty()) {
                                model = editor.updatePom(visitedPath, cdiWrapper);
                            } else {
                                model = editor.updatePom(visitedPath, jsonPath, cdiWrapper);
                            }
                            if (!model.getBuild().getPlugins().isEmpty()) {
                                counter[0]++;
                            }
                        } catch (Exception e) {
                            system.err().println("Error reading from filename [" + fileName + "] (error below).");
                            e.printStackTrace(system.err());
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
            system.out().println("Migrated " + counter[0] + " POMs for project: " + workspaceProject.getName());
        }
    }

    private boolean projectMigrationWasExecuted() {
        if (!config.getTarget().resolve("system").resolve(MigrationConstants.SYSTEM_GIT).toFile().exists()) {
            system.err().println(String.format("The PROJECT STRUCTURE MIGRATION must be ran before this one."));
            return false;
        }
        return true;
    }

    private boolean systemMigrationWasExecuted(MigrationServicesCDIWrapper cdiWrapper) {
        if (!systemMigrationCheck) {
            systemMigrationCheck = true;
            final IOService systemIoService = cdiWrapper.getSystemIoService();
            final Repository systemRepository = cdiWrapper.getSystemRepository();
            if (!systemIoService.exists(systemIoService.get(systemRepository.getUri()).resolve("spaces"))) {
                system.err().println(String.format("The SYSTEM CONFIGURATION DIRECTORY STRUCTURE MIGRATION must be ran before this one."));
                return false;
            }
            return true;
        } else {
            return true;
        }
    }
}
