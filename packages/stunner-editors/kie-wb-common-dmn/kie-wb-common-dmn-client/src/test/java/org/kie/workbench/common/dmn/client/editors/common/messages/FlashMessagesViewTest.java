/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.editors.common.messages;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.DOMTokenList;
import elemental2.dom.DomGlobal.SetTimeoutCallbackFn;
import elemental2.dom.Element;
import elemental2.dom.Event;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.NodeList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessagesView.ERROR_CSS_CLASS;
import static org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessagesView.OPENED_CONTAINER_CSS_CLASS;
import static org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessagesView.WARNING_CSS_CLASS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class FlashMessagesViewTest {

    @Mock
    private HTMLDivElement errorContainer;

    @Mock
    private HTMLDivElement warningContainer;

    @Mock
    private HTMLElement strongErrorMessage;

    @Mock
    private HTMLElement regularErrorMessage;

    @Mock
    private HTMLElement strongWarningMessage;

    @Mock
    private HTMLElement regularWarningMessage;

    @Mock
    private HTMLButtonElement okButton;

    @Mock
    private HTMLButtonElement cancelButton;

    @Mock
    private FlashMessages presenter;

    @Mock
    private HTMLButtonElement closeSuccessButton;

    @Mock
    private HTMLDivElement successContainer;

    @Mock
    private HTMLElement strongSuccessMessage;

    @Mock
    private HTMLElement regularSuccessMessage;

    private FlashMessagesView view;

    @Before
    public void setup() {
        view = Mockito.spy(new FlashMessagesView(errorContainer, warningContainer, strongErrorMessage, regularErrorMessage, strongWarningMessage, regularWarningMessage, okButton, cancelButton, closeSuccessButton, successContainer, strongSuccessMessage, regularSuccessMessage));
        view.init(presenter);
    }

    @Test
    public void testOnOkWarningButtonClick() {

        final ClickEvent event = mock(ClickEvent.class);

        doNothing().when(view).disableWarningHighlight();
        warningContainer.classList = mock(DOMTokenList.class);

        view.onOkWarningButtonClick(event);

        verify(presenter).executeSuccessWarningCallback();
        verify(warningContainer.classList).remove(OPENED_CONTAINER_CSS_CLASS);
        verify(view).disableWarningHighlight();
    }

    @Test
    public void testOnCancelWarningButtonClick() {

        final ClickEvent event = mock(ClickEvent.class);

        doNothing().when(view).disableWarningHighlight();
        warningContainer.classList = mock(DOMTokenList.class);

        view.onCancelWarningButtonClick(event);

        verify(presenter).executeErrorWarningCallback();
        verify(warningContainer.classList).remove(OPENED_CONTAINER_CSS_CLASS);
        verify(view).disableWarningHighlight();
    }

    @Test
    public void testOnCloseSuccessButtonClick() {

        final ClickEvent event = mock(ClickEvent.class);
        doNothing().when(view).hideSuccessContainer();

        view.onCloseSuccessButtonClick(event);

        verify(view).hideSuccessContainer();
    }

    @Test
    public void testShowErrorMessage() {

        final String expectedStrongMessage = "*message*";
        final String expectedRegularMessage = "message";
        errorContainer.classList = mock(DOMTokenList.class);

        view.showErrorMessage(expectedStrongMessage, expectedRegularMessage);

        final String actualStrongMessage = strongErrorMessage.textContent;
        final String actualRegularMessage = regularErrorMessage.textContent;

        verify(errorContainer.classList).add(OPENED_CONTAINER_CSS_CLASS);
        assertEquals(expectedStrongMessage, actualStrongMessage);
        assertEquals(expectedRegularMessage, actualRegularMessage);
    }

    @Test
    public void testShowWarningMessage() {

        final String expectedStrongMessage = "*message*";
        final String expectedRegularMessage = "message";
        warningContainer.classList = mock(DOMTokenList.class);

        view.showWarningMessage(expectedStrongMessage, expectedRegularMessage);

        final String actualStrongMessage = strongWarningMessage.textContent;
        final String actualRegularMessage = regularWarningMessage.textContent;

        verify(warningContainer.classList).add(OPENED_CONTAINER_CSS_CLASS);
        assertEquals(expectedStrongMessage, actualStrongMessage);
        assertEquals(expectedRegularMessage, actualRegularMessage);
    }

    @Test
    public void testShowSuccessMessage() {

        final String expectedStrongMessage = "*message*";
        final String expectedRegularMessage = "message";
        successContainer.classList = mock(DOMTokenList.class);

        view.showSuccessMessage(expectedStrongMessage, expectedRegularMessage);

        final String actualStrongMessage = strongSuccessMessage.textContent;
        final String actualRegularMessage = regularSuccessMessage.textContent;

        verify(successContainer.classList).add(OPENED_CONTAINER_CSS_CLASS);
        assertEquals(expectedStrongMessage, actualStrongMessage);
        assertEquals(expectedRegularMessage, actualRegularMessage);
    }

    @Test
    public void testShowErrorHighlight() {

        final String errorElementSelector = "#error-element-selector";
        final HTMLElement element = mock(HTMLElement.class);
        final HTMLElement parentElement = mock(HTMLElement.class);
        final Element errorElement = mock(Element.class);

        doNothing().when(view).enableErrorHighlight(any());
        doNothing().when(view).setupDisableErrorHighlightCallbacks(any());
        doReturn(element).when(view).getElement();
        element.parentNode = parentElement;
        when(parentElement.querySelector(errorElementSelector)).thenReturn(errorElement);

        view.showErrorHighlight(errorElementSelector);

        verify(view).enableErrorHighlight(errorElement);
        verify(view).setupDisableErrorHighlightCallbacks(errorElement);
        verify(errorElement).focus();
    }

    @Test
    public void testShowErrorHighlightWhenElementDoesNotExist() {

        final String errorElementSelector = "#error-element-selector";
        final HTMLElement element = mock(HTMLElement.class);
        final HTMLElement parentElement = mock(HTMLElement.class);

        doNothing().when(view).enableErrorHighlight(any());
        doNothing().when(view).setupDisableErrorHighlightCallbacks(any());
        doReturn(element).when(view).getElement();
        element.parentNode = parentElement;
        when(parentElement.querySelector(errorElementSelector)).thenReturn(null);

        view.showErrorHighlight(errorElementSelector);

        verify(view, never()).enableErrorHighlight(any());
        verify(view, never()).setupDisableErrorHighlightCallbacks(any());
    }

    @Test
    public void testShowWarningHighlight() {

        final String warningElementSelector = "#warning-element-selector";
        final HTMLElement element = mock(HTMLElement.class);
        final HTMLElement parentElement = mock(HTMLElement.class);
        final Element warningElement = mock(Element.class);

        doNothing().when(view).disableWarningHighlight();
        doNothing().when(view).enableWarningHighlight(any());
        doReturn(element).when(view).getElement();
        element.parentNode = parentElement;
        when(parentElement.querySelector(warningElementSelector)).thenReturn(warningElement);

        view.showWarningHighlight(warningElementSelector);

        verify(view).enableWarningHighlight(warningElement);
    }

    @Test
    public void testShowWarningHighlightWhenElementDoesNotExist() {

        final String warningElementSelector = "#warning-element-selector";
        final HTMLElement element = mock(HTMLElement.class);
        final HTMLElement parentElement = mock(HTMLElement.class);

        doNothing().when(view).disableWarningHighlight();
        doNothing().when(view).enableWarningHighlight(any());
        doReturn(element).when(view).getElement();
        element.parentNode = parentElement;
        when(parentElement.querySelector(warningElementSelector)).thenReturn(null);

        view.showWarningHighlight(warningElementSelector);

        verify(view, never()).enableWarningHighlight(any());
    }

    @Test
    public void testEnableErrorHighlight() {

        final Element errorElement = mock(Element.class);
        errorElement.classList = mock(DOMTokenList.class);

        view.enableErrorHighlight(errorElement);

        verify(errorElement.classList).add(ERROR_CSS_CLASS);
    }

    @Test
    public void testEnableWarningHighlight() {

        final Element warningElement = mock(Element.class);
        warningElement.classList = mock(DOMTokenList.class);

        view.enableWarningHighlight(warningElement);

        verify(warningElement.classList).add(WARNING_CSS_CLASS);
    }

    @Test
    public void testDisableErrorHighlightWhenErrorIsNotEnabled() {

        final Element errorElement = mock(Element.class);
        final ArgumentCaptor<SetTimeoutCallbackFn> timeoutLambdaCaptor = ArgumentCaptor.forClass(SetTimeoutCallbackFn.class);

        doNothing().when(view).setTimeout(any(), anyDouble());

        errorElement.classList = mock(DOMTokenList.class);
        errorContainer.classList = mock(DOMTokenList.class);

        view.disableErrorHighlight(errorElement);

        verify(view).setTimeout(timeoutLambdaCaptor.capture(), eq(500d));
        verify(errorElement.classList).remove(ERROR_CSS_CLASS);

        timeoutLambdaCaptor.getValue().onInvoke();

        verify(errorContainer.classList).remove(OPENED_CONTAINER_CSS_CLASS);
        verify(view).teardownDisableErrorHighlightCallbacks(errorElement);
    }

    @Test
    public void testDisableWarningHighlight() {

        final String warningElementSelector = "." + WARNING_CSS_CLASS;
        final HTMLElement element = mock(HTMLElement.class);
        final HTMLElement parentElement = mock(HTMLElement.class);
        final NodeList<Element> warningElements = spy(new NodeList<>());
        final Element warningElement1 = mock(Element.class);
        final Element warningElement2 = mock(Element.class);

        doReturn(warningElement1).when(warningElements).getAt(0);
        doReturn(warningElement2).when(warningElements).getAt(1);
        doReturn(element).when(view).getElement();
        when(parentElement.querySelectorAll(warningElementSelector)).thenReturn(warningElements);
        element.parentNode = parentElement;
        warningElements.length = 2;
        warningElement1.classList = mock(DOMTokenList.class);
        warningElement2.classList = mock(DOMTokenList.class);

        view.disableWarningHighlight();

        verify(warningElement1.classList).remove(WARNING_CSS_CLASS);
        verify(warningElement2.classList).remove(WARNING_CSS_CLASS);
    }

    @Test
    public void testTeardownDisableErrorHighlightCallbacks() {

        final Element element = mock(Element.class);
        final Event event = mock(Event.class);

        element.onkeypress = (e) -> false;
        element.onblur = (e) -> false;

        view.teardownDisableErrorHighlightCallbacks(element);

        assertTrue(Boolean.valueOf(element.onkeypress.onInvoke(event).toString()));
        assertTrue(Boolean.valueOf(element.onblur.onInvoke(event).toString()));
    }

    @Test
    public void testSetupDisableErrorHighlightCallbacks() {

        final Event event = mock(Event.class);
        final Element errorElement = mock(Element.class);

        doNothing().when(view).disableErrorHighlight(any());

        view.setupDisableErrorHighlightCallbacks(errorElement);

        errorElement.onkeypress.onInvoke(event);
        errorElement.onblur.onInvoke(event);

        verify(view, times(2)).disableErrorHighlight(errorElement);
    }

    @Test
    public void testHideWarningContainer() {
        warningContainer.classList = mock(DOMTokenList.class);

        view.hideWarningContainer();

        verify(warningContainer.classList).remove(OPENED_CONTAINER_CSS_CLASS);
    }

    @Test
    public void testHideErrorContainer() {
        errorContainer.classList = mock(DOMTokenList.class);

        view.hideErrorContainer();

        verify(errorContainer.classList).remove(OPENED_CONTAINER_CSS_CLASS);
    }

    @Test
    public void testHideSuccessContainer() {
        successContainer.classList = mock(DOMTokenList.class);

        view.hideSuccessContainer();

        verify(successContainer.classList).remove(OPENED_CONTAINER_CSS_CLASS);
    }
}
