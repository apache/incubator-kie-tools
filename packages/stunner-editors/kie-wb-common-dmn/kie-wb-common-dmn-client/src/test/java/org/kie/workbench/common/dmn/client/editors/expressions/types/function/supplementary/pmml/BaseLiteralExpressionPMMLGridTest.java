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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.pmml;

import java.util.List;
import java.util.Optional;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.commands.general.DeleteCellValueCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.FunctionSupplementaryGrid;
import org.kie.workbench.common.dmn.client.editors.expressions.types.literal.BaseLiteralExpressionGridTest;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.TextAreaSingletonDOMElementFactory;
import org.kie.workbench.common.dmn.client.widgets.grid.handlers.DelegatingGridWidgetCellSelectorMouseEventHandler;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellValueTuple;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.NodeMouseEventHandler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

@RunWith(LienzoMockitoTestRunner.class)
public abstract class BaseLiteralExpressionPMMLGridTest extends BaseLiteralExpressionGridTest<LiteralExpressionPMMLGrid> {

    @Mock
    protected PMMLDocumentMetadataProvider pmmlDocumentMetadataProvider;

    @Mock
    protected FunctionSupplementaryGrid parentGridWidget;

    @Mock
    protected LiteralExpressionPMMLGrid pmmlValueEditor;

    @Captor
    protected ArgumentCaptor<GridCellValueTuple<GridCellValue<String>>> pmmlValueEditorCellHasValueCommandParameterCaptor;

    @Override
    protected void setupGrid(final int nesting) {
        this.hasExpression.setExpression(expression.get());
        this.grid = spy((LiteralExpressionPMMLGrid) definition.getEditor(parent,
                                                                         nesting == 0 ? Optional.of(NODE_UUID) : Optional.empty(),
                                                                         hasExpression,
                                                                         hasName,
                                                                         false,
                                                                         nesting).get());
    }

    @Test
    public void testMouseClickEventHandlers() {
        setupGrid(0);

        final List<NodeMouseEventHandler> handlers = grid.getNodeMouseClickEventHandlers(selectionManager);
        assertThat(handlers).hasSize(1);
        assertThat(handlers.get(0)).isInstanceOf(DelegatingGridWidgetCellSelectorMouseEventHandler.class);
    }

    @Test
    public void testInitialSetupFromDefinition() {
        setupGrid(0);

        final GridData uiModel = grid.getModel();
        assertThat(uiModel).isInstanceOf(DMNGridData.class);

        assertThat(uiModel.getColumnCount()).isEqualTo(1);
        assertThat(uiModel.getColumns().get(0)).isInstanceOf(LiteralExpressionPMMLColumn.class);

        assertThat(uiModel.getRowCount()).isEqualTo(1);

        assertThat(uiModel.getCell(0, 0).getValue().getValue()).isEqualTo(EXPRESSION_TEXT);
    }

    @Test
    public void testBodyFactory() {
        setupGrid(1);

        final TextAreaSingletonDOMElementFactory factory = grid.getBodyTextAreaFactory();
        assertThat(factory.getHasNoValueCommand().apply(tupleWithoutValue)).isInstanceOf(DeleteCellValueCommand.class);
        assertThat(factory.getHasValueCommand().apply(tupleWithValue)).isInstanceOf(CompositeCommand.class);
    }
}
