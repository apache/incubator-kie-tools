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

package org.guvnor.ala.ui.client.wizard;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;
import org.uberfire.ext.widgets.core.client.wizards.WizardView;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class WizardBaseTest {

    @Mock
    protected TranslationService translationService;

    @Mock
    protected EventSourceMock<NotificationEvent> notification;

    @Mock
    protected WizardView wizardView;

    @SuppressWarnings("unchecked")
    public void preparePageCompletion(WizardPage page) {
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Callback callback = (Callback) invocation.getArguments()[0];
                callback.callback(true);
                return null;
            }
        }).when(page).isComplete(any(Callback.class));
    }
}
