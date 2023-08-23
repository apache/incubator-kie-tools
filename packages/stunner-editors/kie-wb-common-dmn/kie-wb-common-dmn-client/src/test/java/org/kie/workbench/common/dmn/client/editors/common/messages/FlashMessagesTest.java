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

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessage.Type.ERROR;
import static org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessage.Type.SUCCESS;
import static org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessage.Type.WARNING;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class FlashMessagesTest {

    @Mock
    private FlashMessagesView view;

    private FlashMessages flashMessages;

    @Before
    public void setup() {
        flashMessages = Mockito.spy(new FlashMessages(view));
    }

    @Test
    public void testInit() {
        flashMessages.init();
        verify(view).init(flashMessages);
    }

    @Test
    public void testGetElement() {

        final HTMLElement expectedHTMLElement = mock(HTMLElement.class);

        when(view.getElement()).thenReturn(expectedHTMLElement);

        final HTMLElement actualHTMLElement = flashMessages.getElement();

        assertEquals(expectedHTMLElement, actualHTMLElement);
    }

    @Test
    public void testOnFlashMessageEventWhenElementIsPresent() {

        final FlashMessage flashMessage = mock(FlashMessage.class);
        final String selector = "selector";

        doNothing().when(flashMessages).registerFlashMessageCallback(flashMessage);
        doNothing().when(flashMessages).showFlashMessage(flashMessage);
        doNothing().when(flashMessages).highlighElement(flashMessage);
        when(flashMessage.getElementSelector()).thenReturn(selector);
        when(view.isElementPresent(selector)).thenReturn(true);

        flashMessages.onFlashMessageEvent(flashMessage);

        verify(flashMessages).registerFlashMessageCallback(flashMessage);
        verify(flashMessages).showFlashMessage(flashMessage);
        verify(flashMessages).highlighElement(flashMessage);
    }

    @Test
    public void testOnFlashMessageEventWhenElementIsNotPresent() {

        final FlashMessage flashMessage = mock(FlashMessage.class);
        final String selector = "selector";

        doNothing().when(flashMessages).registerFlashMessageCallback(flashMessage);
        doNothing().when(flashMessages).showFlashMessage(flashMessage);
        doNothing().when(flashMessages).highlighElement(flashMessage);
        when(flashMessage.getElementSelector()).thenReturn(selector);
        when(view.isElementPresent(selector)).thenReturn(false);

        flashMessages.onFlashMessageEvent(flashMessage);

        verify(flashMessages).registerFlashMessageCallback(flashMessage);
        verify(flashMessages).showFlashMessage(flashMessage);
        verify(flashMessages, never()).highlighElement(flashMessage);
    }

    @Test
    public void testOnFlashMessageEventWhenSelectorIsEmpty() {

        final FlashMessage flashMessage = mock(FlashMessage.class);
        final String selector = "";

        doNothing().when(flashMessages).registerFlashMessageCallback(flashMessage);
        doNothing().when(flashMessages).showFlashMessage(flashMessage);
        doNothing().when(flashMessages).highlighElement(flashMessage);
        when(flashMessage.getElementSelector()).thenReturn(selector);

        flashMessages.onFlashMessageEvent(flashMessage);

        verify(flashMessages).registerFlashMessageCallback(flashMessage);
        verify(flashMessages).showFlashMessage(flashMessage);
        verify(flashMessages, never()).highlighElement(flashMessage);
    }

    @Test
    public void testShowFlashMessageWhenItsAnErrorMessage() {

        final FlashMessage flashMessage = mock(FlashMessage.class);
        final String strongMessage = "*message*";
        final String regularMessage = "message";

        when(flashMessage.getType()).thenReturn(ERROR);
        when(flashMessage.getStrongMessage()).thenReturn(strongMessage);
        when(flashMessage.getRegularMessage()).thenReturn(regularMessage);

        flashMessages.showFlashMessage(flashMessage);

        verify(view).showErrorMessage(strongMessage, regularMessage);
        verify(view, never()).showWarningMessage(Mockito.<String>any(), Mockito.<String>any());
        verify(view, never()).showSuccessMessage(Mockito.<String>any(), Mockito.<String>any());
    }

    @Test
    public void testShowFlashMessageWhenItsAWarningMessage() {

        final FlashMessage flashMessage = mock(FlashMessage.class);
        final String strongMessage = "*message*";
        final String regularMessage = "message";

        when(flashMessage.getType()).thenReturn(WARNING);
        when(flashMessage.getStrongMessage()).thenReturn(strongMessage);
        when(flashMessage.getRegularMessage()).thenReturn(regularMessage);

        flashMessages.showFlashMessage(flashMessage);

        verify(view).showWarningMessage(strongMessage, regularMessage);
        verify(view, never()).showErrorMessage(Mockito.<String>any(), Mockito.<String>any());
        verify(view, never()).showSuccessMessage(Mockito.<String>any(), Mockito.<String>any());
    }

    @Test
    public void testShowFlashMessageWhenItsASuccessMessage() {

        final FlashMessage flashMessage = mock(FlashMessage.class);
        final String strongMessage = "*message*";
        final String regularMessage = "message";

        when(flashMessage.getType()).thenReturn(SUCCESS);
        when(flashMessage.getStrongMessage()).thenReturn(strongMessage);
        when(flashMessage.getRegularMessage()).thenReturn(regularMessage);

        flashMessages.showFlashMessage(flashMessage);

        verify(view).showSuccessMessage(strongMessage, regularMessage);
        verify(view, never()).showWarningMessage(Mockito.<String>any(), Mockito.<String>any());
        verify(view, never()).showErrorMessage(Mockito.<String>any(), Mockito.<String>any());
    }

    @Test
    public void testHighlightElementWhenItsAnErrorMessage() {

        final FlashMessage flashMessage = mock(FlashMessage.class);
        final String elementSelector = "elementSelector";

        when(flashMessage.getType()).thenReturn(ERROR);
        when(flashMessage.getElementSelector()).thenReturn(elementSelector);

        flashMessages.highlighElement(flashMessage);

        verify(view).showErrorHighlight(elementSelector);
        verify(view, never()).showWarningHighlight(Mockito.<String>any());
    }

    @Test
    public void testHighlightElementWhenItsAWarningMessage() {

        final FlashMessage flashMessage = mock(FlashMessage.class);
        final String elementSelector = "elementSelector";

        when(flashMessage.getType()).thenReturn(WARNING);
        when(flashMessage.getElementSelector()).thenReturn(elementSelector);

        flashMessages.highlighElement(flashMessage);

        verify(view).showWarningHighlight(elementSelector);
        verify(view, never()).showErrorHighlight(Mockito.<String>any());
    }

    @Test
    public void testRegisterFlashMessageCallback() {

        final FlashMessage flashMessage = mock(FlashMessage.class);
        final Command onSuccess = mock(Command.class);
        final Command onError = mock(Command.class);

        when(flashMessage.getOnSuccess()).thenReturn(onSuccess);
        when(flashMessage.getOnError()).thenReturn(onError);
        when(flashMessage.getType()).thenReturn(WARNING);

        flashMessages.registerFlashMessageCallback(flashMessage);
        flashMessages.executeSuccessWarningCallback();
        flashMessages.executeErrorWarningCallback();

        verify(onSuccess).execute();
        verify(onError).execute();
    }

    @Test
    public void testHideMessages() {

        flashMessages.hideMessages();

        verify(view).hideErrorContainer();
        verify(view).hideWarningContainer();
    }
}
