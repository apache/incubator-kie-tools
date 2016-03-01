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
import org.kie.workbench.common.screens.server.management.model.MergeMode;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ClientMergeModeTest {

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
        assertEquals( ClientMergeMode.KEEP_ALL, ClientMergeMode.convert( MergeMode.KEEP_ALL ) );
        assertEquals( ClientMergeMode.OVERRIDE_ALL, ClientMergeMode.convert( MergeMode.OVERRIDE_ALL ) );
        assertEquals( ClientMergeMode.OVERRIDE_EMPTY, ClientMergeMode.convert( MergeMode.OVERRIDE_EMPTY ) );
        assertEquals( ClientMergeMode.MERGE_COLLECTIONS, ClientMergeMode.convert( MergeMode.MERGE_COLLECTIONS ) );
    }

    @Test
    public void testConvertString() {
        assertEquals( ClientMergeMode.OVERRIDE_ALL, ClientMergeMode.convert( Constants.ClientMergeMode_OverrideAll, translationService ) );
        assertEquals( ClientMergeMode.MERGE_COLLECTIONS, ClientMergeMode.convert( Constants.ClientMergeMode_MergeCollections, translationService ) );
        assertEquals( ClientMergeMode.KEEP_ALL, ClientMergeMode.convert( Constants.ClientMergeMode_KeepAll, translationService ) );
        assertEquals( ClientMergeMode.OVERRIDE_EMPTY, ClientMergeMode.convert( Constants.ClientMergeMode_OverrideEmpty, translationService ) );
    }

    @Test
    public void testConvertInvalidString() {
        ClientMergeMode clientMergeMode = ClientMergeMode.convert( "invalidString", translationService );
        assertEquals( ClientMergeMode.KEEP_ALL, clientMergeMode );
    }

}
