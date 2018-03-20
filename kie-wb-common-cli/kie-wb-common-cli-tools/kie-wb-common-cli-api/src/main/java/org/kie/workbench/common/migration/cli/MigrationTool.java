package org.kie.workbench.common.migration.cli;

public interface MigrationTool {

    String getTitle();

    String getDescription();

    Integer getPriority();

    void run(ToolConfig config, SystemAccess system);
}
