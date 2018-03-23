package org.kie.workbench.common.project;

import java.nio.file.Path;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.kie.workbench.common.migration.cli.MigrationConstants;
import org.kie.workbench.common.migration.cli.MigrationTool;
import org.kie.workbench.common.migration.cli.SystemAccess;
import org.kie.workbench.common.migration.cli.ToolConfig;
import org.kie.workbench.common.project.cli.ExternalMigrationService;
import org.kie.workbench.common.project.cli.InternalMigrationService;
import org.kie.workbench.common.project.cli.MigrationSetup;
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

        if (config.getTarget().resolve("system").resolve(MigrationConstants.SYSTEM_GIT).toFile().exists()) {
            system.err().println(String.format("The target path looks like it already contains an updated filesystem: %s", niogitDir));
            return false;
        }

        return true;
    }

    private void migrate() {
        WeldContainer container = null;
        try {
            container = new Weld().initialize();
            InternalMigrationService internalService = loadInternalService(container);
            internalService.migrateAllProjects(niogitDir);
        } catch (Throwable t) {
            system.err().println("Error during migration: ");
            t.printStackTrace(system.err());
        } finally {
            if (container != null && container.isRunning()) {
                quietShutdown(container);
            }
        }
    }

    private void quietShutdown(WeldContainer container) {
        try {
            container.shutdown();
        } catch (Throwable ignore) {
            // Suppress exceptions from bad shutdown
        }
    }

    private static InternalMigrationService loadInternalService(WeldContainer container) {
        return container.instance().select(InternalMigrationService.class).get();
    }
}
