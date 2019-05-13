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

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.dom.client.ButtonElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
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
import org.uberfire.mvp.Command;

import static org.drools.workbench.screens.scenariosimulation.client.popover.AbstractPopoverView.ABSOLUTE;
import static org.drools.workbench.screens.scenariosimulation.client.popover.AbstractPopoverView.LEFT;
import static org.drools.workbench.screens.scenariosimulation.client.popover.AbstractPopoverView.POSITION;
import static org.drools.workbench.screens.scenariosimulation.client.popover.AbstractPopoverView.TITLE;
import static org.drools.workbench.screens.scenariosimulation.client.popover.AbstractPopoverView.TOP;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ErrorReportPopoverViewTest {

    public final static String ERROR_TITLE_TEXT = "ERROR_TITLE_TEXT";
    public final static String ERROR_CONTENT_TEXT = "ERROR_CONTENT_TEXT";
    public final static String KEEP_TEXT = "KEEP_TEXT";
    public final static String APPLY_TEXT = "APPLY_TEXT";
    public final static int MX = 36;
    public final static int MY = 52;
    public final static String TOP_PX = MY + "px";
    public final static String LEFT_PX = MX + "px";

    private ErrorReportPopoverView errorReportPopupView;

    @Mock
    private HTMLElement elementMock;

    @Mock
    private CSSStyleDeclaration styleMock;

    @Mock
    private Div errorContentMock;

    @Mock
    private Div popoverElementMock;

    @Mock
    private Div popoverContainerMock;

    @Mock
    private Div popoverContentMock;

    @Mock
    private Popover popoverMock;

    @Mock
    private ButtonElement keepButtonMock;

    @Mock
    private ButtonElement applyButtonMock;

    @Mock
    private Style applyButtonStyleMock;

    @Mock
    private Command applyCommandMock;

    @Mock
    protected JQueryProducer.JQuery<Popover> jQueryPopoverMock;

    @Mock private ElementWrapperWidget<Object> wrappedWidgetMock;

    @Before
    public void setup() {
        errorReportPopupView = spy(new ErrorReportPopoverView() {
            {
                this.keepButton = keepButtonMock;
                this.applyButton = applyButtonMock;
                this.applyCommand = applyCommandMock;
                this.errorContent = errorContentMock;
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
        when(applyButtonMock.getStyle()).thenReturn(applyButtonStyleMock);
    }

    @Test
    public void show() {
        errorReportPopupView.show(ERROR_TITLE_TEXT, ERROR_CONTENT_TEXT, KEEP_TEXT, APPLY_TEXT, applyCommandMock, MX, MY, PopoverView.Position.RIGHT);
        verify(errorReportPopupView, times(1)).addWidgetToRootPanel();
        verify(popoverElementMock, times(1)).setAttribute(TITLE, ERROR_TITLE_TEXT);
        verify(jQueryPopoverMock, times(1)).wrap(elementMock);
        verify(popoverMock, times(1)).popover(isA(PopoverOptions.class));
        verify(styleMock, times(1)).setProperty(eq(TOP),eq(TOP_PX));
        verify(styleMock, times(1)).setProperty(eq(LEFT), eq(LEFT_PX));
        verify(styleMock, times(1)).setProperty(eq(POSITION),eq(ABSOLUTE));
        verify(errorReportPopupView, times(1)).scheduleTask();
        verify(errorContentMock, times(1)).setTextContent(eq(ERROR_CONTENT_TEXT));
        verify(keepButtonMock, times(1)).setInnerText(eq(KEEP_TEXT));
        verify(applyButtonMock, times(1)).setInnerText(eq(APPLY_TEXT));
        verify(applyButtonMock.getStyle(), times(1)).setDisplay(eq(Style.Display.INLINE));
    }

    @Test
    public void show_withoutApplyButton() {
        errorReportPopupView.show(ERROR_TITLE_TEXT, ERROR_CONTENT_TEXT, KEEP_TEXT, MX, MY, PopoverView.Position.RIGHT);
        verify(errorReportPopupView, times(1)).addWidgetToRootPanel();
        verify(popoverElementMock, times(1)).setAttribute(TITLE, ERROR_TITLE_TEXT);
        verify(jQueryPopoverMock, times(1)).wrap(elementMock);
        verify(popoverMock, times(1)).popover(isA(PopoverOptions.class));
        verify(styleMock, times(1)).setProperty(eq(TOP),eq(TOP_PX));
        verify(styleMock, times(1)).setProperty(eq(LEFT), eq(LEFT_PX));
        verify(styleMock, times(1)).setProperty(eq(POSITION),eq(ABSOLUTE));
        verify(errorReportPopupView, times(1)).scheduleTask();
        verify(errorContentMock, times(1)).setTextContent(eq(ERROR_CONTENT_TEXT));
        verify(keepButtonMock, times(1)).setInnerText(eq(KEEP_TEXT));
        verify(applyButtonMock.getStyle(), times(1)).setDisplay(eq(Style.Display.NONE));
    }

    @Test
    public void show_alreadyVisible() {
        doReturn(Boolean.TRUE).when(errorReportPopupView).isShown();
        errorReportPopupView.show(ERROR_TITLE_TEXT, ERROR_CONTENT_TEXT, KEEP_TEXT, APPLY_TEXT, applyCommandMock, MX, MY, PopoverView.Position.RIGHT);
        verify(errorReportPopupView, times(1)).hide();
        verify(errorReportPopupView, times(1)).addWidgetToRootPanel();
        verify(popoverElementMock, times(1)).setAttribute(TITLE, ERROR_TITLE_TEXT);
        verify(jQueryPopoverMock, times(1)).wrap(elementMock);
        verify(popoverMock, times(1)).popover(isA(PopoverOptions.class));
        verify(styleMock, times(1)).setProperty(eq(TOP),eq(TOP_PX));
        verify(styleMock, times(1)).setProperty(eq(LEFT), eq(LEFT_PX));
        verify(styleMock, times(1)).setProperty(eq(POSITION),eq(ABSOLUTE));
        verify(errorReportPopupView, times(1)).scheduleTask();
        verify(errorContentMock, times(1)).setTextContent(eq(ERROR_CONTENT_TEXT));
        verify(keepButtonMock, times(1)).setInnerText(eq(KEEP_TEXT));
        verify(applyButtonMock, times(1)).setInnerText(eq(APPLY_TEXT));
    }

    @Test
    public void hide_visible() {
        doReturn(Boolean.TRUE).when(errorReportPopupView).isShown();
        errorReportPopupView.hide();
        verify(popoverMock, times(1)).hide();
        verify(popoverMock, times(1)).destroy();
        verify(errorReportPopupView, times(1)).removeWidgetFromRootPanel();
    }

    @Test
    public void hide_notVisible() {
        doReturn(Boolean.FALSE).when(errorReportPopupView).isShown();
        errorReportPopupView.hide();
        verify(popoverMock, never()).hide();
        verify(popoverMock, never()).destroy();
        verify(errorReportPopupView, never()).removeWidgetFromRootPanel();
    }

    @Test
    public void onKeepButtonClicked() {
        errorReportPopupView.onKeepButtonClicked(mock(ClickEvent.class));
        verify(errorReportPopupView, times(1)).hide();
    }

    @Test
    public void onApplyButtonClicked() {
        errorReportPopupView.onApplyButtonClicked(mock(ClickEvent.class));
        verify(applyCommandMock, times(1)).execute();
        verify(errorReportPopupView, times(1)).hide();
    }
}