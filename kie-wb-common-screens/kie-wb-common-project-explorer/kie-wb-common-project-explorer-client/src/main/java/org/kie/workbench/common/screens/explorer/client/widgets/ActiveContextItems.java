/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kie.workbench.common.screens.explorer.client.widgets;

import java.util.Map;
import java.util.Set;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.common.services.project.context.ProjectContextChangeEvent;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.kie.workbench.common.screens.explorer.client.utils.Utils;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderItemType;
import org.kie.workbench.common.screens.explorer.model.FolderListing;
import org.kie.workbench.common.screens.explorer.model.ProjectExplorerContent;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;

@EntryPoint
public class ActiveContextItems {

    protected Event<ProjectContextChangeEvent> contextChangedEvent;

    protected Caller<ExplorerService> explorerService;

    private OrganizationalUnit activeOrganizationalUnit;
    private Repository         activeRepository;
    private String             activeBranch;
    private Project            activeProject;
    private Package            activePackage;
    private FolderItem         activeFolderItem;
    private FolderListing      activeContent;
    private Set<Repository>    repositories;


    public ActiveContextItems() {
    }

    @Inject
    public ActiveContextItems(final Event<ProjectContextChangeEvent> contextChangedEvent,
                              final Caller<ExplorerService> explorerService) {
        this.contextChangedEvent = contextChangedEvent;
        this.explorerService = explorerService;
    }

    public OrganizationalUnit getActiveOrganizationalUnit() {
        return activeOrganizationalUnit;
    }

    public Repository getActiveRepository() {
        return activeRepository;
    }

    public String getActiveBranch() {
        return activeBranch;
    }

    public Project getActiveProject() {
        return activeProject;
    }

    public Package getActivePackage() {
        return activePackage;
    }

    public FolderItem getActiveFolderItem() {
        return activeFolderItem;
    }

    public FolderListing getActiveContent() {
        return activeContent;
    }

    public Set<Repository> getRepositories() {
        return repositories;
    }

    public void setRepositories( final Set<Repository> repositories ) {
        this.repositories = repositories;
    }

    boolean setupActiveProject( final ProjectExplorerContent content ) {
        if ( Utils.hasProjectChanged( content.getProject(),
                                      activeProject ) ) {
            activeProject = content.getProject();
            return true;
        } else {
            return false;
        }
    }

    public void setActiveBranch( final String activeBranch ) {
        this.activeBranch = activeBranch;
    }

    boolean setupActiveRepository( final ProjectExplorerContent content ) {
        if ( Utils.hasRepositoryChanged( content.getRepository(),
                                         activeRepository ) ) {
            activeRepository = content.getRepository();
            return true;
        } else {
            return false;
        }
    }

    public boolean setupActiveBranch( final ProjectExplorerContent content ) {
        if ( activeBranch != null && activeBranch.equals( content.getBranch() ) ) {
            return false;
        } else {
            setActiveBranch( content.getBranch() );
            return true;
        }
    }

    public void flush() {
        activeRepository = null;
        activeBranch = null;
        activeProject = null;
        activePackage = null;
        activeFolderItem = null;
    }

    boolean setupActiveOrganizationalUnit( final ProjectExplorerContent content ) {

        if ( Utils.hasOrganizationalUnitChanged( content.getOrganizationalUnit(),
                                                 activeOrganizationalUnit ) ) {
            activeOrganizationalUnit = content.getOrganizationalUnit();
            return true;
        } else {
            return false;
        }
    }

    boolean setupActiveFolderAndPackage( final ProjectExplorerContent content ) {
        if ( Utils.hasFolderItemChanged( content.getFolderListing().getItem(),
                                         activeFolderItem ) ) {
            activeFolderItem = content.getFolderListing().getItem();
            if ( activeFolderItem != null && activeFolderItem.getItem() != null && activeFolderItem.getItem() instanceof Package ) {
                activePackage = ( Package ) activeFolderItem.getItem();
            } else if ( activeFolderItem == null || activeFolderItem.getItem() == null ) {
                activePackage = null;
            }

            return true;
        } else {
            return false;
        }
    }

    void fireContextChangeEvent() {
        if ( activeFolderItem == null ) {
            contextChangedEvent.fire( new ProjectContextChangeEvent( activeOrganizationalUnit,
                                                                     activeRepository,
                                                                     activeBranch,
                                                                     activeProject ) );
            return;
        }

        if ( activeFolderItem.getItem() instanceof Package ) {
            activePackage = ( Package ) activeFolderItem.getItem();
            contextChangedEvent.fire( new ProjectContextChangeEvent( activeOrganizationalUnit,
                                                                     activeRepository,
                                                                     activeBranch,
                                                                     activeProject,
                                                                     activePackage ) );
        } else if ( activeFolderItem.getType().equals( FolderItemType.FOLDER ) ) {
            explorerService.call( getResolvePackageRemoteCallback() ).resolvePackage( activeFolderItem );
        }
    }

    private RemoteCallback<Package> getResolvePackageRemoteCallback() {
        return new RemoteCallback<Package>() {
            @Override
            public void callback( final Package pkg ) {
                if ( Utils.hasPackageChanged( pkg,
                                              activePackage ) ) {
                    activePackage = pkg;
                    contextChangedEvent.fire( new ProjectContextChangeEvent( activeOrganizationalUnit,
                                                                             activeRepository,
                                                                             activeBranch,
                                                                             activeProject,
                                                                             activePackage ) );
                } else {
                    contextChangedEvent.fire( new ProjectContextChangeEvent( activeOrganizationalUnit,
                                                                             activeRepository,
                                                                             activeBranch,
                                                                             activeProject ) );
                }
            }
        };
    }

    boolean isTheActiveRepository( final String alias ) {
        return activeRepository != null && activeRepository.getAlias().equals( alias );
    }

    void updateRepository( final String alias,
                           final Map<String, Object> environment ) {
        if ( repositories != null ) {
            for ( Repository repository : repositories ) {
                if ( repository.getAlias().equals( alias ) ) {
                    repository.getEnvironment().clear();
                    repository.getEnvironment().putAll( environment );
                }
            }
        }
    }

    public void setActiveContent( final FolderListing activeContent ) {
        this.activeContent = activeContent;
    }

    public void setActiveFolderItem( final FolderItem activeFolderItem ) {
        this.activeFolderItem = activeFolderItem;
    }

    public void flushActiveProject() {
        this.activeProject = null;
    }
}
