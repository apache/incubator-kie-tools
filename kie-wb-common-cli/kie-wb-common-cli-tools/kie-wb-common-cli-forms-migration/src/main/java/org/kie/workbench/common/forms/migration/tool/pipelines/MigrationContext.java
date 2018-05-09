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

package org.kie.workbench.common.forms.migration.tool.pipelines;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.jboss.weld.environment.se.WeldContainer;
import org.kie.workbench.common.forms.migration.tool.FormMigrationSummary;
import org.kie.workbench.common.forms.migration.tool.cdi.FormsMigrationServicesCDIWrapper;
import org.kie.workbench.common.migration.cli.MigrationServicesCDIWrapper;
import org.kie.workbench.common.migration.cli.SystemAccess;

public class MigrationContext {

    private final WorkspaceProject workspaceProject;
    private final WeldContainer weldContainer;
    private final FormsMigrationServicesCDIWrapper formCdiWrapper;
    private final MigrationServicesCDIWrapper migrationServicesCDIWrapper;
    private final SystemAccess system;
    private final List<FormMigrationSummary> summaries;
    private final List<FormMigrationSummary> extraSummaries = new ArrayList<>();

    public MigrationContext(WorkspaceProject workspaceProject, WeldContainer weldContainer, FormsMigrationServicesCDIWrapper formsMigrationServicesCDIWrapper, SystemAccess system, List<FormMigrationSummary> summaries, MigrationServicesCDIWrapper migrationServicesCDIWrapper) {
        this.workspaceProject = workspaceProject;
        this.weldContainer = weldContainer;
        this.formCdiWrapper = formsMigrationServicesCDIWrapper;
        this.system = system;
        this.summaries = summaries;
        this.migrationServicesCDIWrapper = migrationServicesCDIWrapper;
    }

    public WorkspaceProject getWorkspaceProject() {
        return workspaceProject;
    }

    public WeldContainer getWeldContainer() {
        return weldContainer;
    }

    public FormsMigrationServicesCDIWrapper getFormCDIWrapper() {
        return formCdiWrapper;
    }

    public MigrationServicesCDIWrapper getMigrationServicesCDIWrapper() {
        return migrationServicesCDIWrapper;
    }

    public SystemAccess getSystem() {
        return system;
    }

    public Collection<FormMigrationSummary> getSummaries() {
        return summaries;
    }

    public List<FormMigrationSummary> getExtraSummaries() {
        return extraSummaries;
    }
}
