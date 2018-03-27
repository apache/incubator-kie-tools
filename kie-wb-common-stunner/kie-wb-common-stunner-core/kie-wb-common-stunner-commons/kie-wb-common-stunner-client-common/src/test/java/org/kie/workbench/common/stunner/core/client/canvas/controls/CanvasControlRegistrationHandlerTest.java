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

import org.jboss.errai.ioc.client.api.Disposer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.listener.CanvasElementListener;
import org.kie.workbench.common.stunner.core.client.canvas.listener.CanvasShapeListener;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CanvasControlRegistrationHandlerTest {

    private static final int CONTROLS_SIZE = 5;

    @Mock
    private AbstractCanvas canvas;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private ClientSession session;

    @Mock
    private MockAbstractCanvasSessionAwareControl sessionAwareControl;

    @Mock
    private Disposer disposer;

    private CanvasControlRegistrationHandler tested;
    private final List<CanvasControl<AbstractCanvas>> canvasControls = new ArrayList<>(CONTROLS_SIZE);
    private final List<CanvasControl<AbstractCanvasHandler>> canvasHandlerControls = new ArrayList<>(CONTROLS_SIZE);

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        this.tested = spy(new CanvasControlRegistrationHandler<>(canvas,
                                                                 canvasHandler,
                                                                 disposer));
        for (int x = 0; x < CONTROLS_SIZE; x++) {
            canvasControls.add(mock(CanvasControl.class));
            canvasHandlerControls.add(mock(CanvasControl.class));
        }
        canvasControls.add(sessionAwareControl);
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
        canvasControls.forEach(c -> verify(c, times(1)).enable(eq(canvas)));
        canvasHandlerControls.forEach(c -> verify(c,
                                                  times(1)).enable(eq(canvasHandler)));
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
        assertEquals(tested.getCanvasControls().size(), canvasControls.size());
        assertEquals(tested.getCanvasHandlerControls().size(), canvasHandlerControls.size());
        tested.clear();
        assertEquals(tested.getCanvasControls().size(), 0);
        assertEquals(tested.getCanvasHandlerControls().size(), 0);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDestroy() {
        tested.enable();
        tested.destroy();

        verify(tested).clear();
        verify(canvas).clearRegistrationListeners();
        verify(canvasHandler).clearRegistrationListeners();
        canvasControls.forEach(control -> verify(disposer).dispose(control));
        canvasHandlerControls.forEach(control -> verify(disposer).dispose(control));
    }

    @Test
    public void checkBindSessionOnSessionAwareControls() {
        tested.bind(session);

        verify(sessionAwareControl,
               times(1)).bind(eq(session));
    }

    @Test
    public void checkUnbindSessionOnSessionAwareControls() {
        tested.unbind();

        verify(sessionAwareControl,
               times(1)).unbind();
    }

    private interface MockAbstractCanvasSessionAwareControl extends CanvasControl<AbstractCanvas>,
                                                                    CanvasControl.SessionAware<ClientSession> {

    }
}
