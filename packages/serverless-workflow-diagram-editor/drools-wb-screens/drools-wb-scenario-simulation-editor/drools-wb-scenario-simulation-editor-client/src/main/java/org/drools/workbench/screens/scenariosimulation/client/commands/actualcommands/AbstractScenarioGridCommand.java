/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
import java.util.Objects;
import java.util.Optional;

import org.drools.scenariosimulation.api.model.Background;
import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.scenariosimulation.api.model.FactMappingType;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationViolation;
import org.drools.workbench.screens.scenariosimulation.client.enums.GridWidget;
import org.drools.workbench.screens.scenariosimulation.client.factories.ScenarioCellTextAreaSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.client.factories.ScenarioHeaderTextBoxSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationBuilders;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.kie.workbench.common.command.client.CommandResult;
import org.kie.workbench.common.command.client.CommandResultBuilder;
import org.kie.workbench.common.command.client.impl.CommandResultImpl;

import static org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationUtils.getHeaderBuilder;
import static org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationUtils.getScenarioGridColumn;

public abstract class AbstractScenarioGridCommand extends AbstractScenarioSimulationUndoableCommand<ScenarioSimulationContext.Status> {

    protected GridWidget gridWidget;

    /**
     * Calling this constructor will set the target <code>GridWidget</code>
     * @param gridWidget
     */
    protected AbstractScenarioGridCommand(final GridWidget gridWidget) {
        this.gridWidget = gridWidget;
    }

    @Override
    protected ScenarioSimulationContext.Status setRestorableStatusPreExecution(ScenarioSimulationContext context) {
        return context.getStatus().cloneStatus();
    }

    @Override
    protected CommandResult<ScenarioSimulationViolation> setCurrentContext(ScenarioSimulationContext context) {
        try {
            final Simulation simulationToRestore = restorableStatus.getSimulation();
            final Background backgroundToRestore = restorableStatus.getBackground();
            if (simulationToRestore == null) {
                throw new IllegalStateException("Simulation is null in restorable status");
            }
            if (backgroundToRestore == null) {
                throw new IllegalStateException("Background is null in restorable status");
            }
            final ScenarioSimulationContext.Status originalStatus = context.getStatus().cloneStatus();
            ScenarioSimulationModel.Type type = context.getScenarioSimulationModel().getSettings().getType();
            context.getSimulationGrid().getModel().clearSelections();
            context.getBackgroundGrid().getModel().clearSelections();
            context.getSimulationGrid().setContent(simulationToRestore, type);
            context.getScenarioSimulationEditorPresenter().getModel().setSimulation(simulationToRestore);
            context.getBackgroundGrid().setContent(backgroundToRestore, type);
            context.getScenarioSimulationEditorPresenter().getModel().setBackground(backgroundToRestore);
            context.getScenarioSimulationEditorPresenter().reloadTestTools(true);
            context.setStatus(restorableStatus);
            restorableStatus = originalStatus;
            return commonExecution(context);
        } catch (Exception e) {
            return new CommandResultImpl<>(CommandResult.Type.ERROR, Collections.singleton(new ScenarioSimulationViolation(e.getMessage())));
        }
    }

    @Override
    public Optional<CommandResult<ScenarioSimulationViolation>> commonUndoRedoPreExecution(final ScenarioSimulationContext context) {
        final Optional<GridWidget> selectedGridWidgetOptional = context.getSelectedGridWidget();
        if (selectedGridWidgetOptional.isPresent() && Objects.equals(gridWidget, selectedGridWidgetOptional.get())) {
            return Optional.empty();
        }
        switch (gridWidget) {
            case SIMULATION:
                context.getScenarioSimulationEditorPresenter().selectSimulationTab();
                break;
            case BACKGROUND:
                context.getScenarioSimulationEditorPresenter().selectBackgroundTab();
                break;
            default:
                throw new IllegalStateException("Illegal GridWidget " + gridWidget);
        }
        context.getScenarioGridPanelByGridWidget(gridWidget).onResize();
        context.getScenarioGridPanelByGridWidget(gridWidget).select();
        return Optional.of(CommandResultBuilder.SUCCESS);
    }

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

        return context.getAbstractScesimGridModelByGridWidget(gridWidget).getColumns().stream()
                .filter(column -> columnTitle.equals(((ScenarioGridColumn) column).getInformationHeaderMetaData().getTitle()))
                .findFirst()
                .map(column -> ((ScenarioGridColumn) column).getFactIdentifier());
    }
}
