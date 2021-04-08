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
package org.drools.workbench.screens.scenariosimulation.kogito.client.dmn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;

import javax.inject.Inject;

import org.drools.scenariosimulation.api.model.AbstractScesimData;
import org.drools.scenariosimulation.api.model.AbstractScesimModel;
import org.drools.scenariosimulation.api.model.Background;
import org.drools.scenariosimulation.api.model.BackgroundData;
import org.drools.scenariosimulation.api.model.BackgroundDataWithIndex;
import org.drools.scenariosimulation.api.model.ExpressionIdentifier;
import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.scenariosimulation.api.model.FactMapping;
import org.drools.scenariosimulation.api.model.FactMappingType;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.ScenarioWithIndex;
import org.drools.scenariosimulation.api.model.ScesimDataWithIndex;
import org.drools.scenariosimulation.api.model.ScesimModelDescriptor;
import org.drools.scenariosimulation.api.model.Settings;
import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.scenariosimulation.api.utils.ScenarioSimulationSharedUtils;
import org.drools.workbench.screens.scenariosimulation.kogito.client.services.ScenarioSimulationKogitoDMNMarshallerService;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTree;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTuple;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDefinitions;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.callbacks.Callback;

import static org.drools.scenariosimulation.api.model.FactMappingType.EXPECT;
import static org.drools.scenariosimulation.api.model.FactMappingType.GIVEN;
import static org.drools.scenariosimulation.api.utils.ConstantsHolder.VALUE;

/**
 * Class used to populate <code>ScenarioSimulationModel</code> inside <code>Callback</code>s
 * <p>
 * Mostly copied from <code>org.drools.workbench.screens.scenariosimulation.backend.server.util.SimulationSettingsCreationStrategy</code> and its children.
 * <p>
 * Implemented to manage the nested callbacks for <b>DMN</b>
 */
public class KogitoScenarioSimulationBuilder {

    @Inject
    private ScenarioSimulationKogitoDMNDataManager dmnDataManager;
    @Inject
    private ScenarioSimulationKogitoDMNMarshallerService dmnMarshallerService;

    private static FactMappingType convert(final FactModelTree.Type modelTreeType) {
        switch (modelTreeType) {
            case INPUT:
                return GIVEN;
            case DECISION:
                return EXPECT;
            default:
                throw new IllegalArgumentException("Impossible to map");
        }
    }

    public void populateScenarioSimulationModelRULE(final String dmoSession,
                                                    final Callback<ScenarioSimulationModel> populateEditorCommand) {
        ScenarioSimulationModel model = new ScenarioSimulationModel();
        model.setSimulation(createRULESimulation());
        model.setBackground(createBackground());
        model.setSettings(createRULESettings(dmoSession));
        populateEditorCommand.callback(model);
    }

    public void populateScenarioSimulationModelDMN(final String dmnFilePath,
                                                   final Callback<ScenarioSimulationModel> populateEditorCommand,
                                                   final ErrorCallback<Object> dmnContentErrorCallback) {
        ScenarioSimulationModel model = new ScenarioSimulationModel();
        model.setBackground(createBackground());
        populateDMNSimulationAndSettings(model, dmnFilePath, populateEditorCommand, dmnContentErrorCallback);
    }

    protected Simulation createRULESimulation() {
        Simulation toReturn = createSimulation();
        ScenarioWithIndex scenarioWithIndex = createScesimDataWithIndex(toReturn, ScenarioWithIndex::new);

        // Add GIVEN Fact
        createEmptyColumn(toReturn.getScesimModelDescriptor(),
                          scenarioWithIndex,
                          1,
                          GIVEN,
                          toReturn.getScesimModelDescriptor().getFactMappings().size());

        // Add EXPECT Fact
        createEmptyColumn(toReturn.getScesimModelDescriptor(),
                          scenarioWithIndex,
                          2,
                          EXPECT,
                          toReturn.getScesimModelDescriptor().getFactMappings().size());
        return toReturn;
    }

    private void populateDMNSimulationAndSettings(final ScenarioSimulationModel toPopulate,
                                                  final String dmnFilePath,
                                                  final Callback<ScenarioSimulationModel> populateEditorCommand,
                                                  final ErrorCallback<Object> dmnContentErrorCallback) {
        String dmnFileName = dmnFilePath.substring(dmnFilePath.lastIndexOf('/') + 1);
        final Path dmnPath = PathFactory.newPath(dmnFileName, dmnFilePath);
        dmnMarshallerService.getDMNContent(dmnPath,
                                           getDMNContentCallback(toPopulate, populateEditorCommand, dmnPath),
                                           dmnContentErrorCallback);
    }

    private Callback<JSITDefinitions> getDMNContentCallback(final ScenarioSimulationModel toPopulate,
                                                            final Callback<ScenarioSimulationModel> populateEditorCommand,
                                                            final Path dmnPath) {
        return jsitDefinitions -> {
            final FactModelTuple factModelTuple = dmnDataManager.getFactModelTuple(jsitDefinitions);
            toPopulate.setSimulation(createDMNSimulation(factModelTuple));
            toPopulate.setSettings(createDMNSettings(jsitDefinitions.getName(),
                                                     jsitDefinitions.getNamespace(),
                                                     dmnPath.toURI()));
            populateEditorCommand.callback(toPopulate);
        };
    }

    protected Background createBackground() {
        Background toReturn = new Background();
        ScesimModelDescriptor simulationDescriptor = toReturn.getScesimModelDescriptor();
        int index = toReturn.getUnmodifiableData().size() + 1;
        BackgroundData backgroundData = toReturn.addData();
        BackgroundDataWithIndex backgroundDataWithIndex = new BackgroundDataWithIndex(index, backgroundData);

        // Add GIVEN Fact
        createEmptyColumn(simulationDescriptor,
                          backgroundDataWithIndex,
                          1,
                          GIVEN,
                          simulationDescriptor.getFactMappings().size());
        return toReturn;
    }
    protected Settings createRULESettings(final String dmoSession) {
        Settings toReturn = new Settings();
        toReturn.setType(ScenarioSimulationModel.Type.RULE);
        toReturn.setDmoSession(dmoSession);
        return toReturn;
    }

    protected Settings createDMNSettings(final String name, final String nameSpace, final String dmnFilePath) {
        Settings toReturn = new Settings();
        toReturn.setType(ScenarioSimulationModel.Type.DMN);
        toReturn.setDmnFilePath(dmnFilePath);
        toReturn.setDmnName(name);
        toReturn.setDmnNamespace(nameSpace);
        return toReturn;
    }

    private <T extends AbstractScesimData, E extends ScesimDataWithIndex<T>> E createScesimDataWithIndex(final AbstractScesimModel<T> abstractScesimModel,
                                                                                                         final BiFunction<Integer, T, E> producer) {
        T scenario = abstractScesimModel.addData();
        scenario.setDescription(null);
        int index = abstractScesimModel.getUnmodifiableData().indexOf(scenario) + 1;
        return producer.apply(index, scenario);
    }

    /**
     * Create an empty column using factMappingType defined. The new column will be added as last column of
     * the group (GIVEN/EXPECT) (see findLastIndexOfGroup)
     * @param simulationDescriptor
     * @param scesimDataWithIndex
     * @param placeholderId
     * @param factMappingType
     * @param columnIndex
     */
    private void createEmptyColumn(final ScesimModelDescriptor simulationDescriptor,
                                   final ScesimDataWithIndex scesimDataWithIndex,
                                   final int placeholderId,
                                   final FactMappingType factMappingType,
                                   final int columnIndex) {
        int row = scesimDataWithIndex.getIndex();
        final ExpressionIdentifier expressionIdentifier = ExpressionIdentifier.create(row + "|" + placeholderId, factMappingType);

        final FactMapping factMapping = simulationDescriptor
                .addFactMapping(
                        columnIndex,
                        FactMapping.getInstancePlaceHolder(placeholderId),
                        FactIdentifier.EMPTY,
                        expressionIdentifier);
        factMapping.setColumnWidth(getColumnWidth(expressionIdentifier.getName()));
        factMapping.setExpressionAlias(FactMapping.getPropertyPlaceHolder(placeholderId));
        scesimDataWithIndex.getScesimData().addMappingValue(FactIdentifier.EMPTY, expressionIdentifier, null);
    }

    /**
     * If DMN model is empty, contains only inputs or only outputs this method add one GIVEN and/or EXPECT empty column
     * @param simulation
     * @param scenarioWithIndex
     */
    private void addEmptyColumnsIfNeeded(final Simulation simulation, final ScenarioWithIndex scenarioWithIndex) {
        boolean hasGiven = false;
        boolean hasExpect = false;
        ScesimModelDescriptor simulationDescriptor = simulation.getScesimModelDescriptor();
        for (FactMapping factMapping : simulationDescriptor.getFactMappings()) {
            FactMappingType factMappingType = factMapping.getExpressionIdentifier().getType();
            if (!hasGiven && GIVEN.equals(factMappingType)) {
                hasGiven = true;
            } else if (!hasExpect && EXPECT.equals(factMappingType)) {
                hasExpect = true;
            }
        }
        if (!hasGiven) {
            createEmptyColumn(simulationDescriptor,
                              scenarioWithIndex,
                              1,
                              GIVEN,
                              findNewIndexOfGroup(simulationDescriptor, GIVEN));
        }
        if (!hasExpect) {
            createEmptyColumn(simulationDescriptor,
                              scenarioWithIndex,
                              2,
                              EXPECT,
                              findNewIndexOfGroup(simulationDescriptor, EXPECT));
        }
    }

    private int findNewIndexOfGroup(final ScesimModelDescriptor simulationDescriptor, final FactMappingType factMappingType) {
        List<FactMapping> factMappings = simulationDescriptor.getFactMappings();
        if (GIVEN.equals(factMappingType)) {
            for (int i = 0; i < factMappings.size(); i += 1) {
                if (EXPECT.equals(factMappings.get(i).getExpressionIdentifier().getType())) {
                    return i;
                }
            }
            return factMappings.size();
        } else if (EXPECT.equals(factMappingType)) {
            return factMappings.size();
        } else {
            throw new IllegalArgumentException("This method can be invoked only with GIVEN or EXPECT as FactMappingType");
        }
    }

    protected Simulation createDMNSimulation(final FactModelTuple factModelTuple) {
        Simulation toReturn = createSimulation();
        ScenarioWithIndex scenarioWithIndex = createScesimDataWithIndex(toReturn, ScenarioWithIndex::new);

        AtomicInteger id = new AtomicInteger(1);
        final Collection<FactModelTree> visibleFactTrees = factModelTuple.getVisibleFacts().values();
        final Map<String, FactModelTree> hiddenValues = factModelTuple.getHiddenFacts();

        visibleFactTrees.stream().sorted((a, b) -> {
            FactModelTree.Type aType = a.getType();
            FactModelTree.Type bType = b.getType();
            int inputFirstOrder = FactModelTree.Type.INPUT.equals(aType) ? -1 : 1;
            return aType.equals(bType) ? 0 : inputFirstOrder;
        }).forEach(factModelTree -> {
            FactIdentifier factIdentifier = FactIdentifier.create(factModelTree.getFactName(), factModelTree.getFactName());
            FactMappingExtractor factMappingExtractor = new FactMappingExtractor(factIdentifier,
                                                                                 scenarioWithIndex.getIndex(),
                                                                                 id,
                                                                                 convert(factModelTree.getType()),
                                                                                 toReturn.getScesimModelDescriptor(),
                                                                                 scenarioWithIndex.getScesimData());
            addFactMapping(factMappingExtractor, factModelTree, new ArrayList<>(), hiddenValues);
        });

        addEmptyColumnsIfNeeded(toReturn, scenarioWithIndex);
        return toReturn;
    }

    protected Simulation createSimulation() {
        Simulation simulation = new Simulation();
        ScesimModelDescriptor simulationDescriptor = simulation.getScesimModelDescriptor();
        FactMapping indexFactMapping = simulationDescriptor.addFactMapping(FactIdentifier.INDEX.getName(), FactIdentifier.INDEX, ExpressionIdentifier.INDEX);
        indexFactMapping.setColumnWidth(getColumnWidth(ExpressionIdentifier.INDEX.getName()));
        FactMapping descriptionFactMapping = simulationDescriptor.addFactMapping(FactIdentifier.DESCRIPTION.getName(), FactIdentifier.DESCRIPTION, ExpressionIdentifier.DESCRIPTION);
        descriptionFactMapping.setColumnWidth(getColumnWidth(ExpressionIdentifier.DESCRIPTION.getName()));
        return simulation;
    }

    /**
     * It defines the column width of a new column, based on its
     * <code>FactMapping.expressionIdentifier.getName()</code> field
     * @param expressionIdentifierName
     * @return
     */
    protected static double getColumnWidth(String expressionIdentifierName) {
        ExpressionIdentifier.NAME expressionName;
        try {
            expressionName = ExpressionIdentifier.NAME.valueOf(expressionIdentifierName);
        } catch (IllegalArgumentException e) {
            expressionName = ExpressionIdentifier.NAME.Other;
        }
        switch (expressionName) {
            case Index:
                return 70;
            case Description:
                return 300;
            default:
                return 114;
        }
    }

    protected void addFactMapping(final FactMappingExtractor factMappingExtractor,
                                  final FactModelTree factModelTree,
                                  final List<String> previousSteps,
                                  final Map<String, FactModelTree> hiddenValues) {
        internalAddToScenario(factMappingExtractor,
                              factModelTree,
                              previousSteps,
                              hiddenValues,
                              new HashSet<>());
    }

    private void internalAddToScenario(final FactMappingExtractor factMappingExtractor,
                                       final FactModelTree factModelTree,
                                       final List<String> readOnlyPreviousSteps,
                                       final Map<String, FactModelTree> hiddenValues,
                                       final Set<String> alreadyVisited) {

        List<String> previousSteps = new ArrayList<>(readOnlyPreviousSteps);
        // if is a simple type it generates a single column
        if (factModelTree.isSimple()) {
            FactModelTree.PropertyTypeName factType = factModelTree.getSimpleProperties().get(VALUE);
            factMappingExtractor.getFactMapping(factModelTree, VALUE, previousSteps, factType.getTypeName());
        }
        // otherwise it adds a column for each simple properties direct or nested
        else {
            factModelTree.getSimpleProperties().entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> {
                String factName = entry.getKey();
                String factTypeName = entry.getValue().getTypeName();

                FactMapping factMapping = factMappingExtractor.getFactMapping(factModelTree, factName, previousSteps, factTypeName);
                if (ScenarioSimulationSharedUtils.isList(factTypeName)) {
                    factMapping.setGenericTypes(factModelTree.getGenericTypeInfo(factName));
                }
                factMapping.addExpressionElement(factName, factTypeName);
           });

            factModelTree.getExpandableProperties().entrySet().stream().sorted(Map.Entry.comparingByValue()).forEach(entry -> {
                String factType = entry.getValue();
                FactModelTree nestedModelTree = hiddenValues.get(factType);

                if (previousSteps.isEmpty()) {
                    previousSteps.add(factModelTree.getFactName());
                }

                ArrayList<String> currentSteps = new ArrayList<>(previousSteps);
                currentSteps.add(entry.getKey());

                if (!alreadyVisited.contains(nestedModelTree.getFactName())) {
                    alreadyVisited.add(factModelTree.getFactName());
                    internalAddToScenario(factMappingExtractor, nestedModelTree, currentSteps, hiddenValues, alreadyVisited);
                }
            });
        }
    }

    protected static class FactMappingExtractor {

        private final FactIdentifier factIdentifier;
        private final int row;
        private final AtomicInteger id;
        private final FactMappingType type;
        private final ScesimModelDescriptor simulationDescriptor;
        private final AbstractScesimData abstractScesimData;

        public FactMappingExtractor(final FactIdentifier factIdentifier,
                                    final int row,
                                    final AtomicInteger id,
                                    final FactMappingType type,
                                    final ScesimModelDescriptor simulationDescriptor,
                                    final AbstractScesimData abstractScesimData) {
            this.factIdentifier = factIdentifier;
            this.row = row;
            this.id = id;
            this.type = type;
            this.simulationDescriptor = simulationDescriptor;
            this.abstractScesimData = abstractScesimData;
        }

        public FactMapping getFactMapping(final FactModelTree factModelTree,
                                          final String propertyName,
                                          final List<String> previousSteps,
                                          final String factType) {

            String factAlias = !previousSteps.isEmpty() ? previousSteps.get(0) : factModelTree.getFactName();

            ExpressionIdentifier expressionIdentifier = ExpressionIdentifier.create(row + "|" + id.getAndIncrement(), type);
            final FactMapping factMapping = simulationDescriptor.addFactMapping(factAlias, factIdentifier, expressionIdentifier);

            List<String> localPreviousStep = new ArrayList<>(previousSteps);
            localPreviousStep.add(propertyName);
            String expressionAlias = String.join(".",
                                                 localPreviousStep.size() > 1 ?
                                                         localPreviousStep.subList(1, localPreviousStep.size()) :
                                                         localPreviousStep);
            factMapping.setExpressionAlias(expressionAlias);
            factMapping.setGenericTypes(factModelTree.getGenericTypeInfo(VALUE));
            factMapping.setColumnWidth(getColumnWidth(factIdentifier.getName()));

            previousSteps.forEach(step -> factMapping.addExpressionElement(step, factType));

            if (previousSteps.isEmpty()) {
                factMapping.addExpressionElement(factModelTree.getFactName(), factType);
            }
            abstractScesimData.addMappingValue(factIdentifier, expressionIdentifier, null);

            return factMapping;
        }
    }
}
