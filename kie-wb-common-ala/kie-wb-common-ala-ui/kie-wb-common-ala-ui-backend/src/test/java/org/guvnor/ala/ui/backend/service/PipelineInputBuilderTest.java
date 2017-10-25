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

package org.guvnor.ala.ui.backend.service;

import java.util.HashMap;
import java.util.Map;

import org.guvnor.ala.config.ProviderConfig;
import org.guvnor.ala.config.RuntimeConfig;
import org.guvnor.ala.pipeline.Input;
import org.guvnor.ala.ui.model.ProviderKey;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PipelineInputBuilderTest {

    private static final String RUNTIME = "RUNTIME";

    private static final String PROVIDER = "PROVIDER";

    private Map<String, String> params;

    private static final int PARAMS_COUNT = 5;

    @Mock
    private ProviderKey providerKey;

    @Before
    public void setUp() {
        when(providerKey.getId()).thenReturn(PROVIDER);
        params = mockParams(PARAMS_COUNT);
    }

    @Test
    public void testBuild() {
        Input result = PipelineInputBuilder.newInstance()
                .withProvider(providerKey)
                .withParams(params)
                .withRuntimeName(RUNTIME).build();

        assertNotNull(result);
        assertEquals(RUNTIME,
                     result.get(RuntimeConfig.RUNTIME_NAME));
        assertEquals(PROVIDER,
                     result.get(ProviderConfig.PROVIDER_NAME));
        params.forEach((name, value) -> assertEquals(value,
                                                     result.get(name)));
    }

    public static Map<String, String> mockParams(int count) {
        HashMap<String, String> params = new HashMap<>();
        for (int i = 0; i < count; i++) {
            params.put("param.name." + i,
                       "param.value." + i);
        }
        return params;
    }
}
