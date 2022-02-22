package org.dashbuilder.project.storage.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.dashbuilder.project.storage.ProjectStorageServices;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.dashbuilder.project.storage.ProjectStorageServices.DATASETS_PARENT_PATH;
import static org.dashbuilder.project.storage.ProjectStorageServices.DATASETS_PATH;
import static org.dashbuilder.project.storage.ProjectStorageServices.DATASET_EXT;
import static org.dashbuilder.project.storage.ProjectStorageServices.README;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ProjectStorageServicesImplTest {

    private String parent;
    private ProjectStorageServicesImpl projectServicesImpl;

    @Before
    public void init() {
        parent = ProjectStorageServicesImplTest.class.getResource(".").getFile() + "/dashbuilder";
        System.setProperty(ProjectStorageServicesImpl.DB_BASE_PATH, parent);
        projectServicesImpl = new ProjectStorageServicesImpl();
        projectServicesImpl.init();
        projectServicesImpl.createStructure();
    }

    @After
    public void cleanup() {
        projectServicesImpl.clear();
    }

    @Test
    public void testInitStructure() {
        var datasetsParentPath = Paths.get(parent, DATASETS_PARENT_PATH);
        assertTrue(datasetsParentPath.resolve(README).toFile().exists());
        assertTrue(datasetsParentPath.resolve(DATASETS_PATH).toFile().exists());

        var perspectivesPath = Paths.get(parent, ProjectStorageServicesImpl.PERSPECTIVES_PATH);
        assertTrue(perspectivesPath.toFile().exists());
        assertTrue(perspectivesPath.resolve(ProjectStorageServicesImpl.README).toFile().exists());

        var navigationParentPath = Paths.get(parent, ProjectStorageServicesImpl.NAVIGATION_PARENT_PATH);
        assertTrue(navigationParentPath.toFile().exists());
        assertTrue(navigationParentPath.resolve(ProjectStorageServicesImpl.README).toFile().exists());
        assertTrue(navigationParentPath.resolve(ProjectStorageServicesImpl.NAVIGATION_PATH).toFile().exists());
    }

    @Test
    public void testSaveDataSet() throws IOException {
        var name = "abc";
        var content = "the content";
        projectServicesImpl.saveDataSet(name, content);
        var dsPath = Paths.get(parent, DATASETS_PARENT_PATH, DATASETS_PATH, name + DATASET_EXT);
        assertEquals(content, Files.readString(dsPath));
    }

    @Test
    public void testGetDataSet() {
        var name = "abc";
        var content = "the content";
        projectServicesImpl.saveDataSet(name, content);
        assertEquals(content, projectServicesImpl.getDataSet(name).get());
        assertEquals(content, projectServicesImpl.getDataSet(name + DATASET_EXT).get());

        assertTrue(projectServicesImpl.getDataSet("xxx").isEmpty());
    }

    @Test
    public void testRemoveDataSet() throws IOException {
        var name = "abc";
        var content = "the content";
        projectServicesImpl.saveDataSet(name, content);
        var dsPath = Paths.get(parent, DATASETS_PARENT_PATH, DATASETS_PATH, name + DATASET_EXT);
        assertTrue(dsPath.toFile().exists());
        projectServicesImpl.removeDataSet(name);
        assertFalse(dsPath.toFile().exists());
    }

    @Test
    public void testListingDataSets() {
        var name1 = "abc";
        var content1 = "the content";

        var name2 = "bca";
        var content2 = "the content 2";

        projectServicesImpl.saveDataSet(name1, content1);
        projectServicesImpl.saveDataSet(name2, content2);

        var datasets = projectServicesImpl.listDataSets();
        assertEquals(2, datasets.size());

        var ds1Path = datasets.keySet().stream().filter(p -> p.toString().endsWith(name1 + DATASET_EXT)).findFirst()
                .get();
        var ds2Path = datasets.keySet().stream().filter(p -> p.toString().endsWith(name2 + DATASET_EXT)).findFirst()
                .get();

        assertEquals(content1, datasets.get(ds1Path));
        assertEquals(content2, datasets.get(ds2Path));
    }

    @Test
    public void testSaveNavigation() throws IOException {
        var content = "the content";
        projectServicesImpl.saveNavigation(content);
        var navigationPath = Paths.get(parent,
                                       ProjectStorageServicesImpl.NAVIGATION_PARENT_PATH,
                                       ProjectStorageServicesImpl.NAVIGATION_PATH,
                                       ProjectStorageServicesImpl.NAV_TREE_FILE_NAME);

        assertEquals(content, Files.readString(navigationPath));
    }

    @Test
    public void testGetNavigation() throws IOException {
        var content = "the content";
        projectServicesImpl.saveNavigation(content);

        var savedContent = projectServicesImpl.getNavigation();
        assertEquals(content, savedContent.get());
    }

    @Test
    public void testCreateTempContent() throws IOException {
        var name = "abc";
        var content = "new content";
        projectServicesImpl.createTempContent(name, content);
        var path = Paths.get(parent, ProjectStorageServicesImpl.TEMP_PATH, name);
        assertEquals(content, Files.readString(path));
    }

    @Test
    public void testCreateTempPath() throws IOException {
        var name = "abc";
        var path = projectServicesImpl.createTempPath(name);
        var expectedPath = Paths.get(parent, ProjectStorageServicesImpl.TEMP_PATH, name);
        assertEquals(path, expectedPath);
    }

    @Test
    public void testGetTempContent() throws IOException {
        var name = "abc";
        var content = "new content";
        projectServicesImpl.createTempContent(name, content);
        assertEquals(content, Files.readString(projectServicesImpl.getTempPath(name)));
    }

    @Test
    public void testRemoveTempContent() throws IOException {
        var name = "abc";
        var content = "new content";

        projectServicesImpl.createTempContent(name, content);
        assertTrue(projectServicesImpl.getTempPath(name).toFile().exists());

        projectServicesImpl.removeTempContent(name);
        assertFalse(projectServicesImpl.getTempPath(name).toFile().exists());
    }

    @Test
    public void testSavePerspective() throws IOException {
        var name = "my page";
        var content = " the content";
        var expectedPath = Paths.get(parent, ProjectStorageServices.PERSPECTIVES_PATH, name);
        projectServicesImpl.savePerspective(name, content);

        assertTrue(expectedPath.toFile().isDirectory());
        assertEquals(name, expectedPath.getFileName().toString());
        assertEquals(content, Files.readString(expectedPath.resolve(ProjectStorageServices.PERSPECTIVE_LAYOUT)));
        assertTrue(expectedPath.resolve(ProjectStorageServices.PERSPECTIVE_LAYOUT_PLUGIN).toFile().exists());
    }

    @Test
    public void testGetPerspective() {
        var name = "my page";
        var content = " the content";
        projectServicesImpl.savePerspective(name, content);
        assertEquals(content, projectServicesImpl.getPerspective(name).get());
    }

    @Test
    public void testListPerspectives() {
        var n1 = "my page1";
        var c1 = "the content1";
        var n2 = "my page2";
        var c2 = "the content2";
        projectServicesImpl.savePerspective(n1, c1);
        projectServicesImpl.savePerspective(n2, c2);

        var perspectives = projectServicesImpl.listPerspectives();
        var p1 = Paths.get(parent, ProjectStorageServices.PERSPECTIVES_PATH, n1, ProjectStorageServices.PERSPECTIVE_LAYOUT);
        var p2 = Paths.get(parent, ProjectStorageServices.PERSPECTIVES_PATH, n2, ProjectStorageServices.PERSPECTIVE_LAYOUT);

        assertEquals(c1, perspectives.get(p1));
        assertEquals(c2, perspectives.get(p2));

        projectServicesImpl.removePerspective(n1);
        projectServicesImpl.removePerspective(n2);

        perspectives = projectServicesImpl.listPerspectives();

        assertTrue(perspectives.isEmpty());
    }

    @Test
    public void testRemovePerspective() {
        var n1 = "my page1";
        var c1 = "the content1";
        projectServicesImpl.savePerspective(n1, c1);

        var p1 = Paths.get(parent, ProjectStorageServices.PERSPECTIVES_PATH, n1, ProjectStorageServices.PERSPECTIVE_LAYOUT);
        projectServicesImpl.removePerspective(n1);

        assertFalse(p1.toFile().exists());
    }
}