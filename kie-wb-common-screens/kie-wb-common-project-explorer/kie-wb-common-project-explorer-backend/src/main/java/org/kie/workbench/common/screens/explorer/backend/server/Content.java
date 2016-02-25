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

package org.kie.workbench.common.screens.explorer.backend.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderListing;
import org.kie.workbench.common.screens.explorer.service.ProjectExplorerContentQuery;

public class Content {

    private OrganizationalUnit selectedOrganizationalUnit = null;
    private Repository selectedRepository = null;
    private String selectedBranch = null;
    private Project selectedProject = null;
    private org.guvnor.common.services.project.model.Package selectedPackage = null;
    private FolderItem selectedItem = null;
    private FolderListing folderListing = null;
    private Map<FolderItem, List<FolderItem>> siblings = new HashMap<FolderItem, List<FolderItem>>();

    private Set<OrganizationalUnit> organizationalUnits;
    private Map<String, Repository> repositories;
    private Map<String, Project> projects;

    public Content( final ProjectExplorerContentQuery query ) {
        setSelectedOrganizationalUnit( query.getOrganizationalUnit() );
        setSelectedRepository( query.getRepository() );
        setSelectedBranch( query.getBranch() );
        setSelectedProject( query.getProject() );
        setSelectedPackage( query.getPkg() );
        setSelectedItem( query.getItem() );
    }

    public OrganizationalUnit getSelectedOrganizationalUnit() {
        return selectedOrganizationalUnit;
    }

    public void setSelectedOrganizationalUnit( OrganizationalUnit selectedOrganizationalUnit ) {
        this.selectedOrganizationalUnit = selectedOrganizationalUnit;
    }

    public Repository getSelectedRepository() {
        return selectedRepository;
    }

    public void setSelectedRepository( Repository selectedRepository ) {
        this.selectedRepository = selectedRepository;
    }

    public Project getSelectedProject() {
        return selectedProject;
    }

    public void setSelectedProject( Project selectedProject ) {
        this.selectedProject = selectedProject;
    }

    public Package getSelectedPackage() {
        return selectedPackage;
    }

    public void setSelectedPackage( Package selectedPackage ) {
        this.selectedPackage = selectedPackage;
    }

    public FolderItem getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem( FolderItem selectedItem ) {
        this.selectedItem = selectedItem;
    }

    public FolderListing getFolderListing() {
        return folderListing;
    }

    public void setFolderListing( FolderListing folderListing ) {
        this.folderListing = folderListing;
    }

    public Map<FolderItem, List<FolderItem>> getSiblings() {
        return siblings;
    }

    public void setSiblings( Map<FolderItem, List<FolderItem>> siblings ) {
        this.siblings = siblings;
    }

    public Set<OrganizationalUnit> getOrganizationalUnits() {
        return organizationalUnits;
    }

    public void setOrganizationalUnits( Set<OrganizationalUnit> organizationalUnits ) {
        this.organizationalUnits = organizationalUnits;
    }

    public Map<String, Repository> getRepositories() {
        return repositories;
    }

    public void setRepositories( Map<String, Repository> repositories ) {
        this.repositories = repositories;
    }

    public Map<String, Project> getProjects() {
        return projects;
    }

    public void setProjects( Map<String, Project> projects ) {
        this.projects = projects;
    }

    public String getSelectedBranch() {
        return selectedBranch;
    }

    public void setSelectedBranch( String selectedBranch ) {
        this.selectedBranch = selectedBranch;
    }
}
