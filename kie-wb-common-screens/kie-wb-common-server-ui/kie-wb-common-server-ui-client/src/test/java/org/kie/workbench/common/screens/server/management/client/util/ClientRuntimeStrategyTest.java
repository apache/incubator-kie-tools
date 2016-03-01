/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.server.management.client.util;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.server.management.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.server.management.model.RuntimeStrategy;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ClientRuntimeStrategyTest {

    @Mock
    TranslationService translationService;

    @Before
    public void setup() {
        when( translationService.format( any( String.class ) ) ).thenAnswer( new Answer<String>() {
            @Override
            public String answer( InvocationOnMock invocation ) throws Throwable {
                Object[] args = invocation.getArguments();
                return (String) args[ 0 ];
            }
        } );
    }

    @Test
    public void testConvertEnum() {
        assertEquals( ClientRuntimeStrategy.PER_PROCESS_INSTANCE, ClientRuntimeStrategy.convert( RuntimeStrategy.PER_PROCESS_INSTANCE ) );
        assertEquals( ClientRuntimeStrategy.PER_REQUEST, ClientRuntimeStrategy.convert( RuntimeStrategy.PER_REQUEST ) );
        assertEquals( ClientRuntimeStrategy.SINGLETON, ClientRuntimeStrategy.convert( RuntimeStrategy.SINGLETON ) );
    }

    @Test
    public void testConvertString() {
        assertEquals( ClientRuntimeStrategy.PER_PROCESS_INSTANCE, ClientRuntimeStrategy.convert( Constants.ClientRuntimeStrategy_PerProcessInstance, translationService ) );
        assertEquals( ClientRuntimeStrategy.PER_REQUEST, ClientRuntimeStrategy.convert( Constants.ClientRuntimeStrategy_PerRequest, translationService ) );
        assertEquals( ClientRuntimeStrategy.SINGLETON, ClientRuntimeStrategy.convert( Constants.ClientRuntimeStrategy_Singleton, translationService ) );
    }

    @Test
    public void testConvertInvalidString() {
        ClientRuntimeStrategy clientRuntimeStrategy = ClientRuntimeStrategy.convert( "invalidString", translationService );
        assertEquals( ClientRuntimeStrategy.SINGLETON, clientRuntimeStrategy );
    }

}
