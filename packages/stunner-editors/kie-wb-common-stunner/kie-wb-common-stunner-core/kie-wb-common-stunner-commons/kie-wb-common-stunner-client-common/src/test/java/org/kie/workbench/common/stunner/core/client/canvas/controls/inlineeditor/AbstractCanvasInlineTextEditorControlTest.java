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

package org.kie.workbench.common.stunner.core.client.canvas.controls.inlineeditor;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.adf.definitions.DynamicReadOnly;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Transform;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.RequiresCommandManager;
import org.kie.workbench.common.stunner.core.client.components.views.FloatingView;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.BoundingBox;
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
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractCanvasInlineTextEditorControlTest<C extends AbstractCanvasInlineTextEditorControl> {

    private static final String UUID = "uuid";
    private static final double CANVAS_X = 0d;
    private static final double CANVAS_Y = 0d;
    private static final double CANVAS_WIDTH = 700d;
    private static final double CANVAS_HEIGHT = 500d;
    private static final double SHAPE_X = 100d;
    private static final double SHAPE_Y = 200d;
    private static final double SCROLL_X = 0d;
    private static final double SCROLL_Y = 0d;
    private static final double BOUNDING_BOX_WIDTH = 150d;
    private static final double BOUNDING_BOX_HEIGHT = 150d;
    private static final double ZOOM = 1d;
    private static final double FONT_SIZE = 16d;
    private static final String FONT_FAMILY = "Open Sans";
    private static final String ALIGN_MIDDLE = "MIDDLE";
    private static final String ALIGN_LEFT = "LEFT";
    private static final String ALIGN_TOP = "TOP";
    private static final String POSITION_INSIDE = "INSIDE";
    private static final String POSITION_OUTSIDE = "OUTSIDE";
    private static final String ORIENTATION_VERTICAL = "VERTICAL";
    private static final String ORIENTATION_HORIZONTAL = "HORIZONTAL";

    @Mock
    protected FloatingView<IsWidget> floatingView;

    @Mock
    protected TextEditorBox<AbstractCanvasHandler, Element> textEditorBox;

    @Mock
    protected HTMLElement textEditBoxElement;

    @Mock
    protected IsWidget textEditBoxWidget;

    @Mock
    protected EditorSession session;

    @Mock
    protected KeyboardControl<AbstractCanvas, ClientSession> keyboardControl;

    @Mock
    protected AbstractCanvasHandler canvasHandler;

    @Mock
    protected AbstractCanvas canvas;

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
    protected Transform transform;

    @Mock
    protected HasTitle hasTitle;

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

    protected BoundingBox boundingBox;

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
        when(element.getUUID()).thenReturn(UUID);
        when(element.getContent()).thenReturn(shapeView);
        when(shapeView.getBounds()).thenReturn(shapeViewBounds);
        when(canvasHandler.getCanvas()).thenReturn(canvas);
        when(canvasHandler.getAbstractCanvas()).thenReturn(abstractCanvas);
        when(abstractCanvas.getView()).thenReturn(abstractCanvasView);
        when(canvas.getShape(eq(UUID))).thenReturn(shape);
        when(shape.getUUID()).thenReturn(UUID);
        when(shape.getShapeView()).thenReturn(testShapeView);
        when(session.getKeyboardControl()).thenReturn(keyboardControl);

        this.control = spy(getControl());

        doAnswer(i -> {
            ((Scheduler.ScheduledCommand) i.getArguments()[0]).execute();
            return null;
        }).when(control).scheduleDeferredCommand(any(Scheduler.ScheduledCommand.class));

        doReturn(textEditBoxWidget).when(control).wrapTextEditorBoxElement(eq(textEditBoxElement));
        doAnswer(i -> abstractCanvas).when(control).getAbstractCanvas();
        doAnswer(i -> hasTitle).when(control).getHasTitle();
        doNothing().when(control).setMouseWheelHandler();
    }

    private void initCanvas(final double canvasX,
                            final double canvasY,
                            final double canvasWidth,
                            final double canvasHeight) {
        when(abstractCanvasView.getAbsoluteLocation()).thenReturn(new Point2D(canvasX, canvasY));
        doAnswer(i -> canvasWidth).when(control).getCanvasAbsoluteWidth();
        doAnswer(i -> canvasHeight).when(control).getCanvasAbsoluteHeight();
    }

    private void initShape(final double x,
                           final double y,
                           final double scrollX,
                           final double scrollY,
                           final double zoom) {
        boundingBox = new BoundingBox(0, 0, BOUNDING_BOX_WIDTH, BOUNDING_BOX_HEIGHT);
        when(testShapeView.getShapeAbsoluteLocation()).thenReturn(new Point2D(x, y));
        when(testShapeView.getBoundingBox()).thenReturn(boundingBox);
        when(canvas.getTransform()).thenReturn(transform);
        when(transform.getTranslate()).thenReturn(new Point2D(scrollX, scrollY));
        when(transform.getScale()).thenReturn(new Point2D(zoom, zoom));
    }

    private void initHasTitle(final String titlePosition,
                              final String orientation,
                              final String fontFamily,
                              final String fontAlignment,
                              final double fontSize,
                              final double marginX) {
        when(hasTitle.getTitlePosition()).thenReturn(titlePosition);
        when(hasTitle.getOrientation()).thenReturn(orientation);
        when(hasTitle.getFontAlignment()).thenReturn(fontAlignment);
        when(hasTitle.getMarginX()).thenReturn(marginX);
        when(hasTitle.getTitleFontSize()).thenReturn(fontSize);
        when(hasTitle.getTitleFontFamily()).thenReturn(fontFamily);
    }

    protected abstract C getControl();

    @Test
    public void testBind() {
        control.bind(session);
        verify(keyboardControl, times(2))
                .addKeyShortcutCallback(any(KeyboardControl.KeyShortcutCallback.class));
    }

    @Test
    public void testBindKeyControlHandledKey() {
        control.bind(session);
        verify(keyboardControl, times(2))
                .addKeyShortcutCallback(keyShortcutCallbackCaptor.capture());

        final KeyboardControl.KeyShortcutCallback keyShortcutCallback = keyShortcutCallbackCaptor.getValue();
        keyShortcutCallback.onKeyShortcut(KeyboardEvent.Key.ESC);
        verify(control).onKeyDownEvent(eq(KeyboardEvent.Key.ESC));
        verify(control).rollback();
    }

    @Test
    public void testBindKeyControlUnhandledKey() {
        control.bind(session);
        verify(keyboardControl, times(2))
                .addKeyShortcutCallback(keyShortcutCallbackCaptor.capture());

        final KeyboardControl.KeyShortcutCallback keyShortcutCallback = keyShortcutCallbackCaptor.getValue();
        keyShortcutCallback.onKeyShortcut(KeyboardEvent.Key.ARROW_DOWN);
        verify(control).onKeyDownEvent(eq(KeyboardEvent.Key.ARROW_DOWN));
        verify(control, never()).hide();
    }

    @Test
    public void testEnable() {
        control.bind(session);
        control.init(canvasHandler);
        verify(textEditorBox).initialize(eq(canvasHandler),
                                         any(Command.class));
        verify(floatingView).hide();
        verify(floatingView).add(textEditBoxWidget);
    }

    @Test
    public void testEnableCloseCallback() {
        control.bind(session);
        control.init(canvasHandler);

        verify(textEditorBox).initialize(eq(canvasHandler),
                                         commandCaptor.capture());

        final Command command = commandCaptor.getValue();
        command.execute();

        verify(control).hide();
    }

    @Test
    public void testRegisterDoubleClickHandler() {
        initCanvas(CANVAS_X, CANVAS_Y, CANVAS_WIDTH, CANVAS_HEIGHT);
        initShape(SHAPE_X, SHAPE_Y, SCROLL_X, SCROLL_Y, ZOOM);
        initHasTitle(POSITION_INSIDE, ORIENTATION_HORIZONTAL, FONT_FAMILY, ALIGN_MIDDLE, FONT_SIZE, 0);
        control.bind(session);
        control.init(canvasHandler);

        when(testShapeView.supports(ViewEventType.TEXT_DBL_CLICK)).thenReturn(true);

        control.register(element);
        assertTrue(control.isRegistered(element));
        verify(testShapeView).addHandler(eq(ViewEventType.TEXT_DBL_CLICK),
                                         textDoubleClickHandlerCaptor.capture());

        final TextDoubleClickHandler textDoubleClickHandler = textDoubleClickHandlerCaptor.getValue();
        textDoubleClickHandler.handle(new TextDoubleClickEvent(0, 1, SHAPE_X, SHAPE_Y));
        verify(control).show(eq(element));
    }

    @Test
    public void testRegisterTextEnter() {
        control.init(canvasHandler);
        when(testShapeView.supports(ViewEventType.TEXT_ENTER)).thenReturn(true);

        control.register(element);
        assertTrue(control.isRegistered(element));
        verify(testShapeView).addHandler(eq(ViewEventType.TEXT_ENTER),
                                         textEnterHandlerCaptor.capture());

        final TextEnterHandler textEnterHandler = textEnterHandlerCaptor.getValue();
        textEnterHandler.handle(new TextEnterEvent(0, 1, SHAPE_X, SHAPE_Y));
        verify(abstractCanvasView).setCursor(eq(AbstractCanvas.Cursors.TEXT));
    }

    @Test
    public void testRegisterTextExit() {
        control.init(canvasHandler);
        when(testShapeView.supports(ViewEventType.TEXT_EXIT)).thenReturn(true);

        control.register(element);
        assertTrue(control.isRegistered(element));
        verify(testShapeView).addHandler(eq(ViewEventType.TEXT_EXIT),
                                         textExitHandlerCaptor.capture());

        final TextExitHandler textExitHandler = textExitHandlerCaptor.getValue();
        textExitHandler.handle(new TextExitEvent(0, 1, SHAPE_X, SHAPE_Y));
        verify(abstractCanvasView).setCursor(eq(AbstractCanvas.Cursors.DEFAULT));
    }

    @Test
    public void testShowInsideMiddleShape() {
        initCanvas(CANVAS_X, CANVAS_Y, CANVAS_WIDTH, CANVAS_HEIGHT);
        initShape(SHAPE_X, SHAPE_Y, SCROLL_X, SCROLL_Y, ZOOM);
        initHasTitle(POSITION_INSIDE, ORIENTATION_HORIZONTAL, FONT_FAMILY, ALIGN_MIDDLE, FONT_SIZE, 0);
        control.isMultiline = true;
        control.bind(session);

        control.init(canvasHandler);
        when(textEditorBox.isVisible()).thenReturn(false);
        control.show(element);

        assertShow(true, ALIGN_MIDDLE, POSITION_INSIDE);
    }

    @Test
    public void testShowOutsideShape() {
        initCanvas(CANVAS_X, CANVAS_Y, CANVAS_WIDTH, CANVAS_HEIGHT);
        initShape(SHAPE_X, SHAPE_Y, SCROLL_X, SCROLL_Y, ZOOM);
        initHasTitle(POSITION_OUTSIDE, ORIENTATION_HORIZONTAL, FONT_FAMILY, ALIGN_TOP, FONT_SIZE, 0);
        control.isMultiline = true;
        control.bind(session);

        control.init(canvasHandler);
        when(textEditorBox.isVisible()).thenReturn(false);
        control.show(element);

        assertShow(true, ALIGN_TOP, POSITION_OUTSIDE);
    }

    @Test
    public void testShowInsideLeftShape() {
        initCanvas(CANVAS_X, CANVAS_Y, CANVAS_WIDTH, CANVAS_HEIGHT);
        initShape(SHAPE_X, SHAPE_Y, SCROLL_X, SCROLL_Y, ZOOM);
        initHasTitle(POSITION_INSIDE, ORIENTATION_VERTICAL, FONT_FAMILY, ALIGN_TOP, FONT_SIZE, 0);
        control.isMultiline = true;
        control.bind(session);

        control.init(canvasHandler);

        when(textEditorBox.isVisible()).thenReturn(false);
        control.show(element);
        assertShow(true, ALIGN_LEFT, POSITION_INSIDE);
    }

    @Test
    public void testShowInsideTopShape() {
        initCanvas(CANVAS_X, CANVAS_Y, CANVAS_WIDTH, CANVAS_HEIGHT);
        initShape(SHAPE_X, SHAPE_Y, SCROLL_X, SCROLL_Y, ZOOM);
        initHasTitle(POSITION_INSIDE, ORIENTATION_HORIZONTAL, FONT_FAMILY, ALIGN_TOP, FONT_SIZE, 0);
        control.isMultiline = true;
        control.bind(session);

        control.init(canvasHandler);
        when(textEditorBox.isVisible()).thenReturn(false);
        control.show(element);

        assertShow(true, ALIGN_TOP, POSITION_INSIDE);
    }

    @Test
    public void testShowWhenAlreadyShown() {
        initCanvas(CANVAS_X, CANVAS_Y, CANVAS_WIDTH, CANVAS_HEIGHT);
        initShape(SHAPE_X, SHAPE_Y, SCROLL_X, SCROLL_Y, ZOOM);
        initHasTitle(POSITION_INSIDE, ORIENTATION_HORIZONTAL, FONT_FAMILY, ALIGN_MIDDLE, FONT_SIZE, 0);
        control.isMultiline = true;
        control.bind(session);

        control.init(canvasHandler);
        when(textEditorBox.isVisible()).thenReturn(true);
        control.show(element);

        assertShow(true, ALIGN_MIDDLE, POSITION_INSIDE);
    }

    @Test
    public void testHideWhenIsVisible() {
        initCanvas(CANVAS_X, CANVAS_Y, CANVAS_WIDTH, CANVAS_HEIGHT);
        initShape(SHAPE_X, SHAPE_Y, SCROLL_X, SCROLL_Y, ZOOM);
        initHasTitle(POSITION_INSIDE, ORIENTATION_HORIZONTAL, FONT_FAMILY, ALIGN_MIDDLE, FONT_SIZE, 0);
        control.bind(session);

        control.init(canvasHandler);

        control.show(element);
        reset(textEditorBox, floatingView);
        when(textEditorBox.isVisible()).thenReturn(true);
        control.hide();

        assertHide(1);
    }

    @Test
    public void testHideWhenIsNotVisible() {
        initCanvas(CANVAS_X, CANVAS_Y, CANVAS_WIDTH, CANVAS_HEIGHT);
        initShape(SHAPE_X, SHAPE_Y, SCROLL_X, SCROLL_Y, ZOOM);
        initHasTitle(POSITION_INSIDE, ORIENTATION_HORIZONTAL, FONT_FAMILY, ALIGN_MIDDLE, FONT_SIZE, 0);
        control.bind(session);

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
    public void testAllowOnlyVisualChangesDefaultValue() {
        final Element element = mock(Element.class);
        final boolean actual = control.allowOnlyVisualChanges(element);
        assertFalse(actual);
    }

    private void assertShow(final boolean multiline, final String textBoxAlignment, final String position) {
        final HasTitle hasTitle = (HasTitle) testShapeView;

        verify(testShapeView).setFillAlpha(eq(AbstractCanvasInlineTextEditorControl.SHAPE_EDIT_ALPHA));
        verify(testShapeView).setTitleAlpha(eq(AbstractCanvasInlineTextEditorControl.TITLE_EDIT_ALPHA));
        verify(textEditorBox).show(eq(element), anyDouble(), anyDouble());
        verify(textEditorBox).setFontFamily(FONT_FAMILY);
        verify(textEditorBox).setFontSize(FONT_SIZE);
        verify(textEditorBox).setMultiline(multiline);
        verify(textEditorBox).setTextBoxInternalAlignment(textBoxAlignment);
        verify(floatingView).clearTimeOut();
        verify(floatingView).show();

        //check inlineEditor position
        if (position.equals(POSITION_INSIDE) && textBoxAlignment.equals(ALIGN_MIDDLE)) {
            verify(floatingView).setX(eq(SHAPE_X));
            verify(floatingView).setY(eq(SHAPE_Y));
        } else if (position.equals(POSITION_INSIDE) && textBoxAlignment.equals(ALIGN_LEFT)) {
            verify(floatingView).setX(eq(100d));
            verify(floatingView).setY(eq(275d));
        } else if (position.equals(POSITION_INSIDE) && textBoxAlignment.equals(ALIGN_TOP)) {
            verify(floatingView).setX(eq(175d));
            verify(floatingView).setY(eq(200d));
        } else if (position.equals(POSITION_OUTSIDE)) {
            verify(floatingView).setX(eq(25d));
            verify(floatingView).setY(eq(350d));
        }

        // Update Shape UI
        verify(hasTitle).batch();
    }

    private void assertHide(final int t) {
        verify(testShapeView, times(t)).setFillAlpha(eq(AbstractCanvasInlineTextEditorControl.NOT_EDIT_ALPHA));
        verify(testShapeView, times(t)).setTitleAlpha(eq(AbstractCanvasInlineTextEditorControl.NOT_EDIT_ALPHA));
        verify(textEditorBox, times(t)).hide();
        verify(floatingView, times(t)).hide();
    }
}