package org.kie.workbench.common.screens.explorer.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.guvnor.common.services.project.model.Project;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.organizationalunit.OrganizationalUnit;
import org.uberfire.backend.repositories.Repository;

@Portable
public class ProjectExplorerContent {

    private OrganizationalUnit organizationalUnit;
    private Repository repository;
    private Project project;

    private Set<OrganizationalUnit> organizationalUnits = new HashSet<OrganizationalUnit>();
    private Set<Repository> repositories = new HashSet<Repository>();
    private Set<Project> projects = new HashSet<Project>();
    private FolderListing folderListing;

    public ProjectExplorerContent() {
    }

    public ProjectExplorerContent( final Set<OrganizationalUnit> organizationalUnits,
                                   final OrganizationalUnit organizationalUnit,
                                   final Set<Repository> repositories,
                                   final Repository repository,
                                   final Set<Project> projects,
                                   final Project project,
                                   final FolderListing folderListing ) {
        this.organizationalUnits = organizationalUnits;
        this.organizationalUnit = organizationalUnit;
        this.repositories = repositories;
        this.repository = repository;
        this.projects = projects;
        this.project = project;
        this.folderListing = folderListing;
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

}
