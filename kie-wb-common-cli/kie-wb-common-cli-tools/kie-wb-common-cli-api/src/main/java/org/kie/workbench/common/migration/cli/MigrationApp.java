package org.kie.workbench.common.migration.cli;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.Stream;

import org.apache.commons.cli.ParseException;
import org.jboss.weld.environment.se.Weld;

public class MigrationApp {

    private List<MigrationTool> migrationTools = new ArrayList<>();

    private SystemAccess system = new RealSystemAccess();

    private ToolConfig actualConfig;

    public MigrationApp(String args[]) {

        actualConfig = parseToolConfigOrExit(args);

        ServiceLoader<MigrationTool> migrationLoader = ServiceLoader.load(MigrationTool.class);

        migrationLoader.forEach(migrationTool -> migrationTools.add(migrationTool));

        Collections.sort(migrationTools, Comparator.comparing(MigrationTool::getPriority));
    }

    public void start() {
        validateTarget(actualConfig.getTarget());
        if (isNiogit(actualConfig.getTarget())) {
            runTasks(actualConfig, migrationTools.toArray(new MigrationTool[migrationTools.size()]), this::exit);
        } else if (isGitRepositoryRoot(actualConfig.getTarget())) {
            setupTmpNiogitAndRunTasks();
        } else {
            system.err().println(String.format("The given target [%s] is not a repository root or an niogit directory.", actualConfig.getTarget()));
            system.exit(1);
        }

    }

    private void setupTmpNiogitAndRunTasks() {
        Path tmpNiogit = createTempNiogit();
        try {
            ToolConfig effectiveConfig = new ToolConfig(tmpNiogit, actualConfig.isBatch());
            Path outputFolder = promptForOutputPath();
            importActualTargetIntoNiogit(actualConfig.getTarget(), tmpNiogit);
            MigrationTool[] nonSystemTools = migrationTools.stream()
                                                           .filter(tool -> !tool.isSystemMigration())
                                                           .toArray(MigrationTool[]::new);
            runTasks(effectiveConfig,
                     nonSystemTools,
                     () -> {
                         copyBareReposToOutputFolder(tmpNiogit, outputFolder);
                         deleteTempDirectory(tmpNiogit);
                         exit();
                     });
        } finally {
            if (tmpNiogit.toFile().exists()) {
                deleteTempDirectory(tmpNiogit);
            }
        }
    }

    private void copyBareReposToOutputFolder(Path tmpNiogit, Path outputFolder) {
        try {
            final Path migrationSpace = tmpNiogit.resolve("migrationSpace");
            Optional.ofNullable(migrationSpace.toFile().listFiles())
            .map(Arrays::stream)
            .orElse(Stream.empty())
            .filter(file -> file.isDirectory() && file.getName().endsWith(".git"))
            .forEach(gitDir -> {
                Path path = gitDir.toPath();
                try {
                    system.copyDirectory(path, outputFolder.resolve(path.getFileName()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Throwable t) {
            system.err().println("Unable to output migrated projects.");
            t.printStackTrace(system.err());
            system.exit(1);
        }
    }

    private Path promptForOutputPath() {
        final Path rawPath = Paths.get(system.console().readLine("Enter locaton migrated repository output: "));
        if (rawPath.isAbsolute()) {
            return rawPath;
        } else {
            return system.currentWorkingDirectory().resolve(rawPath).normalize();
        }
    }

    private void deleteTempDirectory(Path tmpNiogit) {
        try {
            system.recursiveDelete(tmpNiogit);
        } catch (IOException ex) {
            system.err().println(String.format("Unable to delete temporary niogit directory [%s].", tmpNiogit));
        }
    }

    private boolean isGitRepositoryRoot(Path target) {
        return target.resolve(".git").toFile().exists();
    }

    private void importActualTargetIntoNiogit(Path actualTarget, Path tmpNiogit) {
        MigrationSetup.configureProperties(system, tmpNiogit);
        final ContainerHandler container = new ContainerHandler(() -> new Weld().initialize());
        system.out().printf("Loading target [%s] into temporary niogit [%s].\n", actualTarget, tmpNiogit);
        container.run(TemporaryNiogitService.class,
                      service -> service.importProjects(actualTarget),
                      error -> {
                          system.err().println(String.format("Failed to load project at given path [%s].", error));
                          error.printStackTrace(system.err());
                          container.close();
                          system.exit(1);
                      });
        container.close();
    }

    private Path createTempNiogit() {
        try {
            return system.createTemporaryDirectory("niogit");
        } catch (IOException e) {
            system.err().println("Unable to create temporary niogit directory. Exiting.");
            return system.exit(1);
        }
    }

    private boolean isNiogit(Path target) {
        Path legacySystemGit = target.resolve(MigrationConstants.SYSTEM_GIT);
        Path systemGit = target.resolve("system").resolve(MigrationConstants.SYSTEM_GIT);
        return legacySystemGit.toFile().exists() || systemGit.toFile().exists();
    }

    private void runTasks(ToolConfig effectiveConfig, MigrationTool[] tools, Runnable exitAction) {
        if(effectiveConfig.isBatch()) {
            execute(effectiveConfig, exitAction, tools);
            exitAction.run();
        } else {
            printWizard(effectiveConfig, tools, exitAction);
        }
    }

    private void printWizard(ToolConfig config, MigrationTool[] tools, Runnable exitAction) {
        system.out().println("\n");
        system.out().println("Kie Workbench Migration Tool");
        system.out().println("============================\n");

        Integer index;

        final SystemAccess.Console console = system.console();

        Map<String, Runnable> menuOptions = new HashMap<>();

        for(index = 1; index <= tools.length; index ++) {
            MigrationTool tool = tools[index -1];

            console.format("%s) %s (%s)\n", index, tool.getTitle().toUpperCase(), tool.getDescription());

            menuOptions.put(index.toString(), () -> execute(config, exitAction, tool));
        }

        console.format("%s) ALL\n", index);
        menuOptions.put(index.toString(), () -> execute(config, exitAction, tools));

        index++;

        console.format("%s) EXIT\n\n", index);
        menuOptions.put(index.toString(), exitAction);

        String response;

        do {
            response = console.readLine("Choose one option [1-" + index + "]: ").toLowerCase();
        } while (!menuOptions.containsKey(response));

        menuOptions.get(response).run();
    }

    private void execute(ToolConfig config, Runnable exitAction, MigrationTool... tools) {
        for (MigrationTool tool : tools) {
            tool.run(config, system);
        }

        if(!config.isBatch()) {
            printWizard(config, tools, exitAction);
        }
    }

    private void exit() {
        system.out().println("\nGoodbye!");
        system.exit(0);
    }

    private ToolConfig parseToolConfigOrExit(String[] args) {
        ToolConfig.ToolConfigFactory configFactory = new ToolConfig.DefaultFactory();
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

    private void validateTarget(Path target) {
        Optional<String> errorMessage = Optional.empty();
        try {
            File dirFile = target.toFile();
            if (!dirFile.exists()) {
                errorMessage = Optional.of(String.format("The target path does not exist: %s", target));
            }
            else if (!dirFile.isDirectory()) {
                errorMessage = Optional.of(String.format("The target path is not a directory: %s", target));
            }
        } catch (UnsupportedOperationException e) {
            errorMessage = Optional.of(String.format("The target path must be a file: %s", target));
        }

        errorMessage.ifPresent(msg -> {
            system.err().println(msg);
            system.exit(1);
        });
    }
}
