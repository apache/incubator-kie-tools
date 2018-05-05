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

package org.kie.workbench.common.stunner.core.client.session.impl;

import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.controls.pan.PanControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.MultipleSelection;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.zoom.ZoomControl;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.Command;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultViewerSessionTest {

    @Mock
    private ManagedSession managedSession;

    private DefaultViewerSession tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        when(managedSession.onCanvasControlRegistered(any(Consumer.class))).thenReturn(managedSession);
        when(managedSession.onCanvasControlDestroyed(any(Consumer.class))).thenReturn(managedSession);
        when(managedSession.onCanvasHandlerControlRegistered(any(Consumer.class))).thenReturn(managedSession);
        when(managedSession.onCanvasHandlerControlDestroyed(any(Consumer.class))).thenReturn(managedSession);
        when(managedSession.registerCanvasControl(any(Class.class))).thenReturn(managedSession);
        when(managedSession.registerCanvasHandlerControl(any(Class.class))).thenReturn(managedSession);
        tested = new DefaultViewerSession(managedSession);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testConstruct() {
        tested.constructInstance();
        verify(managedSession, times(1)).onCanvasControlRegistered(any(Consumer.class));
        verify(managedSession, times(1)).onCanvasControlDestroyed(any(Consumer.class));
        verify(managedSession, times(1)).onCanvasHandlerControlRegistered(any(Consumer.class));
        verify(managedSession, times(1)).onCanvasHandlerControlDestroyed(any(Consumer.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInit() {
        Metadata metadata = mock(Metadata.class);
        Command command = mock(Command.class);
        tested.init(metadata,
                    command);
        verify(managedSession, times(1)).registerCanvasControl(eq(ZoomControl.class));
        verify(managedSession, times(1)).registerCanvasControl(eq(PanControl.class));
        verify(managedSession, times(1)).registerCanvasHandlerControl(eq(SelectionControl.class),
                                                                      eq(MultipleSelection.class));
        verify(managedSession, times(1)).init(eq(metadata),
                                              eq(command));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOpen() {
        tested.open();
        verify(managedSession, times(1)).open();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDestroy() {
        tested.destroy();
        verify(managedSession, times(1)).destroy();
    }
}
