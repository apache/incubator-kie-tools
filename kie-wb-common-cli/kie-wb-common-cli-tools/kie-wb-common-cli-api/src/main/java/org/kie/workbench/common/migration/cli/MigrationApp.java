package org.kie.workbench.common.migration.cli;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;

import org.apache.commons.cli.ParseException;

public class MigrationApp {

    private List<MigrationTool> migrationTools = new ArrayList<>();

    private SystemAccess system = new RealSystemAccess();

    private ToolConfig toolConfig;

    public MigrationApp(String args[]) {

        toolConfig = parseToolConfigOrExit(args);

        ServiceLoader<MigrationTool> migrationLoader = ServiceLoader.load(MigrationTool.class);

        migrationLoader.forEach(migrationTool -> migrationTools.add(migrationTool));

        Collections.sort(migrationTools, Comparator.comparing(MigrationTool::getPriority));
    }

    public void start() {

        validateTarget();

        if(toolConfig.isBatch()) {
            runBatch();
        } else {
            printWizard();
        }
    }

    private void printWizard() {
        system.out().println("\n");
        system.out().println("Kie Workbench Migration Tool");
        system.out().println("============================\n");

        Integer index;

        final SystemAccess.Console console = system.console();

        Map<String, Runnable> menuOptions = new HashMap<>();

        for(index = 1; index <= migrationTools.size(); index ++) {
            MigrationTool tool = migrationTools.get(index -1);

            console.format("%s) %s (%s)\n", index, tool.getTitle().toUpperCase(), tool.getDescription());

            menuOptions.put(index.toString(), () -> execute(tool));
        }

        console.format("%s) ALL\n", index);
        menuOptions.put(index.toString(), () -> execute(migrationTools.toArray(new MigrationTool[migrationTools.size()])));

        index++;

        console.format("%s) EXIT\n\n", index);
        menuOptions.put(index.toString(), this::exit);

        String response;

        do {
            response = console.readLine("Choose one option [1-" + index + "]: ").toLowerCase();
        } while (!menuOptions.containsKey(response));

        menuOptions.get(response).run();
    }

    private void runBatch() {
        execute(migrationTools.toArray(new MigrationTool[migrationTools.size()]));
    }

    private void execute(MigrationTool... tools) {
        for(MigrationTool tool : tools) {
            tool.run(toolConfig, system);
        }

        if(!toolConfig.isBatch()) {
            printWizard();
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

    private void validateTarget() {
        Path niogitDir = toolConfig.getTarget();

        Optional<String> errorMessage = Optional.empty();
        try {
            File dirFile = niogitDir.toFile();
            if (!dirFile.exists()) {
                errorMessage = Optional.of(String.format("The target path does not exist: %s", niogitDir));
            }
            else if (!dirFile.isDirectory()) {
                errorMessage = Optional.of(String.format("The target path is not a directory: %s", niogitDir));
            }
        } catch (UnsupportedOperationException e) {
            errorMessage = Optional.of(String.format("The target path must be a file: %s", niogitDir));
        }

        errorMessage.ifPresent(msg -> {
            system.err().println(msg);
            system.exit(1);
        });
    }
}
