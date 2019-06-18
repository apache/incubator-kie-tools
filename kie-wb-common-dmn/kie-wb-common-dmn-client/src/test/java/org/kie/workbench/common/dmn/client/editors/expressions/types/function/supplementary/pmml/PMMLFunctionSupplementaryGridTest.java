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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.pmml;

import java.util.Optional;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpressionPMMLDocument;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpressionPMMLDocumentModel;
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
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridData;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class PMMLFunctionSupplementaryGridTest extends BaseFunctionSupplementaryGridTest<PMMLFunctionEditorDefinition> {

    @Mock
    private ExpressionEditorDefinition literalExpressionPMMLDocumentEditorDefinition;

    @Mock
    private ExpressionEditorDefinition literalExpressionPMMLDocumentModelEditorDefinition;

    @Mock
    private BaseExpressionGrid literalExpressionPMMLDocumentEditor;

    @Mock
    private BaseExpressionGrid literalExpressionPMMLDocumentModelEditor;

    protected PMMLFunctionEditorDefinition getEditorDefinition() {
        return new PMMLFunctionEditorDefinition(definitionUtils,
                                                sessionManager,
                                                sessionCommandManager,
                                                canvasCommandFactory,
                                                editorSelectedEvent,
                                                refreshFormPropertiesEvent,
                                                domainObjectSelectionEvent,
                                                listSelector,
                                                translationService,
                                                expressionEditorDefinitionsSupplier);
    }

    protected String[] getExpectedNames() {
        return new String[]{LiteralExpressionPMMLDocument.VARIABLE_DOCUMENT, LiteralExpressionPMMLDocumentModel.VARIABLE_MODEL};
    }

    @Override
    @SuppressWarnings("unchecked")
    protected BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper> getExpectedExpressionValueEditor(int uiRowIndex) {
        return uiRowIndex == 0 ? literalExpressionPMMLDocumentEditor : literalExpressionPMMLDocumentModelEditor;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void setupEditorDefinitions(final ExpressionEditorDefinitions expressionEditorDefinitions) {
        expressionEditorDefinitions.add(literalExpressionPMMLDocumentEditorDefinition);
        when(literalExpressionPMMLDocumentEditorDefinition.getModelClass()).thenReturn(Optional.of(new LiteralExpressionPMMLDocument()));
        when(literalExpressionPMMLDocumentEditorDefinition.getEditor(any(GridCellTuple.class),
                                                                     any(Optional.class),
                                                                     any(HasExpression.class),
                                                                     any(Optional.class),
                                                                     anyBoolean(),
                                                                     anyInt())).thenReturn(Optional.of(literalExpressionPMMLDocumentEditor));

        expressionEditorDefinitions.add(literalExpressionPMMLDocumentModelEditorDefinition);
        when(literalExpressionPMMLDocumentModelEditorDefinition.getModelClass()).thenReturn(Optional.of(new LiteralExpressionPMMLDocumentModel()));
        when(literalExpressionPMMLDocumentModelEditorDefinition.getEditor(any(GridCellTuple.class),
                                                                          any(Optional.class),
                                                                          any(HasExpression.class),
                                                                          any(Optional.class),
                                                                          anyBoolean(),
                                                                          anyInt())).thenReturn(Optional.of(literalExpressionPMMLDocumentModelEditor));
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
        }

        final ExpressionCellValue dcv0 = (ExpressionCellValue) uiModel.getCell(0, 2).getValue();
        assertEquals(literalExpressionPMMLDocumentEditor,
                     dcv0.getValue().get());

        final ExpressionCellValue dcv1 = (ExpressionCellValue) uiModel.getCell(1, 2).getValue();
        assertEquals(literalExpressionPMMLDocumentModelEditor,
                     dcv1.getValue().get());
    }
}
