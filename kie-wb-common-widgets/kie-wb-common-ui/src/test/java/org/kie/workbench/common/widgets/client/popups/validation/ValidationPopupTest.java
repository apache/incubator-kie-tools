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

package org.kie.workbench.common.widgets.client.popups.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.callbacks.AssetValidatedCallback;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ValidationPopupTest {

    @Mock
    private ValidationPopupView view;

    @Mock
    private ValidationMessageTranslatorUtils validationMessageTranslatorUtils;

    @Mock
    private TranslationService translationService;

    @Mock
    private EventSourceMock notificationEvent;

    private ValidationPopup validationPopup;

    @Before
    public void setUp() {
        validationPopup = new ValidationPopup(view,
                                              validationMessageTranslatorUtils,
                                              translationService,
                                              notificationEvent);

        view.init(validationPopup);
    }

    @Test
    public void showMessages() {
        ValidationMessage validationMessage = new ValidationMessage();

        validationPopup.showMessages(Arrays.asList(validationMessage));

        verify(view).showYesButton(false);
        verify(view).showCancelButton(true);

        verify(view).setValidationMessages(anyListOf(ValidationMessage.class));
        verify(view).show();
    }

    @Test
    public void showCopyValidationMessages() {
        List<ValidationMessage> validationMessages = Arrays.asList(new ValidationMessage());

        validationPopup.showCopyValidationMessages(() -> {
                                                   },
                                                   () -> {
                                                   },
                                                   validationMessages);

        verify(view).showYesButton(true);
        verify(view).showCancelButton(true);

        List<ValidationMessage> translatedMessages = Collections.emptyList();
        when(validationMessageTranslatorUtils.translate(validationMessages)).thenReturn(translatedMessages);

        verify(view).setValidationMessages(translatedMessages);
        verify(view).show();
    }

    @Test
    public void showSaveValidationMessages() {
        List<ValidationMessage> validationMessages = Arrays.asList(new ValidationMessage());

        validationPopup.showSaveValidationMessages(() -> {
                                                   },
                                                   () -> {
                                                   },
                                                   validationMessages);

        verify(view).showYesButton(true);
        verify(view).showCancelButton(true);

        List<ValidationMessage> translatedMessages = Collections.emptyList();
        when(validationMessageTranslatorUtils.translate(validationMessages)).thenReturn(translatedMessages);

        verify(view).setValidationMessages(translatedMessages);
        verify(view).show();
    }

    @Test
    public void showTranslatedMessages() {
        List<ValidationMessage> validationMessages = Arrays.asList(new ValidationMessage());

        validationPopup.showTranslatedMessages(validationMessages);

        List<ValidationMessage> translatedMessages = Collections.emptyList();
        when(validationMessageTranslatorUtils.translate(validationMessages)).thenReturn(translatedMessages);

        verify(view).setValidationMessages(translatedMessages);
        verify(view).show();
    }

    @Test
    public void getCallback() throws Exception {

        final AssetValidatedCallback callback = validationPopup.getValidationCallback(mock(Command.class));

        callback.callback(new ArrayList<>());

        verify(notificationEvent).fire(any());
    }
}