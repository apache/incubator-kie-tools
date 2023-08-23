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
import java.util.function.Function;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpressionPMMLDocument;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpressionPMMLDocumentModel;
import org.kie.workbench.common.dmn.client.commands.general.SetCellValueCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.types.BaseEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.GridFactoryCommandUtils;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.pmml.document.LiteralExpressionPMMLDocumentEditorDefinition;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellValueTuple;
import org.kie.workbench.common.stunner.core.client.ReadOnlyProvider;
import org.kie.workbench.common.stunner.core.command.Command;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class LiteralExpressionPMMLDocumentGridTest extends BaseLiteralExpressionPMMLGridTest {

    @Mock
    private Function<GridCellValueTuple, Command> pmmlDocumentModelEditorCellHasValueCommandProvider;

    @Mock
    private SetCellValueCommand pmmlDocumentModelEditorCommand;

    @Mock
    private ReadOnlyProvider readOnlyProvider;

    @Override
    protected BaseEditorDefinition<LiteralExpressionPMMLDocument, DMNGridData> getDefinition() {
        return new LiteralExpressionPMMLDocumentEditorDefinition(definitionUtils,
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
        when(parentGridWidget.getExpressionValueEditor(LiteralExpressionPMMLDocumentModel.VARIABLE_MODEL)).thenReturn(Optional.of(pmmlValueEditor));
        when(pmmlValueEditor.newCellHasValueCommand()).thenReturn(pmmlDocumentModelEditorCellHasValueCommandProvider);
        when(pmmlDocumentModelEditorCellHasValueCommandProvider.apply(any())).thenReturn(pmmlDocumentModelEditorCommand);

        return parentGridWidget;
    }

    @Test
    public void testGetPlaceHolder() {
        setupGrid(0);

        assertThat(grid.getPlaceHolder()).isEqualTo(DMNEditorConstants.LiteralExpressionPMMLDocumentEditorDefinition_Placeholder);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLoadValues() {
        setupGrid(0);

        final List<String> documentNames = mock(List.class);
        final Consumer<List<String>> consumer = mock(Consumer.class);

        when(pmmlDocumentMetadataProvider.getPMMLDocumentNames()).thenReturn(documentNames);

        grid.loadValues(consumer);

        verify(pmmlDocumentMetadataProvider).getPMMLDocumentNames();
        verify(consumer).accept(documentNames);
    }

    @Test
    public void testNewCellHasValueCommand() {
        setupGrid(0);

        final Command command = grid.newCellHasValueCommand().apply(tupleWithValue);

        GridFactoryCommandUtils.assertCommands(command,
                                               SetCellValueCommand.class,
                                               SetCellValueCommand.class);

        verify(pmmlDocumentModelEditorCellHasValueCommandProvider).apply(pmmlValueEditorCellHasValueCommandParameterCaptor.capture());

        final GridCellValueTuple<GridCellValue<String>> gcvt = pmmlValueEditorCellHasValueCommandParameterCaptor.getValue();
        assertThat(gcvt.getRowIndex()).isEqualTo(0);
        assertThat(gcvt.getColumnIndex()).isEqualTo(0);
        assertThat(gcvt.getGridWidget()).isEqualTo(pmmlValueEditor);

        final GridCellValue<String> gcv = gcvt.getValue();
        assertThat(gcv).isNotNull();
        assertThat(gcv.getValue()).isEqualTo("");
        assertThat(gcv.getPlaceHolder()).isEqualTo(DMNEditorConstants.LiteralExpressionPMMLDocumentModelEditorDefinition_Placeholder);
    }
}
