/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.marshall.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.workitem.WorkItemDefinitionClientService;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.cm.CaseManagementSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.AdHoc;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.DiagramSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Executable;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Id;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Package;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.ProcessInstanceDescription;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.ProcessType;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Version;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.Imports;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.SLADueDate;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.AdvancedData;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessData;
import org.kie.workbench.common.stunner.bpmn.factory.BPMNDiagramFactory;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinition;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.client.ShapeSet;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.DiagramImpl;
import org.kie.workbench.common.stunner.core.diagram.DiagramParsingException;
import org.kie.workbench.common.stunner.core.diagram.MetadataImpl;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bound;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.registry.definition.TypeDefinitionSetRegistry;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.promise.Promises;
import org.uberfire.promise.SyncPromises;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BPMNClientDiagramServiceTest {

    private static final String DEF_SET_ID = BPMNClientMarshalling.getDefinitionSetId();
    private static final String SHAPE_SET_ID = BPMNClientMarshalling.getDefinitionSetId() + "ShapeSet";
    private static final WorkItemDefinition WID1 = new WorkItemDefinition().setName("wid1");
    private static final WorkItemDefinition WID2 = new WorkItemDefinition().setName("wid2");
    private static final WorkItemDefinition WID3 = new WorkItemDefinition().setName("wid3");
    private static final Collection<WorkItemDefinition> WIDS = Arrays.asList(WID1, WID2, WID3);
    private static final String PATH_DIAGRAM = "org/kie/workbench/common/stunner/bpmn/client/marshall/testFlight.bpmn";

    private static String xml;

    static {
        try {
            xml = loadStreamAsString(PATH_DIAGRAM);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BPMNClientDiagramService tested;

    @Mock
    private DefinitionManager definitionManager;

    @Mock
    private BPMNClientMarshalling marshalling;

    @Mock
    private FactoryManager factoryManager;

    @Mock
    private BPMNDiagramFactory diagramFactory;

    @Mock
    private ShapeManager shapeManager;

    @Mock
    private WorkItemDefinitionClientService widService;

    private Promises promises;

    @Mock
    private TypeDefinitionSetRegistry definitionSetRegistry;

    @Mock
    private Graph<DefinitionSet, Node> graph;

    private Name processName;

    private Documentation processDocumentation;

    private Id processId;

    private Package packageProperty;

    private Version version;

    private AdHoc adHoc;

    private ProcessInstanceDescription processInstanceDescription;

    private Executable executable;

    @Mock
    private Imports imports;

    @Mock
    private SLADueDate slaDueDate;

    private DiagramSet diagramSet;

    private ProcessType processType;

    private List<Node> nodes;

    @Before
    public void setUp() {
        promises = new SyncPromises();

        ShapeSet shapeSet = mock(ShapeSet.class);
        when(shapeSet.getId()).thenReturn(SHAPE_SET_ID);
        when(shapeManager.getDefaultShapeSet(eq(DEF_SET_ID)))
                .thenReturn(shapeSet);
        when(definitionManager.definitionSets()).thenReturn(mock(TypeDefinitionSetRegistry.class));
        when(widService.call(any())).thenReturn(promises.create((resolve, reject) -> resolve.onInvoke(WIDS)));

        //DiagramSet
        processName = new Name("");
        processId = new Id("");
        processDocumentation = new Documentation("someDocumentation");
        packageProperty = new Package();
        version = new Version("1.0");
        adHoc = new AdHoc(false);
        processInstanceDescription = new ProcessInstanceDescription("description");
        executable = new Executable(false);
        slaDueDate = new SLADueDate("");
        processType = new ProcessType();

        diagramSet = new DiagramSet(processName,
                                    processDocumentation,
                                    processId,
                                    packageProperty,
                                    processType,
                                    version,
                                    adHoc,
                                    processInstanceDescription,
                                    imports,
                                    executable,
                                    slaDueDate);

        BPMNDiagramImpl bpmnDiagram = new BPMNDiagramImpl(
                diagramSet,
                new ProcessData(),
                new CaseManagementSet(),
                new BackgroundSet(),
                new FontSet(),
                new RectangleDimensionsSet(),
                new AdvancedData()
        );

        nodes = Arrays.asList(createNode(bpmnDiagram));

        tested = new BPMNClientDiagramService(definitionManager, marshalling, factoryManager, diagramFactory, shapeManager, promises, widService);
    }

    public static String loadStreamAsString(final String path) throws IOException {

        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        InputStreamReader isReader = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(isReader);
        StringBuffer sb = new StringBuffer();
        String str;
        while ((str = reader.readLine()) != null) {
            sb.append(str);
        }
        return sb.toString();
    }

    private Node createNode(Object content) {
        NodeImpl node = new NodeImpl(UUID.uuid());
        node.setContent(new ViewImpl<>(content, new Bounds(new Bound(0d, 0d), new Bound(1d, 1d))));
        return node;
    }

    @Test
    public void testNameIdFileName() throws IOException {

        when(definitionManager.definitionSets()).thenReturn(definitionSetRegistry);
        when(marshalling.unmarshall(any(), any())).thenReturn(graph);
        when(graph.nodes()).thenReturn(nodes);

        tested.transform("someFile", xml,
                         new ServiceCallback<Diagram>() {

                             @Override
                             public void onSuccess(Diagram item) {

                             }

                             @Override
                             public void onError(ClientRuntimeError error) {

                             }
                         });

        assertEquals("someFile", diagramSet.getName().getValue());
        assertEquals("someFile", diagramSet.getId().getValue());
    }

    @Test
    public void testNameIdPackageAsDefaultIfNull() {

        when(definitionManager.definitionSets()).thenReturn(definitionSetRegistry);
        when(marshalling.unmarshall(any(), any())).thenReturn(graph);
        when(graph.nodes()).thenReturn(nodes);

        diagramSet.getName().setValue(null);
        diagramSet.getId().setValue(null);
        diagramSet.getPackageProperty().setValue(null);

        tested.transform(BPMNClientDiagramService.DEFAULT_DIAGRAM_ID, xml,

                         new ServiceCallback<Diagram>() {

                             @Override
                             public void onSuccess(Diagram item) {

                             }

                             @Override
                             public void onError(ClientRuntimeError error) {

                             }
                         });

        assertEquals(BPMNClientDiagramService.DEFAULT_DIAGRAM_ID, diagramSet.getName().getValue());
        assertEquals(BPMNClientDiagramService.DEFAULT_DIAGRAM_ID, diagramSet.getId().getValue());
        assertEquals(BPMNClientDiagramService.DEFAULT_PACKAGE, diagramSet.getPackageProperty().getValue());
    }

    @Test
    public void testNameIdPackageAsDefaultIfEmpty() {

        when(definitionManager.definitionSets()).thenReturn(definitionSetRegistry);
        when(marshalling.unmarshall(any(), any())).thenReturn(graph);
        when(graph.nodes()).thenReturn(nodes);

        diagramSet.getName().setValue("");
        diagramSet.getId().setValue("");
        diagramSet.getPackageProperty().setValue("");

        tested.transform(BPMNClientDiagramService.DEFAULT_DIAGRAM_ID, xml,

                         new ServiceCallback<Diagram>() {

                             @Override
                             public void onSuccess(Diagram item) {

                             }

                             @Override
                             public void onError(ClientRuntimeError error) {

                             }
                         });

        assertEquals(BPMNClientDiagramService.DEFAULT_DIAGRAM_ID, diagramSet.getName().getValue());
        assertEquals(BPMNClientDiagramService.DEFAULT_DIAGRAM_ID, diagramSet.getId().getValue());
        assertEquals(BPMNClientDiagramService.DEFAULT_PACKAGE, diagramSet.getPackageProperty().getValue());
    }

    @Test
    public void testNameIdPackageAsDefaultIfNotEmpty() {

        when(definitionManager.definitionSets()).thenReturn(definitionSetRegistry);
        when(marshalling.unmarshall(any(), any())).thenReturn(graph);
        when(graph.nodes()).thenReturn(nodes);
        diagramSet.getName().setValue("somePreviousName");
        diagramSet.getId().setValue("somePreviousId");
        diagramSet.getPackageProperty().setValue("somePreviousPackage");

        tested.transform(BPMNClientDiagramService.DEFAULT_DIAGRAM_ID, xml,

                         new ServiceCallback<Diagram>() {

                             @Override
                             public void onSuccess(Diagram item) {

                             }

                             @Override
                             public void onError(ClientRuntimeError error) {

                             }
                         });

        assertEquals("somePreviousName", diagramSet.getName().getValue());
        assertEquals("somePreviousId", diagramSet.getId().getValue());
        assertEquals("somePreviousPackage", diagramSet.getPackageProperty().getValue());
    }

    @Test
    public void testNameIdPackageDefaultOnNewDiagram() {

        when(definitionManager.definitionSets()).thenReturn(definitionSetRegistry);
        when(marshalling.unmarshall(any(), any())).thenReturn(graph);
        when(graph.nodes()).thenReturn(nodes);
        tested.transform(BPMNClientDiagramService.DEFAULT_DIAGRAM_ID, xml,

                         new ServiceCallback<Diagram>() {

                             @Override
                             public void onSuccess(Diagram item) {

                             }

                             @Override
                             public void onError(ClientRuntimeError error) {

                             }
                         });

        assertEquals(BPMNClientDiagramService.DEFAULT_DIAGRAM_ID, diagramSet.getName().getValue());
        assertEquals(BPMNClientDiagramService.DEFAULT_DIAGRAM_ID, diagramSet.getId().getValue());
        assertEquals(BPMNClientDiagramService.DEFAULT_PACKAGE, diagramSet.getPackageProperty().getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testTransformNewDiagram() {

        ServiceCallback<Diagram> callback = mock(ServiceCallback.class);
        String xml = "";

        MetadataImpl metadata = new MetadataImpl();
        metadata.setDefinitionSetId(DEF_SET_ID);
        Diagram result = new DiagramImpl("result", graph, metadata);

        when(factoryManager.newDiagram(anyString(), eq(DEF_SET_ID), any()))
                .thenReturn(result);
        when(graph.nodes()).thenReturn(nodes);
        tested.transform(xml, callback);

        verify(callback, never()).onError(any());

        ArgumentCaptor<Diagram> diagramArgumentCaptor = ArgumentCaptor.forClass(Diagram.class);
        verify(callback, times(1)).onSuccess(diagramArgumentCaptor.capture());
        Diagram diagram = diagramArgumentCaptor.getValue();
        assertNotNull(diagram);
        assertEquals(result, diagram);
        assertEquals(SHAPE_SET_ID, diagram.getMetadata().getShapeSetId());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testTransformNoDiagram() {

        ServiceCallback<Diagram> callback = mock(ServiceCallback.class);

        tested.transform(BPMNClientDiagramService.DEFAULT_DIAGRAM_ID, xml, callback);

        ArgumentCaptor<ClientRuntimeError> errorArgumentCaptor = ArgumentCaptor.forClass(ClientRuntimeError.class);
        verify(callback, times(1)).onError(errorArgumentCaptor.capture());
        ClientRuntimeError error = errorArgumentCaptor.getValue();

        assertTrue(error.getThrowable() instanceof DiagramParsingException);
    }

    @Test
    public void testGetDiagramTitleWhenIsEmpty() {
        final String actual = tested.createDiagramTitleFromFilePath("");

        assertEquals(BPMNClientDiagramService.DEFAULT_DIAGRAM_ID, actual);
    }

    @Test
    public void testGetDiagramTitleWhenIsFileName() {
        final String fileName = "file.dmn";
        final String expected = "file";

        final String actual = tested.createDiagramTitleFromFilePath(fileName);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetDiagramTitleWhenIsWindowsPath() {
        final String fileName = "C:\\my path\\folder\\file.dmn";
        final String expected = "file";

        final String actual = tested.createDiagramTitleFromFilePath(fileName);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetDiagramTitleWhenIsUnixPath() {
        final String fileName = "/users/user/file.dmn";
        final String expected = "file";

        final String actual = tested.createDiagramTitleFromFilePath(fileName);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetDiagramTitleWhenIsWindowsPathMoreWords() {
        final String fileName = "C:\\my path\\folder\\file a.dmn";
        final String expected = "file a";

        final String actual = tested.createDiagramTitleFromFilePath(fileName);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetDiagramTitleWhenIsUnixPathMoreWords() {
        final String fileName = "/users/user/file a.dmn";
        final String expected = "file a";

        final String actual = tested.createDiagramTitleFromFilePath(fileName);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetDiagramTitleWhenMoreDotsContained() {
        final String fileName = "/users/user/file.template.dmn";
        final String expected = "file.template";

        final String actual = tested.createDiagramTitleFromFilePath(fileName);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetDiagramTitleWhenIsNotFileNameOrEmpty() {
        final String fileName = "Something";
        final String expected = "Something";

        final String actual = tested.createDiagramTitleFromFilePath(fileName);

        assertEquals(expected, actual);
    }
}
