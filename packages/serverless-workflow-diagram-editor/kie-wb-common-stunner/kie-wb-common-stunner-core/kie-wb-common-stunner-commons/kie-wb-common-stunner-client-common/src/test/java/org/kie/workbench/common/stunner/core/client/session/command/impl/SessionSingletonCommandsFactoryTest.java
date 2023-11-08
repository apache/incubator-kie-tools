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


package org.kie.workbench.common.stunner.core.client.session.command.impl;

import jakarta.enterprise.event.Event;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SessionSingletonCommandsFactoryTest {

    @Mock
    protected EditorSession session;

    @Mock
    protected EditorSession session2;

    @Mock
    protected SessionManager sessionManager;

    @Mock
    protected Event<CutSelectionSessionCommandExecutedEvent> commandExecutedEvent;

    @Before
    public void setUp() throws Exception {
        when(sessionManager.getCurrentSession()).thenReturn(session);
    }

    @Test(expected = IllegalStateException.class)
    public void testOnlyOneInstancePerSessionCopy() {
        final CopySelectionSessionCommand copySelectionSessionCommand = new CopySelectionSessionCommand(null, sessionManager);
        final CopySelectionSessionCommand copySelectionSessionCommand2 = new CopySelectionSessionCommand(null, sessionManager);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testOnlyAllowedCommands() {
        final CutSelectionSessionCommand cut = new CutSelectionSessionCommand(commandExecutedEvent, sessionManager);
        SessionSingletonCommandsFactory.createOrPut(cut, sessionManager);
    }

    @Test
    public void testNewInstancesOndifferentSessionsCopy() {
        final CopySelectionSessionCommand copySelectionSessionCommand = new CopySelectionSessionCommand(null, sessionManager);
        when(sessionManager.getCurrentSession()).thenReturn(session2);
        final CopySelectionSessionCommand copySelectionSessionCommand2 = new CopySelectionSessionCommand(null, sessionManager);
        assertTrue(copySelectionSessionCommand.hashCode() != copySelectionSessionCommand2.hashCode());
    }

    @Test
    public void testGetInstancesCopy() {
        final CopySelectionSessionCommand copySelectionSessionCommand = new CopySelectionSessionCommand(null, sessionManager);
        final CopySelectionSessionCommand instanceCopy = SessionSingletonCommandsFactory.getInstanceCopy(null, sessionManager);

        assertEquals(copySelectionSessionCommand, instanceCopy);

        when(sessionManager.getCurrentSession()).thenReturn(session2);
        final CopySelectionSessionCommand copySelectionSessionCommand2 = new CopySelectionSessionCommand(null, sessionManager);
        final CopySelectionSessionCommand instanceCopy2 = SessionSingletonCommandsFactory.getInstanceCopy(null, sessionManager);

        assertEquals(copySelectionSessionCommand2, instanceCopy2);
    }

    @Test
    public void testGetInstancesOnFetchCopy() {
        final CopySelectionSessionCommand instanceCopy = SessionSingletonCommandsFactory.getInstanceCopy(null, sessionManager);
        final CopySelectionSessionCommand instanceCopy2 = SessionSingletonCommandsFactory.getInstanceCopy(null, sessionManager);

        assertEquals(instanceCopy, instanceCopy2);
    }

    @Test(expected = IllegalStateException.class)
    public void testGetInstancesOnFetchCopyError() {
        final CopySelectionSessionCommand instanceCopy = SessionSingletonCommandsFactory.getInstanceCopy(null, sessionManager);
        final CopySelectionSessionCommand instanceCopy2 = new CopySelectionSessionCommand(null, sessionManager);
    }

    @Test(expected = IllegalStateException.class)
    public void testOnlyOneInstancePerSessionDelete() {
        final DeleteSelectionSessionCommand deleteSelectionSessionCommand = new DeleteSelectionSessionCommand(null, null, null, null, sessionManager, null);
        final DeleteSelectionSessionCommand deleteSelectionSessionCommand2 = new DeleteSelectionSessionCommand(null, null, null, null, sessionManager, null);
    }

    @Test
    public void testNewInstancesOndifferentSessionsDelete() {
        final DeleteSelectionSessionCommand deleteSelectionSessionCommand = new DeleteSelectionSessionCommand(null, null, null, null, sessionManager, null);
        when(sessionManager.getCurrentSession()).thenReturn(session2);
        final DeleteSelectionSessionCommand deleteSelectionSessionCommand2 = new DeleteSelectionSessionCommand(null, null, null, null, sessionManager, null);
        assertTrue(deleteSelectionSessionCommand.hashCode() != deleteSelectionSessionCommand2.hashCode());
    }

    @Test
    public void testGetInstancesDelete() {
        final DeleteSelectionSessionCommand deleteSelectionSessionCommand = new DeleteSelectionSessionCommand(null, null, null, null, sessionManager, null);
        final DeleteSelectionSessionCommand instanceCopy = SessionSingletonCommandsFactory.getInstanceDelete(null, null, null, null, sessionManager, null);

        assertEquals(deleteSelectionSessionCommand, instanceCopy);

        when(sessionManager.getCurrentSession()).thenReturn(session2);
        final DeleteSelectionSessionCommand deleteSelectionSessionCommand2 = new DeleteSelectionSessionCommand(null, null, null, null, sessionManager, null);
        final DeleteSelectionSessionCommand instanceCopy2 = SessionSingletonCommandsFactory.getInstanceDelete(null, null, null, null, sessionManager, null);

        assertEquals(deleteSelectionSessionCommand2, instanceCopy2);
    }

    @Test(expected = IllegalStateException.class)
    public void testGetInstancesOnFetchDelete() {
        final DeleteSelectionSessionCommand instanceCopy = SessionSingletonCommandsFactory.getInstanceDelete(null, null, null, null, sessionManager, null);
        final DeleteSelectionSessionCommand instanceCopy2 = new DeleteSelectionSessionCommand(null, null, null, null, sessionManager, null);
    }
}
