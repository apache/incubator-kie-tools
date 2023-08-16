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


package org.kie.workbench.common.stunner.forms.client.notifications;

import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FormGenerationNotifierTest {

    private static final String MESSAGE = "Everything OK!";
    private static final String ERROR = "Total disaster!";

    @Mock
    private ClientTranslationService translationService;

    @Mock
    private Consumer<String> messageNotification;

    @Mock
    private Consumer<String> errorNotification;

    private FormGenerationNotifier notifier;

    @Before
    public void init() {
        notifier = new FormGenerationNotifier(translationService, messageNotification, errorNotification);
    }

    @Test
    public void testShowNotification() {
        notifier.showNotification(MESSAGE);

        verify(messageNotification, times(1)).accept(MESSAGE);
        verify(errorNotification, times(0)).accept(MESSAGE);
    }

    @Test
    public void testshowError() {
        notifier.showError(ERROR);

        verify(errorNotification, times(1)).accept(ERROR);
        verify(messageNotification, times(0)).accept(ERROR);
    }
}
