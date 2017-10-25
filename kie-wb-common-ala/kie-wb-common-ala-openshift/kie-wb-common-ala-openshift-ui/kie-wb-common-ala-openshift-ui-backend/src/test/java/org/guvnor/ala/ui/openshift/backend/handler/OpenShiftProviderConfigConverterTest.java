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

package org.guvnor.ala.ui.openshift.backend.handler;

import java.util.HashMap;
import java.util.Map;

import org.guvnor.ala.openshift.config.OpenShiftProviderConfig;
import org.guvnor.ala.ui.model.ProviderConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.guvnor.ala.openshift.config.OpenShiftProperty.KUBERNETES_AUTH_BASIC_PASSWORD;
import static org.guvnor.ala.openshift.config.OpenShiftProperty.KUBERNETES_AUTH_BASIC_USERNAME;
import static org.guvnor.ala.openshift.config.OpenShiftProperty.KUBERNETES_MASTER;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OpenShiftProviderConfigConverterTest {

    private static final String PROVIDER_NAME_VALUE = "PROVIDER_NAME_VALUE";

    private static final String MASTER_URL_VALUE = "MASTER_URL_VALUE";

    private static final String USER_VALUE = "USER_VALUE";

    private static final String PASSWORD_VALUE = "PASSWORD_VALUE";

    private OpenShiftProviderConfigConverter converter;

    @Before
    public void setUp() {
        converter = new OpenShiftProviderConfigConverter();
    }

    @Test
    public void testToDomain() {
        Map<String, Object> values = new HashMap<>();
        values.put(KUBERNETES_MASTER.inputKey(),
                   MASTER_URL_VALUE);
        values.put(KUBERNETES_AUTH_BASIC_USERNAME.inputKey(),
                   USER_VALUE);
        values.put(KUBERNETES_AUTH_BASIC_PASSWORD.inputKey(),
                   PASSWORD_VALUE);

        ProviderConfiguration configuration = new ProviderConfiguration(PROVIDER_NAME_VALUE,
                                                                        values);
        OpenShiftProviderConfig result = converter.toDomain(configuration);

        assertNotNull(result);

        assertEquals(PROVIDER_NAME_VALUE,
                     result.getName());
        assertEquals(MASTER_URL_VALUE,
                     result.getKubernetesMaster());
        assertEquals(USER_VALUE,
                     result.getKubernetesAuthBasicUsername());
        assertEquals(PASSWORD_VALUE,
                     result.getKubernetesAuthBasicPassword());
    }

    @Test
    public void testToDomainNull() {
        assertNull(converter.toDomain(null));
    }

    @Test
    public void testToModel() {
        OpenShiftProviderConfig config = mock(OpenShiftProviderConfig.class);
        when(config.getName()).thenReturn(PROVIDER_NAME_VALUE);
        when(config.getKubernetesMaster()).thenReturn(MASTER_URL_VALUE);
        when(config.getKubernetesAuthBasicUsername()).thenReturn(USER_VALUE);
        when(config.getKubernetesAuthBasicPassword()).thenReturn(PASSWORD_VALUE);

        ProviderConfiguration result = converter.toModel(config);

        assertNotNull(result);
        assertEquals(PROVIDER_NAME_VALUE,
                     result.getId());
        assertEquals(MASTER_URL_VALUE,
                     result.getValues().get(KUBERNETES_MASTER.inputKey()));
        assertEquals(USER_VALUE,
                     result.getValues().get(KUBERNETES_AUTH_BASIC_USERNAME.inputKey()));
        assertEquals(PASSWORD_VALUE,
                     result.getValues().get(KUBERNETES_AUTH_BASIC_PASSWORD.inputKey()));
    }

    @Test
    public void testToModelNull() {
        assertNull(converter.toModel(null));
    }
}
