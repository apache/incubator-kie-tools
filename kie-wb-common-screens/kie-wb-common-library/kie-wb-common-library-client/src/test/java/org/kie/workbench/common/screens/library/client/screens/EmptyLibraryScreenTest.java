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


import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.api.LibraryContextSwitchEvent;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.ResourceRef;
import org.uberfire.security.authz.AuthorizationManager;

import javax.enterprise.event.Event;
import javax.enterprise.util.TypeLiteral;
import java.lang.annotation.Annotation;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class EmptyLibraryScreenTest {

    @Mock
    private AuthorizationManager authorizationManager;

    @Mock
    private PlaceManager placeManager;

    @Mock
    EventMock eventMock;

    @Mock
    private SessionInfo sessionInfo;

    @InjectMocks
    EmptyLibraryScreen emptyLibrary;

    @Test
    public void importExampleTest() throws Exception {
        when( authorizationManager.authorize( ( ResourceRef ) any(), any() ) ).thenReturn( true );
        emptyLibrary.libraryContextSwitchEvent = eventMock;

        emptyLibrary.importExample();

        verify( placeManager ).goTo( any( PlaceRequest.class ) );
        verify( eventMock ).fire( any() );

    }

    @Test
    public void importExampleNoRightsTest() throws Exception {
        when( authorizationManager.authorize( ( ResourceRef ) any(), any() ) ).thenReturn( false );

        EmptyLibraryScreen spy = spy( emptyLibrary );
        doNothing().when( spy ).openNoRightsPopup();

        spy.importExample();

        verify( placeManager, never() ).goTo( any( PlaceRequest.class ) );
        verify( eventMock, never() ).fire( any() );
        verify( spy ).openNoRightsPopup();

    }

    static class EventMock extends EventSourceMock<LibraryContextSwitchEvent> {

        @Override
        public <U extends LibraryContextSwitchEvent> Event<U> select( TypeLiteral<U> typeLiteral,
                                                                      Annotation... annotations ) {
            return null;
        }
    }

}