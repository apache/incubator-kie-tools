/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.scenariosimulation.client.popover;

import java.util.Optional;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.jboss.errai.common.client.dom.CSSStyleDeclaration;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.views.pfly.widgets.JQueryProducer;
import org.uberfire.client.views.pfly.widgets.Popover;
import org.uberfire.client.views.pfly.widgets.PopoverOptions;

import static org.drools.workbench.screens.scenariosimulation.client.popover.AbstractPopoverView.ABSOLUTE;
import static org.drools.workbench.screens.scenariosimulation.client.popover.AbstractPopoverView.LEFT;
import static org.drools.workbench.screens.scenariosimulation.client.popover.AbstractPopoverView.POSITION;
import static org.drools.workbench.screens.scenariosimulation.client.popover.AbstractPopoverView.TITLE;
import static org.drools.workbench.screens.scenariosimulation.client.popover.AbstractPopoverView.TOP;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class AbstractPopoverViewTest {

    public final static String EDITOR_TITLE_TEXT = "EDITOR_TITLE_TEXT";
    public final static int MX = 36;
    public final static int MY = 52;
    public final static String TOP_PX = MY + "px";
    public final static String LEFT_PX = MX + "px";

    private AbstractPopoverView abstractPopoverView;

    @Mock
    private HTMLElement elementMock;

    @Mock
    private CSSStyleDeclaration styleMock;

    @Mock
    private Div popoverElementMock;

    @Mock
    private Div popoverContainerMock;

    @Mock
    private Div popoverContentMock;

    @Mock
    private Popover popoverMock;

    @Mock
    protected JQueryProducer.JQuery<Popover> jQueryPopoverMock;

    @Mock private ElementWrapperWidget<Object> wrappedWidgetMock;

    @Before
    public void setup() {
        abstractPopoverView = spy(new AbstractPopoverView() {
            {
                this.wrappedWidget = wrappedWidgetMock;
                this.popover = popoverMock;
                this.popoverElement = popoverElementMock;
                this.popoverContentElement = popoverContentMock;
                this.popoverContainerElement = popoverContainerMock;
                this.jQueryPopover = jQueryPopoverMock;
            }

            @Override
            public HTMLElement getElement() {
                return elementMock;
            }
        });
        when(jQueryPopoverMock.wrap(any())).thenReturn(popoverMock);
        when(popoverElementMock.getStyle()).thenReturn(styleMock);
    }

    @Test
    public void setupIsShown() {
        doReturn(Boolean.TRUE).when(abstractPopoverView).isShown();
        abstractPopoverView.setup(Optional.of(EDITOR_TITLE_TEXT), MX, MY, PopoverView.Position.RIGHT);
        verify(abstractPopoverView, times(1)).hide();
        verify(abstractPopoverView, times(1)).addWidgetToRootPanel();
        verify(popoverElementMock, times(1)).setAttribute(TITLE, EDITOR_TITLE_TEXT);
        verify(jQueryPopoverMock, times(1)).wrap(elementMock);
        verify(popoverMock, times(1)).popover(isA(PopoverOptions.class));
        verify(styleMock, times(1)).setProperty(eq(TOP),eq(TOP_PX));
        verify(styleMock, times(1)).setProperty(eq(LEFT), eq(LEFT_PX));
        verify(styleMock, times(1)).setProperty(eq(POSITION),eq(ABSOLUTE));
    }

    @Test
    public void setupIsNotShown() {
        doReturn(Boolean.FALSE).when(abstractPopoverView).isShown();
        abstractPopoverView.setup(Optional.of(EDITOR_TITLE_TEXT), MX, MY, PopoverView.Position.RIGHT);
        verify(abstractPopoverView, never()).hide();
        verify(abstractPopoverView, times(1)).addWidgetToRootPanel();
        verify(popoverElementMock, times(1)).setAttribute(TITLE, EDITOR_TITLE_TEXT);
        verify(jQueryPopoverMock, times(1)).wrap(elementMock);
        verify(popoverMock, times(1)).popover(isA(PopoverOptions.class));
        verify(styleMock, times(1)).setProperty(eq(TOP),eq(TOP_PX));
        verify(styleMock, times(1)).setProperty(eq(LEFT), eq(LEFT_PX));
        verify(styleMock, times(1)).setProperty(eq(POSITION),eq(ABSOLUTE));
    }

    @Test
    public void show() {
        abstractPopoverView.show();
        verify(abstractPopoverView, times(1)).scheduleTask();
    }

    @Test
    public void hideIsShown() {
        doReturn(Boolean.TRUE).when(abstractPopoverView).isShown();
        assertNotNull(abstractPopoverView.wrappedWidget);
        abstractPopoverView.hide();
        verify(popoverMock, times(1)).destroy();
        verify(abstractPopoverView, times(1)).removeWidgetFromRootPanel();
        assertNull(abstractPopoverView.wrappedWidget);
    }

    @Test
    public void hideNotIsShown() {
        doReturn(Boolean.FALSE).when(abstractPopoverView).isShown();
        abstractPopoverView.hide();
        verify(popoverMock, never()).hide();
        verify(popoverMock, never()).destroy();
        verify(abstractPopoverView, never()).removeWidgetFromRootPanel();
    }
}