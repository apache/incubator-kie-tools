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

package org.dashbuilder.transfer;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.enterprise.event.Event;

import org.dashbuilder.dataset.DataSetDefRegistryCDI;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.events.DataSetDefRegisteredEvent;
import org.dashbuilder.dataset.json.DataSetDefJSONMarshaller;
import org.dashbuilder.external.model.ExternalComponent;
import org.dashbuilder.external.service.ComponentLoader;
import org.dashbuilder.navigation.event.NavTreeChangedEvent;
import org.dashbuilder.navigation.storage.NavTreeStorage;
import org.dashbuilder.project.storage.ProjectStorageServices;
import org.dashbuilder.project.storage.impl.ProjectStorageServicesImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lesscss.deps.org.apache.commons.io.FileUtils;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.ext.plugin.event.PluginAdded;
import org.uberfire.rpc.SessionInfo;

import static org.dashbuilder.project.storage.ProjectStorageServices.NAV_TREE_FILE_NAME;
import static org.dashbuilder.project.storage.ProjectStorageServices.PERSPECTIVE_LAYOUT;
import static org.dashbuilder.project.storage.ProjectStorageServices.PERSPECTIVE_LAYOUT_PLUGIN;
import static org.dashbuilder.project.storage.ProjectStorageServices.README;
import static org.dashbuilder.project.storage.ProjectStorageServices.getDatasetsExportPath;
import static org.dashbuilder.project.storage.ProjectStorageServices.getNavigationExportPath;
import static org.dashbuilder.project.storage.ProjectStorageServices.getPerspectivesExportPath;
import static org.dashbuilder.transfer.DataTransferServices.COMPONENTS_EXPORT_PATH;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class DataTransferServicesTest {

    private DataTransferServicesImpl dataTransferServices;

    @Mock
    private DataSetDefRegistryCDI dataSetDefRegistryCDI;
    @Mock
    private Event<DataSetDefRegisteredEvent> dataSetDefRegisteredEvent;
    @Mock
    private Event<PluginAdded> pluginAddedEvent;
    @Mock
    SessionInfo sessionInfo;
    @Mock
    private Event<NavTreeChangedEvent> navTreeChangedEvent;
    @Mock
    private NavTreeStorage navTreeStorage;
    @Mock
    DataSetDefJSONMarshaller dataSetDefJSONMarshaller;
    @Mock
    ComponentLoader externalComponentLoader;
    @Mock
    LayoutComponentHelper layoutComponentsHelper;

    Path componentsDir;
    private ProjectStorageServices projectStorageServices;

    @Before
    public void setup() throws IOException {

        componentsDir = Files.createTempDirectory("dashbuilder-components");

        when(dataSetDefRegistryCDI.getDataSetDefJsonMarshaller()).thenReturn(dataSetDefJSONMarshaller);
        when(externalComponentLoader.getExternalComponentsDir()).thenReturn(componentsDir.toString());
        when(externalComponentLoader.isExternalComponentsEnabled()).thenReturn(true);

        projectStorageServices = new ProjectStorageServicesImpl();
        dataTransferServices = new DataTransferServicesImpl(projectStorageServices,
                dataSetDefRegistryCDI,
                sessionInfo,
                dataSetDefRegisteredEvent,
                pluginAddedEvent,
                navTreeChangedEvent,
                externalComponentLoader,
                layoutComponentsHelper);
        projectStorageServices.clear();
        projectStorageServices.createStructure();
    }

    @After
    public void cleanFileSystems() {
        projectStorageServices.clear();
        FileUtils.deleteQuietly(componentsDir.toFile());
    }
    
    @Test
    public void testDoExportEmptyFileSystems() throws Exception {
        dataTransferServices.doExport(DataTransferExportModel.exportAll());

        var exportPath = projectStorageServices.getTempPath(DataTransferServicesImpl.EXPORT_ZIP);

        assertTrue(exportPath.toFile().exists());

        var zis = new ZipInputStream(new FileInputStream(exportPath.toFile()));

        var expected = List.of(getDatasetsExportPath().getParent().resolve(README).toString(),
                getPerspectivesExportPath().resolve(README).toString(),
                getNavigationExportPath().getParent().resolve(README).toString(),
                DataTransferServicesImpl.VERSION_FILE);

        assertArrayEquals(expected.toArray(), getFiles(zis).toArray());
    }

    @Test
    public void testDoExportNotEmptyFileSystems() throws Exception {
        projectStorageServices.saveDataSetContent("dataset1.csv", "");
        projectStorageServices.saveDataSet("dataset1", "");
        projectStorageServices.savePerspective("page1", "");
        projectStorageServices.saveNavigation("");

        var exportPath = dataTransferServices.doExport(DataTransferExportModel.exportAll());
        var zis = new ZipInputStream(new FileInputStream(exportPath));

        String[] expected = {
                getDatasetsExportPath().resolve("dataset1.csv").toString(),
                getDatasetsExportPath().resolve("dataset1.dset").toString(),
                getDatasetsExportPath().getParent().resolve(README).toString(),

                getPerspectivesExportPath().resolve("page1").resolve(PERSPECTIVE_LAYOUT).toString(),
                getPerspectivesExportPath().resolve("page1").resolve(PERSPECTIVE_LAYOUT_PLUGIN).toString(),
                getPerspectivesExportPath().resolve(README).toString(),

                getNavigationExportPath().resolve(NAV_TREE_FILE_NAME).toString(),
                getNavigationExportPath().getParent().resolve(README).toString(),
                DataTransferServicesImpl.VERSION_FILE
        };
        assertArraysEquals(expected, getFiles(zis).toArray());
    }

    @Test
    public void testDoExportFilteringDataSets() throws Exception {
        projectStorageServices.saveDataSetContent("dataset1.csv", "");
        projectStorageServices.saveDataSet("dataset1", "");
        projectStorageServices.saveDataSet("dataset2", "");
        projectStorageServices.savePerspective("page1", "");
        projectStorageServices.saveNavigation("");

        var def = mock(DataSetDef.class);
        when(def.getUUID()).thenReturn("dataset2");
        var model = new DataTransferExportModel(List.of(def),
                List.of("page1", "page2"),
                true);

        var exportPath = dataTransferServices.doExport(model);
        var zis = new ZipInputStream(new FileInputStream(exportPath));
        String[] expected = {
                getDatasetsExportPath().resolve("dataset2.dset").toString(),
                getDatasetsExportPath().getParent().resolve(README).toString(),

                getPerspectivesExportPath().resolve("page1").resolve(PERSPECTIVE_LAYOUT).toString(),
                getPerspectivesExportPath().resolve("page1").resolve(PERSPECTIVE_LAYOUT_PLUGIN).toString(),
                getPerspectivesExportPath().resolve(README).toString(),

                getNavigationExportPath().resolve(NAV_TREE_FILE_NAME).toString(),
                getNavigationExportPath().getParent().resolve(README).toString(),
                DataTransferServicesImpl.VERSION_FILE
                };
        assertArraysEquals(expected, getFiles(zis).toArray());
    }

    @Test
    public void testDoExportFilteringPages() throws Exception {
        projectStorageServices.saveDataSetContent("dataset.csv", "");
        projectStorageServices.saveDataSet("dataset", "");
        projectStorageServices.saveDataSet("dataset", "");
        projectStorageServices.savePerspective("page1", "");
        projectStorageServices.savePerspective("page2", "");
        projectStorageServices.saveNavigation("");

        var def = mock(DataSetDef.class);
        when(def.getUUID()).thenReturn("dataset");
        var model = new DataTransferExportModel(List.of(def),
                List.of("page2"),
                true);

        var exportPath = dataTransferServices.doExport(model);

        var zis = new ZipInputStream(new FileInputStream(exportPath));
        String[] expected = {
                getDatasetsExportPath().resolve("dataset.csv").toString(),
                getDatasetsExportPath().resolve("dataset.dset").toString(),
                getDatasetsExportPath().getParent().resolve(README).toString(),

                getPerspectivesExportPath().resolve("page2").resolve(PERSPECTIVE_LAYOUT).toString(),
                getPerspectivesExportPath().resolve("page2").resolve(PERSPECTIVE_LAYOUT_PLUGIN).toString(),
                getPerspectivesExportPath().resolve(README).toString(),

                getNavigationExportPath().resolve(NAV_TREE_FILE_NAME).toString(),
                getNavigationExportPath().getParent().resolve(README).toString(),
                DataTransferServicesImpl.VERSION_FILE};
        var files = getFiles(zis).toArray();
        assertArraysEquals(expected, files);
    }

    @Test
    public void testDoExportWithoutNavigation() throws Exception {
        projectStorageServices.saveDataSetContent("dataset.csv", "");
        projectStorageServices.saveDataSet("dataset", "");
        projectStorageServices.saveDataSet("dataset", "");
        projectStorageServices.savePerspective("page1", "");
        projectStorageServices.saveNavigation("");

        var def = mock(DataSetDef.class);
        when(def.getUUID()).thenReturn("dataset");
        var model = new DataTransferExportModel(List.of(def),
                List.of("page1"),
                false);

        var exportPath = dataTransferServices.doExport(model);

        var zis = new ZipInputStream(new FileInputStream(exportPath));
        String[] expected = {
                getDatasetsExportPath().resolve("dataset.csv").toString(),
                getDatasetsExportPath().resolve("dataset.dset").toString(),
                getDatasetsExportPath().getParent().resolve(README).toString(),

                getPerspectivesExportPath().resolve("page1").resolve(PERSPECTIVE_LAYOUT).toString(),
                getPerspectivesExportPath().resolve("page1").resolve(PERSPECTIVE_LAYOUT_PLUGIN).toString(),
                getPerspectivesExportPath().resolve(README).toString(),

                getNavigationExportPath().getParent().resolve(README).toString(),
                DataTransferServicesImpl.VERSION_FILE
        };
        assertArraysEquals(expected, getFiles(zis).toArray());
    }

    @Test
    public void testDoExportWithComponents() throws Exception {
        when(layoutComponentsHelper.findComponentsInTemplates((any()))).thenReturn(List.of("c1"));

        projectStorageServices.savePerspective("page1", "");

        createComponentFile("c1", "manifest.json", "manifest");
        createComponentFile("c1", "index.html", "html");
        createComponentFile("c1", "css/style.css", "style");
        createComponentFile("c1", "js/index.js", "js");

        // lost file in component Dir that should be ignored
        createComponentFile("lost", "lostfile", "ignore-me-import");

        // Other component that is not used so it should not be exported
        createComponentFile("c2", "manifest.json", "manifest");
        createComponentFile("c2", "index.html", "html");
        createComponentFile("c2", "css/style.css", "style");
        createComponentFile("c2", "js/index.js", "js");

        var exportPath = dataTransferServices.doExport(DataTransferExportModel.exportAll());

        var zis = new ZipInputStream(new FileInputStream(exportPath));

        String[] expectedFiles = {
                                  getDatasetsExportPath().getParent().resolve(README).toString(),
                                  getPerspectivesExportPath().resolve("page1").resolve(PERSPECTIVE_LAYOUT).toString(),
                                  getPerspectivesExportPath().resolve("page1").resolve(PERSPECTIVE_LAYOUT_PLUGIN)
                                          .toString(),
                                  getPerspectivesExportPath().resolve(README).toString(),
                                  getNavigationExportPath().getParent().resolve(README).toString(),
                                  COMPONENTS_EXPORT_PATH + "c1/js/index.js",
                                  COMPONENTS_EXPORT_PATH + "c1/css/style.css",
                                  COMPONENTS_EXPORT_PATH + "c1/index.html",
                                  COMPONENTS_EXPORT_PATH + "c1/manifest.json",
                                  DataTransferServicesImpl.VERSION_FILE
        };
        assertArraysEquals(expectedFiles, getFiles(zis).toArray());
    }

    @Test
    public void testDoExportIgnoringComponents() throws Exception {
        when(externalComponentLoader.isExternalComponentsEnabled()).thenReturn(false);
        when(externalComponentLoader.loadExternal()).thenReturn(List.of(component("c1")));

        projectStorageServices.savePerspective("page1", "");

        createComponentFile("c1", "manifest.json", "manifest");
        createComponentFile("c1", "index.html", "html");
        createComponentFile("c1", "css/style.css", "style");
        createComponentFile("c1", "js/index.js", "js");

        var exportPath = dataTransferServices.doExport(DataTransferExportModel.exportAll());
        var zis = new ZipInputStream(new FileInputStream(exportPath));

        String[] expected = {
                             getDatasetsExportPath().getParent().resolve(README).toString(),
                             getPerspectivesExportPath().resolve("page1").resolve(PERSPECTIVE_LAYOUT).toString(),
                             getPerspectivesExportPath().resolve("page1").resolve(PERSPECTIVE_LAYOUT_PLUGIN)
                                     .toString(),
                             getPerspectivesExportPath().resolve(README).toString(),
                             getNavigationExportPath().getParent().resolve(README).toString(),
                             DataTransferServicesImpl.VERSION_FILE

        };
        var actual = getFiles(zis).toArray();
        assertArraysEquals(expected, actual);
    }

    @Test
    public void testDoExportWhenComponentsDirIsNotPresent() throws Exception {
        projectStorageServices.savePerspective("page1", "");

        FileUtils.deleteQuietly(componentsDir.toFile());

        var exportPath = dataTransferServices.doExport(DataTransferExportModel.exportAll());
        var zis = new ZipInputStream(new FileInputStream(exportPath));

        String[] expected = {
                             getDatasetsExportPath().getParent().resolve(README).toString(),
                             getPerspectivesExportPath().resolve("page1").resolve(PERSPECTIVE_LAYOUT).toString(),
                             getPerspectivesExportPath().resolve("page1").resolve(PERSPECTIVE_LAYOUT_PLUGIN)
                                     .toString(),
                             getPerspectivesExportPath().resolve(README).toString(),
                             getNavigationExportPath().getParent().resolve(README).toString(),
                             DataTransferServicesImpl.VERSION_FILE
        };

        assertArraysEquals(expected, getFiles(zis).toArray());
    }

    @Test
    public void testDoImportEmptyZip() throws Exception {
        var importZip = projectStorageServices.createTempPath(DataTransferServices.IMPORT_FILE_NAME);

        Files.copy(getClass().getResource("/empty.zip").openStream(), importZip, StandardCopyOption.REPLACE_EXISTING);

        var filesImported = dataTransferServices.doImport();

        assertTrue(filesImported.isEmpty());

        assertTrue(projectStorageServices.listAllDataSetsContent().isEmpty());
        assertTrue(projectStorageServices.listPerspectivesPlugins().isEmpty());
        assertTrue(projectStorageServices.getNavigation().isEmpty());
        verify(dataSetDefRegisteredEvent, times(0)).fire(any());
        verify(pluginAddedEvent, times(0)).fire(any());
        verify(navTreeChangedEvent, times(0)).fire(any());
    }

    @Test
    public void testDoImportNotEmptyZip() throws Exception {
        var importZip = projectStorageServices.createTempPath(DataTransferServices.IMPORT_FILE_NAME);
        Files.copy(getClass().getResource("/import.zip").openStream(), importZip, StandardCopyOption.REPLACE_EXISTING);

        var filesImported = dataTransferServices.doImport();
        String[] expected =
                {
                 "dashbuilder/datasets/definitions/eb241039-1792-4d08-9596-b6c8d27dfe6b.csv",
                 "dashbuilder/datasets/definitions/d1b24449-fe90-40d4-8cd7-f175b498c0bb.dset",
                 "dashbuilder/datasets/definitions/8060a7f1-ef03-4ce9-a0a8-266301e79ff6.dset",
                 "dashbuilder/datasets/definitions/eb241039-1792-4d08-9596-b6c8d27dfe6b.dset",
                 "dashbuilder/datasets/definitions/7e68d20d-6807-4b86-8737-1d429afe9dbc.csv",
                 "dashbuilder/datasets/definitions/d1b24449-fe90-40d4-8cd7-f175b498c0bb.csv",
                 "dashbuilder/datasets/definitions/8060a7f1-ef03-4ce9-a0a8-266301e79ff6.csv",
                 "dashbuilder/datasets/definitions/7e68d20d-6807-4b86-8737-1d429afe9dbc.dset",
                 "dashbuilder/components/c2/manifest.json",
                 "dashbuilder/components/c2/styles/level/styles.css",
                 "dashbuilder/components/c2/index.html",
                 "dashbuilder/components/c1/manifest.json",
                 "dashbuilder/components/c1/images/db_logo.png",
                 "dashbuilder/components/c1/scripts/index.js",
                 "dashbuilder/components/c1/index.html",
                 "dashbuilder/perspectives/page3/perspective_layout",
                 "dashbuilder/perspectives/page4/perspective_layout",
                 "dashbuilder/perspectives/page2/perspective_layout",
                 "dashbuilder/perspectives/page1/perspective_layout",
                 "dashbuilder/navigation/navigation/navtree.json"

                };

        assertArrayEquals(expected, filesImported.toArray());

        assertTrue(projectStorageServices.getDataSet("7e68d20d-6807-4b86-8737-1d429afe9dbc").isPresent());
        assertTrue(projectStorageServices.getDataSet("8060a7f1-ef03-4ce9-a0a8-266301e79ff6").isPresent());
        assertTrue(projectStorageServices.getDataSet("d1b24449-fe90-40d4-8cd7-f175b498c0bb").isPresent());
        assertTrue(projectStorageServices.getDataSet("eb241039-1792-4d08-9596-b6c8d27dfe6b").isPresent());

        assertTrue(projectStorageServices.getDataSetContent("7e68d20d-6807-4b86-8737-1d429afe9dbc.csv").isPresent());
        assertTrue(projectStorageServices.getDataSetContent("8060a7f1-ef03-4ce9-a0a8-266301e79ff6.csv").isPresent());
        assertTrue(projectStorageServices.getDataSetContent("d1b24449-fe90-40d4-8cd7-f175b498c0bb.csv").isPresent());
        assertTrue(projectStorageServices.getDataSetContent("eb241039-1792-4d08-9596-b6c8d27dfe6b.csv").isPresent());

        assertTrue(projectStorageServices.getPerspective("page1").isPresent());
        assertTrue(projectStorageServices.getPerspective("page2").isPresent());
        assertTrue(projectStorageServices.getPerspective("page3").isPresent());
        assertTrue(projectStorageServices.getPerspective("page4").isPresent());

        assertTrue(projectStorageServices.getNavigation().isPresent());

        String[] expectedComponentsFiles = {"c1/index.html",
                                            "c1/scripts/index.js",
                                            "c1/images/db_logo.png",
                                            "c1/manifest.json",
                                            "c2/index.html",
                                            "c2/styles/level/styles.css",
                                            "c2/manifest.json"};
        var allComponentsFiles = getComponentsFiles(componentsDir).toArray();

        assertArraysEquals(expectedComponentsFiles, allComponentsFiles);

        verify(dataSetDefRegisteredEvent, times(4)).fire(any());
        verify(pluginAddedEvent, times(4)).fire(any());
        verify(navTreeChangedEvent, times(1)).fire(any());
    }

    @Test
    public void testAssetsToImport() throws Exception {
        final String PAGE_ID = "page";
        final String DS_CSV = "ds.csv";
        final String DS_CONTENT = "TEST_CONTENT";
        final String DS_NAME = "test_dataset";

        var dataSetDef = mock(DataSetDef.class);
        when(dataSetDef.isPublic()).thenReturn(true);
        when(dataSetDef.getName()).thenReturn(DS_NAME);
        when(dataSetDefRegistryCDI.getDataSetDefJsonMarshaller()).thenReturn(dataSetDefJSONMarshaller);
        when(dataSetDefJSONMarshaller.fromJson(DS_CONTENT)).thenReturn(dataSetDef);

        projectStorageServices.saveDataSet(DS_NAME, DS_CONTENT);
        projectStorageServices.saveDataSetContent(DS_CSV, "");
        projectStorageServices.savePerspective(PAGE_ID, "");

        var exportInfo = dataTransferServices.exportInfo();

        assertEquals(1, exportInfo.getDatasetsDefinitions().size());
        assertEquals(DS_NAME, exportInfo.getDatasetsDefinitions().get(0).getName());
        assertEquals(1, exportInfo.getPages().size());
        assertEquals(PAGE_ID, exportInfo.getPages().get(0));
    }

    @Test
    public void testAssetsToImportNoFiles() {
        ExportInfo assetsToExport = dataTransferServices.exportInfo();
        assertTrue(assetsToExport.getDatasetsDefinitions().isEmpty());
        assertTrue(assetsToExport.getPages().isEmpty());
    }

    @Test
    public void testShouldNotExposePrivateDS() throws Exception {
        final String DS_CONTENT = "TEST_CONTENT";
        final String DS_NAME = "test_dataset";

        var dataSetDef = mock(DataSetDef.class);
        when(dataSetDef.isPublic()).thenReturn(false);
        when(dataSetDef.getName()).thenReturn(DS_NAME);
        when(dataSetDefJSONMarshaller.fromJson(DS_CONTENT)).thenReturn(dataSetDef);

        projectStorageServices.saveDataSetContent(DS_NAME, DS_CONTENT);

        var assetsToExport = dataTransferServices.exportInfo();

        assertTrue(assetsToExport.getDatasetsDefinitions().isEmpty());
    }

    private Path createComponentFile(String componentId, String filename, String data) throws IOException {
        Path componentPath = componentsDir.resolve(componentId);
        Path componentFile = componentPath.resolve(filename);
        componentFile.getParent().toFile().mkdirs();
        Files.write(componentFile, data.getBytes());
        return componentFile;
    }

    private List<String> getComponentsFiles(Path root) throws IOException {
        return Files.walk(root)
                .filter(p -> p.toFile().isFile())
                .map(p -> p.toString().replaceAll(componentsDir.toString() + "/", ""))
                .collect(Collectors.toList());

    }

    private List<String> getFiles(ZipInputStream zis) {
        List<String> files = new ArrayList<>();
        ZipEntry entry = null;

        try {
            while ((entry = zis.getNextEntry()) != null) {
                files.add(entry.getName());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return files;
    }

    private ExternalComponent component(String id) {
        return new ExternalComponent(id, id, "", false, Collections.emptyList());
    }
    
    private void assertArraysEquals(String[] expected, Object[] actual) {
        Arrays.sort(expected);
        Arrays.sort(actual);
        assertArrayEquals(expected, actual);
    }


}
