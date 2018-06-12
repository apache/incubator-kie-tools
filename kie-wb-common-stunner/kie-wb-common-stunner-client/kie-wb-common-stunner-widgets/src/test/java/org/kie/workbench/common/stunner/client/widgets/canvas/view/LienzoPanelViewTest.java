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
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.shape.view.event.HandlerRegistrationImpl;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class LienzoPanelViewTest {

    @Mock
    private HandlerRegistrationImpl handlerRegistrationManager;

    private LienzoPanelView spyTested;

    @Before
    public void setup() {
        spyTested = Mockito.spy(new LienzoPanelView(200, 200, handlerRegistrationManager));
    }

    @Test
    public void testInitHandlers() {
        spyTested.initHandlers();

        verify(spyTested).addMouseDownHandler(any(MouseDownHandler.class));
        verify(spyTested).addMouseUpHandler(any(MouseUpHandler.class));

        verify(spyTested).addKeyPressHandler(any(KeyPressHandler.class));
        verify(spyTested).addKeyDownHandler(any(KeyDownHandler.class));
        verify(spyTested).addKeyUpHandler(any(KeyUpHandler.class));

        verify(handlerRegistrationManager, times(5)).register(any(HandlerRegistration.class));
    }
}
