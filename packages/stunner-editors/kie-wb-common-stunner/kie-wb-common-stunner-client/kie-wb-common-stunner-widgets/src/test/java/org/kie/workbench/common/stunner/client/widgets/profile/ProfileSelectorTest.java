/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.client.widgets.profile;

import java.util.Collections;
import java.util.function.Function;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.RequestSessionRefreshEvent;
import org.kie.workbench.common.stunner.client.widgets.views.SelectorImpl;
import org.kie.workbench.common.stunner.core.api.ProfileManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.profile.Profile;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ProfileSelectorTest {

    private static final String DEF_SET_ID = "dewfSet1";
    private static final String SESSION_UUID = "session1";
    private static final String PROFILE_ID = "profile1";
    private static final String PROFILE_NAME = "profileName1";

    @Mock
    private SelectorImpl<Profile> selector;

    @Mock
    private ProfileManager profileManager;

    @Mock
    private Profile profile1;

    @Mock
    private EventSourceMock<RequestSessionRefreshEvent> requestSessionRefreshEvent;

    private ProfileSelector tested;
    private AbstractSession session;
    private Metadata metadata;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        session = mock(AbstractSession.class);
        AbstractCanvasHandler canvasHandler = mock(AbstractCanvasHandler.class);
        Diagram diagram = mock(Diagram.class);
        metadata = mock(Metadata.class);
        when(session.getSessionUUID()).thenReturn(SESSION_UUID);
        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getDefinitionSetId()).thenReturn(DEF_SET_ID);
        when(metadata.getProfileId()).thenReturn(PROFILE_ID);
        when(selector.setItemProvider(any(Function.class))).thenReturn(selector);
        when(selector.setTextProvider(any(Function.class))).thenReturn(selector);
        when(selector.setValueProvider(any(Function.class))).thenReturn(selector);
        when(selector.setValueChangedCommand(any(Command.class))).thenReturn(selector);
        when(profileManager.getProfile(eq(DEF_SET_ID), eq(PROFILE_ID))).thenReturn(profile1);
        when(profileManager.getProfiles(eq(DEF_SET_ID))).thenReturn(Collections.singleton(profile1));
        when(profile1.getProfileId()).thenReturn(PROFILE_ID);
        when(profile1.getName()).thenReturn(PROFILE_NAME);
        tested = new ProfileSelector(selector,
                                     profileManager,
                                     requestSessionRefreshEvent);
        tested.init();
    }

    @Test
    public void testBind() {
        when(selector.getSelectedItem()).thenReturn(profile1);
        tested.bind(() -> session);
        verify(selector, times(1)).clear();
        verify(selector, times(1)).addItem(eq(profile1));
        ArgumentCaptor<Command> commandArgumentCaptor = ArgumentCaptor.forClass(Command.class);
        verify(selector, times(1)).setValueChangedCommand(commandArgumentCaptor.capture());
        Command command = commandArgumentCaptor.getValue();
        command.execute();
        verify(metadata, times(1)).setProfileId(eq(PROFILE_ID));
        ArgumentCaptor<RequestSessionRefreshEvent> eventArgumentCaptor =
                ArgumentCaptor.forClass(RequestSessionRefreshEvent.class);
        verify(requestSessionRefreshEvent, times(1)).fire(eventArgumentCaptor.capture());
        RequestSessionRefreshEvent event = eventArgumentCaptor.getValue();
        assertEquals(SESSION_UUID, event.getSessionUUID());
    }
}
