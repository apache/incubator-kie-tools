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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.enterprise.event.Event;

import org.dashbuilder.dataset.DataSetDefRegistryCDI;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.events.DataSetDefRegisteredEvent;
import org.dashbuilder.dataset.json.DataSetDefJSONMarshaller;
import org.dashbuilder.external.model.ExternalComponent;
import org.dashbuilder.external.service.ExternalComponentLoader;
import org.dashbuilder.navigation.event.NavTreeChangedEvent;
import org.dashbuilder.navigation.storage.NavTreeStorage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lesscss.deps.org.apache.commons.io.FileUtils;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.plugin.event.PluginAdded;
import org.uberfire.io.IOService;
import org.uberfire.io.impl.IOServiceDotFileImpl;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileVisitResult;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;
import org.uberfire.java.nio.file.SimpleFileVisitor;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.spaces.SpacesAPI;

import static java.util.Arrays.asList;
import static org.dashbuilder.transfer.DataTransferServices.COMPONENTS_EXPORT_PATH;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DataTransferServicesTest {

    private IOService ioService;
    private FileSystem datasetsFS;
    private FileSystem perspectivesFS;
    private FileSystem navigationFS;
    private FileSystem systemFS;
    private DataTransferServices dataTransferServices;

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
    ExternalComponentLoader externalComponentLoader;

    Path componentsDir;
    
    @Before
    public void setup() {
        ioService = new IOServiceDotFileImpl();
        componentsDir = Files.createTempDirectory("dashbuilder-components");

        datasetsFS = createFileSystem("datasets");
        perspectivesFS = createFileSystem("perspectives");
        navigationFS = createFileSystem("navigation");
        systemFS = createFileSystem("system");

        when(dataSetDefRegistryCDI.getDataSetDefJsonMarshaller()).thenReturn(dataSetDefJSONMarshaller);
        when(externalComponentLoader.getExternalComponentsDir()).thenReturn(componentsDir.toString());
        when(externalComponentLoader.isEnabled()).thenReturn(true);

        dataTransferServices = new DataTransferServicesImpl(ioService,
                                                            datasetsFS,
                                                            perspectivesFS,
                                                            navigationFS,
                                                            systemFS,
                                                            dataSetDefRegistryCDI,
                                                            sessionInfo,
                                                            dataSetDefRegisteredEvent,
                                                            pluginAddedEvent,
                                                            navTreeChangedEvent,
                                                            navTreeStorage,
                                                            externalComponentLoader);
    }

    @After
    public void cleanFileSystems() {
        cleanFileSystem(datasetsFS);
        cleanFileSystem(perspectivesFS);
        cleanFileSystem(navigationFS);
        cleanFileSystem(systemFS);
        
        FileUtils.deleteQuietly(componentsDir.toFile());
    }

    @Test
    @SuppressWarnings("serial")
    public void testDoExportEmptyFileSystems() throws Exception {
        String exportPath = dataTransferServices.doExport(DataTransferExportModel.exportAll());

        assertTrue(exportPath.equals(getExpectedExportFileSystemPath()));

        assertEquals(new ArrayList<String>() {{
                        add(getExpectedExportFilePath());
                        add("/readme.md");
                    }}, getFiles(systemFS));

        ZipInputStream zis = getZipInputStream();

        assertEquals(new ArrayList<String>() {{
                        add(datasetsFS.getName() + "/readme.md");
                        add(perspectivesFS.getName() + "/readme.md");
                        add(navigationFS.getName() + "/readme.md");
                        add("VERSION");
                    }}, getFiles(zis));
    }

    @Test
    @SuppressWarnings("serial")
    public void testDoExportNotEmptyFileSystems() throws Exception {
        createFile(datasetsFS, "definitions/dataset1.csv", "Test 1");
        createFile(datasetsFS, "definitions/dataset1.dset", "Test ABC");
        createFile(perspectivesFS, "page1/perspective_layout", "Test Page 1");
        createFile(perspectivesFS, "page1/perspective_layout.plugin", "Test Page 1 Plugin");
        createFile(navigationFS, "navtree.json", "{ }");

        String exportPath = dataTransferServices.doExport(DataTransferExportModel.exportAll());

        assertTrue(exportPath.equals(getExpectedExportFileSystemPath()));

        assertEquals(new ArrayList<String>() {{
                        add(getExpectedExportFilePath());
                        add("/readme.md");
                    }}, getFiles(systemFS));

        ZipInputStream zis = getZipInputStream();

        assertEquals(new ArrayList<String>() {{
                        add(datasetsFS.getName() + "/definitions/dataset1.csv");
                        add(datasetsFS.getName() + "/definitions/dataset1.dset");
                        add(datasetsFS.getName() + "/readme.md");
                        add(perspectivesFS.getName() + "/page1/perspective_layout");
                        add(perspectivesFS.getName() + "/page1/perspective_layout.plugin");
                        add(perspectivesFS.getName() + "/readme.md");
                        add(navigationFS.getName() + "/navtree.json");
                        add(navigationFS.getName() + "/readme.md");
                        add("VERSION");
                    }}, getFiles(zis));
    }
    
    @Test
    @SuppressWarnings("serial")
    public void testDoExportFilteringDataSets() throws Exception {
        createFile(datasetsFS, "definitions/dataset1.csv", "");
        createFile(datasetsFS, "definitions/dataset1.dset", "");
        createFile(datasetsFS, "definitions/dataset2.dset", "");
        createFile(perspectivesFS, "page1/perspective_layout", "");
        createFile(perspectivesFS, "page1/perspective_layout.plugin", "");
        createFile(navigationFS, "navtree.json", "");
        
        DataSetDef def = mock(DataSetDef.class);
        when(def.getUUID()).thenReturn("dataset1");
        DataTransferExportModel model = new DataTransferExportModel(Arrays.asList(def), 
                                                                    Arrays.asList("page1", "page2"), 
                                                                    true);

        String exportPath = dataTransferServices.doExport(model);

        assertTrue(exportPath.equals(getExpectedExportFileSystemPath()));

        ZipInputStream zis = getZipInputStream();

        assertEquals(new ArrayList<String>() {{
                        add(datasetsFS.getName() + "/definitions/dataset1.csv");
                        add(datasetsFS.getName() + "/definitions/dataset1.dset");
                        add(datasetsFS.getName() + "/readme.md");
                        add(perspectivesFS.getName() + "/page1/perspective_layout");
                        add(perspectivesFS.getName() + "/page1/perspective_layout.plugin");
                        add(perspectivesFS.getName() + "/readme.md");
                        add(navigationFS.getName() + "/navtree.json");
                        add(navigationFS.getName() + "/readme.md");
                        add("VERSION");
                    }}, getFiles(zis));
        cleanFileSystems();
    }
    
    @Test
    @SuppressWarnings("serial")
    public void testDoExportFilteringPages() throws Exception {
        createFile(datasetsFS, "definitions/dataset.csv", "");
        createFile(datasetsFS, "definitions/dataset.dset", "");
        createFile(perspectivesFS, "page1/perspective_layout", "");
        createFile(perspectivesFS, "page1/perspective_layout.plugin", "");
        createFile(perspectivesFS, "page2/perspective_layout", "");
        createFile(perspectivesFS, "page2/perspective_layout.plugin", "");
        createFile(navigationFS, "navtree.json", "");
        
        DataSetDef def = mock(DataSetDef.class);
        when(def.getUUID()).thenReturn("dataset");
        DataTransferExportModel model = new DataTransferExportModel(Arrays.asList(def), 
                                                                    Arrays.asList("page2"), 
                                                                    true);

        String exportPath = dataTransferServices.doExport(model);

        assertTrue(exportPath.equals(getExpectedExportFileSystemPath()));

        ZipInputStream zis = getZipInputStream();

        assertEquals(new ArrayList<String>() {{
                        add(datasetsFS.getName() + "/definitions/dataset.csv");
                        add(datasetsFS.getName() + "/definitions/dataset.dset");
                        add(datasetsFS.getName() + "/readme.md");
                        add(perspectivesFS.getName() + "/page2/perspective_layout");
                        add(perspectivesFS.getName() + "/page2/perspective_layout.plugin");
                        add(perspectivesFS.getName() + "/readme.md");
                        add(navigationFS.getName() + "/navtree.json");
                        add(navigationFS.getName() + "/readme.md");
                        add("VERSION");
                    }}, getFiles(zis));
        cleanFileSystems();
    }
    
    @Test
    @SuppressWarnings("serial")
    public void testDoExportWithoutNavigation() throws Exception {
        createFile(datasetsFS, "definitions/dataset.csv", "");
        createFile(datasetsFS, "definitions/dataset.dset", "");
        createFile(perspectivesFS, "page1/perspective_layout", "");
        createFile(perspectivesFS, "page1/perspective_layout.plugin", "");
        createFile(navigationFS, "navtree.json", "");
        
        DataSetDef def = mock(DataSetDef.class);
        when(def.getUUID()).thenReturn("dataset");
        DataTransferExportModel model = new DataTransferExportModel(Arrays.asList(def), 
                                                                    Arrays.asList("page1"), 
                                                                    false);

        String exportPath = dataTransferServices.doExport(model);

        assertTrue(exportPath.equals(getExpectedExportFileSystemPath()));

        ZipInputStream zis = getZipInputStream();

        assertEquals(new ArrayList<String>() {{
                        add(datasetsFS.getName() + "/definitions/dataset.csv");
                        add(datasetsFS.getName() + "/definitions/dataset.dset");
                        add(datasetsFS.getName() + "/readme.md");
                        add(perspectivesFS.getName() + "/page1/perspective_layout");
                        add(perspectivesFS.getName() + "/page1/perspective_layout.plugin");
                        add(perspectivesFS.getName() + "/readme.md");
                        add(navigationFS.getName() + "/readme.md");
                        add("VERSION");
                    }}, getFiles(zis));
        cleanFileSystems();
    }
    
    @Test
    public void testDoExportWithComponents() throws Exception {
        when(externalComponentLoader.load()).thenReturn(asList(component("c1")));

        createFile(perspectivesFS, "page1/perspective_layout", "");
        createFile(perspectivesFS, "page1/perspective_layout.plugin", "");
        
        createComponentFile("c1", "manifest.json", "manifest");
        createComponentFile("c1", "index.html", "html");
        createComponentFile("c1", "css/style.css", "style");
        createComponentFile("c1", "js/index.js", "js");
        
        // lost file in component Dir that should be ignored
        createComponentFile("lost", "lostfile", "ignore-me-import");
        
        dataTransferServices.doExport(DataTransferExportModel.exportAll());

        ZipInputStream zis = getZipInputStream();

        String[] expectedFiles = {
                                  datasetsFS.getName() + "/readme.md",
                                  perspectivesFS.getName() + "/page1/perspective_layout",
                                  perspectivesFS.getName() + "/page1/perspective_layout.plugin",
                                  perspectivesFS.getName() + "/readme.md",
                                  navigationFS.getName() + "/readme.md",
                                  COMPONENTS_EXPORT_PATH + "c1/js/index.js",
                                  COMPONENTS_EXPORT_PATH + "c1/css/style.css",
                                  COMPONENTS_EXPORT_PATH + "c1/index.html",
                                  COMPONENTS_EXPORT_PATH + "c1/manifest.json",
                                  "VERSION"
                              };
        Object[] actualList = getFiles(zis).toArray();
        Arrays.sort(expectedFiles);
        Arrays.sort(actualList);
        assertArrayEquals(expectedFiles, actualList);
        
        cleanFileSystems();
        
    }
    
    @Test
    @SuppressWarnings("serial")
    public void testDoExportIgnoringComponents() throws Exception {
        when(externalComponentLoader.isEnabled()).thenReturn(false);
        when(externalComponentLoader.load()).thenReturn(asList(component("c1")));
        
        createFile(perspectivesFS, "page1/perspective_layout", "");
        createFile(perspectivesFS, "page1/perspective_layout.plugin", "");
        
        createComponentFile("c1", "manifest.json", "manifest");
        createComponentFile("c1", "index.html", "html");
        createComponentFile("c1", "css/style.css", "style");
        createComponentFile("c1", "js/index.js", "js");
                
        dataTransferServices.doExport(DataTransferExportModel.exportAll());

        ZipInputStream zis = getZipInputStream();

        assertEquals(new ArrayList<String>() {{
                        add(datasetsFS.getName() + "/readme.md");
                        add(perspectivesFS.getName() + "/page1/perspective_layout");
                        add(perspectivesFS.getName() + "/page1/perspective_layout.plugin");
                        add(perspectivesFS.getName() + "/readme.md");
                        add(navigationFS.getName() + "/readme.md");
                        add("VERSION");
                    }}, getFiles(zis));
        
        cleanFileSystems();
        
    }
    
    @Test
    @SuppressWarnings("serial")
    public void testDoExportWhenComponentsDirIsNotPresent() throws Exception {
        createFile(perspectivesFS, "page1/perspective_layout", "");
        createFile(perspectivesFS, "page1/perspective_layout.plugin", "");

        FileUtils.deleteQuietly(componentsDir.toFile());
        
        dataTransferServices.doExport(DataTransferExportModel.exportAll());

        ZipInputStream zis = getZipInputStream();

        assertEquals(new ArrayList<String>() {{
                        add(datasetsFS.getName() + "/readme.md");
                        add(perspectivesFS.getName() + "/page1/perspective_layout");
                        add(perspectivesFS.getName() + "/page1/perspective_layout.plugin");
                        add(perspectivesFS.getName() + "/readme.md");
                        add(navigationFS.getName() + "/readme.md");
                        add("VERSION");
                    }}, getFiles(zis));
        
        cleanFileSystems();        
    }
    
    @Test
    @SuppressWarnings("serial")
    public void testDoImportNoZip() throws Exception {
        List<String> filesImported = dataTransferServices.doImport();

        assertEquals(new ArrayList<String>(), filesImported);

        assertEquals(new ArrayList<String>() {{
                        add("/readme.md");
                    }}, getFiles(datasetsFS));

        assertEquals(new ArrayList<String>() {{
                        add("/readme.md");
                    }}, getFiles(perspectivesFS));

        assertEquals(new ArrayList<String>() {{
                        add("/readme.md");
                    }}, getFiles(navigationFS));

        assertEquals(new ArrayList<String>() {{
                        add("/readme.md"); 
                    }}, getFiles(systemFS));

        verify(dataSetDefRegisteredEvent, times(0)).fire(any());
        verify(pluginAddedEvent, times(0)).fire(any());
        verify(navTreeChangedEvent, times(0)).fire(any());
    }

    @Test
    @SuppressWarnings("serial")
    public void testDoImportEmptyZip() throws Exception {
        moveZipToFileSystem("/empty.zip");

        List<String> filesImported = dataTransferServices.doImport();

        assertEquals(new ArrayList<String>(), filesImported);

        assertEquals(new ArrayList<String>() {{
                        add("/readme.md");
                    }}, getFiles(datasetsFS));

        assertEquals(new ArrayList<String>() {{
                        add("/readme.md");
                    }}, getFiles(perspectivesFS));

        assertEquals(new ArrayList<String>() {{
                        add("/readme.md");
                    }}, getFiles(navigationFS));

        assertEquals(new ArrayList<String>() {{
                        add("/readme.md");
                    }}, getFiles(systemFS));

        verify(dataSetDefRegisteredEvent, times(0)).fire(any());
        verify(pluginAddedEvent, times(0)).fire(any());
        verify(navTreeChangedEvent, times(0)).fire(any());
    }

    @Test
    @SuppressWarnings("serial")
    public void testDoImportNotEmptyZip() throws Exception {
        moveZipToFileSystem("/import.zip");

        List<String> filesImported = dataTransferServices.doImport();
        
        assertEquals(new ArrayList<String>() {{
                        add("dashbuilder/datasets/readme.md");
                        add("dashbuilder/datasets/definitions/eb241039-1792-4d08-9596-b6c8d27dfe6b.csv");
                        add("dashbuilder/datasets/definitions/d1b24449-fe90-40d4-8cd7-f175b498c0bb.dset");
                        add("dashbuilder/datasets/definitions/8060a7f1-ef03-4ce9-a0a8-266301e79ff6.dset");
                        add("dashbuilder/datasets/definitions/eb241039-1792-4d08-9596-b6c8d27dfe6b.dset");
                        add("dashbuilder/datasets/definitions/7e68d20d-6807-4b86-8737-1d429afe9dbc.csv");
                        add("dashbuilder/datasets/definitions/d1b24449-fe90-40d4-8cd7-f175b498c0bb.csv");
                        add("dashbuilder/datasets/definitions/8060a7f1-ef03-4ce9-a0a8-266301e79ff6.csv");
                        add("dashbuilder/datasets/definitions/7e68d20d-6807-4b86-8737-1d429afe9dbc.dset");
                        add("dashbuilder/components/c2/manifest.json");
                        add("dashbuilder/components/c2/styles/level/styles.css");
                        add("dashbuilder/components/c2/index.html");
                        add("dashbuilder/components/c1/manifest.json");
                        add("dashbuilder/components/c1/images/db_logo.png");
                        add("dashbuilder/components/c1/scripts/index.js");
                        add("dashbuilder/components/c1/index.html");
                        add("dashbuilder/perspectives/page3/perspective_layout");
                        add("dashbuilder/perspectives/page3/perspective_layout.plugin");
                        add("dashbuilder/perspectives/readme.md");
                        add("dashbuilder/perspectives/page4/perspective_layout");
                        add("dashbuilder/perspectives/page4/perspective_layout.plugin");
                        add("dashbuilder/perspectives/page2/perspective_layout");
                        add("dashbuilder/perspectives/page2/perspective_layout.plugin");
                        add("dashbuilder/perspectives/page1/perspective_layout");
                        add("dashbuilder/perspectives/page1/perspective_layout.plugin");
                        add("dashbuilder/navigation/readme.md");
                        add("dashbuilder/navigation/navigation/navtree.json");
                    }}, filesImported);

        assertEquals(new ArrayList<String>() {{
                        add("/definitions/7e68d20d-6807-4b86-8737-1d429afe9dbc.csv");
                        add("/definitions/7e68d20d-6807-4b86-8737-1d429afe9dbc.dset");
                        add("/definitions/8060a7f1-ef03-4ce9-a0a8-266301e79ff6.csv");
                        add("/definitions/8060a7f1-ef03-4ce9-a0a8-266301e79ff6.dset");
                        add("/definitions/d1b24449-fe90-40d4-8cd7-f175b498c0bb.csv");
                        add("/definitions/d1b24449-fe90-40d4-8cd7-f175b498c0bb.dset");
                        add("/definitions/eb241039-1792-4d08-9596-b6c8d27dfe6b.csv");
                        add("/definitions/eb241039-1792-4d08-9596-b6c8d27dfe6b.dset");
                        add("/readme.md");
                    }}, getFiles(datasetsFS));

        assertEquals(new ArrayList<String>() {{
                        add("/page1/perspective_layout");
                        add("/page1/perspective_layout.plugin");
                        add("/page2/perspective_layout");
                        add("/page2/perspective_layout.plugin");
                        add("/page3/perspective_layout");
                        add("/page3/perspective_layout.plugin");
                        add("/page4/perspective_layout");
                        add("/page4/perspective_layout.plugin");
                        add("/readme.md");
                    }}, getFiles(perspectivesFS));

        assertEquals(new ArrayList<String>() {{
                        add("/navigation/navtree.json");
                        add("/readme.md");
                    }}, getFiles(navigationFS));

        assertEquals(new ArrayList<String>() {{
                        add("/readme.md");
                    }}, getFiles(systemFS));
        
        List<String> expectedComponents = new ArrayList<String>() {{
            add("c1/index.html");
            add("c1/scripts/index.js");
            add("c1/images/db_logo.png");
            add("c1/manifest.json");
            add("c2/index.html");
            add("c2/styles/level/styles.css");
            add("c2/manifest.json");
        }};
        expectedComponents.removeAll(getFiles(componentsDir));
        assertTrue(expectedComponents.isEmpty());

        verify(dataSetDefRegisteredEvent, times(4)).fire(any());
        verify(pluginAddedEvent, times(4)).fire(any());
        verify(navTreeChangedEvent, times(1)).fire(any());
    }

    @Test
    public void testAssetsToImport() throws Exception {
        final String PAGE_ID = "page";
        final String DS_CSV = "ds.csv";
        final String DS = "ds.dset";
        final String DS_CONTENT = "TEST_CONTENT";
        final String DS_NAME = "test_dataset";
        final String PAGE = PAGE_ID + "/perspective_layout";
        final String PAGE_PLUGIN = PAGE_ID + "/perspective_layout.plugin";
        
        DataSetDef dataSetDef = mock(DataSetDef.class);
        when(dataSetDef.isPublic()).thenReturn(true);
        when(dataSetDef.getName()).thenReturn(DS_NAME);
        when(dataSetDefJSONMarshaller.fromJson(DS_CONTENT)).thenReturn(dataSetDef);

        createFile(datasetsFS, DS, DS_CONTENT);
        createFile(datasetsFS, DS_CSV, "");
        createFile(perspectivesFS, PAGE, "");
        createFile(perspectivesFS, PAGE_PLUGIN, "");

        DataTransferAssets assetsToExport = dataTransferServices.assetsToExport();

        assertEquals(1, assetsToExport.getDatasetsDefinitions().size());
        assertEquals(DS_NAME, assetsToExport.getDatasetsDefinitions().get(0).getName());
        assertEquals(1, assetsToExport.getPages().size());
        assertEquals(PAGE_ID, assetsToExport.getPages().get(0));
        
        cleanFileSystems();
    }
    
    @Test
    public void testAssetsToImportNoFiles() {
        DataTransferAssets assetsToExport = dataTransferServices.assetsToExport();
        assertTrue(assetsToExport.getDatasetsDefinitions().isEmpty());
        assertTrue(assetsToExport.getPages().isEmpty());
    }

    @SuppressWarnings("serial")
    private FileSystem createFileSystem(String name) {
        String path = new StringBuilder().append("git://dashbuilder")
                                         .append(File.separator)
                                         .append(name)
                                         .toString();

        try {
            return ioService.newFileSystem(URI.create(path),
                                           new HashMap<String, Object>() {{
                                                   put("init", Boolean.TRUE);
                                               }});

        } catch (Exception e) {
            return ioService.getFileSystem(URI.create(path));
        }
    }
    
    @Test
    public void testShouldNotExposePrivateDS() throws Exception {
        final String DS = "ds.dset";
        final String DS_CONTENT = "TEST_CONTENT";
        final String DS_NAME = "test_dataset";
        
        DataSetDef dataSetDef = mock(DataSetDef.class);
        when(dataSetDef.isPublic()).thenReturn(false);
        when(dataSetDef.getName()).thenReturn(DS_NAME);
        when(dataSetDefJSONMarshaller.fromJson(DS_CONTENT)).thenReturn(dataSetDef);

        createFile(datasetsFS, DS, DS_CONTENT);

        DataTransferAssets assetsToExport = dataTransferServices.assetsToExport();

        assertTrue(assetsToExport.getDatasetsDefinitions().isEmpty());
        
        cleanFileSystems();
    }

    private Path createFile(FileSystem fs, String filename, String data) {
        Path path = fs.getRootDirectories().iterator().next();
        Path filePath = path.resolve(filename);
        ioService.write(filePath, data);
        return filePath;
    }
    
    private Path createComponentFile(String componentId, String filename, String data) {
        Path componentPath = componentsDir.resolve(componentId);
        Path componentFile = componentPath.resolve(filename);
        componentFile.getParent().toFile().mkdirs();
        ioService.write(componentFile, data);
        return componentFile;
    }
    
    private List<String> getFiles(Path root) {
        List<String> files = new ArrayList<>();

        Files.walkFileTree(root, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(final Path path, final BasicFileAttributes attrs) throws IOException {
                files.add(path.toString().replaceAll(componentsDir.toString() + "/", ""));
                return FileVisitResult.CONTINUE;
            }
        });

        return files;
    }

    private List<String> getFiles(FileSystem fs) {
        List<String> files = new ArrayList<>();
        Path root = fs.getRootDirectories().iterator().next();

        Files.walkFileTree(root, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(final Path path, final BasicFileAttributes attrs) throws IOException {
                files.add(path.toString());
                return FileVisitResult.CONTINUE;
            }
        });

        return files;
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

    private String getExpectedExportFilePath() {
        return new StringBuilder().append(File.separator)
                                  .append(DataTransferServices.FILE_PATH)
                                  .append(File.separator)
                                  .append(DataTransferServices.EXPORT_FILE_NAME)
                                  .toString();
    }

    private String getExpectedImportFilePath() {
        return new StringBuilder().append(File.separator)
                                  .append(DataTransferServices.FILE_PATH)
                                  .append(File.separator)
                                  .append(DataTransferServices.IMPORT_FILE_NAME)
                                  .toString();
    }

    private String getExpectedExportFileSystemPath() {
        return new StringBuilder().append("git://")
                                  .append(systemFS.getName())
                                  .append(getExpectedExportFilePath())
                                  .toString();
    }

    private void moveZipToFileSystem(String path) {
        URL url = DataTransferServicesTest.class.getResource(path);

        String sourceLocation = new StringBuilder().append(SpacesAPI.Scheme.FILE)
                                                   .append("://")
                                                   .append(url.toString())
                                                   .toString();

        Path source = Paths.get(URI.create(sourceLocation));

        Path target = systemFS.getRootDirectories()
                              .iterator()
                              .next()
                              .resolve(DataTransferServices.FILE_PATH)
                              .resolve(DataTransferServices.IMPORT_FILE_NAME);

        ioService.write(target, Files.readAllBytes(source));
    }

    private void cleanFileSystem(FileSystem fs) {
        for (String file : getFiles(fs)) {
            if (file.endsWith("readme.md")) {
                continue;
            }

            String path = new StringBuilder().append("git://")
                                             .append(fs.getName())
                                             .append(file)
                                             .toString();

            ioService.delete(Paths.get(URI.create(path)));
        }
    }

    private ZipInputStream getZipInputStream() {
        Path path = Paths.get(URI.create(new StringBuilder().append("git://")
                                                            .append(systemFS.getName())
                                                            .append(getExpectedExportFilePath())
                                                            .toString()));
        return new ZipInputStream(new ByteArrayInputStream(ioService.readAllBytes(path)));
    }
    
    
    public ExternalComponent component(String id) {
        return new ExternalComponent(id, id, "", false, Collections.emptyList());
    }
    
}
