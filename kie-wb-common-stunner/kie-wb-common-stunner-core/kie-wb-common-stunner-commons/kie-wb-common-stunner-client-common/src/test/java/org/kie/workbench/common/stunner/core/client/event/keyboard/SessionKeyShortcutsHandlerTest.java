/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.event.keyboard;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SessionKeyShortcutsHandlerTest {

    @Mock
    private ClientKeyShortcutsHandler clientKeyShortcutsHandler;

    @Mock
    private SessionManager clientSessionManager;

    @Mock
    private ClientSession session;

    private SessionKeyShortcutsHandler tested;

    @Before
    public void setup() throws Exception {
        when(clientSessionManager.getCurrentSession()).thenReturn(session);
        this.tested = new SessionKeyShortcutsHandler(clientSessionManager,
                                                     clientKeyShortcutsHandler);
    }

    @Test
    public void testCallbacksWithBindUnbindSession() {
        final SessionKeyShortcutsHandler.SessionKeyShortcutCallback[] sessionCallback = new SessionKeyShortcutsHandler.SessionKeyShortcutCallback[1];
        doAnswer(invocationOnMock -> {
            sessionCallback[0] = (SessionKeyShortcutsHandler.SessionKeyShortcutCallback) invocationOnMock.getArguments()[0];
            return null;
        }).when(clientKeyShortcutsHandler).setKeyShortcutCallback(any(ClientKeyShortcutsHandler.KeyShortcutCallback.class));
        final ClientKeyShortcutsHandler.KeyShortcutCallback callback = mock(ClientKeyShortcutsHandler.KeyShortcutCallback.class);
        tested.setKeyShortcutCallback(callback);
        assertEquals(callback,
                     sessionCallback[0].getDelegate());
        verify(callback,
               never()).onKeyShortcut(any(KeyboardEvent.Key.class));
        tested.bind(session);
        sessionCallback[0].onKeyShortcut(KeyboardEvent.Key.ESC);
        verify(callback,
               times(1)).onKeyShortcut(eq(KeyboardEvent.Key.ESC));
        tested.unbind();
        // Verify the callback is not called a second time, due to the above session unbind.
        sessionCallback[0].onKeyShortcut(KeyboardEvent.Key.ESC);
        verify(callback,
               times(1)).onKeyShortcut(any(KeyboardEvent.Key.class));
    }
}
