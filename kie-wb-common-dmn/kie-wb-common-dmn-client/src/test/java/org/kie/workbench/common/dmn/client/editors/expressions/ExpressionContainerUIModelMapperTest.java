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

package org.kie.workbench.common.dmn.client.editors.expressions;

import java.util.Optional;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
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
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridRow;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
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

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        uiModel = new BaseGridData();
        uiModel.appendRow(new DMNGridRow());
        uiModel.appendColumn(uiExpressionColumn);
        doReturn(0).when(uiExpressionColumn).getIndex();
        doReturn(MINIMUM_COLUMN_WIDTH).when(uiExpressionColumn).getMinimumWidth();

        parent = new GridCellTuple(0, 0, expressionContainerGrid);

        final ExpressionEditorDefinitions expressionEditorDefinitions = new ExpressionEditorDefinitions();
        expressionEditorDefinitions.add(literalExpressionEditorDefinition);
        expressionEditorDefinitions.add(undefinedExpressionEditorDefinition);

        doReturn(expressionEditorDefinitions).when(expressionEditorDefinitionsSupplier).get();
        doReturn(Optional.of(literalExpression)).when(literalExpressionEditorDefinition).getModelClass();
        doReturn(true).when(literalExpressionEditor).isCacheable();
        doReturn(Optional.of(literalExpression)).when(literalExpressionEditor).getExpression();
        doReturn(Optional.of(literalExpressionEditor)).when(literalExpressionEditorDefinition).getEditor(any(GridCellTuple.class),
                                                                                                         any(Optional.class),
                                                                                                         any(HasExpression.class),
                                                                                                         any(Optional.class),
                                                                                                         any(Optional.class),
                                                                                                         anyInt());

        doReturn(Optional.empty()).when(undefinedExpressionEditorDefinition).getModelClass();
        doReturn(Optional.of(undefinedExpressionEditor)).when(undefinedExpressionEditorDefinition).getEditor(any(GridCellTuple.class),
                                                                                                             any(Optional.class),
                                                                                                             any(HasExpression.class),
                                                                                                             any(Optional.class),
                                                                                                             any(Optional.class),
                                                                                                             anyInt());

        expressionGridCache = new ExpressionGridCacheImpl();
        mapper = new ExpressionContainerUIModelMapper(parent,
                                                      () -> uiModel,
                                                      () -> Optional.ofNullable(expression),
                                                      () -> NODE_UUID,
                                                      () -> hasExpression,
                                                      () -> Optional.of(hasName),
                                                      expressionEditorDefinitionsSupplier,
                                                      expressionGridCache,
                                                      listSelector);
    }

    @Test
    public void testFromDMNModelUndefinedExpressionType() {
        expression = null;

        mapper.fromDMNModel(0, 0);

        assertUiModel();
        assertEditorType(undefinedExpressionEditor.getClass());

        verify(undefinedExpressionEditorDefinition).getEditor(eq(parent),
                                                              nodeUUIDCaptor.capture(),
                                                              eq(hasExpression),
                                                              eq(Optional.ofNullable(expression)),
                                                              eq(Optional.of(hasName)),
                                                              eq(0));
        final Optional<String> nodeUUID = nodeUUIDCaptor.getValue();
        assertThat(nodeUUID.isPresent()).isTrue();
        assertThat(nodeUUID.get()).isEqualTo(NODE_UUID);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testFromDMNModelLiteralExpressionType() {
        expression = new LiteralExpression();

        mapper.fromDMNModel(0, 0);

        assertUiModel();
        assertEditorType(literalExpressionEditor.getClass());

        verify(literalExpressionEditorDefinition).getEditor(eq(parent),
                                                            nodeUUIDCaptor.capture(),
                                                            eq(hasExpression),
                                                            eq(Optional.of(expression)),
                                                            eq(Optional.of(hasName)),
                                                            eq(0));
        final Optional<String> nodeUUID = nodeUUIDCaptor.getValue();
        assertThat(nodeUUID.isPresent()).isTrue();
        assertThat(nodeUUID.get()).isEqualTo(NODE_UUID);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testFromDMNModelExpressionGridCacheIsHit() {
        expression = new LiteralExpression();

        mapper.fromDMNModel(0, 0);

        verify(literalExpressionEditorDefinition).getEditor(eq(parent),
                                                            nodeUUIDCaptor.capture(),
                                                            eq(hasExpression),
                                                            eq(Optional.of(expression)),
                                                            eq(Optional.of(hasName)),
                                                            eq(0));

        mapper.fromDMNModel(0, 0);

        //There should only be one interaction with LiteralExpressionEditorDefinition
        verify(literalExpressionEditorDefinition).getEditor(any(GridCellTuple.class),
                                                            any(Optional.class),
                                                            any(HasExpression.class),
                                                            any(Optional.class),
                                                            any(Optional.class),
                                                            anyInt());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testToDMNModelIsUnsupported() {
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
        final Optional<BaseExpressionGrid> editor = ecv.getValue();

        assertThat(editor.isPresent()).isTrue();
        assertThat(editor.get()).isInstanceOf(clazz);
    }
}
