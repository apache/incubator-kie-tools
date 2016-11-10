/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.command.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.registry.RegistryFactory;
import org.kie.workbench.common.stunner.core.registry.command.CommandRegistry;
import org.kie.workbench.common.stunner.core.registry.impl.CommandRegistryImpl;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class RedoCommandHandlerTest {

    @Mock
    RegistryFactory registryFactory;

    @Mock
    CommandRegistry commandRegistry;

    @Mock
    Command command1;

    @Mock
    Command command2;

    private final List<Command> commands1 = new ArrayList<>( 1 );
    private final List<Command> commands2 = new ArrayList<>( 1 );
    private final CommandRegistry commandRegistry1 = new CommandRegistryImpl();

    private RedoCommandHandler tested;

    @Before
    @SuppressWarnings( "unchecked" )
    public void setup() throws Exception {
        commands1.add( command1 );
        commands2.add( command2 );
        when( registryFactory.newCommandRegistry() ).thenReturn( commandRegistry );;
        this.tested = new RedoCommandHandler( registryFactory );
    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void tesUndoCommandExecuted() {
        tested.onUndoCommandExecuted( command1 );
        verify( commandRegistry, times( 1 ) ).register( eq( command1 )  );
        assertTrue( tested.isEnabled() );
    }
    @Test
    @SuppressWarnings( "unchecked" )
    public void tesExecute1() {
        createRealRegistry();
        tested.onCommandExecuted( command1 );
        assertFalse( tested.isEnabled() );
    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void tesExecuteJustRecentRedoCommand() {
        createRealRegistry();
        tested.onUndoCommandExecuted( command1 );
        tested.onCommandExecuted( command1 );
        assertFalse( tested.isEnabled() );
    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void tesExecuteRemoveRedoCommands() {
        createRealRegistry();
        Command command3 = mock( Command.class );
        tested.onUndoCommandExecuted( command1 );
        tested.onUndoCommandExecuted( command2 );
        tested.onCommandExecuted( command3 );
        assertFalse( tested.isEnabled() );
    }

    @SuppressWarnings( "unchecked" )
    private void createRealRegistry() {
        when( registryFactory.newCommandRegistry() ).thenReturn( commandRegistry1 );;
        this.tested = new RedoCommandHandler( registryFactory );
    }

}
