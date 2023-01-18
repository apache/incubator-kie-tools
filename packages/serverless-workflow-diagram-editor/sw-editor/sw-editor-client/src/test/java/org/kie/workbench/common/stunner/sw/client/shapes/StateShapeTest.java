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

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import elemental2.promise.Promise;
import org.appformer.kogito.bridge.client.resource.ResourceContentService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.sw.definition.Metadata;
import org.kie.workbench.common.stunner.sw.definition.State;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    private final String DEFAULT_EMPTY_COLOR = "#FFF";

    @Mock
    ResourceContentService kogitoService;

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

    public void simpleStateIconTest(String type, String color) {
        State state = createState(type);

        StateShape shape = new StateShape(state, kogitoService);
        assertFalse(shape.getView().isIconEmpty());
        assertEquals(color, shape.getView().getIconBackgroundColor());
    }

    private State createState(String type) {
        State state = new State();
        state.setName(NAME);
        state.setType(type);
        return state;
    }

    public void customTypeStateIconTest(String defaultType, String customType, String color) {
        State state = createState(defaultType);
        Metadata metadata = new Metadata();
        metadata.setType(customType);
        state.setMetadata(metadata);

        StateShape shape = new StateShape(state, kogitoService);
        assertFalse(shape.getView().isIconEmpty());
        assertEquals(color, shape.getView().getIconBackgroundColor());
    }

    @Test
    public void setNullPictureTest() {
        StateShape shape = spy(new StateShape(createState(INJECT), kogitoService));
        shape.setIconPicture(null, "icon.png");

        verify(shape, never()).setIconPicture(any());
    }

    @Test
    public void setEmptyPictureTest() {
        StateShape shape = spy(new StateShape(createState(INJECT), kogitoService));
        shape.setIconPicture("", "icon.png");

        verify(shape, never()).setIconPicture(any());
    }

    @Test
    public void setValidPictureTest() {
        StateShape shape = spy(new StateShape(createState(INJECT), kogitoService));
        shape.setIconPicture("base64string", "icon.png");

        verify(shape, times(1)).setIconPicture(any());
    }

    @Test
    public void customBase64IconStateIconTest() {
        State state = spy(createState(INJECT));
        Metadata metadata = new Metadata();
        metadata.setIcon("data://png..lalala");
        state.setMetadata(metadata);

        StateShape shape = new StateShape(state, kogitoService);
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

        StateShape shape = new StateShape(state, kogitoService);
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
}
