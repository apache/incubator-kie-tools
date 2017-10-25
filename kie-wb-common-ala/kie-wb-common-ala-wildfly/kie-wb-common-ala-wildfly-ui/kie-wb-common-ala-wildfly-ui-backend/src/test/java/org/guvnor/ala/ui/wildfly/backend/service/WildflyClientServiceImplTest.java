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

package org.guvnor.ala.ui.wildfly.backend.service;

import org.guvnor.ala.ui.wildfly.service.TestConnectionResult;
import org.guvnor.ala.ui.wildfly.service.WildflyClientService;
import org.guvnor.ala.wildfly.access.WildflyClient;
import org.guvnor.ala.wildfly.access.exceptions.WildflyClientException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WildflyClientServiceImplTest {

    private static final String CONNECTION_SUCCESSFUL = "CONNECTION_SUCCESSFUL";

    private static final String ERROR_MESSAGE = "ERROR_MESSAGE";

    private static final String DUMMY_VALUE = "DUMMY_VALUE";

    private static final int DUMMY_INT_VALUE = 9990;

    @Mock
    private WildflyClient wfClient;

    private WildflyClientService wildflyClientService;

    @Before
    public void setUp() {
        wildflyClientService = new WildflyClientServiceImpl() {
            @Override
            protected WildflyClient createWFClient(String host,
                                                   int port,
                                                   int managementPort,
                                                   String user,
                                                   String password) {
                return wfClient;
            }
        };
    }

    @Test
    public void testTestConnectionSuccessful() {
        when(wfClient.testConnection()).thenReturn(CONNECTION_SUCCESSFUL);
        TestConnectionResult result = wildflyClientService.testConnection(DUMMY_VALUE,
                                                                          DUMMY_INT_VALUE,
                                                                          DUMMY_INT_VALUE,
                                                                          DUMMY_VALUE,
                                                                          DUMMY_VALUE);
        assertFalse(result.getManagementConnectionError());
        assertEquals(CONNECTION_SUCCESSFUL,
                     result.getManagementConnectionMessage());
    }

    @Test
    public void testTestConnectionFailed() {
        when(wfClient.testConnection()).thenThrow(new WildflyClientException(ERROR_MESSAGE));
        TestConnectionResult result = wildflyClientService.testConnection(DUMMY_VALUE,
                                                                          DUMMY_INT_VALUE,
                                                                          DUMMY_INT_VALUE,
                                                                          DUMMY_VALUE,
                                                                          DUMMY_VALUE);
        assertTrue(result.getManagementConnectionError());
        assertEquals(ERROR_MESSAGE,
                     result.getManagementConnectionMessage());
    }
}
