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

package org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.dom;

import java.util.Optional;
import java.util.function.Function;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.SimplePanel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.commands.general.DeleteCellValueCommand;
import org.kie.workbench.common.dmn.client.commands.general.SetCellValueCommand;
import org.kie.workbench.common.dmn.client.widgets.codecompletion.MonacoPropertiesFactory;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellValueTuple;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.command.Command;
import org.mockito.Mock;
import org.uberfire.client.views.pfly.monaco.jsinterop.MonacoEditor;
import org.uberfire.client.views.pfly.monaco.jsinterop.MonacoStandaloneCodeEditor;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

import static com.google.gwt.dom.client.Style.Unit.PCT;
import static com.google.gwt.dom.client.Style.Unit.PX;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class MonacoEditorDOMElementTest extends BaseDOMElementTest<MonacoEditorWidget, MonacoEditorDOMElementTest.MonacoEditorDOMElementFake> {

    @Mock
    private MonacoEditorWidget monacoEditorWidget;

    @Override
    protected MonacoEditorWidget getWidget() {
        when(monacoEditorWidget.getCodeEditor()).thenReturn(Optional.empty());
        return monacoEditorWidget;
    }

    @Override
    protected MonacoEditorDOMElementFake getDomElement() {
        return spy(new MonacoEditorDOMElementFake(widget,
                                                  gridLayer,
                                                  gridWidget,
                                                  sessionManager,
                                                  sessionCommandManager,
                                                  (gc) -> new DeleteCellValueCommand(gc,
                                                                                     () -> uiModelMapper,
                                                                                     gridLayer::batch),
                                                  (gcv) -> new SetCellValueCommand(gcv,
                                                                                   () -> uiModelMapper,
                                                                                   gridLayer::batch)));
    }

    @Test
    public void testSetValue() {
        domElement.setValue(VALUE);
        verify(widget).setValue(VALUE);
    }

    @Test
    public void testGetValue() {
        domElement.getValue();
        verify(widget).getValue();
    }

    @Test
    public void testSetupElements() {

        doNothing().when(domElement).setupContainerComponent();
        doNothing().when(domElement).setupInternalComponent();

        domElement.setupElements();

        verify(domElement).setupContainerComponent();
        verify(domElement).setupInternalComponent();
    }

    @Test
    public void testSetupContainerComponent() {

        final SimplePanel container = mock(SimplePanel.class);
        final Element element = mock(Element.class);
        final Style style = mock(Style.class);

        doReturn(container).when(domElement).getContainer();
        when(container.getElement()).thenReturn(element);
        when(element.getStyle()).thenReturn(style);

        domElement.setupContainerComponent();

        verify(style).setPadding(5, PX);
        verify(container).setWidget(widget);
    }

    @Test
    public void testSetupInternalComponent() {

        final Element element = mock(Element.class);
        final Style style = mock(Style.class);
        final elemental2.dom.Element mockedElement = mock(elemental2.dom.Element.class);
        final MonacoPropertiesFactory properties = mock(MonacoPropertiesFactory.class);
        final JavaScriptObject constructionOptions = mock(JavaScriptObject.class);
        final MonacoEditor editor = mock(MonacoEditor.class);
        final MonacoStandaloneCodeEditor standaloneCodeEditor = mock(MonacoStandaloneCodeEditor.class);
        final com.google.gwt.user.client.Element gwtElement = mock(com.google.gwt.user.client.Element.class);
        final MonacoStandaloneCodeEditor.CallbackFunction onKeyDown = mock(MonacoStandaloneCodeEditor.CallbackFunction.class);
        final MonacoStandaloneCodeEditor.CallbackFunction widgetTrigger = mock(MonacoStandaloneCodeEditor.CallbackFunction.class);
        final NativeEvent blurEvent = mock(NativeEvent.class);

        when(widget.getElement()).thenReturn(element);
        when(element.getStyle()).thenReturn(style);
        when(domElement.uncheckedCast(any())).thenReturn(mockedElement);
        when(properties.getConstructionOptions()).thenReturn(constructionOptions);
        doReturn(onKeyDown).when(domElement).getOnKeyDown(standaloneCodeEditor);
        doReturn(widgetTrigger).when(domElement).getWidgetTrigger(blurEvent);
        doReturn(blurEvent).when(domElement).getBlurEvent();
        doReturn(properties).when(domElement).makeMonacoPropertiesFactory();
        doReturn(editor).when(domElement).getMonacoEditor();
        doReturn(standaloneCodeEditor).when(editor).create(mockedElement, constructionOptions);

        domElement.setupInternalComponent();

        verify(style).setWidth(100, PCT);
        verify(style).setHeight(100, PCT);
        verify(standaloneCodeEditor).onKeyDown(onKeyDown);
        verify(standaloneCodeEditor).onDidBlurEditorWidget(widgetTrigger);
        verify(widget).setCodeEditor(standaloneCodeEditor);
        verify(widget).setFocus(true);
    }

    @Test
    public void testGetWidgetTrigger() {

        final NativeEvent triggeredBlur = mock(NativeEvent.class);
        final NativeEvent monacoBlur = mock(NativeEvent.class);

        domElement.getWidgetTrigger(triggeredBlur).call(monacoBlur);

        verify(domElement).fireNativeEvent(triggeredBlur, widget);
    }

    @Test
    public void testGetOnKeyDownWhenSuggestWidgetIsVisibleAndKeyCodeIsEsc() {

        final MonacoStandaloneCodeEditor codeEditor = mock(MonacoStandaloneCodeEditor.class);
        final NativeEvent event = mock(NativeEvent.class);

        when(event.getKeyCode()).thenReturn(9);
        when(codeEditor.isSuggestWidgetVisible()).thenReturn(true);

        domElement.getOnKeyDown(codeEditor).call(event);

        verify(codeEditor).trigger("keyboard", "cursorHome");
        verify(codeEditor).trigger("keyboard", "cursorEnd");
        verify(event).stopPropagation();
        verify(event).preventDefault();
    }

    @Test
    public void testGetOnKeyDownWhenSuggestWidgetIsVisibleAndKeyCodeIsNotEsc() {

        final MonacoStandaloneCodeEditor codeEditor = mock(MonacoStandaloneCodeEditor.class);
        final NativeEvent event = mock(NativeEvent.class);

        when(event.getKeyCode()).thenReturn(10);
        when(codeEditor.isSuggestWidgetVisible()).thenReturn(true);

        domElement.getOnKeyDown(codeEditor).call(event);

        verify(codeEditor, never()).trigger("keyboard", "cursorHome");
        verify(codeEditor, never()).trigger("keyboard", "cursorEnd");
        verify(event, never()).stopPropagation();
        verify(event, never()).preventDefault();
    }

    @Test
    public void testGetOnKeyDownWhenSuggestWidgetIsNotVisibleAndKeyCodeIsEsc() {

        final MonacoStandaloneCodeEditor codeEditor = mock(MonacoStandaloneCodeEditor.class);
        final NativeEvent event = mock(NativeEvent.class);

        when(event.getKeyCode()).thenReturn(9);
        when(codeEditor.isSuggestWidgetVisible()).thenReturn(false);

        domElement.getOnKeyDown(codeEditor).call(event);

        verify(codeEditor, never()).trigger("keyboard", "cursorHome");
        verify(codeEditor, never()).trigger("keyboard", "cursorEnd");
        verify(event, never()).stopPropagation();
        verify(event, never()).preventDefault();
    }

    @Test
    public void testGetOnKeyDownWhenSuggestWidgetIsNotVisibleAndKeyCodeIsNotEsc() {

        final MonacoStandaloneCodeEditor codeEditor = mock(MonacoStandaloneCodeEditor.class);
        final NativeEvent event = mock(NativeEvent.class);

        when(event.getKeyCode()).thenReturn(10);
        when(codeEditor.isSuggestWidgetVisible()).thenReturn(false);

        domElement.getOnKeyDown(codeEditor).call(event);

        verify(codeEditor, never()).trigger("keyboard", "cursorHome");
        verify(codeEditor, never()).trigger("keyboard", "cursorEnd");
        verify(event, never()).stopPropagation();
        verify(event, never()).preventDefault();
    }

    @Test
    public void testInitialise() {
        final GridBodyCellRenderContext context = mock(GridBodyCellRenderContext.class);
        domElement.initialise(context);
        verify(domElement).transform(context);
    }

    static class MonacoEditorDOMElementFake extends MonacoEditorDOMElement {

        MonacoEditorDOMElementFake(final MonacoEditorWidget widget,
                                   final GridLayer gridLayer,
                                   final GridWidget gridWidget,
                                   final SessionManager sessionManager,
                                   final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                   final Function<GridCellTuple, Command> hasNoValueCommand,
                                   final Function<GridCellValueTuple, Command> hasValueCommand) {
            super(widget, gridLayer, gridWidget, sessionManager, sessionCommandManager, hasNoValueCommand, hasValueCommand);
        }

        @Override
        protected SimplePanel getContainer() {
            return super.getContainer();
        }

        @Override
        protected void transform(final GridBodyCellRenderContext context) {
            // empty
        }
    }
}
