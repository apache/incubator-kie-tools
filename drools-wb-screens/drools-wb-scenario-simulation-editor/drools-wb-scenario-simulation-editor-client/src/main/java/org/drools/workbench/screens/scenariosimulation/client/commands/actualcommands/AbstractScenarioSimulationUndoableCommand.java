/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands;

import java.util.Optional;

import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationViolation;
import org.kie.workbench.common.command.client.CommandResult;

/**
 * This abstract class defines a family of Commands which can be undo-able and redo-able.
 * @param <S> defines the changing object status during the command execution
 */
public abstract class AbstractScenarioSimulationUndoableCommand<S> extends AbstractScenarioSimulationCommand {

    /**
     * The <code>ScenarioSimulationContext.Status</code> to restore when calling <b>undo/redo</b>.
     * Needed only for <b>undoable</b> commands.
     */
    protected S restorableStatus = null;

    /**
     * The action to perform when an UNDO or REDO is required on this command. Typically it restores the previous status
     * and it stores the current one.
     * @param context
     * @return
     */
    protected abstract CommandResult<ScenarioSimulationViolation> setCurrentContext(ScenarioSimulationContext context);

    /**
     * It sets the status BEFORE the command is launched. Typically it clones the current status before changes are applied.
     * @param context
     * @return
     */
    protected abstract S setRestorableStatusPreExecution(ScenarioSimulationContext context);

    /**
     * Method called soon before actual <b>undo</b> and <b>redo</b> operations to preliminary execute a tab switch <b>without</b>
     * altering the call stack.
     * (eg If the command change the status of a not shown grid, this switches the tab)
     * @param context
     * @return <code>Optional&lt;CommandResult&lt;ScenarioSimulationViolation&gt;&gt;</code> of <code>CommandResultBuilder.SUCCESS</code>
     * if a tab switch happened, otherwise <code>Optional.empty()</code>
     */
    public abstract Optional<CommandResult<ScenarioSimulationViolation>> commonUndoRedoPreExecution(ScenarioSimulationContext context);

    @Override
    public CommandResult<ScenarioSimulationViolation> execute(final ScenarioSimulationContext context) {
        restorableStatus = setRestorableStatusPreExecution(context);
        return super.execute(context);
    }

    @Override
    public CommandResult<ScenarioSimulationViolation> undo(final ScenarioSimulationContext context) {
        return commonUndoRedo(context);
    }

    public CommandResult<ScenarioSimulationViolation> redo(final ScenarioSimulationContext context) {
        return commonUndoRedo(context);
    }

    private CommandResult<ScenarioSimulationViolation> commonUndoRedo(final ScenarioSimulationContext context) {
        if (restorableStatus == null) {
            String message = this.getClass().getSimpleName() + " restorableStatus status is null";
            throw new IllegalStateException(message);
        }
        return setCurrentContext(context);
    }

}
