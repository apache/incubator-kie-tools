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

package org.kie.workbench.common.screens.examples.client.wizard.pages.targetrepository;

import java.util.List;
import javax.enterprise.event.Event;

import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.examples.client.wizard.model.ExamplesWizardModel;
import org.kie.workbench.common.screens.examples.model.ExampleOrganizationalUnit;
import org.kie.workbench.common.screens.examples.model.ExampleTargetRepository;
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

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TargetRepositoryPageTest {

    @Mock
    private TargetRepositoryPageView view;

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
    private ArgumentCaptor<List<ExampleOrganizationalUnit>> organizationalUnitsArgumentCaptor;

    @Captor
    private ArgumentCaptor<ExampleOrganizationalUnit> organizationalUnitArgumentCaptor;

    private TargetRepositoryPage page;

    private ExamplesWizardModel model;

    @Before
    public void setup() {
        page = new TargetRepositoryPage(view,
                                        translator,
                                        examplesServiceCaller,
                                        pageStatusChangedEvent);

        model = new ExamplesWizardModel();
        page.setModel(model);
    }

    @Test
    public void testInit() {
        page.init();
        verify(view,
               times(1)).init(eq(page));
        verify(view,
               times(1)).setTargetRepositoryPlaceHolder(any(String.class));
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
    public void testSetTargetRepository() {
        page.setTargetRepository(new ExampleTargetRepository("target"));
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
               times(1)).setTargetRepositoryGroupType(eq(ValidationState.ERROR));
        verify(view,
               times(1)).showTargetRepositoryHelpMessage(any(String.class));
        verify(examplesService,
               never()).validateRepositoryName(any(String.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testIsComplete_EmptyRepository() {
        model.setTargetRepository(new ExampleTargetRepository(""));
        final Callback<Boolean> callback = mock(Callback.class);
        page.isComplete(callback);

        verify(callback,
               times(1)).callback(eq(false));
        verify(view,
               times(1)).setTargetRepositoryGroupType(eq(ValidationState.ERROR));
        verify(view,
               times(1)).showTargetRepositoryHelpMessage(any(String.class));
        verify(examplesService,
               never()).validateRepositoryName(any(String.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testIsComplete_InvalidRepository() {
        model.setTargetRepository(new ExampleTargetRepository("%$£"));

        when(examplesService.validateRepositoryName(any(String.class))).thenReturn(false);

        final Callback<Boolean> callback = mock(Callback.class);
        page.isComplete(callback);

        verify(callback,
               times(1)).callback(eq(false));
        verify(view,
               times(1)).setTargetRepositoryGroupType(eq(ValidationState.ERROR));
        verify(view,
               times(1)).showTargetRepositoryHelpMessage(any(String.class));
        verify(examplesService,
               times(1)).validateRepositoryName(any(String.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testIsComplete_ValidRepository() {
        model.setTargetRepository(new ExampleTargetRepository("target"));

        when(examplesService.validateRepositoryName(any(String.class))).thenReturn(true);

        final Callback<Boolean> callback = mock(Callback.class);
        page.isComplete(callback);

        verify(callback,
               times(1)).callback(eq(true));
        verify(view,
               times(1)).setTargetRepositoryGroupType(eq(ValidationState.NONE));
        verify(view,
               times(1)).hideTargetRepositoryHelpMessage();
        verify(examplesService,
               times(1)).validateRepositoryName(any(String.class));
    }
}
