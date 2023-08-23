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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.model.Context;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.IsLiteralExpression;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ContextGridRowNumberColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionEditorColumn;
import org.kie.workbench.common.dmn.client.session.DMNSession;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.BaseUIModelMapper;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridColumn;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public abstract class BaseFunctionSupplementaryGridTest<D extends ExpressionEditorDefinition<Context>> {

    @Mock
    protected DMNGridPanel gridPanel;

    @Mock
    protected DMNGridLayer gridLayer;

    @Mock
    protected DefinitionUtils definitionUtils;

    @Mock
    protected SessionManager sessionManager;

    @Mock
    protected DMNSession session;

    @Mock
    protected SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    protected DefaultCanvasCommandFactory canvasCommandFactory;

    @Mock
    protected CellEditorControlsView.Presenter cellEditorControls;

    @Mock
    protected ListSelectorView.Presenter listSelector;

    @Mock
    protected TranslationService translationService;

    @Mock
    protected Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;

    @Mock
    protected EventSourceMock<ExpressionEditorChanged> editorSelectedEvent;

    @Mock
    protected EventSourceMock<RefreshFormPropertiesEvent> refreshFormPropertiesEvent;

    @Mock
    protected EventSourceMock<DomainObjectSelectionEvent> domainObjectSelectionEvent;

    @Mock
    private GridCellTuple parent;

    @Mock
    private HasExpression hasExpression;

    private Optional<Context> expression = Optional.empty();

    private Optional<HasName> hasName = Optional.empty();

    private D definition;

    protected FunctionSupplementaryGrid grid;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        when(sessionManager.getCurrentSession()).thenReturn(session);
        when(session.getGridPanel()).thenReturn(gridPanel);
        when(session.getGridLayer()).thenReturn(gridLayer);
        when(session.getCellEditorControls()).thenReturn(cellEditorControls);

        definition = getEditorDefinition();

        expression = definition.getModelClass();
        definition.enrich(Optional.empty(), hasExpression, expression);
        final ExpressionEditorDefinitions expressionEditorDefinitions = new ExpressionEditorDefinitions();
        expressionEditorDefinitions.add((ExpressionEditorDefinition) definition);

        when(expressionEditorDefinitionsSupplier.get()).thenReturn(expressionEditorDefinitions);

        setupEditorDefinitions(expressionEditorDefinitions);
    }

    protected void setupGrid(final int nesting) {
        when(hasExpression.getExpression()).thenReturn(expression.get());
        this.grid = (FunctionSupplementaryGrid) definition.getEditor(parent,
                                                                     Optional.empty(),
                                                                     hasExpression,
                                                                     hasName,
                                                                     false,
                                                                     nesting).get();
    }

    protected abstract D getEditorDefinition();

    protected abstract String[] getExpectedNames();

    protected abstract void setupEditorDefinitions(final ExpressionEditorDefinitions expressionEditorDefinitions);

    protected abstract BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper> getExpectedExpressionValueEditor(final int uiRowIndex);

    @Test
    public void testInitialColumnWidthsFromDefinition() {
        setupGrid(0);

        assertComponentWidths(ContextGridRowNumberColumn.DEFAULT_WIDTH,
                              DMNGridColumn.DEFAULT_WIDTH,
                              ExpressionEditorColumn.DEFAULT_WIDTH);
    }

    @Test
    public void testInitialColumnWidthsFromExpression() {
        final List<Double> componentWidths = expression.get().getComponentWidths();
        componentWidths.set(0, 100.0);
        componentWidths.set(1, 200.0);
        componentWidths.set(2, 300.0);

        setupGrid(0);

        assertComponentWidths(100.0,
                              200.0,
                              300.0);
    }

    private void assertComponentWidths(final double... widths) {
        final GridData uiModel = grid.getModel();
        IntStream.range(0, widths.length).forEach(i -> assertEquals(widths[i], uiModel.getColumns().get(i).getWidth(), 0.0));
    }

    @Test
    public void testCacheable() {
        setupGrid(0);

        assertTrue(grid.isCacheable());
    }

    @Test
    public void testGetExpressionValue() {
        setupGrid(0);

        final String[] expectedNames = getExpectedNames();
        for (int uiRowIndex = 0; uiRowIndex < expectedNames.length; uiRowIndex++) {
            final String value = expectedNames[uiRowIndex] + "-value";
            ((IsLiteralExpression) expression.get().getContextEntry().get(uiRowIndex).getExpression()).getText().setValue(value);
            assertEquals(value, grid.getExpressionValue(getExpectedNames()[uiRowIndex]));
        }
    }

    @Test
    public void testGetExpressionValueQuoteRemoval() {
        setupGrid(0);

        final String[] expectedNames = getExpectedNames();
        for (int uiRowIndex = 0; uiRowIndex < expectedNames.length; uiRowIndex++) {
            final String value = expectedNames[uiRowIndex] + "-value";
            ((IsLiteralExpression) expression.get().getContextEntry().get(uiRowIndex).getExpression()).getText().setValue("\"" + value + "\"");
            assertEquals(value, grid.getExpressionValue(getExpectedNames()[uiRowIndex]));
        }
    }

    @Test
    public void testGetExpressionValueEditor() {
        setupGrid(0);

        final String[] expectedNames = getExpectedNames();
        for (int uiRowIndex = 0; uiRowIndex < expectedNames.length; uiRowIndex++) {
            final Optional<BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper>> valueEditor = grid.getExpressionValueEditor(getExpectedNames()[uiRowIndex]);
            assertTrue(valueEditor.isPresent());
            assertEquals(getExpectedExpressionValueEditor(uiRowIndex), valueEditor.get());
        }
    }
}
