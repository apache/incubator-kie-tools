/*
 * Copyright 2014 JBoss Inc
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

package org.kie.workbench.common.screens.explorer.service;

import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.screens.explorer.model.FolderItem;

import java.util.Set;

@Portable
public class ProjectExplorerContentQuery {

    private OrganizationalUnit organizationalUnit = null;
    private Repository repository = null;
    private Project project = null;
    private org.guvnor.common.services.project.model.Package pkg = null;
    private FolderItem item = null;
    private Set<Option> options = null;
    private boolean branchChangeFlag;

    public ProjectExplorerContentQuery() {
    }

    public ProjectExplorerContentQuery(OrganizationalUnit organizationalUnit) {
        this.organizationalUnit = organizationalUnit;
    }

    public ProjectExplorerContentQuery(OrganizationalUnit organizationalUnit, Repository repository) {
        this.organizationalUnit = organizationalUnit;
        this.repository = repository;
    }

    public ProjectExplorerContentQuery(OrganizationalUnit organizationalUnit, Repository repository, Project project) {
        this.organizationalUnit = organizationalUnit;
        this.repository = repository;
        this.project = project;
    }

    public ProjectExplorerContentQuery(OrganizationalUnit organizationalUnit, Repository repository, Project project, Package pkg) {
        this.organizationalUnit = organizationalUnit;
        this.repository = repository;
        this.project = project;
        this.pkg = pkg;
    }

    public ProjectExplorerContentQuery(OrganizationalUnit organizationalUnit, Repository repository, Project project, Package pkg, FolderItem item) {
        this.organizationalUnit = organizationalUnit;
        this.repository = repository;
        this.project = project;
        this.pkg = pkg;
        this.item = item;
    }


    public OrganizationalUnit getOrganizationalUnit() {
        return organizationalUnit;
    }

    public Repository getRepository() {
        return repository;
    }

    public Project getProject() {
        return project;
    }

    public Package getPkg() {
        return pkg;
    }

    public FolderItem getItem() {
        return item;
    }

    public Set<Option> getOptions() {
        return options;
    }

    public void setOptions(Set<Option> options) {
        this.options = options;
    }

    public void setBranchChangeFlag(boolean branchChangeFlag) {
        this.branchChangeFlag = branchChangeFlag;
    }

    public boolean isBranchChangeFlag() {
        return branchChangeFlag;
    }
}
