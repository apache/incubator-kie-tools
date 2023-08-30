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

package org.kie.workbench.common.dmn.client.widgets.grid.controls.popover;

import java.util.Optional;
import java.util.function.Consumer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.Event;
import elemental2.dom.KeyboardEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.CanBeClosedByKeyboard;
import org.uberfire.client.views.pfly.widgets.PopoverOptions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class AbstractPopoverViewImplTest {

    private AbstractPopoverViewImpl view;

    @Before
    public void setup() {
        this.view = spy(new AbstractPopoverViewImpl() {
            //Nothing to implement
        });
    }

    @Test
    public void testCreateOptions() {

        final PopoverOptions options = mock(PopoverOptions.class);

        doReturn(options).when(view).createPopoverOptionsInstance();

        view.createOptions();

        verify(options).setAnimation(false);
        verify(options).setHtml(true);
        verify(options).setPlacement(AbstractPopoverViewImpl.PLACEMENT);
    }

    @Test
    public void testOnClosedByKeyboard() {
        final Consumer consumer = mock(Consumer.class);
        final Optional opt = Optional.of(consumer);
        doReturn(opt).when(view).getClosedByKeyboardCallback();

        view.onClosedByKeyboard();

        verify(consumer).accept(view);
    }

    @Test
    public void testSetOnClosedByKeyboardCallback() {
        final Consumer consumer = mock(Consumer.class);
        view.setOnClosedByKeyboardCallback(consumer);

        final Optional<Consumer<CanBeClosedByKeyboard>> actual = view.getClosedByKeyboardCallback();

        assertTrue(actual.isPresent());
        assertEquals(consumer, actual.get());
    }

    @Test
    public void testSetOnClosedByKeyboardCallbackNullCallback() {
        view.setOnClosedByKeyboardCallback(null);

        final Optional<Consumer<CanBeClosedByKeyboard>> actual = view.getClosedByKeyboardCallback();

        assertFalse(actual.isPresent());
        assertEquals(Optional.empty(), actual);
    }

    @Test
    public void testKeyDownEventListenerEnterKey() {
        final KeyboardEvent event = mock(KeyboardEvent.class);

        doNothing().when(view).hide();
        doNothing().when(view).onClosedByKeyboard();

        doReturn(true).when(view).isEnterKeyPressed(event);

        view.keyDownEventListener(event);

        verify(view).hide();
        verify(event).stopPropagation();
        verify(view).onClosedByKeyboard();
        verify(view, never()).isEscapeKeyPressed(event);
    }

    @Test
    public void testKeyDownEventListenerEscKey() {
        final KeyboardEvent event = mock(KeyboardEvent.class);

        doNothing().when(view).hide();
        doNothing().when(view).reset();
        doNothing().when(view).onClosedByKeyboard();

        doReturn(false).when(view).isEnterKeyPressed(event);
        doReturn(true).when(view).isEscapeKeyPressed(event);

        view.keyDownEventListener(event);

        verify(view).hide();
        verify(view).reset();
        verify(view).onClosedByKeyboard();
    }

    @Test
    public void testKeyDownEventListenerWhenIsNotAHandledKey() {

        final KeyboardEvent event = mock(KeyboardEvent.class);

        doReturn(false).when(view).isEnterKeyPressed(event);
        doReturn(false).when(view).isEscapeKeyPressed(event);

        view.keyDownEventListener(event);

        verify(view, never()).hide();
        verify(event, never()).stopPropagation();
        verify(view, never()).onClosedByKeyboard();
        verify(view, never()).reset();
    }

    @Test
    public void testKeyDownEventListenerWhenIsNotKeyboardEvent() {

        final Event event = mock(Event.class);
        view.keyDownEventListener(event);

        verify(view, never()).hide();
        verify(event, never()).stopPropagation();
        verify(view, never()).onClosedByKeyboard();
        verify(view, never()).isEscapeKeyPressed(any());
        verify(view, never()).reset();
    }
}