/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.stunner.core.client.canvas.controls.actions;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.adf.definitions.DynamicReadOnly;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasFocusedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasShapeRemovedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.RequiresCommandManager;
import org.kie.workbench.common.stunner.core.client.components.views.FloatingView;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.HasEventHandlers;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.event.TextDoubleClickEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.TextDoubleClickHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.TextEnterEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.TextEnterHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.TextExitEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.TextExitHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractCanvasInPlaceTextEditorControlTest<C extends AbstractCanvasInPlaceTextEditorControl> {

    private static final String UUID = "uuid";

    private static final double X = 10.0;

    private static final double Y = 20.0;

    private static final double OFFSET_X = 30.0;

    private static final double OFFSET_Y = 40.0;

    @Mock
    protected FloatingView<IsWidget> floatingView;

    @Mock
    protected TextEditorBox<AbstractCanvasHandler, Element> textEditorBox;

    @Mock
    protected HTMLElement textEditBoxElement;

    @Mock
    protected IsWidget textEditBoxWidget;

    @Mock
    protected EventSourceMock<CanvasSelectionEvent> canvasSelectionEvent;

    @Mock
    protected EditorSession session;

    @Mock
    protected KeyboardControl<AbstractCanvas, ClientSession> keyboardControl;

    @Mock
    protected AbstractCanvasHandler canvasHandler;

    @Mock
    protected Canvas canvas;

    @Mock
    protected AbstractCanvas abstractCanvas;

    @Mock
    protected AbstractCanvas.CanvasView abstractCanvasView;

    @Mock
    protected Element element;

    @Mock
    protected Shape shape;

    @Mock
    protected TestShapeView testShapeView;

    @Mock
    protected View shapeView;

    @Mock
    protected RequiresCommandManager.CommandManagerProvider<AbstractCanvasHandler> commandManagerProvider;

    protected Bounds shapeViewBounds = Bounds.create();

    @Captor
    protected ArgumentCaptor<KeyboardControl.KeyShortcutCallback> keyShortcutCallbackCaptor;

    @Captor
    protected ArgumentCaptor<Command> commandCaptor;

    @Captor
    protected ArgumentCaptor<CanvasSelectionEvent> canvasSelectionEventCaptor;

    @Captor
    protected ArgumentCaptor<TextDoubleClickHandler> textDoubleClickHandlerCaptor;

    @Captor
    protected ArgumentCaptor<TextEnterHandler> textEnterHandlerCaptor;

    @Captor
    protected ArgumentCaptor<TextExitHandler> textExitHandlerCaptor;

    protected C control;

    interface TestShapeView extends ShapeView,
                                    HasTitle,
                                    HasEventHandlers {

    }

    @Before
    public void setup() {
        when(session.getKeyboardControl()).thenReturn(keyboardControl);
        when(floatingView.hide()).thenReturn(floatingView);
        when(floatingView.setHideCallback(any(Command.class))).thenReturn(floatingView);
        when(floatingView.setTimeOut(anyInt())).thenReturn(floatingView);
        when(floatingView.setX(anyDouble())).thenReturn(floatingView);
        when(floatingView.setY(anyDouble())).thenReturn(floatingView);
        when(floatingView.setOffsetX(anyDouble())).thenReturn(floatingView);
        when(floatingView.setOffsetY(anyDouble())).thenReturn(floatingView);
        when(textEditorBox.getElement()).thenReturn(textEditBoxElement);
        when(textEditorBox.getDisplayOffsetX()).thenReturn(OFFSET_X);
        when(textEditorBox.getDisplayOffsetY()).thenReturn(OFFSET_Y);
        when(element.getUUID()).thenReturn(UUID);
        when(element.getContent()).thenReturn(shapeView);
        when(shapeView.getBounds()).thenReturn(shapeViewBounds);
        when(canvasHandler.getCanvas()).thenReturn(canvas);
        when(canvasHandler.getAbstractCanvas()).thenReturn(abstractCanvas);
        when(abstractCanvas.getView()).thenReturn(abstractCanvasView);
        when(canvas.getShape(eq(UUID))).thenReturn(shape);
        when(shape.getUUID()).thenReturn(UUID);
        when(shape.getShapeView()).thenReturn(testShapeView);

        this.control = spy(getControl());

        doReturn(textEditBoxWidget).when(control).wrapTextEditorBoxElement(eq(textEditBoxElement));
    }

    protected abstract C getControl();

    @Test
    public void testBind() {
        control.bind(session);

        verify(keyboardControl, times(2)).addKeyShortcutCallback(any(KeyboardControl.KeyShortcutCallback.class));
    }

    @Test
    public void testBindKeyControlHandledKey() {
        control.bind(session);

        verify(keyboardControl, times(2)).addKeyShortcutCallback(keyShortcutCallbackCaptor.capture());

        final KeyboardControl.KeyShortcutCallback keyShortcutCallback = keyShortcutCallbackCaptor.getValue();
        keyShortcutCallback.onKeyShortcut(KeyboardEvent.Key.ESC);

        verify(control).onKeyDownEvent(eq(KeyboardEvent.Key.ESC));

        verify(control).hide();
    }

    @Test
    public void testBindKeyControlUnhandledKey() {
        control.bind(session);

        verify(keyboardControl, times(2)).addKeyShortcutCallback(keyShortcutCallbackCaptor.capture());

        final KeyboardControl.KeyShortcutCallback keyShortcutCallback = keyShortcutCallbackCaptor.getValue();
        keyShortcutCallback.onKeyShortcut(KeyboardEvent.Key.ARROW_DOWN);

        verify(control).onKeyDownEvent(eq(KeyboardEvent.Key.ARROW_DOWN));

        verify(control, never()).hide();
    }

    @Test
    public void testEnable() {
        control.init(canvasHandler);

        verify(textEditorBox).initialize(eq(canvasHandler),
                                         any(Command.class));
        verify(floatingView).hide();
        verify(floatingView).setHideCallback(any(Command.class));
        verify(floatingView).setTimeOut(AbstractCanvasInPlaceTextEditorControl.FLOATING_VIEW_TIMEOUT);
        verify(floatingView).add(textEditBoxWidget);
    }

    @Test
    public void testEnableCloseCallback() {
        control.init(canvasHandler);

        verify(textEditorBox).initialize(eq(canvasHandler),
                                         commandCaptor.capture());

        final Command command = commandCaptor.getValue();
        command.execute();

        verify(control).hide();
        verify(canvasSelectionEvent).fire(canvasSelectionEventCaptor.capture());

        final CanvasSelectionEvent cse = canvasSelectionEventCaptor.getValue();
        assertEquals(canvasHandler,
                     cse.getCanvasHandler());
    }

    @Test
    public void testEnableTimeoutCallback() {
        control.init(canvasHandler);

        verify(floatingView).setHideCallback(commandCaptor.capture());

        final Command command = commandCaptor.getValue();
        command.execute();

        verify(textEditorBox).flush();
    }

    @Test
    public void testRegisterDoubleClickHandler() {
        control.init(canvasHandler);

        when(testShapeView.supports(ViewEventType.TEXT_DBL_CLICK)).thenReturn(true);

        control.register(element);

        //We cannot check AbstractCanvasHandlerRegistrationControl.registerHandler(..) is called; so this is the next best thing
        assertTrue(control.isRegistered(element));

        verify(testShapeView).addHandler(eq(ViewEventType.TEXT_DBL_CLICK),
                                         textDoubleClickHandlerCaptor.capture());
        final TextDoubleClickHandler textDoubleClickHandler = textDoubleClickHandlerCaptor.getValue();
        textDoubleClickHandler.handle(new TextDoubleClickEvent(0, 1, X, Y));

        verify(control).show(eq(element),
                             eq(X),
                             eq(Y));
    }

    @Test
    public void testRegisterTextEnter() {
        control.init(canvasHandler);

        when(testShapeView.supports(ViewEventType.TEXT_ENTER)).thenReturn(true);

        control.register(element);

        //We cannot check AbstractCanvasHandlerRegistrationControl.registerHandler(..) is called; so this is the next best thing
        assertTrue(control.isRegistered(element));

        verify(testShapeView).addHandler(eq(ViewEventType.TEXT_ENTER),
                                         textEnterHandlerCaptor.capture());
        final TextEnterHandler textEnterHandler = textEnterHandlerCaptor.getValue();
        textEnterHandler.handle(new TextEnterEvent(0, 1, X, Y));
        verify(abstractCanvasView).setCursor(eq(AbstractCanvas.Cursors.TEXT));
    }

    @Test
    public void testRegisterTextExit() {
        control.init(canvasHandler);

        when(testShapeView.supports(ViewEventType.TEXT_EXIT)).thenReturn(true);

        control.register(element);

        //We cannot check AbstractCanvasHandlerRegistrationControl.registerHandler(..) is called; so this is the next best thing
        assertTrue(control.isRegistered(element));

        verify(testShapeView).addHandler(eq(ViewEventType.TEXT_EXIT),
                                         textExitHandlerCaptor.capture());
        final TextExitHandler textExitHandler = textExitHandlerCaptor.getValue();
        textExitHandler.handle(new TextExitEvent(0, 1, X, Y));
        verify(abstractCanvasView).setCursor(eq(AbstractCanvas.Cursors.DEFAULT));
    }

    @Test
    public void testShowWhenAlreadyShown() {
        control.init(canvasHandler);

        when(textEditorBox.isVisible()).thenReturn(true);

        control.show(element, X, Y);

        verify(textEditorBox).flush();
        assertShow();
    }

    @Test
    public void testShowWhenNotAlreadyShown() {
        control.init(canvasHandler);

        when(textEditorBox.isVisible()).thenReturn(false);

        control.show(element, X, Y);

        assertShow();
    }

    @Test
    public void testHideWhenIsVisible() {
        control.init(canvasHandler);

        control.show(element, X, Y);

        reset(textEditorBox, floatingView);

        when(textEditorBox.isVisible()).thenReturn(true);

        control.hide();

        assertHide(1);
    }

    @Test
    public void testHideWhenIsNotVisible() {
        control.init(canvasHandler);

        reset(textEditorBox, floatingView);

        when(textEditorBox.isVisible()).thenReturn(false);

        control.hide();

        assertHide(0);
    }

    @Test
    public void testSetCommandManagerProvider() {
        control.setCommandManagerProvider(commandManagerProvider);

        verify(textEditorBox).setCommandManagerProvider(eq(commandManagerProvider));
    }

    @Test
    public void testOnCanvasClearSelectionEvent() {
        control.onCanvasClearSelectionEvent(new CanvasClearSelectionEvent(canvasHandler));

        verify(control).flush();
    }

    @Test
    public void testOnCanvasShapeRemovedEvent() {
        control.onCanvasShapeRemovedEvent(new CanvasShapeRemovedEvent(canvas, shape));

        verify(control).flush();
    }

    @Test
    public void testOnCanvasFocusedEvent() {
        control.onCanvasFocusedEvent(new CanvasFocusedEvent(canvas));

        verify(control).flush();
    }

    @Test
    public void testOnCanvasSelectionEvent() {
        control.onCanvasSelectionEvent(new CanvasSelectionEvent(canvasHandler, UUID));

        verify(control).flush();
    }

    @Test
    public void testAllowOnlyVisualChanges() {
        final Element element = mock(Element.class);
        final Definition definition = mock(Definition.class);
        final DynamicReadOnly dynamicReadOnly = mock(DynamicReadOnly.class);
        when(element.getContent()).thenReturn(definition);
        when(definition.getDefinition()).thenReturn(dynamicReadOnly);

        boolean actual = control.allowOnlyVisualChanges(element);

        assertFalse(actual);

        when(dynamicReadOnly.isAllowOnlyVisualChange()).thenReturn(true);

        actual = control.allowOnlyVisualChanges(element);

        assertTrue(actual);
    }

    @Test
    public void testAllowOnlyVisualChangesDefaultValue(){
        final Element element = mock(Element.class);
        final boolean actual = control.allowOnlyVisualChanges(element);

        assertFalse(actual);
    }

    private void assertShow() {
        verify(testShapeView).setFillAlpha(eq(AbstractCanvasInPlaceTextEditorControl.SHAPE_EDIT_ALPHA));
        verify(testShapeView).setTitleAlpha(eq(AbstractCanvasInPlaceTextEditorControl.SHAPE_EDIT_ALPHA));

        verify(textEditorBox).show(eq(element));

        verify(floatingView).setX(eq(X));
        verify(floatingView).setY(eq(Y));
        verify(floatingView).setOffsetX(eq(-OFFSET_X));
        verify(floatingView).setOffsetY(eq(-OFFSET_Y));
        verify(floatingView).show();
    }

    private void assertHide(final int t) {
        verify(testShapeView, times(t)).setFillAlpha(eq(AbstractCanvasInPlaceTextEditorControl.SHAPE_NOT_EDIT_ALPHA));
        verify(testShapeView, times(t)).setTitleAlpha(eq(AbstractCanvasInPlaceTextEditorControl.SHAPE_NOT_EDIT_ALPHA));

        verify(textEditorBox, times(t)).hide();
        verify(floatingView, times(t)).hide();
    }
}
