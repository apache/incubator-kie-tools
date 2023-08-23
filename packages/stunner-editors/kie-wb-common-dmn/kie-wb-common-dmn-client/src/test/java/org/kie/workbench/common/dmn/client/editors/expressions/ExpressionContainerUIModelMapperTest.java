/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.editors.expressions;

import java.util.Optional;
import java.util.function.Supplier;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ContextGridCell;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionCellValue;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionEditorColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.literal.LiteralExpressionGrid;
import org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.UndefinedExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.ExpressionGridCache;
import org.kie.workbench.common.dmn.client.widgets.grid.ExpressionGridCacheImpl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.BaseUIModelMapper;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ExpressionContainerUIModelMapperTest {

    private static final double MINIMUM_COLUMN_WIDTH = 200.0;

    private static final String NODE_UUID = "uuid";

    @Mock
    private ExpressionEditorColumn uiExpressionColumn;

    @Mock
    private ExpressionContainerGrid expressionContainerGrid;

    @Mock
    private HasName hasName;

    @Mock
    private HasExpression hasExpression;

    @Mock
    private Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;

    @Mock
    private ListSelectorView.Presenter listSelector;

    @Mock
    private ExpressionEditorDefinition literalExpressionEditorDefinition;

    @Mock
    private LiteralExpressionGrid literalExpressionEditor;

    @Mock
    private UndefinedExpressionEditorDefinition undefinedExpressionEditorDefinition;

    @Mock
    private BaseExpressionGrid undefinedExpressionEditor;

    @Captor
    private ArgumentCaptor<Optional<String>> nodeUUIDCaptor;

    private LiteralExpression literalExpression = new LiteralExpression();

    private GridCellTuple parent;

    private BaseGridData uiModel;

    private Expression expression;

    private ExpressionGridCache expressionGridCache;

    private ExpressionContainerUIModelMapper mapper;

    @SuppressWarnings("unchecked")
    public void setup(final boolean isOnlyVisualChangeAllowed) {
        uiModel = new BaseGridData();
        uiModel.appendRow(new BaseGridRow());
        uiModel.appendColumn(uiExpressionColumn);
        when(uiExpressionColumn.getIndex()).thenReturn(0);
        when(uiExpressionColumn.getMinimumWidth()).thenReturn(MINIMUM_COLUMN_WIDTH);

        parent = new GridCellTuple(0, 0, expressionContainerGrid);

        final ExpressionEditorDefinitions expressionEditorDefinitions = new ExpressionEditorDefinitions();
        expressionEditorDefinitions.add(literalExpressionEditorDefinition);
        expressionEditorDefinitions.add(undefinedExpressionEditorDefinition);

        when(expressionEditorDefinitionsSupplier.get()).thenReturn(expressionEditorDefinitions);
        when(literalExpressionEditorDefinition.getModelClass()).thenReturn(Optional.of(literalExpression));
        when(literalExpressionEditor.isCacheable()).thenReturn(true);
        when(literalExpressionEditor.getExpression()).thenReturn(() -> Optional.of(literalExpression));
        when(literalExpressionEditorDefinition.getEditor(any(GridCellTuple.class),
                                                         any(Optional.class),
                                                         any(HasExpression.class),
                                                         any(Optional.class),
                                                         anyBoolean(),
                                                         anyInt())).thenReturn(Optional.of(literalExpressionEditor));

        when(undefinedExpressionEditorDefinition.getModelClass()).thenReturn(Optional.empty());
        when(undefinedExpressionEditorDefinition.getEditor(any(GridCellTuple.class),
                                                           any(Optional.class),
                                                           any(HasExpression.class),
                                                           any(Optional.class),
                                                           anyBoolean(),
                                                           anyInt())).thenReturn(Optional.of(undefinedExpressionEditor));

        expressionGridCache = spy(new ExpressionGridCacheImpl());
        mapper = new ExpressionContainerUIModelMapper(parent,
                                                      () -> uiModel,
                                                      () -> Optional.ofNullable(expression),
                                                      () -> NODE_UUID,
                                                      () -> hasExpression,
                                                      () -> Optional.of(hasName),
                                                      () -> isOnlyVisualChangeAllowed,
                                                      expressionEditorDefinitionsSupplier,
                                                      () -> expressionGridCache,
                                                      listSelector);
    }

    @Test
    public void testFromDMNModelUndefinedExpressionType() {
        setup(false);

        expression = null;

        mapper.fromDMNModel(0, 0);

        assertFromDMNModelUndefinedExpressionType(false);
    }

    @Test
    public void testFromDMNModelWhenOnlyVisualChangeAllowed() {
        setup(true);

        expression = null;

        mapper.fromDMNModel(0, 0);

        assertFromDMNModelUndefinedExpressionType(true);
    }

    private void assertFromDMNModelUndefinedExpressionType(final boolean isOnlyVisualChangeAllowed) {
        assertUiModel();
        assertEditorType(undefinedExpressionEditor.getClass());

        verify(undefinedExpressionEditorDefinition).getEditor(eq(parent),
                                                              nodeUUIDCaptor.capture(),
                                                              eq(hasExpression),
                                                              eq(Optional.of(hasName)),
                                                              eq(isOnlyVisualChangeAllowed),
                                                              eq(0));
        final Optional<String> nodeUUID = nodeUUIDCaptor.getValue();
        assertThat(nodeUUID.isPresent()).isTrue();
        assertThat(nodeUUID.get()).isEqualTo(NODE_UUID);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testFromDMNModelLiteralExpressionType() {
        setup(false);

        expression = new LiteralExpression();

        mapper.fromDMNModel(0, 0);

        assertUiModel();
        assertEditorType(literalExpressionEditor.getClass());

        verify(literalExpressionEditorDefinition).getEditor(eq(parent),
                                                            nodeUUIDCaptor.capture(),
                                                            eq(hasExpression),
                                                            eq(Optional.of(hasName)),
                                                            eq(false),
                                                            eq(0));
        final Optional<String> nodeUUID = nodeUUIDCaptor.getValue();
        assertThat(nodeUUID.isPresent()).isTrue();
        assertThat(nodeUUID.get()).isEqualTo(NODE_UUID);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testFromDMNModelExpressionGridCacheIsHit() {
        setup(false);

        expression = new LiteralExpression();

        mapper.fromDMNModel(0, 0);

        verify(literalExpressionEditorDefinition).getEditor(eq(parent),
                                                            nodeUUIDCaptor.capture(),
                                                            eq(hasExpression),
                                                            eq(Optional.of(hasName)),
                                                            eq(false),
                                                            eq(0));

        verify(expressionGridCache).putExpressionGrid(nodeUUIDCaptor.getValue().get(),
                                                      Optional.of(literalExpressionEditor));

        mapper.fromDMNModel(0, 0);

        //There should only be one interaction with LiteralExpressionEditorDefinition
        verify(literalExpressionEditorDefinition).getEditor(any(GridCellTuple.class),
                                                            any(Optional.class),
                                                            any(HasExpression.class),
                                                            any(Optional.class),
                                                            anyBoolean(),
                                                            anyInt());
        verify(expressionGridCache).putExpressionGrid(Mockito.<String>any(),
                                                      any(Optional.class));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testToDMNModelIsUnsupported() {
        setup(false);

        mapper.toDMNModel(0, 0, () -> null);
    }

    private void assertUiModel() {
        assertThat(uiModel.getRowCount()).isEqualTo(1);
        assertThat(uiModel.getColumnCount()).isEqualTo(1);
    }

    private void assertEditorType(final Class<?> clazz) {
        final GridCell<?> gridCell = uiModel.getCell(0, 0);

        assertThat(gridCell).isNotNull();
        assertThat(gridCell).isInstanceOf(ContextGridCell.class);

        final GridCellValue<?> gridCellValue = gridCell.getValue();

        assertThat(gridCellValue).isNotNull();
        assertThat(gridCellValue).isInstanceOf(ExpressionCellValue.class);

        final ExpressionCellValue ecv = (ExpressionCellValue) gridCellValue;
        final Optional<BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper>> editor = ecv.getValue();

        assertThat(editor.isPresent()).isTrue();
        assertThat(editor.get()).isInstanceOf(clazz);
    }
}
