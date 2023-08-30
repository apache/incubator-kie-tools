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

package org.kie.workbench.common.dmn.client.editors.included.imports.persistence;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.Definitions;
import org.kie.workbench.common.dmn.client.editors.included.BaseIncludedModelActiveRecord;
import org.kie.workbench.common.dmn.client.editors.included.imports.IncludedModelsPageStateProviderImpl;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DefinitionsHandlerTest {

    @Mock
    private IncludedModelsPageStateProviderImpl stateProvider;

    @Mock
    private DMNGraphUtils dmnGraphUtils;

    @Mock
    private Diagram diagram;

    @Mock
    private Definitions definitions;

    private DefinitionsHandler handler;

    private String model;

    private String namespace;

    private Map<String, String> nsContext;

    @Before
    public void setup() {
        handler = new DefinitionsHandler(stateProvider, dmnGraphUtils);
        model = "model";
        namespace = "://namespace";
        nsContext = Stream.of(new AbstractMap.SimpleEntry<>(model, namespace))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        when(stateProvider.getDiagram()).thenReturn(Optional.of(diagram));
        when(dmnGraphUtils.getDefinitions(diagram)).thenReturn(definitions);
        when(definitions.getNsContext()).thenReturn(nsContext);
    }

    @Test
    public void testDestroy() {

        final BaseIncludedModelActiveRecord includedModel = mock(BaseIncludedModelActiveRecord.class);

        when(includedModel.getNamespace()).thenReturn(namespace);

        handler.destroy(includedModel);

        assertEquals(0, nsContext.size());
    }

    @Test
    public void testCreate() {

        final BaseIncludedModelActiveRecord includedModel = mock(BaseIncludedModelActiveRecord.class);
        final String newNamespace = "://namespace2";

        when(includedModel.getNamespace()).thenReturn(newNamespace);

        handler.create(includedModel);

        assertEquals(2, nsContext.size());
        assertTrue(nsContext.values().contains(namespace));
        assertTrue(nsContext.values().contains(newNamespace));
    }
}
