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

import java.util.OptionalInt;
import java.util.function.BiFunction;

import com.ait.lienzo.client.widget.panel.LienzoBoundsPanel;
import com.ait.lienzo.client.widget.panel.impl.PreviewPanel;
import com.ait.lienzo.client.widget.panel.scrollbars.ScrollablePanel;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class PreviewLienzoPanelTest {

    @Mock
    private StunnerLienzoBoundsPanel panel;

    private PreviewLienzoPanel tested;

    @Before
    public void init() {
        tested = new PreviewLienzoPanel(panel);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInit() {
        tested.init();
        ArgumentCaptor<BiFunction> builderCaptor = ArgumentCaptor.forClass(BiFunction.class);
        verify(panel, times(1)).setPanelBuilder(builderCaptor.capture());
        BiFunction<OptionalInt, OptionalInt, LienzoBoundsPanel> builder = builderCaptor.getValue();
        LienzoBoundsPanel result = builder.apply(OptionalInt.of(300), OptionalInt.of(450));
        assertTrue(result instanceof PreviewPanel);
        assertEquals(300, result.getWidthPx());
        assertEquals(450, result.getHeightPx());
    }

    @Test
    public void testRefresh() {
        PreviewPanel view = mock(PreviewPanel.class);
        when(panel.getView()).thenReturn(view);
        ScrollableLienzoPanel delegate = mock(ScrollableLienzoPanel.class);
        StunnerLienzoBoundsPanel delegate2 = mock(StunnerLienzoBoundsPanel.class);
        when(delegate.getDelegate()).thenReturn(delegate2);
        ScrollablePanel previewView = mock(ScrollablePanel.class);
        when(delegate2.getView()).thenReturn(previewView);
        tested.observe(delegate);
        verify(view, times(1)).observe(eq(previewView));
    }
}
