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

import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import org.jboss.errai.bus.client.api.ClientMessageBus;
import org.jboss.errai.bus.client.framework.ClientMessageBusImpl;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.resources.WorkbenchResources;
import org.uberfire.client.workbench.Workbench;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.rpc.impl.SessionInfoImpl;
import org.uberfire.security.Identity;
import org.uberfire.workbench.events.OrganizationalUnitChangeEvent;

@EntryPoint
public class WorkbenchEntryPoint {

    @Inject
    private Workbench workbench;

    @Inject
    private Identity identity;

    @Inject
    private ClientMessageBus bus;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Caller<VFSService> vfsService;

    @Inject
    private Event<OrganizationalUnitChangeEvent> groupChangedEvent;

    private SessionInfo sessionInfo = null;

    private final SimplePanel appWidget = new SimplePanel();

    @PostConstruct
    public void init() {
        appWidget.add( workbench );
    }

    @AfterInitialization
    private void startApp() {
        loadStyles();
        RootLayoutPanel.get().add( appWidget );

        if ( Window.Location.getParameterMap().containsKey( "standalone" ) ) {
            handleIntegration( Window.Location.getParameterMap() );
        }

        //No context by default.. Ensure dependent widgets know about it.
        groupChangedEvent.fire( new OrganizationalUnitChangeEvent( null ) );

        ( (SessionInfoImpl) sessionInfo ).setId( ( (ClientMessageBusImpl) bus ).getSessionId() );
    }

    @Produces
    @ApplicationScoped
    public SessionInfo currentSession() {
        if ( sessionInfo == null ) {
            setupSessionInfo();
        }
        return sessionInfo;
    }

    private void setupSessionInfo() {
        sessionInfo = new SessionInfoImpl( identity );
    }

    private void handleIntegration( final Map<String, List<String>> parameters ) {
        if ( parameters.containsKey( "perspective" ) && !parameters.get( "perspective" ).isEmpty() ) {
            placeManager.goTo( new DefaultPlaceRequest( parameters.get( "perspective" ).get( 0 ) ) );
        } else if ( parameters.containsKey( "path" ) && !parameters.get( "path" ).isEmpty() ) {

            vfsService.call( new RemoteCallback<Path>() {
                @Override
                public void callback( Path response ) {
                    if ( parameters.containsKey( "editor" ) && !parameters.get( "editor" ).isEmpty() ) {
                        placeManager.goTo( new PathPlaceRequest( response, parameters.get( "editor" ).get( 0 ) ) );
                    } else {
                        placeManager.goTo( new PathPlaceRequest( response ) );
                    }
                }
            } ).get( parameters.get( "path" ).get( 0 ) );

        }
    }

    private void loadStyles() {
        //Ensure CSS has been loaded
        WorkbenchResources.INSTANCE.CSS().ensureInjected();
    }
}
