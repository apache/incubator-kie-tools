package org.kie.workbench.common.migration;

import java.util.logging.LogManager;

import org.kie.workbench.common.migration.cli.MigrationApp;

public class Main {

    public static void main(String[] args) {

        disableLogging();

        new MigrationApp(args).start();
    }

    private static void disableLogging() {
        LogManager.getLogManager().reset();
    }
}
