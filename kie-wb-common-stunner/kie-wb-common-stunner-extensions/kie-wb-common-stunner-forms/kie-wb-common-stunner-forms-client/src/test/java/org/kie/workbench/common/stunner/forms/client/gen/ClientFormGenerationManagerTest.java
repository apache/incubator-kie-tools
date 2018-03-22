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

package org.kie.workbench.common.stunner.forms.client.gen;

import java.util.function.Consumer;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.forms.client.notifications.FormGenerationNotifier;
import org.kie.workbench.common.stunner.forms.client.resources.i18n.FormsClientConstants;
import org.kie.workbench.common.stunner.forms.service.FormGeneratedEvent;
import org.kie.workbench.common.stunner.forms.service.FormGenerationFailureEvent;
import org.kie.workbench.common.stunner.forms.service.FormGenerationService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClientFormGenerationManagerTest {

    @Mock
    private ClientTranslationService translationService;

    @Mock
    private FormGenerationService formGenerationService;

    @Mock
    private Caller<FormGenerationService> formGenerationServiceCaller;

    @Mock
    private FormGenerationNotifier formGenerationNotifier;

    private ClientFormGenerationManager tested;

    @Before
    public void init() {
        when(formGenerationServiceCaller.call(any(RemoteCallback.class),
                                              any(ErrorCallback.class)))
                .thenReturn(formGenerationService);
        tested = new ClientFormGenerationManager(translationService,
                                                 formGenerationNotifier,
                                                 formGenerationServiceCaller);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCall() {
        final Consumer<FormGenerationService> consumer = mock(Consumer.class);
        tested.call(consumer);
        verify(consumer, times(1)).accept(eq(formGenerationService));
    }

    @Test
    public void testOnFormGenerationSuccess() {
        final FormGeneratedEvent event = mock(FormGeneratedEvent.class);
        when(event.getName()).thenReturn("name1");
        tested.onFormGeneratedEvent(event);
        verify(translationService, times(1)).getValue(eq(FormsClientConstants.FormsGenerationSuccess),
                                                         eq("name1"));
        verify(formGenerationNotifier, times(1)).showNotification(anyString());
        verify(formGenerationNotifier, never()).showError(anyString());
    }

    @Test
    public void testOnFormGenerationFailure() {
        final FormGenerationFailureEvent event = mock(FormGenerationFailureEvent.class);
        when(event.getName()).thenReturn("name1");
        tested.onFormGenerationFailureEvent(event);
        verify(translationService, times(1)).getValue(eq(FormsClientConstants.FormsGenerationFailure),
                                                         eq("name1"));
        verify(formGenerationNotifier, times(1)).showError(anyString());
        verify(formGenerationNotifier, never()).showNotification(anyString());
    }
}
