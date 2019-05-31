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

package org.kie.workbench.common.stunner.core.client.session.command;

import java.lang.annotation.Annotation;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.Command;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ManagedClientSessionCommandsTest {

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private ManagedInstance<ClientSessionCommand> sessionCommands;

    private ManagedClientSessionCommands tested;

    @Before
    public void setUp() {
        tested = new ManagedClientSessionCommands(definitionUtils, sessionCommands);
    }

    @Test
    public void testDestroyCommands() {
        final ClientSession clientSession = mock(ClientSession.class);
        final CanvasHandler canvasHandler = mock(CanvasHandler.class);
        final Diagram diagram = mock(Diagram.class);
        final Metadata metadata = mock(Metadata.class);
        final String definitionSetId = "definitionSetId";
        when(clientSession.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getDefinitionSetId()).thenReturn(definitionSetId);

        final Annotation qualifier = mock(Annotation.class);
        when(definitionUtils.getQualifier(eq(definitionSetId))).thenReturn(qualifier);

        final ManagedInstance managedInstance = mock(ManagedInstance.class);
        final ClientSessionCommand clientSessionCommand = mock(ClientSessionCommand.class);
        when(sessionCommands.select(eq(MockClientSessionCommand.class), eq(qualifier))).thenReturn(managedInstance);
        when(managedInstance.get()).thenReturn(clientSessionCommand);

        tested.register(MockClientSessionCommand.class);
        tested.bind(clientSession);

        tested.clearCommands();

        verify(clientSessionCommand, times(1)).destroy();
        verify(sessionCommands, times(1)).destroy(eq(clientSessionCommand));
    }

    private static class MockClientSessionCommand implements ClientSessionCommand {

        @Override
        public ClientSessionCommand listen(Command statusCallback) {
            return null;
        }

        @Override
        public boolean isEnabled() {
            return false;
        }

        @Override
        public void destroy() {

        }

        @Override
        public void execute(Callback callback) {

        }

        @Override
        public void bind(ClientSession session) {

        }
    }
}