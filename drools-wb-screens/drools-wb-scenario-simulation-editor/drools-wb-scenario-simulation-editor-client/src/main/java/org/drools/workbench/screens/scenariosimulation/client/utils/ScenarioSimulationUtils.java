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
package org.drools.workbench.screens.scenariosimulation.client.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.ait.lienzo.client.core.types.Point2D;
import org.drools.scenariosimulation.api.model.ExpressionIdentifier;
import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.scenariosimulation.api.model.FactMappingType;
import org.drools.workbench.screens.scenariosimulation.client.editor.strategies.SimpleClassEntry;
import org.drools.workbench.screens.scenariosimulation.client.factories.ScenarioCellTextAreaSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.client.factories.ScenarioHeaderTextBoxSingletonDOMElementFactory;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.util.CellContextUtilities;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellEditContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

import static org.drools.workbench.screens.scenariosimulation.client.editor.strategies.DataManagementStrategy.SIMPLE_CLASSES_MAP;
import static org.drools.workbench.screens.scenariosimulation.client.utils.ConstantHolder.LOCALDATE_CANONICAL_NAME;

public class ScenarioSimulationUtils {

    protected static AtomicInteger subGroupCounter = new AtomicInteger(0);

    public static String getPropertyMetaDataGroup(String columnGroup) {
        return columnGroup + "-" + subGroupCounter.getAndIncrement();
    }

    /**
     * This method can be used <b>ONLY</b> when managing a <b>RULE</b> type Test Scenario.
     * @param className
     * @return
     */
    public static boolean isSimpleJavaType(String className) {
        return SIMPLE_CLASSES_MAP.values()
                .stream()
                .map(SimpleClassEntry::getCanonicalName)
                .anyMatch(className::equals);
    }

    /**
     * Method to retrieve a <b>new</b> <code>List</code> of <b>property name elements</b> where the first one is the
     * the <b>actual</b> class name (i.e. an eventual <i>alias</i> get replaced)
     * @param propertyNameElements
     * @param factIdentifier
     * @return
     */
    public static List<String> getPropertyNameElementsWithoutAlias(List<String> propertyNameElements, FactIdentifier factIdentifier) {
        String actualClassName = factIdentifier.getClassName();
        if (actualClassName.contains(".")) {
            actualClassName = actualClassName.substring(actualClassName.lastIndexOf(".") + 1);
        }
        List<String> toReturn = new ArrayList<>(); // We have to keep the original List unmodified
        toReturn.addAll(propertyNameElements);
        if (toReturn.size() > 1) {
            toReturn.set(0, actualClassName);
        }
        return toReturn;
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
    public static ScenarioGridColumn getScenarioGridColumn(String instanceTitle,
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

    /**
     * Returns a <code>ScenarioGridColumn</code> with the following default values:
     * <p>
     * width: 150
     * </p>
     * <p>
     * isMovable: <code>false</code>;
     * </p>
     * <p>
     * columnRenderer: new ScenarioGridColumnRenderer()
     * </p>
     * @param headerBuilder
     * @param factoryCell
     * @param placeHolder
     * @return
     */
    public static ScenarioGridColumn getScenarioGridColumn(ScenarioSimulationBuilders.HeaderBuilder headerBuilder,
                                                           ScenarioCellTextAreaSingletonDOMElementFactory factoryCell,
                                                           String placeHolder) {
        ScenarioSimulationBuilders.ScenarioGridColumnBuilder scenarioGridColumnBuilder = getScenarioGridColumnBuilder(factoryCell,
                                                                                                                      headerBuilder,
                                                                                                                      placeHolder);
        return scenarioGridColumnBuilder.build();
    }

    /**
     * Returns a <code>ScenarioSimulationBuilders.ScenarioGridColumnBuilder</code> with the following default values:
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
     * @param factoryCell
     * @param headerBuilder
     * @param placeHolder
     * @return
     */
    public static ScenarioSimulationBuilders.ScenarioGridColumnBuilder getScenarioGridColumnBuilder(ScenarioCellTextAreaSingletonDOMElementFactory factoryCell,
                                                                                                    ScenarioSimulationBuilders.HeaderBuilder headerBuilder,
                                                                                                    String placeHolder) {
        ScenarioSimulationBuilders.ScenarioGridColumnBuilder toReturn = ScenarioSimulationBuilders.ScenarioGridColumnBuilder.get(factoryCell, headerBuilder);
        toReturn.setPlaceHolder(placeHolder);
        toReturn.setWidth(getColumnWidth(headerBuilder.getColumnId()));
        return toReturn;
    }

    /**
     * Retrieve a <b>single</b> or <b>triple</b> level Header metadata, i.e. a <code>List&lt;GridColumn.HeaderMetaData&gt;</code> with one  or three elements,
     * depending on the column <b>Group</b>:
     * <p>
     * OTHER: single level
     * </p>
     * <p>
     * EXPECT/GIVEN: triple level
     * </p>
     * @param instanceTitle
     * @param propertyTitle
     * @param columnId
     * @param columnGroup
     * @param factMappingType
     * @param factoryHeader
     * @return
     */
    public static ScenarioSimulationBuilders.HeaderBuilder getHeaderBuilder(String instanceTitle,
                                                                            String propertyTitle,
                                                                            String columnId,
                                                                            String columnGroup,
                                                                            FactMappingType factMappingType,
                                                                            ScenarioHeaderTextBoxSingletonDOMElementFactory factoryHeader) {

        ScenarioSimulationBuilders.HeaderBuilder headerBuilder = ScenarioSimulationBuilders.HeaderBuilder.get(factoryHeader);

        headerBuilder.setColumnId(columnId);

        headerBuilder.setColumnTitle(columnGroup);
        headerBuilder.setReadOnly(true);

        if (isOther(factMappingType)) {
            headerBuilder.setColumnTitle(instanceTitle);
            headerBuilder.setColumnGroup(columnGroup);
            headerBuilder.setInstanceHeader(true);
            return headerBuilder;
        }

        // The "instance" header
        final ScenarioSimulationBuilders.HeaderBuilder instanceHeader = headerBuilder.newLevel()
                .setColumnTitle(instanceTitle)
                .setColumnGroup(columnGroup)
                .setReadOnly(false)
                .setInstanceHeader(true)
                .setPropertyHeader(false);

        // The "property" header
        instanceHeader.newLevel()
                .setColumnTitle(propertyTitle)
                .setColumnGroup(getPropertyMetaDataGroup(columnGroup))
                .setReadOnly(false)
                .setInstanceHeader(false)
                .setPropertyHeader(true);

        return headerBuilder;
    }

    /**
     * Returns an array where the 0-element is middle x of given cell and 1-element is middle y
     * @param gridWidget
     * @param column
     * @param isHeader
     * @param uiRowIndex
     * @param gridLayer
     * @return
     */
    public static Point2D getMiddleXYCell(final GridWidget gridWidget, final GridColumn<?> column, boolean isHeader, final int uiRowIndex, final GridLayer gridLayer) {
        final BaseGridRendererHelper rendererHelper = gridWidget.getRendererHelper();
        final BaseGridRendererHelper.RenderingInformation ri = rendererHelper.getRenderingInformation();
        final double columnXCoordinate = rendererHelper.getColumnOffset(column) + column.getWidth() / 2;
        final BaseGridRendererHelper.ColumnInformation ci = rendererHelper.getColumnInformation(columnXCoordinate);

        final GridBodyCellEditContext context = isHeader ?
                CellContextUtilities.makeHeaderCellRenderContext(gridWidget,
                                                                 ri,
                                                                 ci,
                                                                 uiRowIndex)
                : CellContextUtilities.makeCellRenderContext(gridWidget,
                                                             ri,
                                                             ci,
                                                             uiRowIndex);
        final int cellXMiddle = (int) (context.getAbsoluteCellX() +
                context.getCellWidth() / 2 +
                gridLayer.getDomElementContainer().getAbsoluteLeft());
        final int cellYMiddle = (int) (context.getAbsoluteCellY() +
                context.getCellHeight() / 2 +
                gridLayer.getDomElementContainer().getAbsoluteTop());
        return new Point2D(cellXMiddle, cellYMiddle);
    }

    public static String getPlaceholder(String canonicalClassName) {
        return isLocalDate(canonicalClassName) ?
                ScenarioSimulationEditorConstants.INSTANCE.dateFormatPlaceholder() :
                ScenarioSimulationEditorConstants.INSTANCE.insertValue();
    }

    public static boolean isLocalDate(String canonicalClassName) {
        return LOCALDATE_CANONICAL_NAME.equals(canonicalClassName);
    }

    protected static double getColumnWidth(String columnId) {
        ExpressionIdentifier.NAME expressionName = ExpressionIdentifier.NAME.Other;
        try {
            expressionName = ExpressionIdentifier.NAME.valueOf(columnId);
        } catch (IllegalArgumentException e) {
            // ColumnId not recognized
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

    private static boolean isOther(FactMappingType factMappingType) {
        return FactMappingType.OTHER.equals(factMappingType);
    }
}
