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

package org.kie.workbench.common.screens.server.management.client.container.config.process;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.server.controller.api.model.spec.Capability;
import org.kie.server.controller.api.model.spec.ContainerSpecKey;
import org.kie.server.controller.api.model.spec.ProcessConfig;
import org.kie.workbench.common.screens.server.management.client.widget.config.process.ProcessConfigPresenter;
import org.kie.workbench.common.screens.server.management.service.SpecManagementService;
import org.uberfire.client.mvp.UberView;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
public class ContainerProcessConfigPresenter {

    public interface View extends UberView<ContainerProcessConfigPresenter> {

        void setProcessConfigView( ProcessConfigPresenter.View view );

        void disable();

        void disableActions();

        void enableActions();

        String getSaveSuccessMessage();

        String getSaveErrorMessage();
    }

    private final View view;
    private final ProcessConfigPresenter processConfigPresenter;
    private final Caller<SpecManagementService> specManagementService;
    private final Event<NotificationEvent> notification;

    @Inject
    public ContainerProcessConfigPresenter( final View view,
                                            final ProcessConfigPresenter processConfigPresenter,
                                            final Caller<SpecManagementService> specManagementService,
                                            final Event<NotificationEvent> notification ) {
        this.view = view;
        this.processConfigPresenter = processConfigPresenter;
        this.specManagementService = specManagementService;
        this.notification = notification;
    }

    @PostConstruct
    public void init() {
        view.init( this );
        view.setProcessConfigView( processConfigPresenter.getView() );
    }

    public View getView() {
        return view;
    }

    public void setup( final ContainerSpecKey containerSpecKey,
                       final ProcessConfig processConfig ) {
        this.processConfigPresenter.setup( containerSpecKey, processConfig );
        setupView( processConfig );
    }

    private void setupView( final ProcessConfig processConfig ) {
        view.enableActions();
        processConfigPresenter.setProcessConfig( processConfig );
    }

    public void disable() {
        view.disable();
    }

    public void save() {
        view.disableActions();
        final ProcessConfig newProcessConfig = processConfigPresenter.buildProcessConfig();
        specManagementService.call( new RemoteCallback<Void>() {
                                        @Override
                                        public void callback( final Void containerConfig ) {
                                            notification.fire( new NotificationEvent( view.getSaveSuccessMessage(), NotificationEvent.NotificationType.SUCCESS ) );
                                            setupView( newProcessConfig );
                                        }
                                    },
                                    new ErrorCallback<Object>() {
                                        @Override
                                        public boolean error( final Object o,
                                                              final Throwable throwable ) {
                                            notification.fire( new NotificationEvent( view.getSaveErrorMessage(), NotificationEvent.NotificationType.ERROR ) );
                                            setupView( processConfigPresenter.getProcessConfig() );
                                            return false;
                                        }
                                    } )
                .updateContainerConfig( processConfigPresenter.getContainerSpecKey().getServerTemplateKey().getId(),
                                        processConfigPresenter.getContainerSpecKey().getId(),
                                        Capability.PROCESS,
                                        newProcessConfig );
    }

    public void cancel() {
        setupView( processConfigPresenter.getProcessConfig() );
    }

}
