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

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ClientKeyShortcutsHandlerTest {

    @Mock
    ClientKeyShortcutsHandler.KeyShortcutCallback shortcutCallback;

    @Mock
    KeyUpEvent keyUpEvent;

    @Mock
    KeyDownEvent keyDownEvent;

    private ClientKeyShortcutsHandler tested;

    @Before
    public void setup() throws Exception {
        this.tested = new ClientKeyShortcutsHandler();
        this.tested.setKeyShortcutCallback(shortcutCallback);
    }

    @Test
    public void testKeyShortcut() {
        final KeyboardEvent.Key key1 = KeyboardEvent.Key.CONTROL;
        final KeyboardEvent.Key key2 = KeyboardEvent.Key.DELETE;
        when(keyDownEvent.getKey()).thenReturn(key1);
        tested.onKeyDownEvent(keyDownEvent);
        when(keyDownEvent.getKey()).thenReturn(key2);
        tested.onKeyDownEvent(keyDownEvent);
        tested.keysTimerTimeIsUp();
        verify(shortcutCallback,
               times(1)).onKeyShortcut(eq(key1),
                                       eq(key2));
    }
}
