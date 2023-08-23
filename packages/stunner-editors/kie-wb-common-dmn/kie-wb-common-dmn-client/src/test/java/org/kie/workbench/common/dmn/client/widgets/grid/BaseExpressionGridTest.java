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

package org.kie.workbench.common.dmn.client.widgets.grid;

import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.dmn.client.editors.expressions.ExpressionContainerUIModelMapper;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
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
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseBounds;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.mocks.EventSourceMock;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

@RunWith(LienzoMockitoTestRunner.class)
public abstract class BaseExpressionGridTest {

    @Mock
    protected GridRenderer renderer;

    @Mock
    protected DMNGridPanel gridPanel;

    @Mock
    protected DMNGridLayer gridLayer;

    @Mock
    protected DefinitionUtils definitionUtils;

    @Mock
    protected Viewport viewport;

    @Mock
    protected SessionManager sessionManager;

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
    protected ExpressionContainerUIModelMapper mapper;

    @Mock
    protected Node gridParent;

    @Mock
    protected GridCellTuple parentCell;

    @Mock
    protected EventSourceMock<ExpressionEditorChanged> editorSelectedEvent;

    @Mock
    protected EventSourceMock<RefreshFormPropertiesEvent> refreshFormPropertiesEvent;

    @Mock
    protected EventSourceMock<DomainObjectSelectionEvent> domainObjectSelectionEvent;

    protected BaseExpressionGrid grid;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        this.grid = spy(getGrid());

        doReturn(gridLayer).when(grid).getLayer();
        doReturn(viewport).when(gridLayer).getViewport();
        doReturn(new BaseBounds(0, 0, 1000, 1000)).when(gridLayer).getVisibleBounds();
    }

    protected abstract BaseExpressionGrid getGrid();
}
