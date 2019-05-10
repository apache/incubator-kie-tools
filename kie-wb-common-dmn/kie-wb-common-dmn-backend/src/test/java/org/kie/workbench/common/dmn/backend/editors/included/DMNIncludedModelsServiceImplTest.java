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

package org.kie.workbench.common.dmn.backend.editors.included;

import java.util.List;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition;
import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedModel;
import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedNode;
import org.kie.workbench.common.dmn.backend.common.DMNMarshallerImportsHelper;
import org.kie.workbench.common.dmn.backend.common.DMNPathsHelperImpl;
import org.kie.workbench.common.dmn.backend.editors.common.DMNIncludedModelFactory;
import org.kie.workbench.common.dmn.backend.editors.common.DMNIncludedNodesFilter;
import org.kie.workbench.common.dmn.backend.editors.types.exceptions.DMNIncludeModelCouldNotBeCreatedException;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DMNIncludedModelsServiceImplTest {

    @Mock
    private DMNPathsHelperImpl pathsHelper;

    @Mock
    private DMNIncludedModelFactory includedModelFactory;

    @Mock
    private DMNIncludedNodesFilter includedNodesFilter;

    @Mock
    private DMNMarshallerImportsHelper importsHelper;

    private DMNIncludedModelsServiceImpl service;

    @Before
    public void setup() {
        service = spy(new DMNIncludedModelsServiceImpl(pathsHelper, includedNodesFilter, includedModelFactory, importsHelper));
    }

    @Test
    public void testLoadModelsWhenWorkspaceProjectIsNull() throws Exception {

        final WorkspaceProject workspaceProject = null;
        final Path path1 = mock(Path.class);
        final Path path2 = mock(Path.class);
        final Path path3 = mock(Path.class);
        final DMNIncludedModel dmnIncludedModel1 = mock(DMNIncludedModel.class);
        final DMNIncludedModel dmnIncludedModel2 = mock(DMNIncludedModel.class);

        when(pathsHelper.getDiagramsPaths(workspaceProject)).thenReturn(asList(path1, path2, path3));
        when(includedModelFactory.create(path1)).thenReturn(dmnIncludedModel1);
        when(includedModelFactory.create(path2)).thenReturn(dmnIncludedModel2);
        when(includedModelFactory.create(path3)).thenThrow(new DMNIncludeModelCouldNotBeCreatedException());

        final List<DMNIncludedModel> dmnIncludedModels = service.loadModels(workspaceProject);

        assertEquals(2, dmnIncludedModels.size());
        assertEquals(dmnIncludedModel1, dmnIncludedModels.get(0));
        assertEquals(dmnIncludedModel2, dmnIncludedModels.get(1));
    }

    @Test
    public void testLoadModelsWhenWorkspaceProjectIsNotNull() throws Exception {

        final WorkspaceProject workspaceProject = mock(WorkspaceProject.class);
        final Path rootPath = mock(Path.class);
        final String uri = "/src/path/file.dmn";
        final Path path1 = mock(Path.class);
        final Path path2 = mock(Path.class);
        final Path path3 = mock(Path.class);
        final DMNIncludedModel dmnIncludedModel1 = mock(DMNIncludedModel.class);
        final DMNIncludedModel dmnIncludedModel2 = mock(DMNIncludedModel.class);

        when(workspaceProject.getRootPath()).thenReturn(rootPath);
        when(rootPath.toURI()).thenReturn(uri);
        when(pathsHelper.getDiagramsPaths(workspaceProject)).thenReturn(asList(path1, path2, path3));
        when(includedModelFactory.create(path1)).thenReturn(dmnIncludedModel1);
        when(includedModelFactory.create(path2)).thenReturn(dmnIncludedModel2);
        when(includedModelFactory.create(path3)).thenThrow(new DMNIncludeModelCouldNotBeCreatedException());

        final List<DMNIncludedModel> dmnIncludedModels = service.loadModels(workspaceProject);

        assertEquals(2, dmnIncludedModels.size());
        assertEquals(dmnIncludedModel1, dmnIncludedModels.get(0));
        assertEquals(dmnIncludedModel2, dmnIncludedModels.get(1));
    }

    @Test
    public void testLoadNodesFromImports() {

        final WorkspaceProject workspaceProject = mock(WorkspaceProject.class);
        final DMNIncludedModel includedModel1 = mock(DMNIncludedModel.class);
        final DMNIncludedModel includedModel2 = mock(DMNIncludedModel.class);
        final DMNIncludedModel includedModel3 = mock(DMNIncludedModel.class);
        final Path path1 = mock(Path.class);
        final Path path2 = mock(Path.class);
        final Path path3 = mock(Path.class);
        final DMNIncludedNode node1 = mock(DMNIncludedNode.class);
        final DMNIncludedNode node2 = mock(DMNIncludedNode.class);
        final DMNIncludedNode node3 = mock(DMNIncludedNode.class);
        final DMNIncludedNode node4 = mock(DMNIncludedNode.class);
        final DMNIncludedNode node5 = mock(DMNIncludedNode.class);
        final DMNIncludedNode node6 = mock(DMNIncludedNode.class);
        final DMNIncludedNode node7 = mock(DMNIncludedNode.class);
        final List<DMNIncludedNode> path1Nodes = asList(node1, node2, node3, node4);
        final List<DMNIncludedNode> path2Nodes = singletonList(node5);
        final List<DMNIncludedNode> path3Nodes = asList(node6, node7);
        final List<DMNIncludedModel> includedModels = asList(includedModel1, includedModel2, includedModel3);
        final List<Path> paths = asList(path1, path2, path3);

        when(pathsHelper.getDiagramsPaths(workspaceProject)).thenReturn(paths);
        when(includedNodesFilter.getNodesFromImports(path1, includedModels)).thenReturn(path1Nodes);
        when(includedNodesFilter.getNodesFromImports(path2, includedModels)).thenReturn(path2Nodes);
        when(includedNodesFilter.getNodesFromImports(path3, includedModels)).thenReturn(path3Nodes);

        final List<DMNIncludedNode> actualNodes = service.loadNodesFromImports(workspaceProject, includedModels);
        final List<DMNIncludedNode> expectedNodes = asList(node1, node2, node3, node4, node5, node6, node7);

        assertEquals(expectedNodes, actualNodes);
    }

    @Test
    public void testLoadItemDefinitionsByNamespace() {

        final WorkspaceProject workspaceProject = mock(WorkspaceProject.class);
        final String modelName = "model1";
        final String namespace = "://namespace";
        final org.kie.dmn.model.api.ItemDefinition itemDefinition1 = mock(org.kie.dmn.model.api.ItemDefinition.class);
        final org.kie.dmn.model.api.ItemDefinition itemDefinition2 = mock(org.kie.dmn.model.api.ItemDefinition.class);
        final org.kie.dmn.model.api.ItemDefinition itemDefinition3 = mock(org.kie.dmn.model.api.ItemDefinition.class);
        final ItemDefinition wbItemDefinition1 = mock(ItemDefinition.class);
        final ItemDefinition wbItemDefinition2 = mock(ItemDefinition.class);
        final ItemDefinition wbItemDefinition3 = mock(ItemDefinition.class);
        final List<org.kie.dmn.model.api.ItemDefinition> itemDefinitions = asList(itemDefinition1, itemDefinition2, itemDefinition3);

        doReturn(wbItemDefinition1).when(service).wbFromDMN(itemDefinition1, modelName);
        doReturn(wbItemDefinition2).when(service).wbFromDMN(itemDefinition2, modelName);
        doReturn(wbItemDefinition3).when(service).wbFromDMN(itemDefinition3, modelName);
        when(importsHelper.getImportedItemDefinitionsByNamespace(workspaceProject, modelName, namespace)).thenReturn(itemDefinitions);

        final List<ItemDefinition> actualItemDefinitions = service.loadItemDefinitionsByNamespace(workspaceProject, modelName, namespace);
        final List<ItemDefinition> expectedItemDefinitions = asList(wbItemDefinition1, wbItemDefinition2, wbItemDefinition3);

        assertEquals(expectedItemDefinitions, actualItemDefinitions);
    }
}
