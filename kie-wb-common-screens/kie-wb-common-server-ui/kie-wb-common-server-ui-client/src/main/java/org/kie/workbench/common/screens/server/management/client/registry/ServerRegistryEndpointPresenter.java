/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.server.management.client.registry;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.server.management.service.ServerAlreadyRegisteredException;
import org.kie.workbench.common.screens.server.management.service.ServerManagementService;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.Commands;

@Dependent
public class ServerRegistryEndpointPresenter {

    public interface View extends UberView<ServerRegistryEndpointPresenter> {

        String getBaseURL();

        void lockScreen();

        void unlockScreen();

        void show();

        void hide();
    }

    private final View view;

    private final Caller<ServerManagementService> service;

    private final ErrorPopupPresenter errorPopup;

    @Inject
    public ServerRegistryEndpointPresenter( final View view,
                                            final Caller<ServerManagementService> service,
                                            final ErrorPopupPresenter errorPopup ) {
        this.view = view;
        this.service = service;
        this.errorPopup = errorPopup;
        this.view.init( this );
    }

    public String getTitle() {
        return "Register Server";
    }

    public void registerServer( final String id,
                                final String name,
                                final String version ) {
        view.lockScreen();

        service.call( new RemoteCallback<Void>() {
                          @Override
                          public void callback( final Void response ) {
                              close();
                          }
                      }, new ErrorCallback<Object>() {
                          @Override
                          public boolean error( final Object message,
                                                final Throwable throwable ) {
                              String errorMessage = "Can't connect to endpoint.";
                              if ( throwable instanceof ServerAlreadyRegisteredException ) {
                                  errorMessage = throwable.getMessage();
                              }
                              errorPopup.showMessage( errorMessage, Commands.DO_NOTHING, new Command() {
                                  @Override
                                  public void execute() {
                                      view.unlockScreen();
                                  }
                              } );
                              return false;
                          }
                      }
        ).registerServer( id, name, version );
    }

    public void close() {
        view.unlockScreen();
        view.hide();
    }

    public void show() {
        view.show();
    }
}
