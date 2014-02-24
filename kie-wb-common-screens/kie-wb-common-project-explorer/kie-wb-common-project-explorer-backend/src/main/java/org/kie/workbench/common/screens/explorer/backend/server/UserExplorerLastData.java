package org.kie.workbench.common.screens.explorer.backend.server;

import java.util.HashSet;
import java.util.Set;

import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.service.Option;
import org.uberfire.backend.organizationalunit.OrganizationalUnit;
import org.uberfire.backend.repositories.Repository;

public class UserExplorerLastData {

    private LastPackage lastPackage = null;
    private LastFolderItem lastFolderItem = null;
    private Set<Option> options = new HashSet<Option>();

    public boolean isDataEmpty() {
        return lastPackage == null && lastFolderItem == null;
    }

    public boolean isEmpty() {
        return isDataEmpty() && options.isEmpty();
    }

    public void setFolderItem( final OrganizationalUnit organizationalUnit,
                               final Repository repository,
                               final Project project,
                               final FolderItem item ) {
        lastFolderItem = new LastFolderItem( organizationalUnit, repository, project, item );
    }

    public void setPackage( final OrganizationalUnit organizationalUnit,
                            final Repository repository,
                            final Project project,
                            final Package pkg ) {
        lastPackage = new LastPackage( organizationalUnit, repository, project, pkg );
    }

    public void setOptions( final Set<Option> options ) {
        this.options.clear();
        this.options.addAll( options );
    }

    public LastPackage getLastPackage() {
        return lastPackage;
    }

    public LastFolderItem getLastFolderItem() {
        return lastFolderItem;
    }

    public Set<Option> getOptions() {
        return options;
    }

    public boolean deleteProject( final Project project ) {
        boolean changed = false;
        if ( lastPackage != null && lastPackage.getProject().equals( project ) ) {
            lastPackage = null;
            changed = true;
        }
        if ( lastFolderItem != null && lastFolderItem.getProject().equals( project ) ) {
            lastFolderItem = null;
            changed = true;
        }
        return changed;
    }

    static class LastPackage {

        OrganizationalUnit organizationalUnit;
        Repository repository;
        Project project;
        org.guvnor.common.services.project.model.Package pkg;

        LastPackage() {

        }

        LastPackage( final OrganizationalUnit organizationalUnit,
                     final Repository repository,
                     final Project project,
                     final Package pkg ) {
            this.organizationalUnit = organizationalUnit;
            this.repository = repository;
            this.project = project;
            this.pkg = pkg;
        }

        OrganizationalUnit getOrganizationalUnit() {
            return organizationalUnit;
        }

        Repository getRepository() {
            return repository;
        }

        Project getProject() {
            return project;
        }

        Package getPkg() {
            return pkg;
        }
    }

    static class LastFolderItem {

        OrganizationalUnit organizationalUnit;
        Repository repository;
        Project project;
        FolderItem item;

        LastFolderItem() {

        }

        LastFolderItem( final OrganizationalUnit organizationalUnit,
                        final Repository repository,
                        final Project project,
                        final FolderItem item ) {
            this.organizationalUnit = organizationalUnit;
            this.repository = repository;
            this.project = project;
            this.item = item;
        }

        OrganizationalUnit getOrganizationalUnit() {
            return organizationalUnit;
        }

        Repository getRepository() {
            return repository;
        }

        Project getProject() {
            return project;
        }

        FolderItem getItem() {
            return item;
        }
    }
}
