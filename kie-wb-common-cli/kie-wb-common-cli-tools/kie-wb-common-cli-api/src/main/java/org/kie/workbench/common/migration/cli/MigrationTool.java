package org.kie.workbench.common.migration.cli;

public interface MigrationTool {

    String getTitle();

    String getDescription();

    Integer getPriority();

    boolean isSystemMigration();

    void run(ToolConfig config, SystemAccess system);
}
