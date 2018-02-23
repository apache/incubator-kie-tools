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

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class GridLienzoScrollUITest {

    @Mock
    private AbsolutePanel panel;

    @Mock
    private Style style;

    @Mock
    private GridLienzoScrollHandler gridLienzoScrollHandler;

    private GridLienzoScrollUI gridLienzoScrollUI;

    @Before
    public void setUp() {
        this.gridLienzoScrollUI = spy(new GridLienzoScrollUI(gridLienzoScrollHandler));

        doReturn(style).when(gridLienzoScrollUI).style(any());
    }

    @Test
    public void testSetup() {

        gridLienzoScrollUI.setup();

        verify(gridLienzoScrollUI).applyScrollPanelStyle();
        verify(gridLienzoScrollUI).applyInternalScrollPanelStyle();
        verify(gridLienzoScrollUI).applyDomElementContainerStyle();
    }

    @Test
    public void testApplyScrollPanelStyle() {

        gridLienzoScrollUI.applyScrollPanelStyle();

        verify(style).setPosition(Style.Position.RELATIVE);
        verify(style).setOverflow(Style.Overflow.SCROLL);
    }

    @Test
    public void testApplyInternalScrollPanelStyle() {

        gridLienzoScrollUI.applyInternalScrollPanelStyle();

        verify(style).setPosition(Style.Position.ABSOLUTE);
    }

    @Test
    public void testApplyDomElementContainerStyle() {

        doReturn(style).when(gridLienzoScrollUI).style(any());

        gridLienzoScrollUI.applyDomElementContainerStyle();

        verify(style).setPosition(Style.Position.ABSOLUTE);
        verify(style).setZIndex(1);
    }

    @Test
    public void testEnablePointerEvents() {

        final Widget widget = mock(Widget.class);

        gridLienzoScrollUI.enablePointerEvents(widget);

        verify(gridLienzoScrollUI).setPointerEvents(widget, "initial");
    }

    @Test
    public void testDisablePointerEvents() {

        final Widget widget = mock(Widget.class);

        gridLienzoScrollUI.disablePointerEvents(widget);

        verify(gridLienzoScrollUI).setPointerEvents(widget, "none");
    }

    @Test
    public void testSetPointerEvents() {

        final Widget widget = mock(Widget.class);

        gridLienzoScrollUI.setPointerEvents(widget, "none");

        verify(style).setProperty(eq("pointerEvents"), eq("none"));
    }

    @Test
    public void testStyle() {

        final Widget widget = mock(Widget.class);
        final Element element = mock(Element.class);
        final Style expectedStyle = mock(Style.class);

        doReturn(expectedStyle).when(element).getStyle();
        doReturn(element).when(widget).getElement();
        doCallRealMethod().when(gridLienzoScrollUI).style(any());

        final Style actualStyle = gridLienzoScrollUI.style(widget);

        assertEquals(expectedStyle,
                     actualStyle);
    }
}
