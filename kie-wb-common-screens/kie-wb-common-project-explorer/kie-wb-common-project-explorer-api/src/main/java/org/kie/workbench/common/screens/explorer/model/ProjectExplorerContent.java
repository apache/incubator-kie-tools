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

package org.kie.workbench.common.screens.explorer.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ProjectExplorerContent {

    private OrganizationalUnit organizationalUnit;
    private Repository repository;
    private String branch;
    private Project project;

    private Set<OrganizationalUnit> organizationalUnits = new HashSet<OrganizationalUnit>();
    private Set<Repository> repositories = new HashSet<Repository>();
    private Set<Project> projects = new HashSet<Project>();
    private FolderListing folderListing;
    private Map<FolderItem, List<FolderItem>> siblings;

    public ProjectExplorerContent() {
    }

    public ProjectExplorerContent( final Set<OrganizationalUnit> organizationalUnits,
                                   final OrganizationalUnit organizationalUnit,
                                   final Set<Repository> repositories,
                                   final Repository repository,
                                   final String branch,
                                   final Set<Project> projects,
                                   final Project project,
                                   final FolderListing folderListing,
                                   final Map<FolderItem, List<FolderItem>> siblings ) {
        this.organizationalUnits = organizationalUnits;
        this.organizationalUnit = organizationalUnit;
        this.repositories = repositories;
        this.repository = repository;
        this.branch = branch;
        this.projects = projects;
        this.project = project;
        this.folderListing = folderListing;
        this.siblings = siblings;
    }

    public String getBranch() {
        return branch;
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

    public FolderListing getFolderListing() {
        return folderListing;
    }

    public Set<OrganizationalUnit> getOrganizationalUnits() {
        return Collections.unmodifiableSet( organizationalUnits );
    }

    public Set<Repository> getRepositories() {
        return Collections.unmodifiableSet( repositories );
    }

    public Set<Project> getProjects() {
        return Collections.unmodifiableSet( projects );
    }

    public Map<FolderItem, List<FolderItem>> getSiblings() {
        return siblings;
    }
}
