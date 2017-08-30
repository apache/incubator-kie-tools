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

package org.kie.workbench.common.screens.examples.client.wizard.pages.sourcerepository;

import java.net.MalformedURLException;
import java.net.URL;
import javax.enterprise.event.Event;

import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.examples.client.wizard.model.ExamplesWizardModel;
import org.kie.workbench.common.screens.examples.model.ExampleRepository;
import org.kie.workbench.common.screens.examples.service.ExamplesService;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SourceRepositoryPageTest {

    private static final String EXAMPLE_REPOSITORY = "https://github.com/guvnorngtestuser1/guvnorng-playground.git";

    @Mock
    private SourceRepositoryPageView view;

    @Mock
    private TranslationService translator;

    private ExamplesService examplesService = mock(ExamplesService.class);
    private Caller<ExamplesService> examplesServiceCaller = new CallerMock<ExamplesService>(examplesService);

    @Spy
    private Event<WizardPageStatusChangeEvent> pageStatusChangedEvent = new EventSourceMock<WizardPageStatusChangeEvent>() {
        @Override
        public void fire(final WizardPageStatusChangeEvent event) {
            //Do nothing. Default implementation throws an exception.
        }
    };

    @Captor
    private ArgumentCaptor<ExampleRepository> repositoryArgumentCaptor;

    private SourceRepositoryPage page;

    private ExamplesWizardModel model;

    @Before
    public void setup() {
        page = new SourceRepositoryPage(view,
                                        translator,
                                        examplesServiceCaller,
                                        pageStatusChangedEvent) {
            @Override
            boolean isUrlValid(final String url) {
                try {
                    //The Presenter uses GWT's URL utilities not available in regular Mockito tests
                    new URL(url);
                } catch (MalformedURLException mfe) {
                    return false;
                }
                return true;
            }
        };

        model = new ExamplesWizardModel();
        page.setModel(model);
    }

    @Test
    public void testInit() {
        page.init();
        verify(view,
               times(1)).init(eq(page));
        verify(view,
               times(1)).setPlaceHolder(any(String.class));
    }

    @Test
    public void testInitialise() {
        page.initialise();
        verify(view,
               times(1)).initialise();
    }

    @Test
    public void testAsWidget() {
        page.asWidget();
        verify(view,
               times(1)).asWidget();
    }

    @Test
    public void testSetPlaygroundRepository_Null() {
        page.setPlaygroundRepository(null);
        verify(view,
               times(1)).showRepositoryUrlInputForm();
        verify(view,
               times(1)).setCustomRepositoryOption();
        verify(view,
               times(1)).disableStockRepositoryOption();
    }

    @Test
    public void testSetPlaygroundRepository() {
        ExampleRepository repository = new ExampleRepository(EXAMPLE_REPOSITORY);
        page.setPlaygroundRepository(repository);

        verify(view,
               times(1)).hideRepositoryUrlInputForm();
        verify(view,
               times(1)).setStockRepositoryOption();
    }

    @Test
    public void testPlaygroundRepositorySelected() {
        page.playgroundRepositorySelected();
        verify(view,
               times(1)).hideRepositoryUrlInputForm();
        verify(view,
               times(1)).setCustomRepositoryValue(null);
        verify(pageStatusChangedEvent,
               times(1)).fire(any(WizardPageStatusChangeEvent.class));
    }

    @Test
    public void testCustomRepositorySelected() {
        page.onCustomRepositorySelected();
        assertNull(model.getSelectedRepository());
        verify(view,
               times(1)).showRepositoryUrlInputForm();
    }

    @Test
    public void testCustomRepositoryValueChanged() {
        page.onCustomRepositoryValueChanged();
        verify(pageStatusChangedEvent,
               times(1)).fire(any(WizardPageStatusChangeEvent.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testIsComplete_NullRepository() {
        final Callback<Boolean> callback = mock(Callback.class);
        page.isComplete(callback);

        verify(callback,
               times(1)).callback(eq(false));
        verify(view,
               times(1)).setUrlGroupType(eq(ValidationState.ERROR));
        verify(view,
               times(1)).showUrlHelpMessage(any(String.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testIsComplete_EmptyRepositoryUrl() {
        final ExampleRepository repository = new ExampleRepository("");
        model.setSelectedRepository(repository);
        final Callback<Boolean> callback = mock(Callback.class);
        page.isComplete(callback);

        verify(callback,
               times(1)).callback(eq(false));
        verify(view,
               times(1)).setUrlGroupType(eq(ValidationState.ERROR));
        verify(view,
               times(1)).showUrlHelpMessage(any(String.class));

        assertFalse(repository.isUrlValid());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testIsComplete_InvalidRepositoryUrl() {
        final ExampleRepository repository = new ExampleRepository("cheese");
        model.setSelectedRepository(repository);
        final Callback<Boolean> callback = mock(Callback.class);
        page.isComplete(callback);

        verify(callback,
               times(1)).callback(eq(false));
        verify(view,
               times(1)).setUrlGroupType(eq(ValidationState.ERROR));
        verify(view,
               times(1)).showUrlHelpMessage(any(String.class));

        assertFalse(repository.isUrlValid());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testIsComplete_ValidRepositoryUrl() {
        final ExampleRepository repository = new ExampleRepository(EXAMPLE_REPOSITORY);
        model.setSelectedRepository(repository);
        final Callback<Boolean> callback = mock(Callback.class);
        page.isComplete(callback);

        verify(callback,
               times(1)).callback(eq(true));
        verify(view,
               times(1)).setUrlGroupType(eq(ValidationState.NONE));
        verify(view,
               times(1)).hideUrlHelpMessage();

        assertTrue(repository.isUrlValid());
    }
}
