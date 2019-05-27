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

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.drools.scenariosimulation.api.model.ExpressionElement;
import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.scenariosimulation.api.model.FactMapping;
import org.drools.scenariosimulation.api.model.FactMappingType;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.utils.ScenarioSimulationSharedUtils;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.metadata.ScenarioHeaderMetaData;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationUtils;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTree;
import org.uberfire.ext.wires.core.grids.client.model.GridData;

import static org.drools.scenariosimulation.api.model.FactMapping.getPropertyPlaceHolder;
import static org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationUtils.getPropertyMetaDataGroup;
import static org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationUtils.getPropertyNameElementsWithoutAlias;

/**
 * <b>Abstract</b> <code>Command</code> class which assures that a <code>ScenarioColumn</code> is selected.
 */
public abstract class AbstractSelectedColumnCommand extends AbstractScenarioSimulationCommand {

    public AbstractSelectedColumnCommand() {
        super(true);
    }

    protected abstract void executeIfSelectedColumn(ScenarioSimulationContext context, ScenarioGridColumn selectedColumn);

    @Override
    protected void internalExecute(ScenarioSimulationContext context) {
        getSelectedColumn(context).ifPresent(selectedColumn -> executeIfSelectedColumn(context, selectedColumn));
    }

    /**
     * It inserts a new <code>ScenarioGridColumn</code> in <code>ScenarioGridModel</code>
     * @param context It contains the <b>Context</b> inside which the commands will be executed
     * @param selectedColumn The selected <code>ScenarioGridColumn</code> where the command was launched
     * @param columnPosition Used to define in which position the new column should be added
     * @param cloneInstance If true, it create a new column inside the same instance of the selected column
     * @return The created <code>ScenarioGridColumn</code>
     */
    protected ScenarioGridColumn insertNewColumn(ScenarioSimulationContext context, ScenarioGridColumn selectedColumn, int columnPosition, boolean cloneInstance) {
        final ScenarioHeaderMetaData selectedInformationHeaderMetaData = selectedColumn.getInformationHeaderMetaData();
        String columnGroup = selectedInformationHeaderMetaData.getColumnGroup();
        String originalInstanceTitle = selectedInformationHeaderMetaData.getTitle();
        final FactMappingType factMappingType = FactMappingType.valueOf(columnGroup.toUpperCase());
        final Map.Entry<String, String> validPlaceholders = context.getModel().getValidPlaceholders();
        String instanceTitle = cloneInstance ? originalInstanceTitle : validPlaceholders.getKey();
        String propertyTitle = validPlaceholders.getValue();
        String placeHolder = ScenarioSimulationEditorConstants.INSTANCE.defineValidType();
        final ScenarioGridColumn scenarioGridColumnLocal = getScenarioGridColumnLocal(instanceTitle,
                                                                                      propertyTitle,
                                                                                      String.valueOf(new Date().getTime()),
                                                                                      columnGroup,
                                                                                      factMappingType,
                                                                                      context.getScenarioHeaderTextBoxSingletonDOMElementFactory(),
                                                                                      context.getScenarioCellTextAreaSingletonDOMElementFactory(),
                                                                                      placeHolder);
        if (cloneInstance) {
            scenarioGridColumnLocal.setFactIdentifier(selectedColumn.getFactIdentifier());
        }
        scenarioGridColumnLocal.setInstanceAssigned(cloneInstance);
        scenarioGridColumnLocal.setPropertyAssigned(false);
        context.getModel().insertColumn(columnPosition, scenarioGridColumnLocal);
        return scenarioGridColumnLocal;
    }

    /**
     * Sets the instance header for a <code>ScenarioSimulationContext</code>.
     * @param context It contains the <b>Context</b> inside which the commands will be executed
     * @param selectedColumn The selected <code>ScenarioGridColumn</code> where the command was launched
     */
    protected void setInstanceHeader(ScenarioSimulationContext context, ScenarioGridColumn selectedColumn, String alias, String fullClassName) {
        int columnIndex = context.getModel().getColumns().indexOf(selectedColumn);
        final FactIdentifier factIdentifier = setEditableHeadersAndGetFactIdentifier(context, selectedColumn, alias, fullClassName);
        setInstanceHeaderMetaData(selectedColumn, alias, factIdentifier);
        final ScenarioHeaderMetaData propertyHeaderMetaData = selectedColumn.getPropertyHeaderMetaData();
        setPropertyMetaData(propertyHeaderMetaData, getPropertyPlaceHolder(columnIndex), false, selectedColumn, ScenarioSimulationEditorConstants.INSTANCE.defineValidType());
        context.getModel().updateColumnInstance(columnIndex, selectedColumn);
        if (context.getScenarioSimulationEditorPresenter() != null) {
            context.getScenarioSimulationEditorPresenter().reloadTestTools(false);
        }
    }

    /**
     * Returns an <code>Optional<ScenarioGridColumn></code> for a <code>ScenarioSimulationContext</code>.
     * @param context It contains the <b>Context</b> inside which the commands will be executed
     * @return
     */
    protected Optional<ScenarioGridColumn> getSelectedColumn(ScenarioSimulationContext context) {
        return Optional.ofNullable((ScenarioGridColumn) context.getModel().getSelectedColumn());
    }

    /**
     * Returns the full package <code>String</code> of a <code>ScenarioSimulationContext</code>.
     * @param context
     * @return
     */
    protected String getFullPackage(ScenarioSimulationContext context) {
        String fullPackage = context.getStatus().getFullPackage();
        if (!fullPackage.endsWith(".")) {
            fullPackage += ".";
        }
        return fullPackage;
    }

    /**
     * Sets the editable headers on a given <code>ScenarioGridColumn</code> and returns a <code>FactIdentifier</code>.
     * @param context
     * @param selectedColumn
     * @param aliasName
     * @param canonicalClassName
     * @return
     */
    protected FactIdentifier setEditableHeadersAndGetFactIdentifier(ScenarioSimulationContext context, ScenarioGridColumn selectedColumn, String aliasName, String canonicalClassName) {
        final ScenarioSimulationModel.Type simulationModelType = context.getModel().getSimulation().get().getSimulationDescriptor().getType();
        selectedColumn.setEditableHeaders(!simulationModelType.equals(ScenarioSimulationModel.Type.DMN));
        String nameToUseForCreation = simulationModelType.equals(ScenarioSimulationModel.Type.DMN) ? aliasName : selectedColumn.getInformationHeaderMetaData().getColumnId();
        return getFactIdentifierByColumnTitle(aliasName, context).orElseGet(() -> FactIdentifier.create(nameToUseForCreation, canonicalClassName));
    }

    /**
     * Sets the metadata for an instance header on a given <code>ScenarioGridColumn</code>.
     * @param scenarioGridColumn
     * @param aliasName
     * @param factIdentifier
     */
    protected void setInstanceHeaderMetaData(ScenarioGridColumn scenarioGridColumn, String aliasName, FactIdentifier factIdentifier) {
        scenarioGridColumn.getInformationHeaderMetaData().setTitle(aliasName);
        scenarioGridColumn.setInstanceAssigned(true);
        scenarioGridColumn.setFactIdentifier(factIdentifier);
    }

    /**
     * It assigns a property to the selected <code>ScenarioGridColumn</code>
     * @param context It contains the <b>Context</b> inside which the commands will be executed
     * @param selectedColumn The selected <code>ScenarioGridColumn</code> where the command was launched
     * @param propertyNameElements The <code>List</code> with the path instance_name.property.name (eg. Author.isAlive)
     * @param propertyClass it contains the full classname of the property (eg. com.Author)
     * @param propertyHeaderTitle The title to assign to this property. Can be null, in this case it will be retrieved used <code>getPropertyHeaderTitle()</code> method
     */
    protected void setPropertyHeader(ScenarioSimulationContext context, ScenarioGridColumn selectedColumn, List<String> propertyNameElements, String propertyClass, Optional<String> propertyHeaderTitle) {
        int columnIndex = context.getModel().getColumns().indexOf(selectedColumn);
        String aliasName = propertyNameElements.get(0);
        String canonicalClassName = getFullPackage(context) + aliasName;
        final FactIdentifier factIdentifier = setEditableHeadersAndGetFactIdentifier(context, selectedColumn, aliasName, canonicalClassName);
        String className = factIdentifier.getClassName();
        String propertyTitle = propertyHeaderTitle.orElseGet(() -> getPropertyHeaderTitle(context, propertyNameElements, factIdentifier));
        final GridData.Range instanceLimits = context.getModel().getInstanceLimits(columnIndex);
        IntStream.range(instanceLimits.getMinRowIndex(), instanceLimits.getMaxRowIndex() + 1)
                .forEach(index -> {
                    final ScenarioGridColumn scenarioGridColumn = (ScenarioGridColumn) context.getModel().getColumns().get(index);
                    if (!scenarioGridColumn.isInstanceAssigned()) { // We have not defined the instance, yet
                        setInstanceHeaderMetaData(scenarioGridColumn, aliasName, factIdentifier);
                    }
                });
        selectedColumn.getPropertyHeaderMetaData().setColumnGroup(getPropertyMetaDataGroup(selectedColumn.getInformationHeaderMetaData().getColumnGroup()));
        String editableCellPlaceholder = ScenarioSimulationUtils.getPlaceholder(propertyClass);
        setPropertyMetaData(selectedColumn.getPropertyHeaderMetaData(), propertyTitle, false, selectedColumn, editableCellPlaceholder);
        selectedColumn.setPropertyAssigned(true);
        context.getModel().updateColumnProperty(columnIndex,
                                                selectedColumn,
                                                propertyNameElements,
                                                propertyClass, context.getStatus().isKeepData());
        if (ScenarioSimulationSharedUtils.isCollection(propertyClass)) {
            manageCollectionProperty(context, selectedColumn, className, columnIndex, propertyNameElements);
        } else {
            selectedColumn.setFactory(context.getScenarioCellTextAreaSingletonDOMElementFactory());
        }
        if (context.getScenarioSimulationEditorPresenter() != null) {
            context.getScenarioSimulationEditorPresenter().reloadTestTools(false);
        }
    }

    /**
     * It sets the title and readOnly setting of a property header and sets the place holder on a given <code>ScenarioGridColumn</code>.
     * @param propertyHeaderMetaData
     * @param title
     * @param readOnly
     * @param selectedColumn
     * @param placeHolder
     */
    protected void setPropertyMetaData(ScenarioHeaderMetaData propertyHeaderMetaData, String title, boolean readOnly, ScenarioGridColumn selectedColumn, String placeHolder) {
        propertyHeaderMetaData.setTitle(title);
        propertyHeaderMetaData.setReadOnly(readOnly);
        selectedColumn.setPlaceHolder(placeHolder);
    }

    /**
     * @param context
     * @param selectedColumn
     * @param className The name of the class to be used to retrieve the corresponding <code>FactModelTree</code>, i.e. without the <b>package</b>
     * @param columnIndex
     * @param fullPropertyPathElements This is the <code>List</code> of all the elements pointing to the final property (ex. Book.author.books)
     */
    protected void manageCollectionProperty(ScenarioSimulationContext context, ScenarioGridColumn selectedColumn, String className, int columnIndex, List<String> fullPropertyPathElements) {
        final SortedMap<String, FactModelTree> dataObjectFieldsMap = context.getDataObjectFieldsMap();
        if (className.contains(".")) {
            className = className.substring(className.lastIndexOf(".") + 1);
        }
        final FactModelTree nestedFactModelTree = navigateComplexObject(dataObjectFieldsMap.get(className),
                                                                        fullPropertyPathElements,
                                                                        dataObjectFieldsMap);
        selectedColumn.setFactory(context.getCollectionEditorSingletonDOMElementFactory());
        final FactMapping factMappingByIndex = context.getModel().getSimulation().get().getSimulationDescriptor().getFactMappingByIndex(columnIndex);
        factMappingByIndex.setGenericTypes(nestedFactModelTree.getGenericTypeInfo(fullPropertyPathElements.get(fullPropertyPathElements.size() - 1)));
    }

    /**
     * @param factModelTree
     * @param pathElements This is the <code>List</code> of all the elements pointing to the final property (ex. Book.author.books)
     * @param dataObjectFieldsMap
     * @return
     */
    protected FactModelTree navigateComplexObject(FactModelTree factModelTree, List<String> pathElements, SortedMap<String, FactModelTree> dataObjectFieldsMap) {
        FactModelTree nestedFactModelTree = factModelTree;
        if (pathElements.size() > 2) {
            for (String step : pathElements.subList(1, pathElements.size() - 1)) {
                if (nestedFactModelTree.getExpandableProperties().containsKey(step)) {
                    nestedFactModelTree = dataObjectFieldsMap.get(factModelTree.getExpandableProperties().get(step));
                }
            }
        }
        return nestedFactModelTree;
    }

    protected String getPropertyHeaderTitle(ScenarioSimulationContext context, List<String> propertyNameElements, FactIdentifier factIdentifier) {
        String propertyPathPart = propertyNameElements.size() > 1 ?
                String.join(".", propertyNameElements.subList(1, propertyNameElements.size())) : "value";
        List<String> propertyNameElementsClone = getPropertyNameElementsWithoutAlias(propertyNameElements, factIdentifier);
        // This is because the propertyName starts with the alias of the fact; i.e. it may be Book.name but also Bookkk.name,
        // while the first element of ExpressionElements is always the class name
        return getMatchingExpressionAlias(context, propertyNameElementsClone, factIdentifier).orElse(propertyPathPart);
    }

    protected Optional<String> getMatchingExpressionAlias(ScenarioSimulationContext context, List<String> propertyNameElements, FactIdentifier factIdentifier) {
        final List<FactMapping> factMappingsByFactName = context.getStatus().getSimulation().getSimulationDescriptor().getFactMappingsByFactName(factIdentifier.getName());
        return factMappingsByFactName.stream()
                .filter(factMapping -> {
                    List<String> expressionElements = factMapping.getExpressionElements().stream().map(ExpressionElement::getStep).collect(Collectors.toList());
                    return Objects.equals(expressionElements, propertyNameElements);
                })
                .findFirst()
                .map(FactMapping::getExpressionAlias);
    }
}