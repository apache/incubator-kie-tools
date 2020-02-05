/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.command;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.event.mouse.CanvasMouseDownEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.mouse.CanvasMouseUpEvent;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MouseRequestLifecycleTest {

    @Mock
    private CommandRequestLifecycle lifecycle;

    private MouseRequestLifecycle tested;

    @Before
    public void setUp() {
        tested = new MouseRequestLifecycle();
        tested.listen(() -> lifecycle);
    }

    @Test
    public void testStart() {
        tested.start();
        verify(lifecycle, times(1)).start();
        verify(lifecycle, never()).complete();
        verify(lifecycle, never()).rollback();
    }

    @Test
    public void testRollback() {
        tested.rollback();
        verify(lifecycle, times(1)).rollback();
        verify(lifecycle, never()).complete();
        verify(lifecycle, never()).start();
    }

    @Test
    public void testComplete() {
        tested.complete();
        verify(lifecycle, times(1)).complete();
        verify(lifecycle, never()).rollback();
        verify(lifecycle, never()).start();
    }

    @Test
    public void testOnMouseDown() {
        tested.onMouseDown(mock(CanvasMouseDownEvent.class));
        verify(lifecycle, times(1)).start();
        verify(lifecycle, never()).complete();
        verify(lifecycle, never()).rollback();
    }

    @Test
    public void testOnMouseUp() {
        tested.onMouseUp(mock(CanvasMouseUpEvent.class));
        verify(lifecycle, times(1)).complete();
        verify(lifecycle, never()).rollback();
        verify(lifecycle, never()).start();
    }
}
