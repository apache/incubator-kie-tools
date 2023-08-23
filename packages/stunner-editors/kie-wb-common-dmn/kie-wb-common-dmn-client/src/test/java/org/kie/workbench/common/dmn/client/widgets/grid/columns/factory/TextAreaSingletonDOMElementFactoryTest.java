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

package org.kie.workbench.common.dmn.client.widgets.grid.columns.factory;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.commands.general.DeleteCellValueCommand;
import org.kie.workbench.common.dmn.client.commands.general.SetCellValueCommand;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.dom.TextAreaDOMElement;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.keyboard.KeyDownHandlerCommon;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class TextAreaSingletonDOMElementFactoryTest extends BaseSingletonDOMElementFactoryTest<TextAreaSingletonDOMElementFactory, TextAreaDOMElement> {

    @Mock
    private KeyDownEvent event;

    @Override
    protected TextAreaSingletonDOMElementFactory getFactoryForAttachDomElementTest() {
        return new TextAreaSingletonDOMElementFactory(gridPanel,
                                                      gridLayer,
                                                      gridWidget,
                                                      sessionManager,
                                                      sessionCommandManager,
                                                      (gc) -> new DeleteCellValueCommand(gc,
                                                                                         () -> uiModelMapper,
                                                                                         gridLayer::batch),
                                                      (gcv) -> new SetCellValueCommand(gcv,
                                                                                       () -> uiModelMapper,
                                                                                       gridLayer::batch)) {
            @Override
            public TextAreaDOMElement createDomElement(final GridLayer gridLayer,
                                                       final GridWidget gridWidget) {
                return spy(super.createDomElement(gridLayer,
                                                  gridWidget));
            }
        };
    }

    @Override
    protected TextAreaSingletonDOMElementFactory getFactoryForFlushTest() {
        return new TextAreaSingletonDOMElementFactory(gridPanel,
                                                      gridLayer,
                                                      gridWidget,
                                                      sessionManager,
                                                      sessionCommandManager,
                                                      (gc) -> new DeleteCellValueCommand(gc,
                                                                                         () -> uiModelMapper,
                                                                                         gridLayer::batch),
                                                      (gcv) -> new SetCellValueCommand(gcv,
                                                                                       () -> uiModelMapper,
                                                                                       gridLayer::batch));
    }

    @Test
    public void testKeyDownHandlerCommon_KEY_TAB() {
        final TextAreaSingletonDOMElementFactory factory = spy(getFactoryForAttachDomElementTest());
        final KeyDownHandlerCommon keyDownHandlerCommon = factory.destroyOrFlushKeyDownHandler();

        when(event.getNativeKeyCode()).thenReturn(KeyCodes.KEY_TAB);

        keyDownHandlerCommon.onKeyDown(event);

        verify(factory).flush();
    }

    @Test
    public void testKeyDownHandlerCommon_KEY_ENTER() {
        final TextAreaSingletonDOMElementFactory factory = spy(getFactoryForAttachDomElementTest());
        final KeyDownHandlerCommon keyDownHandlerCommon = factory.destroyOrFlushKeyDownHandler();

        when(event.getNativeKeyCode()).thenReturn(KeyCodes.KEY_ENTER);

        keyDownHandlerCommon.onKeyDown(event);

        verify(factory, never()).flush();
    }

    @Test
    public void testKeyDownHandlerCommon_KEY_ESCAPE() {
        final TextAreaSingletonDOMElementFactory factory = spy(getFactoryForAttachDomElementTest());
        final KeyDownHandlerCommon keyDownHandlerCommon = factory.destroyOrFlushKeyDownHandler();

        when(event.getNativeKeyCode()).thenReturn(KeyCodes.KEY_ESCAPE);

        keyDownHandlerCommon.onKeyDown(event);

        verify(factory, never()).flush();
    }
}
