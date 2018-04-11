package org.kie.workbench.common.project;

import java.nio.file.Path;

import org.jboss.weld.environment.se.Weld;
import org.kie.workbench.common.migration.cli.ContainerHandler;
import org.kie.workbench.common.migration.cli.MigrationSetup;
import org.kie.workbench.common.migration.cli.MigrationTool;
import org.kie.workbench.common.migration.cli.NiogitDirUtil;
import org.kie.workbench.common.migration.cli.SystemAccess;
import org.kie.workbench.common.migration.cli.ToolConfig;
import org.kie.workbench.common.project.cli.ExternalMigrationService;
import org.kie.workbench.common.project.cli.InternalMigrationService;
import org.kie.workbench.common.project.cli.PromptService;

public class ProjectMigrationTool implements MigrationTool {

    public static final String NAME = "Project structure migration";

    private SystemAccess system;
    private ToolConfig config;
    private Path niogitDir;

    @Override
    public String getTitle() {
        return NAME;
    }

    @Override
    public String getDescription() {
        return "Moves old project structure to new modules structure";
    }

    @Override
    public Integer getPriority() {
        return 0;
    }

    @Override
    public boolean isSystemMigration() {
        return true;
    }

    @Override
    public void run(ToolConfig config, SystemAccess system) {

        this.config = config;
        this.system = system;
        this.niogitDir = config.getTarget();

        final PromptService promptService = new PromptService(system,
                                                              config);

        system.out().println("Starting project structure migration");

        if (validateTarget() && promptService.maybePromptForBackup()) {
            final ExternalMigrationService externalService = new ExternalMigrationService(system);
            externalService.moveSystemRepos(niogitDir);

            MigrationSetup.configureProperties(system,
                                               niogitDir);
            migrate();
        }
    }

    private boolean validateTarget() {
        if (NiogitDirUtil.isLegacyNiogitDir(config.getTarget())) {
            return true;
        } else {
            system.err().println(String.format("The target path looks like it already contains an updated filesystem: %s", niogitDir));
            return false;
        }
    }

    private void migrate() {
        final ContainerHandler container = new ContainerHandler(() -> new Weld().initialize());
        container.run(InternalMigrationService.class,
                      service -> service.migrateAllProjects(niogitDir),
                      error -> {
                          system.err().println("Error during migration: ");
                          error.printStackTrace(system.err());
                      });
        container.close();
    }
}
