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

package org.kie.workbench.common.dmn.client.editors.expressions.types.context;

import java.util.Optional;
import java.util.function.Supplier;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.model.Context;
import org.kie.workbench.common.dmn.api.definition.model.ContextEntry;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.literal.LiteralExpressionGrid;
import org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.UndefinedExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public abstract class BaseContextUIModelMapperTest<M extends ContextUIModelMapper> {

    @Mock
    private RowNumberColumn uiRowNumberColumn;

    @Mock
    private NameColumn uiNameColumn;

    @Mock
    private ExpressionEditorColumn uiExpressionEditorColumn;

    @Mock
    private ExpressionEditorDefinition literalExpressionEditorDefinition;

    @Mock
    protected LiteralExpressionGrid literalExpressionEditor;

    @Mock
    private UndefinedExpressionEditorDefinition undefinedExpressionEditorDefinition;

    @Mock
    protected BaseExpressionGrid undefinedExpressionEditor;

    private LiteralExpression literalExpression = new LiteralExpression();

    private Supplier<Optional<GridCellValue<?>>> cellValueSupplier;

    @Mock
    protected Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;

    @Mock
    protected ListSelectorView.Presenter listSelector;

    @Mock
    protected GridWidget gridWidget;

    @Captor
    private ArgumentCaptor<GridCellTuple> parentCaptor;

    protected BaseGridData uiModel;

    protected Context context;

    protected M mapper;

    @SuppressWarnings("unchecked")
    public void setup(final boolean isOnlyVisualChangeAllowedSupplier) {
        this.uiModel = new BaseGridData();
        this.uiModel.appendRow(new BaseGridRow());
        this.uiModel.appendRow(new BaseGridRow());
        this.uiModel.appendColumn(uiRowNumberColumn);
        this.uiModel.appendColumn(uiNameColumn);
        this.uiModel.appendColumn(uiExpressionEditorColumn);
        when(uiRowNumberColumn.getIndex()).thenReturn(0);
        when(uiNameColumn.getIndex()).thenReturn(1);
        when(uiExpressionEditorColumn.getIndex()).thenReturn(2);

        final ExpressionEditorDefinitions expressionEditorDefinitions = new ExpressionEditorDefinitions();
        expressionEditorDefinitions.add(literalExpressionEditorDefinition);
        expressionEditorDefinitions.add(undefinedExpressionEditorDefinition);

        when(expressionEditorDefinitionsSupplier.get()).thenReturn(expressionEditorDefinitions);
        when(literalExpressionEditorDefinition.getModelClass()).thenReturn(Optional.of(literalExpression));
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

        this.context = new Context();
        this.context.getContextEntry().add(new ContextEntry() {{
            setVariable(new InformationItem() {{
                setName(new Name("ii1"));
            }});
        }});
        this.context.getContextEntry().add(new ContextEntry() {{
            setExpression(new LiteralExpression());
        }});

        this.mapper = getMapper(isOnlyVisualChangeAllowedSupplier);
        this.cellValueSupplier = Optional::empty;
    }

    protected abstract M getMapper(final boolean isOnlyVisualChangeAllowedSupplier);

    @Test
    public void testFromDMNModelUndefinedExpression() {
        setup(false);

        mapper.fromDMNModel(0, 2);

        assertFromDMNModelUndefinedExpression(false);
    }

    @Test
    public void testFromDMNModelUndefinedExpressionWhenOnlyVisualChangeAllowed() {
        setup(true);

        mapper.fromDMNModel(0, 2);

        assertFromDMNModelUndefinedExpression(true);
    }

    private void assertFromDMNModelUndefinedExpression(final boolean isOnlyVisualChangeAllowed) {
        assertTrue(uiModel.getCell(0, 2).getValue() instanceof ExpressionCellValue);
        final ExpressionCellValue dcv0 = (ExpressionCellValue) uiModel.getCell(0, 2).getValue();
        assertEquals(undefinedExpressionEditor,
                     dcv0.getValue().get());

        verify(undefinedExpressionEditorDefinition).getEditor(parentCaptor.capture(),
                                                              eq(Optional.empty()),
                                                              eq(context.getContextEntry().get(0)),
                                                              eq(Optional.of(context.getContextEntry().get(0).getVariable())),
                                                              eq(isOnlyVisualChangeAllowed),
                                                              eq(1));
        final GridCellTuple parent = parentCaptor.getValue();
        assertEquals(0, parent.getRowIndex());
        assertEquals(2, parent.getColumnIndex());
        assertEquals(gridWidget, parent.getGridWidget());
    }

    @Test
    public void testFromDMNModelLiteralExpression() {
        setup(false);

        mapper.fromDMNModel(1, 2);

        assertFromDMNModelLiteralExpression(false);
    }

    @Test
    public void testFromDMNModelLiteralExpressionWhenOnlyVisualChangeAllowed() {
        setup(true);

        mapper.fromDMNModel(1, 2);

        assertFromDMNModelLiteralExpression(true);
    }

    private void assertFromDMNModelLiteralExpression(final boolean isOnlyVisualChangeAllowed) {
        assertTrue(uiModel.getCell(1, 2).getValue() instanceof ExpressionCellValue);
        final ExpressionCellValue dcv1 = (ExpressionCellValue) uiModel.getCell(1, 2).getValue();
        assertEquals(literalExpressionEditor,
                     dcv1.getValue().get());

        verify(literalExpressionEditorDefinition).getEditor(parentCaptor.capture(),
                                                            eq(Optional.empty()),
                                                            eq(context.getContextEntry().get(1)),
                                                            eq(Optional.empty()),
                                                            eq(isOnlyVisualChangeAllowed),
                                                            eq(1));
        final GridCellTuple parent = parentCaptor.getValue();
        assertEquals(1, parent.getRowIndex());
        assertEquals(2, parent.getColumnIndex());
        assertEquals(gridWidget, parent.getGridWidget());
    }

    @Test
    public void testToDMNModelName() {
        setup(false);

        cellValueSupplier = () -> Optional.of(new BaseGridCellValue<>("ii2"));

        mapper.toDMNModel(0,
                          1,
                          cellValueSupplier);

        assertEquals("ii2",
                     context.getContextEntry().get(0).getVariable().getName().getValue());
    }

    @Test
    public void testToDMNModelExpression() {
        setup(false);

        cellValueSupplier = () -> Optional.of(new ExpressionCellValue(Optional.of(literalExpressionEditor)));

        mapper.toDMNModel(0,
                          2,
                          cellValueSupplier);

        assertEquals(literalExpression,
                     context.getContextEntry().get(0).getExpression());
    }
}
