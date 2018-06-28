/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function;

import java.util.Optional;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.v1_1.Context;
import org.kie.workbench.common.dmn.api.definition.v1_1.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionCellValue;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionEditorColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.FunctionSupplementaryGrid;
import org.kie.workbench.common.dmn.client.editors.expressions.types.literal.LiteralExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridRow;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FunctionUIModelMapperTest {

    @Mock
    private GridWidget gridWidget;

    @Mock
    private ExpressionEditorColumn uiExpressionEditorColumn;

    @Mock
    private Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;

    @Mock
    private ExpressionEditorDefinition literalExpressionEditorDefinition;

    @Mock
    private LiteralExpressionGrid literalExpressionEditor;

    private LiteralExpression literalExpression = new LiteralExpression();

    @Mock
    private Supplier<ExpressionEditorDefinitions> supplementaryEditorDefinitionsSupplier;

    @Mock
    private ExpressionEditorDefinition supplementaryEditorDefinition;

    @Mock
    private FunctionSupplementaryGrid supplementaryEditor;

    @Mock
    private ListSelectorView.Presenter listSelector;

    @Captor
    private ArgumentCaptor<GridCellTuple> parentCaptor;

    private Context context = new Context();

    private BaseGridData uiModel;

    private FunctionDefinition function;

    private Supplier<Optional<GridCellValue<?>>> cellValueSupplier;

    private FunctionUIModelMapper mapper;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        this.uiModel = new BaseGridData();
        this.uiModel.appendRow(new DMNGridRow());
        this.uiModel.appendRow(new DMNGridRow());
        this.uiModel.appendColumn(uiExpressionEditorColumn);
        doReturn(0).when(uiExpressionEditorColumn).getIndex();
        doReturn(uiModel).when(gridWidget).getModel();

        //Core Editor definitions
        final ExpressionEditorDefinitions expressionEditorDefinitions = new ExpressionEditorDefinitions();
        expressionEditorDefinitions.add(literalExpressionEditorDefinition);

        doReturn(expressionEditorDefinitions).when(expressionEditorDefinitionsSupplier).get();
        doReturn(Optional.of(literalExpression)).when(literalExpressionEditorDefinition).getModelClass();
        doReturn(Optional.of(literalExpression)).when(literalExpressionEditor).getExpression();
        doReturn(Optional.of(literalExpressionEditor)).when(literalExpressionEditorDefinition).getEditor(any(GridCellTuple.class),
                                                                                                         any(Optional.class),
                                                                                                         any(HasExpression.class),
                                                                                                         any(Optional.class),
                                                                                                         any(Optional.class),
                                                                                                         anyInt());

        //Supplementary Editor definitions
        final ExpressionEditorDefinitions supplementaryEditorDefinitions = new ExpressionEditorDefinitions();
        supplementaryEditorDefinitions.add(supplementaryEditorDefinition);

        doReturn(supplementaryEditorDefinitions).when(supplementaryEditorDefinitionsSupplier).get();
        doReturn(Optional.of(context)).when(supplementaryEditorDefinition).getModelClass();
        doReturn(Optional.of(context)).when(supplementaryEditor).getExpression();
        doReturn(Optional.of(supplementaryEditor)).when(supplementaryEditorDefinition).getEditor(any(GridCellTuple.class),
                                                                                                 any(Optional.class),
                                                                                                 any(HasExpression.class),
                                                                                                 any(Optional.class),
                                                                                                 any(Optional.class),
                                                                                                 anyInt());

        this.function = new FunctionDefinition();

        this.mapper = new FunctionUIModelMapper(gridWidget,
                                                () -> uiModel,
                                                () -> Optional.of(function),
                                                expressionEditorDefinitionsSupplier,
                                                supplementaryEditorDefinitionsSupplier,
                                                listSelector,
                                                1);
        this.cellValueSupplier = Optional::empty;
    }

    @Test
    public void testFromDMNModelExpressionKindFEEL() {
        this.function.setExpression(literalExpression);
        this.function.getAdditionalAttributes().put(FunctionDefinition.KIND_QNAME,
                                                    FunctionDefinition.Kind.FEEL.code());

        mapper.fromDMNModel(0, 0);

        assertFromDMNModelEditor(literalExpressionEditor,
                                 literalExpressionEditorDefinition);
    }

    @Test
    public void testFromDMNModelExpressionKindJava() {
        this.function.setExpression(context);
        this.function.getAdditionalAttributes().put(FunctionDefinition.KIND_QNAME,
                                                    FunctionDefinition.Kind.JAVA.code());

        mapper.fromDMNModel(0, 0);

        assertFromDMNModelEditor(supplementaryEditor,
                                 supplementaryEditorDefinition);
    }

    @Test
    public void testFromDMNModelExpressionKindPMML() {
        this.function.setExpression(context);
        this.function.getAdditionalAttributes().put(FunctionDefinition.KIND_QNAME,
                                                    FunctionDefinition.Kind.PMML.code());

        mapper.fromDMNModel(0, 0);

        assertFromDMNModelEditor(supplementaryEditor,
                                 supplementaryEditorDefinition);
    }

    @SuppressWarnings("unchecked")
    private void assertFromDMNModelEditor(final BaseExpressionGrid editor,
                                          final ExpressionEditorDefinition definition) {
        assertTrue(uiModel.getCell(0, 0).getValue() instanceof ExpressionCellValue);
        final ExpressionCellValue dcv = (ExpressionCellValue) uiModel.getCell(0, 0).getValue();
        assertEquals(editor,
                     dcv.getValue().get());

        verify(definition).getEditor(parentCaptor.capture(),
                                     eq(Optional.empty()),
                                     eq(function),
                                     eq(Optional.ofNullable(function.getExpression())),
                                     eq(Optional.empty()),
                                     eq(1));
        final GridCellTuple parent = parentCaptor.getValue();
        assertEquals(0, parent.getRowIndex());
        assertEquals(0, parent.getColumnIndex());
        assertEquals(gridWidget, parent.getGridWidget());
    }

    @Test
    public void testToDMNModelExpressionKindFEEL() {
        cellValueSupplier = () -> Optional.of(new ExpressionCellValue(Optional.of(literalExpressionEditor)));

        mapper.toDMNModel(0,
                          0,
                          cellValueSupplier);

        assertEquals(literalExpression,
                     function.getExpression());
    }

    @Test
    public void testToDMNModelExpressionKindJavaAndPMML() {
        cellValueSupplier = () -> Optional.of(new ExpressionCellValue(Optional.of(supplementaryEditor)));

        mapper.toDMNModel(0,
                          0,
                          cellValueSupplier);

        assertEquals(context,
                     function.getExpression());
    }
}
