/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.guvnor.common.services.project.client.context;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.guvnor.common.services.project.context.ProjectContextChangeHandle;
import org.guvnor.common.services.project.context.WorkspaceProjectContextChangeEvent;
import org.guvnor.common.services.project.context.WorkspaceProjectContextChangeHandler;
import org.guvnor.common.services.project.events.ModuleUpdatedEvent;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.UpdatedOrganizationalUnitEvent;
import org.uberfire.backend.vfs.Path;

/**
 * <p>
 * Project context contains the active organizational unit, project, module, and package (referred to as the "active unit" henceforth).
 * This context represents the active unit currently displayed in the UI by this workbench instance. If a screen or perspective that displays
 * an active unit is shown or hidden, it is that screen's responsibility (or that of a related component) to fire a WorkspaceProjectContextChangeEvent
 * to alter the context. This context should NOT directly observe deletion events. Instead it should be the relevant UI components job to observe such
 * events and react (likely by closing themselves, and changing the active unit in this context).
 * <p>
 * Each field can be null, then there is nothing active.
 * <p>
 * Only the WorkspaceProjectContextChangeEvent can change this and after each change we need to alert the change handlers.
 */
@ApplicationScoped
public class WorkspaceProjectContext {

    private OrganizationalUnit activeOrganizationalUnit;
    private WorkspaceProject activeWorkspaceProject;
    private Module activeModule;
    private Package activePackage;

    private Map<ProjectContextChangeHandle, WorkspaceProjectContextChangeHandler> changeHandlers = new HashMap<>();

    private Event<WorkspaceProjectContextChangeEvent> contextChangeEvent;

    public WorkspaceProjectContext() {
    }

    @Inject
    public WorkspaceProjectContext(final Event<WorkspaceProjectContextChangeEvent> contextChangeEvent) {
        this.contextChangeEvent = contextChangeEvent;
    }

    public void onOrganizationalUnitUpdated(@Observes final UpdatedOrganizationalUnitEvent event) {
        if (activeWorkspaceProject != null) {
            WorkspaceProject updatedWorkspaceProject = new WorkspaceProject(event.getOrganizationalUnit(),
                                                                            activeWorkspaceProject.getRepository(),
                                                                            activeWorkspaceProject.getBranch(),
                                                                            activeWorkspaceProject.getMainModule());
            contextChangeEvent.fire(new WorkspaceProjectContextChangeEvent(updatedWorkspaceProject,
                                                                           activeModule,
                                                                           activePackage));
        }
    }

    public void onModuleUpdated(@Observes final ModuleUpdatedEvent moduleUpdatedEvent) {
        if (activeModule != null && activeModule.getRootPath().equals(moduleUpdatedEvent.getOldModule().getRootPath())) {
            contextChangeEvent.fire(new WorkspaceProjectContextChangeEvent(new WorkspaceProject(activeWorkspaceProject.getOrganizationalUnit(),
                                                                                                activeWorkspaceProject.getRepository(),
                                                                                                activeWorkspaceProject.getBranch(),
                                                                                                moduleUpdatedEvent.getNewModule()),
                                                                           moduleUpdatedEvent.getNewModule()));
        }
    }

    public void onProjectContextChanged(@Observes final WorkspaceProjectContextChangeEvent event) {
        WorkspaceProjectContextChangeEvent previous = new WorkspaceProjectContextChangeEvent(activeWorkspaceProject,
                                                                                             activeModule,
                                                                                             activePackage);

        this.setActiveOrganizationalUnit(event.getOrganizationalUnit());
        this.setActiveWorkspaceProject(event.getWorkspaceProject());
        this.setActiveModule(event.getModule());
        this.setActivePackage(event.getPackage());

        for (WorkspaceProjectContextChangeHandler handler : changeHandlers.values()) {
            handler.onChange(previous,
                             event);
        }
    }

    public Optional<Path> getActiveRepositoryRoot() {
        return getActiveWorkspaceProject().map(proj -> proj.getBranch()).map(branch -> branch.getPath());
    }

    protected void setActiveOrganizationalUnit(final OrganizationalUnit activeOrganizationalUnit) {
        this.activeOrganizationalUnit = activeOrganizationalUnit;
    }

    public Optional<OrganizationalUnit> getActiveOrganizationalUnit() {
        return Optional.ofNullable(this.activeOrganizationalUnit);
    }

    protected void setActiveWorkspaceProject(final WorkspaceProject activeWorkspaceProject) {
        this.activeWorkspaceProject = activeWorkspaceProject;
    }

    public Optional<WorkspaceProject> getActiveWorkspaceProject() {
        return Optional.ofNullable(this.activeWorkspaceProject);
    }

    public Optional<Module> getActiveModule() {
        return Optional.ofNullable(this.activeModule);
    }

    protected void setActiveModule(final Module activeModule) {
        this.activeModule = activeModule;
    }

    public Optional<Package> getActivePackage() {
        return Optional.ofNullable(this.activePackage);
    }

    protected void setActivePackage(final Package activePackage) {
        this.activePackage = activePackage;
    }

    public ProjectContextChangeHandle addChangeHandler(final WorkspaceProjectContextChangeHandler changeHandler) {
        ProjectContextChangeHandle handle = new ProjectContextChangeHandle();
        changeHandlers.put(handle,
                           changeHandler);
        return handle;
    }

    public void removeChangeHandler(final ProjectContextChangeHandle projectContextChangeHandle) {
        changeHandlers.remove(projectContextChangeHandle);
    }

    public void updateProjectModule(final Module module) {
        this.activeModule = module;
    }
}
