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
import org.kie.workbench.common.stunner.core.client.ManagedInstanceStub;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDestroyedEvent;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationCommandManagerTest {

    private static final String SESSION_ID = "s1";

    @Mock
    private SessionManager sessionManager;

    @Mock
    private RegistryAwareCommandManager commandManager;
    private ManagedInstanceStub<RegistryAwareCommandManager> commandManagerInstances;

    @Mock
    private ClientSession session;

    private ApplicationCommandManager tested;
    private MouseRequestLifecycle lifecycle;

    @Before
    public void setUp() {

        lifecycle = spy(new MouseRequestLifecycle());
        commandManagerInstances = spy(new ManagedInstanceStub<>(commandManager));
        when(session.getSessionUUID()).thenReturn(SESSION_ID);
        when(sessionManager.getCurrentSession()).thenReturn(session);
        when(commandManager.init(eq(session))).thenReturn(commandManager);

        tested = new ApplicationCommandManager(sessionManager,
                                               lifecycle,
                                               commandManagerInstances);
    }

    @Test
    public void testInit() {
        tested.init();
        assertEquals(tested, lifecycle.getTarget());
        verify(lifecycle, never()).start();
        verify(lifecycle, never()).rollback();
        verify(lifecycle, never()).complete();
    }

    @Test
    public void testStart() {
        tested.start();
        verify(commandManager, times(1)).init(eq(session));
        verify(commandManager, times(1)).start();
        verify(commandManager, never()).rollback();
        verify(commandManager, never()).complete();
    }

    @Test
    public void testComplete() {
        tested.start();
        tested.complete();
        verify(commandManager, times(1)).start();
        verify(commandManager, times(1)).complete();
        verify(commandManager, never()).rollback();
    }

    @Test
    public void testRollback() {
        tested.start();
        tested.rollback();
        verify(commandManager, times(1)).start();
        verify(commandManager, times(1)).rollback();
        verify(commandManager, never()).complete();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAllow() {
        Command<AbstractCanvasHandler, CanvasViolation> command = mock(Command.class);
        tested.allow(command);
        verify(commandManager, times(1)).allow(eq(command));
        verify(commandManager, never()).execute(any());
        verify(commandManager, never()).undo();
        verify(commandManager, never()).undo(any(), any());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecute() {
        Command<AbstractCanvasHandler, CanvasViolation> command = mock(Command.class);
        tested.execute(command);
        verify(commandManager, times(1)).execute(eq(command));
        verify(commandManager, never()).allow(any());
        verify(commandManager, never()).undo();
        verify(commandManager, never()).undo(any(), any());
    }

    @Test
    public void testUndo() {
        tested.undo();
        verify(commandManager, times(1)).undo();
        verify(commandManager, never()).execute(any());
        verify(commandManager, never()).allow(any());
    }

    @Test
    public void testOnSessionDestroyed() {
        tested.start();
        tested.onSessionDestroyed(new SessionDestroyedEvent(SESSION_ID,
                                                            "diagram1",
                                                            "graph1",
                                                            mock(Metadata.class)));
        assertTrue(tested.getCommandManagers().isEmpty());
        verify(commandManagerInstances, times(1)).destroy(eq(commandManager));
    }

    @Test
    public void testDestroy() {
        tested.start();
        tested.destroy();
        assertTrue(tested.getCommandManagers().isEmpty());
        verify(commandManagerInstances, times(1)).destroyAll();
    }
}
