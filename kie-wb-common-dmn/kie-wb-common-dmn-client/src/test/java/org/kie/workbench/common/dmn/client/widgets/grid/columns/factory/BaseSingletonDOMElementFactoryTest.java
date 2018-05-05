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

package org.kie.workbench.common.dmn.client.widgets.grid.columns.factory;

import com.ait.lienzo.client.core.types.Transform;
import com.google.gwt.user.client.ui.AbsolutePanel;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.dmn.client.widgets.grid.model.BaseUIModelMapper;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.AbstractCanvasGraphCommand;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.dom.impl.BaseDOMElement;
import org.uberfire.ext.wires.core.grids.client.widget.dom.single.impl.BaseSingletonDOMElementFactory;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLayerRedrawManager;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public abstract class BaseSingletonDOMElementFactoryTest<F extends BaseSingletonDOMElementFactory, E extends BaseDOMElement> {

    @Mock
    private GridBodyCellRenderContext context;

    @Mock
    private Callback<E> onCreation;

    @Mock
    private Callback<E> onDisplay;

    @Mock
    private EditorSession session;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Captor
    private ArgumentCaptor<GridLayerRedrawManager.PrioritizedCommand> commandArgumentCaptor;

    @Captor
    private ArgumentCaptor<E> domElementOnCreationArgumentCaptor;

    @Captor
    private ArgumentCaptor<E> domElementOnDisplayArgumentCaptor;

    @Mock
    protected DMNGridPanel gridPanel;

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

    @Before
    public void setup() {
        when(context.getTransform()).thenReturn(new Transform());
        when(gridLayer.getDomElementContainer()).thenReturn(new AbsolutePanel());
        when(sessionManager.getCurrentSession()).thenReturn(session);
        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(gridWidget.getModel()).thenReturn(new BaseGridData());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkAttachDomElement() {
        final F factory = getFactoryForAttachDomElementTest();

        factory.attachDomElement(context,
                                 onCreation,
                                 onDisplay);
        verify(gridLayer).batch(commandArgumentCaptor.capture());

        final GridLayerRedrawManager.PrioritizedCommand command = commandArgumentCaptor.getValue();
        command.execute();

        verify(onCreation).callback(domElementOnCreationArgumentCaptor.capture());
        final E domElementOnCreation = domElementOnCreationArgumentCaptor.getValue();
        verify(domElementOnCreation).setContext(eq(context));
        verify(domElementOnCreation).initialise(eq(context));

        verify(onDisplay).callback(domElementOnDisplayArgumentCaptor.capture());
        final E domElementOnDisplay = domElementOnDisplayArgumentCaptor.getValue();
        verify(domElementOnDisplay).attach();

        assertEquals(domElementOnCreation,
                     domElementOnDisplay);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkFlush() {
        final F factory = getFactoryForFlushTest();

        factory.attachDomElement(context,
                                 onCreation,
                                 onDisplay);
        verify(gridLayer).batch(commandArgumentCaptor.capture());

        final GridLayerRedrawManager.PrioritizedCommand command = commandArgumentCaptor.getValue();
        command.execute();

        factory.flush();

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              any(AbstractCanvasGraphCommand.class));
    }

    protected abstract F getFactoryForAttachDomElementTest();

    protected abstract F getFactoryForFlushTest();
}
