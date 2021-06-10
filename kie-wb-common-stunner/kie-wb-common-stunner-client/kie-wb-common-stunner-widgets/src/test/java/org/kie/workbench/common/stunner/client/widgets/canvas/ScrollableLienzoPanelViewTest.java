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

package org.kie.workbench.common.stunner.client.widgets.canvas;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import elemental2.dom.EventListener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ScrollableLienzoPanelViewTest {

    @Mock
    private StunnerLienzoBoundsPanel presenter;

    private ScrollableLienzoPanelView tested;

    @Before
    @SuppressWarnings("unchecked")
    public void init() {
        tested = spy(new ScrollableLienzoPanelView());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetPresenter() {
        when(tested.isRemoteCommunicationEnabled()).thenReturn(true);
        tested.setPresenter(presenter);

        verify(presenter, times(1)).addKeyDownHandler(any(EventListener.class));
        verify(presenter, times(1)).addKeyPressHandler(any(EventListener.class));
        verify(presenter, times(1)).addKeyUpHandler(any(EventListener.class));
    }
}
