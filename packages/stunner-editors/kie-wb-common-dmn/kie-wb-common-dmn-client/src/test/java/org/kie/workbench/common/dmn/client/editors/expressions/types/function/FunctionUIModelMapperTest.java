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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function;

import java.util.Optional;
import java.util.function.Supplier;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.model.Context;
import org.kie.workbench.common.dmn.api.definition.model.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionCellValue;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionEditorColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.FunctionSupplementaryGrid;
import org.kie.workbench.common.dmn.client.editors.expressions.types.literal.LiteralExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
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

    @SuppressWarnings("unchecked")
    public void setup(final boolean isOnlyVisualChangeAllowedSupplier) {
        this.uiModel = new BaseGridData();
        this.uiModel.appendRow(new BaseGridRow());
        this.uiModel.appendRow(new BaseGridRow());
        this.uiModel.appendColumn(uiExpressionEditorColumn);
        when(uiExpressionEditorColumn.getIndex()).thenReturn(0);
        when(gridWidget.getModel()).thenReturn(uiModel);

        //Core Editor definitions
        final ExpressionEditorDefinitions expressionEditorDefinitions = new ExpressionEditorDefinitions();
        expressionEditorDefinitions.add(literalExpressionEditorDefinition);

        when(expressionEditorDefinitionsSupplier.get()).thenReturn(expressionEditorDefinitions);
        when(literalExpressionEditorDefinition.getModelClass()).thenReturn(Optional.of(literalExpression));
        when(literalExpressionEditor.getExpression()).thenReturn(() -> Optional.of(literalExpression));
        when(literalExpressionEditorDefinition.getEditor(any(GridCellTuple.class),
                                                         any(Optional.class),
                                                         any(HasExpression.class),
                                                         any(Optional.class),
                                                         anyBoolean(),
                                                         anyInt())).thenReturn(Optional.of(literalExpressionEditor));

        //Supplementary Editor definitions
        final ExpressionEditorDefinitions supplementaryEditorDefinitions = new ExpressionEditorDefinitions();
        supplementaryEditorDefinitions.add(supplementaryEditorDefinition);

        when(supplementaryEditorDefinitionsSupplier.get()).thenReturn(supplementaryEditorDefinitions);
        when(supplementaryEditorDefinition.getModelClass()).thenReturn(Optional.of(context));
        when(supplementaryEditor.getExpression()).thenReturn(() -> Optional.of(context));
        when(supplementaryEditorDefinition.getEditor(any(GridCellTuple.class),
                                                     any(Optional.class),
                                                     any(HasExpression.class),
                                                     any(Optional.class),
                                                     anyBoolean(),
                                                     anyInt())).thenReturn(Optional.of(supplementaryEditor));

        this.function = new FunctionDefinition();

        this.mapper = new FunctionUIModelMapper(gridWidget,
                                                () -> uiModel,
                                                () -> Optional.of(function),
                                                () -> isOnlyVisualChangeAllowedSupplier,
                                                expressionEditorDefinitionsSupplier,
                                                supplementaryEditorDefinitionsSupplier,
                                                listSelector,
                                                1);
        this.cellValueSupplier = Optional::empty;
    }

    @Test
    public void testFromDMNModelExpressionKindFEEL() {
        setup(false);

        this.function.setExpression(literalExpression);
        this.function.setKind(FunctionDefinition.Kind.FEEL);

        mapper.fromDMNModel(0, 0);

        assertFromDMNModelEditor(literalExpressionEditor,
                                 literalExpressionEditorDefinition,
                                 false);
    }

    @Test
    public void testFromDMNModelExpressionKindJava() {
        setup(false);

        this.function.setExpression(context);
        this.function.setKind(FunctionDefinition.Kind.JAVA);

        mapper.fromDMNModel(0, 0);

        assertFromDMNModelEditor(supplementaryEditor,
                                 supplementaryEditorDefinition,
                                 false);
    }

    @Test
    public void testFromDMNModelExpressionKindPMML() {
        setup(false);

        this.function.setExpression(context);
        this.function.setKind(FunctionDefinition.Kind.PMML);

        mapper.fromDMNModel(0, 0);

        assertFromDMNModelEditor(supplementaryEditor,
                                 supplementaryEditorDefinition,
                                 false);
    }

    @Test
    public void testFromDMNModelExpressionKindFEELWhenOnlyVisualChangeAllowed() {
        setup(true);

        this.function.setExpression(literalExpression);
        this.function.setKind(FunctionDefinition.Kind.FEEL);

        mapper.fromDMNModel(0, 0);

        assertFromDMNModelEditor(literalExpressionEditor,
                                 literalExpressionEditorDefinition,
                                 true);
    }

    @Test
    public void testFromDMNModelExpressionKindJavaWhenOnlyVisualChangeAllowed() {
        setup(true);

        this.function.setExpression(context);
        this.function.setKind(FunctionDefinition.Kind.JAVA);

        mapper.fromDMNModel(0, 0);

        assertFromDMNModelEditor(supplementaryEditor,
                                 supplementaryEditorDefinition,
                                 true);
    }

    @Test
    public void testFromDMNModelExpressionKindPMMLWhenOnlyVisualChangeAllowed() {
        setup(true);

        this.function.setExpression(context);
        this.function.setKind(FunctionDefinition.Kind.PMML);

        mapper.fromDMNModel(0, 0);

        assertFromDMNModelEditor(supplementaryEditor,
                                 supplementaryEditorDefinition,
                                 true);
    }

    @SuppressWarnings("unchecked")
    private void assertFromDMNModelEditor(final BaseExpressionGrid editor,
                                          final ExpressionEditorDefinition definition,
                                          final boolean isOnlyVisualChangeAllowedSupplier) {
        assertTrue(uiModel.getCell(0, 0).getValue() instanceof ExpressionCellValue);
        final ExpressionCellValue dcv = (ExpressionCellValue) uiModel.getCell(0, 0).getValue();
        assertEquals(editor,
                     dcv.getValue().get());

        verify(definition).getEditor(parentCaptor.capture(),
                                     eq(Optional.empty()),
                                     eq(function),
                                     eq(Optional.empty()),
                                     eq(isOnlyVisualChangeAllowedSupplier),
                                     eq(1));
        final GridCellTuple parent = parentCaptor.getValue();
        assertEquals(0, parent.getRowIndex());
        assertEquals(0, parent.getColumnIndex());
        assertEquals(gridWidget, parent.getGridWidget());
    }

    @Test
    public void testToDMNModelExpressionKindFEEL() {
        setup(false);

        cellValueSupplier = () -> Optional.of(new ExpressionCellValue(Optional.of(literalExpressionEditor)));

        mapper.toDMNModel(0,
                          0,
                          cellValueSupplier);

        assertEquals(literalExpression,
                     function.getExpression());
    }

    @Test
    public void testToDMNModelExpressionKindJavaAndPMML() {
        setup(false);

        cellValueSupplier = () -> Optional.of(new ExpressionCellValue(Optional.of(supplementaryEditor)));

        mapper.toDMNModel(0,
                          0,
                          cellValueSupplier);

        assertEquals(context,
                     function.getExpression());
    }
}
