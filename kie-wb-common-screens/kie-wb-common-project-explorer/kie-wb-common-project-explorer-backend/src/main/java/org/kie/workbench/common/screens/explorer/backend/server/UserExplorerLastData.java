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

import java.util.HashSet;
import java.util.Set;

import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.service.ActiveOptions;
import org.kie.workbench.common.screens.explorer.service.Option;

public class UserExplorerLastData {

    public static final UserExplorerLastData EMPTY = new UserExplorerLastData();

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
                               final String branch,
                               final Project project,
                               final FolderItem item ) {
        lastFolderItem = new LastFolderItem( organizationalUnit, repository, branch, project, item );
    }

    public void setPackage( final OrganizationalUnit organizationalUnit,
                            final Repository repository,
                            final String branch,
                            final Project project,
                            final Package pkg ) {
        lastPackage = new LastPackage( organizationalUnit, repository, branch, project, pkg );
    }

    public void setOptions( final ActiveOptions options ) {
        this.options.clear();
        this.options.addAll( options.getValues() );
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
        String branch;
        Project project;
        org.guvnor.common.services.project.model.Package pkg;

        LastPackage() {

        }

        LastPackage( final OrganizationalUnit organizationalUnit,
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

        OrganizationalUnit getOrganizationalUnit() {
            return organizationalUnit;
        }

        Repository getRepository() {
            return repository;
        }

        String getBranch() {
            return branch;
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
        String branch;
        Project project;
        FolderItem item;

        LastFolderItem() {

        }

        LastFolderItem( final OrganizationalUnit organizationalUnit,
                        final Repository repository,
                        final String branch,
                        final Project project,
                        final FolderItem item ) {
            this.organizationalUnit = organizationalUnit;
            this.repository = repository;
            this.branch = branch;
            this.project = project;
            this.item = item;
        }

        OrganizationalUnit getOrganizationalUnit() {
            return organizationalUnit;
        }

        Repository getRepository() {
            return repository;
        }

        String getBranch() {
            return branch;
        }

        Project getProject() {
            return project;
        }

        FolderItem getItem() {
            return item;
        }
    }
}
