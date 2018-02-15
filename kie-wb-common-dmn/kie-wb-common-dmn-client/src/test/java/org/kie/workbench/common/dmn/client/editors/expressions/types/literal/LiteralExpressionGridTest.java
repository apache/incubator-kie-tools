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

package org.kie.workbench.common.dmn.client.editors.expressions.types.literal;

import java.util.HashSet;
import java.util.Optional;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.Decision;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.events.ExpressionEditorSelectedEvent;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControls;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.Bounds;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.mocks.EventSourceMock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class LiteralExpressionGridTest {

    private static final String EXPRESSION_TEXT = "expression";

    @Mock
    private DMNGridPanel gridPanel;

    @Mock
    private DMNGridLayer gridLayer;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    private ClientSession session;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private EventSourceMock<ExpressionEditorSelectedEvent> editorSelectedEvent;

    @Mock
    private CellEditorControls cellEditorControls;

    @Mock
    private GridCellTuple parent;

    @Mock
    private HasExpression hasExpression;

    @Captor
    private ArgumentCaptor<ExpressionEditorSelectedEvent> expressionEditorSelectedEventCaptor;

    private LiteralExpressionGrid grid;

    @Before
    public void setup() {
        final LiteralExpressionEditorDefinition definition = new LiteralExpressionEditorDefinition(gridPanel,
                                                                                                   gridLayer,
                                                                                                   sessionManager,
                                                                                                   sessionCommandManager,
                                                                                                   editorSelectedEvent,
                                                                                                   cellEditorControls);

        final Decision decision = new Decision();
        decision.setName(new Name("name"));
        final Optional<HasName> hasName = Optional.of(decision);
        final Optional<LiteralExpression> expression = definition.getModelClass();
        expression.ifPresent(e -> e.setText(EXPRESSION_TEXT));

        doReturn(session).when(sessionManager).getCurrentSession();
        doReturn(canvasHandler).when(session).getCanvasHandler();
        doReturn(mock(Bounds.class)).when(gridLayer).getVisibleBounds();

        this.grid = spy((LiteralExpressionGrid) definition.getEditor(parent,
                                                                     hasExpression,
                                                                     expression,
                                                                     hasName,
                                                                     false).get());
    }

    @Test
    public void testInitialSetupFromDefinition() {
        final GridData uiModel = grid.getModel();
        assertThat(uiModel).isInstanceOf(DMNGridData.class);

        assertThat(uiModel.getColumnCount()).isEqualTo(1);
        assertThat(uiModel.getColumns().get(0)).isInstanceOf(LiteralExpressionColumn.class);

        assertThat(uiModel.getRowCount()).isEqualTo(1);

        assertThat(uiModel.getCell(0, 0).getValue().getValue()).isEqualTo(EXPRESSION_TEXT);
    }

    @Test
    public void testGetEditorControls() {
        assertThat(grid.getEditorControls()).isEmpty();
    }

    @Test
    public void testFireExpressionEditorSelectedEventSelectsParentGridWidget() {
        final GridData mockParentUiModel = mock(GridData.class);
        final BaseExpressionGrid mockParentGrid = mock(BaseExpressionGrid.class);
        doReturn(mockParentUiModel).when(parent).getGridData();
        doReturn(mockParentUiModel).when(mockParentGrid).getModel();
        doReturn(new HashSet<GridWidget>() {{
            add(mockParentGrid);
            add(grid);
        }}).when(gridLayer).getGridWidgets();

        grid.select();

        verify(editorSelectedEvent).fire(expressionEditorSelectedEventCaptor.capture());

        final ExpressionEditorSelectedEvent event = expressionEditorSelectedEventCaptor.getValue();
        assertThat(event.getEditor()).isPresent();
        assertThat(mockParentGrid).isSameAs(event.getEditor().get());
    }
}
