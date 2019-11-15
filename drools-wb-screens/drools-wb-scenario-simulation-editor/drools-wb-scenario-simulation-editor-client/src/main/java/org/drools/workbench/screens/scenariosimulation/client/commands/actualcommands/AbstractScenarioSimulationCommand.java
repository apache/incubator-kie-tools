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
package org.drools.workbench.screens.scenariosimulation.client.commands.actualcommands;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;

import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationViolation;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridPanel;
import org.kie.workbench.common.command.client.AbstractCommand;
import org.kie.workbench.common.command.client.CommandResult;
import org.kie.workbench.common.command.client.CommandResultBuilder;
import org.kie.workbench.common.command.client.impl.CommandResultImpl;

/**
 * <b>Abstract</b> <code>Command</code> class to provide common methods used by actual implementations
 */
public abstract class AbstractScenarioSimulationCommand extends AbstractCommand<ScenarioSimulationContext, ScenarioSimulationViolation> {

    private static final AtomicLong COUNTER_ID = new AtomicLong();

    /**
     * Auto-generated incremental identifier used  to uniquely identify each command
     */
    private final long id;


    protected AbstractScenarioSimulationCommand() {
        this.id = COUNTER_ID.getAndIncrement();
    }

    public long getId() {
        return id;
    }

    @Override
    public CommandResult<ScenarioSimulationViolation> undo(ScenarioSimulationContext context) {
        String message = this.getClass().getSimpleName() + " is not undoable";
        throw new IllegalStateException(message);
    }

    @Override
    public CommandResult<ScenarioSimulationViolation> execute(ScenarioSimulationContext context) {
        context.setStatusSimulationIfEmpty();
        try {
            internalExecute(context);
            return commonExecution(context);
        } catch (Exception e) {
            return new CommandResultImpl<>(CommandResult.Type.ERROR, Collections.singleton(new ScenarioSimulationViolation(e.getMessage())));
        }
    }



    protected abstract void internalExecute(ScenarioSimulationContext context) throws Exception;

    protected CommandResult<ScenarioSimulationViolation> commonExecution(final ScenarioSimulationContext context) {
        context.getSelectedScenarioGridPanel().ifPresent(ScenarioGridPanel::onResize);
        context.getSelectedScenarioGridPanel().ifPresent(ScenarioGridPanel::select);
        return CommandResultBuilder.SUCCESS;
    }
}
