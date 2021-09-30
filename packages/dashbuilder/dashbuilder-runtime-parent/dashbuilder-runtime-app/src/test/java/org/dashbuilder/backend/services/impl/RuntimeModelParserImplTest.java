/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dashbuilder.backend.services.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.enterprise.event.Event;

import org.dashbuilder.backend.RuntimeOptions;
import org.dashbuilder.backend.helper.PartitionHelper;
import org.dashbuilder.backend.navigation.RuntimeNavigationBuilder;
import org.dashbuilder.displayer.json.DisplayerSettingsJSONMarshaller;
import org.dashbuilder.external.service.ComponentLoader;
import org.dashbuilder.navigation.impl.NavTreeBuilder;
import org.dashbuilder.shared.event.NewDataSetContentEvent;
import org.dashbuilder.shared.model.DataSetContent;
import org.dashbuilder.shared.service.RuntimeModelRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.dashbuilder.shared.model.DataSetContentType.CSV;
import static org.dashbuilder.shared.model.DataSetContentType.DEFINITION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RuntimeModelParserImplTest {

    @Mock
    Event<NewDataSetContentEvent> newDataSetContentEventSource;

    @Mock
    RuntimeNavigationBuilder navigationBuilder;

    @Mock
    RuntimeOptions runtimeOptions;

    @Mock
    RuntimeModelRegistry runtimeModelRegistry;

    @Mock
    ComponentLoader externalComponentLoader;

    @InjectMocks
    RuntimeModelParserImpl parser;

    @Test
    public void testEmptyImport() throws IOException {
        when(navigationBuilder.build(any(), any())).thenReturn(new NavTreeBuilder().build());
        var emptyImport = this.getClass().getResourceAsStream("/empty.zip");
        var runtimeModel = parser.retrieveRuntimeModel("", emptyImport);

        verify(newDataSetContentEventSource, times(0)).fire(any());
        assertTrue(runtimeModel.getLayoutTemplates().isEmpty());
        assertTrue(runtimeModel.getNavTree().getRootItems().isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testValidImport() throws IOException {
        parser.init();
        var validImport = this.getClass().getResourceAsStream("/valid_import.zip");
        var runtimeModel = parser.parse("", validImport);

        @SuppressWarnings("rawtypes")
        var navigationContent = ArgumentCaptor.forClass(Optional.class);
        verify(navigationBuilder).build(navigationContent.capture(), any());
        assertEquals("{}", navigationContent.getValue().get());

        var layoutTemplates = runtimeModel.getLayoutTemplates();
        assertEquals(1, layoutTemplates.size());

        var layoutTemplate = layoutTemplates.get(0);
        assertEquals("life_expectancy", layoutTemplate.getName());
        assertEquals(3, layoutTemplate.getVersion());

        var datasetContents = ArgumentCaptor.forClass(NewDataSetContentEvent.class);
        verify(newDataSetContentEventSource).fire(datasetContents.capture());

        var newDataSetContentEvent = datasetContents.getValue();
        assertEquals(2, newDataSetContentEvent.getContent().size());

        var datasets = newDataSetContentEvent.getContent();
        assertEquals(2, datasets.size());
        assertEquals("e26a81a1-5636-493c-96e0-51bc32322b17", datasets.get(0).getId());
        assertEquals("e26a81a1-5636-493c-96e0-51bc32322b17", datasets.get(1).getId());
        Predicate<DataSetContent> csvMatcher = ds -> ds.getContentType().equals(CSV);
        Predicate<DataSetContent> defMatcher = ds -> ds.getContentType().equals(DEFINITION);
        assertTrue(datasets.stream().anyMatch(csvMatcher));
        assertTrue(datasets.stream().anyMatch(defMatcher));
        var dsContent = datasets.stream().filter(defMatcher).findAny().get().getContent();
        var csvContent = datasets.stream().filter(csvMatcher).findAny().get().getContent();

        assertEquals(getFileContent("/ds.dset"), dsContent);
        assertEquals(getFileContent("/ds.csv"), csvContent);
    }

    @Test
    public void testTransformedUuid() {
        when(runtimeOptions.isMultipleImport()).thenReturn(true);
        when(runtimeOptions.isDatasetPartition()).thenReturn(true);
        final String runtimeModelId = "123";
        final String transformedId = PartitionHelper.partition(runtimeModelId, "e26a81a1-5636-493c-96e0-51bc32322b17");

        parser.init();
        var validImport = this.getClass().getResourceAsStream("/valid_import.zip");
        var runtimeModel = parser.parse(runtimeModelId, validImport);

        var layoutTemplates = runtimeModel.getLayoutTemplates();

        // check if ID in the layout template component was transformed
        var layoutTemplate = layoutTemplates.get(0);
        var json = layoutTemplate.getRows().get(1).getLayoutColumns().get(0).getLayoutComponents().get(0).getProperties().get("json");
        var loadedUUID = DisplayerSettingsJSONMarshaller.get().fromJsonString(json).getDataSetLookup().getDataSetUUID();
        assertEquals(transformedId, loadedUUID);

        // check if dataset ID was changed
        var datasetContents = ArgumentCaptor.forClass(NewDataSetContentEvent.class);
        verify(newDataSetContentEventSource).fire(datasetContents.capture());
        var newDataSetContentEvent = datasetContents.getValue();
        var datasets = newDataSetContentEvent.getContent();

        assertEquals(transformedId, datasets.get(0).getId());
        assertEquals(transformedId, datasets.get(1).getId());

    }

    @Test
    public void testUnzipComponentFile() throws IOException, URISyntaxException {
        Path componentPath = Paths.get(this.getClass().getResource("/").toURI());
        when(externalComponentLoader.getExternalComponentsDir()).thenReturn(componentPath.toString());

        var componentFileContent1 = "This is a component file.";
        var fileName = "component1/someFile.txt";

        var zis = new ByteArrayInputStream(componentFileContent1.getBytes());

        parser.extractComponentFile("test", zis, fileName);

        var componentFilePath = componentPath.resolve(fileName);
        assertTrue(Files.exists(componentFilePath));
        assertEquals(componentFileContent1, Files.readAllLines(componentFilePath).get(0));
        Files.delete(componentFilePath);
    }

    @Test
    public void testUnzipComponentFileWithPartition() throws IOException, URISyntaxException {
        var componentPath = Paths.get(this.getClass().getResource("/").toURI());

        when(externalComponentLoader.getExternalComponentsDir()).thenReturn(componentPath.toString());
        when(runtimeOptions.isComponentPartition()).thenReturn(true);
        when(runtimeOptions.isMultipleImport()).thenReturn(true);

        var modelId = "testModel";
        var componentFileContent1 = "This is a component file.";
        var fileName = "component1/someFile.txt";

        var zis = new ByteArrayInputStream(componentFileContent1.getBytes());

        parser.extractComponentFile(modelId, zis, fileName);

        var componentFilePath = Paths.get(componentPath.toString(), modelId, fileName);

        assertTrue(Files.exists(componentFilePath));
        assertEquals(componentFileContent1, Files.readAllLines(componentFilePath).get(0));
        Files.delete(componentFilePath);
    }

    private String getFileContent(String resource) throws IOException {
        var csvActualPath = Paths.get(this.getClass()
                                          .getResource(resource)
                                          .getFile());
        return Files.readAllLines(csvActualPath)
                    .stream()
                    .collect(Collectors.joining(System.lineSeparator()));
    }

}