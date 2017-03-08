/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.BusLifecycleAdapter;
import org.jboss.errai.bus.client.api.BusLifecycleEvent;
import org.jboss.errai.bus.client.api.ClientMessageBus;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.slf4j.Logger;
import org.uberfire.client.workbench.WorkbenchServicesProxy;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.mvp.ParameterizedCommand;

@EntryPoint
public class WorkbenchBackendEntryPoint {

    private Logger logger;

    private ClientMessageBus bus;

    private WorkbenchServicesProxy workbenchServices;

    private ErrorPopupPresenter errorPopupPresenter;

    private boolean isWorkbenchOnCluster = false;
    private boolean showedError = false;

    public WorkbenchBackendEntryPoint() {

    }

    @Inject
    public WorkbenchBackendEntryPoint(final Logger logger,
                                      final ClientMessageBus bus,
                                      final WorkbenchServicesProxy workbenchServices,
                                      final ErrorPopupPresenter errorPopupPresenter) {
        this.logger = logger;
        this.bus = bus;
        this.workbenchServices = workbenchServices;
        this.errorPopupPresenter = errorPopupPresenter;
    }

    @AfterInitialization
    public void init() {
        workbenchServices.isWorkbenchOnCluster(new ParameterizedCommand<Boolean>() {
            @Override
            public void execute(final Boolean parameter) {
                isWorkbenchOnCluster = !(parameter == null || parameter.equals(Boolean.FALSE));
            }
        });
    }

    @PostConstruct
    public void postConstruct() {
        bus.addLifecycleListener(new BusLifecycleAdapter() {
            @Override
            public void busOnline(final BusLifecycleEvent e) {
                logger.info("Bus is back online.");
                showedError = false;
            }

            @Override
            public void busOffline(final BusLifecycleEvent e) {
                if (showedError) {
                    return;
                }
                logger.error("Bus is offline. [" + e.getReason().getErrorMessage() + "]");
                if (!isWorkbenchOnCluster) {
                    errorPopupPresenter.showMessage("You've been disconnected.");
                }
                showedError = true;
            }
        });
    }
}