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

package org.guvnor.ala.ui.client.wizard.pipeline.params;

import org.guvnor.ala.ui.client.util.ContentChangeHandler;
import org.jboss.errai.common.client.api.IsElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PipelineParamsPagePresenterTest {

    private static final String TITLE = "TITLE";

    @Mock
    private PipelineParamsPagePresenter.View view;

    @Mock
    private EventSourceMock<WizardPageStatusChangeEvent> wizardPageStatusChangeEvent;

    @Mock
    private PipelineParamsForm paramsForm;

    private PipelineParamsPagePresenter presenter;

    @Before
    public void setUp() {
        presenter = new PipelineParamsPagePresenter(view,
                                                    wizardPageStatusChangeEvent);
        presenter.init();
        verify(view,
               times(1)).init(presenter);
    }

    @Test
    public void testSetPipelineParamsForm() {
        IsElement formView = mock(IsElement.class);
        when(paramsForm.getView()).thenReturn(formView);
        presenter.setPipelineParamsForm(paramsForm);
        verify(view,
               times(1)).setForm(formView);
        verify(paramsForm,
               times(1)).addContentChangeHandler(any(ContentChangeHandler.class));
    }

    @Test
    public void testInitialize() {
        presenter.setPipelineParamsForm(paramsForm);
        presenter.initialise();
        verify(paramsForm,
               times(1)).initialise();
    }

    @Test
    public void testPrepareView() {
        presenter.setPipelineParamsForm(paramsForm);
        presenter.prepareView();
        verify(paramsForm,
               times(1)).prepareView();
    }

    @Test
    public void testIsComplete() {
        presenter.setPipelineParamsForm(paramsForm);
        Callback callback = mock(Callback.class);
        presenter.isComplete(callback);
        verify(paramsForm,
               times(1)).isComplete(callback);
    }

    @Test
    public void testGetTitle() {
        presenter.setPipelineParamsForm(paramsForm);
        when(paramsForm.getWizardTitle()).thenReturn(TITLE);
        assertEquals(TITLE,
                     paramsForm.getWizardTitle());
    }

    @Test
    public void testOnContentChanged() {
        presenter.onContentChanged();
        verify(wizardPageStatusChangeEvent,
               times(1)).fire(any(WizardPageStatusChangeEvent.class));
    }
}
