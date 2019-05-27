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
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.scenariosimulation.api.model.FactMappingType;
import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationViolation;
import org.drools.workbench.screens.scenariosimulation.client.factories.ScenarioCellTextAreaSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.client.factories.ScenarioHeaderTextBoxSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationBuilders;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.kie.workbench.common.command.client.AbstractCommand;
import org.kie.workbench.common.command.client.CommandResult;
import org.kie.workbench.common.command.client.CommandResultBuilder;
import org.kie.workbench.common.command.client.impl.CommandResultImpl;

import static org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationUtils.getHeaderBuilder;
import static org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationUtils.getScenarioGridColumn;

/**
 * <b>Abstract</b> <code>Command</code> class to provide common methods used by actual implementations
 */
public abstract class AbstractScenarioSimulationCommand extends AbstractCommand<ScenarioSimulationContext, ScenarioSimulationViolation> {

    private static final AtomicLong COUNTER_ID = new AtomicLong();

    /**
     * Auto-generated incremental identifier used  to uniquely identify each command
     */
    private final long id;

    /**
     * Flag that indicates if the command is <b>undoable</b>. Default is <code>false</code>
     */
    private final boolean undoable;

    /**
     * The <code>ScenarioSimulationContext.Status</code> to restore when calling <b>undo/redo</b>.
     * Needed only for <b>undoable</b> commands.
     */
    protected ScenarioSimulationContext.Status restorableStatus = null;

    /**
     * Calling this constructor will set the command as <b>undoable</b>
     * @param undoable
     */
    protected AbstractScenarioSimulationCommand(final boolean undoable) {
        this.id = COUNTER_ID.getAndIncrement();
        this.undoable = undoable;
    }

    public long getId() {
        return id;
    }

    public boolean isUndoable() {
        return undoable;
    }

    @Override
    public CommandResult<ScenarioSimulationViolation> undo(ScenarioSimulationContext context) throws UnsupportedOperationException {
        if (!undoable || restorableStatus == null) {
            String message = !undoable ? this.getClass().getSimpleName() + " is not undoable" : "restorableStatus status is null";
            throw new UnsupportedOperationException(message);
        }
        return setCurrentContext(context);
    }

    public CommandResult<ScenarioSimulationViolation> redo(ScenarioSimulationContext context) throws UnsupportedOperationException {
        if (!undoable || restorableStatus == null) {
            String message = !undoable ? this.getClass().getSimpleName() + " is not redoable" : "restorableStatus status is null";
            throw new UnsupportedOperationException(message);
        }
        return setCurrentContext(context);
    }

    @Override
    public CommandResult<ScenarioSimulationViolation> execute(ScenarioSimulationContext context) {
        context.setStatusSimulationIfEmpty();
        if (undoable) {
            restorableStatus = context.getStatus().cloneStatus();
        }
        try {
            internalExecute(context);
            return commonExecution(context);
        } catch (Exception e) {
            return new CommandResultImpl<>(CommandResult.Type.ERROR, Collections.singleton(new ScenarioSimulationViolation(e.getMessage())));
        }
    }

    protected CommandResult<ScenarioSimulationViolation> setCurrentContext(ScenarioSimulationContext context) {
        try {
            final Simulation toRestore = restorableStatus.getSimulation();
            if (toRestore != null) {
                final ScenarioSimulationContext.Status originalStatus = context.getStatus().cloneStatus();
                context.getModel().clearSelections();
                context.getScenarioSimulationEditorPresenter().getView().setContent(toRestore);
                context.getScenarioSimulationEditorPresenter().getModel().setSimulation(toRestore);
                context.getScenarioSimulationEditorPresenter().reloadTestTools(true);
                context.setStatus(restorableStatus);
                restorableStatus = originalStatus;
                return commonExecution(context);
            } else {
                return new CommandResultImpl<>(CommandResult.Type.ERROR, Collections.singletonList(new ScenarioSimulationViolation("Simulation not set inside Model")));
            }
        } catch (Exception e) {
            return new CommandResultImpl<>(CommandResult.Type.ERROR, Collections.singleton(new ScenarioSimulationViolation(e.getMessage())));
        }
    }

    protected abstract void internalExecute(ScenarioSimulationContext context) throws Exception;

    /**
     * Returns a <code>ScenarioGridColumn</code> with the following default values:
     * <p>
     * width: 150
     * </p>
     * <p>
     * isMovable: <code>false</code>;
     * </p>
     * <p>
     * isPropertyAssigned: <code>false</code>;
     * </p>
     * <p>
     * columnRenderer: new ScenarioGridColumnRenderer()
     * </p>
     * @param instanceTitle
     * @param propertyTitle
     * @param columnId
     * @param columnGroup
     * @param factMappingType
     * @param factoryHeader
     * @param factoryCell
     * @param placeHolder
     * @return
     */
    protected ScenarioGridColumn getScenarioGridColumnLocal(String instanceTitle,
                                                            String propertyTitle,
                                                            String columnId,
                                                            String columnGroup,
                                                            FactMappingType factMappingType,
                                                            ScenarioHeaderTextBoxSingletonDOMElementFactory factoryHeader,
                                                            ScenarioCellTextAreaSingletonDOMElementFactory factoryCell,
                                                            String placeHolder) {
        ScenarioSimulationBuilders.HeaderBuilder headerBuilder = getHeaderBuilder(instanceTitle, propertyTitle, columnId, columnGroup, factMappingType, factoryHeader);
        return getScenarioGridColumn(headerBuilder, factoryCell, placeHolder);
    }

    protected Optional<FactIdentifier> getFactIdentifierByColumnTitle(String columnTitle, ScenarioSimulationContext context) {

        return context.getScenarioGridLayer().getScenarioGrid().getModel().getColumns().stream()
                .filter(column -> columnTitle.equals(((ScenarioGridColumn) column).getInformationHeaderMetaData().getTitle()))
                .findFirst()
                .map(column -> ((ScenarioGridColumn) column).getFactIdentifier());
    }

    protected CommandResult<ScenarioSimulationViolation> commonExecution(final ScenarioSimulationContext context) {
        context.getScenarioGridPanel().onResize();
        context.getScenarioGridPanel().select();
        return CommandResultBuilder.SUCCESS;
    }
}
