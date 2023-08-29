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


package org.kie.workbench.common.forms.crud.client.component.formDislpay.embedded;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.crud.client.component.formDisplay.FormDisplayer;
import org.kie.workbench.common.forms.crud.client.component.formDisplay.IsFormView;
import org.kie.workbench.common.forms.crud.client.component.formDisplay.embedded.EmbeddedFormDisplayer;
import org.kie.workbench.common.forms.crud.client.component.formDisplay.embedded.EmbeddedFormDisplayerViewImpl;
import org.mockito.Mock;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class EmbeddedFormDisplayerTest extends TestCase {

    private EmbeddedFormDisplayer displayer;

    @GwtMock
    private EmbeddedFormDisplayerViewImpl displayerView;

    @GwtMock
    private IsFormView formView;

    @Mock
    private FormDisplayer.FormDisplayerCallback displayerCallback;

    @Before
    public void setup() {
        displayer = new EmbeddedFormDisplayer(displayerView);

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

        verify(displayerView).clear();
    }

    @Test
    public void testSubmitFormValidationPassed() {
        testSubmitForm(true);

        verify(displayerCallback).onAccept();

        verify(displayerView).clear();
    }

    @Test
    public void testSubmitFormValidationFailed() {
        testSubmitForm(false);

        verify(displayerCallback,
               never()).onAccept();

        verify(displayerView,
               never()).clear();
    }

    private void testSubmitForm(final boolean validate) {
        when(formView.isValid()).thenReturn(validate);

        displayer.submitForm();

        verify(formView).isValid();
    }
}
