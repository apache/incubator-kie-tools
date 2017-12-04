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

package org.kie.workbench.common.dmn.client.editors.expressions.types.relation;

import java.util.Optional;

import javax.enterprise.event.Event;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.api.definition.v1_1.List;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.v1_1.Relation;
import org.kie.workbench.common.dmn.client.commands.expressions.types.relation.AddRelationColumnCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.relation.AddRelationRowCommand;
import org.kie.workbench.common.dmn.client.events.ExpressionEditorSelectedEvent;
import org.kie.workbench.common.dmn.client.session.DMNClientFullSession;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class RelationGridTest {

    @Mock
    private GridCellTuple parent;

    @Mock
    private HasExpression hasExpression;

    private Relation relation = new Relation();

    private Optional<Relation> expression = Optional.of(relation);

    private Optional<HasName> hasName;

    @Mock
    private DMNGridPanel gridPanel;

    @Mock
    private DMNGridLayer gridLayer;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private DMNClientFullSession dmnClientFullSession;

    @Mock
    private AbstractCanvasHandler abstractCanvasHandler;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    private Event<ExpressionEditorSelectedEvent> editorSelectedEvent;

    @Mock
    private RelationGridControls controls;

    @Captor
    private ArgumentCaptor<AddRelationColumnCommand> addColumnCommand;

    @Captor
    private ArgumentCaptor<AddRelationRowCommand> addRowCommand;

    private RelationGrid relationGrid;

    @Before
    public void setUp() throws Exception {
        editorSelectedEvent = new EventSourceMock<>();
        doReturn(abstractCanvasHandler).when(dmnClientFullSession).getCanvasHandler();
        doReturn(dmnClientFullSession).when(sessionManager).getCurrentSession();
    }

    @Test
    public void testInitialiseUiColumnsEmptyModel() throws Exception {
        relationGrid = new RelationGrid(parent, hasExpression, expression, hasName, gridPanel, gridLayer, sessionManager,
                                        sessionCommandManager, editorSelectedEvent, controls);

        assertEquals(0, relationGrid.getModel().getRowCount());
        assertEquals(1, relationGrid.getModel().getColumns().size());
        assertTrue(relationGrid.getModel().getColumns().get(0) instanceof RowNumberColumn);
    }

    @Test
    public void testInitialiseUiColumns() throws Exception {
        final String columnHeader = "first column";
        relation.getColumn().add(new InformationItem() {{
            getName().setValue(columnHeader);
        }});
        relationGrid = new RelationGrid(parent, hasExpression, expression, hasName, gridPanel, gridLayer, sessionManager,
                                        sessionCommandManager, editorSelectedEvent, controls);

        assertEquals(2, relationGrid.getModel().getColumns().size());
        assertTrue(relationGrid.getModel().getColumns().get(0) instanceof RowNumberColumn);
        assertEquals(columnHeader, relationGrid.getModel().getColumns().get(1).getHeaderMetaData().get(0).getTitle());
    }

    @Test
    public void testInitialiseUiModel() throws Exception {
        relation.getColumn().add(new InformationItem() {{
            getName().setValue("first column header");
        }});
        final String firstRowValue = "first column value 1";
        final String secondRowValue = "first column value 2";
        relation.getRow().add(new List() {{
            getExpression().add(new LiteralExpression() {{
                setText(firstRowValue);
            }});
        }});
        relation.getRow().add(new List() {{
            getExpression().add(new LiteralExpression() {{
                setText(secondRowValue);
            }});
        }});
        relationGrid = new RelationGrid(parent, hasExpression, expression, hasName, gridPanel, gridLayer, sessionManager,
                                        sessionCommandManager, editorSelectedEvent, controls);

        assertEquals(2, relationGrid.getModel().getRowCount());
        assertEquals(firstRowValue, relationGrid.getModel().getRow(0).getCells().get(1).getValue().getValue());
        assertEquals(secondRowValue, relationGrid.getModel().getRow(1).getCells().get(1).getValue().getValue());
    }

    @Test
    public void testAddColumn() throws Exception {
        relationGrid = new RelationGrid(parent, hasExpression, expression, hasName, gridPanel, gridLayer, sessionManager,
                                        sessionCommandManager, editorSelectedEvent, controls);

        relationGrid.addColumn();

        verify(sessionCommandManager).execute(eq(abstractCanvasHandler), addColumnCommand.capture());

        addColumnCommand.getValue().execute(abstractCanvasHandler);
        verify(gridPanel).refreshScrollPosition();
        verify(gridPanel).updatePanelSize();
        verify(gridLayer).batch();
    }

    @Test
    public void testAddRow() throws Exception {
        relationGrid = new RelationGrid(parent, hasExpression, expression, hasName, gridPanel, gridLayer, sessionManager,
                                        sessionCommandManager, editorSelectedEvent, controls);

        relationGrid.addRow();

        verify(sessionCommandManager).execute(eq(abstractCanvasHandler), addRowCommand.capture());

        addRowCommand.getValue().execute(abstractCanvasHandler);
        verify(gridPanel).refreshScrollPosition();
        verify(gridPanel).updatePanelSize();
        verify(gridLayer).batch();
    }
}
