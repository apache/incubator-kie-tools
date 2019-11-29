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

package org.drools.workbench.screens.scenariosimulation.client.factories;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.drools.scenariosimulation.api.model.AbstractScesimData;
import org.drools.scenariosimulation.api.model.AbstractScesimModel;
import org.drools.scenariosimulation.api.model.FactMapping;
import org.drools.scenariosimulation.api.utils.ScenarioSimulationSharedUtils;
import org.drools.workbench.screens.scenariosimulation.client.collectioneditor.CollectionViewImpl;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;
import org.drools.workbench.screens.scenariosimulation.client.domelements.CollectionEditorDOMElement;
import org.drools.workbench.screens.scenariosimulation.client.models.AbstractScesimGridModel;
import org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationUtils;
import org.drools.workbench.screens.scenariosimulation.client.utils.ViewsProvider;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGrid;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.dom.single.impl.BaseSingletonDOMElementFactory;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLienzoPanel;

import static org.drools.scenariosimulation.api.model.ScenarioSimulationModel.Type.RULE;
import static org.drools.scenariosimulation.api.utils.ConstantsHolder.VALUE;
import static org.drools.workbench.screens.scenariosimulation.client.utils.ScenarioSimulationUtils.isSimpleJavaType;

public class CollectionEditorSingletonDOMElementFactory extends BaseSingletonDOMElementFactory<String, CollectionViewImpl, CollectionEditorDOMElement> {

    protected ViewsProvider viewsProvider;

    protected ScenarioSimulationContext scenarioSimulationContext;

    public CollectionEditorSingletonDOMElementFactory(final GridLienzoPanel gridPanel,
                                                      final GridLayer gridLayer,
                                                      final GridWidget gridWidget,
                                                      final ScenarioSimulationContext scenarioSimulationContext,
                                                      final ViewsProvider viewsProvider) {
        super(gridPanel,
              gridLayer,
              gridWidget);
        this.scenarioSimulationContext = scenarioSimulationContext;
        this.viewsProvider = viewsProvider;
    }

    @Override
    public CollectionViewImpl createWidget() {
        return (CollectionViewImpl) viewsProvider.getCollectionEditorView();
    }

    @Override
    public CollectionEditorDOMElement createDomElement(final GridLayer gridLayer,
                                                       final GridWidget gridWidget) {
        if (this.widget != null) {
            this.widget.close();
        }
        this.widget = createWidget();
        final AbstractScesimGridModel<? extends AbstractScesimModel, ? extends AbstractScesimData> model = ((ScenarioGrid) gridWidget).getModel();
        final GridData.SelectedCell selectedCellsOrigin = model.getSelectedCellsOrigin();
        final Optional<GridColumn<?>> selectedColumn = model.getColumns().stream()
                .filter(col -> col.getIndex() == selectedCellsOrigin.getColumnIndex())
                .findFirst();
        selectedColumn.ifPresent(col -> {
            final int actualIndex = model.getColumns().indexOf(col);
            final FactMapping factMapping = model.getAbstractScesimModel().get().getScesimModelDescriptor().getFactMappingByIndex(actualIndex);
            setCollectionEditorStructureData(this.widget, factMapping);
            this.e = createDomElementInternal(widget, gridLayer, gridWidget);
        });
        return e;
    }

    @Override
    protected String getValue() {
        return widget != null ? widget.getValue() : null;
    }

    protected void setCollectionEditorStructureData(CollectionViewImpl collectionEditorView, FactMapping factMapping) {
        String propertyClass = factMapping.getClassName();
        String className = factMapping.getFactAlias();
        String propertyName = factMapping.getExpressionAlias();
        List<String> genericTypes = factMapping.getGenericTypes();
        if (propertyClass == null || className == null || propertyName == null || genericTypes == null || genericTypes.isEmpty()) {
            throw new IllegalStateException("Missing required properties inside FactMapping");
        }
        String key = className + "#" + propertyName;
        String genericTypeName0 = genericTypes.get(0);
        boolean isRule = RULE.equals(scenarioSimulationContext.getSettings().getType());
        if (isRule && !isSimpleJavaType(genericTypeName0)) {
            genericTypeName0 = getRuleComplexType(genericTypeName0);
        }
        if (ScenarioSimulationSharedUtils.isList(propertyClass)) {
            manageList(collectionEditorView, key, genericTypeName0);
        } else {
            manageMap(collectionEditorView, key, genericTypeName0, genericTypes.get(1), isRule);
        }
    }

    protected String getRuleComplexType(String genericTypeName0) {
        return genericTypeName0.substring(genericTypeName0.lastIndexOf('.') + 1);
    }

    protected void manageList(CollectionViewImpl collectionEditorView, String key, String genericTypeName0) {
        collectionEditorView.setListWidget(true);
        collectionEditorView.initListStructure(key, getSimplePropertiesMap(genericTypeName0), getExpandablePropertiesMap(genericTypeName0));
    }

    protected void manageMap(CollectionViewImpl collectionEditorView, String key, String genericTypeName0, String genericTypeName1, boolean isRule) {
        if (isRule && !isSimpleJavaType(genericTypeName1)) {
            genericTypeName1 = getRuleComplexType(genericTypeName1);
        }
        collectionEditorView.setListWidget(false);
        collectionEditorView.initMapStructure(key, getSimplePropertiesMap(genericTypeName0), getSimplePropertiesMap(genericTypeName1));
    }

    @Override
    public CollectionEditorDOMElement createDomElementInternal(final CollectionViewImpl collectionEditorView,
                                                               final GridLayer gridLayer,
                                                               final GridWidget gridWidget) {
        return new CollectionEditorDOMElement(collectionEditorView, gridLayer, gridWidget);
    }

    /**
     * Retrieve a <code>Map</code> with the property name/type of the given <b>typeName</b>
     * <b>If</b> typeName is a <b>simple</b> class (see {@link ScenarioSimulationUtils#isSimpleJavaType(java.lang.String)})
     * the returned <code>Map</code> will have an entry with <b>value</b> as key and <b>(typeName)</b> as value
     * @param typeName
     * @return
     */
    protected Map<String, String> getSimplePropertiesMap(String typeName) {
        Map<String, String> toReturn;
        if (isSimpleJavaType(typeName)) {
            toReturn = new HashMap<>();
            toReturn.put(VALUE, typeName);
        } else {
            toReturn = scenarioSimulationContext.getDataObjectFieldsMap().get(typeName).getSimpleProperties();
        }
        return toReturn;
    }

    protected Map<String, Map<String, String>> getExpandablePropertiesMap(String typeName) {
        final Map<String, Map<String, String>> toReturn = new HashMap<>();
        if (isSimpleJavaType(typeName)) {
            return toReturn;
        }
        boolean isRule = RULE.equals(scenarioSimulationContext.getSettings().getType());
        final Map<String, String> expandableProperties = scenarioSimulationContext.getDataObjectFieldsMap().get(typeName).getExpandableProperties();
        expandableProperties.forEach((key, nestedTypeName) -> {
            if (isRule) {
                nestedTypeName = nestedTypeName.substring(nestedTypeName.lastIndexOf('.') + 1);
            }
            toReturn.put(key, getSimplePropertiesMap(nestedTypeName));
        });
        return toReturn;
    }

    protected void commonCloseHandling(final CollectionEditorDOMElement collectionEditorDOMElement) {
        destroyResources();
        gridLayer.batch();
        gridPanel.setFocus(true);
        collectionEditorDOMElement.stopEditingMode();
    }

    @Override
    public void registerHandlers(final CollectionViewImpl widget, final CollectionEditorDOMElement widgetDomElement) {
        widget.addCloseCompositeEventHandler(event -> commonCloseHandling(widgetDomElement));
        widget.addSaveEditorEventHandler(event -> flush());
    }
}

