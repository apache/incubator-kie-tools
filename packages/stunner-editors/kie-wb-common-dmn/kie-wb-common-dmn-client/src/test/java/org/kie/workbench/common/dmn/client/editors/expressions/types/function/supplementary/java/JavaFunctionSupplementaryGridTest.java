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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.java;

import java.util.Optional;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ContextGridRowNumberColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionCellValue;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionEditorColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.InformationItemCell;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.BaseFunctionSupplementaryGridTest;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.FunctionSupplementaryGridData;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.NameColumn;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.model.BaseUIModelMapper;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.stunner.core.client.ReadOnlyProvider;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridData;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class JavaFunctionSupplementaryGridTest extends BaseFunctionSupplementaryGridTest<JavaFunctionEditorDefinition> {

    @Mock
    private ExpressionEditorDefinition literalExpressionEditorDefinition;

    @Mock
    private BaseExpressionGrid literalExpressionEditor;

    @Mock
    private ReadOnlyProvider readOnlyProvider;

    protected JavaFunctionEditorDefinition getEditorDefinition() {
        return new JavaFunctionEditorDefinition(definitionUtils,
                                                sessionManager,
                                                sessionCommandManager,
                                                canvasCommandFactory,
                                                editorSelectedEvent,
                                                refreshFormPropertiesEvent,
                                                domainObjectSelectionEvent,
                                                listSelector,
                                                translationService,
                                                expressionEditorDefinitionsSupplier,
                                                readOnlyProvider);
    }

    protected String[] getExpectedNames() {
        return new String[]{JavaFunctionEditorDefinition.VARIABLE_CLASS, JavaFunctionEditorDefinition.VARIABLE_METHOD_SIGNATURE};
    }

    @Override
    @SuppressWarnings("unchecked")
    protected BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper> getExpectedExpressionValueEditor(int uiRowIndex) {
        return literalExpressionEditor;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void setupEditorDefinitions(final ExpressionEditorDefinitions expressionEditorDefinitions) {
        expressionEditorDefinitions.add(literalExpressionEditorDefinition);
        when(literalExpressionEditorDefinition.getModelClass()).thenReturn(Optional.of(new LiteralExpression()));
        when(literalExpressionEditorDefinition.getEditor(any(GridCellTuple.class),
                                                         any(Optional.class),
                                                         any(HasExpression.class),
                                                         any(Optional.class),
                                                         anyBoolean(),
                                                         anyInt())).thenReturn(Optional.of(literalExpressionEditor));
    }

    @Test
    public void testInitialSetupFromDefinition() {
        setupGrid(0);

        final GridData uiModel = grid.getModel();
        assertTrue(uiModel instanceof FunctionSupplementaryGridData);

        assertEquals(3,
                     uiModel.getColumnCount());
        assertTrue(uiModel.getColumns().get(0) instanceof ContextGridRowNumberColumn);
        assertTrue(uiModel.getColumns().get(1) instanceof NameColumn);
        assertTrue(uiModel.getColumns().get(2) instanceof ExpressionEditorColumn);

        assertEquals(2,
                     uiModel.getRowCount());

        final String[] expectedNames = getExpectedNames();
        for (int i = 0; i < uiModel.getRowCount(); i++) {
            assertEquals(i + 1,
                         uiModel.getCell(i, 0).getValue().getValue());
            assertEquals(expectedNames[i],
                         ((InformationItemCell.HasNameAndDataTypeCell) uiModel.getCell(i, 1).getValue().getValue()).getName().getValue());
            assertTrue(uiModel.getCell(i, 2).getValue() instanceof ExpressionCellValue);
            final ExpressionCellValue dcv = (ExpressionCellValue) uiModel.getCell(i, 2).getValue();
            assertEquals(literalExpressionEditor,
                         dcv.getValue().get());
        }
    }
}
