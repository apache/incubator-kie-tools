/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.guvnor.asset.management.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;

@Portable
public class RepositoryStructureModel {

    private POM pom;

    private Metadata POMMetaData;

    private Path pathToPOM;

    private List<String> modules = new ArrayList<String>();

    private Map<String, Project> modulesProject = new HashMap<String, Project>();

    private List<Project> orphanProjects = new ArrayList<Project>();

    private Map<String, POM> orphanProjectsPOM = new HashMap<String, POM>();

    private Boolean managed;

    public RepositoryStructureModel() {
    }

    public POM getPOM() {
        return pom;
    }

    public void setPOM(POM pom) {
        this.pom = pom;
    }

    public void setPOMMetaData(Metadata POMMetaData) {
        this.POMMetaData = POMMetaData;
    }

    public Metadata getPOMMetaData() {
        return POMMetaData;
    }

    public Path getPathToPOM() {
        return pathToPOM;
    }

    public void setPathToPOM(Path pathToPOM) {
        this.pathToPOM = pathToPOM;
    }

    public List<String> getModules() {
        return modules;
    }

    public void setModules(final Collection<String> modules) {
        this.modules.clear();
        for (final String module : modules) {
            this.modules.add(module);
        }
    }

    public boolean isMultiModule() {
        return pom != null;
    }

    public boolean isSingleProject() {
        return isManaged() && orphanProjects != null && orphanProjects.size() == 1;
    }

    public Boolean isManaged() {
        return managed != null && managed;
    }

    public void setManaged(Boolean managed) {
        this.managed = managed;
    }

    public Boolean getManaged() {
        return managed;
    }

    public List<Project> getOrphanProjects() {
        return orphanProjects;
    }

    public Project getSingleProject() {
        return orphanProjects != null && isSingleProject() ? orphanProjects.get(0) : null;
    }

    public POM getSingleProjectPOM() {
        Project project = getSingleProject();
        if (project != null) {
            return orphanProjectsPOM.get(project.getIdentifier());
        }
        return null;
    }

    public void setOrphanProjects(List<Project> orphanProjects) {
        this.orphanProjects = orphanProjects;
    }

    public Map<String, Project> getModulesProject() {
        return modulesProject;
    }

    public void setModulesProject(Map<String, Project> modulesProject) {
        this.modulesProject = modulesProject;
    }

    public Map<String, POM> getOrphanProjectsPOM() {
        return orphanProjectsPOM;
    }

    public void setOrphanProjectsPOM(Map<String, POM> orphanProjectsPOM) {
        this.orphanProjectsPOM = orphanProjectsPOM;
    }

    public POM getActivePom() {
        return isMultiModule() ? getPOM() : getSingleProjectPOM();
    }
}