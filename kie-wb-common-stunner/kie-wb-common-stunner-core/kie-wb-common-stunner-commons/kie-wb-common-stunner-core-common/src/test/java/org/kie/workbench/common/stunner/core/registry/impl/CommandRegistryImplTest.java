/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.registry.impl;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.command.Command;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class CommandRegistryImplTest {

    private CommandRegistryImpl<Command> tested;

    @Mock
    private Command command;

    @Mock
    private Command command1;

    @Before
    public void setup() throws Exception {
        tested = new CommandRegistryImpl<>();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRemove() {
        tested.remove(command);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testContains() {
        tested.contains(command);
    }

    @Test
    public void testRegisterCommand() {
        tested.register(command);
        Command result = tested.peek();
        assertNotNull(result);
        assertEquals(command,
                     result);
    }

    @Test
    public void testEmpty() {
        boolean empty = tested.isEmpty();
        assertTrue(empty);
    }

    @Test
    public void testNotEmpty() {
        tested.register(command);
        boolean empty = tested.isEmpty();
        assertFalse(empty);
    }

    @Test
    public void testClear() {
        tested.register(command);
        tested.register(command1);
        tested.clear();
        List<Command> result = tested.getCommandHistory();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testPeek() {
        tested.register(command);
        Command result = tested.peek();
        assertNotNull(result);
        assertEquals(command,
                     result);
        List<Command> result2 = tested.getCommandHistory();
        assertNotNull(result2);
        assertFalse(result2.isEmpty());
    }

    @Test
    public void testPop() {
        tested.register(command);
        Command result = tested.pop();
        assertNotNull(result);
        assertEquals(command,
                     result);
        List<Command> result2 = tested.getCommandHistory();
        assertNotNull(result2);
        assertTrue(result2.isEmpty());
    }

    @Test
    public void testAddCommandStackExceeded() {
        tested.setMaxSize(1);
        tested.register(command);
        tested.register(command1);
        assertEquals(1, tested.getCommandHistory().size());
        assertEquals(command1, tested.peek());
    }

    @Test
    public void testGetCommandSize() {
        tested.register(command);
        tested.register(command1);
        int size = tested.getCommandHistory().size();
        assertEquals(2,
                     size);
    }

    @Test
    public void testGetCommandHistory() {
        tested.register(command);
        tested.register(command1);
        List<Command> result = tested.getCommandHistory();
        assertNotNull(result);
        Command r1 = result.get(0);
        assertNotNull(r1);
        assertEquals(command1,
                     r1);
        Command r2 = result.get(1);
        assertNotNull(r2);
        assertEquals(command,
                     r2);
    }

    @Test
    public void testStackSize() {
        tested.setMaxSize(1);
        tested.register(command);
        tested.register(command);
        assertEquals(1, tested.getCommandHistory().size());
    }

    @Test
    public void testStackSize2() {
        final Command commandOne = mock(Command.class);
        final Command commandTwo = mock(Command.class);
        final Command commandThree = mock(Command.class);

        tested.setMaxSize(2);

        tested.register(commandOne);
        tested.register(commandTwo);
        tested.register(commandThree);

        assertEquals(2, tested.getCommandHistory().size());
        assertTrue(tested.getCommandHistory().contains(commandTwo));
        assertTrue(tested.getCommandHistory().contains(commandThree));
    }
}
