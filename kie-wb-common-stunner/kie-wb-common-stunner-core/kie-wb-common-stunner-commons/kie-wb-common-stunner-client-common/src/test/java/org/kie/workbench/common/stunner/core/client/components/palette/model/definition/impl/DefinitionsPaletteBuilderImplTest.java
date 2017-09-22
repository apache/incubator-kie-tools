/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.components.palette.model.definition.impl;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.components.palette.model.PaletteDefinitionBuilder;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionPaletteItem;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionsPalette;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefinitionsPaletteBuilderImplTest {

    private static final String DEFINITION_SET_ID = "definitionSetId";

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private ClientFactoryService clientFactoryServices;

    @Mock
    private DefinitionManager definitionManager;

    @Mock
    private AdapterManager adapterManager;

    @Mock
    private DefinitionAdapter definitionAdapter;

    private Set<String> definitionIds = new HashSet<>();

    private PaletteDefinitionBuilder.Configuration configuration;

    private PaletteDefinitionBuilder.Callback<DefinitionsPalette, ClientRuntimeError> callback;

    private Object defId1 = new Object();

    private Object defId2 = new Object();

    private Object defId3 = new Object();

    private DefinitionsPalette paletteDefinition;

    private DefinitionsPaletteBuilderImpl paletteBuilder;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        paletteBuilder = new DefinitionsPaletteBuilderImpl(definitionUtils,
                                                           clientFactoryServices);
        definitionIds.add("id1");
        definitionIds.add("id2");
        definitionIds.add("id3");

        configuration = new PaletteDefinitionBuilder.Configuration() {
            @Override
            public String getDefinitionSetId() {
                return DEFINITION_SET_ID;
            }

            @Override
            public Set<String> getDefinitionIds() {
                return definitionIds;
            }
        };
        callback = new PaletteDefinitionBuilder.Callback<DefinitionsPalette, ClientRuntimeError>() {
            @Override
            public void onSuccess(final DefinitionsPalette paletteDefinition) {
                DefinitionsPaletteBuilderImplTest.this.paletteDefinition = paletteDefinition;
            }

            @Override
            public void onError(final ClientRuntimeError error) {
                fail(error.getMessage());
            }
        };

        doAnswer((InvocationOnMock invocation) -> {
            final String id = (String) invocation.getArguments()[0];
            final ServiceCallback callback = (ServiceCallback) invocation.getArguments()[1];
            if (id.equals("id1")) {
                callback.onSuccess(defId1);
            } else if (id.equals("id2")) {
                callback.onSuccess(defId2);
            } else if (id.equals("id3")) {
                callback.onSuccess(defId3);
            } else {
                callback.onError(new ClientRuntimeError("Not found"));
            }
            return null;
        }).when(clientFactoryServices).newDefinition(anyString(),
                                                     any(ServiceCallback.class));

        when(definitionUtils.getDefinitionManager()).thenReturn(definitionManager);
        when(definitionManager.adapters()).thenReturn(adapterManager);
        when(adapterManager.forDefinition()).thenReturn(definitionAdapter);

        doAnswer((InvocationOnMock invocation) -> {
            final Object o = invocation.getArguments()[0];
            if (o == defId1) {
                return "id1";
            } else if (o == defId2) {
                return "id2";
            } else if (o == defId3) {
                return "id3";
            }
            return null;
        }).when(definitionAdapter).getId(anyObject());

        doAnswer((InvocationOnMock invocation) -> {
            final Object o = invocation.getArguments()[0];
            if (o == defId1) {
                return "category1";
            } else if (o == defId2) {
                return "category2";
            } else if (o == defId3) {
                return "category2";
            }
            return null;
        }).when(definitionAdapter).getCategory(anyObject());

        doAnswer((InvocationOnMock invocation) -> {
            final Object o = invocation.getArguments()[0];
            if (o == defId1) {
                return "defId1-title";
            } else if (o == defId2) {
                return "defId2-title";
            } else if (o == defId3) {
                return "defId3-title";
            }
            return null;
        }).when(definitionAdapter).getTitle(anyObject());

        doAnswer((InvocationOnMock invocation) -> {
            final Object o = invocation.getArguments()[0];
            if (o == defId1) {
                return "defId1-description";
            } else if (o == defId2) {
                return "defId2-description";
            } else if (o == defId3) {
                return "defId3-description";
            }
            return null;
        }).when(definitionAdapter).getDescription(anyObject());
    }

    @Test
    public void checkBuildWithNoExclusions() {
        paletteBuilder.build(configuration,
                             callback);

        assertNotNull(paletteDefinition);
        assertEquals(3,
                     paletteDefinition.getItems().size());
        assertPaletteContainsId("id1");
        assertPaletteContainsId("id2");
        assertPaletteContainsId("id3");
    }

    @Test
    public void checkBuildWithCategoryExclusion() {
        paletteBuilder.excludeCategory("category1");

        paletteBuilder.build(configuration,
                             callback);

        assertNotNull(paletteDefinition);
        assertEquals(2,
                     paletteDefinition.getItems().size());
        assertPaletteContainsId("id2");
        assertPaletteContainsId("id3");
    }

    @Test
    public void checkBuildWithDefinitionExclusion() {
        paletteBuilder.excludeDefinition("id3");

        paletteBuilder.build(configuration,
                             callback);

        assertNotNull(paletteDefinition);
        assertEquals(2,
                     paletteDefinition.getItems().size());
        assertPaletteContainsId("id1");
        assertPaletteContainsId("id2");
    }

    private DefinitionPaletteItem assertPaletteContainsId(final String id) {
        return paletteDefinition.getItems().stream().filter(c -> c.getId().equals(id)).findFirst().orElseThrow(() -> new RuntimeException("Not found"));
    }
}
