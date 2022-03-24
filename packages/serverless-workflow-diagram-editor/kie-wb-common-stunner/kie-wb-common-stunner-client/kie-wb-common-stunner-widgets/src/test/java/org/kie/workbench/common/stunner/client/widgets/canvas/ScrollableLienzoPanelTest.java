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

import java.util.function.Supplier;

import com.ait.lienzo.client.widget.panel.LienzoBoundsPanel;
import com.ait.lienzo.client.widget.panel.impl.LienzoPanelScrollEvent;
import com.ait.lienzo.client.widget.panel.impl.ScrollablePanel;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ScrollableLienzoPanelTest {

    @Mock
    private StunnerLienzoBoundsPanel panel;

    private ScrollableLienzoPanel tested;

    @Mock
    private EventSourceMock<LienzoPanelScrollEvent> scrollEvent;

    @Before
    public void init() {
        tested = new ScrollableLienzoPanel(panel, scrollEvent);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInit() {
        tested.init();
        ArgumentCaptor<Supplier> builderCaptor = ArgumentCaptor.forClass(Supplier.class);
        verify(panel, times(1)).setPanelBuilder(builderCaptor.capture());
        Supplier<LienzoBoundsPanel> builder = builderCaptor.getValue();
        LienzoBoundsPanel result = builder.get();
        assertTrue(result instanceof ScrollableLienzoPanelView);
        assertEquals(0, result.getWidePx());
        assertEquals(0, result.getHighPx());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRefresh() {
        ScrollablePanel view = mock(ScrollablePanel.class);
        when(panel.getView()).thenReturn(view);
        tested.refresh();
        verify(view, times(1)).refresh();
    }
}
