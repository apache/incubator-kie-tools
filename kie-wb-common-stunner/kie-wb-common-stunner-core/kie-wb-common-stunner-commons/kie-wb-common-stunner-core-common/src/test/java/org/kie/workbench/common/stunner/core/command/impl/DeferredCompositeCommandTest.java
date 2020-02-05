/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.command.impl;

import java.util.stream.Stream;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder.SUCCESS;
import static org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder.failed;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DeferredCompositeCommandTest {

    @Mock
    private GraphCommandExecutionContext commandExecutionContext;

    private DeferredCompositeCommand compositeCommand;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    @SuppressWarnings("unchecked")
    public void testInitialize() {
        compositeCommand = buildCompositeCommand();
        assertFalse(compositeCommand.isInitialized());
        compositeCommand.ensureInitialized(commandExecutionContext);
        assertTrue(compositeCommand.isInitialized());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAllow() {
        compositeCommand = buildCompositeCommand();
        expectedException.expectMessage(DeferredCompositeCommand.ILLEGAL_ALLOW_MESSAGE);
        compositeCommand.allow(commandExecutionContext);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecuteSuccessful() {
        //c1 command succeeded
        Command c1 = mockCommand(SUCCESS,
                                 SUCCESS);
        //c2 command succeeded
        Command c2 = mockCommand(SUCCESS,
                                 SUCCESS);
        //c3 command succeed
        Command c3 = mockCommand(SUCCESS,
                                 SUCCESS);
        //c4 command succeeded
        Command c4 = mockCommand(SUCCESS,
                                 SUCCESS);

        compositeCommand = buildCompositeCommand(c1,
                                                 c2,
                                                 c3,
                                                 c4);

        compositeCommand.execute(commandExecutionContext);

        //c1, c2, c3, c4, must have been allowed and executed
        verifyAllowedAndExecuted(c1);
        verifyAllowedAndExecuted(c2);
        verifyAllowedAndExecuted(c3);
        verifyAllowedAndExecuted(c4);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecuteWithAllowFailure() {
        //c1 command succeeded
        Command c1 = mockCommand(SUCCESS,
                                 SUCCESS);
        //c2 command allowance check failed
        Command c2 = mockCommand(failed(),
                                 SUCCESS);
        //no matter
        Command c3 = mockCommand(SUCCESS,
                                 SUCCESS);
        //no matter
        Command c4 = mockCommand(SUCCESS,
                                 SUCCESS);

        compositeCommand = buildCompositeCommand(c1,
                                                 c2,
                                                 c3,
                                                 c4);

        compositeCommand.execute(commandExecutionContext);

        //c1 must have been allowed, executed and undone
        verifyAllowedExecutedAndUnDone(c1);

        //c2 must have been allowed but not executed nor undone
        verify(c2).allow(commandExecutionContext);
        verify(c2,
               never()).execute(commandExecutionContext);
        verify(c2,
               never()).undo(commandExecutionContext);

        verifyNeverUsed(c3);
        verifyNeverUsed(c4);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecuteWithExecutionFailure() {
        //c1 command succeeded
        Command c1 = mockCommand(SUCCESS,
                                 SUCCESS);
        //c2 command allowance succeeded but execution failed
        Command c2 = mockCommand(SUCCESS,
                                 failed());
        //no matter
        Command c3 = mockCommand(SUCCESS,
                                 SUCCESS);
        //no matter
        Command c4 = mockCommand(SUCCESS,
                                 SUCCESS);

        compositeCommand = buildCompositeCommand(c1,
                                                 c2,
                                                 c3,
                                                 c4);
        compositeCommand.execute(commandExecutionContext);

        //c1 must have been allowed, executed and undone
        verifyAllowedExecutedAndUnDone(c1);

        //c2 must have been allowed, executed and undone
        verifyAllowedAndExecuted(c2);

        verifyNeverUsed(c3);
        verifyNeverUsed(c4);
    }

    @SuppressWarnings("unchecked")
    private DeferredCompositeCommand buildCompositeCommand(Command... commands) {
        DeferredCompositeCommand.Builder builder = new DeferredCompositeCommand.Builder();
        Stream.of(commands).forEach(command -> builder.deferCommand(() -> command));
        return builder.build();
    }

    @SuppressWarnings("unchecked")
    private Command mockCommand(CommandResult<RuleViolation> allowResult,
                                CommandResult<RuleViolation> executeResult) {
        Command command = mock(Command.class);
        when(command.allow(commandExecutionContext)).thenReturn(allowResult);
        when(command.execute(commandExecutionContext)).thenReturn(executeResult);
        when(command.undo(commandExecutionContext)).thenReturn(GraphCommandResultBuilder.SUCCESS);
        return command;
    }

    @SuppressWarnings("unchecked")
    private void verifyAllowedExecutedAndUnDone(Command command) {
        verify(command).allow(commandExecutionContext);
        verify(command).execute(commandExecutionContext);
        verify(command).undo(commandExecutionContext);
    }

    @SuppressWarnings("unchecked")
    private void verifyAllowedAndExecuted(Command command) {
        verify(command).allow(commandExecutionContext);
        verify(command).execute(commandExecutionContext);
    }

    @SuppressWarnings("unchecked")
    private void verifyNeverUsed(Command command) {
        verify(command,
               never()).allow(commandExecutionContext);
        verify(command,
               never()).execute(commandExecutionContext);
        verify(command,
               never()).undo(commandExecutionContext);
    }
}
