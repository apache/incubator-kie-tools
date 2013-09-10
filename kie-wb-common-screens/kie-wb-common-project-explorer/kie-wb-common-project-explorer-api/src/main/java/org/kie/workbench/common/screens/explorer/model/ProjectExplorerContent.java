package org.kie.workbench.common.screens.explorer.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.organizationalunit.OrganizationalUnit;
import org.uberfire.backend.repositories.Repository;

@Portable
public class ProjectExplorerContent {

    private OrganizationalUnit organizationalUnit;
    private Repository repository;
    private Project project;
    private Package pkg;

    private Set<OrganizationalUnit> organizationalUnits = new HashSet<OrganizationalUnit>();
    private Set<Repository> repositories = new HashSet<Repository>();
    private Set<Project> projects = new HashSet<Project>();
    private Set<Package> packages = new HashSet<Package>();
    private Collection<FolderItem> items;

    public ProjectExplorerContent() {
    }

    public ProjectExplorerContent( final Set<OrganizationalUnit> organizationalUnits,
                                   final OrganizationalUnit organizationalUnit,
                                   final Set<Repository> repositories,
                                   final Repository repository,
                                   final Set<Project> projects,
                                   final Project project,
                                   final Set<Package> packages,
                                   final Package pkg,
                                   final Collection<FolderItem> items ) {
        this.organizationalUnits = organizationalUnits;
        this.organizationalUnit = organizationalUnit;
        this.repositories = repositories;
        this.repository = repository;
        this.projects = projects;
        this.project = project;
        this.packages = packages;
        this.pkg = pkg;
        this.items = items;
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

    public Package getPackage() {
        return pkg;
    }

    public Collection<FolderItem> getItems() {
        return items;
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

    public Set<Package> getPackages() {
        return Collections.unmodifiableSet( packages );
    }

}
