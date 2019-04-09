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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedMap;
import java.util.stream.IntStream;

import javax.enterprise.context.Dependent;

import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.drools.workbench.screens.scenariosimulation.model.FactIdentifier;
import org.drools.workbench.screens.scenariosimulation.model.FactMapping;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTree;
import org.drools.workbench.screens.scenariosimulation.utils.ScenarioSimulationSharedUtils;
import org.uberfire.ext.wires.core.grids.client.model.GridData;

import static org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationUtils.getPropertyMetaDataGroup;

/**
 * <code>Command</code> to to set the <i>property</i> level header for a given column
 */
@Dependent
public class SetPropertyHeaderCommand extends AbstractSetHeaderCommand {

    @Override
    protected void executeIfSelectedColumn(ScenarioSimulationContext context, ScenarioGridColumn selectedColumn) {
        int columnIndex = context.getModel().getColumns().indexOf(selectedColumn);
        String value = context.getStatus().getValue();
        final List<String> valuesElements = Arrays.asList(value.split("\\."));
        String aliasName = valuesElements.get(0);
        String canonicalClassName = getFullPackage(context) + aliasName;
        FactIdentifier factIdentifier = setEditableHeadersAndGetFactIdentifier(context, selectedColumn, aliasName, canonicalClassName);
        String className = factIdentifier.getClassName();
        String propertyHeaderTitle = getPropertyHeaderTitle(context, factIdentifier);
        final GridData.Range instanceLimits = context.getModel().getInstanceLimits(columnIndex);
        IntStream.range(instanceLimits.getMinRowIndex(), instanceLimits.getMaxRowIndex() + 1)
                .forEach(index -> {
                    final ScenarioGridColumn scenarioGridColumn = (ScenarioGridColumn) context.getModel().getColumns().get(index);
                    if (!scenarioGridColumn.isInstanceAssigned()) { // We have not defined the instance, yet
                        setInstanceHeaderMetaData(scenarioGridColumn, aliasName, factIdentifier);
                    }
                });
        selectedColumn.getPropertyHeaderMetaData().setColumnGroup(getPropertyMetaDataGroup(selectedColumn.getInformationHeaderMetaData().getColumnGroup()));
        setPropertyMetaData(selectedColumn.getPropertyHeaderMetaData(), propertyHeaderTitle, false, selectedColumn, ScenarioSimulationEditorConstants.INSTANCE.insertValue());
        selectedColumn.setPropertyAssigned(true);
        String propertyClass = context.getStatus().getValueClassName();
        context.getModel().updateColumnProperty(columnIndex,
                                                selectedColumn,
                                                value,
                                                propertyClass, context.getStatus().isKeepData());
        if (ScenarioSimulationSharedUtils.isCollection(propertyClass)) {
            final SortedMap<String, FactModelTree> dataObjectFieldsMap = context.getDataObjectFieldsMap();
            final List<String> classNameElements = Arrays.asList(className.split("\\."));
            final FactModelTree nestedFactModelTree = navigateComplexObject(dataObjectFieldsMap.get(classNameElements.get(classNameElements.size() - 1)),
                                                                            classNameElements,
                                                                            dataObjectFieldsMap);

            selectedColumn.setFactory(context.getCollectionEditorSingletonDOMElementFactory());
            final FactMapping factMappingByIndex = context.getModel().getSimulation().get().getSimulationDescriptor().getFactMappingByIndex(columnIndex);
            factMappingByIndex.setGenericTypes(nestedFactModelTree.getGenericTypeInfo(valuesElements.get(valuesElements.size() - 1)));
        } else {
            selectedColumn.setFactory(context.getScenarioCellTextAreaSingletonDOMElementFactory());
        }
        if (context.getScenarioSimulationEditorPresenter() != null) {
            context.getScenarioSimulationEditorPresenter().reloadTestTools(false);
        }
    }

    protected FactModelTree navigateComplexObject(FactModelTree factModelTree, List<String> elements, SortedMap<String, FactModelTree> dataObjectFieldsMap) {
        FactModelTree nestedFactModelTree = factModelTree;
        if (elements.size() > 2) {
            for (String step : elements.subList(1, elements.size() - 1)) {
                if (nestedFactModelTree.getExpandableProperties().containsKey(step)) {
                    nestedFactModelTree = dataObjectFieldsMap.get(factModelTree.getExpandableProperties().get(step));
                }
            }
        }
        return nestedFactModelTree;
    }

    protected String getPropertyHeaderTitle(ScenarioSimulationContext context, FactIdentifier factIdentifier) {
        String value = context.getStatus().getValue();
        String toReturn = value.contains(".") ? value.substring(value.indexOf(".") + 1) : "value";
        final List<FactMapping> factMappingsByFactName = context.getStatus().getSimulation().getSimulationDescriptor().getFactMappingsByFactName(factIdentifier.getName());
        final Optional<FactMapping> matchingFactMapping = factMappingsByFactName.stream()
                .filter(factMapping -> Objects.equals(factMapping.getFullExpression(), value))
                .findFirst();
        if (matchingFactMapping.isPresent()) {
            toReturn = matchingFactMapping.get().getExpressionAlias();
        }
        return toReturn;
    }
}