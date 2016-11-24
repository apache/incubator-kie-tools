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

package org.kie.workbench.common.screens.library.client.screens;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.library.api.LibraryContextSwitchEvent;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.ResourceRef;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.model.ActivityResourceType;

@WorkbenchScreen( identifier = LibraryPlaces.EMPTY_LIBRARY_SCREEN )
public class EmptyLibraryScreen {

    public interface View extends UberElement<EmptyLibraryScreen> {

        void setup( String username );

        void openNoRightsPopup();
    }

    @Inject
    private View view;

    @Inject
    private User user;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private AuthorizationManager authorizationManager;

    @Inject
    private SessionInfo sessionInfo;

    @Inject
    private TranslationService ts;

    @Inject
    Event<LibraryContextSwitchEvent> libraryContextSwitchEvent;

    @PostConstruct
    public void setup() {
        view.init( this );
        view.setup( user.getIdentifier() );
    }

    public void newProject() {
        placeManager.goTo( new DefaultPlaceRequest( LibraryPlaces.NEW_PROJECT_SCREEN ) );
    }

    public void importExample() {
        if ( hasAccessToPerspective( LibraryPlaces.AUTHORING ) ) {
            placeManager.goTo( new DefaultPlaceRequest( LibraryPlaces.AUTHORING ) );
            libraryContextSwitchEvent
                    .fire( new LibraryContextSwitchEvent( LibraryContextSwitchEvent.EventType.PROJECT_FROM_EXAMPLE ) );
        } else {
            openNoRightsPopup();
        }
    }

    void openNoRightsPopup() {
        view.openNoRightsPopup();
    }

    boolean hasAccessToPerspective( String perspectiveId ) {
        ResourceRef resourceRef = new ResourceRef( perspectiveId, ActivityResourceType.PERSPECTIVE );
        return authorizationManager.authorize( resourceRef, sessionInfo.getIdentity() );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return ts.getTranslation( LibraryConstants.EmptyLibraryScreen );
    }

    @WorkbenchPartView
    public UberElement<EmptyLibraryScreen> getView() {
        return view;
    }
}
