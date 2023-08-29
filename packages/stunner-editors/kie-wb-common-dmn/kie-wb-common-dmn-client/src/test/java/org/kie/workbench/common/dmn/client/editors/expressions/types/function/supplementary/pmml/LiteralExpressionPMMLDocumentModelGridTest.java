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
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpressionPMMLDocument;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpressionPMMLDocumentModel;
import org.kie.workbench.common.dmn.client.commands.expressions.types.function.SetParametersCommand;
import org.kie.workbench.common.dmn.client.commands.general.SetCellValueCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.types.BaseEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.GridFactoryCommandUtils;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.FunctionGrid;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.pmml.model.LiteralExpressionPMMLDocumentModelEditorDefinition;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.stunner.core.client.ReadOnlyProvider;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class LiteralExpressionPMMLDocumentModelGridTest extends BaseLiteralExpressionPMMLGridTest {

    private static final String DOCUMENT_NAME = "document";

    @Mock
    private FunctionGrid grandParentFunctionGridWidget;

    @Mock
    private FunctionDefinition grandParentExpression;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private GraphCommandExecutionContext graphCommandExecutionContext;

    @Mock
    private ReadOnlyProvider readOnlyProvider;

    @Before
    public void setup() {
        final GridCellTuple grandParentInformation = new GridCellTuple(0, 0, grandParentFunctionGridWidget);
        when(parentGridWidget.getExpressionValueEditor(LiteralExpressionPMMLDocument.VARIABLE_DOCUMENT)).thenReturn(Optional.of(pmmlValueEditor));
        when(parentGridWidget.getExpressionValue(LiteralExpressionPMMLDocument.VARIABLE_DOCUMENT)).thenReturn(DOCUMENT_NAME);
        when(parentGridWidget.getParentInformation()).thenReturn(grandParentInformation);
        when(grandParentFunctionGridWidget.getExpression()).thenReturn(() -> Optional.of(grandParentExpression));
        when(grandParentExpression.getFormalParameter()).thenCallRealMethod();
        when(canvasHandler.getGraphExecutionContext()).thenReturn(graphCommandExecutionContext);

        super.setup();
    }

    @Override
    protected BaseEditorDefinition<LiteralExpressionPMMLDocumentModel, DMNGridData> getDefinition() {
        return new LiteralExpressionPMMLDocumentModelEditorDefinition(definitionUtils,
                                                                      sessionManager,
                                                                      sessionCommandManager,
                                                                      canvasCommandFactory,
                                                                      editorSelectedEvent,
                                                                      refreshFormPropertiesEvent,
                                                                      domainObjectSelectionEvent,
                                                                      listSelector,
                                                                      translationService,
                                                                      headerEditor,
                                                                      pmmlDocumentMetadataProvider,
                                                                      readOnlyProvider);
    }

    @Override
    protected GridWidget getParentGridWidget() {
        return parentGridWidget;
    }

    @Test
    public void testGetPlaceHolder() {
        setupGrid(0);

        assertThat(grid.getPlaceHolder()).isEqualTo(DMNEditorConstants.LiteralExpressionPMMLDocumentModelEditorDefinition_Placeholder);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLoadValues() {
        setupGrid(0);

        final List<String> modelNames = mock(List.class);
        final Consumer<List<String>> consumer = mock(Consumer.class);

        when(pmmlDocumentMetadataProvider.getPMMLDocumentModels(DOCUMENT_NAME)).thenReturn(modelNames);

        grid.loadValues(consumer);

        verify(pmmlDocumentMetadataProvider).getPMMLDocumentModels(eq(DOCUMENT_NAME));
        verify(consumer).accept(modelNames);
    }

    @Test
    public void testNewCellHasValueCommand() {
        setupGrid(0);

        final List<String> parameterNames = asList("param1", "param2");
        final String modelName = (String) tupleWithValue.getValue().getValue();
        when(pmmlDocumentMetadataProvider.getPMMLDocumentModelParameterNames(eq(DOCUMENT_NAME), eq(modelName))).thenReturn(parameterNames);

        final Command command = grid.newCellHasValueCommand().apply(tupleWithValue);

        verify(pmmlDocumentMetadataProvider).getPMMLDocumentModelParameterNames(eq(DOCUMENT_NAME),
                                                                                eq(modelName));

        GridFactoryCommandUtils.assertCommands(command,
                                               SetCellValueCommand.class,
                                               SetParametersCommand.class);

        final SetParametersCommand setParametersCommand = (SetParametersCommand) ((CompositeCommand) command).getCommands().get(1);
        setParametersCommand.execute(canvasHandler);

        assertThat(grandParentExpression.getFormalParameter().stream().map(ii -> ii.getName().getValue()).collect(Collectors.toList())).containsExactly("param1", "param2");
    }
}
