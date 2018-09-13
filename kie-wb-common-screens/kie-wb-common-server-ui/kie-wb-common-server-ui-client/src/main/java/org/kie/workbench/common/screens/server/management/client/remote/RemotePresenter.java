/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.server.management.client.remote;

import java.util.Collection;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.server.controller.api.model.events.ServerInstanceUpdated;
import org.kie.server.controller.api.model.runtime.Container;
import org.kie.server.controller.api.model.runtime.ServerInstanceKey;
import org.kie.workbench.common.screens.server.management.client.events.ServerInstanceSelected;
import org.kie.workbench.common.screens.server.management.client.remote.empty.RemoteEmptyPresenter;
import org.kie.workbench.common.screens.server.management.service.RuntimeManagementService;
import org.kie.workbench.common.screens.server.management.service.SpecManagementService;
import org.slf4j.Logger;
import org.uberfire.client.mvp.UberView;
import org.uberfire.workbench.events.NotificationEvent;

import static org.kie.workbench.common.screens.server.management.client.util.Convert.*;

@Dependent
public class RemotePresenter {

    public interface View extends UberView<RemotePresenter> {

        void clear();

        void setServerName( final String serverName );

        void setServerURL( String url );

        void setEmptyView( final RemoteEmptyPresenter.View view );

        void setStatusPresenter( final RemoteStatusPresenter.View view );

        String getRemoteInstanceRemoveSuccessMessage();

        String getRemoteInstanceRemoveErrorMessage();
    }

    private final Logger logger;
    private final View view;
    private final RemoteStatusPresenter remoteStatusPresenter;
    private final RemoteEmptyPresenter remoteEmptyPresenter;
    private final Caller<RuntimeManagementService> runtimeManagementService;
    private final Caller<SpecManagementService> specManagementServiceCaller;
    private final Event<NotificationEvent> notification;

    private ServerInstanceKey serverInstanceKey;

    @Inject
    public RemotePresenter( final Logger logger,
                            final View view,
                            final RemoteStatusPresenter remoteStatusPresenter,
                            final RemoteEmptyPresenter remoteEmptyPresenter,
                            final Caller<RuntimeManagementService> runtimeManagementService,
                            final Caller<SpecManagementService> specManagementServiceCaller,
                            final Event<NotificationEvent> notification ) {
        this.logger = logger;
        this.view = view;
        this.remoteStatusPresenter = remoteStatusPresenter;
        this.remoteEmptyPresenter = remoteEmptyPresenter;
        this.runtimeManagementService = runtimeManagementService;
        this.specManagementServiceCaller = specManagementServiceCaller;
        this.notification = notification;
    }

    @PostConstruct
    public void init() {
        view.init( this );
    }

    public View getView() {
        return view;
    }

    public void onSelect( @Observes final ServerInstanceSelected serverInstanceSelected ) {
        if ( serverInstanceSelected != null &&
                serverInstanceSelected.getServerInstanceKey() != null ) {
            this.serverInstanceKey = serverInstanceSelected.getServerInstanceKey();
            refresh();
        } else {
            logger.warn( "Illegal event argument." );
        }
    }

    public void onInstanceUpdate( @Observes final ServerInstanceUpdated serverInstanceUpdated ) {
        if ( serverInstanceUpdated != null &&
                serverInstanceUpdated.getServerInstance() != null ) {
            final ServerInstanceKey updatedServerInstanceKey = toKey( serverInstanceUpdated.getServerInstance() );
            if ( serverInstanceKey != null && serverInstanceKey.getServerInstanceId().equals( updatedServerInstanceKey.getServerInstanceId() ) ) {
                serverInstanceKey = updatedServerInstanceKey;
                loadContent( serverInstanceUpdated.getServerInstance().getContainers() );
            }
        } else {
            logger.warn( "Illegal event argument." );
        }
    }

    public void remove() {
        specManagementServiceCaller.call( new RemoteCallback<Void>() {
            @Override
            public void callback( final Void aVoid ) {
                notification.fire( new NotificationEvent( view.getRemoteInstanceRemoveSuccessMessage(), NotificationEvent.NotificationType.SUCCESS ) );
            }
        }, new ErrorCallback<Object>() {
            @Override
            public boolean error( final Object o,
                                  final Throwable throwable ) {
                notification.fire( new NotificationEvent( view.getRemoteInstanceRemoveErrorMessage(), NotificationEvent.NotificationType.ERROR ) );
                return false;
            }
        } ).deleteServerInstance( serverInstanceKey );
    }

    public void refresh() {
        load( serverInstanceKey );
    }

    public void load( final ServerInstanceKey serverInstanceKey ) {
        runtimeManagementService.call( new RemoteCallback<Collection<Container>>() {
            @Override
            public void callback( final Collection<Container> containers ) {
                loadContent( containers );
            }
        } ).getContainersByServerInstance( serverInstanceKey.getServerTemplateId(),
                                           serverInstanceKey.getServerInstanceId() );
    }

    private void loadContent( final Collection<Container> containers ) {

        view.clear();
        view.setServerName( serverInstanceKey.getServerName() );
        view.setServerURL( serverInstanceKey.getUrl() );
        if ( containers.isEmpty() ) {
            view.setEmptyView( remoteEmptyPresenter.getView() );
        } else {
            remoteStatusPresenter.setup( containers );
            view.setStatusPresenter( remoteStatusPresenter.getView() );
        }
    }

}
