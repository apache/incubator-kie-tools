/*
 * Copyright 2013 JBoss Inc
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
package org.kie.workbench.common.screens.explorer.client;

import java.util.Collection;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.workbench.common.screens.explorer.client.resources.i18n.ProjectExplorerConstants;
import org.kie.workbench.common.screens.explorer.client.widgets.BaseViewPresenter;
import org.kie.workbench.common.screens.explorer.client.widgets.business.BusinessViewPresenter;
import org.kie.workbench.common.screens.explorer.client.widgets.technical.TechnicalViewPresenter;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;
import org.kie.workbench.common.services.shared.context.KieWorkbenchContext;
import org.kie.workbench.common.services.shared.context.Project;
import org.kie.workbench.common.services.shared.context.ProjectAddedEvent;
import org.kie.workbench.common.services.shared.context.ProjectChangeEvent;
import org.kie.workbench.common.widgets.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.group.Group;
import org.uberfire.backend.repositories.NewRepositoryEvent;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.security.Identity;
import org.uberfire.security.impl.authz.RuntimeAuthorizationManager;
import org.uberfire.workbench.events.GroupChangeEvent;
import org.uberfire.workbench.events.RepositoryChangeEvent;

/**
 * Repository, Package, Folder and File explorer
 */
@WorkbenchScreen(identifier = "org.kie.guvnor.explorer")
public class ExplorerPresenterImpl implements ExplorerPresenter {

    @Inject
    private Identity identity;

    @Inject
    private RuntimeAuthorizationManager authorizationManager;

    @Inject
    private Caller<ExplorerService> explorerService;

    @Inject
    private Event<GroupChangeEvent> groupChangeEvent;

    @Inject
    private Event<RepositoryChangeEvent> repositoryChangeEvent;

    @Inject
    private Event<ProjectChangeEvent> projectChangeEvent;

    @Inject
    private KieWorkbenchContext context;

    @Inject
    private ExplorerView view;

    @Inject
    private BusinessViewPresenter businessViewPresenter;

    @Inject
    private TechnicalViewPresenter technicalViewPresenter;

    private BaseViewPresenter activeViewPresenter;

    @PostConstruct
    public void init() {
        businessViewPresenter.init( this );
        technicalViewPresenter.init( this );
        selectBusinessView();
    }

    @OnStart
    public void onStart() {
        initialiseViewForActiveContext();
    }

    private Group getActiveGroup() {
        return context.getActiveGroup();
    }

    private Repository getActiveRepository() {
        return context.getActiveRepository();
    }

    private Project getActiveProject() {
        return context.getActiveProject();
    }

    @WorkbenchPartView
    public UberView<ExplorerPresenterImpl> getView() {
        return this.view;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return ProjectExplorerConstants.INSTANCE.explorerTitle();
    }

    private void initialiseViewForActiveContext() {
        //Show busy popup. Groups cascade through Repositories, Projects, Packages and Items where it is closed
        view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
        explorerService.call( new RemoteCallback<Collection<Group>>() {
            @Override
            public void callback( final Collection<Group> groups ) {
                final Group activeGroup = getActiveGroup();
                activeViewPresenter.setGroups( groups,
                                               activeGroup );

            }

        }, new HasBusyIndicatorDefaultErrorCallback( view ) ).getGroups();
    }

    @Override
    public void groupSelected( final Group group ) {
        if ( group == null || !group.equals( getActiveGroup() ) ) {
            groupChangeEvent.fire( new GroupChangeEvent( group ) );
        } else {
            groupChangeHandler( group );
        }
    }

    public void groupChangeHandler( final @Observes GroupChangeEvent event ) {
        final Group group = event.getGroup();
        groupChangeHandler( group );
    }

    private void groupChangeHandler( final Group group ) {
        //Show busy popup. Repositories cascade through Projects, Packages and Items where it is closed
        view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
        explorerService.call( new RemoteCallback<Collection<Repository>>() {
            @Override
            public void callback( final Collection<Repository> repositories ) {
                final Repository activeRepository = getActiveRepository();
                activeViewPresenter.setRepositories( repositories,
                                                     activeRepository );

            }

        }, new HasBusyIndicatorDefaultErrorCallback( view ) ).getRepositories( group );
    }

    @Override
    public void repositorySelected( final Repository repository ) {
        if ( repository == null || !repository.equals( getActiveRepository() ) ) {
            repositoryChangeEvent.fire( new RepositoryChangeEvent( repository ) );
        } else {
            repositoryChangeHandler( repository );
        }
    }

    public void repositoryChangeHandler( final @Observes RepositoryChangeEvent event ) {
        final Repository repository = event.getRepository();
        repositoryChangeHandler( repository );
    }

    private void repositoryChangeHandler( final Repository repository ) {
        //Show busy popup. Projects cascade through Packages and Items where it is closed
        view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
        explorerService.call( new RemoteCallback<Collection<Project>>() {
            @Override
            public void callback( final Collection<Project> projects ) {
                final Project activeProject = getActiveProject();
                activeViewPresenter.setProjects( projects,
                                                 activeProject );

            }

        }, new HasBusyIndicatorDefaultErrorCallback( view ) ).getProjects( repository );
    }

    public void onRepositoryAdded( @Observes final NewRepositoryEvent event ) {
        final Repository repository = event.getNewRepository();
        if ( repository == null ) {
            return;
        }
        if ( authorizationManager.authorize( repository,
                                             identity ) ) {
            activeViewPresenter.addRepository( repository );
        }
    }

    public void onProjectAdded( @Observes final ProjectAddedEvent event ) {
        final Project project = event.getProject();
        if ( project == null ) {
            return;
        }
        if ( getActiveRepository() == null ) {
            return;
        }
        final String projectRoot = project.getRootPath().toURI();
        final String activeRepositoryRoot = getActiveRepository().getRoot().toURI();
        if ( !projectRoot.startsWith( activeRepositoryRoot ) ) {
            return;
        }
        if ( authorizationManager.authorize( project,
                                             identity ) ) {
            activeViewPresenter.addProject( project );
        }
    }

    @Override
    public void selectBusinessView() {
        if ( activeViewPresenter != null ) {
            activeViewPresenter.deactivate();
        }
        activeViewPresenter = businessViewPresenter;
        activeViewPresenter.activate();

        initialiseViewForActiveContext();
    }

    @Override
    public void selectTechnicalView() {
        if ( activeViewPresenter != null ) {
            activeViewPresenter.deactivate();
        }
        activeViewPresenter = technicalViewPresenter;
        activeViewPresenter.activate();

        initialiseViewForActiveContext();
    }

}
