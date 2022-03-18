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
import java.util.stream.Stream;

import org.drools.scenariosimulation.api.model.AbstractScesimModel;
import org.drools.scenariosimulation.api.model.ExpressionElement;
import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.scenariosimulation.api.model.FactMapping;
import org.drools.scenariosimulation.api.model.FactMappingType;
import org.drools.scenariosimulation.api.model.FactMappingValueType;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.utils.ScenarioSimulationSharedUtils;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.enums.GridWidget;
import org.drools.workbench.screens.scenariosimulation.client.metadata.ScenarioHeaderMetaData;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.utils.ConstantHolder;
import org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationUtils;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTree;
import org.uberfire.ext.wires.core.grids.client.model.GridData;

import static org.drools.scenariosimulation.api.model.FactMapping.getPropertyPlaceHolder;
import static org.drools.scenariosimulation.api.utils.ConstantsHolder.VALUE;
import static org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationUtils.getColumnSubGroup;
import static org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationUtils.getPropertyNameElementsWithoutAlias;

/**
 * <b>Abstract</b> <code>Command</code> class which assures that a <code>ScenarioColumn</code> is selected.
 */
public abstract class AbstractSelectedColumnCommand extends AbstractScenarioGridCommand {

    protected FactMappingValueType factMappingValueType;

    protected AbstractSelectedColumnCommand(GridWidget gridWidget, FactMappingValueType factMappingValueType) {
        super(gridWidget);
        this.factMappingValueType = factMappingValueType;
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
        final Map.Entry<String, String> validPlaceholders = context.getAbstractScesimGridModelByGridWidget(gridWidget).getValidPlaceholders();
        String instanceTitle = cloneInstance ? originalInstanceTitle : validPlaceholders.getKey();
        String propertyTitle = validPlaceholders.getValue();
        String placeHolder = ScenarioSimulationEditorConstants.INSTANCE.defineValidType();
        final ScenarioGridColumn scenarioGridColumnLocal = getScenarioGridColumnLocal(instanceTitle,
                                                                                      propertyTitle,
                                                                                      String.valueOf(new Date().getTime()),
                                                                                      columnGroup,
                                                                                      factMappingType,
                                                                                      context.getScenarioHeaderTextBoxSingletonDOMElementFactory(gridWidget),
                                                                                      context.getScenarioCellTextAreaSingletonDOMElementFactory(gridWidget),
                                                                                      placeHolder);
        if (cloneInstance) {
            scenarioGridColumnLocal.setFactIdentifier(selectedColumn.getFactIdentifier());
        }
        scenarioGridColumnLocal.setInstanceAssigned(cloneInstance);
        scenarioGridColumnLocal.setPropertyAssigned(false);
        context.getAbstractScesimGridModelByGridWidget(gridWidget).insertColumn(columnPosition, scenarioGridColumnLocal);
        return scenarioGridColumnLocal;
    }

    /**
     * Sets the instance header for a <code>ScenarioSimulationContext</code>.
     * @param context It contains the <b>Context</b> inside which the commands will be executed
     * @param selectedColumn The selected <code>ScenarioGridColumn</code> where the command was launched
     */
    protected void setInstanceHeader(ScenarioSimulationContext context, ScenarioGridColumn selectedColumn, String alias, String fullClassName) {
        int columnIndex = context.getAbstractScesimGridModelByGridWidget(gridWidget).getColumns().indexOf(selectedColumn);
        final FactIdentifier factIdentifier = setEditableHeadersAndGetFactIdentifier(context, selectedColumn, alias, fullClassName);
        setInstanceHeaderMetaData(selectedColumn, alias, factIdentifier);
        final ScenarioHeaderMetaData propertyHeaderMetaData = selectedColumn.getPropertyHeaderMetaData();
        setPropertyMetaData(propertyHeaderMetaData,
                            getPropertyPlaceHolder(columnIndex),
                            false,
                            selectedColumn,
                            ScenarioSimulationUtils.getPlaceHolder(selectedColumn.isInstanceAssigned(),
                                                                   selectedColumn.isPropertyAssigned(),
                                                                   factMappingValueType,
                                                                   fullClassName));
        context.getAbstractScesimGridModelByGridWidget(gridWidget).updateColumnInstance(columnIndex, selectedColumn);
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
        return Optional.ofNullable((ScenarioGridColumn) context.getAbstractScesimGridModelByGridWidget(gridWidget).getSelectedColumn());
    }

    /**
     * Returns the full package <code>String</code> of a <code>ScenarioSimulationContext</code>.
     * @param context
     * @return
     */
    protected String getFullPackage(ScenarioSimulationContext context) {
        String fullPackage = context.getStatus().getFullPackage();
        if (!fullPackage.isEmpty() && !fullPackage.endsWith(".")) {
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
        final ScenarioSimulationModel.Type simulationModelType = context.getScenarioSimulationModel().getSettings().getType();
        selectedColumn.setEditableHeaders(!(simulationModelType.equals(ScenarioSimulationModel.Type.DMN) || GridWidget.BACKGROUND.equals(gridWidget)));
        String factIdentifierName = simulationModelType.equals(ScenarioSimulationModel.Type.DMN) ? aliasName : selectedColumn.getInformationHeaderMetaData().getColumnId();
        String factIdentifierClassName = simulationModelType.equals(ScenarioSimulationModel.Type.DMN) ? aliasName : canonicalClassName;
        String importPrefix = context.getStatus().getImportPrefix();
        return getFactIdentifierByColumnTitle(aliasName, context).orElseGet(() -> FactIdentifier.create(factIdentifierName, factIdentifierClassName, importPrefix));
    }

    /**
     * Sets the metadata for an instance header on a given <code>ScenarioGridColumn</code>.
     * @param selectedColumn The selected <code>ScenarioGridColumn</code> where the command was launched
     * @param aliasName The title to assign to the selected column
     * @param factIdentifier The <code>FactIdentifier</code> to assign to the selected column
     */
    protected void setInstanceHeaderMetaData(ScenarioGridColumn selectedColumn, String aliasName, FactIdentifier factIdentifier) {
        selectedColumn.getInformationHeaderMetaData().setTitle(aliasName);
        selectedColumn.setInstanceAssigned(true);
        selectedColumn.setFactIdentifier(factIdentifier);
    }

    /**
     * It assigns a property to the selected <code>ScenarioGridColumn</code>
     * @param context It contains the <b>Context</b> inside which the commands will be executed
     * @param selectedColumn The selected <code>ScenarioGridColumn</code> where the command was launched
     * @param propertyNameElements The <code>List</code> with the path instance_name.property.name (eg. Author.isAlive)
     * @param propertyClass it contains the full classname of the property (eg. com.Author)
     */
    protected void setPropertyHeader(ScenarioSimulationContext context, ScenarioGridColumn selectedColumn, String canonicalClassName, List<String> propertyNameElements, String propertyClass) {
        String instanceAliasName = propertyNameElements.get(0);
        final FactIdentifier factIdentifier = setEditableHeadersAndGetFactIdentifier(context, selectedColumn, instanceAliasName, canonicalClassName);
        String propertyTitle = getPropertyHeaderTitle(context, propertyNameElements, factIdentifier);
        this.setPropertyHeader(context, selectedColumn, factIdentifier, propertyNameElements, propertyClass, propertyTitle);
    }

    /**
     * It assigns a property to the selected <code>ScenarioGridColumn</code>
     * @param context It contains the <b>Context</b> inside which the commands will be executed
     * @param selectedColumn The selected <code>ScenarioGridColumn</code> where the command was launched
     * @param propertyNameElements The <code>List</code> with the path instance_name.property.name (eg. Author.isAlive)
     * @param propertyClass it contains the full classname of the property (eg. com.Author)
     * @param propertyTitle The title to assign to this property.
     */
    protected void setPropertyHeader(ScenarioSimulationContext context, ScenarioGridColumn selectedColumn, String canonicalClassName, List<String> propertyNameElements, String propertyClass, String propertyTitle) {
        String instanceAliasName = propertyNameElements.get(0);
        final FactIdentifier factIdentifier = setEditableHeadersAndGetFactIdentifier(context, selectedColumn, instanceAliasName, canonicalClassName);
        this.setPropertyHeader(context, selectedColumn, factIdentifier, propertyNameElements, propertyClass, propertyTitle);
    }

    /**
     * It assigns a property to the selected <code>ScenarioGridColumn</code>
     * @param context It contains the <b>Context</b> inside which the commands will be executed
     * @param selectedColumn The selected <code>ScenarioGridColumn</code> where the command was launched
     * @param factIdentifier The <code>FactIdentifier</code> associated to the selected column
     * @param propertyNameElements The <code>List</code> with the path instance_name.property.name (eg. Author.isAlive)
     * @param propertyClass it contains the full classname of the property (eg. com.Author)
     * @param propertyTitle The title to assign to this property.
     */
    protected void setPropertyHeader(ScenarioSimulationContext context, ScenarioGridColumn selectedColumn, FactIdentifier factIdentifier, List<String> propertyNameElements, String propertyClass, String propertyTitle) {
        final ScenarioSimulationModel.Type simulationModelType = context.getScenarioSimulationModel().getSettings().getType();
        if (propertyTitle == null) {
            throw new IllegalArgumentException("Property title can not be null");
        }
        int columnIndex = context.getAbstractScesimGridModelByGridWidget(gridWidget).getColumns().indexOf(selectedColumn);
        String instanceAliasName = propertyNameElements.get(0);
        if (selectedColumn.isInstanceAssigned() && !instanceAliasName.equals(selectedColumn.getInformationHeaderMetaData().getTitle())) {
            throw new IllegalArgumentException("It's not possible to assign this property");
        }
        String factName = simulationModelType.equals(ScenarioSimulationModel.Type.DMN) ?
                factIdentifier.getName() :
                factIdentifier.getClassNameWithoutPackage();
        final GridData.Range instanceLimits = context.getAbstractScesimGridModelByGridWidget(gridWidget).getInstanceLimits(columnIndex);
        IntStream.range(instanceLimits.getMinRowIndex(), instanceLimits.getMaxRowIndex() + 1)
                .forEach(index -> {
                    final ScenarioGridColumn scenarioGridColumn = (ScenarioGridColumn) context.getAbstractScesimGridModelByGridWidget(gridWidget).getColumns().get(index);
                    if (!scenarioGridColumn.isInstanceAssigned()) { // We have not defined the instance, yet
                        setInstanceHeaderMetaData(scenarioGridColumn, instanceAliasName, factIdentifier);
                    }
                });
        selectedColumn.setPropertyAssigned(true);
        selectedColumn.getPropertyHeaderMetaData().setColumnGroup(getColumnSubGroup(selectedColumn.getInformationHeaderMetaData().getColumnGroup()));
        setPropertyMetaData(selectedColumn.getPropertyHeaderMetaData(),
                            propertyTitle,
                            false,
                            selectedColumn,
                            ScenarioSimulationUtils.getPlaceHolder(selectedColumn.isInstanceAssigned(),
                                                                   selectedColumn.isPropertyAssigned(),
                                                                   factMappingValueType,
                                                                   propertyClass));
        context.getAbstractScesimGridModelByGridWidget(gridWidget).updateColumnProperty(columnIndex,
                                                                                        selectedColumn,
                                                                                        propertyNameElements,
                                                                                        propertyClass,
                                                                                        context.getStatus().isKeepData(),
                                                                                        factMappingValueType,
                                                                                        context.getScenarioSimulationModel().getSettings().getType());
        if (ScenarioSimulationSharedUtils.isCollectionOrMap(propertyClass) && factMappingValueType.equals(FactMappingValueType.NOT_EXPRESSION)) {
            manageCollectionProperty(context, selectedColumn, factName, columnIndex, propertyNameElements);
        } else {
            selectedColumn.setFactory(context.getAbstractScesimGridModelByGridWidget(gridWidget).getDOMElementFactory(propertyClass,
                                                                                                                      context.getScenarioSimulationModel().getSettings().getType(),
                                                                                                                      factMappingValueType));
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
     * @param factName The name of the class to be used to retrieve the corresponding <code>FactModelTree</code>, i.e. without the <b>package</b>
     * @param columnIndex
     * @param fullPropertyPathElements This is the <code>List</code> of all the elements pointing to the final property (ex. Book.author.books)
     */
    protected void manageCollectionProperty(ScenarioSimulationContext context, ScenarioGridColumn selectedColumn, String factName, int columnIndex, List<String> fullPropertyPathElements) {
        final SortedMap<String, FactModelTree> dataObjectFieldsMap = context.getDataObjectFieldsMap();
        final FactModelTree factModelTree = dataObjectFieldsMap.get(factName);
        final Optional<AbstractScesimModel> selectedScenarioGridModel = context.getAbstractScesimGridModelByGridWidget(gridWidget).getAbstractScesimModel();
        if (!selectedScenarioGridModel.isPresent()) {
            throw new IllegalArgumentException("SelectedGrid not found");
        }
        final FactMapping factMapping = selectedScenarioGridModel.get().getScesimModelDescriptor().getFactMappingByIndex(columnIndex);
        selectedColumn.setFactory(context.getCollectionEditorSingletonDOMElementFactory(gridWidget));
        if (factModelTree.isSimple()) {
            factMapping.setGenericTypes(factModelTree.getGenericTypeInfo(VALUE));
        } else {
            final FactModelTree nestedFactModelTree = navigateComplexObject(factModelTree,
                                                                            fullPropertyPathElements,
                                                                            dataObjectFieldsMap);
            factMapping.setGenericTypes(nestedFactModelTree.getGenericTypeInfo(fullPropertyPathElements.get(fullPropertyPathElements.size() - 1)));
        }
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
                    nestedFactModelTree = dataObjectFieldsMap.get(nestedFactModelTree.getExpandableProperties().get(step));
                }
            }
        }
        return nestedFactModelTree;
    }

    protected String getPropertyHeaderTitle(ScenarioSimulationContext context, List<String> propertyNameElements, FactIdentifier factIdentifier) {
        /* If propertyNameElements contains only one step, it's managing an Expression or a SimpleClass type */
        if (propertyNameElements.size() == 1) {
            return FactMappingValueType.EXPRESSION.equals(factMappingValueType) ? ConstantHolder.EXPRESSION_INSTANCE_PLACEHOLDER : VALUE;
        }
        String propertyPathPart = String.join(".", propertyNameElements.subList(1, propertyNameElements.size()));
        List<String> propertyNameElementsClone = getPropertyNameElementsWithoutAlias(propertyNameElements,
                                                                                     factIdentifier,
                                                                                     context.getScenarioSimulationModel().getSettings().getType());
        // This is because the propertyName starts with the alias of the fact; i.e. it may be Book.name but also Bookkk.name,
        // while the first element of ExpressionElements is always the class name
        return getMatchingExpressionAlias(context, propertyNameElementsClone, factIdentifier).orElse(propertyPathPart);
    }

    protected Optional<String> getMatchingExpressionAlias(ScenarioSimulationContext context, List<String> propertyNameElements, FactIdentifier factIdentifier) {
        final Stream<FactMapping> factMappingsByFactName = context.getAbstractScesimModelByGridWidget(gridWidget).getScesimModelDescriptor().getFactMappingsByFactName(factIdentifier.getName());
        return factMappingsByFactName
                .filter(factMapping -> {
                    List<String> expressionElements = factMapping.getExpressionElements().stream().map(ExpressionElement::getStep).collect(Collectors.toList());
                    return Objects.equals(expressionElements, propertyNameElements);
                })
                .findFirst()
                .map(FactMapping::getExpressionAlias);
    }
}