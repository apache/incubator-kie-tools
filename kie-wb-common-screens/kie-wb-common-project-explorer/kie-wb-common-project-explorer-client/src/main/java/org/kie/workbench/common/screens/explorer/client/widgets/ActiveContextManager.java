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

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.events.DeleteProjectEvent;
import org.guvnor.common.services.project.events.NewPackageEvent;
import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.events.RenameProjectEvent;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.config.SystemRepositoryChangedEvent;
import org.guvnor.structure.organizationalunit.NewOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.RemoveOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.RepoAddedToOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.RepoRemovedFromOrganizationalUnitEvent;
import org.guvnor.structure.repositories.NewBranchEvent;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryRemovedEvent;
import org.guvnor.structure.repositories.RepositoryUpdatedEvent;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.identity.User;
import org.kie.workbench.common.screens.explorer.client.utils.Utils;
import org.kie.workbench.common.screens.explorer.model.ProjectExplorerContent;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;
import org.kie.workbench.common.screens.explorer.service.ProjectExplorerContentQuery;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.impl.authz.RuntimeAuthorizationManager;
import org.uberfire.workbench.events.ResourceBatchChangesEvent;

public class ActiveContextManager {

    @Inject
    private ActiveContextItems activeContextItems;

    @Inject
    private ActiveContextOptions activeOptions;

    @Inject
    private Caller<ExplorerService> explorerService;

    @Inject
    private RuntimeAuthorizationManager authorizationManager;

    @Inject
    private transient SessionInfo sessionInfo;

    @Inject
    private User identity;

    @Inject
    private BusyIndicatorView busyIndicator;

    private View view;
    private RemoteCallback<ProjectExplorerContent> contentCallback;
    private boolean showLoadingIndicator;

    public void init( final View view,
                      final RemoteCallback<ProjectExplorerContent> contentCallback ) {
        this.view = view;
        this.contentCallback = contentCallback;
    }

    public void initActiveContext( final String path ) {
        busyIndicator.showBusyIndicator( CommonConstants.INSTANCE.Loading() );

        explorerService.call( contentCallback,
                              new HasBusyIndicatorDefaultErrorCallback( busyIndicator ) ).getContent( path,
                                                                                                      activeOptions.getOptions() );
    }

    public void initActiveContext( final OrganizationalUnit organizationalUnit ) {

        this.showLoadingIndicator = true;
        refresh( new ProjectExplorerContentQuery( organizationalUnit ) );
    }

    public void initActiveContext( final OrganizationalUnit organizationalUnit,
                                   final Repository repository ) {

        this.showLoadingIndicator = true;
        refresh( new ProjectExplorerContentQuery( organizationalUnit,
                                                  repository ) );
    }

    public void initActiveContext( final OrganizationalUnit organizationalUnit,
                                   final Repository repository,
                                   final Project project ) {

        this.showLoadingIndicator = true;
        refresh( new ProjectExplorerContentQuery( organizationalUnit,
                                                  repository,
                                                  project ) );
    }

    public void initActiveContext( final Project project ) {
        this.showLoadingIndicator = true;
        refresh( project );
    }

    public void initActiveContext( final OrganizationalUnit organizationalUnit,
                                   final Repository repository,
                                   final Project project,
                                   final org.guvnor.common.services.project.model.Package pkg ) {
        this.showLoadingIndicator = true;
        refresh( new ProjectExplorerContentQuery( organizationalUnit,
                                                  repository,
                                                  project,
                                                  pkg ) );
    }

    private void refresh( final ProjectExplorerContentQuery query ) {
        if ( this.showLoadingIndicator ) {
            busyIndicator.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
        }

        query.setOptions( activeOptions.getOptions() );

        explorerService.call( contentCallback,
                              new HasBusyIndicatorDefaultErrorCallback( busyIndicator ) ).getContent( query );
    }

    private void refresh( final Project project ) {
        refresh( new ProjectExplorerContentQuery( activeContextItems.getActiveOrganizationalUnit(),
                                                  activeContextItems.getActiveRepository(),
                                                  project ) );
    }

    void refresh( final boolean showLoadingIndicator ) {
        this.showLoadingIndicator = showLoadingIndicator;
        refresh( new ProjectExplorerContentQuery( activeContextItems.getActiveOrganizationalUnit(),
                                                  activeContextItems.getActiveRepository(),
                                                  activeContextItems.getActiveProject(),
                                                  activeContextItems.getActivePackage(),
                                                  activeContextItems.getActiveFolderItem() ) );
    }

    private boolean isNotInActiveRepository( final Project project ) {
        return !isInActiveRepository( project );
    }

    private boolean isInActiveRepository( final Project project ) {
        return Utils.isInRepository( activeContextItems.getActiveRepository(),
                                     project );
    }

    public void initActiveContext( final ProjectContext context ) {
        initActiveContext( context.getActiveOrganizationalUnit(),
                           context.getActiveRepository(),
                           context.getActiveProject(),
                           context.getActivePackage() );

    }

    public void onBranchCreated( @Observes final NewBranchEvent event ) {
        if ( activeContextItems.isTheActiveRepository( event.getRepositoryAlias() ) ) {
            if ( activeContextItems.getActiveRepository() instanceof GitRepository ) {
                addBranch( activeContextItems.getActiveRepository(),
                           event.getBranchName(),
                           event.getBranchPath() );
            }
        }

        if ( activeContextItems.getRepositories() != null ) {
            for ( Repository repository : activeContextItems.getRepositories() ) {
                if ( repository.getAlias().equals( event.getRepositoryAlias() ) ) {
                    addBranch( repository,
                               event.getBranchName(),
                               event.getBranchPath() );
                }
            }
        }
    }

    private void addBranch( final Repository repository,
                            final String branchName,
                            final Path branchPath ) {
        ( (GitRepository) repository ).addBranch( branchName, branchPath );
        refresh( false );
    }

    // Refresh when a batch Resource change has occurred. Simply refresh everything.
    public void onBatchResourceChanges( @Observes final ResourceBatchChangesEvent resourceBatchChangesEvent ) {
        if ( !view.isVisible() ) {
            return;
        }

        boolean projectChange = false;
        for ( final Path path : resourceBatchChangesEvent.getBatch().keySet() ) {
            if ( path.getFileName().equals( "pom.xml" ) ) {
                projectChange = true;
                break;
            }
        }

        if ( !projectChange ) {
            refresh( false );
        }
    }

    public void onSystemRepositoryChanged( @Observes final SystemRepositoryChangedEvent event ) {
        if ( view.isVisible() ) {
            refresh( false );
        }
    }

    public void onOrganizationalUnitAdded( @Observes final NewOrganizationalUnitEvent event ) {
        if ( !view.isVisible() ) {
            return;
        }
        final OrganizationalUnit organizationalUnit = event.getOrganizationalUnit();
        if ( organizationalUnit == null ) {
            return;
        }
        if ( authorizationManager.authorize( organizationalUnit,
                                             identity ) ) {
            refresh( false );
        }
    }

    public void onOrganizationalUnitRemoved( @Observes final RemoveOrganizationalUnitEvent event ) {
        if ( !view.isVisible() ) {
            return;
        }
        final OrganizationalUnit organizationalUnit = event.getOrganizationalUnit();
        if ( organizationalUnit == null ) {
            return;
        }

        refresh( false );
    }

    public void onRepoAddedToOrganizationalUnitEvent( @Observes final RepoAddedToOrganizationalUnitEvent event ) {
        if ( !view.isVisible() ) {
            return;
        }
        final Repository repository = event.getRepository();
        if ( repository == null ) {
            return;
        }
        if ( authorizationManager.authorize( repository,
                                             identity ) ) {
            refresh( false );
        }
    }

    public void onRepoRemovedFromOrganizationalUnitEvent( @Observes final RepoRemovedFromOrganizationalUnitEvent event ) {
        if ( view.isVisible() ) {
            refresh( false );
        }
    }

    public void onRepositoryRemovedEvent( @Observes final RepositoryRemovedEvent event ) {
        if ( !view.isVisible() ) {
            return;
        }
        if ( activeContextItems.getActiveRepository().equals( event.getRepository() ) ) {
            activeContextItems.flush();
        }
        refresh( false );
    }

    public void onRepositoryUpdatedEvent( @Observes final RepositoryUpdatedEvent event ) {
        if ( activeContextItems.isTheActiveRepository( event.getRepository().getAlias() ) ) {
            refresh( false );
        } else {
            activeContextItems.updateRepository( event.getRepository().getAlias(),
                                                 event.getUpdatedRepository().getEnvironment() );
        }
    }

    public void onPackageAdded( @Observes final NewPackageEvent event ) {
        if ( !view.isVisible() ) {
            return;
        }
        final org.guvnor.common.services.project.model.Package pkg = event.getPackage();
        if ( pkg == null ) {
            return;
        }
        if ( !Utils.isInProject( activeContextItems.getActiveProject(),
                                 pkg ) ) {
            return;
        }

        this.showLoadingIndicator = false;
        refresh(
                new ProjectExplorerContentQuery(
                        activeContextItems.getActiveOrganizationalUnit(),
                        activeContextItems.getActiveRepository(),
                        activeContextItems.getActiveProject(),
                        pkg ) );
    }

    public void onProjectAdded( @Observes final NewProjectEvent event ) {
        if ( view.isVisible() && event.getProject() != null ) {

            if ( sessionInfo.getId().equals( event.getSessionId() )
                    && isInActiveRepository( event.getProject() ) ) {

                this.showLoadingIndicator = false;
                refresh( event.getProject() );

            } else {

                refresh( false );

            }
        }
    }

    public void onProjectRename( @Observes final RenameProjectEvent event ) {
        if ( !isNotInActiveRepository( event.getOldProject() ) ) {
            if ( authorizationManager.authorize( event.getOldProject(),
                                                 identity ) ) {
                this.showLoadingIndicator = true;
                refresh( event.getNewProject() );
            }
        }
    }

    public void onProjectDelete( @Observes final DeleteProjectEvent event ) {
        if ( isInActiveRepository( event.getProject() ) && authorizationManager.authorize( event.getProject(),
                                                                                           identity ) ) {
            if ( activeContextItems.getActiveProject() != null && activeContextItems.getActiveProject().equals( event.getProject() ) ) {
                activeContextItems.setActiveProject( null );
            }

            this.showLoadingIndicator = true;
            refresh(
                    new ProjectExplorerContentQuery(
                            activeContextItems.getActiveOrganizationalUnit(),
                            activeContextItems.getActiveRepository() ) );
        }
    }
}
