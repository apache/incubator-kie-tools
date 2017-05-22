/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.preferences.client.central.screen;

import javax.enterprise.event.Event;

import org.jboss.errai.common.client.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.preferences.client.central.PreferencesCentralNavBarScreen;
import org.uberfire.ext.preferences.client.central.actions.PreferencesCentralActionsScreen;
import org.uberfire.ext.preferences.client.event.HierarchyItemFormInitializationEvent;
import org.uberfire.ext.preferences.client.event.HierarchyItemSelectedEvent;
import org.uberfire.ext.preferences.client.utils.PreferenceFormBeansInfo;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.preferences.shared.bean.BasePreferencePortable;
import org.uberfire.preferences.shared.bean.PreferenceHierarchyElement;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PreferencesRootScreenTest {

    @Mock
    private PreferencesRootScreen.View view;

    @Mock
    private PlaceManager placeManager;

    @Mock
    private PreferenceFormBeansInfo preferenceFormBeansInfo;

    @Mock
    private Event<HierarchyItemFormInitializationEvent> hierarchyItemFormInitializationEvent;

    @InjectMocks
    private PreferencesRootScreen screen;

    @Before
    public void setup() {
        doReturn(mock(HTMLElement.class)).when(view).getNavbarContainer();
        doReturn(mock(HTMLElement.class)).when(view).getActionsContainer();
    }

    @Test
    public void setupTest() {
        screen.setup();

        verify(placeManager).goTo(new DefaultPlaceRequest(PreferencesCentralNavBarScreen.IDENTIFIER),
                                  view.getNavbarContainer());
        verify(placeManager).goTo(new DefaultPlaceRequest(PreferencesCentralActionsScreen.IDENTIFIER),
                                  view.getActionsContainer());
    }

    @Test
    public void onCloseTest() {
        screen.onClose();

        verify(placeManager).closePlace(PreferencesCentralNavBarScreen.IDENTIFIER);
        verify(placeManager).closePlace(PreferencesCentralActionsScreen.IDENTIFIER);
    }

    @Test
    public void hierarchyItemSelectedEventWithoutPreviouslyOpenedPreference() {
        final HierarchyItemSelectedEvent event = mock(HierarchyItemSelectedEvent.class);
        doReturn(mock(BasePreferencePortable.class)).when(event).getPreference();
        doReturn(mock(PreferenceHierarchyElement.class)).when(event).getHierarchyElement();

        screen.hierarchyItemSelectedEvent(event);

        verify(placeManager,
               never()).closePlace(any(PlaceRequest.class));
        verify(placeManager).goTo(any(PlaceRequest.class),
                                  any(HTMLElement.class));
        verify(hierarchyItemFormInitializationEvent).fire(any());
    }

    @Test
    public void hierarchyItemSelectedEventWithPreviouslyOpenedPreference() {
        hierarchyItemSelectedEventWithoutPreviouslyOpenedPreference();
        Mockito.reset(placeManager,
                      hierarchyItemFormInitializationEvent);

        final HierarchyItemSelectedEvent event = mock(HierarchyItemSelectedEvent.class);
        doReturn(mock(BasePreferencePortable.class)).when(event).getPreference();
        doReturn(mock(PreferenceHierarchyElement.class)).when(event).getHierarchyElement();

        screen.hierarchyItemSelectedEvent(event);

        verify(placeManager).closePlace(any(PlaceRequest.class));
        verify(placeManager).goTo(any(PlaceRequest.class),
                                  any(HTMLElement.class));
        verify(hierarchyItemFormInitializationEvent).fire(any());
    }
}
