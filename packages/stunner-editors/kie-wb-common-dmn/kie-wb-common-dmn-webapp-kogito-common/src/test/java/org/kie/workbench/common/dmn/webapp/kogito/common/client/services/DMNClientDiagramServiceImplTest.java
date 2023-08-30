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

package org.kie.workbench.common.dmn.webapp.kogito.common.client.services;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.DMNDefinitionSet;
import org.kie.workbench.common.dmn.client.DMNShapeSet;
import org.kie.workbench.common.dmn.client.marshaller.DMNMarshallerService;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.registry.definition.TypeDefinitionSetRegistry;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.uberfire.client.promise.Promises;
import org.uberfire.promise.SyncPromises;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DMNClientDiagramServiceImplTest {

    @Mock
    private FactoryManager factoryManager;

    @Mock
    private DefinitionManager definitionManager;

    @Mock
    private DMNMarshallerService marshallerService;

    @Mock
    private ServiceCallback<Diagram> callback;

    private Promises promises;

    private DMNClientDiagramServiceImpl service;

    @Before
    public void setup() {
        promises = new SyncPromises();
        service = spy(new DMNClientDiagramServiceImpl(factoryManager, definitionManager, promises, marshallerService));
    }

    @Test
    public void testUnmarshallWhenXmlIsNotPresent() {

        final String fileName = "file.dmn";
        final String xml = "";

        doNothing().when(service).doNewDiagram(Mockito.<String>any(), any());

        service.transform(fileName, xml, callback);

        verify(service).doNewDiagram(fileName, callback);
    }

    @Test
    public void testUnmarshallWhenXmlIsPresent() {

        final String fileName = "file.dmn";
        final String xml = "<dmn />";

        doNothing().when(service).doTransformation(Mockito.<String>any(), Mockito.<String>any(), any());

        service.transform(fileName, xml, callback);

        verify(service).doTransformation(fileName, xml, callback);
    }

    @Test
    public void testDoNewDiagram() {

        final String fileName = "file.dmn";
        final String title = "file";
        final Metadata metadata = mock(Metadata.class);
        final String defSetId = BindableAdapterUtils.getDefinitionSetId(DMNDefinitionSet.class);
        final String shapeSetId = BindableAdapterUtils.getShapeSetId(DMNShapeSet.class);
        final Diagram diagram = mock(Diagram.class);

        doReturn(metadata).when(service).buildMetadataInstance(fileName);
        when(factoryManager.newDiagram(title, defSetId, metadata)).thenReturn(diagram);

        service.doNewDiagram(fileName, callback);

        verify(marshallerService).setOnDiagramLoad(callback);
        verify(marshallerService).registerDiagramInstance(diagram, title, shapeSetId);
        verify(callback).onSuccess(diagram);
    }

    @Test
    public void testDoTransformation() {

        final String fileName = "file.dmn";
        final String xml = "<dmn />";
        final Metadata metadata = mock(Metadata.class);

        doReturn(metadata).when(service).buildMetadataInstance(fileName);

        service.doTransformation(fileName, xml, callback);

        verify(marshallerService).unmarshall(metadata, xml, callback);
    }

    @Test
    public void testBuildMetadataInstance() {

        final String fileName = "file.dmn";
        when(definitionManager.definitionSets()).thenReturn(mock(TypeDefinitionSetRegistry.class));

        final Metadata metadata = service.buildMetadataInstance(fileName);

        assertEquals("/file.dmn", metadata.getPath().toURI());
    }

    @Test
    public void testMarshall() {

        final Diagram diagram = mock(Diagram.class);

        service.transform(diagram);

        verify(marshallerService).marshall(eq(diagram), any());
    }
}
