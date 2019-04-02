/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard;

import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyDownEvent;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyUpEvent;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class KeyEventHandlerTest {

    @Mock
    private KeyboardControl.KeyShortcutCallback shortcutCallback;

    @Mock
    private KeyDownEvent keyDownEvent;

    @Captor
    private ArgumentCaptor<KeyboardEvent.Key> keysArgumentCaptor;

    private KeyEventHandler tested;

    @Before
    public void setup() throws Exception {
        this.tested = spy(new KeyEventHandler());
        this.tested.addKeyShortcutCallback(shortcutCallback);
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
               times(1)).onKeyShortcut(keysArgumentCaptor.capture());
        verify(tested,
               never()).reset();

        final List<KeyboardEvent.Key> keys = keysArgumentCaptor.getAllValues();
        assertTrue(keys.contains(key1));
        assertTrue(keys.contains(key2));
    }

    @Test
    public void testOnKeyUp() {
        tested.onKeyUpEvent(new KeyUpEvent(KeyboardEvent.Key.ALT));
        verify(shortcutCallback, times(1)).onKeyUp(eq(KeyboardEvent.Key.ALT));
    }
}
