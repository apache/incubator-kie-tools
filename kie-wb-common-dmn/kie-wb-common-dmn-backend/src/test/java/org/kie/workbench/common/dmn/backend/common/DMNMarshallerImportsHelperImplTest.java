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

package org.kie.workbench.common.dmn.backend.common;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.dmn.api.marshalling.DMNMarshaller;
import org.kie.dmn.model.api.DRGElement;
import org.kie.dmn.model.api.Definitions;
import org.kie.dmn.model.api.Import;
import org.kie.dmn.model.api.InformationItem;
import org.kie.dmn.model.api.ItemDefinition;
import org.kie.dmn.model.v1_2.TDecision;
import org.kie.dmn.model.v1_2.TDecisionService;
import org.kie.dmn.model.v1_2.TInformationItem;
import org.kie.dmn.model.v1_2.TInputData;
import org.kie.dmn.model.v1_2.TItemDefinition;
import org.kie.workbench.common.dmn.api.editors.included.PMMLDocumentMetadata;
import org.kie.workbench.common.dmn.backend.editors.common.PMMLIncludedDocumentFactory;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.IOException;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.uberfire.backend.vfs.PathFactory.PathImpl;

@RunWith(MockitoJUnitRunner.class)
public class DMNMarshallerImportsHelperImplTest {

    @Mock
    private DMNPathsHelperImpl pathsHelper;

    @Mock
    private WorkspaceProjectService projectService;

    @Mock
    private IOService ioService;

    @Mock
    private DMNMarshaller marshaller;

    @Mock
    private DMNIOHelper dmnIOHelper;

    @Mock
    private PMMLIncludedDocumentFactory pmmlDocumentFactory;

    private DMNMarshallerImportsHelperImpl helper;

    @Before
    public void setup() {
        helper = spy(new DMNMarshallerImportsHelperImpl(pathsHelper,
                                                        projectService,
                                                        marshaller,
                                                        dmnIOHelper,
                                                        pmmlDocumentFactory,
                                                        ioService));
    }

    @Test
    public void testGetImportDefinitions() {

        final Metadata metadata = mock(Metadata.class);
        final Import import1 = mock(Import.class);
        final Import import2 = mock(Import.class);
        final Import import3 = mock(Import.class);
        final List<Import> imports = asList(import1, import2, import3);
        final Definitions definitions1 = mock(Definitions.class);
        final Definitions definitions2 = mock(Definitions.class);
        final Definitions definitions3 = mock(Definitions.class);
        final List<Definitions> definitions = asList(definitions1, definitions2, definitions3);

        when(definitions1.getNamespace()).thenReturn("://namespace1");
        when(definitions2.getNamespace()).thenReturn("://namespace2");
        when(definitions3.getNamespace()).thenReturn("://namespace3");
        when(import1.getNamespace()).thenReturn("://namespace1");
        when(import2.getNamespace()).thenReturn("://namespace2-diff");
        when(import3.getNamespace()).thenReturn("://namespace3");
        doReturn(definitions).when(helper).getOtherDMNDiagramsDefinitions(metadata);

        final Map<Import, Definitions> importDefinitions = helper.getImportDefinitions(metadata, imports);

        assertEquals(2, importDefinitions.size());
        assertEquals(definitions1, importDefinitions.get(import1));
        assertEquals(definitions3, importDefinitions.get(import3));
    }

    @Test
    public void testGetPMMLDocuments() {
        final Path dmnModelPath = mock(Path.class);
        final Metadata metadata = mock(Metadata.class);
        final PMMLDocumentMetadata pmmlDocument = mock(PMMLDocumentMetadata.class);
        final Import import1 = mock(Import.class);
        final List<Import> imports = singletonList(import1);
        final Path path1 = mock(Path.class);
        final Path path2 = mock(Path.class);
        final List<Path> paths = asList(path1, path2);

        when(metadata.getPath()).thenReturn(dmnModelPath);
        when(import1.getLocationURI()).thenReturn("document1.pmml");
        when(pathsHelper.getRelativeURI(dmnModelPath, path1)).thenReturn("document1.pmml");
        when(pathsHelper.getRelativeURI(dmnModelPath, path2)).thenReturn("document2.pmml");
        when(pmmlDocumentFactory.getDocumentByPath(path1)).thenReturn(pmmlDocument);

        doReturn(paths).when(helper).getPMMLDocumentPaths(metadata);

        final Map<Import, PMMLDocumentMetadata> importDefinitions = helper.getPMMLDocuments(metadata, imports);

        assertEquals(1, importDefinitions.size());

        assertEquals(pmmlDocument, importDefinitions.get(import1));
    }

    @Test
    public void testGetPMMLDocumentPaths() {
        final Metadata metadata = mock(Metadata.class);
        final WorkspaceProject project = mock(WorkspaceProject.class);
        final Path projectPath = mock(Path.class);

        when(metadata.getPath()).thenReturn(projectPath);
        when(projectService.resolveProject(any(Path.class))).thenReturn(project);

        helper.getPMMLDocumentPaths(metadata);

        verify(projectService).resolveProject(projectPath);
    }

    @Test
    public void testGetImportXML() throws java.io.IOException {
        final String xml1 = "<some xml/>";
        final String xml2 = "<some other xml/>";

        final Metadata metadata = mock(Metadata.class);
        final Import import1 = mock(Import.class);

        final Path path1 = makePath("../file1.dmn");
        final Path path2 = makePath("../file2.dmn");

        //Mock loading of XML files in Project
        final InputStream inputStream1 = mock(InputStream.class);
        final InputStream inputStream2 = mock(InputStream.class);
        final InputStreamReader inputStreamReader1 = mock(InputStreamReader.class);
        final InputStreamReader inputStreamReader2 = mock(InputStreamReader.class);
        final List<Path> paths = asList(path1, path2);

        when(pathsHelper.getDMNModelsPaths(any())).thenReturn(paths);
        when(dmnIOHelper.isAsString(inputStream1)).thenReturn(xml1);
        when(dmnIOHelper.isAsString(inputStream2)).thenReturn(xml2);

        doReturn(Optional.of(inputStream1)).when(helper).loadPath(path1);
        doReturn(Optional.of(inputStream2)).when(helper).loadPath(path2);
        doReturn(inputStreamReader1).when(helper).toInputStreamReader(inputStream1);
        doReturn(inputStreamReader2).when(helper).toInputStreamReader(inputStream2);

        //Mock retrieval of Definitions from XML files in Project
        final StringReader stringReader1 = mock(StringReader.class);
        final StringReader stringReader2 = mock(StringReader.class);
        final Definitions definitions1 = mock(Definitions.class);
        final Definitions definitions2 = mock(Definitions.class);

        doReturn(stringReader1).when(helper).toStringReader(xml1);
        doReturn(stringReader2).when(helper).toStringReader(xml2);
        when(marshaller.unmarshal(stringReader1)).thenReturn(definitions1);
        when(marshaller.unmarshal(stringReader2)).thenReturn(definitions2);
        when(import1.getNamespace()).thenReturn("://namespace1");
        when(definitions1.getNamespace()).thenReturn("://namespace1");
        when(definitions2.getNamespace()).thenReturn("://namespace2-not-imported");

        final List<Import> imports = Collections.singletonList(import1);

        final Map<Import, String> importXML = helper.getImportXML(metadata, imports);

        assertEquals(1, importXML.size());
        assertEquals(xml1, importXML.get(import1));
    }

    @Test
    public void testGetImportedDRGElements() {

        final Map<Import, Definitions> importDefinitions = new HashMap<>();
        final Import anImport = mock(Import.class);
        final Definitions definitions = mock(Definitions.class);
        final DRGElement drgElement1 = mock(DRGElement.class);
        final DRGElement drgElement2 = mock(DRGElement.class);
        final DRGElement drgElement3 = mock(DRGElement.class);
        final List<DRGElement> expectedDRGElements = asList(drgElement1, drgElement2, drgElement3);

        doReturn(expectedDRGElements).when(helper).getDrgElementsWithNamespace(definitions, anImport);

        importDefinitions.put(anImport, definitions);

        final List<DRGElement> actualDRGElements = helper.getImportedDRGElements(importDefinitions);

        assertEquals(expectedDRGElements, actualDRGElements);
    }

    @Test
    public void testGetImportedItemDefinitions() {

        final Map<Import, Definitions> importDefinitions = new HashMap<>();
        final Import anImport = mock(Import.class);
        final Definitions definitions = mock(Definitions.class);
        final ItemDefinition itemDefinition1 = mock(ItemDefinition.class);
        final ItemDefinition itemDefinition2 = mock(ItemDefinition.class);
        final ItemDefinition itemDefinition3 = mock(ItemDefinition.class);
        final List<ItemDefinition> expectedItemDefinitions = asList(itemDefinition1, itemDefinition2, itemDefinition3);

        doReturn(expectedItemDefinitions).when(helper).getItemDefinitionsWithNamespace(definitions, anImport);

        importDefinitions.put(anImport, definitions);

        final List<ItemDefinition> actualItemDefinitions = helper.getImportedItemDefinitions(importDefinitions);

        assertEquals(expectedItemDefinitions, actualItemDefinitions);
    }

    @Test
    public void testGetDrgElementsWithNamespace() {

        final Definitions definitions = mock(Definitions.class);
        final Import anImport = mock(Import.class);
        final TDecision drgElement1 = new TDecision();
        final TInputData drgElement2 = new TInputData();
        final TDecisionService drgElement3 = new TDecisionService();
        final InformationItem informationItem1 = new TInformationItem();
        final InformationItem informationItem2 = new TInformationItem();
        final InformationItem informationItem3 = new TInformationItem();
        final List<DRGElement> drgElements = asList(drgElement1, drgElement2, drgElement3);
        final String namespace = "http://github.com/kiegroup/_something";

        when(anImport.getName()).thenReturn("model");
        when(anImport.getNamespace()).thenReturn(namespace);
        informationItem1.setTypeRef(new QName(XMLConstants.NULL_NS_URI, "tUUID", XMLConstants.DEFAULT_NS_PREFIX));
        informationItem2.setTypeRef(new QName(XMLConstants.NULL_NS_URI, "tAge", XMLConstants.DEFAULT_NS_PREFIX));
        informationItem3.setTypeRef(new QName(XMLConstants.NULL_NS_URI, "tNum", XMLConstants.DEFAULT_NS_PREFIX));
        drgElement1.setId("0000-1111");
        drgElement2.setId("2222-3333");
        drgElement3.setId("4444-5555");
        drgElement1.setName("Decision");
        drgElement2.setName("Input Data");
        drgElement3.setName("Decision Service");
        drgElement1.setVariable(informationItem1);
        drgElement2.setVariable(informationItem2);
        drgElement3.setVariable(informationItem3);
        when(definitions.getDrgElement()).thenReturn(drgElements);

        final List<DRGElement> elements = helper.getDrgElementsWithNamespace(definitions, anImport);

        assertEquals(3, elements.size());

        final TDecision element1 = (TDecision) elements.get(0);
        assertEquals("model:0000-1111", element1.getId());
        assertEquals("model.Decision", element1.getName());
        assertEquals("model.tUUID", element1.getVariable().getTypeRef().getLocalPart());
        assertEquals(namespace, getNamespace(element1));

        final TInputData element2 = (TInputData) elements.get(1);
        assertEquals("model:2222-3333", element2.getId());
        assertEquals("model.Input Data", element2.getName());
        assertEquals("model.tAge", element2.getVariable().getTypeRef().getLocalPart());
        assertEquals(namespace, getNamespace(element2));

        final TDecisionService element3 = (TDecisionService) elements.get(2);
        assertEquals("model:4444-5555", element3.getId());
        assertEquals("model.Decision Service", element3.getName());
        assertEquals("model.tNum", element3.getVariable().getTypeRef().getLocalPart());
        assertEquals(namespace, getNamespace(element3));
    }

    private String getNamespace(final DRGElement element) {
        return element.getAdditionalAttributes().get(DMNMarshallerImportsHelperImpl.NAMESPACE);
    }

    @Test
    public void testGetItemDefinitionsWithNamespace() {

        final Definitions definitions = mock(Definitions.class);
        final Import anImport = mock(Import.class);
        final ItemDefinition itemDefinition1 = new TItemDefinition();
        final ItemDefinition itemDefinition2 = new TItemDefinition();
        final ItemDefinition itemDefinition3 = new TItemDefinition();
        final ItemDefinition itemDefinition4 = new TItemDefinition();
        final ItemDefinition itemDefinition5 = new TItemDefinition();
        final List<ItemDefinition> itemDefinitions = asList(itemDefinition1, itemDefinition2, itemDefinition3, itemDefinition4, itemDefinition5);

        itemDefinition1.setName("tUUID");
        itemDefinition2.setName("tPerson");
        itemDefinition3.setName("id");
        itemDefinition4.setName("name");
        itemDefinition5.setName("age");
        itemDefinition1.setTypeRef(new QName("string"));
        itemDefinition2.setTypeRef(null);
        itemDefinition3.setTypeRef(new QName("tUUID"));
        itemDefinition4.setTypeRef(new QName("string"));
        itemDefinition5.setTypeRef(new QName("number"));

        when(anImport.getName()).thenReturn("model");
        when(definitions.getItemDefinition()).thenReturn(itemDefinitions);

        final List<ItemDefinition> actualItemDefinitions = helper.getItemDefinitionsWithNamespace(definitions, anImport);

        assertEquals(5, actualItemDefinitions.size());

        final ItemDefinition actualItemDefinition1 = actualItemDefinitions.get(0);
        assertEquals("model.tUUID", actualItemDefinition1.getName());
        assertEquals("string", actualItemDefinition1.getTypeRef().getLocalPart());

        final ItemDefinition actualItemDefinition2 = actualItemDefinitions.get(1);
        assertEquals("model.tPerson", actualItemDefinition2.getName());
        assertNull(actualItemDefinition2.getTypeRef());

        final ItemDefinition actualItemDefinition3 = actualItemDefinitions.get(2);
        assertEquals("model.id", actualItemDefinition3.getName());
        assertEquals("model.tUUID", actualItemDefinition3.getTypeRef().getLocalPart());

        final ItemDefinition actualItemDefinition4 = actualItemDefinitions.get(3);
        assertEquals("model.name", actualItemDefinition4.getName());
        assertEquals("string", actualItemDefinition4.getTypeRef().getLocalPart());

        final ItemDefinition actualItemDefinition5 = actualItemDefinitions.get(4);
        assertEquals("model.age", actualItemDefinition5.getName());
        assertEquals("number", actualItemDefinition5.getTypeRef().getLocalPart());
    }

    @Test
    public void testGetOtherDMNDiagramsDefinitions() {

        final Metadata metadata = mock(Metadata.class);
        final Path path1 = makePath("../file1.dmn");
        final Path path2 = makePath("../file2.dmn");
        final Path path3 = makePath("../file3.dmn");
        final Path path4 = makePath("../file4.dmn");
        final InputStream inputStream1 = mock(InputStream.class);
        final InputStream inputStream2 = mock(InputStream.class);
        final InputStream inputStream3 = mock(InputStream.class);
        final InputStreamReader inputStreamReader1 = mock(InputStreamReader.class);
        final InputStreamReader inputStreamReader2 = mock(InputStreamReader.class);
        final InputStreamReader inputStreamReader3 = mock(InputStreamReader.class);
        final Definitions definitions1 = mock(Definitions.class);
        final Definitions definitions2 = mock(Definitions.class);
        final Definitions definitions3 = mock(Definitions.class);
        final List<Path> paths = asList(path1, path2, path3, path4);

        when(pathsHelper.getDMNModelsPaths(any())).thenReturn(paths);
        when(metadata.getPath()).thenReturn(path2);
        doReturn(Optional.of(inputStream1)).when(helper).loadPath(path1);
        doReturn(Optional.of(inputStream2)).when(helper).loadPath(path2);
        doReturn(Optional.of(inputStream3)).when(helper).loadPath(path3);
        doReturn(inputStreamReader1).when(helper).toInputStreamReader(inputStream1);
        doReturn(inputStreamReader2).when(helper).toInputStreamReader(inputStream2);
        doReturn(inputStreamReader3).when(helper).toInputStreamReader(inputStream3);
        doReturn(Optional.empty()).when(helper).loadPath(path4);
        when(marshaller.unmarshal(inputStreamReader1)).thenReturn(definitions1);
        when(marshaller.unmarshal(inputStreamReader2)).thenReturn(definitions2);
        when(marshaller.unmarshal(inputStreamReader3)).thenReturn(definitions3);

        final List<Definitions> actualDefinitions = helper.getOtherDMNDiagramsDefinitions(metadata);
        final List<Definitions> expectedDefinitions = asList(definitions1, definitions3);

        assertEquals(expectedDefinitions, actualDefinitions);
    }

    @Test
    public void testGetOtherDMNDiagramsDefinitionsWhenProjectCannotBeFound() {

        final Metadata metadata = mock(Metadata.class);
        final Path path1 = makePath("../file1.dmn");
        final Path path2 = makePath("../file2.dmn");
        final Path path3 = makePath("../file3.dmn");
        final InputStream inputStream1 = mock(InputStream.class);
        final InputStream inputStream2 = mock(InputStream.class);
        final InputStream inputStream3 = mock(InputStream.class);
        final InputStreamReader inputStreamReader1 = mock(InputStreamReader.class);
        final InputStreamReader inputStreamReader2 = mock(InputStreamReader.class);
        final InputStreamReader inputStreamReader3 = mock(InputStreamReader.class);
        final Definitions definitions1 = mock(Definitions.class);
        final Definitions definitions2 = mock(Definitions.class);
        final Definitions definitions3 = mock(Definitions.class);
        final List<Path> paths = asList(path1, path2, path3);

        when(projectService.resolveProject(any(Path.class))).thenThrow(new NullPointerException());
        when(pathsHelper.getDMNModelsPaths(any())).thenReturn(paths);
        when(metadata.getPath()).thenReturn(path2);
        doReturn(Optional.of(inputStream1)).when(helper).loadPath(path1);
        doReturn(Optional.of(inputStream2)).when(helper).loadPath(path2);
        doReturn(Optional.of(inputStream3)).when(helper).loadPath(path3);
        doReturn(inputStreamReader1).when(helper).toInputStreamReader(inputStream1);
        doReturn(inputStreamReader2).when(helper).toInputStreamReader(inputStream2);
        doReturn(inputStreamReader3).when(helper).toInputStreamReader(inputStream3);
        when(marshaller.unmarshal(inputStreamReader1)).thenReturn(definitions1);
        when(marshaller.unmarshal(inputStreamReader2)).thenReturn(definitions2);
        when(marshaller.unmarshal(inputStreamReader3)).thenReturn(definitions3);

        final List<Definitions> actualDefinitions = helper.getOtherDMNDiagramsDefinitions(metadata);
        final List<Definitions> expectedDefinitions = asList(definitions1, definitions3);

        assertEquals(expectedDefinitions, actualDefinitions);
    }

    @Test
    public void testLoadPath() {

        final Path path = mock(Path.class);
        final org.uberfire.java.nio.file.Path nioPath = mock(org.uberfire.java.nio.file.Path.class);
        final String expectedContent = "<dmn/>";
        final byte[] contentBytes = expectedContent.getBytes();

        doReturn(nioPath).when(helper).convertPath(path);
        when(ioService.newInputStream(nioPath)).thenReturn(new ByteArrayInputStream(contentBytes));

        final Optional<InputStream> inputStream = helper.loadPath(path);

        assertTrue(inputStream.isPresent());
        assertEquals(expectedContent, new Scanner(new InputStreamReader(inputStream.get())).next());
    }

    @Test
    public void testLoadPathWhenPathDoesNotExist() {

        final Path path = mock(Path.class);
        final org.uberfire.java.nio.file.Path nioPath = mock(org.uberfire.java.nio.file.Path.class);

        doReturn(nioPath).when(helper).convertPath(path);
        when(ioService.newInputStream(nioPath)).thenThrow(new IOException());

        final Optional<InputStream> inputStream = helper.loadPath(path);

        assertFalse(inputStream.isPresent());
    }

    @Test
    public void testGetImportedItemDefinitionsByNamespace() {

        final WorkspaceProject workspaceProject = mock(WorkspaceProject.class);
        final String modelName = "model1";
        final String namespace = "://namespace1";
        final Path path1 = makePath("../file1.dmn");
        final Path path2 = makePath("../file2.dmn");
        final Path path3 = makePath("../file3.dmn");
        final Path path4 = makePath("../file4.dmn");
        final InputStream inputStream1 = mock(InputStream.class);
        final InputStream inputStream2 = mock(InputStream.class);
        final InputStream inputStream3 = mock(InputStream.class);
        final InputStreamReader inputStreamReader1 = mock(InputStreamReader.class);
        final InputStreamReader inputStreamReader2 = mock(InputStreamReader.class);
        final InputStreamReader inputStreamReader3 = mock(InputStreamReader.class);
        final Definitions definitions1 = mock(Definitions.class);
        final Definitions definitions2 = mock(Definitions.class);
        final Definitions definitions3 = mock(Definitions.class);
        final ItemDefinition itemDefinition1 = mock(ItemDefinition.class);
        final ItemDefinition itemDefinition2 = mock(ItemDefinition.class);
        final List<Path> paths = asList(path1, path2, path3, path4);

        when(pathsHelper.getDMNModelsPaths(any())).thenReturn(paths);
        doReturn(Optional.of(inputStream1)).when(helper).loadPath(path1);
        doReturn(Optional.of(inputStream2)).when(helper).loadPath(path2);
        doReturn(Optional.of(inputStream3)).when(helper).loadPath(path3);
        doReturn(inputStreamReader1).when(helper).toInputStreamReader(inputStream1);
        doReturn(inputStreamReader2).when(helper).toInputStreamReader(inputStream2);
        doReturn(inputStreamReader3).when(helper).toInputStreamReader(inputStream3);
        doReturn(Optional.empty()).when(helper).loadPath(path4);
        when(marshaller.unmarshal(inputStreamReader1)).thenReturn(definitions1);
        when(marshaller.unmarshal(inputStreamReader2)).thenReturn(definitions2);
        when(marshaller.unmarshal(inputStreamReader3)).thenReturn(definitions3);
        when(definitions1.getNamespace()).thenReturn("://namespace1");
        when(definitions2.getNamespace()).thenReturn("://namespace2");
        when(definitions3.getNamespace()).thenReturn("://namespace3");
        when(definitions1.getItemDefinition()).thenReturn(asList(itemDefinition1, itemDefinition2));

        final List<ItemDefinition> actualItemDefinitions = helper.getImportedItemDefinitionsByNamespace(workspaceProject, modelName, namespace);
        final List<ItemDefinition> expectedItemDefinitions = asList(itemDefinition1, itemDefinition2);

        assertEquals(expectedItemDefinitions, actualItemDefinitions);
    }

    @Test
    public void testGetModelPath() {

        final Metadata metadata = mock(Metadata.class);
        final WorkspaceProject workspaceProject = mock(WorkspaceProject.class);
        final Path metadataPath = mock(Path.class);
        final Path path1 = mock(Path.class);
        final Path path2 = mock(Path.class);
        final Path path3 = mock(Path.class);
        final Definitions definitions1 = mock(Definitions.class);
        final Definitions definitions2 = mock(Definitions.class);
        final String modelNamespace = "0000-1111-2222-3333";
        final String modelName = "model name";

        doReturn(Optional.of(definitions1)).when(helper).getDefinitionsByPath(path1);
        doReturn(Optional.of(definitions2)).when(helper).getDefinitionsByPath(path2);
        doReturn(Optional.empty()).when(helper).getDefinitionsByPath(path3);
        when(definitions1.getNamespace()).thenReturn("0000-0000-0000-0000");
        when(definitions2.getNamespace()).thenReturn("0000-1111-2222-3333");
        when(definitions1.getName()).thenReturn("modll name");
        when(definitions2.getName()).thenReturn("model name");
        when(metadata.getPath()).thenReturn(metadataPath);
        when(projectService.resolveProject(metadataPath)).thenReturn(workspaceProject);
        when(pathsHelper.getDMNModelsPaths(workspaceProject)).thenReturn(asList(path1, path2, path3));

        final Path modelPath = helper.getDMNModelPath(metadata, modelNamespace, modelName);

        assertEquals(path2, modelPath);
    }

    @Test
    public void testGetModelPathWhenDMNModelCouldNotBeFound() {

        final Metadata metadata = mock(Metadata.class);
        final String modelNamespace = "0000-1111-2222-3333";
        final String modelName = "model name";

        assertThatThrownBy(() -> helper.getDMNModelPath(metadata, modelNamespace, modelName))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("No DMN model could be found for the following namespace: 0000-1111-2222-3333");
    }

    private Path makePath(final String uri) {

        final PathImpl path = spy(new PathImpl());

        doReturn(uri).when(path).toURI();

        return path;
    }
}
