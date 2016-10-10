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
import org.kie.workbench.common.stunner.core.command.CommandManager;
import org.kie.workbench.common.stunner.core.command.CommandManagerFactory;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.batch.BatchCommandManagerListener;
import org.kie.workbench.common.stunner.core.command.batch.BatchCommandResult;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class BatchCommandManagerImplTest {

    @Mock
    private CommandManagerFactory commandManagerFactory;

    @Mock
    private CommandManager<Object, Object> commandManager;

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

    private BatchCommandManagerImpl<Object, Object> tested;

    @Before
    public void setup() throws Exception {
        when( commandManagerFactory.newCommandManager() ).thenReturn( commandManager );
        tested = new BatchCommandManagerImpl<>( commandManagerFactory );
    }

    @Test
    public void testAllow() {
        when( commandManager.allow( context, command1 ) ).thenReturn( commandResult1 );
        CommandResult<Object> result1 = tested.allow( context, command1 );
        verify( commandManager, times( 1 ) ).allow( eq( context ), eq( command1 ) );
        verify( commandManager, times( 0 ) ).execute( anyObject(), anyObject() );
        verify( commandManager, times( 0 ) ).undo( anyObject(), anyObject() );
        assertNotNull( result1 );
        assertEquals( commandResult1, result1 );
    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testAllowWithListener() {
        BatchCommandManagerListener<Object, Object> listener = mock( BatchCommandManagerListener.class );
        tested.setCommandManagerListener( listener );
        testAllow();
        verify( listener, times( 1 ) ).onAllow( eq( context ), eq( command1 ), eq( commandResult1 ) );
        verify( listener, times( 0 ) ).onExecute( anyObject(), anyObject(), anyObject() );
        verify( listener, times( 0 ) ).onUndo( anyObject(), anyObject(), anyObject() );
        verify( listener, times( 0 ) ).onExecuteBatch( anyObject(), anyObject(), anyObject() );
        verify( listener, times( 0 ) ).onUndoBatch( anyObject(), anyObject(), anyObject() );

    }

    @Test
    public void testExecute() {
        when( commandManager.execute( context, command1 ) ).thenReturn( commandResult1 );
        CommandResult<Object> result1 = tested.execute( context, command1 );
        verify( commandManager, times( 0 ) ).allow( eq( context ), eq( command1 ) );
        verify( commandManager, times( 1 ) ).execute( anyObject(), anyObject() );
        verify( commandManager, times( 0 ) ).undo( anyObject(), anyObject() );
        assertNotNull( result1 );
        assertEquals( commandResult1, result1 );
    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testExecuteWithListener() {
        BatchCommandManagerListener<Object, Object> listener = mock( BatchCommandManagerListener.class );
        tested.setCommandManagerListener( listener );
        testExecute();
        verify( listener, times( 1 ) ).onExecute( eq( context ), eq( command1 ), eq( commandResult1 ) );
        verify( listener, times( 0 ) ).onAllow( anyObject(), anyObject(), anyObject() );
        verify( listener, times( 0 ) ).onUndo( anyObject(), anyObject(), anyObject() );
        verify( listener, times( 0 ) ).onExecuteBatch( anyObject(), anyObject(), anyObject() );
        verify( listener, times( 0 ) ).onUndoBatch( anyObject(), anyObject(), anyObject() );

    }

    @Test
    public void testUndo() {
        when( commandManager.undo( context, command1 ) ).thenReturn( commandResult1 );
        CommandResult<Object> result1 = tested.undo( context, command1 );
        verify( commandManager, times( 1 ) ).undo( eq( context ), eq( command1 ) );
        verify( commandManager, times( 0 ) ).execute( anyObject(), anyObject() );
        verify( commandManager, times( 0 ) ).allow( anyObject(), anyObject() );
        assertNotNull( result1 );
        assertEquals( commandResult1, result1 );
    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testUndoWithListener() {
        BatchCommandManagerListener<Object, Object> listener = mock( BatchCommandManagerListener.class );
        tested.setCommandManagerListener( listener );
        testUndo();
        verify( listener, times( 1 ) ).onUndo( eq( context ), eq( command1 ), eq( commandResult1 ) );
        verify( listener, times( 0 ) ).onAllow( anyObject(), anyObject(), anyObject() );
        verify( listener, times( 0 ) ).onExecute( anyObject(), anyObject(), anyObject() );
        verify( listener, times( 0 ) ).onExecuteBatch( anyObject(), anyObject(), anyObject() );
        verify( listener, times( 0 ) ).onUndoBatch( anyObject(), anyObject(), anyObject() );

    }

    @Test
    public void testExecuteBatch() {
        when( commandResult1.getType() ).thenReturn( CommandResult.Type.INFO );
        when( commandResult1.getMessage() ).thenReturn( "m1" );
        when( commandResult1.getViolations() ).thenReturn( new ArrayList<Object>() );
        when( commandManager.execute( context, command1 ) ).thenReturn( commandResult1 );
        when( commandResult2.getType() ).thenReturn( CommandResult.Type.INFO );
        when( commandResult2.getMessage() ).thenReturn( "m2" );
        when( commandResult2.getViolations() ).thenReturn( new ArrayList<Object>() );
        when( commandManager.execute( context, command2 ) ).thenReturn( commandResult2 );
        tested.batch( command1 );
        tested.batch( command2 );
        Collection<Command<Object, Object>> commands = tested.getBatchCommands();
        assertNotNull( commands );
        assertEquals( 2, commands.size() );
        Iterator<Command<Object, Object>> it = commands.iterator();
        assertEquals( command1, it.next() );
        assertEquals( command2, it.next() );
        BatchCommandResult<Object> result1 = tested.executeBatch( context );
        verify( commandManager, times( 1 ) ).execute( eq( context ), eq( command1 ) );
        verify( commandManager, times( 1 ) ).execute( eq( context ), eq( command2 ) );
        verify( commandManager, times( 0 ) ).allow( anyObject(), anyObject() );
        verify( commandManager, times( 0 ) ).allow( anyObject(), anyObject() );
        verify( commandManager, times( 0 ) ).undo( anyObject(), anyObject() );
        verify( commandManager, times( 0 ) ).undo( anyObject(), anyObject() );
        Collection<Command<Object, Object>> commands2 = tested.getBatchCommands();
        assertNotNull( commands2 );
        assertEquals( 0, commands2.size() );
        assertNotNull( result1 );
        assertEquals( CommandResult.Type.INFO, result1.getType() );
        assertFalse( result1.getViolations().iterator().hasNext() );
        Iterator<CommandResult<Object>> resultIt = result1.iterator();
        assertEquals( commandResult1, resultIt.next() );
        assertEquals( commandResult2, resultIt.next() );

    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testExecuteBatchWithListener() {
        BatchCommandManagerListener<Object, Object> listener = mock( BatchCommandManagerListener.class );
        tested.setCommandManagerListener( listener );
        final ArgumentCaptor<BatchCommandResult> resultCaptor = ArgumentCaptor.forClass( BatchCommandResult.class );
        testExecuteBatch();
        verify( listener, times( 1 ) ).onExecuteBatch( eq( context ), any( Collection.class ), resultCaptor.capture() );
        BatchCommandResult result = resultCaptor.getValue();
        assertEquals( CommandResult.Type.INFO, result.getType() );
        verify( listener, times( 0 ) ).onAllow( anyObject(), anyObject(), anyObject() );
        verify( listener, times( 0 ) ).onExecute( anyObject(), anyObject(), anyObject() );
        verify( listener, times( 0 ) ).onUndo( anyObject(), anyObject(), anyObject() );
        verify( listener, times( 0 ) ).onUndoBatch( anyObject(), anyObject(), anyObject() );

    }

    @Test
    public void testExecuteBatchWithErrors() {
        when( commandResult1.getType() ).thenReturn( CommandResult.Type.INFO );
        when( commandResult1.getMessage() ).thenReturn( "m1" );
        when( commandResult1.getViolations() ).thenReturn( new ArrayList<Object>() );
        when( commandManager.execute( context, command1 ) ).thenReturn( commandResult1 );
        when( commandManager.undo( context, command1 ) ).thenReturn( commandResult1 );
        when( commandResult2.getType() ).thenReturn( CommandResult.Type.ERROR );
        when( commandResult2.getMessage() ).thenReturn( "m2" );
        Object violation1 = "violation1";
        Collection<Object> violations2 = new ArrayList<Object>() {{
            add( violation1 );
        }};
        when( commandResult2.getViolations() ).thenReturn( violations2 );
        when( commandManager.execute( context, command2 ) ).thenReturn( commandResult2 );
        tested.batch( command1 );
        tested.batch( command2 );
        Collection<Command<Object, Object>> commands = tested.getBatchCommands();
        assertNotNull( commands );
        assertEquals( 2, commands.size() );
        Iterator<Command<Object, Object>> it = commands.iterator();
        assertEquals( command1, it.next() );
        assertEquals( command2, it.next() );
        BatchCommandResult<Object> result1 = tested.executeBatch( context );
        verify( commandManager, times( 1 ) ).execute( eq( context ), eq( command1 ) );
        verify( commandManager, times( 1 ) ).undo( eq( context ), eq( command1 ) );
        verify( commandManager, times( 1 ) ).execute( eq( context ), eq( command2 ) );
        verify( commandManager, times( 0 ) ).allow( anyObject(), anyObject() );
        verify( commandManager, times( 0 ) ).allow( anyObject(), anyObject() );
        verify( commandManager, times( 0 ) ).undo( eq( context ), eq( command2 ) );
        Collection<Command<Object, Object>> commands2 = tested.getBatchCommands();
        assertNotNull( commands2 );
        assertEquals( 0, commands2.size() );
        assertNotNull( result1 );
        assertEquals( CommandResult.Type.ERROR, result1.getType() );
        assertEquals( violation1, result1.getViolations().iterator().next() );
        Iterator<CommandResult<Object>> resultIt = result1.iterator();
        assertEquals( commandResult2, resultIt.next() );
        assertFalse( resultIt.hasNext() );

    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testExecuteBatchWithErrorsAndListener() {
        BatchCommandManagerListener<Object, Object> listener = mock( BatchCommandManagerListener.class );
        tested.setCommandManagerListener( listener );
        final ArgumentCaptor<BatchCommandResult> resultCaptor = ArgumentCaptor.forClass( BatchCommandResult.class );
        testExecuteBatchWithErrors();
        verify( listener, times( 1 ) ).onExecuteBatch( eq( context ), any( Collection.class ), resultCaptor.capture() );
        BatchCommandResult result = resultCaptor.getValue();
        assertEquals( CommandResult.Type.ERROR, result.getType() );
        verify( listener, times( 0 ) ).onAllow( anyObject(), anyObject(), anyObject() );
        verify( listener, times( 0 ) ).onExecute( anyObject(), anyObject(), anyObject() );
        verify( listener, times( 0 ) ).onUndo( anyObject(), anyObject(), anyObject() );
        verify( listener, times( 0 ) ).onUndoBatch( anyObject(), anyObject(), anyObject() );

    }

    @Test
    public void testUndoBatch() {
        when( commandResult1.getType() ).thenReturn( CommandResult.Type.INFO );
        when( commandResult1.getMessage() ).thenReturn( "m1" );
        when( commandResult1.getViolations() ).thenReturn( new ArrayList<Object>() );
        when( commandManager.undo( context, command1 ) ).thenReturn( commandResult1 );
        when( commandResult2.getType() ).thenReturn( CommandResult.Type.INFO );
        when( commandResult2.getMessage() ).thenReturn( "m2" );
        when( commandResult2.getViolations() ).thenReturn( new ArrayList<Object>() );
        when( commandManager.undo( context, command2 ) ).thenReturn( commandResult2 );
        tested.batch( command1 );
        tested.batch( command2 );
        Collection<Command<Object, Object>> commands = tested.getBatchCommands();
        assertNotNull( commands );
        assertEquals( 2, commands.size() );
        Iterator<Command<Object, Object>> it = commands.iterator();
        assertEquals( command1, it.next() );
        assertEquals( command2, it.next() );
        BatchCommandResult<Object> result1 = tested.undoBatch( context );
        verify( commandManager, times( 1 ) ).undo( eq( context ), eq( command1 ) );
        verify( commandManager, times( 1 ) ).undo( eq( context ), eq( command2 ) );
        verify( commandManager, times( 0 ) ).allow( anyObject(), anyObject() );
        verify( commandManager, times( 0 ) ).allow( anyObject(), anyObject() );
        verify( commandManager, times( 0 ) ).execute( anyObject(), anyObject() );
        verify( commandManager, times( 0 ) ).execute( anyObject(), anyObject() );
        Collection<Command<Object, Object>> commands2 = tested.getBatchCommands();
        assertNotNull( commands2 );
        assertEquals( 0, commands2.size() );
        assertNotNull( result1 );
        assertEquals( CommandResult.Type.INFO, result1.getType() );
        assertFalse( result1.getViolations().iterator().hasNext() );
        Iterator<CommandResult<Object>> resultIt = result1.iterator();
        assertEquals( commandResult1, resultIt.next() );
        assertEquals( commandResult2, resultIt.next() );

    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testUndoBatchWithListener() {
        BatchCommandManagerListener<Object, Object> listener = mock( BatchCommandManagerListener.class );
        tested.setCommandManagerListener( listener );
        final ArgumentCaptor<BatchCommandResult> resultCaptor = ArgumentCaptor.forClass( BatchCommandResult.class );
        testUndoBatch();
        verify( listener, times( 1 ) ).onUndoBatch( eq( context ), any( Collection.class ), resultCaptor.capture() );
        BatchCommandResult result = resultCaptor.getValue();
        assertEquals( CommandResult.Type.INFO, result.getType() );
        verify( listener, times( 0 ) ).onAllow( anyObject(), anyObject(), anyObject() );
        verify( listener, times( 0 ) ).onExecute( anyObject(), anyObject(), anyObject() );
        verify( listener, times( 0 ) ).onUndo( anyObject(), anyObject(), anyObject() );
        verify( listener, times( 0 ) ).onExecuteBatch( anyObject(), anyObject(), anyObject() );

    }

}
