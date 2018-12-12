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

package com.ait.lienzo.client.widget.panel.scrollbars;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class ScrollUITest {

    @Mock
    private Style style;

    @Mock
    private ScrollablePanelHandler scrollHandler;

    private ScrollUI scrollUI;

    @Before
    public void setUp() {
        this.scrollUI = spy(new ScrollUI(scrollHandler));

        doReturn(style).when(scrollUI).style(any(Widget.class));
    }

    @Test
    public void testSetup() {

        scrollUI.setup();

        verify(scrollUI).applyScrollPanelStyle();
        verify(scrollUI).applyInternalScrollPanelStyle();
        verify(scrollUI).applyDomElementContainerStyle();
    }

    @Test
    public void testApplyScrollPanelStyle() {

        scrollUI.applyScrollPanelStyle();

        verify(style).setPosition(Style.Position.RELATIVE);
        verify(style).setOverflow(Style.Overflow.SCROLL);
    }

    @Test
    public void testApplyInternalScrollPanelStyle() {

        scrollUI.applyInternalScrollPanelStyle();

        verify(style).setPosition(Style.Position.ABSOLUTE);
    }

    @Test
    public void testApplyDomElementContainerStyle() {

        doReturn(style).when(scrollUI).style(any(Widget.class));

        scrollUI.applyDomElementContainerStyle();

        verify(style).setPosition(Style.Position.ABSOLUTE);
        verify(style).setZIndex(1);
    }

    @Test
    public void testEnablePointerEvents() {

        final Widget widget = mock(Widget.class);

        scrollUI.enablePointerEvents(widget);

        verify(scrollUI).setPointerEvents(widget, "initial");
    }

    @Test
    public void testDisablePointerEvents() {

        final Widget widget = mock(Widget.class);

        scrollUI.disablePointerEvents(widget);

        verify(scrollUI).setPointerEvents(widget, "none");
    }

    @Test
    public void testSetPointerEvents() {

        final Widget widget = mock(Widget.class);

        scrollUI.setPointerEvents(widget, "none");

        verify(style).setProperty(Matchers.eq("pointerEvents"), Matchers.eq("none"));
    }

    @Test
    public void testStyle() {

        final Widget widget = mock(Widget.class);
        final Element element = mock(Element.class);
        final Style expectedStyle = mock(Style.class);

        doReturn(expectedStyle).when(element).getStyle();
        doReturn(element).when(widget).getElement();
        doCallRealMethod().when(scrollUI).style(any(Widget.class));

        final Style actualStyle = scrollUI.style(widget);

        assertEquals(expectedStyle,
                     actualStyle);
    }
}
