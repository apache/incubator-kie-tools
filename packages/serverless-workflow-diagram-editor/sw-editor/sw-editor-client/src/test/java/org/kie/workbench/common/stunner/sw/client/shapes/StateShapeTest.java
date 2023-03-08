/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.stunner.sw.client.shapes;

import com.ait.lienzo.client.core.event.NodeMouseExitEvent;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import elemental2.promise.Promise;
import org.appformer.kogito.bridge.client.resource.ResourceContentService;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.client.shape.impl.ShapeStateHandler;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.sw.definition.CallbackState;
import org.kie.workbench.common.stunner.sw.definition.EventState;
import org.kie.workbench.common.stunner.sw.definition.ForEachState;
import org.kie.workbench.common.stunner.sw.definition.InjectState;
import org.kie.workbench.common.stunner.sw.definition.Metadata;
import org.kie.workbench.common.stunner.sw.definition.OperationState;
import org.kie.workbench.common.stunner.sw.definition.ParallelState;
import org.kie.workbench.common.stunner.sw.definition.SleepState;
import org.kie.workbench.common.stunner.sw.definition.State;
import org.kie.workbench.common.stunner.sw.definition.SwitchState;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.sw.client.shapes.StateShapeView.STATE_SHAPE_HEIGHT;
import static org.kie.workbench.common.stunner.sw.client.shapes.StateShapeView.STATE_SHAPE_WIDTH;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class StateShapeTest {

    private final String NAME = "test_name";
    private final String INJECT = "inject";
    private final String SWITCH = "switch";
    private final String OPERATION = "operation";
    private final String EVENT = "event";
    private final String CALLBACK = "callback";
    private final String FOREACH = "foreach";
    private final String PARALLEL = "parallel";
    private final String SLEEP = "sleep";
    private final String ANSIBLE = "ansible";
    private final String KAOTO = "kaoto";
    private final String IMAGE_DATA = "imageData";
    private final String INJECT_COLOR = "#8BC1F7";
    private final String SWITCH_COLOR = "#009596";
    private final String OPERATION_COLOR = "#0066CC";
    private final String EVENT_COLOR = "#F4C145";
    private final String CALLBACK_COLOR = "#EC7A08";
    private final String FOR_EACH_COLOR = "#8F4700";
    private final String PARALLEL_COLOR = "#4CB140";
    private final String SLEEP_COLOR = "#5752D1";
    private final String ANSIBLE_COLOR = "#BB271A";
    private final String KAOTO_COLOR = "#332174";
    private final int SHAPE_X = 50;
    private final int SHAPE_Y = 50;

    @Mock
    ResourceContentService kogitoService;

    @Mock
    private TranslationService translationService;

    @Test
    public void injectStateColorTest() {
        simpleStateIconTest(INJECT, INJECT_COLOR);
    }

    @Test
    public void switchStateColorTest() {
        simpleStateIconTest(SWITCH, SWITCH_COLOR);
    }

    @Test
    public void operationStateColorTest() {
        simpleStateIconTest(OPERATION, OPERATION_COLOR);
    }

    @Test
    public void eventStateColorTest() {
        simpleStateIconTest(EVENT, EVENT_COLOR);
    }

    @Test
    public void callbackStateColorTest() {
        simpleStateIconTest(CALLBACK, CALLBACK_COLOR);
    }

    @Test
    public void forEachStateColorTest() {
        simpleStateIconTest(FOREACH, FOR_EACH_COLOR);
    }

    @Test
    public void parallelStateColorTest() {
        simpleStateIconTest(PARALLEL, PARALLEL_COLOR);
    }

    @Test
    public void sleepStateColorTest() {
        simpleStateIconTest(SLEEP, SLEEP_COLOR);
    }

    @Test
    public void kaotoStateColorTest() {
        customTypeStateIconTest(INJECT, KAOTO, KAOTO_COLOR);
    }

    @Test
    public void ansibleStateColorTest() {
        customTypeStateIconTest(ANSIBLE, ANSIBLE, ANSIBLE_COLOR);
    }

    @Test
    public void setNullPictureTest() {
        StateShape shape = spy(StateShape.create(createState(INJECT), kogitoService, translationService));
        shape.setIconPicture(null, "icon.png");

        verify(shape, never()).setIconPicture(any());
    }

    @Test
    public void setEmptyPictureTest() {
        StateShape shape = spy(StateShape.create(createState(INJECT), kogitoService, translationService));
        shape.setIconPicture("", "icon.png");

        verify(shape, never()).setIconPicture(any());
    }

    @Test
    public void setValidPictureTest() {
        StateShape shape = spy(StateShape.create(createState(INJECT), kogitoService, translationService));
        shape.setIconPicture("base64string", "icon.png");

        verify(shape, times(1)).setIconPicture(any());
    }

    @Test
    public void customBase64IconStateIconTest() {
        State state = spy(createState(INJECT));
        Metadata metadata = new Metadata();
        metadata.setIcon("data://png..lalala");
        state.setMetadata(metadata);

        StateShape shape = StateShape.create(state, kogitoService, translationService);
        assertTrue(shape.getView().isIconEmpty());
        assertNull(shape.getView().getIconBackgroundColor());

        shape.applyProperties(createElement(state), null);
        assertFalse(shape.getView().isIconEmpty());
        assertEquals("#FFF", shape.getView().getIconBackgroundColor());
    }

    @Test
    public void invalidIconStateTest() {
        State state = spy(createState(INJECT));
        Metadata metadata = new Metadata();
        metadata.setIcon("png..lalala");
        state.setMetadata(metadata);

        when(kogitoService.get(eq("png..lalala"), any())).thenReturn(new Promise<>((resolve, reject) -> {
        }));

        StateShape shape = StateShape.create(state, kogitoService, translationService);
        assertTrue(shape.getView().isIconEmpty());
        assertNull(shape.getView().getIconBackgroundColor());

        shape.applyProperties(createElement(state), null);
        assertFalse(shape.getView().isIconEmpty());
        assertEquals(INJECT_COLOR, shape.getView().getIconBackgroundColor());
    }

    @Test
    public void scaleLargeWidthSmallHeightTest() {
        assertEquals(2.0,
                     StateShape.calculateIconScale((int) StateShapeView.STATE_SHAPE_ICON_RADIUS * 4,
                                                   (int) StateShapeView.STATE_SHAPE_ICON_RADIUS),
                     0.0);
    }

    @Test
    public void scaleLargeHeightSmallWidthTest() {
        assertEquals(2.0,
                     StateShape.calculateIconScale((int) StateShapeView.STATE_SHAPE_ICON_RADIUS,
                                                   (int) StateShapeView.STATE_SHAPE_ICON_RADIUS * 4),
                     0.0);
    }

    @Test
    public void scaleLargeWidthLargerHeightTest() {
        assertEquals(0.5,
                     StateShape.calculateIconScale((int) StateShapeView.STATE_SHAPE_ICON_RADIUS * 4,
                                                   (int) StateShapeView.STATE_SHAPE_ICON_RADIUS * 8),
                     0.0);
    }

    @Test
    public void scaleLargeHeightLargerWidthTest() {
        assertEquals(0.5,
                     StateShape.calculateIconScale((int) StateShapeView.STATE_SHAPE_ICON_RADIUS * 8,
                                                   (int) StateShapeView.STATE_SHAPE_ICON_RADIUS * 4),
                     0.0);
    }

    @Test
    public void base64StringFromLongPathGenerationTest() {
        assertEquals("data:image/png;base64, " + IMAGE_DATA, StateShape.iconDataUri("path/to/image.png", IMAGE_DATA));
    }

    @Test
    public void base64StringFromRootGenerationTest() {
        assertEquals("data:image/jpeg;base64, " + IMAGE_DATA, StateShape.iconDataUri("image.jpeg", IMAGE_DATA));
    }

    @Test
    public void base64StringFromIncorrectPathGenerationTest() {
        assertEquals(IMAGE_DATA, StateShape.iconDataUri("imagejpeg", IMAGE_DATA));
    }

    @Test
    public void leftTopCornerOutsideTest() {
        assertTrue(isCoordinateOutsideTest(0.5, 0.5));
    }

    @Test
    public void leftTopCornerInsideTest() {
        assertFalse(isCoordinateOutsideTest(6, 6));
    }

    @Test
    public void rightTopCornerOutsideTest() {
        assertTrue(isCoordinateOutsideTest(STATE_SHAPE_WIDTH - 0.5, 0.5));
    }

    @Test
    public void rightTopCornerInsideTest() {
        assertFalse(isCoordinateOutsideTest(STATE_SHAPE_WIDTH - 6, 6));
    }

    @Test
    public void leftBottomCornerOutsideTest() {
        assertTrue(isCoordinateOutsideTest(0.5, STATE_SHAPE_HEIGHT - 0.5));
    }

    @Test
    public void leftBottomCornerInsideTest() {
        assertFalse(isCoordinateOutsideTest(6, STATE_SHAPE_HEIGHT - 6));
    }

    @Test
    public void rightBottomCornerOutsideTest() {
        assertTrue(isCoordinateOutsideTest(STATE_SHAPE_WIDTH - 0.5, STATE_SHAPE_HEIGHT - 0.5));
    }

    @Test
    public void rightBottomCornerInsideTest() {
        assertFalse(isCoordinateOutsideTest(STATE_SHAPE_WIDTH - 6, STATE_SHAPE_HEIGHT - 6));
    }

    @Test
    public void exitFromSelectedShapeTest() {
        ShapeStateHandler stateHandler = mock(ShapeStateHandler.class);

        StateShape shape = prepareShapeForExitTests(stateHandler, ShapeState.SELECTED);
        NodeMouseExitEvent exitEvent = prepareMouseExitEvent(49, 49);

        shape.getExitHandler().onNodeMouseExit(exitEvent);

        verify(stateHandler, never()).applyState(any());
    }

    @Test
    public void exitFromTheShapeToTopTest() {
        ShapeStateHandler stateHandler = mock(ShapeStateHandler.class);

        StateShape shape = prepareShapeForExitTests(stateHandler, ShapeState.HIGHLIGHT);
        NodeMouseExitEvent exitEvent = prepareMouseExitEvent(55, 49);

        shape.getExitHandler().onNodeMouseExit(exitEvent);

        verify(stateHandler, times(1)).applyState(ShapeState.NONE);
    }

    @Test
    public void exitFromTheLeftShapeTest() {
        ShapeStateHandler stateHandler = mock(ShapeStateHandler.class);

        StateShape shape = prepareShapeForExitTests(stateHandler, ShapeState.HIGHLIGHT);
        NodeMouseExitEvent exitEvent = prepareMouseExitEvent(SHAPE_X - 5, SHAPE_Y + 5);

        shape.getExitHandler().onNodeMouseExit(exitEvent);

        verify(stateHandler, times(1)).applyState(ShapeState.NONE);
    }

    @Test
    public void exitFromTheRightShapeTest() {
        ShapeStateHandler stateHandler = mock(ShapeStateHandler.class);

        StateShape shape = prepareShapeForExitTests(stateHandler, ShapeState.HIGHLIGHT);
        NodeMouseExitEvent exitEvent = prepareMouseExitEvent((int) STATE_SHAPE_WIDTH + SHAPE_X + 5, SHAPE_Y + 5);

        shape.getExitHandler().onNodeMouseExit(exitEvent);

        verify(stateHandler, times(1)).applyState(ShapeState.NONE);
    }

    @Test
    public void exitFromTheBottomShapeTest() {
        ShapeStateHandler stateHandler = mock(ShapeStateHandler.class);

        StateShape shape = prepareShapeForExitTests(stateHandler, ShapeState.HIGHLIGHT);
        NodeMouseExitEvent exitEvent = prepareMouseExitEvent(SHAPE_X + 5, (int) STATE_SHAPE_HEIGHT + SHAPE_Y + 5);

        shape.getExitHandler().onNodeMouseExit(exitEvent);

        verify(stateHandler, times(1)).applyState(ShapeState.NONE);
    }

    @Test
    public void raisedExitInsideOfTheShapeTest() {
        ShapeStateHandler stateHandler = mock(ShapeStateHandler.class);

        StateShape shape = prepareShapeForExitTests(stateHandler, ShapeState.HIGHLIGHT);
        NodeMouseExitEvent exitEvent = prepareMouseExitEvent(SHAPE_X + 5, SHAPE_Y + 5);

        shape.getExitHandler().onNodeMouseExit(exitEvent);

        verify(stateHandler, never()).applyState(ShapeState.NONE);
    }

    @SuppressWarnings("rawtypes")
    private NodeMouseExitEvent prepareMouseExitEvent(int mouseX, int mouseY) {
        NodeMouseExitEvent exitEvent = mock(NodeMouseExitEvent.class);
        when(exitEvent.getX()).thenReturn(mouseX);
        when(exitEvent.getY()).thenReturn(mouseY);
        com.ait.lienzo.client.core.shape.Node node = mock(com.ait.lienzo.client.core.shape.Node.class);
        when(exitEvent.getSource()).thenReturn(node);
        com.ait.lienzo.client.core.types.Point2D point = new com.ait.lienzo.client.core.types.Point2D(SHAPE_X, SHAPE_Y);
        when(node.getAbsoluteLocation()).thenReturn(point);
        return exitEvent;
    }

    private StateShape prepareShapeForExitTests(ShapeStateHandler stateHandler, ShapeState currentState) {
        State state = createState(INJECT);
        StateShape shape = spy(StateShape.create(state, kogitoService, translationService));
        shape.applyProperties(createElement(state), null);

        when(stateHandler.getShapeState()).thenReturn(currentState);
        when(shape.getShapeStateHandler()).thenReturn(stateHandler);

        return shape;
    }

    private void simpleStateIconTest(String type, String color) {
        State state = createState(type);

        StateShape shape = StateShape.create(state, kogitoService, translationService);
        assertTrue(shape.getView().isIconEmpty());
        assertNull(shape.getView().getIconBackgroundColor());

        shape.applyProperties(createElement(state), null);
        assertFalse(shape.getView().isIconEmpty());
        assertEquals(color, shape.getView().getIconBackgroundColor());
    }

    private State createState(String type) {
        State state;
        switch (type) {
            case "inject":
                state = new InjectState();
                break;
            case "switch":
                state = new SwitchState();
                break;
            case "operation":
                state = new OperationState();
                break;
            case "event":
                state = new EventState();
                break;
            case "callback":
                state = new CallbackState();
                break;
            case "foreach":
                state = new ForEachState();
                break;
            case "parallel":
                state = new ParallelState();
                break;
            case "sleep":
                state = new SleepState();
                break;
            default:
                state = new State();
        }
        state.setName(NAME);
        state.setType(type);
        return state;
    }

    private void customTypeStateIconTest(String defaultType, String customType, String color) {
        State state = createState(defaultType);
        Metadata metadata = new Metadata();
        metadata.setType(customType);
        state.setMetadata(metadata);

        StateShape shape = StateShape.create(state, kogitoService, translationService);
        assertTrue(shape.getView().isIconEmpty());
        assertNull(shape.getView().getIconBackgroundColor());

        shape.applyProperties(createElement(state), null);
        assertFalse(shape.getView().isIconEmpty());
        assertEquals(color, shape.getView().getIconBackgroundColor());
    }

    private Node<View<State>, Edge> createElement(State state) {
        StateView view = new StateView(state);
        return new StateNode(view);
    }

    private boolean isCoordinateOutsideTest(double x, double y) {
        return StateShape.isOutsideOfCorners(x, y, STATE_SHAPE_WIDTH, STATE_SHAPE_HEIGHT);
    }
}
