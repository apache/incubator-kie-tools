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
import org.kie.workbench.common.dmn.api.editors.included.IncludedModel;
import org.kie.workbench.common.dmn.api.editors.included.PMMLDocumentMetadata;
import org.kie.workbench.common.dmn.api.editors.included.PMMLIncludedModel;
import org.kie.workbench.common.dmn.api.editors.included.PMMLModelMetadata;
import org.kie.workbench.common.dmn.backend.common.DMNImportTypesHelper;
import org.kie.workbench.common.dmn.backend.common.DMNPathsHelper;
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
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IncludedModelFactoryTest {

    private static final String NAMESPACE = "://namespace";

    @Mock
    private DMNImportTypesHelper importTypesHelper;

    @Mock
    private DMNDiagramHelper dmnDiagramHelper;

    @Mock
    private DMNPathsHelper pathsHelper;

    @Mock
    private PMMLIncludedDocumentFactory pmmlDocumentFactory;

    @Mock
    private KieModuleService moduleService;

    @Mock
    private Path dmnModelPath;

    @Mock
    private Path includedModelPath;

    @Mock
    private Diagram<Graph, Metadata> diagram;

    private IncludedModelFactory factory;

    @Before
    public void setup() {
        factory = spy(new IncludedModelFactory(dmnDiagramHelper,
                                               pathsHelper,
                                               importTypesHelper,
                                               pmmlDocumentFactory,
                                               moduleService));
    }

    @Test
    public void testCreateDMN() throws Exception {

        final Package aPackage = mock(Package.class);
        final String packageName = "com.kie.dmn";
        final String fileName = "file.dmn";
        final String uri = "/src/main/java/com/kie/dmn/file.dmn";
        final Integer expectedDrgElementsCount = 2;
        final Integer expectedItemDefinitionsCount = 3;

        when(aPackage.getPackageName()).thenReturn(packageName);
        when(includedModelPath.getFileName()).thenReturn(fileName);
        when(includedModelPath.toURI()).thenReturn(uri);
        when(moduleService.resolvePackage(includedModelPath)).thenReturn(aPackage);

        when(importTypesHelper.isDMN(includedModelPath)).thenReturn(true);
        when(importTypesHelper.isPMML(includedModelPath)).thenReturn(false);
        when(dmnDiagramHelper.getDiagramByPath(includedModelPath)).thenReturn(diagram);
        when(dmnDiagramHelper.getNamespace(diagram)).thenReturn(NAMESPACE);
        when(dmnDiagramHelper.getNodes(diagram)).thenReturn(asList(mock(DRGElement.class), mock(DRGElement.class)));
        when(dmnDiagramHelper.getItemDefinitions(diagram)).thenReturn(asList(mock(ItemDefinition.class), mock(ItemDefinition.class), mock(ItemDefinition.class)));
        when(pathsHelper.getRelativeURI(dmnModelPath, includedModelPath)).thenReturn(uri);

        final IncludedModel includedModel = factory.create(dmnModelPath, includedModelPath);
        assertTrue(includedModel instanceof DMNIncludedModel);

        final DMNIncludedModel dmnIncludedModel = (DMNIncludedModel) includedModel;

        assertEquals(packageName, includedModel.getModelPackage());
        assertEquals(fileName, includedModel.getModelName());
        assertEquals(uri, includedModel.getPath());
        assertEquals(NAMESPACE, dmnIncludedModel.getNamespace());
        assertEquals(expectedDrgElementsCount, dmnIncludedModel.getDrgElementsCount());
        assertEquals(expectedItemDefinitionsCount, dmnIncludedModel.getItemDefinitionsCount());
    }

    @Test
    public void testCreatePMML() throws Exception {

        final PMMLDocumentMetadata pmmlDocument = mock(PMMLDocumentMetadata.class);
        final Package aPackage = mock(Package.class);
        final String packageName = "com.kie.pmml";
        final String fileName = "file.pmml";
        final String uri = "/src/main/java/com/kie/pmml/file.pmml";
        final Integer expectedModelsCount = 2;

        when(aPackage.getPackageName()).thenReturn(packageName);
        when(includedModelPath.getFileName()).thenReturn(fileName);
        when(includedModelPath.toURI()).thenReturn(uri);
        when(moduleService.resolvePackage(includedModelPath)).thenReturn(aPackage);

        when(importTypesHelper.isDMN(includedModelPath)).thenReturn(false);
        when(importTypesHelper.isPMML(includedModelPath)).thenReturn(true);
        when(pathsHelper.getRelativeURI(dmnModelPath, includedModelPath)).thenReturn(uri);
        when(pmmlDocumentFactory.getDocumentByPath(includedModelPath)).thenReturn(pmmlDocument);
        when(pmmlDocument.getModels()).thenReturn(asList(mock(PMMLModelMetadata.class), mock(PMMLModelMetadata.class)));

        final IncludedModel includedModel = factory.create(dmnModelPath, includedModelPath);
        assertTrue(includedModel instanceof PMMLIncludedModel);

        final PMMLIncludedModel pmmlIncludedModel = (PMMLIncludedModel) includedModel;

        assertEquals(packageName, includedModel.getModelPackage());
        assertEquals(fileName, includedModel.getModelName());
        assertEquals(uri, includedModel.getPath());
        assertEquals(expectedModelsCount, pmmlIncludedModel.getModelCount());
    }

    @Test(expected = DMNIncludeModelCouldNotBeCreatedException.class)
    public void testCreateDMNIncludedModelWhenGetNamespaceRaisesAnError() throws Exception {
        when(importTypesHelper.isDMN(includedModelPath)).thenReturn(true);
        doThrow(NoSuchFileException.class).when(dmnDiagramHelper).getDiagramByPath(includedModelPath);

        factory.create(dmnModelPath, includedModelPath);
    }

    @Test(expected = DMNIncludeModelCouldNotBeCreatedException.class)
    public void testCreateUnknownIncludeRaisesAnError() throws Exception {
        when(importTypesHelper.isDMN(includedModelPath)).thenReturn(false);
        when(importTypesHelper.isPMML(includedModelPath)).thenReturn(false);

        factory.create(dmnModelPath, includedModelPath);
    }
}
