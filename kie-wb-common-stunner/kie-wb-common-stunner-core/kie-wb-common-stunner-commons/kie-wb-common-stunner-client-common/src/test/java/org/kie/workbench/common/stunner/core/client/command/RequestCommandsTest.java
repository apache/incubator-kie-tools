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

import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RequestCommandsTest {

    private RequestCommands tested;
    private CompositeCommand<AbstractCanvasHandler, CanvasViolation> command;
    private Command<AbstractCanvasHandler, CanvasViolation> c1;
    private Command<AbstractCanvasHandler, CanvasViolation> c3;
    private Command<AbstractCanvasHandler, CanvasViolation> c2;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        c1 = mock(Command.class);
        c3 = mock(Command.class);
        c2 = mock(Command.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSuccessfulRequest() {
        Consumer<Command<AbstractCanvasHandler, CanvasViolation>> fireRollback = mock(Consumer.class);
        tested = new RequestCommands.Builder()
                .onComplete(c -> command = (CompositeCommand<AbstractCanvasHandler, CanvasViolation>) c)
                .onRollback(fireRollback)
                .build();
        tested.start();
        tested.push(c1);
        tested.push(c2);
        tested.push(c3);
        tested.complete();
        assertNotNull(command);
        assertTrue(command.getCommands().contains(c1));
        assertTrue(command.getCommands().contains(c2));
        assertTrue(command.getCommands().contains(c3));
        verify(fireRollback, never()).accept(any());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRollback() {
        Consumer<Command<AbstractCanvasHandler, CanvasViolation>> fireComplete = mock(Consumer.class);
        tested = new RequestCommands.Builder()
                .onComplete(fireComplete)
                .onRollback(c -> command = (CompositeCommand<AbstractCanvasHandler, CanvasViolation>) c)
                .build();
        tested.start();
        tested.push(c1);
        tested.push(c2);
        tested.rollback();
        tested.push(c3);
        tested.complete();
        assertNotNull(command);
        assertTrue(command.getCommands().contains(c1));
        assertTrue(command.getCommands().contains(c2));
        assertTrue(command.getCommands().contains(c3));
        verify(fireComplete, never()).accept(any());
    }
}