/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.widgets.canvas;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ScrollableLienzoPanelViewTest {

    @Mock
    private StunnerLienzoBoundsPanel presenter;

    private ScrollableLienzoPanelView tested;

    @Before
    @SuppressWarnings("unchecked")
    public void init() {
        tested = spy(new ScrollableLienzoPanelView(300, 150));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetPresenter() {
        tested.setPresenter(presenter);
        verify(presenter, times(3)).register(any(HandlerRegistration.class));
        ArgumentCaptor<KeyDownHandler> downCaptor = ArgumentCaptor.forClass(KeyDownHandler.class);
        verify(tested, times(1)).addKeyDownHandler(downCaptor.capture());
        KeyDownHandler downHandler = downCaptor.getValue();
        KeyDownEvent keyDownEvent = mock(KeyDownEvent.class);
        when(keyDownEvent.getNativeKeyCode()).thenReturn(11);
        downHandler.onKeyDown(keyDownEvent);
        verify(presenter, times(1)).onKeyDown(eq(11));
        ArgumentCaptor<KeyPressHandler> pressCaptor = ArgumentCaptor.forClass(KeyPressHandler.class);
        verify(tested, times(1)).addKeyPressHandler(pressCaptor.capture());
        KeyPressHandler pressHandler = pressCaptor.getValue();
        KeyPressEvent kePressEvent = mock(KeyPressEvent.class);
        when(kePressEvent.getUnicodeCharCode()).thenReturn(33);
        pressHandler.onKeyPress(kePressEvent);
        verify(presenter, times(1)).onKeyPress(eq(33));
        ArgumentCaptor<KeyUpHandler> upCaptor = ArgumentCaptor.forClass(KeyUpHandler.class);
        verify(tested, times(1)).addKeyUpHandler(upCaptor.capture());
        KeyUpHandler upHandler = upCaptor.getValue();
        KeyUpEvent keyUpEvent = mock(KeyUpEvent.class);
        when(keyUpEvent.getNativeKeyCode()).thenReturn(55);
        upHandler.onKeyUp(keyUpEvent);
        verify(presenter, times(1)).onKeyUp(eq(55));
    }
}
