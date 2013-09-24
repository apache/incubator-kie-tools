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

package org.uberfire.client.editors.repository.list;

import java.util.Collection;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.backend.repositories.NewRepositoryEvent;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.repositories.RepositoryRemovedEvent;
import org.uberfire.backend.repositories.RepositoryService;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.lifecycle.OnStartup;

@Dependent
@WorkbenchScreen(identifier = "RepositoriesEditor")
public class RepositoriesPresenter {

    @Inject
    private Caller<VFSService> vfsService;

    @Inject
    private Caller<RepositoryService> repositoryService;

    @Inject
    private Event<RepositoryRemovedEvent> repositoryRemovedEvent;

    @Inject
    private SyncBeanManager iocManager;

    public interface View
            extends
            UberView<RepositoriesPresenter> {

        void addRepository( Repository repository );

        boolean confirmDeleteRepository( Repository repository );

        void removeIfExists( Repository repository );

        void clear();
    }

    @Inject
    public View view;

    public RepositoriesPresenter() {
    }

    @OnStartup
    public void onStartup() {
        view.init( this );
        view.clear();

        repositoryService.call( new RemoteCallback<Collection<Repository>>() {
            @Override
            public void callback( Collection<Repository> response ) {
                for ( final Repository repo : response ) {
                    vfsService.call( new RemoteCallback<Map>() {
                        @Override
                        public void callback( Map response ) {
                            view.addRepository( repo );
                        }
                    } ).readAttributes( repo.getRoot() );
                }
            }
        } ).getRepositories();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "RepositoriesEditor";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return view;
    }

    public void removeRepository( final Repository repository ) {
        if ( view.confirmDeleteRepository( repository ) ) {
            repositoryService.call( new RemoteCallback<Void>() {

                @Override
                public void callback( Void aVoid ) {
                    repositoryRemovedEvent.fire( new RepositoryRemovedEvent( repository ) );
                }
            } ).removeRepository( repository.getAlias() );
        }
    }

    public void newRepository( @Observes final NewRepositoryEvent event ) {
        vfsService.call( new RemoteCallback<Map>() {
            @Override
            public void callback( Map response ) {
                view.addRepository( event.getNewRepository() );
            }
        } ).readAttributes( event.getNewRepository().getRoot() );
    }

    public void removeRootDirectory( @Observes RepositoryRemovedEvent event ) {
        view.removeIfExists( event.getRepository() );
    }

}