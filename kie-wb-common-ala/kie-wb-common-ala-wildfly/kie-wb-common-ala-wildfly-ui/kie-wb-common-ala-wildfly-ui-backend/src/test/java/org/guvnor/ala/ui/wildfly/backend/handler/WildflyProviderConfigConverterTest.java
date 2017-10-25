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

package org.guvnor.ala.ui.wildfly.backend.handler;

import java.util.HashMap;
import java.util.Map;

import org.guvnor.ala.ui.model.ProviderConfiguration;
import org.guvnor.ala.wildfly.config.WildflyProviderConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.guvnor.ala.wildfly.config.WildflyProviderConfig.HOST;
import static org.guvnor.ala.wildfly.config.WildflyProviderConfig.MANAGEMENT_PORT;
import static org.guvnor.ala.wildfly.config.WildflyProviderConfig.PORT;
import static org.guvnor.ala.wildfly.config.WildflyProviderConfig.WILDFLY_PASSWORD;
import static org.guvnor.ala.wildfly.config.WildflyProviderConfig.WILDFLY_USER;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WildflyProviderConfigConverterTest {

    private static final String PROVIDER_NAME_VALUE = "PROVIDER_NAME_VALUE";

    private static final String HOST_VALUE = "HOST_VALUE";

    private static final String PORT_VALUE = "PORT_VALUE";

    private static final String MANAGEMENT_PORT_VALUE = "MANAGEMENT_PORT_VALUE";

    private static final String WILDFLY_USER_VALUE = "WILDFLY_USER_VALUE";

    private static final String WILDFLY_PASSWORD_VALUE = "WILDFLY_PASSWORD_VALUE";

    private WildflyProviderConfigConverter converter;

    @Before
    public void setUp() {
        converter = new WildflyProviderConfigConverter();
    }

    @Test
    public void testToDomain() {
        Map<String, Object> values = new HashMap<>();
        values.put(HOST,
                   HOST_VALUE);
        values.put(PORT,
                   PORT_VALUE);
        values.put(MANAGEMENT_PORT,
                   MANAGEMENT_PORT_VALUE);
        values.put(WILDFLY_USER,
                   WILDFLY_USER_VALUE);
        values.put(WILDFLY_PASSWORD,
                   WILDFLY_PASSWORD_VALUE);

        ProviderConfiguration configuration = new ProviderConfiguration(PROVIDER_NAME_VALUE,
                                                                        values);
        WildflyProviderConfig result = converter.toDomain(configuration);

        assertNotNull(result);

        assertEquals(PROVIDER_NAME_VALUE,
                     result.getName());
        assertEquals(HOST_VALUE,
                     result.getHost());
        assertEquals(PORT_VALUE,
                     result.getPort());
        assertEquals(MANAGEMENT_PORT_VALUE,
                     result.getManagementPort());
        assertEquals(WILDFLY_USER_VALUE,
                     result.getUser());
        assertEquals(WILDFLY_PASSWORD_VALUE,
                     result.getPassword());
    }

    @Test
    public void testToDomainNull() {
        assertNull(converter.toDomain(null));
    }

    @Test
    public void testToModel() {
        WildflyProviderConfig config = mock(WildflyProviderConfig.class);
        when(config.getName()).thenReturn(PROVIDER_NAME_VALUE);
        when(config.getHost()).thenReturn(HOST_VALUE);
        when(config.getPort()).thenReturn(PORT_VALUE);
        when(config.getManagementPort()).thenReturn(MANAGEMENT_PORT_VALUE);
        when(config.getUser()).thenReturn(WILDFLY_USER_VALUE);
        when(config.getPassword()).thenReturn(WILDFLY_PASSWORD_VALUE);

        ProviderConfiguration result = converter.toModel(config);

        assertNotNull(result);
        assertEquals(PROVIDER_NAME_VALUE,
                     result.getId());
        assertEquals(HOST_VALUE,
                     result.getValues().get(HOST));
        assertEquals(PORT_VALUE,
                     result.getValues().get(PORT));
        assertEquals(MANAGEMENT_PORT_VALUE,
                     result.getValues().get(MANAGEMENT_PORT));
        assertEquals(WILDFLY_USER_VALUE,
                     result.getValues().get(WILDFLY_USER));
        assertEquals(WILDFLY_PASSWORD_VALUE,
                     result.getValues().get(WILDFLY_PASSWORD));
    }

    @Test
    public void testToModelNull() {
        assertNull(converter.toModel(null));
    }
}
