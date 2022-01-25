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
package org.drools.workbench.screens.scenariosimulation.client.commands;

import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.Dependent;

import org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands.AbstractScenarioSimulationUndoableCommand;
import org.kie.workbench.common.command.client.CommandResult;
import org.kie.workbench.common.command.client.CommandResultBuilder;
import org.kie.workbench.common.command.client.impl.CommandResultImpl;
import org.kie.workbench.common.command.client.registry.AbstractCommandRegistryManager;

/**
 * This class is used to store <code>Queue</code>es of <b>executed/undone</b> <code>Command</code>s
 */
@Dependent
public class ScenarioCommandRegistryManager extends AbstractCommandRegistryManager<AbstractScenarioSimulationUndoableCommand> {

    /**
     * Method to register the status as it was soon before the command execution,
     * to be used for undo/redo
     * @param context
     * @param command
     */
    public void register(ScenarioSimulationContext context, AbstractScenarioSimulationUndoableCommand command) {
        doneCommandsRegistry.register(command);
        undoneCommandsRegistry.clear();
        setUndoRedoButtonStatus(context);
    }

    /**
     * Calls <b>undo</b> on the last executed <code>Command</code>
     * @param scenarioSimulationContext
     * @throws NoSuchElementException
     */
    public CommandResult<ScenarioSimulationViolation> undo(ScenarioSimulationContext scenarioSimulationContext) {
        CommandResult<ScenarioSimulationViolation> toReturn;
        if (!doneCommandsRegistry.isEmpty()) {
            commonUndoRedoPreexecution(scenarioSimulationContext, doneCommandsRegistry.peek());
            final AbstractScenarioSimulationUndoableCommand toUndo = doneCommandsRegistry.pop();
            toReturn = commonUndoRedoOperation(scenarioSimulationContext, toUndo, true);
            if (Objects.equals(CommandResultBuilder.SUCCESS, toReturn)) {
                undoneCommandsRegistry.register(toUndo);
            }
        } else {
            toReturn = new CommandResultImpl<>(CommandResult.Type.WARNING, Collections.singletonList(
                    new ScenarioSimulationViolation("No commands to undo")));
        }
        setUndoRedoButtonStatus(scenarioSimulationContext);
        return toReturn;
    }

    /**
     * Re-execute the last <b>undone</b> <code>Command</code>
     * @param scenarioSimulationContext
     * @throws NoSuchElementException
     */
    public CommandResult<ScenarioSimulationViolation> redo(ScenarioSimulationContext scenarioSimulationContext) {
        CommandResult<ScenarioSimulationViolation> toReturn;
        if (!undoneCommandsRegistry.isEmpty()) {
            commonUndoRedoPreexecution(scenarioSimulationContext, undoneCommandsRegistry.peek());
            final AbstractScenarioSimulationUndoableCommand toRedo = undoneCommandsRegistry.pop();
            toReturn = commonUndoRedoOperation(scenarioSimulationContext, toRedo, false);
            if (Objects.equals(CommandResultBuilder.SUCCESS, toReturn)) {
                doneCommandsRegistry.register(toRedo);
            }
        } else {
            toReturn = new CommandResultImpl<>(CommandResult.Type.WARNING, Collections.singletonList(
                    new ScenarioSimulationViolation("No commands to redo")));
        }
        setUndoRedoButtonStatus(scenarioSimulationContext);
        return toReturn;
    }

    /**
     * Method called soon before actual <b>undo</b> and <b>redo</b> operations to preliminary execute a tab switch <b>without</b>
     * altering the call stack.
     * If the command change the status of a not shown grid, this switches the tab and returns without removing now executing the actual command.
     * @param scenarioSimulationContext
     * @param command
     */
    protected Optional<CommandResult<ScenarioSimulationViolation>> commonUndoRedoPreexecution(final ScenarioSimulationContext scenarioSimulationContext,
                                                                                              final AbstractScenarioSimulationUndoableCommand command) {
        return command.commonUndoRedoPreExecution(scenarioSimulationContext);
    }

    /**
     * Common method called by <b>undo</b> and <b>redo</b> events
     * @param scenarioSimulationContext
     * @param command
     * @param isUndo
     * @return
     */
    protected CommandResult<ScenarioSimulationViolation> commonUndoRedoOperation(final ScenarioSimulationContext scenarioSimulationContext,
                                                                                 final AbstractScenarioSimulationUndoableCommand command,
                                                                                 final boolean isUndo) {
        CommandResult<ScenarioSimulationViolation> toReturn;
        if (isUndo) {
            toReturn = command.undo(scenarioSimulationContext);
        } else {
            toReturn = command.redo(scenarioSimulationContext);
        }
        return toReturn;
    }

    protected void setUndoRedoButtonStatus(final ScenarioSimulationContext scenarioSimulationContext) {
        scenarioSimulationContext.setUndoButtonEnabledStatus(!doneCommandsRegistry.isEmpty());
        scenarioSimulationContext.setRedoButtonEnabledStatus(!undoneCommandsRegistry.isEmpty());
    }
}
