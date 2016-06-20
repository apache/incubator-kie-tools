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

package org.kie.workbench.common.screens.server.management.client.navigation.template;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.server.controller.api.model.events.ServerInstanceDeleted;
import org.kie.server.controller.api.model.events.ServerInstanceUpdated;
import org.kie.server.controller.api.model.runtime.ServerInstance;
import org.kie.server.controller.api.model.runtime.ServerInstanceKey;
import org.kie.server.controller.api.model.spec.Capability;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.workbench.common.screens.server.management.client.events.AddNewContainer;
import org.kie.workbench.common.screens.server.management.client.events.ContainerSpecSelected;
import org.kie.workbench.common.screens.server.management.client.events.ServerInstanceSelected;
import org.kie.workbench.common.screens.server.management.client.events.ServerTemplateListRefresh;
import org.kie.workbench.common.screens.server.management.client.navigation.template.copy.CopyPopupPresenter;
import org.kie.workbench.common.screens.server.management.service.SpecManagementService;
import org.slf4j.Logger;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.workbench.events.NotificationEvent;

@ApplicationScoped
public class ServerTemplatePresenter {

    public interface View extends UberView<ServerTemplatePresenter> {

        void clear();

        void setTemplate( final String id,
                          final String name );

        void selectContainer( final String serverTemplateId,
                              final String id );

        void selectServerInstance( final String serverTemplateId,
                                   final String id );

        void addContainer( final String serverTemplateId,
                           final String containerSpecId,
                           final String containerName,
                           final Command onSelect );

        void addServerInstance( final String serverTemplateId,
                                final String serverInstanceId,
                                final String serverName,
                                final Command onSelect );

        void setRulesCapability( boolean value );

        void setProcessCapability( boolean value );

        void setPlanningCapability( final boolean value );

        void confirmRemove( Command command );

        String getCopyTemplateErrorMessage();

        String getRemoveTemplateErrorMessage();
    }

    private final Logger logger;
    private final View view;
    private final CopyPopupPresenter copyPresenter;
    private final Caller<SpecManagementService> specManagementService;
    private final Event<NotificationEvent> notification;

    private final Event<AddNewContainer> addNewContainerEvent;
    private final Event<ContainerSpecSelected> containerSpecSelectedEvent;
    private final Event<ServerInstanceSelected> serverInstanceSelectedEvent;
    private final Event<ServerTemplateListRefresh> serverTemplateListRefreshEvent;

    private ServerTemplate serverTemplate;

    private Set<String> serverInstances = new HashSet<String>();

    @Inject
    public ServerTemplatePresenter( final Logger logger,
                                    final View view,
                                    final CopyPopupPresenter copyPresenter,
                                    final Caller<SpecManagementService> specManagementService,
                                    final Event<NotificationEvent> notification,
                                    final Event<AddNewContainer> addNewContainerEvent,
                                    final Event<ContainerSpecSelected> containerSpecSelectedEvent,
                                    final Event<ServerInstanceSelected> serverInstanceSelectedEvent,
                                    final Event<ServerTemplateListRefresh> serverTemplateListRefreshEvent ) {
        this.logger = logger;
        this.view = view;
        this.copyPresenter = copyPresenter;
        this.specManagementService = specManagementService;
        this.notification = notification;
        this.addNewContainerEvent = addNewContainerEvent;
        this.containerSpecSelectedEvent = containerSpecSelectedEvent;
        this.serverInstanceSelectedEvent = serverInstanceSelectedEvent;
        this.serverTemplateListRefreshEvent = serverTemplateListRefreshEvent;
    }

    @PostConstruct
    public void init() {
        view.init( this );
    }

    public View getView() {
        return view;
    }

    public ServerTemplate getCurrentServerTemplate() {
        return serverTemplate;
    }

    public void setup( final ServerTemplate serverTemplate,
                       final ContainerSpec firstContainerSpec ) {
        view.clear();
        this.serverTemplate = serverTemplate;
        view.setTemplate( serverTemplate.getId(), serverTemplate.getName() );

        view.setProcessCapability( serverTemplate.getCapabilities().contains( Capability.PROCESS.toString() ) );

        view.setRulesCapability( serverTemplate.getCapabilities().contains( Capability.RULE.toString() ) );

        view.setPlanningCapability( serverTemplate.getCapabilities().contains( Capability.PLANNING.toString() ) );

        if ( firstContainerSpec != null ) {
            addContainer( firstContainerSpec );
            for ( final ContainerSpec containerSpec : serverTemplate.getContainersSpec() ) {
                if ( !containerSpec.getId().equals( firstContainerSpec.getId() ) ) {
                    addContainer( containerSpec );
                }
            }
            containerSpecSelectedEvent.fire( new ContainerSpecSelected( firstContainerSpec ) );
        }

        for ( final ServerInstanceKey serverInstanceKey : serverTemplate.getServerInstanceKeys() ) {
            addServerInstance( serverInstanceKey );
        }
    }

    private void addContainer( final ContainerSpec containerSpec ) {
        view.addContainer( containerSpec.getServerTemplateKey().getId(),
                           containerSpec.getId(),
                           containerSpec.getContainerName(),
                           new Command() {
                               @Override
                               public void execute() {
                                   containerSpecSelectedEvent.fire( new ContainerSpecSelected( containerSpec ) );
                               }
                           } );
    }

    private void addServerInstance( final ServerInstanceKey serverInstanceKey ) {
        serverInstances.add( serverInstanceKey.getServerInstanceId() );
        view.addServerInstance( serverInstanceKey.getServerTemplateId(),
                                serverInstanceKey.getServerInstanceId(),
                                serverInstanceKey.getServerName(),
                                new Command() {
                                    @Override
                                    public void execute() {
                                        serverInstanceSelectedEvent.fire( new ServerInstanceSelected( serverInstanceKey ) );
                                    }
                                } );
    }

    public void onContainerSelect( @Observes final ContainerSpecSelected containerSpecSelected ) {
        if ( containerSpecSelected != null &&
                containerSpecSelected.getContainerSpecKey() != null &&
                containerSpecSelected.getContainerSpecKey().getServerTemplateKey() != null &&
                containerSpecSelected.getContainerSpecKey().getServerTemplateKey().getId() != null &&
                containerSpecSelected.getContainerSpecKey().getId() != null ) {
            view.selectContainer( containerSpecSelected.getContainerSpecKey().getServerTemplateKey().getId(),
                                  containerSpecSelected.getContainerSpecKey().getId() );
        } else {
            logger.warn( "Illegal event argument." );
        }
    }

    public void onServerInstanceSelect( @Observes final ServerInstanceSelected serverInstanceSelected ) {
        if ( serverInstanceSelected != null &&
                serverInstanceSelected.getServerInstanceKey() != null &&
                serverInstanceSelected.getServerInstanceKey().getServerTemplateId() != null &&
                serverInstanceSelected.getServerInstanceKey().getServerInstanceId() != null ) {
            view.selectServerInstance( serverInstanceSelected.getServerInstanceKey().getServerTemplateId(),
                                       serverInstanceSelected.getServerInstanceKey().getServerInstanceId() );
        } else {
            logger.warn( "Illegal event argument." );
        }
    }

    public void onServerInstanceUpdated( @Observes final ServerInstanceUpdated serverInstanceUpdated ) {
        if ( serverInstanceUpdated != null &&
                serverInstanceUpdated.getServerInstance() != null ) {
            final ServerInstance updatedServerInstance = serverInstanceUpdated.getServerInstance();
            if ( serverTemplate != null &&
                    updatedServerInstance.getServerTemplateId().equals( serverTemplate.getId() ) &&
                    !serverInstances.contains( updatedServerInstance.getServerInstanceId() ) ) {
                addServerInstance( updatedServerInstance );
            }
        } else {
            logger.warn( "Illegal event argument." );
        }
    }

    public void onServerInstanceDeleted( @Observes final ServerInstanceDeleted serverInstanceDeleted ) {
        if ( serverInstanceDeleted != null &&
                serverInstanceDeleted.getServerInstanceId() != null ) {
            serverInstances.remove( serverInstanceDeleted.getServerInstanceId() );
        } else {
            logger.warn( "Illegal event argument." );
        }
    }

    public void addNewContainer() {
        addNewContainerEvent.fire( new AddNewContainer( serverTemplate ) );
    }

    public void copyTemplate() {
        copyPresenter.copy( new ParameterizedCommand<String>() {
            @Override
            public void execute( final String value ) {
                specManagementService.call( new RemoteCallback<Void>() {
                    @Override
                    public void callback( final Void aVoid ) {
                        copyPresenter.hide();
                        serverTemplateListRefreshEvent.fire( new ServerTemplateListRefresh( value ) );
                    }
                }, new ErrorCallback<Object>() {
                    @Override
                    public boolean error( final Object o,
                                          final Throwable throwable ) {
                        copyPresenter.errorDuringProcessing( view.getCopyTemplateErrorMessage() );
                        return false;
                    }
                } ).copyServerTemplate( serverTemplate.getId(), value, value );
            }
        } );
    }

    public void removeTemplate() {
        view.confirmRemove( new Command() {
            @Override
            public void execute() {
                specManagementService.call( new RemoteCallback<Void>() {
                    @Override
                    public void callback( final Void aVoid ) {
                        serverTemplateListRefreshEvent.fire( new ServerTemplateListRefresh() );
                    }
                }, new ErrorCallback<Object>() {
                    @Override
                    public boolean error( final Object o,
                                          final Throwable throwable ) {
                        notification.fire( new NotificationEvent( view.getRemoveTemplateErrorMessage(), NotificationEvent.NotificationType.ERROR ) );
                        serverTemplateListRefreshEvent.fire( new ServerTemplateListRefresh() );
                        return false;
                    }
                } ).deleteServerTemplate( serverTemplate.getId() );
            }
        } );
    }

}
