/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.registry.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.registry.exception.RegistrySizeExceededException;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import static org.junit.Assert.*;

@RunWith( MockitoJUnitRunner.class )
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

    @Test( expected = UnsupportedOperationException.class )
    public void testRemove() {
        tested.remove( command );
    }

    @Test( expected = UnsupportedOperationException.class )
    public void testContains() {
        tested.contains( command );
    }

    @Test
    public void testRegisterCommand() {
        tested.register( command );
        Iterable<Command> result = tested.peek();
        assertNotNull( result );
        assertEquals( command, result.iterator().next() );

    }

    @Test
    public void testRegisterCommands() {
        Collection<Command> commands = new ArrayList<Command>( 2 ) {{
            add( command );
            add( command1 );
        }};
        tested.register( commands );
        Iterable<Command> result = tested.peek();
        assertNotNull( result );
        Iterator<Command> it = result.iterator();
        assertEquals( command1, it.next() );
        assertEquals( command, it.next() );

    }

    @Test
    public void testClear() {
        Collection<Command> commands = new ArrayList<Command>( 2 ) {{
            add( command );
            add( command1 );
        }};
        tested.register( commands );
        tested.clear();
        Iterable<Iterable<Command>> result = tested.getCommandHistory();
        assertNotNull( result );
        assertFalse( result.iterator().hasNext() );

    }

    @Test
    public void testPeek() {
        tested.register( command );
        Iterable<Command> result = tested.peek();
        assertNotNull( result );
        assertEquals( command, result.iterator().next() );
        Iterable<Iterable<Command>> result2 = tested.getCommandHistory();
        assertNotNull( result2 );
        assertTrue( result.iterator().hasNext() );

    }

    @Test
    public void testPop() {
        tested.register( command );
        Iterable<Command> result = tested.pop();
        assertNotNull( result );
        assertEquals( command, result.iterator().next() );
        Iterable<Iterable<Command>> result2 = tested.getCommandHistory();
        assertNotNull( result2 );
        assertFalse( result2.iterator().hasNext() );

    }

    @Test( expected = RegistrySizeExceededException.class )
    public void testAddCommandStackExceeded() {
        tested.setMaxSize( 1 );
        tested.register( command );
        tested.register( command1 );
    }

    @Test( expected = RegistrySizeExceededException.class )
    public void testAddCollectionStackExceeded() {
        Collection<Command> commands = new ArrayList<Command>( 2 ) {{
            add( command );
            add( command1 );
        }};
        tested.setMaxSize( 1 );
        tested.register( commands );
    }

    @Test
    public void testGetCommandSize() {
        tested.register( command );
        Collection<Command> commands = new ArrayList<Command>( 2 ) {{
            add( command );
            add( command1 );
        }};
        tested.register( commands );
        int size = tested.getCommandHistorySize();
        assertEquals( 2, size );

    }

    @Test
    public void testGetCommandHistory() {
        tested.register( command );
        Collection<Command> commands = new ArrayList<Command>( 2 ) {{
            add( command );
            add( command1 );
        }};
        tested.register( commands );
        Iterable<Iterable<Command>> result = tested.getCommandHistory();
        assertNotNull( result );
        Iterator<Iterable<Command>> it = result.iterator();
        Iterable<Command> r1 = it.next();
        assertNotNull( r1 );
        Iterator<Command> it1 = r1.iterator();
        Command cr1 = it1.next();
        assertEquals( command1, cr1 );
        Command cr11 = it1.next();
        assertEquals( command, cr11 );
        Iterable<Command> r2 = it.next();
        Iterator<Command> it2 = r2.iterator();
        Command cr2 = it2.next();
        assertEquals( command, cr2 );
    }

    @Test( expected = RegistrySizeExceededException.class )
    public void testStackSize() {
        tested.setMaxSize( 1 );
        tested.register( command );
        tested.register( command );

    }

}
