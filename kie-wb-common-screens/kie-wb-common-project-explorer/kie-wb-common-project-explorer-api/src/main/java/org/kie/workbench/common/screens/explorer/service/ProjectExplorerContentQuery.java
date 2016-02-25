/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

import java.util.Set;

import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.screens.explorer.model.FolderItem;

@Portable
public class ProjectExplorerContentQuery {

    private OrganizationalUnit organizationalUnit = null;
    private Repository repository = null;
    private String branch = null;
    private Project project = null;
    private org.guvnor.common.services.project.model.Package pkg = null;
    private FolderItem item = null;
    private ActiveOptions options = null;

    public ProjectExplorerContentQuery() {
    }

    public ProjectExplorerContentQuery( final OrganizationalUnit organizationalUnit ) {
        this.organizationalUnit = organizationalUnit;
    }

    public ProjectExplorerContentQuery( final OrganizationalUnit organizationalUnit,
                                        final Repository repository,
                                        final String branch) {
        this.organizationalUnit = organizationalUnit;
        this.repository = repository;
        this.branch = branch;
    }

    public ProjectExplorerContentQuery( final OrganizationalUnit organizationalUnit,
                                        final Repository repository,
                                        final String branch,
                                        final Project project ) {
        this.organizationalUnit = organizationalUnit;
        this.repository = repository;
        this.branch = branch;
        this.project = project;
    }

    public ProjectExplorerContentQuery( final OrganizationalUnit organizationalUnit,
                                        final Repository repository,
                                        final String branch,
                                        final Project project,
                                        final Package pkg ) {
        this.organizationalUnit = organizationalUnit;
        this.repository = repository;
        this.branch = branch;
        this.project = project;
        this.pkg = pkg;
    }

    public ProjectExplorerContentQuery( final OrganizationalUnit organizationalUnit,
                                        final Repository repository,
                                        final String branch,
                                        final Project project,
                                        final Package pkg,
                                        final FolderItem item ) {
        this.organizationalUnit = organizationalUnit;
        this.repository = repository;
        this.branch = branch;
        this.project = project;
        this.pkg = pkg;
        this.item = item;
    }

    public ProjectExplorerContentQuery( final OrganizationalUnit organizationalUnit,
                                        final Repository repository,
                                        final String branch,
                                        final Project project,
                                        final ActiveOptions activeOptions ) {
        this.organizationalUnit = organizationalUnit;
        this.repository = repository;
        this.branch = branch;
        this.project = project;
        this.options = activeOptions;
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

    public ActiveOptions getOptions() {
        return options;
    }

    public String getBranch() {
        return branch;
    }

    public void setOptions( ActiveOptions options ) {
        this.options = options;
    }

}
