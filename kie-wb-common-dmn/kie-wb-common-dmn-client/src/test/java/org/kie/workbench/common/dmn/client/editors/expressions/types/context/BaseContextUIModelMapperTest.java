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

package org.kie.workbench.common.dmn.client.editors.expressions.types.context;

import java.util.Optional;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.v1_1.Context;
import org.kie.workbench.common.dmn.api.definition.v1_1.ContextEntry;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.literal.LiteralExpressionGrid;
import org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.UndefinedExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridRow;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

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
    private LiteralExpressionGrid literalExpressionEditor;

    @Mock
    private UndefinedExpressionEditorDefinition undefinedExpressionEditorDefinition;

    @Mock
    private BaseExpressionGrid undefinedExpressionEditor;

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

    @Captor
    private ArgumentCaptor<Optional<String>> nodeUUIDCaptor;

    protected BaseGridData uiModel;

    protected Context context;

    protected M mapper;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        this.uiModel = new BaseGridData();
        this.uiModel.appendRow(new DMNGridRow());
        this.uiModel.appendRow(new DMNGridRow());
        this.uiModel.appendColumn(uiRowNumberColumn);
        this.uiModel.appendColumn(uiNameColumn);
        this.uiModel.appendColumn(uiExpressionEditorColumn);
        doReturn(0).when(uiRowNumberColumn).getIndex();
        doReturn(1).when(uiNameColumn).getIndex();
        doReturn(2).when(uiExpressionEditorColumn).getIndex();

        final ExpressionEditorDefinitions expressionEditorDefinitions = new ExpressionEditorDefinitions();
        expressionEditorDefinitions.add(literalExpressionEditorDefinition);
        expressionEditorDefinitions.add(undefinedExpressionEditorDefinition);

        doReturn(expressionEditorDefinitions).when(expressionEditorDefinitionsSupplier).get();
        doReturn(Optional.of(literalExpression)).when(literalExpressionEditorDefinition).getModelClass();
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

        this.context = new Context();
        this.context.getContextEntry().add(new ContextEntry() {{
            setVariable(new InformationItem() {{
                setName(new Name("ii1"));
            }});
        }});
        this.context.getContextEntry().add(new ContextEntry() {{
            setExpression(new LiteralExpression());
        }});

        this.mapper = getMapper();
        this.cellValueSupplier = Optional::empty;
    }

    protected abstract M getMapper();

    @Test
    public void testFromDMNModelUndefinedExpression() {
        mapper.fromDMNModel(0, 2);

        assertTrue(uiModel.getCell(0, 2).getValue() instanceof ExpressionCellValue);
        final ExpressionCellValue dcv0 = (ExpressionCellValue) uiModel.getCell(0, 2).getValue();
        assertEquals(undefinedExpressionEditor,
                     dcv0.getValue().get());

        verify(undefinedExpressionEditorDefinition).getEditor(parentCaptor.capture(),
                                                              eq(Optional.empty()),
                                                              eq(context.getContextEntry().get(0)),
                                                              eq(Optional.empty()),
                                                              eq(Optional.of(context.getContextEntry().get(0).getVariable())),
                                                              eq(1));
        final GridCellTuple parent = parentCaptor.getValue();
        assertEquals(0, parent.getRowIndex());
        assertEquals(2, parent.getColumnIndex());
        assertEquals(gridWidget, parent.getGridWidget());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testFromDMNModelLiteralExpression() {
        mapper.fromDMNModel(1, 2);

        assertTrue(uiModel.getCell(1, 2).getValue() instanceof ExpressionCellValue);
        final ExpressionCellValue dcv1 = (ExpressionCellValue) uiModel.getCell(1, 2).getValue();
        assertEquals(literalExpressionEditor,
                     dcv1.getValue().get());

        verify(literalExpressionEditorDefinition).getEditor(parentCaptor.capture(),
                                                            eq(Optional.empty()),
                                                            eq(context.getContextEntry().get(1)),
                                                            eq(Optional.of(context.getContextEntry().get(1).getExpression())),
                                                            eq(Optional.empty()),
                                                            eq(1));
        final GridCellTuple parent = parentCaptor.getValue();
        assertEquals(1, parent.getRowIndex());
        assertEquals(2, parent.getColumnIndex());
        assertEquals(gridWidget, parent.getGridWidget());
    }

    @Test
    public void testToDMNModelName() {
        cellValueSupplier = () -> Optional.of(new BaseGridCellValue<>("ii2"));

        mapper.toDMNModel(0,
                          1,
                          cellValueSupplier);

        assertEquals("ii2",
                     context.getContextEntry().get(0).getVariable().getName().getValue());
    }

    @Test
    public void testToDMNModelExpression() {
        cellValueSupplier = () -> Optional.of(new ExpressionCellValue(Optional.of(literalExpressionEditor)));

        mapper.toDMNModel(0,
                          2,
                          cellValueSupplier);

        assertEquals(literalExpression,
                     context.getContextEntry().get(0).getExpression());
    }
}
