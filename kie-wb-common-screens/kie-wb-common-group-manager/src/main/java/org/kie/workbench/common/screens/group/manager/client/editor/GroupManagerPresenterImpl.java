/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.screens.group.manager.client.editor;

import java.util.ArrayList;
import java.util.Collection;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.Command;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.workbench.common.screens.group.manager.client.editor.popups.AddGroupPopup;
import org.kie.workbench.common.screens.group.manager.client.resources.i18n.GroupManagerConstants;
import org.kie.workbench.common.widgets.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.group.Group;
import org.uberfire.backend.group.GroupService;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.repositories.RepositoryService;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;

@ApplicationScoped
@WorkbenchScreen(identifier = "org.kie.workbench.common.screens.group.manager.GroupManager")
public class GroupManagerPresenterImpl implements GroupManagerPresenter {

    @Inject
    private GroupManagerView view;

    @Inject
    private Caller<GroupService> groupService;

    @Inject
    private Caller<RepositoryService> repositoryService;

    @Inject
    private AddGroupPopup addGroupPopup;

    @PostConstruct
    public void setup() {
        addGroupPopup.setCallback( new Command() {

            @Override
            public void execute() {
                final String groupName = addGroupPopup.getGroupName();
                final String groupOwner = addGroupPopup.getGroupOwner();
                final Collection<Repository> repositories = new ArrayList<Repository>();
                view.showBusyIndicator( CommonConstants.INSTANCE.Wait() );
                groupService.call( new RemoteCallback<Group>() {

                    @Override
                    public void callback( final Group newGroup ) {
                        view.addGroup( newGroup );
                        view.hideBusyIndicator();
                    }
                }, new HasBusyIndicatorDefaultErrorCallback( view ) ).createGroup( groupName,
                                                                                   groupOwner,
                                                                                   repositories );
            }
        } );
    }

    @OnStartup
    public void onStartup() {
        view.showBusyIndicator( CommonConstants.INSTANCE.Wait() );
        repositoryService.call( new RemoteCallback<Collection<Repository>>() {
            @Override
            public void callback( final Collection<Repository> repositories ) {
                view.setAllRepositories( repositories );
                view.hideBusyIndicator();
            }
        }, new HasBusyIndicatorDefaultErrorCallback( view ) ).getRepositories();
    }

    @OnOpen
    public void onOpen() {
        view.reset();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return GroupManagerConstants.INSTANCE.GroupManagerTitle();
    }

    @WorkbenchPartView
    public UberView<GroupManagerPresenter> getView() {
        return view;
    }

    @Override
    public void loadGroups() {
        view.showBusyIndicator( CommonConstants.INSTANCE.Wait() );
        groupService.call( new RemoteCallback<Collection<Group>>() {
            @Override
            public void callback( final Collection<Group> groups ) {
                view.setGroups( groups );
                view.hideBusyIndicator();
            }
        }, new HasBusyIndicatorDefaultErrorCallback( view ) ).getGroups();
    }

    @Override
    public void groupSelected( final Group group ) {
        final Collection<Repository> repositories = group.getRepositories();
        view.setGroupRepositories( repositories );
    }

    @Override
    public void addNewGroup() {
        addGroupPopup.show();
    }

    @Override
    public void deleteGroup( final Group group ) {
        view.showBusyIndicator( CommonConstants.INSTANCE.Wait() );
        groupService.call( new RemoteCallback<Void>() {
            @Override
            public void callback( final Void v ) {
                view.deleteGroup( group );
                view.hideBusyIndicator();
            }
        }, new HasBusyIndicatorDefaultErrorCallback( view ) ).removeGroup( group.getName() );
    }

    @Override
    public void addGroupRepository( final Group group,
                                    final Repository repository ) {
        view.showBusyIndicator( CommonConstants.INSTANCE.Wait() );
        group.getRepositories().add( repository );
        groupService.call( new RemoteCallback<Void>() {
            @Override
            public void callback( final Void v ) {
                view.setGroupRepositories( group.getRepositories() );
                view.hideBusyIndicator();
            }
        }, new HasBusyIndicatorDefaultErrorCallback( view ) ).addRepository( group,
                                                                             repository );
    }

    @Override
    public void removeGroupRepository( final Group group,
                                       final Repository repository ) {
        view.showBusyIndicator( CommonConstants.INSTANCE.Wait() );
        group.getRepositories().remove( repository );
        groupService.call( new RemoteCallback<Void>() {
            @Override
            public void callback( final Void v ) {
                view.setGroupRepositories( group.getRepositories() );
                view.hideBusyIndicator();
            }
        }, new HasBusyIndicatorDefaultErrorCallback( view ) ).removeRepository( group,
                                                                                repository );
    }
}
