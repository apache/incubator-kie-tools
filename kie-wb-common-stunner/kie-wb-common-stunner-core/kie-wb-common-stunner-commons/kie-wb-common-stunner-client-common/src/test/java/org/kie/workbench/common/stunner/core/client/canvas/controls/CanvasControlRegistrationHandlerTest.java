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

package org.kie.workbench.common.stunner.core.client.canvas.controls;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.listener.CanvasElementListener;
import org.kie.workbench.common.stunner.core.client.canvas.listener.CanvasShapeListener;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CanvasControlRegistrationHandlerTest {

    private static final int CTROLS_SIZE = 5;

    @Mock
    AbstractCanvas canvas;

    @Mock
    AbstractCanvasHandler canvasHandler;

    private CanvasControlRegistrationHandler tested;
    private final List<CanvasControl<AbstractCanvas>> canvasControls = new ArrayList<>(CTROLS_SIZE);
    private final List<CanvasControl<AbstractCanvasHandler>> canvasHandlerControls = new ArrayList<>(CTROLS_SIZE);

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        this.tested = new CanvasControlRegistrationHandler<AbstractCanvas, AbstractCanvasHandler>(canvas,
                                                                                                  canvasHandler);
        for (int x = 0; x < CTROLS_SIZE; x++) {
            canvasControls.add(mock(CanvasControl.class));
            canvasHandlerControls.add(mock(CanvasControl.class));
        }
        canvasControls.forEach(tested::registerCanvasControl);
        canvasHandlerControls.forEach(tested::registerCanvasHandlerControl);
    }

    @Test
    public void testEnable() {
        tested.enable();
        verify(canvas,
               times(1)).addRegistrationListener(any(CanvasShapeListener.class));
        verify(canvasHandler,
               times(1)).addRegistrationListener(any(CanvasElementListener.class));
        canvasControls.forEach(c -> {
            verify(c,
                   times(1)).enable(eq(canvas));
        });
        canvasHandlerControls.forEach(c -> {
            verify(c,
                   times(1)).enable(eq(canvasHandler));
        });
    }

    @Test
    public void testDisable() {
        tested.enable();
        tested.disable();
        verify(canvas,
               times(1)).addRegistrationListener(any(CanvasShapeListener.class));
        verify(canvasHandler,
               times(1)).addRegistrationListener(any(CanvasElementListener.class));
        verify(canvas,
               times(1)).removeRegistrationListener(any(CanvasShapeListener.class));
        verify(canvasHandler,
               times(1)).removeRegistrationListener(any(CanvasElementListener.class));
        canvasControls.forEach(c -> {
            verify(c,
                   times(1)).enable(eq(canvas));
            verify(c,
                   times(1)).disable();
        });
        canvasHandlerControls.forEach(c -> {
            verify(c,
                   times(1)).enable(eq(canvasHandler));
            verify(c,
                   times(1)).disable();
        });
    }

    @Test
    public void testClear() {
        tested.enable();
        tested.clear();
        verify(canvas,
               times(1)).addRegistrationListener(any(CanvasShapeListener.class));
        verify(canvasHandler,
               times(1)).addRegistrationListener(any(CanvasElementListener.class));
        verify(canvas,
               times(1)).removeRegistrationListener(any(CanvasShapeListener.class));
        verify(canvasHandler,
               times(1)).removeRegistrationListener(any(CanvasElementListener.class));
        canvasControls.forEach(c -> {
            verify(c,
                   times(1)).enable(eq(canvas));
            verify(c,
                   times(1)).disable();
        });
        canvasHandlerControls.forEach(c -> {
            verify(c,
                   times(1)).enable(eq(canvasHandler));
            verify(c,
                   times(1)).disable();
        });
    }

    @Test
    public void testDestroy() {
        tested.enable();
        tested.destroy();
        verify(canvas,
               times(1)).addRegistrationListener(any(CanvasShapeListener.class));
        verify(canvasHandler,
               times(1)).addRegistrationListener(any(CanvasElementListener.class));
        verify(canvas,
               times(1)).removeRegistrationListener(any(CanvasShapeListener.class));
        verify(canvas,
               times(1)).clearRegistrationListeners();
        verify(canvasHandler,
               times(1)).clearRegistrationListeners();
        canvasControls.forEach(c -> {
            verify(c,
                   times(1)).enable(eq(canvas));
            verify(c,
                   times(1)).disable();
        });
        canvasHandlerControls.forEach(c -> {
            verify(c,
                   times(1)).enable(eq(canvasHandler));
            verify(c,
                   times(1)).disable();
        });
    }
}
