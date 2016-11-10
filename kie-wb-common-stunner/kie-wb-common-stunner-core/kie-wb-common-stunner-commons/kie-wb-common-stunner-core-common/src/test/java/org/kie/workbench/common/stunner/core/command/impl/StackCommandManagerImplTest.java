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

package org.kie.workbench.common.stunner.core.command.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandManagerListener;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.registry.RegistryFactory;
import org.kie.workbench.common.stunner.core.registry.command.CommandRegistry;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class StackCommandManagerImplTest {

    @Mock
    private CommandRegistry<Command> commandRegistry;

    @Mock
    private RegistryFactory registryFactory;

    @Mock
    private CommandManagerImpl<Object, Object> commandManager;

    @Mock
    private Object context;

    @Mock
    private Command<Object, Object> command1;

    @Mock
    private Command<Object, Object> command2;

    @Mock
    private CommandResult<Object> commandResult1;

    @Mock
    private CommandResult<Object> commandResult2;

    private StackCommandManagerImpl<Object, Object> tested;

    @Mock
    private CommandManagerListener<Object, Object> commandManagerListener;

    @Before
    @SuppressWarnings( "unchecked" )
    public void setup() throws Exception {
        doAnswer( new Answer<CommandResult<Object>>() {
            @Override
            public CommandResult<Object> answer( InvocationOnMock invocationOnMock ) throws Throwable {
                commandManagerListener.onAllow( context, command1, commandResult1 );
                return commandResult1;
            }
        } ).when( commandManager ).allow( anyObject(), eq( command1 ) );
        doAnswer( new Answer<CommandResult<Object>>() {
            @Override
            public CommandResult<Object> answer( InvocationOnMock invocationOnMock ) throws Throwable {
                commandManagerListener.onExecute( context, command1, commandResult1 );
                return commandResult1;
            }
        } ).when( commandManager ).execute( anyObject(), eq( command1 ) );
        doAnswer( new Answer<CommandResult<Object>>() {
            @Override
            public CommandResult<Object> answer( InvocationOnMock invocationOnMock ) throws Throwable {
                commandManagerListener.onUndo( context, command1, commandResult1 );
                return commandResult1;
            }
        } ).when( commandManager ).undo( anyObject(), eq( command1 ) );
        when( registryFactory.newCommandRegistry() ).thenReturn( commandRegistry );
        tested = new StackCommandManagerImpl( registryFactory, commandManager );
    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testAllow() {
        CommandResult<Object> result1 = tested.allow( context, command1 );
        verify( commandManager, times( 1 ) ).allow( eq( context ), eq( command1 ) );
        verify( commandManager, times( 0 ) ).execute( anyObject(), anyObject() );
        verify( commandManager, times( 0 ) ).undo( anyObject(), anyObject() );
        verify( commandManagerListener, times( 1 ) ).onAllow( eq( context ), eq( command1 ), eq( result1 ) );
        verify( commandManagerListener, times( 0 ) ).onExecute( anyObject(), anyObject(), anyObject() );
        verify( commandManagerListener, times( 0 ) ).onUndo( anyObject(), anyObject(), anyObject() );

    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testExecute() {
        final CommandManagerListener<Object, Object>[] _batchCommandManagerListeners = new CommandManagerListener[ 1 ];
        doAnswer( new Answer<Void>() {
            @Override
            public Void answer( InvocationOnMock invocationOnMock ) throws Throwable {
                _batchCommandManagerListeners[ 0 ] = ( CommandManagerListener ) invocationOnMock.getArguments()[ 0 ];
                return null;
            }
        } ).when( commandManager ).setCommandManagerListener( any( CommandManagerListener.class ) );
        doAnswer( new Answer<CommandResult<Object>>() {
            @Override
            public CommandResult<Object> answer( InvocationOnMock invocationOnMock ) throws Throwable {
                _batchCommandManagerListeners[ 0 ].onExecute( context, command1, commandResult1 );
                return commandResult1;
            }
        } ).when( commandManager ).execute( anyObject(), eq( command1 ) );
        tested = new StackCommandManagerImpl<>( registryFactory, commandManager );
        CommandResult<Object> result1 = tested.execute( context, command1 );
        verify( commandManager, times( 1 ) ).execute( eq( context ), eq( command1 ) );
        verify( commandManager, times( 0 ) ).allow( anyObject(), anyObject() );
        verify( commandManager, times( 0 ) ).undo( anyObject(), anyObject() );
        verify( commandRegistry, times( 1 ) ).register( eq( command1 ) );
    }


    @Test
    @SuppressWarnings( "unchecked" )
    public void testExecuteWithErrors() {
        final CommandManagerListener<Object, Object>[] _batchCommandManagerListeners = new CommandManagerListener[ 1 ];
        doAnswer( new Answer<Void>() {
            @Override
            public Void answer( InvocationOnMock invocationOnMock ) throws Throwable {
                _batchCommandManagerListeners[ 0 ] = ( CommandManagerListener ) invocationOnMock.getArguments()[ 0 ];
                return null;
            }
        } ).when( commandManager ).setCommandManagerListener( any( CommandManagerListener.class ) );
        doAnswer( new Answer<CommandResult<Object>>() {
            @Override
            public CommandResult<Object> answer( InvocationOnMock invocationOnMock ) throws Throwable {
                _batchCommandManagerListeners[ 0 ].onExecute( context, command1, commandResult1 );
                return commandResult1;
            }
        } ).when( commandManager ).execute( anyObject(), eq( command1 ) );
        when( commandResult1.getType() ).thenReturn( CommandResult.Type.ERROR );
        tested = new StackCommandManagerImpl<>( registryFactory, commandManager );
        CommandResult<Object> result1 = tested.execute( context, command1 );
        verify( commandManager, times( 1 ) ).execute( eq( context ), eq( command1 ) );
        verify( commandManager, times( 0 ) ).undo( eq( context ), eq( command1 ) );
        verify( commandManager, times( 0 ) ).allow( anyObject(), anyObject() );
        verify( commandRegistry, times( 0 ) ).register( any( Command.class ) );

    }

}
