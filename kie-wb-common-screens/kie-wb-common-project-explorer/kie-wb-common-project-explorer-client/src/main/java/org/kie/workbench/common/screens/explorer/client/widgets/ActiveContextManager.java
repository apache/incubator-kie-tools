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
import org.guvnor.structure.repositories.RepositoryEnvironmentUpdatedEvent;
import org.guvnor.structure.repositories.RepositoryRemovedEvent;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.explorer.client.utils.Utils;
import org.kie.workbench.common.screens.explorer.model.ProjectExplorerContent;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;
import org.kie.workbench.common.screens.explorer.service.ProjectExplorerContentQuery;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.impl.authz.RuntimeAuthorizationManager;
import org.uberfire.workbench.events.ResourceBatchChangesEvent;

public class ActiveContextManager {

    private ActiveContextItems activeContextItems;
    private ActiveContextOptions activeOptions;
    private Caller<ExplorerService> explorerService;
    private RuntimeAuthorizationManager authorizationManager;
    private transient SessionInfo sessionInfo;

    private View view;

    private RemoteCallback<ProjectExplorerContent> contentCallback;

    public ActiveContextManager() {
    }

    @Inject
    public ActiveContextManager( final ActiveContextItems activeContextItems,
                                 final ActiveContextOptions activeOptions,
                                 final Caller<ExplorerService> explorerService,
                                 final RuntimeAuthorizationManager authorizationManager,
                                 final SessionInfo sessionInfo ) {
        this.activeContextItems = activeContextItems;
        this.activeOptions = activeOptions;
        this.explorerService = explorerService;
        this.authorizationManager = authorizationManager;
        this.sessionInfo = sessionInfo;
    }

    public void init( final View view,
                      final RemoteCallback<ProjectExplorerContent> contentCallback ) {
        this.view = view;
        this.contentCallback = contentCallback;
    }

    public void initActiveContext( final String path ) {
        view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );

        explorerService.call( contentCallback,
                              new HasBusyIndicatorDefaultErrorCallback( view ) ).getContent( path,
                                                                                             activeOptions.getOptions() );
    }

    public void initActiveContext( final OrganizationalUnit organizationalUnit ) {

        view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
        refresh( new ProjectExplorerContentQuery( organizationalUnit ) );
    }

    public void initActiveContext( final OrganizationalUnit organizationalUnit,
                                   final Repository repository,
                                   final String branch ) {

        view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
        refresh( new ProjectExplorerContentQuery( organizationalUnit,
                                                  repository,
                                                  branch ) );
    }

    public void initActiveContext( final OrganizationalUnit organizationalUnit,
                                   final Repository repository,
                                   final String branch,
                                   final Project project ) {

        view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
        refresh( new ProjectExplorerContentQuery( organizationalUnit,
                                                  repository,
                                                  branch,
                                                  project ) );
    }

    public void initActiveContext( final OrganizationalUnit organizationalUnit,
                                   final Repository repository,
                                   final String branch,
                                   final Project project,
                                   final org.guvnor.common.services.project.model.Package pkg ) {
        view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
        refresh( new ProjectExplorerContentQuery( organizationalUnit,
                                                  repository,
                                                  branch,
                                                  project,
                                                  pkg ) );
    }

    private void refresh( final ProjectExplorerContentQuery query ) {

        query.setOptions( activeOptions.getOptions() );

        explorerService.call( contentCallback,
                              new HasBusyIndicatorDefaultErrorCallback( view ) ).getContent( query );
    }

    private void refresh( final Project project ) {
        refresh( new ProjectExplorerContentQuery( activeContextItems.getActiveOrganizationalUnit(),
                                                  activeContextItems.getActiveRepository(),
                                                  activeContextItems.getActiveBranch(),
                                                  project ) );
    }

    void refresh() {
        refresh( new ProjectExplorerContentQuery( activeContextItems.getActiveOrganizationalUnit(),
                                                  activeContextItems.getActiveRepository(),
                                                  activeContextItems.getActiveBranch(),
                                                  activeContextItems.getActiveProject(),
                                                  activeContextItems.getActivePackage(),
                                                  activeContextItems.getActiveFolderItem() ) );
    }

    private boolean isInActiveBranch( final Project project ) {
        return Utils.isInBranch( getCurrentBranchRoot(),
                                 project );
    }

    private Path getCurrentBranchRoot() {
        if ( activeContextItems.getActiveRepository() == null ) {
            return null;
        } else {
            return activeContextItems.getActiveRepository().getBranchRoot( activeContextItems.getActiveBranch() );
        }
    }

    public void initActiveContext( final ProjectContext context ) {
        initActiveContext( context.getActiveOrganizationalUnit(),
                           context.getActiveRepository(),
                           context.getActiveBranch(),
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
        refresh();
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
            refresh();
        }
    }

    public void onSystemRepositoryChanged( @Observes final SystemRepositoryChangedEvent event ) {
        if ( view.isVisible() ) {
            refresh();
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
                                             sessionInfo.getIdentity() ) ) {
            refresh();
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

        refresh();
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
                                             sessionInfo.getIdentity() ) ) {
            refresh();
        }
    }

    public void onRepoRemovedFromOrganizationalUnitEvent( @Observes final RepoRemovedFromOrganizationalUnitEvent event ) {
        if ( view.isVisible() ) {
            refresh();
        }
    }

    public void onRepositoryRemovedEvent( @Observes final RepositoryRemovedEvent event ) {
        if ( !view.isVisible() ) {
            return;
        }

        // The following comparison must stay in that order to avoid a NullPointerException
        if ( event.getRepository().equals( activeContextItems.getActiveRepository() ) ) {
            activeContextItems.flush();
        }

        refresh();
    }

    public void onRepositoryUpdatedEvent( @Observes final RepositoryEnvironmentUpdatedEvent event ) {
        if ( activeContextItems.isTheActiveRepository( event.getUpdatedRepository().getAlias() ) ) {
            refresh();
        } else {
            activeContextItems.updateRepository( event.getUpdatedRepository().getAlias(),
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

        refresh( new ProjectExplorerContentQuery(
                        activeContextItems.getActiveOrganizationalUnit(),
                        activeContextItems.getActiveRepository(),
                        activeContextItems.getActiveBranch(),
                        activeContextItems.getActiveProject(),
                        pkg ) );
    }

    public void onProjectAdded( @Observes final NewProjectEvent event ) {
        if ( view.isVisible() && event.getProject() != null ) {

            if ( sessionInfo.getId().equals( event.getSessionId() )
                    && isInActiveBranch( event.getProject() ) ) {

                refresh( event.getProject() );

            } else {

                refresh();

            }
        }
    }

    public void onProjectRename( @Observes final RenameProjectEvent event ) {
        if ( isInActiveBranch( event.getOldProject() ) ) {
            if ( authorizationManager.authorize( event.getOldProject(),
                                                 sessionInfo.getIdentity() ) ) {
                refresh( event.getNewProject() );
            }
        }
    }

    public void onProjectDelete( @Observes final DeleteProjectEvent event ) {
        if ( isInActiveBranch( event.getProject() ) && authorizationManager.authorize( event.getProject(),
                                                                                       sessionInfo.getIdentity() ) ) {
            if ( activeContextItems.getActiveProject() != null && activeContextItems.getActiveProject().equals( event.getProject() ) ) {
                activeContextItems.flushActiveProject();
            }

            view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
            refresh( new ProjectExplorerContentQuery(
                            activeContextItems.getActiveOrganizationalUnit(),
                            activeContextItems.getActiveRepository(),
                            activeContextItems.getActiveBranch() ) );
        }
    }
}
