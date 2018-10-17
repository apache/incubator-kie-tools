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

package org.kie.workbench.common.dmn.client.editors.types.messages;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.client.editors.types.messages.DataTypeFlashMessage.Type.ERROR;
import static org.kie.workbench.common.dmn.client.editors.types.messages.DataTypeFlashMessage.Type.WARNING;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeFlashMessagesTest {

    @Mock
    private DataTypeFlashMessagesView view;

    private DataTypeFlashMessages dataTypeFlashMessages;

    @Before
    public void setup() {
        dataTypeFlashMessages = spy(new DataTypeFlashMessages(view));
    }

    @Test
    public void testGetElement() {

        final HTMLElement expectedHTMLElement = mock(HTMLElement.class);

        when(view.getElement()).thenReturn(expectedHTMLElement);

        final HTMLElement actualHTMLElement = dataTypeFlashMessages.getElement();

        assertEquals(expectedHTMLElement, actualHTMLElement);
    }

    @Test
    public void testOnNameIsBlankErrorMessage() {

        final DataTypeFlashMessage flashMessage = mock(DataTypeFlashMessage.class);

        doNothing().when(dataTypeFlashMessages).registerFlashMessageCallback(flashMessage);
        doNothing().when(dataTypeFlashMessages).showFlashMessage(flashMessage);
        doNothing().when(dataTypeFlashMessages).highlightDataField(flashMessage);

        dataTypeFlashMessages.onNameIsBlankErrorMessage(flashMessage);

        verify(dataTypeFlashMessages).registerFlashMessageCallback(flashMessage);
        verify(dataTypeFlashMessages).showFlashMessage(flashMessage);
        verify(dataTypeFlashMessages).highlightDataField(flashMessage);
    }

    @Test
    public void testShowFlashMessageWhenItsAnErrorMessage() {

        final DataTypeFlashMessage flashMessage = mock(DataTypeFlashMessage.class);
        final String strongMessage = "*message*";
        final String regularMessage = "message";

        when(flashMessage.getType()).thenReturn(ERROR);
        when(flashMessage.getStrongMessage()).thenReturn(strongMessage);
        when(flashMessage.getRegularMessage()).thenReturn(regularMessage);

        dataTypeFlashMessages.showFlashMessage(flashMessage);

        verify(view).showErrorMessage(strongMessage, regularMessage);
        verify(view, never()).showWarningMessage(anyString(), anyString());
    }

    @Test
    public void testShowFlashMessageWhenItsAWarningMessage() {

        final DataTypeFlashMessage flashMessage = mock(DataTypeFlashMessage.class);
        final String strongMessage = "*message*";
        final String regularMessage = "message";

        when(flashMessage.getType()).thenReturn(WARNING);
        when(flashMessage.getStrongMessage()).thenReturn(strongMessage);
        when(flashMessage.getRegularMessage()).thenReturn(regularMessage);

        dataTypeFlashMessages.showFlashMessage(flashMessage);

        verify(view).showWarningMessage(strongMessage, regularMessage);
        verify(view, never()).showErrorMessage(anyString(), anyString());
    }

    @Test
    public void testHighlightDataFieldWhenItsAnErrorMessage() {

        final DataTypeFlashMessage flashMessage = mock(DataTypeFlashMessage.class);
        final String elementSelector = "elementSelector";

        when(flashMessage.getType()).thenReturn(ERROR);
        when(flashMessage.getErrorElementSelector()).thenReturn(elementSelector);

        dataTypeFlashMessages.highlightDataField(flashMessage);

        verify(view).showErrorHighlight(elementSelector);
        verify(view, never()).showWarningHighlight(anyString());
    }

    @Test
    public void testHighlightDataFieldWhenItsAWarningMessage() {

        final DataTypeFlashMessage flashMessage = mock(DataTypeFlashMessage.class);
        final String elementSelector = "elementSelector";

        when(flashMessage.getType()).thenReturn(WARNING);
        when(flashMessage.getErrorElementSelector()).thenReturn(elementSelector);

        dataTypeFlashMessages.highlightDataField(flashMessage);

        verify(view).showWarningHighlight(elementSelector);
        verify(view, never()).showErrorHighlight(anyString());
    }

    @Test
    public void testRegisterFlashMessageCallback() {

        final DataTypeFlashMessage flashMessage = mock(DataTypeFlashMessage.class);
        final Command onSuccess = mock(Command.class);
        final Command onError = mock(Command.class);

        when(flashMessage.getOnSuccess()).thenReturn(onSuccess);
        when(flashMessage.getOnError()).thenReturn(onError);
        when(flashMessage.getType()).thenReturn(WARNING);

        dataTypeFlashMessages.registerFlashMessageCallback(flashMessage);
        dataTypeFlashMessages.executeSuccessWarningCallback();
        dataTypeFlashMessages.executeErrorWarningCallback();

        verify(onSuccess).execute();
        verify(onError).execute();
    }

    @Test
    public void testHideMessages() {

        dataTypeFlashMessages.hideMessages();

        verify(view).hideErrorContainer();
        verify(view).hideWarningContainer();
    }
}
