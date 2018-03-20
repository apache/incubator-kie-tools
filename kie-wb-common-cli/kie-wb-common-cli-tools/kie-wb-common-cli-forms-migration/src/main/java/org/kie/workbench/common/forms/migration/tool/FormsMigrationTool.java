/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.migration.tool;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.kie.workbench.common.forms.migration.legacy.model.Form;
import org.kie.workbench.common.forms.migration.legacy.services.FormSerializationManager;
import org.kie.workbench.common.forms.migration.legacy.services.impl.FormSerializationManagerImpl;
import org.kie.workbench.common.forms.migration.tool.cdi.MigrationServicesCDIWrapper;
import org.kie.workbench.common.forms.migration.tool.pipelines.MigrationContext;
import org.kie.workbench.common.forms.migration.tool.pipelines.MigrationPipeline;
import org.kie.workbench.common.forms.migration.tool.util.FormsMigrationConstants;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.migration.cli.MigrationConstants;
import org.kie.workbench.common.migration.cli.MigrationTool;
import org.kie.workbench.common.migration.cli.SystemAccess;
import org.kie.workbench.common.migration.cli.ToolConfig;
import org.kie.workbench.common.project.ProjectMigrationTool;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.FileVisitResult;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.SimpleFileVisitor;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProviderConfiguration;

public class FormsMigrationTool implements MigrationTool {

    private FormSerializationManager legacyFormSerializer = new FormSerializationManagerImpl();
    private SystemAccess system;
    private ToolConfig config;
    private Path niogitDir;
    private WeldContainer weldContainer;
    private MigrationServicesCDIWrapper cdiWrapper;
    private MigrationPipeline pipeline;

    @Override
    public String getTitle() {
        return "Forms migration";
    }

    @Override
    public String getDescription() {
        return "Moves old jBPM Form Modeler forms into the new Forms format. Requires " + ProjectMigrationTool.NAME.toUpperCase() + " to be already executed";
    }

    @Override
    public Integer getPriority() {
        return 1;
    }

    @Override
    public void run(ToolConfig config, SystemAccess system) {
        this.config = config;
        this.system = system;

        this.niogitDir = config.getTarget();

        system.out().println("\nStarting Forms migration");

        if (validateTarget()) {
            try {
                configureProperties();

                pipeline = new MigrationPipeline();

                if(!config.isBatch()) {

                    system.out().println(pipeline.getAllInfo());

                    Collection<String> validResponses = Arrays.asList("yes", "no");

                    String response;
                    do {
                        response = system.console().readLine("\nDo you want to continue? [yes/no]: ").toLowerCase();
                    } while (!validResponses.contains(response));

                    if("no".equals(response)) {
                        return;
                    }
                }

                weldContainer = new Weld().initialize();

                cdiWrapper = weldContainer.instance().select(MigrationServicesCDIWrapper.class).get();

                WorkspaceProjectService service = weldContainer.instance().select(WorkspaceProjectService.class).get();

                service.getAllWorkspaceProjects().forEach(this::processWorkspaceProject);
            } finally {
                if (weldContainer != null) {
                    try {
                        cdiWrapper = null;
                        weldContainer.close();
                    } catch (Exception ex) {

                    }
                }
            }
        }
    }

    private void processWorkspaceProject(WorkspaceProject workspaceProject) {

        List<FormMigrationSummary> summaries = new ArrayList<>();

        Files.walkFileTree(Paths.convert(workspaceProject.getRootPath()), new SimpleFileVisitor<org.uberfire.java.nio.file.Path>() {
            @Override
            public FileVisitResult visitFile(org.uberfire.java.nio.file.Path visitedPath, BasicFileAttributes attrs) throws IOException {

                org.uberfire.backend.vfs.Path visitedVFSPath = Paths.convert(visitedPath);
                String fileName = visitedVFSPath.getFileName();
                File file = visitedPath.toFile();

                if (file.isFile()) {
                    if (fileName.endsWith("." + FormsMigrationConstants.LEGACY_FOMRS_EXTENSION)) {
                        try {
                            Form legacyForm = legacyFormSerializer.loadFormFromXML(cdiWrapper.getIOService().readAllString(visitedPath));

                            FormMigrationSummary summary = new FormMigrationSummary(new Resource<>(legacyForm, visitedVFSPath));

                            // Trying to lookup new form with same name!
                            String newFormFileName = fileName.substring(0, fileName.lastIndexOf(".") - 1) + FormsMigrationConstants.NEW_FOMRS_EXTENSION;
                            org.uberfire.java.nio.file.Path newFormPath = visitedPath.getParent().resolve(newFormFileName);

                            if (cdiWrapper.getIOService().exists(newFormPath)) {
                                Resource<FormDefinition> newFormResource = new Resource<>(cdiWrapper.getFormDefinitionSerializer().deserialize(cdiWrapper.getIOService().readAllString(newFormPath)), Paths.convert(newFormPath));
                                summary.setNewFormResource(newFormResource);
                            }

                            summaries.add(summary);
                        } catch (Exception e) {
                            system.err().println("Error reading form: " + fileName + ":\n");
                            e.printStackTrace(system.err());
                        }
                    }
                }
                return FileVisitResult.CONTINUE;
            }
        });

        system.console().format("\nProcessing module %s: %s forms found\n", workspaceProject.getName(), summaries.size());

        MigrationContext context = new MigrationContext(workspaceProject, weldContainer, cdiWrapper, system, summaries);

        pipeline.migrate(context);
    }

    private boolean validateTarget() {

        if (!config.getTarget().resolve("system").resolve(MigrationConstants.SYSTEM_GIT).toFile().exists()) {
            system.err().println(String.format("The target path (%s) looks like it doesn't contain an updated filesystem, %s must be excuted before migrating forms", niogitDir, ProjectMigrationTool.NAME.toUpperCase()));
            return false;
        }

        return true;
    }

    private void configureProperties() {
        system.setProperty(JGitFileSystemProviderConfiguration.GIT_NIO_DIR, niogitDir.getParent().toString());
        system.setProperty(JGitFileSystemProviderConfiguration.GIT_NIO_DIR_NAME, niogitDir.getFileName().toString());
        system.setProperty(JGitFileSystemProviderConfiguration.GIT_DAEMON_ENABLED, "false");
        system.setProperty(JGitFileSystemProviderConfiguration.GIT_SSH_ENABLED, "false");
    }
}
