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

package org.uberfire.client;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.user.client.Command;
import org.jboss.errai.bus.client.api.ClientMessageBus;
import org.jboss.errai.bus.client.api.TransportError;
import org.jboss.errai.bus.client.api.TransportErrorHandler;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.uberfire.client.workbench.widgets.common.ErrorPopup;

@EntryPoint
public class WorkbenchBackendEntryPoint {

    @Inject
    private ClientMessageBus bus;

    private boolean askRefresh = false;

    @PostConstruct
    public void init() {
        startApp();
    }

    private void startApp() {
        bus.addTransportErrorHandler( new TransportErrorHandler() {
            @Override
            public void onError( TransportError error ) {
                if ( askRefresh ) {
                    return;
                }
                if ( error != null && error.getStatusCode() > 400 && error.getStatusCode() < 500 ) {
                    askRefresh = true;
                    ErrorPopup.showMessage( "You've been disconnected, click OK to refresh application.", null, new Command() {
                        @Override
                        public void execute() {
                            forceReload();
                        }
                    } );
                }
            }
        } );
    }

    private static native void forceReload() /*-{
        $wnd.location.reload(true);
    }-*/;

}
