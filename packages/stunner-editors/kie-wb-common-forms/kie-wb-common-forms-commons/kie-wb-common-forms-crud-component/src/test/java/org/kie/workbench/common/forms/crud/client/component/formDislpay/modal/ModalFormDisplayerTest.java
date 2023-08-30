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


package org.kie.workbench.common.forms.crud.client.component.formDislpay.modal;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.crud.client.component.formDisplay.FormDisplayer;
import org.kie.workbench.common.forms.crud.client.component.formDisplay.IsFormView;
import org.kie.workbench.common.forms.crud.client.component.formDisplay.modal.ModalFormDisplayer;
import org.kie.workbench.common.forms.crud.client.component.formDisplay.modal.ModalFormDisplayerViewImpl;
import org.mockito.Mock;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ModalFormDisplayerTest extends TestCase {

    private ModalFormDisplayer displayer;

    @GwtMock
    private ModalFormDisplayerViewImpl displayerView;

    @GwtMock
    private IsFormView formView;

    @Mock
    private FormDisplayer.FormDisplayerCallback displayerCallback;

    @Before
    public void setup() {
        displayer = new ModalFormDisplayer(displayerView);

        verify(displayerView).setPresenter(displayer);

        displayer.asWidget();

        verify(displayerView).asWidget();

        displayer.display("Form Title",
                          formView,
                          displayerCallback);

        verify(displayerView).show("Form Title",
                                   formView);
    }

    @Test
    public void testCancelForm() {
        displayer.cancel();

        verify(displayerCallback).onCancel();

        verify(displayerView).hide();
    }

    @Test
    public void testSubmitFormValidationPassed() {
        testSubmitForm(true);

        verify(displayerCallback).onAccept();

        verify(displayerView).hide();
    }

    @Test
    public void testSubmitFormValidationFailed() {
        testSubmitForm(false);

        verify(displayerCallback,
               never()).onAccept();

        verify(displayerView,
               never()).hide();
    }

    private void testSubmitForm(final boolean validate) {
        when(formView.isValid()).thenReturn(validate);

        displayer.submitForm();

        verify(formView).isValid();
    }
}
