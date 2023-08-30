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
import org.jboss.errai.common.client.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.CanBeClosedByKeyboard;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class AbstractPopoverImplTest {

    private static final int UI_ROW_INDEX = 0;

    private static final int UI_COLUMN_INDEX = 1;

    @Mock
    private PopoverView view;

    @Mock
    private Consumer<CanBeClosedByKeyboard> callback;

    private Object control = new Object();

    private AbstractPopoverImpl<PopoverView, Object> popover;

    @Before
    public void setup() {
        this.popover = spy(new AbstractPopoverImpl<PopoverView, Object>(view) {
            //NOP
        });
    }

    @Test
    public void testGetElement() {
        final HTMLElement element = mock(HTMLElement.class);

        when(popover.getElement()).thenReturn(element);

        final HTMLElement actual = popover.getElement();

        assertEquals(element, actual);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetOnClosedByKeyboardCallbackNullControl() {
        popover.setOnClosedByKeyboardCallback(callback);

        verify(view, never()).setOnClosedByKeyboardCallback(any(Consumer.class));
    }

    @Test
    public void testSetOnClosedByKeyboardCallbackNonNullControl() {
        popover.bind(control,
                     UI_ROW_INDEX,
                     UI_COLUMN_INDEX);

        popover.setOnClosedByKeyboardCallback(callback);

        verify(view).setOnClosedByKeyboardCallback(callback);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testShowNullControl() {
        popover.show();

        verify(view, never()).show(any(Optional.class));
    }

    @Test
    public void testShowNonNullControl() {
        popover.bind(control,
                     UI_ROW_INDEX,
                     UI_COLUMN_INDEX);

        reset(view);

        popover.show();

        verify(view).show(eq(Optional.empty()));
    }

    @Test
    public void testHideNullControl() {
        popover.hide();

        verify(view, never()).hide();
    }

    @Test
    public void testHideNonNullControl() {
        popover.bind(control,
                     UI_ROW_INDEX,
                     UI_COLUMN_INDEX);

        reset(view);

        popover.hide();

        verify(view).hide();
    }
}