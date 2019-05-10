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

package org.kie.workbench.common.dmn.backend.editors.common;

import java.nio.file.NoSuchFileException;

import org.guvnor.common.services.project.model.Package;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.DRGElement;
import org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition;
import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedModel;
import org.kie.workbench.common.dmn.backend.editors.types.exceptions.DMNIncludeModelCouldNotBeCreatedException;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DMNIncludedModelFactoryTest {

    @Mock
    private DMNDiagramHelper dmnDiagramHelper;

    @Mock
    private KieModuleService moduleService;

    @Mock
    private Path path;

    @Mock
    private Diagram<Graph, Metadata> diagram;

    private DMNIncludedModelFactory factory;

    @Before
    public void setup() {
        factory = spy(new DMNIncludedModelFactory(dmnDiagramHelper, moduleService));
    }

    @Test
    public void testCreate() throws Exception {

        final Package aPackage = mock(Package.class);
        final String packageName = "com.kie.dmn";
        final String fileName = "file.dmn";
        final String uri = "/src/main/java/com/kie/dmn/file.dmn";
        final String namespace = "://namespace";
        final Integer expectedDrgElementsCount = 2;
        final Integer expectedItemDefinitionsCount = 3;

        when(aPackage.getPackageName()).thenReturn(packageName);
        when(path.getFileName()).thenReturn(fileName);
        when(path.toURI()).thenReturn(uri);
        when(moduleService.resolvePackage(path)).thenReturn(aPackage);

        when(dmnDiagramHelper.getDiagramByPath(path)).thenReturn(diagram);
        when(dmnDiagramHelper.getNamespace(diagram)).thenReturn(namespace);
        when(dmnDiagramHelper.getNodes(diagram)).thenReturn(asList(mock(DRGElement.class), mock(DRGElement.class)));
        when(dmnDiagramHelper.getItemDefinitions(diagram)).thenReturn(asList(mock(ItemDefinition.class), mock(ItemDefinition.class), mock(ItemDefinition.class)));

        final DMNIncludedModel dmnIncludedModel = factory.create(path);

        assertEquals(packageName, dmnIncludedModel.getModelPackage());
        assertEquals(fileName, dmnIncludedModel.getModelName());
        assertEquals(uri, dmnIncludedModel.getPath());
        assertEquals(namespace, dmnIncludedModel.getNamespace());
        assertEquals(expectedDrgElementsCount, dmnIncludedModel.getDrgElementsCount());
        assertEquals(expectedItemDefinitionsCount, dmnIncludedModel.getItemDefinitionsCount());
    }

    @Test(expected = DMNIncludeModelCouldNotBeCreatedException.class)
    public void testCreateWhenGetNamespaceRaisesAnError() throws Exception {
        doThrow(NoSuchFileException.class).when(dmnDiagramHelper).getDiagramByPath(path);
        factory.create(path);
    }
}
