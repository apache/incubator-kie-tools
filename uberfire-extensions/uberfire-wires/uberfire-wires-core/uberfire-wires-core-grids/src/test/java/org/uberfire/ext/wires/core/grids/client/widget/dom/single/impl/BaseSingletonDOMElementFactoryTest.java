/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.wires.core.grids.client.widget.dom.single.impl;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.shared.EventHandler;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.widget.dom.impl.BaseDOMElement;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.keyboard.KeyDownHandlerCommon;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLienzoPanel;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public abstract class BaseSingletonDOMElementFactoryTest {

    @Mock
    protected GridLienzoPanel gridLienzoPanelMock;

    @Mock
    protected GridLayer gridLayerMock;

    @Mock
    protected GridWidget gridWidgetMock;

    private BaseSingletonDOMElementFactory testedFactory;

    @Captor
    private ArgumentCaptor<EventHandler> handlerCaptor;

    public abstract BaseSingletonDOMElementFactory getTestedFactory();

    @Before
    public void setUp() throws Exception {
        testedFactory = getTestedFactory();
    }

    @Test
    public void testRegisterHandlers() {
        testedFactory.createDomElement(gridLayerMock, gridWidgetMock);
        testedFactory.registerHandlers(testedFactory.widget, testedFactory.e);

        verify(testedFactory.widget, times(4)).addDomHandler(handlerCaptor.capture(), any(DomEvent.Type.class));

        // KeyDownHandlerCommon
        Assertions.assertThat(handlerCaptor.getAllValues().get(0)).isInstanceOf(KeyDownHandlerCommon.class);

        // KeyDownHandler - stopPropagation
        final KeyDownEvent keyDownEventMock = mock(KeyDownEvent.class);
        Assertions.assertThat(handlerCaptor.getAllValues().get(1)).isInstanceOf(KeyDownHandler.class);
        ((KeyDownHandler) handlerCaptor.getAllValues().get(1)).onKeyDown(keyDownEventMock);
        verify(keyDownEventMock).stopPropagation();

        // MouseDownHandler - stopPropagation
        final MouseDownEvent mouseDownEventMock = mock(MouseDownEvent.class);
        Assertions.assertThat(handlerCaptor.getAllValues().get(2)).isInstanceOf(MouseDownHandler.class);
        ((MouseDownHandler) handlerCaptor.getAllValues().get(2)).onMouseDown(mouseDownEventMock);
        verify(mouseDownEventMock).stopPropagation();

        // BlurHandler
        final BaseDOMElement domElement = testedFactory.e;
        final BlurEvent blurEventMock = mock(BlurEvent.class);
        Assertions.assertThat(handlerCaptor.getAllValues().get(3)).isInstanceOf(BlurHandler.class);
        ((BlurHandler) handlerCaptor.getAllValues().get(3)).onBlur(blurEventMock);
        Assertions.assertThat(testedFactory.widget).isNull();
        Assertions.assertThat(testedFactory.e).isNull();
        verify(domElement).detach();
        verify(gridLayerMock).batch();
        verify(gridLienzoPanelMock).setFocus(true);
    }
}
