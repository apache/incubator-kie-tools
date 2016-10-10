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
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.batch.BatchCommandManagerListener;
import org.kie.workbench.common.stunner.core.command.batch.BatchCommandResult;
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
    private BatchCommandManagerImpl<Object, Object> batchCommandManager;

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
    private BatchCommandManagerListener<Object, Object> batchCommandManagerListener;

    @Before
    @SuppressWarnings( "unchecked" )
    public void setup() throws Exception {
        doAnswer( new Answer<CommandResult<Object>>() {
            @Override
            public CommandResult<Object> answer( InvocationOnMock invocationOnMock ) throws Throwable {
                batchCommandManagerListener.onAllow( context, command1, commandResult1 );
                return commandResult1;
            }
        } ).when( batchCommandManager ).allow( anyObject(), eq( command1 ) );
        doAnswer( new Answer<CommandResult<Object>>() {
            @Override
            public CommandResult<Object> answer( InvocationOnMock invocationOnMock ) throws Throwable {
                batchCommandManagerListener.onExecute( context, command1, commandResult1 );
                return commandResult1;
            }
        } ).when( batchCommandManager ).execute( anyObject(), eq( command1 ) );
        doAnswer( new Answer<CommandResult<Object>>() {
            @Override
            public CommandResult<Object> answer( InvocationOnMock invocationOnMock ) throws Throwable {
                batchCommandManagerListener.onUndo( context, command1, commandResult1 );
                return commandResult1;
            }
        } ).when( batchCommandManager ).undo( anyObject(), eq( command1 ) );
        when( registryFactory.newCommandRegistry() ).thenReturn( commandRegistry );
        tested = new StackCommandManagerImpl( registryFactory, batchCommandManager );
    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testAllow() {
        CommandResult<Object> result1 = tested.allow( context, command1 );
        verify( batchCommandManager, times( 1 ) ).allow( eq( context ), eq( command1 ) );
        verify( batchCommandManager, times( 0 ) ).execute( anyObject(), anyObject() );
        verify( batchCommandManager, times( 0 ) ).undo( anyObject(), anyObject() );
        verify( batchCommandManager, times( 0 ) ).executeBatch( anyObject() );
        verify( batchCommandManager, times( 0 ) ).undoBatch( anyObject() );
        verify( batchCommandManagerListener, times( 1 ) ).onAllow( eq( context ), eq( command1 ), eq( result1 ) );
        verify( batchCommandManagerListener, times( 0 ) ).onExecute( anyObject(), anyObject(), anyObject() );
        verify( batchCommandManagerListener, times( 0 ) ).onUndo( anyObject(), anyObject(), anyObject() );
        verify( batchCommandManagerListener, times( 0 ) ).onUndoBatch( anyObject(), anyObject(), anyObject() );
        verify( batchCommandManagerListener, times( 0 ) ).onExecuteBatch( anyObject(), anyObject(), anyObject() );

    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testExecute() {
        final BatchCommandManagerListener<Object, Object>[] _batchCommandManagerListeners = new BatchCommandManagerListener[ 1 ];
        doAnswer( new Answer<Void>() {
            @Override
            public Void answer( InvocationOnMock invocationOnMock ) throws Throwable {
                _batchCommandManagerListeners[ 0 ] = ( BatchCommandManagerListener ) invocationOnMock.getArguments()[ 0 ];
                return null;
            }
        } ).when( batchCommandManager ).setCommandManagerListener( any( BatchCommandManagerListener.class ) );
        doAnswer( new Answer<CommandResult<Object>>() {
            @Override
            public CommandResult<Object> answer( InvocationOnMock invocationOnMock ) throws Throwable {
                _batchCommandManagerListeners[ 0 ].onExecute( context, command1, commandResult1 );
                return commandResult1;
            }
        } ).when( batchCommandManager ).execute( anyObject(), eq( command1 ) );
        tested = new StackCommandManagerImpl<>( registryFactory, batchCommandManager );
        CommandResult<Object> result1 = tested.execute( context, command1 );
        verify( batchCommandManager, times( 1 ) ).execute( eq( context ), eq( command1 ) );
        verify( batchCommandManager, times( 0 ) ).allow( anyObject(), anyObject() );
        verify( batchCommandManager, times( 0 ) ).undo( anyObject(), anyObject() );
        verify( batchCommandManager, times( 0 ) ).executeBatch( anyObject() );
        verify( batchCommandManager, times( 0 ) ).undoBatch( anyObject() );
        verify( commandRegistry, times( 1 ) ).register( eq( command1 ) );
        verify( commandRegistry, times( 0 ) ).register( any( Collection.class ) );

    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testExecuteBatch() {
        final BatchCommandManagerListener<Object, Object>[] _batchCommandManagerListeners = new BatchCommandManagerListener[ 1 ];
        final Collection<Command<Object, Object>> batchedCommands = new ArrayList<>();
        BatchCommandResult<Object> batchCommandResult = mock( BatchCommandResult.class );
        when( batchCommandResult.getType() ).thenReturn( CommandResult.Type.INFO );
        doAnswer( new Answer<Void>() {
            @Override
            public Void answer( InvocationOnMock invocationOnMock ) throws Throwable {
                _batchCommandManagerListeners[ 0 ] = ( BatchCommandManagerListener ) invocationOnMock.getArguments()[ 0 ];
                return null;
            }
        } ).when( batchCommandManager ).setCommandManagerListener( any( BatchCommandManagerListener.class ) );
        doAnswer( new Answer<Void>() {
            @Override
            public Void answer( InvocationOnMock invocationOnMock ) throws Throwable {
                Command c = ( Command ) invocationOnMock.getArguments()[ 0 ];
                batchedCommands.add( c );
                return null;
            }
        } ).when( batchCommandManager ).batch( any( Command.class ) );
        doAnswer( new Answer<CommandResult<Object>>() {
            @Override
            public CommandResult<Object> answer( InvocationOnMock invocationOnMock ) throws Throwable {
                _batchCommandManagerListeners[ 0 ].onExecuteBatch( context, batchedCommands, batchCommandResult );
                return batchCommandResult;
            }
        } ).when( batchCommandManager ).executeBatch( anyObject() );
        doAnswer( new Answer<Collection<Command<Object, Object>>>() {
            @Override
            public Collection<Command<Object, Object>> answer( InvocationOnMock invocationOnMock ) throws Throwable {
                return batchedCommands;
            }
        } ).when( batchCommandManager ).getBatchCommands();
        tested = new StackCommandManagerImpl<>( registryFactory, batchCommandManager );
        tested.batch( command1 );
        CommandResult<Object> result1 = tested.executeBatch( context );
        verify( batchCommandManager, times( 1 ) ).executeBatch( eq( context ) );
        verify( batchCommandManager, times( 0 ) ).allow( anyObject(), anyObject() );
        verify( batchCommandManager, times( 0 ) ).undo( anyObject(), anyObject() );
        verify( batchCommandManager, times( 0 ) ).execute( eq( context ), eq( command1 ) );
        verify( batchCommandManager, times( 0 ) ).undoBatch( anyObject() );
        final ArgumentCaptor<Collection> registeredCommandsCaptor = ArgumentCaptor.forClass( Collection.class );
        verify( commandRegistry, times( 0 ) ).register( any( Command.class ) );
        verify( commandRegistry, times( 1 ) ).register( registeredCommandsCaptor.capture() );
        Collection<Command> registered = registeredCommandsCaptor.getValue();
        assertNotNull( registered );
        assertEquals( 1, registered.size() );

    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testExecuteWithErrors() {
        final BatchCommandManagerListener<Object, Object>[] _batchCommandManagerListeners = new BatchCommandManagerListener[ 1 ];
        doAnswer( new Answer<Void>() {
            @Override
            public Void answer( InvocationOnMock invocationOnMock ) throws Throwable {
                _batchCommandManagerListeners[ 0 ] = ( BatchCommandManagerListener ) invocationOnMock.getArguments()[ 0 ];
                return null;
            }
        } ).when( batchCommandManager ).setCommandManagerListener( any( BatchCommandManagerListener.class ) );
        doAnswer( new Answer<CommandResult<Object>>() {
            @Override
            public CommandResult<Object> answer( InvocationOnMock invocationOnMock ) throws Throwable {
                _batchCommandManagerListeners[ 0 ].onExecute( context, command1, commandResult1 );
                return commandResult1;
            }
        } ).when( batchCommandManager ).execute( anyObject(), eq( command1 ) );
        when( commandResult1.getType() ).thenReturn( CommandResult.Type.ERROR );
        tested = new StackCommandManagerImpl<>( registryFactory, batchCommandManager );
        CommandResult<Object> result1 = tested.execute( context, command1 );
        verify( batchCommandManager, times( 1 ) ).execute( eq( context ), eq( command1 ) );
        verify( batchCommandManager, times( 0 ) ).undo( eq( context ), eq( command1 ) );
        verify( batchCommandManager, times( 0 ) ).allow( anyObject(), anyObject() );
        verify( batchCommandManager, times( 0 ) ).executeBatch( anyObject() );
        verify( batchCommandManager, times( 0 ) ).undoBatch( anyObject() );
        verify( commandRegistry, times( 0 ) ).register( any( Command.class ) );
        verify( commandRegistry, times( 0 ) ).register( any( Collection.class ) );

    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testExecuteBatchWithErrors() {
        final BatchCommandManagerListener<Object, Object>[] _batchCommandManagerListeners = new BatchCommandManagerListener[ 1 ];
        final Collection<Command<Object, Object>> batchedCommands = new ArrayList<>();
        BatchCommandResult<Object> batchCommandResult = mock( BatchCommandResult.class );
        when( batchCommandResult.getType() ).thenReturn( CommandResult.Type.ERROR );
        doAnswer( new Answer<Void>() {
            @Override
            public Void answer( InvocationOnMock invocationOnMock ) throws Throwable {
                _batchCommandManagerListeners[ 0 ] = ( BatchCommandManagerListener ) invocationOnMock.getArguments()[ 0 ];
                return null;
            }
        } ).when( batchCommandManager ).setCommandManagerListener( any( BatchCommandManagerListener.class ) );
        doAnswer( new Answer<Void>() {
            @Override
            public Void answer( InvocationOnMock invocationOnMock ) throws Throwable {
                Command c = ( Command ) invocationOnMock.getArguments()[ 0 ];
                batchedCommands.add( c );
                return null;
            }
        } ).when( batchCommandManager ).batch( any( Command.class ) );
        doAnswer( new Answer<CommandResult<Object>>() {
            @Override
            public CommandResult<Object> answer( InvocationOnMock invocationOnMock ) throws Throwable {
                _batchCommandManagerListeners[ 0 ].onExecuteBatch( context, batchedCommands, batchCommandResult );
                return batchCommandResult;
            }
        } ).when( batchCommandManager ).executeBatch( anyObject() );
        doAnswer( new Answer<Collection<Command<Object, Object>>>() {
            @Override
            public Collection<Command<Object, Object>> answer( InvocationOnMock invocationOnMock ) throws Throwable {
                return batchedCommands;
            }
        } ).when( batchCommandManager ).getBatchCommands();
        tested = new StackCommandManagerImpl<>( registryFactory, batchCommandManager );
        tested.batch( command1 );
        CommandResult<Object> result1 = tested.executeBatch( context );
        verify( batchCommandManager, times( 1 ) ).executeBatch( eq( context ) );
        verify( batchCommandManager, times( 0 ) ).allow( anyObject(), anyObject() );
        verify( batchCommandManager, times( 0 ) ).undo( anyObject(), anyObject() );
        verify( batchCommandManager, times( 0 ) ).execute( eq( context ), eq( command1 ) );
        verify( batchCommandManager, times( 0 ) ).undoBatch( anyObject() );
        verify( commandRegistry, times( 0 ) ).register( any( Command.class ) );
        verify( commandRegistry, times( 0 ) ).register( any( Collection.class ) );

    }

}
