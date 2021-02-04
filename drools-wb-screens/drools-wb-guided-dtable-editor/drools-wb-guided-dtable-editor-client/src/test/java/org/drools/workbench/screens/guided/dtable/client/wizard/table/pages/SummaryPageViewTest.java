/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.client.wizard.table.pages;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class SummaryPageViewTest {

    @Mock
    private SummaryPageView view;
    @Mock
    private Caller<ValidationService> validationServiceCaller;
    @Mock
    private ValidationService validationService;

    @Captor
    ArgumentCaptor<RemoteCallback<Boolean>> callbackCaptor;

    @InjectMocks
    SummaryPageWithEventOverride page;

    @Test
    public void validationWhenStateChanges() {
        when( validationServiceCaller.call( callbackCaptor.capture() ) ).thenReturn( validationService );

        page.stateChanged();
        verify( validationService ).isFileNameValid( any() );

        callbackCaptor.getValue().callback( Boolean.FALSE );
        verify( view ).setValidBaseFileName( false );

        callbackCaptor.getValue().callback( Boolean.TRUE );
        verify( view ).setValidBaseFileName( true );
    }

    public static class SummaryPageWithEventOverride extends SummaryPage {

        @Override
        void fireEvent() {
            // override needed as it wasn't possible to create a mock of the event used here by SummaryPage
        }
    }
}