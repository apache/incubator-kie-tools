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
package org.guvnor.common.services.project.context;

import java.util.Objects;

import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.jboss.errai.common.client.api.annotations.LocalEvent;
import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * <p>An event raised when the Project Context changes.
 *
 * <p>This must be {@link LocalEvent @LocalEvent} because it should not be
 * observed on the server when fired from the client, but it is {@link Portable @Portable} because
 * it is used as a return type from some RPC methods.
 */
@Portable
@LocalEvent
public class WorkspaceProjectContextChangeEvent {

    private final OrganizationalUnit ou;
    private final WorkspaceProject workspaceProject;
    private final Module module;
    private final Package pkg;

    public WorkspaceProjectContextChangeEvent() {
        ou = null;
        workspaceProject = null;
        module = null;
        pkg = null;
    }

    public WorkspaceProjectContextChangeEvent(final OrganizationalUnit ou) {
        this.ou = ou;
        this.workspaceProject = null;
        this.module = null;
        this.pkg = null;
    }

    public WorkspaceProjectContextChangeEvent(final WorkspaceProject workspaceProject) {
        this(workspaceProject,
             null);
    }

    public WorkspaceProjectContextChangeEvent(final WorkspaceProject workspaceProject,
                                              final Module module) {
        this(workspaceProject,
             module,
             null);
    }

    public WorkspaceProjectContextChangeEvent(final WorkspaceProject workspaceProject,
                                              final Module module,
                                              final Package pkg) {
        this.ou = workspaceProject != null ? workspaceProject.getOrganizationalUnit() : null;
        this.workspaceProject = workspaceProject;
        this.module = module;
        this.pkg = pkg;
    }

    public OrganizationalUnit getOrganizationalUnit() {
        return ou;
    }

    public WorkspaceProject getWorkspaceProject() {
        return workspaceProject;
    }

    public Module getModule() {
        return module;
    }

    public Package getPackage() {
        return pkg;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((module == null) ? 0 : module.hashCode());
        result = prime * result + ((ou == null) ? 0 : ou.hashCode());
        result = prime * result + ((pkg == null) ? 0 : pkg.hashCode());
        result = prime * result + ((workspaceProject == null) ? 0 : workspaceProject.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || getClass() != obj.getClass()) {
            return false;
        } else {
            WorkspaceProjectContextChangeEvent other = (WorkspaceProjectContextChangeEvent) obj;
            return Objects.equals(module, other.module)
                    && Objects.equals(ou, other.ou)
                    && Objects.equals(pkg, other.pkg)
                    && Objects.equals(workspaceProject, other.workspaceProject);
        }
    }

    @Override
    public String toString() {
        return "WorkspaceProjectContextChangeEvent [ou=" + ou + ", workspaceProject=" + workspaceProject + ", module=" + module + ", pkg=" + pkg + "]";
    }
}
