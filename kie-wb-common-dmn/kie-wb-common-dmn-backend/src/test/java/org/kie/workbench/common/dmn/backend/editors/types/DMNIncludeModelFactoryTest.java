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

package org.kie.workbench.common.dmn.backend.editors.types;

import java.nio.file.NoSuchFileException;

import org.guvnor.common.services.project.model.Package;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNDiagram;
import org.kie.workbench.common.dmn.api.definition.v1_1.Definitions;
import org.kie.workbench.common.dmn.api.editors.types.DMNIncludeModel;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.kie.workbench.common.dmn.backend.editors.types.exceptions.DMNIncludeModelCouldNotBeCreatedException;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.service.DiagramService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DMNIncludeModelFactoryTest {

    @Mock
    private DiagramService diagramService;

    @Mock
    private KieModuleService moduleService;

    @Mock
    private Graph<?, Node> graph;

    @Mock
    private Node node;

    @Mock
    private Definition definition;

    @Mock
    private DMNDiagram dmnDiagram;

    @Mock
    private Diagram<Graph, Metadata> diagram;

    @Mock
    private Definitions definitions;

    @Mock
    private Path path;

    private DMNIncludeModelFactory factory;

    @Before
    public void setup() {
        factory = spy(new DMNIncludeModelFactory(diagramService, moduleService));
    }

    @Test
    public void testCreate() throws Exception {

        final Package aPackage = mock(Package.class);
        final String packageName = "com.kie.dmn";
        final String fileName = "file.dmn";
        final String uri = "/src/main/java/com/kie/dmn/file.dmn";
        final String namespace = "://namespace";

        when(aPackage.getPackageName()).thenReturn(packageName);
        when(path.getFileName()).thenReturn(fileName);
        when(path.toURI()).thenReturn(uri);
        when(moduleService.resolvePackage(path)).thenReturn(aPackage);
        when(diagramService.getDiagramByPath(path)).thenReturn(diagram);
        doReturn(namespace).when(factory).getNamespace(diagram);

        final DMNIncludeModel dmnIncludeModel = factory.create(path);

        assertEquals(packageName, dmnIncludeModel.getModelPackage());
        assertEquals(fileName, dmnIncludeModel.getModelName());
        assertEquals(uri, dmnIncludeModel.getPath());
        assertEquals(namespace, dmnIncludeModel.getNamespace());
    }

    @Test(expected = DMNIncludeModelCouldNotBeCreatedException.class)
    public void testCreateWhenGetNamespaceRaisesAnError() throws Exception {
        doThrow(NoSuchFileException.class).when(factory).getNamespace(path);
        factory.create(path);
    }

    @Test
    public void testGetNamespace() {

        final String expectedNamespace = "://namespace";

        when(diagram.getGraph()).thenReturn(graph);
        when(graph.nodes()).thenReturn(singletonList(node));
        when(node.getContent()).thenReturn(definition);
        when(definition.getDefinition()).thenReturn(dmnDiagram);
        when(dmnDiagram.getDefinitions()).thenReturn(definitions);
        when(definitions.getNamespace()).thenReturn(new Text(expectedNamespace));

        final String actualNamespace = factory.getNamespace(diagram);

        assertEquals(expectedNamespace, actualNamespace);
    }
}
