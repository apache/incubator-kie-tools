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

package org.kie.workbench.common.dmn.client.editors.expressions.types.relation;

import java.util.Optional;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.gwtbootstrap3.client.ui.TextArea;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.client.editors.expressions.mocks.MockHasDOMElementResourcesHeaderMetaData;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionCellValue;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionEditorColumn;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.BaseDOMElementSingletonColumnTest;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.TextAreaSingletonDOMElementFactory;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.dom.TextAreaDOMElement;
import org.kie.workbench.common.dmn.client.widgets.grid.model.BaseUIModelMapper;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridColumn;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class RelationColumnTest extends BaseDOMElementSingletonColumnTest<TextAreaSingletonDOMElementFactory, TextAreaDOMElement, TextArea, RelationColumn, RelationGrid> {

    @Mock
    private TextAreaSingletonDOMElementFactory factory;

    @Mock
    private TextAreaDOMElement domElement;

    @Mock
    private TextArea widget;

    @Mock
    private GridWidget parentGridWidget;

    @Mock
    private BaseExpressionGrid peerExpressionEditor;

    private GridData parentUiModel;

    @Override
    protected TextAreaSingletonDOMElementFactory getFactory() {
        return factory;
    }

    @Override
    protected TextAreaDOMElement getDomElement() {
        return domElement;
    }

    @Override
    protected TextArea getWidget() {
        return widget;
    }

    @Override
    protected RelationGrid getGridWidget() {
        return mock(RelationGrid.class);
    }

    @Override
    protected RelationColumn getColumn() {
        return new RelationColumn(headerMetaData,
                                  factory,
                                  RelationColumn.DEFAULT_WIDTH,
                                  gridWidget);
    }

    @Before
    public void setUp() throws Exception {
        parentUiModel = new BaseGridData();
        parentUiModel.appendRow(new BaseGridRow());
        parentUiModel.appendRow(new BaseGridRow());
        parentUiModel.appendRow(new BaseGridRow());
        parentUiModel.appendColumn(mock(ExpressionEditorColumn.class));
        final GridCellTuple parent = new GridCellTuple(0, 0, parentGridWidget);

        doReturn(parentUiModel).when(parentGridWidget).getModel();
        doReturn(widget).when(domElement).getWidget();
        doReturn(parent).when(gridWidget).getParentInformation();
        doReturn(100.0).when(gridWidget).getWidth();
    }

    @Test
    public void testGetMinimumWidthWithNoPeers() {
        assertEquals(DMNGridColumn.DEFAULT_WIDTH,
                     column.getMinimumWidth(),
                     0.0);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetMinimumWidthWithPeerNarrowerThanThisGrid() {
        final double PEER_WIDTH = 50.0;
        final double RELATION_GRID_WIDTH = 100.0;

        assertMinimumWidth(PEER_WIDTH,
                           RELATION_GRID_WIDTH,
                           DMNGridColumn.DEFAULT_WIDTH,
                           Optional.of(peerExpressionEditor));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetMinimumWidthWithMultiplePeersNarrowerThanThisGrid() {
        final double PEER_WIDTH = 50.0;
        final double RELATION_GRID_WIDTH = 100.0;

        final RelationColumn peerRelationColumn = getColumn();
        final RelationGrid peerRelationEditor = mock(RelationGrid.class);
        when(peerRelationEditor.getMinimumWidth()).thenAnswer(i -> peerRelationColumn.getMinimumWidth());

        assertMinimumWidth(PEER_WIDTH,
                           RELATION_GRID_WIDTH,
                           DMNGridColumn.DEFAULT_WIDTH,
                           Optional.of(peerExpressionEditor),
                           Optional.of(peerRelationEditor));
    }

    @Test
    public void testHeaderDOMElementsAreDestroyed() {
        final MockHasDOMElementResourcesHeaderMetaData mockHeaderMetaData = mock(MockHasDOMElementResourcesHeaderMetaData.class);
        column.getHeaderMetaData().add(mockHeaderMetaData);

        column.destroyResources();

        verify(mockHeaderMetaData).destroyResources();
    }

    @SafeVarargs
    private final void assertMinimumWidth(final double peerWidth,
                                          final double relationGridWidth,
                                          final double expectedMinimumWidth,
                                          final Optional<BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper>>... peers) {
        doReturn(peerWidth).when(peerExpressionEditor).getMinimumWidth();
        doReturn(relationGridWidth).when(gridWidget).getWidth();

        for (int i = 0; i < peers.length; i++) {
            parentUiModel.setCellValue(i + 1, 0, new ExpressionCellValue(peers[i]));
        }

        assertEquals(expectedMinimumWidth,
                     column.getMinimumWidth(),
                     0.0);
    }
}
