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

package org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.dom;

import java.util.Optional;
import java.util.function.Supplier;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.dmn.client.commands.general.DeleteCellValueCommand;
import org.kie.workbench.common.dmn.client.commands.general.SetCellValueCommand;
import org.kie.workbench.common.dmn.client.widgets.grid.model.BaseUIModelMapper;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.dom.impl.BaseDOMElement;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public abstract class BaseDOMElementTest<W extends Widget, D extends BaseDOMElement> {

    @Mock
    private Element widgetElement;

    @Mock
    private Style widgetElementStyle;

    @Mock
    private EditorSession session;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private GridBodyCellRenderContext context;

    @Mock
    private GraphCommandExecutionContext graphCommandExecutionContext;

    @Captor
    private ArgumentCaptor<SetCellValueCommand> setCellValueCommandArgumentCaptor;

    @Captor
    private ArgumentCaptor<DeleteCellValueCommand> deleteCellValueCommandArgumentCaptor;

    @Captor
    private ArgumentCaptor<Supplier<Optional<GridCellValue<?>>>> valueSupplierArgumentCaptor;

    @Mock
    protected GridLayer gridLayer;

    @Mock
    protected GridWidget gridWidget;

    @Mock
    protected SessionManager sessionManager;

    @Mock
    protected SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    protected BaseUIModelMapper<?> uiModelMapper;

    protected W widget;

    private D domElement;

    @Before
    public void setup() {
        widget = getWidget();

        when(widget.getElement()).thenReturn(widgetElement);
        when(widgetElement.getStyle()).thenReturn(widgetElementStyle);
        when(sessionManager.getCurrentSession()).thenReturn(session);
        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(context.getRowIndex()).thenReturn(0);
        when(context.getColumnIndex()).thenReturn(1);
        when(gridWidget.getModel()).thenReturn(new BaseGridData());

        domElement = getDomElement();
        domElement.setContext(context);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkFlushWithValue() {
        domElement.flush("value");

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              setCellValueCommandArgumentCaptor.capture());

        final SetCellValueCommand command = setCellValueCommandArgumentCaptor.getValue();
        command.getGraphCommand(canvasHandler).execute(graphCommandExecutionContext);

        verify(uiModelMapper).toDMNModel(eq(0),
                                         eq(1),
                                         valueSupplierArgumentCaptor.capture());

        final Supplier<Optional<GridCellValue<?>>> valueSupplier = valueSupplierArgumentCaptor.getValue();
        assertTrue(valueSupplier.get().isPresent());
        assertEquals("value",
                     valueSupplier.get().get().getValue().toString());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkFlushWithoutValue() {
        domElement.flush("");

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              deleteCellValueCommandArgumentCaptor.capture());

        final DeleteCellValueCommand command = deleteCellValueCommandArgumentCaptor.getValue();
        command.getGraphCommand(canvasHandler).execute(graphCommandExecutionContext);

        verify(uiModelMapper).toDMNModel(eq(0),
                                         eq(1),
                                         valueSupplierArgumentCaptor.capture());

        final Supplier<Optional<GridCellValue<?>>> valueSupplier = valueSupplierArgumentCaptor.getValue();
        assertFalse(valueSupplier.get().isPresent());
    }

    protected abstract W getWidget();

    protected abstract D getDomElement();
}
