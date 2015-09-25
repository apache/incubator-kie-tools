/*
 * Copyright 2015 JBoss Inc
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
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.common.services.project.context.ProjectContextChangeEvent;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.explorer.client.utils.Utils;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderItemType;
import org.kie.workbench.common.screens.explorer.model.FolderListing;
import org.kie.workbench.common.screens.explorer.model.ProjectExplorerContent;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;

@ApplicationScoped
public class ActiveContextItems {

    @Inject
    protected Event<ProjectContextChangeEvent> contextChangedEvent;

    @Inject
    protected Caller<ExplorerService> explorerService;

    private OrganizationalUnit activeOrganizationalUnit;
    private Repository activeRepository;
    private Project activeProject;
    private Package activePackage;
    private FolderItem activeFolderItem;
    private FolderListing activeContent;
    private Set<Repository> repositories;

    public OrganizationalUnit getActiveOrganizationalUnit() {
        return activeOrganizationalUnit;
    }

    public Repository getActiveRepository() {
        return activeRepository;
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

    public void setActiveContent( FolderListing activeContent ) {
        this.activeContent = activeContent;
    }

    public void setActiveProject( Project activeProject ) {
        this.activeProject = activeProject;
    }

    public void setActiveFolderItem( FolderItem activeFolderItem ) {
        this.activeFolderItem = activeFolderItem;
    }

    public void setActivePackage( Package activePackage ) {
        this.activePackage = activePackage;
    }

    public void setActiveRepository( Repository repository ) {
        this.activeRepository = repository;
    }

    public void setActiveOrganizationalUnit( OrganizationalUnit organizationalUnit ) {
        this.activeOrganizationalUnit = organizationalUnit;
    }

    public void setRepositories( final Set<Repository> repositories ) {
        this.repositories = repositories;
    }

    public void flush() {
        activeRepository = null;
        activeProject = null;
        activePackage = null;
        activeFolderItem = null;
    }

    boolean setupActiveProject( final ProjectExplorerContent content ) {
        if ( Utils.hasProjectChanged( content.getProject(),
                                      activeProject ) ) {
            setActiveProject( content.getProject() );
            return true;
        } else {
            return false;
        }
    }

    boolean setupActiveRepository( final ProjectExplorerContent content ) {
        if ( Utils.hasRepositoryChanged( content.getRepository(),
                                         activeRepository ) ) {
            setActiveRepository( content.getRepository() );
            return true;
        } else {
            return false;
        }
    }

    boolean setupActiveOrganizationalUnit( final ProjectExplorerContent content ) {

        if ( Utils.hasOrganizationalUnitChanged( content.getOrganizationalUnit(),
                                                 activeOrganizationalUnit ) ) {
            setActiveOrganizationalUnit( content.getOrganizationalUnit() );
            return true;
        } else {
            return false;
        }
    }

    boolean setupActiveFolderAndPackage( final ProjectExplorerContent content ) {
        if ( Utils.hasFolderItemChanged( content.getFolderListing().getItem(),
                                         activeFolderItem ) ) {
            setActiveFolderItem( content.getFolderListing().getItem() );
            if ( activeFolderItem != null && activeFolderItem.getItem() != null && activeFolderItem.getItem() instanceof Package ) {
                setActivePackage( (Package) activeFolderItem.getItem() );
            } else if ( activeFolderItem == null || activeFolderItem.getItem() == null ) {
                setActivePackage( null );
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
                                                                     activeProject ) );
            return;
        }

        if ( activeFolderItem.getItem() instanceof Package ) {
            setActivePackage( (Package) activeFolderItem.getItem() );
            contextChangedEvent.fire( new ProjectContextChangeEvent( activeOrganizationalUnit,
                                                                     activeRepository,
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
                    setActivePackage( pkg );
                    contextChangedEvent.fire( new ProjectContextChangeEvent( activeOrganizationalUnit,
                                                                             activeRepository,
                                                                             activeProject,
                                                                             activePackage ) );
                } else {
                    contextChangedEvent.fire( new ProjectContextChangeEvent( activeOrganizationalUnit,
                                                                             activeRepository,
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

}
