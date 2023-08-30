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


package org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class KeyboardControlImplTest {

    @Mock
    private KeyEventHandler keyEventHandler;

    @Mock
    private SessionManager clientSessionManager;

    @Mock
    private ClientSession session;

    private KeyboardControlImpl tested;

    @Before
    public void setup() throws Exception {
        when(clientSessionManager.getCurrentSession()).thenReturn(session);
        this.tested = new KeyboardControlImpl(clientSessionManager,
                                              keyEventHandler);
    }

    @Test
    public void testCallbacksWithBindUnbindSession() {
        final KeyboardControlImpl.SessionKeyShortcutCallback[] sessionCallback = new KeyboardControlImpl.SessionKeyShortcutCallback[1];
        doAnswer(invocationOnMock -> {
            sessionCallback[0] = (KeyboardControlImpl.SessionKeyShortcutCallback) invocationOnMock.getArguments()[0];
            return null;
        }).when(keyEventHandler).addKeyShortcutCallback(any(KeyboardControl.KeyShortcutCallback.class));
        final KeyboardControl.KeyShortcutCallback callback = mock(KeyboardControl.KeyShortcutCallback.class);
        tested.addKeyShortcutCallback(callback);
        verify(keyEventHandler).addKeyShortcutCallback(any());
        assertEquals(callback,
                     sessionCallback[0].getDelegate());
        verify(callback,
               never()).onKeyShortcut(any(KeyboardEvent.Key.class));
        tested.bind(session);
        sessionCallback[0].onKeyShortcut(KeyboardEvent.Key.ESC);
        verify(callback,
               times(1)).onKeyShortcut(eq(KeyboardEvent.Key.ESC));
    }
}
