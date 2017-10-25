/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.ala.ui.client.wizard.provider;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.ala.ui.client.handler.ProviderConfigurationForm;
import org.guvnor.ala.ui.client.util.ContentChangeHandler;
import org.jboss.errai.common.client.api.IsElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;
import org.uberfire.mocks.EventSourceMock;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ProviderConfigurationPagePresenterTest {

    @Mock
    private ProviderConfigurationPagePresenter.View view;

    @Mock
    private EventSourceMock<WizardPageStatusChangeEvent> wizardPageStatusChangeEvent;

    private ProviderConfigurationPagePresenter presenter;

    @Mock
    private ProviderConfigurationForm configurationForm;

    @Mock
    private IsElement configurationFormView;

    @Before
    public void setUp() {
        presenter = new ProviderConfigurationPagePresenter(view,
                                                           wizardPageStatusChangeEvent);
        presenter.init();
        verify(view,
               times(1)).init(presenter);
        when(configurationForm.getView()).thenReturn(configurationFormView);
    }

    @Test
    public void testSetProviderConfigurationForm() {
        presenter.setProviderConfigurationForm(configurationForm);
        verify(view,
               times(1)).setForm(configurationFormView);
        verify(configurationForm,
               times(1)).addContentChangeHandler(any(ContentChangeHandler.class));
    }

    @Test
    public void testCompletionCheck() {
        presenter.setProviderConfigurationForm(configurationForm);
        //the page is completion check is deferred to the generic ProviderConfigurationForm
        @SuppressWarnings("unchecked")
        Callback<Boolean> callback = mock(Callback.class);
        presenter.isComplete(callback);
        verify(configurationForm,
               times(1)).isValid(callback);
    }

    @Test
    public void testClear() {
        presenter.setProviderConfigurationForm(configurationForm);
        presenter.clear();
        verify(configurationForm,
               times(1)).clear();
    }

    @Test
    public void testBuildProviderConfiguration() {
        presenter.setProviderConfigurationForm(configurationForm);
        presenter.buildProviderConfiguration();
        verify(configurationForm,
               times(1)).buildProviderConfiguration();
    }

    @Test
    public void testOnContentChanged() {
        presenter.onContentChanged();
        verify(wizardPageStatusChangeEvent,
               times(1)).fire(any(WizardPageStatusChangeEvent.class));
    }
}
