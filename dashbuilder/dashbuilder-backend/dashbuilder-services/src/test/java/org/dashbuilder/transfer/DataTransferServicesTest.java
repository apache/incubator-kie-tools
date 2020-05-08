package org.dashbuilder.transfer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.any;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.enterprise.event.Event;

import org.dashbuilder.dataset.DataSetDefRegistryCDI;
import org.dashbuilder.dataset.events.DataSetDefRegisteredEvent;
import org.dashbuilder.dataset.json.DataSetDefJSONMarshaller;
import org.dashbuilder.navigation.event.NavTreeChangedEvent;
import org.dashbuilder.navigation.storage.NavTreeStorage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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

@RunWith(MockitoJUnitRunner.class)
public class DataTransferServicesTest {

    private IOService ioService;
    private FileSystem datasetsFS;
    private FileSystem perspectivesFS;
    private FileSystem navigationFS;
    private FileSystem systemFS;
    private DataTransferServices dataTransferServices;

    @Mock private DataSetDefRegistryCDI dataSetDefRegistryCDI;
    @Mock private Event<DataSetDefRegisteredEvent> dataSetDefRegisteredEvent;
    @Mock private Event<PluginAdded> pluginAddedEvent;
    @Mock SessionInfo sessionInfo;
    @Mock private Event<NavTreeChangedEvent> navTreeChangedEvent;
    @Mock private NavTreeStorage navTreeStorage;

    @Before
    public void setup() {
        ioService = new IOServiceDotFileImpl();

        datasetsFS = createFileSystem("datasets");
        perspectivesFS = createFileSystem("perspectives");
        navigationFS = createFileSystem("navigation");
        systemFS = createFileSystem("system");

        when(
            dataSetDefRegistryCDI.getDataSetDefJsonMarshaller())
                .thenReturn(
                    mock(DataSetDefJSONMarshaller.class));

        dataTransferServices = new DataTransferServicesImpl(
            ioService,
            datasetsFS,
            perspectivesFS,
            navigationFS,
            systemFS,
            dataSetDefRegistryCDI,
            sessionInfo,
            dataSetDefRegisteredEvent,
            pluginAddedEvent,
            navTreeChangedEvent,
            navTreeStorage);
    }

    @After
    public void cleanFileSystems() {
        cleanFileSystem(datasetsFS);
        cleanFileSystem(perspectivesFS);
        cleanFileSystem(navigationFS);
        cleanFileSystem(systemFS);
    }

    @Test
    public void testDoExportEmptyFileSystems() throws Exception {
        String exportPath = dataTransferServices.doExport();

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
    public void testDoExportNotEmptyFileSystems() throws Exception {
        createFile(datasetsFS, "definitions/dataset1.csv", "Test 1");
        createFile(datasetsFS, "definitions/dataset1.dset", "Test ABC");
        createFile(perspectivesFS, "page1/perspective_layout", "Test Page 1");
        createFile(perspectivesFS, "page1/perspective_layout.plugin", "Test Page 1 Plugin");
        createFile(navigationFS, "navtree.json", "{ }");

        String exportPath = dataTransferServices.doExport();

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
    public void testDoImportNotEmptyZip() throws Exception {
        moveZipToFileSystem("/import.zip");

        List<String> filesImported = dataTransferServices.doImport();

        assertEquals(new ArrayList<String>() {{
            add("dashbuilder/datasets/definitions/7e68d20d-6807-4b86-8737-1d429afe9dbc.csv");
            add("dashbuilder/datasets/definitions/7e68d20d-6807-4b86-8737-1d429afe9dbc.dset");
            add("dashbuilder/datasets/definitions/8060a7f1-ef03-4ce9-a0a8-266301e79ff6.csv");
            add("dashbuilder/datasets/definitions/8060a7f1-ef03-4ce9-a0a8-266301e79ff6.dset");
            add("dashbuilder/datasets/definitions/d1b24449-fe90-40d4-8cd7-f175b498c0bb.csv");
            add("dashbuilder/datasets/definitions/d1b24449-fe90-40d4-8cd7-f175b498c0bb.dset");
            add("dashbuilder/datasets/definitions/eb241039-1792-4d08-9596-b6c8d27dfe6b.csv");
            add("dashbuilder/datasets/definitions/eb241039-1792-4d08-9596-b6c8d27dfe6b.dset");
            add("dashbuilder/datasets/readme.md");
            add("dashbuilder/perspectives/page1/perspective_layout");
            add("dashbuilder/perspectives/page1/perspective_layout.plugin");
            add("dashbuilder/perspectives/page2/perspective_layout");
            add("dashbuilder/perspectives/page2/perspective_layout.plugin");
            add("dashbuilder/perspectives/page3/perspective_layout");
            add("dashbuilder/perspectives/page3/perspective_layout.plugin");
            add("dashbuilder/perspectives/page4/perspective_layout");
            add("dashbuilder/perspectives/page4/perspective_layout.plugin");
            add("dashbuilder/perspectives/readme.md");
            add("dashbuilder/navigation/navigation/navtree.json");
            add("dashbuilder/navigation/readme.md");
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

        verify(dataSetDefRegisteredEvent, times(4)).fire(any());
        verify(pluginAddedEvent, times(4)).fire(any());
        verify(navTreeChangedEvent, times(1)).fire(any());
    }

    private FileSystem createFileSystem(String name) {
        String path = new StringBuilder()
            .append("git://dashbuilder")
            .append(File.separator)
            .append(name)
            .toString();

        try {
            return ioService.newFileSystem(
                URI.create(path),
                new HashMap<String, Object>() {{
                    put("init", Boolean.TRUE);
                }});

        } catch (Exception e) {
            return ioService.getFileSystem(
                URI.create(path));
        }
    }

    private void createFile(FileSystem fs, String filename, String data) {
         Path path = fs.getRootDirectories().iterator().next();
         ioService.write(path.resolve(filename), data);
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

        } catch(Exception e) {
            e.printStackTrace();
        }

        return files;
    }

    private String getExpectedExportFilePath() {
        return new StringBuilder()
            .append(File.separator)
            .append(DataTransferServices.FILE_PATH)
            .append(File.separator)
            .append(DataTransferServices.EXPORT_FILE_NAME)
            .toString();
    }

    private String getExpectedImportFilePath() {
        return new StringBuilder()
            .append(File.separator)
            .append(DataTransferServices.FILE_PATH)
            .append(File.separator)
            .append(DataTransferServices.IMPORT_FILE_NAME)
            .toString();
    }

    private String getExpectedExportFileSystemPath() {
        return new StringBuilder()
            .append("default://")
            .append(systemFS.getName())
            .append(getExpectedExportFilePath())
            .toString();
    }

    private void moveZipToFileSystem(String path) {
        URL url = DataTransferServicesTest.class.getResource(path);

        String sourceLocation = new StringBuilder()
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

            String path = new StringBuilder()
                .append("git://")
                .append(fs.getName())
                .append(file)
                .toString();

            ioService.delete(
                Paths.get(
                    URI.create(path)));
        }
    }

    private ZipInputStream getZipInputStream() {
        return new ZipInputStream(
            new ByteArrayInputStream(
                ioService.readAllBytes(
                    Paths.get(
                        URI.create(
                            new StringBuilder()
                                .append("git://")
                                .append(systemFS.getName())
                                .append(getExpectedExportFilePath())
                                .toString())))));
    }
}
