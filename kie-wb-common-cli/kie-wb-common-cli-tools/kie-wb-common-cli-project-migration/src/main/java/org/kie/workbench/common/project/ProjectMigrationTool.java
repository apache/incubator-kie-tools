package org.kie.workbench.common.project;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.kie.workbench.common.migration.cli.MigrationConstants;
import org.kie.workbench.common.migration.cli.SystemAccess;
import org.kie.workbench.common.migration.cli.ToolConfig;
import org.kie.workbench.common.migration.cli.MigrationTool;
import org.kie.workbench.common.project.cli.ExternalMigrationService;
import org.kie.workbench.common.project.cli.InternalMigrationService;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProviderConfiguration;

public class ProjectMigrationTool implements MigrationTool {

    public static final String NAME = "Project structure migration";

    private SystemAccess system;
    private ToolConfig config;
    private Path niogitDir;
    private ExternalMigrationService externalService;

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

        system.out().println("Starting project structure migration");

        if(validateTarget() && maybePromptForBackup()) {
            this.externalService = new ExternalMigrationService(system);

            externalService.moveSystemRepos(niogitDir);

            configureProperties();
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

    private boolean maybePromptForBackup() {
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

    private void configureProperties() {
        system.setProperty(JGitFileSystemProviderConfiguration.GIT_NIO_DIR, niogitDir.getParent().toString());
        system.setProperty(JGitFileSystemProviderConfiguration.GIT_NIO_DIR_NAME, niogitDir.getFileName().toString());
        system.setProperty(JGitFileSystemProviderConfiguration.GIT_DAEMON_ENABLED, "false");
        system.setProperty(JGitFileSystemProviderConfiguration.GIT_SSH_ENABLED, "false");
    }

    private static InternalMigrationService loadInternalService(WeldContainer container) {
        return container.instance().select(InternalMigrationService.class).get();
    }
}
