/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.widgets.client.handlers;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.gwtbootstrap3.client.ui.ModalHeader;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.ext.editor.commons.client.validation.ValidatorWithReasonCallback;

@WithClassesToStub(ModalHeader.class)
@RunWith(GwtMockitoTestRunner.class)
public class NewResourceViewTest {

    @Mock
    private NewResourcePresenter presenter;

    @InjectMocks
    private NewResourceView view;

    private ArgumentCaptor<ValidatorWithReasonCallback> callbackCaptor = ArgumentCaptor.forClass(ValidatorWithReasonCallback.class);

    @Before
    public void setUp() {
        view.init(presenter);
    }

    /* Test that regular input is validated, ... */

    @Test
    public void validateOnOKButtonClick_regularFileName() {
        when(view.fileNameTextBox.getText()).thenReturn("mock");
        view.onOKButtonClick();
        verify(presenter).validate(anyString(), any(ValidatorWithReasonCallback.class));
    }

    /* ... that any kind of 'empty' input gets caught even before validation and results in error state and message. */

    @Test
    public void validateOnOKButtonClick_nullFileName() {
        when(view.fileNameTextBox.getText()).thenReturn(null);
        testOnOKButtonClick_emptyFileNames();
    }

    @Test
    public void validateOnOKButtonClick_emptyFileName() {
        when(view.fileNameTextBox.getText()).thenReturn("");
        testOnOKButtonClick_emptyFileNames();
    }

    @Test
    public void validateOnOKButtonClick_whitespaceFileName() {
        when(view.fileNameTextBox.getText()).thenReturn("\t");
        testOnOKButtonClick_emptyFileNames();
    }

    private void testOnOKButtonClick_emptyFileNames() {
        view.onOKButtonClick();
        verify(view.fileNameGroup).setValidationState(ValidationState.ERROR);
        verify(view.fileNameHelpInline).setText(anyString());
        verify(presenter, never()).validate(anyString(), any(ValidatorWithReasonCallback.class));
    }

    /* If validation fails, no item is created, the callback should also set the error state ... */

    @Test
    public void callbackOnValidationFailure_noReason() {
        getCallback().onFailure();
        verify(view.fileNameGroup).setValidationState(ValidationState.ERROR);
        verify(presenter, never()).makeItem(anyString());
    }

    /* and show any reason given. */

    @Test
    public void callbackOnValidationFailure_withReason() {
        getCallback().onFailure("mock reason");
        verify(view.fileNameGroup).setValidationState(ValidationState.ERROR);
        verify(view.fileNameHelpInline).setText("mock reason");
        verify(presenter, never()).makeItem(anyString());
    }

    /* Whereas successful validation results in item being created. */

    @Test
    public void callbackOnValidationsuccess() {
        getCallback().onSuccess();
        verify(view.fileNameGroup).setValidationState(ValidationState.NONE);
        verify(presenter).makeItem(anyString());
    }

    private ValidatorWithReasonCallback getCallback() {
        when(view.fileNameTextBox.getText()).thenReturn("mock");
        view.onOKButtonClick();
        verify(presenter).validate(anyString(), callbackCaptor.capture());

        return callbackCaptor.getValue();
    }
}