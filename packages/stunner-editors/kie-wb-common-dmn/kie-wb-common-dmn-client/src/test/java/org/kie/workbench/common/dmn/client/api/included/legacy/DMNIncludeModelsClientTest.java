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

package org.kie.workbench.common.dmn.client.api.included.legacy;

import java.util.List;
import java.util.function.Consumer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedModel;
import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedNode;
import org.kie.workbench.common.dmn.api.editors.included.IncludedModel;
import org.kie.workbench.common.dmn.client.service.DMNClientServicesProxy;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;

import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class DMNIncludeModelsClientTest {

    @Mock
    private DMNClientServicesProxy service;

    @Mock
    private Consumer<List<IncludedModel>> listConsumerDMNModels;

    @Mock
    private Consumer<List<DMNIncludedNode>> listConsumerDMNNodes;

    @Mock
    private Consumer<List<ItemDefinition>> listConsumerDMNItemDefinitions;

    @Mock
    private Path path;

    private DMNIncludeModelsClient client;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        client = new DMNIncludeModelsClient(service);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLoadModels() {
        client.loadModels(path,
                          listConsumerDMNModels);

        verify(service).loadModels(eq(path),
                                   any(ServiceCallback.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLoadNodesFromImports() {
        final DMNIncludedModel includedModel1 = mock(DMNIncludedModel.class);
        final DMNIncludedModel includedModel2 = mock(DMNIncludedModel.class);
        final List<DMNIncludedModel> imports = asList(includedModel1, includedModel2);

        client.loadNodesFromImports(imports,
                                    listConsumerDMNNodes);

        verify(service).loadNodesFromImports(eq(imports),
                                             any(ServiceCallback.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLoadItemDefinitionsByNamespace() {
        final String modelName = "model1";
        final String namespace = "://namespace1";

        client.loadItemDefinitionsByNamespace(modelName,
                                              namespace,
                                              listConsumerDMNItemDefinitions);

        verify(service).loadItemDefinitionsByNamespace(eq(modelName),
                                                       eq(namespace),
                                                       any(ServiceCallback.class));
    }
}
