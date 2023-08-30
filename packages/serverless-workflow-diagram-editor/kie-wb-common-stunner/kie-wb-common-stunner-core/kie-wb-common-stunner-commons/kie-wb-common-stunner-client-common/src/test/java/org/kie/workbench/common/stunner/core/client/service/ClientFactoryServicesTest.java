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

package org.kie.workbench.common.stunner.core.client.service;

import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.api.ClientFactoryManager;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.service.FactoryService;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClientFactoryServicesTest {

    @Mock
    ClientFactoryManager clientFactoryManager;
    @Mock
    FactoryService factoryService;
    @Mock
    Metadata metadata;

    private ClientFactoryService tested;

    @Before
    public void setup() throws Exception {
        Caller<FactoryService> factoryServiceCaller = new CallerMock<>(factoryService);
        this.tested = new ClientFactoryService(clientFactoryManager,
                                               factoryServiceCaller);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNewDefinitionLocal() {
        String id = "id1";
        ServiceCallback<Object> callback = mock(ServiceCallback.class);
        Object def = mock(Object.class);
        when(clientFactoryManager.newDefinition(eq(id))).thenReturn(def);
        tested.newDefinition(id,
                             callback);
        verify(callback,
               times(1)).onSuccess(eq(def));
        verify(clientFactoryManager,
               times(1)).newDefinition(eq(id));
        verify(factoryService,
               times(0)).newDefinition(anyString());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNewDefinitionRemote() {
        String id = "id1";
        ServiceCallback<Object> callback = mock(ServiceCallback.class);
        Object def = mock(Object.class);
        when(clientFactoryManager.newDefinition(eq(id))).thenReturn(null);
        when(factoryService.newDefinition(eq(id))).thenReturn(def);
        tested.newDefinition(id,
                             callback);
        verify(callback,
               times(1)).onSuccess(eq(def));
        verify(clientFactoryManager,
               times(1)).newDefinition(eq(id));
        verify(factoryService,
               times(1)).newDefinition(eq(id));
    }

    private class MyType {

    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNewElementLocal() {
        String id = "id1";
        String defSetId = "defSet1";
        ServiceCallback<Element> callback = mock(ServiceCallback.class);
        Element def = mock(Element.class);
        when(clientFactoryManager.newElement(eq(id),
                                             eq(defSetId))).thenReturn(def);
        tested.newElement(id,
                          defSetId,
                          callback);
        verify(callback,
               times(1)).onSuccess(eq(def));
        verify(clientFactoryManager,
               times(1)).newElement(eq(id),
                                    eq(defSetId));
        verify(factoryService,
               times(0)).newElement(anyString(),
                                    anyString());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNewElementRemote() {
        String id = "id1";
        String defSetId = "defSet1";
        ServiceCallback<Element> callback = mock(ServiceCallback.class);
        Element def = mock(Element.class);
        when(clientFactoryManager.newElement(eq(id),
                                             eq(defSetId))).thenReturn(null);
        when(factoryService.newElement(eq(id),
                                       eq(defSetId))).thenReturn(def);
        tested.newElement(id,
                          defSetId,
                          callback);
        verify(callback,
               times(1)).onSuccess(eq(def));
        verify(clientFactoryManager,
               times(1)).newElement(eq(id),
                                    eq(defSetId));
        verify(factoryService,
               times(1)).newElement(eq(id),
                                    eq(defSetId));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNewDiagramLocal() {
        String id = "id1";
        String name = "name1";
        ServiceCallback<Diagram> callback = mock(ServiceCallback.class);
        Diagram def = mock(Diagram.class);
        when(clientFactoryManager.newDiagram(eq(name),
                                             eq(id),
                                             any(Metadata.class))).thenReturn(def);
        tested.newDiagram(name,
                          id,
                          metadata,
                          callback);
        verify(callback,
               times(1)).onSuccess(eq(def));
        verify(clientFactoryManager,
               times(1)).newDiagram(eq(name),
                                    eq(id),
                                    eq(metadata));
        verify(factoryService,
               times(0)).newDiagram(anyString(),
                                    anyString(),
                                    any(Metadata.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNewDiagramRemote() {
        String id = "id1";
        String name = "name1";
        ServiceCallback<Diagram> callback = mock(ServiceCallback.class);
        Diagram def = mock(Diagram.class);
        when(clientFactoryManager.newDiagram(eq(name),
                                             eq(id),
                                             any(Metadata.class))).thenReturn(null);
        when(factoryService.newDiagram(eq(name),
                                       eq(id),
                                       any(Metadata.class))).thenReturn(def);
        tested.newDiagram(name,
                          id,
                          metadata,
                          callback);
        verify(callback,
               times(1)).onSuccess(eq(def));
        verify(clientFactoryManager,
               times(1)).newDiagram(eq(name),
                                    eq(id),
                                    any(Metadata.class));
        verify(factoryService,
               times(1)).newDiagram(eq(name),
                                    eq(id),
                                    any(Metadata.class));
    }
}
