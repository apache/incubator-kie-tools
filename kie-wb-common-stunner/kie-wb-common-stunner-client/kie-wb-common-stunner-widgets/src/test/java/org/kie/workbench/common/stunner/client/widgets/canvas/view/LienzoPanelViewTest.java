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

package org.kie.workbench.common.stunner.client.widgets.canvas.view;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.RootPanel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.shape.view.event.HandlerRegistrationImpl;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class LienzoPanelViewTest {

    @Mock
    private HandlerRegistrationImpl handlerRegistrationManager;

    @Mock
    private RootPanel rootPanel;

    @Mock
    private LienzoPanel lienzoPanel;

    private LienzoPanelView spyTested;

    @Before
    public void setup() {
        spyTested = Mockito.spy(new LienzoPanelView(200, 200, handlerRegistrationManager));
        when(spyTested.getRootPanel()).thenReturn(rootPanel);
    }

    @Test
    public void testInit() {
        spyTested.init(lienzoPanel);

        verify(spyTested).addFocusHandler(any(FocusHandler.class));
        verify(spyTested).addBlurHandler(any(BlurHandler.class));

        verify(spyTested).addMouseOverHandler(any(MouseOverHandler.class));
        verify(spyTested).addMouseOutHandler(any(MouseOutHandler.class));
        verify(spyTested).addMouseDownHandler(any(MouseDownHandler.class));
        verify(spyTested).addMouseUpHandler(any(MouseUpHandler.class));
        verify(spyTested).addMouseUpHandler(any(MouseUpHandler.class));

        verify(rootPanel).addDomHandler(any(KeyPressHandler.class), eq(KeyPressEvent.getType()));
        verify(rootPanel).addDomHandler(any(KeyUpHandler.class), eq(KeyUpEvent.getType()));
        verify(rootPanel).addDomHandler(any(KeyDownHandler.class), eq(KeyDownEvent.getType()));

        verify(handlerRegistrationManager, times(9)).register(any(HandlerRegistration.class));
    }
}
