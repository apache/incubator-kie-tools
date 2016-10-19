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

package org.uberfire.ext.preferences.client.central.actions;

import javax.enterprise.event.Event;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.preferences.client.admin.AdminPagePerspective;
import org.uberfire.ext.preferences.client.event.PreferencesCentralPreSaveEvent;
import org.uberfire.ext.preferences.client.event.PreferencesCentralSaveEvent;
import org.uberfire.ext.preferences.client.event.PreferencesCentralUndoChangesEvent;
import org.uberfire.workbench.events.NotificationEvent;

import static org.mockito.Mockito.*;

public class PreferencesCentralActionsScreenTest {

    @Mock
    private PreferencesCentralActionsScreen.View view;

    @Mock
    private PlaceManager placeManager;

    @Mock
    private Event<PreferencesCentralPreSaveEvent> preSaveEvent;

    @Mock
    private Event<PreferencesCentralSaveEvent> saveEvent;

    @Mock
    private Event<PreferencesCentralUndoChangesEvent> undoChangesEvent;

    @Mock
    private Event<NotificationEvent> notification;

    private PreferencesCentralActionsScreen actionsScreen;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks( this );

        actionsScreen = new PreferencesCentralActionsScreen( view,
                                                             placeManager,
                                                             preSaveEvent,
                                                             saveEvent,
                                                             undoChangesEvent,
                                                             notification );
    }

    @Test
    public void fireSaveEvent() {
        actionsScreen.fireSaveEvent();

        verify( preSaveEvent ).fire( any( PreferencesCentralPreSaveEvent.class ) );
        verify( saveEvent ).fire( any( PreferencesCentralSaveEvent.class ) );
        verify( placeManager ).goTo( AdminPagePerspective.IDENTIFIER );
    }

    @Test
    public void fireUndoEventTest() {
        actionsScreen.fireCancelEvent();

        verify( undoChangesEvent ).fire( any( PreferencesCentralUndoChangesEvent.class ) );
        verify( notification ).fire( any( NotificationEvent.class ) );
        verify( placeManager ).goTo( AdminPagePerspective.IDENTIFIER );
    }
}
