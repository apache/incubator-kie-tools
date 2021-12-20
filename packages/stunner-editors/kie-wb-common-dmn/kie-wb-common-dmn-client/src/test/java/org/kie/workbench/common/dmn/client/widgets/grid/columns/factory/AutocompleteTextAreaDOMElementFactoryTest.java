/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import java.util.function.Function;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.commands.general.DeleteCellValueCommand;
import org.kie.workbench.common.dmn.client.commands.general.SetCellValueCommand;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.dom.MonacoEditorDOMElement;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.dom.MonacoEditorWidget;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellValueTuple;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.command.Command;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.keyboard.KeyDownHandlerCommon;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class AutocompleteTextAreaDOMElementFactoryTest extends BaseSingletonDOMElementFactoryTest<AutocompleteTextAreaDOMElementFactory, MonacoEditorDOMElement> {

    @Mock
    private KeyDownEvent event;

    @Override
    protected AutocompleteTextAreaDOMElementFactory getFactoryForAttachDomElementTest() {
        return new AutocompleteTextAreaDOMElementFactoryFake(gridPanel,
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
        };
    }

    @Override
    protected AutocompleteTextAreaDOMElementFactory getFactoryForFlushTest() {
        return new AutocompleteTextAreaDOMElementFactoryFake(gridPanel,
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
        final AutocompleteTextAreaDOMElementFactory factory = spy(getFactoryForAttachDomElementTest());
        final KeyDownHandlerCommon keyDownHandlerCommon = factory.destroyOrFlushKeyDownHandler();

        when(event.getNativeKeyCode()).thenReturn(KeyCodes.KEY_TAB);

        keyDownHandlerCommon.onKeyDown(event);

        verify(factory).flush();
    }

    @Test
    public void testKeyDownHandlerCommon_KEY_ENTER() {
        final AutocompleteTextAreaDOMElementFactory factory = spy(getFactoryForAttachDomElementTest());
        final KeyDownHandlerCommon keyDownHandlerCommon = factory.destroyOrFlushKeyDownHandler();

        when(event.getNativeKeyCode()).thenReturn(KeyCodes.KEY_ENTER);

        keyDownHandlerCommon.onKeyDown(event);

        verify(factory, never()).flush();
    }

    @Test
    public void testKeyDownHandlerCommon_KEY_ESCAPE() {
        final AutocompleteTextAreaDOMElementFactory factory = spy(getFactoryForAttachDomElementTest());
        final KeyDownHandlerCommon keyDownHandlerCommon = factory.destroyOrFlushKeyDownHandler();

        when(event.getNativeKeyCode()).thenReturn(KeyCodes.KEY_ESCAPE);

        keyDownHandlerCommon.onKeyDown(event);

        verify(factory, never()).flush();
    }

    @Test
    public void testCreateDomElementInternal() {

        final MonacoEditorWidget widget = mock(MonacoEditorWidget.class);
        final MonacoEditorDOMElement domElement = getFactoryForAttachDomElementTest().createDomElementInternal(widget, gridLayer, gridWidget);

        verify(domElement).setupElements();
    }

    static class AutocompleteTextAreaDOMElementFactoryFake extends AutocompleteTextAreaDOMElementFactory {

        AutocompleteTextAreaDOMElementFactoryFake(final DMNGridPanel gridPanel,
                                                  final GridLayer gridLayer,
                                                  final GridWidget gridWidget,
                                                  final SessionManager sessionManager,
                                                  final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                                  final Function<GridCellTuple, Command> hasNoValueCommand,
                                                  final Function<GridCellValueTuple, Command> hasValueCommand) {
            super(gridPanel, gridLayer, gridWidget, sessionManager, sessionCommandManager, hasNoValueCommand, hasValueCommand);
        }

        @Override
        protected MonacoEditorDOMElement makeMonacoEditorDOMElement(final MonacoEditorWidget widget,
                                                                    final GridLayer gridLayer,
                                                                    final GridWidget gridWidget) {
            final MonacoEditorDOMElement domElement = spy(super.makeMonacoEditorDOMElement(widget, gridLayer, gridWidget));
            doNothing().when(domElement).setupElements();
            return domElement;
        }
    }
}
