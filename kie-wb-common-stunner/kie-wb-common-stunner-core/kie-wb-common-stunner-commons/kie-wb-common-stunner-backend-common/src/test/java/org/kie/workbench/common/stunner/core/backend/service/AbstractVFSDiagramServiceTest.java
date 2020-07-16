/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.backend.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.Instance;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.definition.DefinitionSetResourceType;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.PropertyAdapter;
import org.kie.workbench.common.stunner.core.definition.property.PropertyMetaTypes;
import org.kie.workbench.common.stunner.core.definition.service.DefinitionSetService;
import org.kie.workbench.common.stunner.core.definition.service.DiagramMarshaller;
import org.kie.workbench.common.stunner.core.definition.service.DiagramMetadataMarshaller;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.DiagramParsingException;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.factory.diagram.DiagramFactory;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.marshaller.MarshallingMessage;
import org.kie.workbench.common.stunner.core.marshaller.MarshallingResponse;
import org.kie.workbench.common.stunner.core.registry.BackendRegistryFactory;
import org.kie.workbench.common.stunner.core.registry.factory.FactoryRegistry;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.commons.data.Pair;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.SimpleFileVisitor;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@SuppressWarnings("unchecked")
@RunWith(PowerMockRunner.class)
public abstract class AbstractVFSDiagramServiceTest<M extends Metadata, D extends Diagram<Graph, M>> {

    public static final String DEFINITION_SET_ID = "DEFINITION_SET_ID";

    public static final String RESOURCE_TYPE_SUFFIX = "SUFFIX";

    public static final String RESOURCE_TYPE_PREFIX = "PREFIX";

    public static final String DIR_URI = "default://master@diagrams/diagramsDirectory";

    public static final String FILE_NAME = "TestFile";

    public static final String FILE_NAME_IN_RESOURCE_FORMAT = RESOURCE_TYPE_PREFIX + FILE_NAME + "." + RESOURCE_TYPE_SUFFIX;

    public static final String FILE_URI = DIR_URI + "/" + FILE_NAME_IN_RESOURCE_FORMAT;

    public static final String DIAGRAM_MARSHALLED = "DIAGRAM_MARSHALLED";

    public static final String METADATA_MARSHALLED = "METADATA_MARSHALLED";

    public static final String DEFINITION_SET = "DefinitionSet";

    protected static final String DIAGRAM_UUID = UUID.uuid();

    public static final String DIAGRAM_SVG = "DIAGRAM_SVG";

    public static final String DIAGRAM_FILE_ID = "diagram-id";

    @Mock
    protected DefinitionManager definitionManager;

    @Mock
    protected FactoryManager factoryManager;

    @Mock
    protected Instance<DefinitionSetService> definitionSetServiceInstances;

    @Mock
    protected DefinitionSetService definitionSetService;

    @Mock
    protected DefinitionSetResourceType resourceType;

    @Mock
    protected DiagramMarshaller diagramMarshaller;

    @Mock
    protected DiagramMetadataMarshaller metadataMarshaller;

    @Mock
    protected IOService ioService;

    @Mock
    protected BackendRegistryFactory registryFactory;

    @Mock
    protected FactoryRegistry factoryRegistry;

    protected AbstractVFSDiagramService<M, D> diagramService;

    protected D diagram;

    protected M metadata;

    @Mock
    protected Graph<DefinitionSet, Node> graph;

    @Mock
    protected DefinitionSet graphContent;

    @Mock
    protected DiagramFactory diagramFactory;

    @Mock
    protected AdapterManager adapters;

    @Mock
    protected DefinitionAdapter<Object> definitionAdapter;

    @Mock
    protected Object idProperty;

    @Mock
    protected Node<DefinitionSet, Edge> graphNode;

    @Mock
    protected PropertyAdapter<Object, Object> propertyAdapter;

    @Before
    public void setUp() throws IOException {
        when(resourceType.getPrefix()).thenReturn(RESOURCE_TYPE_PREFIX);
        when(resourceType.getSuffix()).thenReturn(RESOURCE_TYPE_SUFFIX);
        doReturn(Object.class).when(resourceType).getDefinitionSetType();

        when(definitionSetService.getResourceType()).thenReturn(resourceType);
        when(definitionSetService.getDiagramMarshaller()).thenReturn(diagramMarshaller);
        when(diagramMarshaller.getMetadataMarshaller()).thenReturn(metadataMarshaller);
        List<DefinitionSetService> services = new ArrayList<>();
        services.add(definitionSetService);
        when(definitionSetService.accepts(DEFINITION_SET_ID)).thenReturn(true);
        when(definitionSetServiceInstances.iterator()).thenReturn(services.iterator());

        when(factoryManager.registry()).thenReturn(factoryRegistry);

        diagram = mockDiagram();
        metadata = mockMetadata();
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getDefinitionSetId()).thenReturn(DEFINITION_SET_ID);
        when(diagramMarshaller.marshall(diagram)).thenReturn(DIAGRAM_MARSHALLED);
        when(metadataMarshaller.marshall(metadata)).thenReturn(METADATA_MARSHALLED);
        when(diagramMarshaller.unmarshallWithValidation(anyObject())).thenReturn(MarshallingResponse.builder()
                                                                                         .result(graph)
                                                                                         .build());
        when(diagramMarshaller.unmarshall(anyObject(),
                                          anyObject())).thenReturn(graph);
        when(graph.getContent()).thenReturn(graphContent);
        when(graphContent.getDefinition()).thenReturn(DEFINITION_SET);

        when(diagramFactory.build(eq(FILE_NAME),
                                  any(Metadata.class),
                                  eq(graph))).thenReturn(diagram);

        when(definitionManager.adapters()).thenReturn(adapters);
        when(adapters.forDefinition()).thenReturn(definitionAdapter);

        when(definitionAdapter.getMetaProperty(PropertyMetaTypes.ID, DEFINITION_SET)).thenReturn(idProperty);
        when(diagram.getGraph()).thenReturn(graph);
        when(graph.getNode(DIAGRAM_UUID)).thenReturn(graphNode);
        when(graphNode.getContent()).thenReturn(graphContent);
        when(metadata.getCanvasRootUUID()).thenReturn(DIAGRAM_UUID);
        when(adapters.forProperty()).thenReturn(propertyAdapter);
        when(propertyAdapter.getValue(idProperty)).thenReturn(DIAGRAM_FILE_ID);

        diagramService = spy(createVFSDiagramService());
        when(factoryRegistry.getDiagramFactory(DEFINITION_SET, getMetadataType())).thenReturn(diagramFactory);
    }

    public abstract AbstractVFSDiagramService<M, D> createVFSDiagramService();

    public abstract Class<? extends Metadata> getMetadataType();

    public abstract D mockDiagram();

    public abstract M mockMetadata();

    @Test
    public void testCreate() throws IOException {
        Path path = mock(Path.class);
        when(path.toURI()).thenReturn(DIR_URI);

        final org.uberfire.java.nio.file.Path expectedNioPath = Paths.convert(path).resolve(FILE_NAME_IN_RESOURCE_FORMAT);

        when(factoryManager.newDiagram(FILE_NAME,
                                       DEFINITION_SET_ID,
                                       metadata)).thenReturn(diagram);
        diagramService.create(path,
                              FILE_NAME,
                              DEFINITION_SET_ID,
                              metadata);

        verify(ioService,
               times(1)).write(eq(expectedNioPath),
                               eq(DIAGRAM_MARSHALLED),
                               any(CommentedOption.class));
    }

    @Test
    public void testGetRawContent() throws IOException {
        String result = diagramService.getRawContent(diagram);
        assertEquals(DIAGRAM_MARSHALLED,
                     result);
    }

    @Test
    public void testGetDiagramByPath() throws IOException {
        final Path path = mockGetDiagramByPathObjects();

        Diagram result = diagramService.getDiagramByPath(path);
        assertEquals(diagram,
                     result);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetDiagramByPathParseException() throws IOException {
        final String processDefinition = "broken DEFINITION";
        final Path path = mockGetDiagramByPathObjects();
        when(ioService.readAllString(Paths.convert(path))).thenReturn(processDefinition);

        //Mock failure to unmarshall XML to Graph
        try {
            Mockito.when(diagramMarshaller.unmarshallWithValidation(anyObject()))
                    .thenReturn(MarshallingResponse.builder()
                                        .state(MarshallingResponse.State.ERROR)
                                        .addMessage(MarshallingMessage.builder().message("error").build())
                                        .build());

            diagramService.getDiagramByPath(path);
        } catch (DiagramParsingException dpe) {
            assertEquals(processDefinition,
                         dpe.getXml());
            assertNotNull(dpe.getMetadata());
        } catch (Exception e) {
            fail("Exception should have been caught and wrapped as DiagramParsingException");
        }
    }

    protected Path mockGetDiagramByPathObjects() {
        final Path path = mock(Path.class);
        final String fileName = FILE_NAME + "." + RESOURCE_TYPE_SUFFIX;
        when(path.toURI()).thenReturn(FILE_URI);
        when(path.getFileName()).thenReturn(fileName);

        final org.uberfire.java.nio.file.Path expectedNioPath = Paths.convert(path);
        final byte[] content = DIAGRAM_MARSHALLED.getBytes();
        when(resourceType.accept(path)).thenReturn(true);
        when(ioService.readAllBytes(expectedNioPath)).thenReturn(content);

        return path;
    }

    @Test
    public void testContains() {
        Path path = mock(Path.class);
        when(metadata.getPath()).thenReturn(path);
        doReturn(diagram).when(diagramService).getDiagramByPath(path);

        assertTrue(diagramService.contains(diagram));
        verify(diagramService,
               times(1)).getDiagramByPath(path);
    }

    @PrepareForTest({Files.class, Paths.class})
    @Test
    public void testGetAll() {
        ArgumentCaptor<SimpleFileVisitor> visitorArgumentCaptor = ArgumentCaptor.forClass(SimpleFileVisitor.class);
        mockStatic(Files.class);
        mockStatic(Paths.class);

        org.uberfire.java.nio.file.Path root = mock(org.uberfire.java.nio.file.Path.class);

        D diagram = mockDiagram();
        List<Pair<Path, org.uberfire.java.nio.file.Path>> visitedPaths = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Path diagramPath = mock(Path.class);
            org.uberfire.java.nio.file.Path nioDiagramPath = mock(org.uberfire.java.nio.file.Path.class);
            when(Paths.convert(diagramPath)).thenReturn(nioDiagramPath);
            when(Paths.convert(nioDiagramPath)).thenReturn(diagramPath);
            visitedPaths.add(new Pair<>(diagramPath,
                                        nioDiagramPath));
            when(resourceType.accept(diagramPath)).thenReturn(true);
            doReturn(diagram).when(diagramService).getDiagramByPath(diagramPath);
        }
        BasicFileAttributes attrs = mock(BasicFileAttributes.class);

        when(ioService.exists(root)).thenReturn(true);
        diagramService.getDiagramsByPath(root);

        verifyStatic();
        Files.walkFileTree(eq(root),
                           visitorArgumentCaptor.capture());

        visitedPaths.forEach(pair -> {
            visitorArgumentCaptor.getValue().visitFile(pair.getK2(),
                                                       attrs);
            verify(diagramService,
                   times(1)).getDiagramByPath(pair.getK1());
        });
    }

    protected void testBaseSaveOrUpdateSvg() {
        final Path path = mockGetDiagramByPathObjects();

        final Path svgPath = diagramService.saveOrUpdateSvg(path, DIAGRAM_SVG);
        ArgumentCaptor<org.uberfire.java.nio.file.Path> svgPathCaptor = ArgumentCaptor.forClass(org.uberfire.java.nio.file.Path.class);
        verify(ioService).write(svgPathCaptor.capture(),
                                eq(DIAGRAM_SVG),
                                any(CommentedOption.class));
        assertEquals(svgPath.getFileName(), svgPathCaptor.getValue().getFileName().toString());
        assertEquals(DIAGRAM_FILE_ID + AbstractVFSDiagramService.SVG_SUFFIX, svgPath.getFileName());
    }
}
