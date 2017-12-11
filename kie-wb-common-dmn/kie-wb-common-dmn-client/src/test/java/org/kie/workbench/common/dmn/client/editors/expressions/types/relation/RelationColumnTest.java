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

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.gwtbootstrap3.client.ui.TextArea;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionCellValue;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionEditorColumn;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.TextAreaSingletonDOMElementFactory;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.dom.TextAreaDOMElement;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridColumn;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCell;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class RelationColumnTest {

    @Mock
    private GridColumn.HeaderMetaData headerMetaData;

    @Mock
    private TextAreaSingletonDOMElementFactory factory;

    @Mock
    private RelationGrid gridWidget;

    @Mock
    private GridBodyCellRenderContext context;

    @Mock
    private Callback callback;

    @Mock
    private TextArea textArea;

    @Mock
    private TextAreaDOMElement textAreaDOMElement;

    @Mock
    private BaseExpressionGrid peerExpressionEditor;

    @Captor
    private ArgumentCaptor<Callback<TextAreaDOMElement>> textAreaDOMElementCallback;

    private GridCellTuple parent;

    private GridData parentUiModel;

    private RelationColumn relationColumn;

    @Before
    public void setUp() throws Exception {
        relationColumn = new RelationColumn(headerMetaData,
                                            factory,
                                            gridWidget);
        parentUiModel = new BaseGridData();
        parentUiModel.appendRow(new BaseGridRow());
        parentUiModel.appendRow(new BaseGridRow());
        parentUiModel.appendColumn(mock(ExpressionEditorColumn.class));
        parent = new GridCellTuple(0, 0, parentUiModel);

        doReturn(textArea).when(textAreaDOMElement).getWidget();
        doReturn(parent).when(gridWidget).getParentInformation();
        doReturn(100.0).when(gridWidget).getWidth();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEditNullCell() throws Exception {
        relationColumn.edit(null, context, callback);
        verify(factory).attachDomElement(eq(context), textAreaDOMElementCallback.capture(), any(Callback.class));
        textAreaDOMElementCallback.getValue().callback(textAreaDOMElement);
        verify(textArea).setValue("");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEditEmptyCell() throws Exception {
        relationColumn.edit(new BaseGridCell<>(null), context, callback);
        verify(factory).attachDomElement(eq(context), textAreaDOMElementCallback.capture(), any(Callback.class));
        textAreaDOMElementCallback.getValue().callback(textAreaDOMElement);
        verify(textArea).setValue("");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEditCell() throws Exception {
        final String cellValue = "abc";
        relationColumn.edit(new BaseGridCell<>(new BaseGridCellValue<>(cellValue)), context, callback);
        verify(factory).attachDomElement(eq(context), textAreaDOMElementCallback.capture(), any(Callback.class));
        textAreaDOMElementCallback.getValue().callback(textAreaDOMElement);
        verify(textArea).setValue(cellValue);
    }

    @Test
    public void testGetMinimumWidthWithNoPeers() {
        assertEquals(DMNGridColumn.DEFAULT_WIDTH,
                     relationColumn.getMinimumWidth(),
                     0.0);
    }

    @Test
    public void testGetMinimumWidthWithPeerNarrowerThanThisGrid() {
        final double PEER_WIDTH = 50.0;
        final double RELATION_GRID_WIDTH = 100.0;

        assertMinimumWidth(Optional.of(peerExpressionEditor),
                           PEER_WIDTH,
                           RELATION_GRID_WIDTH,
                           DMNGridColumn.DEFAULT_WIDTH);
    }

    @Test
    public void testGetMinimumWidthWithPeerWiderThanThisGrid() {
        final double PEER_WIDTH = 150.0;
        final double RELATION_GRID_WIDTH = 100.0;

        assertMinimumWidth(Optional.of(peerExpressionEditor),
                           PEER_WIDTH,
                           RELATION_GRID_WIDTH,
                           PEER_WIDTH);
    }

    @Test
    public void testGetMinimumWidthWithPeerNarrowerThanThisGridThatIsWiderThanThisColumn() {
        final double PEER_WIDTH = 200.0;
        final double RELATION_GRID_WIDTH = 150.0;

        assertMinimumWidth(Optional.of(peerExpressionEditor),
                           PEER_WIDTH,
                           RELATION_GRID_WIDTH,
                           PEER_WIDTH - (RELATION_GRID_WIDTH - DMNGridColumn.DEFAULT_WIDTH));
    }

    @Test
    public void testGetMinimumWidthWithPeerWiderThanThisGridThatIsWiderThanThisColumn() {
        final double PEER_WIDTH = 150.0;
        final double RELATION_GRID_WIDTH = 100.0;

        assertMinimumWidth(Optional.of(peerExpressionEditor),
                           PEER_WIDTH,
                           RELATION_GRID_WIDTH,
                           PEER_WIDTH);
    }

    private void assertMinimumWidth(final Optional<BaseExpressionGrid> peer,
                                    final double peerWidth,
                                    final double relationGridWidth,
                                    final double expectedMinimumWidth) {
        doReturn(peerWidth).when(peerExpressionEditor).getMinimumWidth();
        doReturn(relationGridWidth).when(gridWidget).getWidth();

        parentUiModel.setCell(1, 0, new ExpressionCellValue(peer));

        assertEquals(expectedMinimumWidth,
                     relationColumn.getMinimumWidth(),
                     0.0);
    }
}
