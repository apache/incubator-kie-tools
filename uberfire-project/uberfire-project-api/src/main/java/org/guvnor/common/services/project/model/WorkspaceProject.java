/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.common.services.project.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.commons.data.Cacheable;

@Portable
/**
 * Each WorkspaceProject is in a single repository.
 * The workbench requires that there is a single parent pom.xml file in the repository root.
 * There can be child Modules, but they are optional.
 * A WorkspaceProject can have several Branches, but the WorkspaceProject model focuses on only one of them.
 * <BR>
 * The WorkspaceProject model contains the Repository field and OrganizationalUnit, but these are here only for convenience.
 * <b>The real WorkspaceProject root is the Branch root.</b>
 * Please do not use the Repository root path, this can point to any branch even to those that are not used.
 */
public class WorkspaceProject
        implements Cacheable {

    private Module mainModule;
    private boolean requiresRefresh = true;

    public WorkspaceProject() {
    }

    public WorkspaceProject(final Module mainModule) {
        this.mainModule = mainModule;
    }

    /**
     * @return The Module that exists in the Project root.
     */
    public Module getMainModule() {
        return mainModule;
    }

    @Override
    public boolean requiresRefresh() {
        return requiresRefresh;
    }

    /**
     * Name resolution sources in priority order: root pom.xml module name, root pom.xml artifactId and if everything else fails we use the repository alias.
     * @return Resolved name of the Project.
     */
    public String getName() {
        if (mainModule != null) {
            final String moduleName = mainModule.getModuleName();
            if (moduleName != null && !mainModule.getModuleName().trim().isEmpty()) {
                return mainModule.getModuleName();
            } else {
                return "null"; //FIXME: tiago
            }
        } else {
            return "null"; //FIXME: tiago
        }
    }

    @Override
    public void markAsCached() {
        this.requiresRefresh = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        WorkspaceProject workspaceProject = (WorkspaceProject) o;

        if (requiresRefresh != workspaceProject.requiresRefresh) {
            return false;
        }
        if (mainModule != null ? !mainModule.equals(workspaceProject.mainModule) : workspaceProject.mainModule != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (mainModule != null ? ~~mainModule.hashCode() : 0);
        result = 31 * result + (requiresRefresh ? 1 : 0);
        return result;
    }
}
