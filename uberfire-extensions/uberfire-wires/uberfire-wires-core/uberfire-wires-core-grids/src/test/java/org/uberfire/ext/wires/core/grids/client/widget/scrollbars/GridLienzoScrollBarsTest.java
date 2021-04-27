/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.wires.core.grids.client.widget.scrollbars;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class GridLienzoScrollBarsTest {

    private static final Integer SCROLL_LEFT = 500;

    private static final Integer SCROLL_TOP = 1500;

    private static final Integer SCROLL_WIDTH = 4000;

    private static final Integer SCROLL_HEIGHT = 4000;

    private static final Integer CLIENT_WIDTH = 2500;

    private static final Integer CLIENT_HEIGHT = 500;

    @Mock
    private GridLienzoScrollHandler gridLienzoScrollHandler;

    private GridLienzoScrollBars gridLienzoScrollBars;

    @Before
    public void setUp() {
        gridLienzoScrollBars = spy(new GridLienzoScrollBars(gridLienzoScrollHandler));
    }

    @Test
    public void testGetHorizontalScrollPosition() {

        doReturn(scrollPanel()).when(gridLienzoScrollHandler).getScrollPanel();

        final Double position = gridLienzoScrollBars.getHorizontalScrollPosition();

        assertEquals(100d * SCROLL_LEFT / (SCROLL_WIDTH - CLIENT_WIDTH),
                     position,
                     0);
    }

    @Test
    public void testGetHorizontalScrollPositionWhenScrollbarIsDisabled() {

        doReturn(emptyPanel()).when(gridLienzoScrollHandler).getScrollPanel();

        final Double position = gridLienzoScrollBars.getHorizontalScrollPosition();

        assertEquals(0,
                     position,
                     0);
    }

    @Test
    public void testGetVerticalScrollPosition() {

        doReturn(scrollPanel()).when(gridLienzoScrollHandler).getScrollPanel();

        final Double position = gridLienzoScrollBars.getVerticalScrollPosition();

        assertEquals(100d * SCROLL_TOP / (SCROLL_HEIGHT - CLIENT_HEIGHT),
                     position,
                     0);
    }

    @Test
    public void testGetVerticalScrollPositionWhenScrollbarIsDisabled() {

        doReturn(emptyPanel()).when(gridLienzoScrollHandler).getScrollPanel();

        final Double position = gridLienzoScrollBars.getVerticalScrollPosition();

        assertEquals(0,
                     position,
                     0);
    }

    @Test
    public void testSetHorizontalScrollPosition() {

        doReturn(scrollPanel()).when(gridLienzoScrollHandler).getScrollPanel();

        final Double percentage = 100d * SCROLL_LEFT / (SCROLL_WIDTH - CLIENT_WIDTH);

        gridLienzoScrollBars.setHorizontalScrollPosition(percentage);

        verify(gridLienzoScrollBars).setScrollLeft(eq(SCROLL_LEFT));
    }

    @Test
    public void testSetVerticalScrollPosition() {

        doReturn(scrollPanel()).when(gridLienzoScrollHandler).getScrollPanel();

        final Double percentage = 100d * SCROLL_TOP / (SCROLL_HEIGHT - CLIENT_HEIGHT);

        gridLienzoScrollBars.setVerticalScrollPosition(percentage);

        verify(gridLienzoScrollBars).setScrollTop(eq(SCROLL_TOP));
    }

    @Test
    public void testPanel() {

        final Panel expectedPanel = mock(AbsolutePanel.class);

        doReturn(expectedPanel).when(gridLienzoScrollHandler).getScrollPanel();

        final Panel actualPanel = gridLienzoScrollBars.scrollPanel();

        assertEquals(expectedPanel,
                     actualPanel);
    }

    private Panel scrollPanel() {

        final Panel panel = mock(AbsolutePanel.class);
        final Element element = mock(Element.class);

        doReturn(SCROLL_LEFT).when(element).getScrollLeft();
        doReturn(SCROLL_TOP).when(element).getScrollTop();
        doReturn(SCROLL_WIDTH).when(element).getScrollWidth();
        doReturn(SCROLL_HEIGHT).when(element).getScrollHeight();
        doReturn(CLIENT_WIDTH).when(element).getClientWidth();
        doReturn(CLIENT_HEIGHT).when(element).getClientHeight();

        doReturn(element).when(panel).getElement();

        return panel;
    }

    private Panel emptyPanel() {

        final Panel panel = mock(AbsolutePanel.class);
        final Element element = mock(Element.class);

        doReturn(0).when(element).getScrollLeft();
        doReturn(0).when(element).getScrollTop();
        doReturn(0).when(element).getScrollWidth();
        doReturn(0).when(element).getScrollHeight();
        doReturn(0).when(element).getClientWidth();
        doReturn(0).when(element).getClientHeight();

        doReturn(element).when(panel).getElement();

        return panel;
    }
}
