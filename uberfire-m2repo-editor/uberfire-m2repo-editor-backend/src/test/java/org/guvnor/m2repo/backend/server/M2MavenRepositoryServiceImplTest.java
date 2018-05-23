/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.m2repo.backend.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.enterprise.inject.Instance;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemHeaders;
import org.eclipse.aether.artifact.Artifact;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.m2repo.backend.server.helpers.FormData;
import org.guvnor.m2repo.backend.server.helpers.HttpPostHelper;
import org.guvnor.m2repo.backend.server.repositories.ArtifactRepository;
import org.guvnor.m2repo.backend.server.repositories.ArtifactRepositoryProducer;
import org.guvnor.m2repo.backend.server.repositories.ArtifactRepositoryService;
import org.guvnor.m2repo.model.JarListPageRequest;
import org.guvnor.m2repo.model.JarListPageRow;
import org.guvnor.m2repo.preferences.ArtifactRepositoryPreference;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.cdi.workspace.WorkspaceNameResolver;
import org.uberfire.mocks.MockInstanceImpl;
import org.uberfire.paging.PageResponse;

import static org.guvnor.m2repo.model.HTMLFileManagerFields.UPLOAD_OK;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class M2MavenRepositoryServiceImplTest {

    private static final Logger log = LoggerFactory.getLogger(M2MavenRepositoryServiceImplTest.class);
    private static GAV gavBackend;
    private static GAV gavBackend1;
    private static GAV gavBackend2;
    private static GAV gavArquillian;
    @Rule
    public final ExpectedException exception = ExpectedException.none();
    private GuvnorM2Repository repo;
    private M2RepoServiceImpl service;
    private HttpPostHelper helper;
    private java.lang.reflect.Method helperMethod;

    @BeforeClass
    public static void setupClass() {
        gavBackend = new GAV("org.kie.guvnor",
                             "guvnor-m2repo-editor-backend",
                             "0.0.1-SNAPSHOT");

        gavBackend1 = new GAV("org.kie.guvnor",
                              "guvnor-m2repo-editor-backend1",
                              "0.0.1-SNAPSHOT");

        gavBackend2 = new GAV("org.kie.guvnor",
                              "guvnor-m2repo-editor-backend2",
                              "0.0.1-SNAPSHOT");

        gavArquillian = new GAV("org.jboss.arquillian.core",
                                "arquillian-core-api",
                                "1.0.2.Final");
    }

    @AfterClass
    public static void tearDown() throws Exception {
        log.info("Deleting all Repository instances..");

        File dir = new File("repositories");
        log.info("DELETING test repo: " + dir.getAbsolutePath());
        deleteDir(dir);
        log.info("TEST repo was deleted.");
    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                if (!deleteDir(new File(dir,
                                        children[i]))) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

    @Before
    public void setup() throws Exception {
        log.info("Deleting existing Repositories instance..");

        File dir = new File("repositories");
        log.info("DELETING test repo: " + dir.getAbsolutePath());
        deleteDir(dir);
        log.info("TEST repo was deleted.");

        ArtifactRepositoryPreference pref = mock(ArtifactRepositoryPreference.class);
        when(pref.getGlobalM2RepoDir()).thenReturn("repositories/kie");
        when(pref.isGlobalM2RepoDirEnabled()).thenReturn(true);
        when(pref.isDistributionManagementM2RepoDirEnabled()).thenReturn(true);
        when(pref.isWorkspaceM2RepoDirEnabled()).thenReturn(false);
        WorkspaceNameResolver resolver = mock(WorkspaceNameResolver.class);
        when(resolver.getWorkspaceName()).thenReturn("global");
        ArtifactRepositoryProducer producer = new ArtifactRepositoryProducer(pref,
                                                                             resolver);
        producer.initialize();
        Instance<ArtifactRepository> repositories = new MockInstanceImpl<>(producer.produceLocalRepository(),
                                                                           producer.produceGlobalRepository(),
                                                                           producer.produceDistributionManagementRepository());
        ArtifactRepositoryService factory = new ArtifactRepositoryService(repositories);
        repo = new GuvnorM2Repository(factory);
        repo.init();

        //Create a shell M2RepoService and set the M2Repository
        service = new M2RepoServiceImpl(mock(Logger.class),
                                        this.repo);
        java.lang.reflect.Field repositoryField = M2RepoServiceImpl.class.getDeclaredField("repository");
        repositoryField.setAccessible(true);
        repositoryField.set(service,
                            repo);

        //Make private method accessible for testing
        helper = new HttpPostHelper();
        helperMethod = HttpPostHelper.class.getDeclaredMethod("upload",
                                                              FormData.class);
        helperMethod.setAccessible(true);

        //Set the repository service created above in the HttpPostHelper
        java.lang.reflect.Field m2RepoServiceField = HttpPostHelper.class.getDeclaredField("m2RepoService");
        m2RepoServiceField.setAccessible(true);
        m2RepoServiceField.set(helper,
                               service);
    }

    @Test
    public void testDeployArtifact() throws Exception {
        deployArtifact(gavBackend);

        Collection<File> files = repo.listFiles();

        boolean found = false;
        for (File file : files) {
            String fileName = file.getName();
            if (fileName.startsWith("guvnor-m2repo-editor-backend-0.0.1") && fileName.endsWith(".jar")) {
                found = true;
                String path = file.getPath();
                String jarPath = path.substring(repo.getM2RepositoryRootDir(ArtifactRepositoryService.GLOBAL_M2_REPO_NAME).length());
                String pom = repo.getPomText(jarPath);
                assertNotNull(pom);
                break;
            }
        }

        assertTrue("Did not find expected file after calling M2Repository.addFile()",
                   found);

        // Test get artifact file
        File file = repo.getArtifactFileFromRepository(gavBackend);
        assertNotNull("Empty file for artifact",
                      file);
        JarFile jarFile = new JarFile(file);
        int count = 0;

        String lastEntryName = null;
        for (Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements(); ) {
            ++count;
            JarEntry entry = entries.nextElement();
            assertNotEquals("Endless loop.",
                            lastEntryName,
                            entry.getName());
        }
        assertTrue("Empty jar file!",
                   count > 0);
    }

    @Test
    public void testDeployPom() throws Exception {
        InputStream is = this.getClass().getResourceAsStream("guvnor-m2repo-editor-backend-test-pom.xml");
        repo.deployPom(is,
                       gavBackend);

        Collection<File> files = repo.listFiles();

        boolean found = false;
        for (File file : files) {
            String fileName = file.getName();
            if (fileName.startsWith("guvnor-m2repo-editor-backend-0.0.1") && fileName.endsWith(".pom")) {
                found = true;
                String path = file.getPath();
                String jarPath = path.substring(repo.getM2RepositoryRootDir(ArtifactRepositoryService.GLOBAL_M2_REPO_NAME).length());
                String pom = repo.getPomText(jarPath);
                assertNotNull(pom);
                break;
            }
        }

        assertTrue("Did not find expected file after calling M2Repository.addFile()",
                   found);
    }

    @Test
    public void testListFiles() throws Exception {
        deployArtifact(gavBackend);
        deployArtifact(gavArquillian);

        Collection<File> files = repo.listFiles();

        boolean found1 = false;
        boolean found2 = false;
        for (File file : files) {
            String fileName = file.getName();
            if (fileName.startsWith("guvnor-m2repo-editor-backend-0.0.1") && fileName.endsWith(".jar")) {
                found1 = true;
            }
            if (fileName.startsWith("arquillian-core-api-1.0.2.Final") && fileName.endsWith(".jar")) {
                found2 = true;
            }
        }

        assertTrue("Did not find expected file after calling M2Repository.addFile()",
                   found1);
        assertTrue("Did not find expected file after calling M2Repository.addFile()",
                   found2);
    }

    @Test
    public void testListFilesWithFilter() throws Exception {
        deployArtifact(gavBackend);
        deployArtifact(gavArquillian);

        //filter with version number
        boolean found1 = false;
        Collection<File> files = repo.listFiles("1.0.2");
        final String VERSION_NUMBER_SEARCH_FILTER = "arquillian-core-api-1.0.2";
        for (File file : files) {
            String fileName = file.getName();
            if (fileName.startsWith(VERSION_NUMBER_SEARCH_FILTER) && fileName.endsWith(".jar")) {
                found1 = true;
            }
        }
        assertTrue("Did not find expected file after calling M2Repository.addFile()",
                   found1);

        for (File file : files) {
            String fileName = file.getName();
            if (!fileName.contains(VERSION_NUMBER_SEARCH_FILTER)) {
                Assert.fail(fileName + " doesn't match the filter " + VERSION_NUMBER_SEARCH_FILTER);
            }
        }

        //filter with artifact id
        found1 = false;
        files = repo.listFiles("arquillian-core-api");
        final String ARTIFACT_SEARCH_FILTER = "arquillian-core-api";
        for (File file : files) {
            String fileName = file.getName();
            if (fileName.startsWith(ARTIFACT_SEARCH_FILTER) && fileName.endsWith(".jar")) {
                found1 = true;
            }
        }
        assertTrue("Did not find expected file after calling M2Repository.addFile()",
                   found1);

        for (File file : files) {
            String fileName = file.getName();
            if (!fileName.contains(ARTIFACT_SEARCH_FILTER)) {
                Assert.fail(fileName + " doesn't match the filter " + ARTIFACT_SEARCH_FILTER);
            }
        }
    }

    @Test
    public void testUploadJARWithPOM() throws Exception {
        FormData uploadItem = new FormData();
        FileItem file = new MockFileItem("guvnor-m2repo-editor-backend-test-with-pom.jar",
                                         this.getClass().getResourceAsStream("guvnor-m2repo-editor-backend-test-with-pom.jar"));
        uploadItem.setFile(file);

        assert (helperMethod.invoke(helper,
                                    uploadItem).equals(UPLOAD_OK));
    }

    @Test
    public void testUploadKJARWithPOM() throws Exception {
        FormData uploadItem = new FormData();
        FileItem file = new MockFileItem("guvnor-m2repo-editor-backend-test-with-pom.kjar",
                                         this.getClass().getResourceAsStream("guvnor-m2repo-editor-backend-test-with-pom.jar"));
        uploadItem.setFile(file);

        assert (helperMethod.invoke(helper,
                                    uploadItem).equals(UPLOAD_OK));
    }

    @Test
    public void testUploadJARWithManualGAV() throws Exception {
        FormData uploadItem = new FormData();
        uploadItem.setGav(gavBackend);
        FileItem file = new MockFileItem("guvnor-m2repo-editor-backend-test-without-pom.jar",
                                         this.getClass().getResourceAsStream("guvnor-m2repo-editor-backend-test-without-pom.jar"));
        uploadItem.setFile(file);

        assert (helperMethod.invoke(helper,
                                    uploadItem).equals(UPLOAD_OK));
    }

    @Test
    public void testUploadKJARWithManualGAV() throws Exception {
        FormData uploadItem = new FormData();
        uploadItem.setGav(gavBackend);
        FileItem file = new MockFileItem("guvnor-m2repo-editor-backend-test.kjar",
                                         this.getClass().getResourceAsStream("guvnor-m2repo-editor-backend-test-without-pom.jar"));
        uploadItem.setFile(file);

        assert (helperMethod.invoke(helper,
                                    uploadItem).equals(UPLOAD_OK));
    }

    /**
     * Verify that
     * {@link M2RepoServiceImpl#listArtifacts(org.guvnor.m2repo.model.JarListPageRequest) M2RepoServiceImpl.listFiles()}
     * returns correct PageResponse.
     * @throws java.lang.Exception
     */
    @Test
    public void testListArtifacts() throws Exception {
        List<Artifact> artifacts = new ArrayList<Artifact>();
        final int TOTAL = 5;
        final int PAGE_START = 1;
        final int PAGE_SIZE = 2;
        for (int i = 0; i < TOTAL; i++) {
            final ArtifactImpl artifact = new ArtifactImpl(new File(repo.getM2RepositoryRootDir(ArtifactRepositoryService.GLOBAL_M2_REPO_NAME),
                                                                    "path/x" + i));
            final HashMap<String, String> map = new HashMap<String, String>();
            map.put("repository",
                    "guvnor-m2-repo");
            artifact.setProperties(map);
            artifacts.add(artifact);
        }
        // Create a mock repository to make the test independent on any project deployment
        GuvnorM2Repository mockRepo = mock(GuvnorM2Repository.class);
        Mockito.when(mockRepo.listArtifacts(Mockito.anyString(),
                                            Matchers.<List<String>>any()))
                .thenReturn(artifacts);
        when(mockRepo.getM2RepositoryDir(any())).thenReturn(repo.getM2RepositoryDir(ArtifactRepositoryService.GLOBAL_M2_REPO_NAME));

        // Create a shell M2RepoService with injected mock M2Repository
        M2RepoServiceImpl m2service = new M2RepoServiceImpl(mock(Logger.class),
                                                            mockRepo);

        // Verify PageResponse
        JarListPageRequest request = new JarListPageRequest(PAGE_START,
                                                            PAGE_SIZE,
                                                            null,
                                                            null,
                                                            null,
                                                            false);
        PageResponse<JarListPageRow> response = m2service.listArtifacts(request);
        assertEquals(PAGE_SIZE,
                     response.getPageRowList().size());
        assertEquals(TOTAL,
                     response.getTotalRowSize());
        int i = PAGE_START;
        for (JarListPageRow row : response.getPageRowList()) {
            assertEquals("x" + i,
                         row.getName());
            assertEquals("path/x" + i,
                         row.getPath());
            i += 1;
        }
    }

    @Test
    public void testUploadPOM() throws Exception {
        FormData uploadItem = new FormData();
        FileItem file = new MockFileItem("pom.xml",
                                         this.getClass().getResourceAsStream("guvnor-m2repo-editor-backend-test-pom.xml"));
        uploadItem.setFile(file);

        assert (helperMethod.invoke(helper,
                                    uploadItem).equals(UPLOAD_OK));

        assertFilesCount(null,
                         null,
                         null,
                         false,
                         1);
    }

    @Test
    public void testListFilesWithSortOnNameAscending() throws Exception {
        deployArtifact(gavBackend1);
        deployArtifact(gavBackend2);

        //Sort by Name ascending
        final PageResponse<JarListPageRow> response = assertFilesCount(null,
                                                                       null,
                                                                       JarListPageRequest.COLUMN_NAME,
                                                                       true,
                                                                       4);
        final List<JarListPageRow> files = response.getPageRowList();
        final String fileName0 = files.get(0).getName();
        final String fileName2 = files.get(2).getName();
        assertTrue(fileName0.startsWith("guvnor-m2repo-editor-backend1"));
        assertTrue(fileName2.startsWith("guvnor-m2repo-editor-backend2"));
    }

    @Test
    public void testListFilesWithSortOnNameDescending() throws Exception {
        deployArtifact(gavBackend1);
        deployArtifact(gavBackend2);

        //Sort by Name descending
        final PageResponse<JarListPageRow> response = assertFilesCount(null,
                                                                       null,
                                                                       JarListPageRequest.COLUMN_NAME,
                                                                       false,
                                                                       4);
        final List<JarListPageRow> files = response.getPageRowList();
        final String fileName0 = files.get(0).getName();
        final String fileName2 = files.get(2).getName();
        assertTrue(fileName0.startsWith("guvnor-m2repo-editor-backend2"));
        assertTrue(fileName2.startsWith("guvnor-m2repo-editor-backend1"));
    }

    @Test
    public void testListFilesWithSortOnPathAscending() throws Exception {
        deployArtifact(gavBackend1);
        deployArtifact(gavBackend2);

        //Sort by Path ascending
        final PageResponse<JarListPageRow> response = assertFilesCount(null,
                                                                       null,
                                                                       JarListPageRequest.COLUMN_PATH,
                                                                       true,
                                                                       4);
        final List<JarListPageRow> files = response.getPageRowList();
        final String filePath0 = files.get(0).getPath();
        final String filePath2 = files.get(2).getPath();
        assertTrue(filePath0.contains("guvnor-m2repo-editor-backend1"));
        assertTrue(filePath2.contains("guvnor-m2repo-editor-backend2"));
    }

    @Test
    public void testListFilesWithSortOnPathDescending() throws Exception {
        deployArtifact(gavBackend1);
        deployArtifact(gavBackend2);

        //Sort by Path descending
        final PageResponse<JarListPageRow> response = assertFilesCount(null,
                                                                       null,
                                                                       JarListPageRequest.COLUMN_PATH,
                                                                       false,
                                                                       4);
        final List<JarListPageRow> files = response.getPageRowList();
        final String filePath0 = files.get(0).getPath();
        final String filePath2 = files.get(2).getPath();
        assertTrue(filePath0.contains("guvnor-m2repo-editor-backend2"));
        assertTrue(filePath2.contains("guvnor-m2repo-editor-backend1"));
    }

    @Test
    public void testListFilesWithSortOnGavAscending() throws Exception {
        deployArtifact(gavBackend1);
        deployArtifact(gavBackend2);

        //Sort by GAV ascending
        final PageResponse<JarListPageRow> response = assertFilesCount(null,
                                                                       null,
                                                                       JarListPageRequest.COLUMN_GAV,
                                                                       true,
                                                                       4);
        final List<JarListPageRow> files = response.getPageRowList();
        final GAV gav0 = files.get(0).getGav();
        final GAV gav2 = files.get(2).getGav();
        assertEquals("guvnor-m2repo-editor-backend1",
                     gav0.getArtifactId());
        assertEquals("guvnor-m2repo-editor-backend2",
                     gav2.getArtifactId());
    }

    @Test
    public void testListFilesWithSortOnGavDescending() throws Exception {
        deployArtifact(gavBackend1);
        deployArtifact(gavBackend2);

        //Sort by GAV descending
        final PageResponse<JarListPageRow> response = assertFilesCount(null,
                                                                       null,
                                                                       JarListPageRequest.COLUMN_GAV,
                                                                       false,
                                                                       4);
        final List<JarListPageRow> files = response.getPageRowList();
        final GAV gav0 = files.get(0).getGav();
        final GAV gav2 = files.get(2).getGav();
        assertEquals("guvnor-m2repo-editor-backend2",
                     gav0.getArtifactId());
        assertEquals("guvnor-m2repo-editor-backend1",
                     gav2.getArtifactId());
    }

    @Test
    public void testListFilesWithSortOnLastModifiedAscending() throws Exception {
        deployArtifact(gavBackend1);

        //Wait a bit before deploying other file (to ensure different Last Modified times)
        Thread.sleep(2000);

        //This installs a JAR and a POM
        deployArtifact(gavBackend2);

        //Sort by Last Modified ascending
        final PageResponse<JarListPageRow> response = assertFilesCount(null,
                                                                       null,
                                                                       JarListPageRequest.COLUMN_LAST_MODIFIED,
                                                                       true,
                                                                       4);
        final List<JarListPageRow> files = response.getPageRowList();
        final Long fileTime0 = files.get(0).getLastModified().getTime();
        final Long fileTime2 = files.get(2).getLastModified().getTime();
        assertTrue(fileTime0.compareTo(fileTime2) < 0);
    }

    @Test
    public void testListFilesWithSortOnLastModifiedDescending() throws Exception {
        deployArtifact(gavBackend1);

        //Wait a bit before deploying other file (to ensure different Last Modified times)
        Thread.sleep(2000);

        deployArtifact(gavBackend2);

        //Sort by Last Modified descending
        final PageResponse<JarListPageRow> response = assertFilesCount(null,
                                                                       null,
                                                                       JarListPageRequest.COLUMN_LAST_MODIFIED,
                                                                       false,
                                                                       4);
        final List<JarListPageRow> files = response.getPageRowList();
        final Long fileTime0 = files.get(0).getLastModified().getTime();
        final Long fileTime2 = files.get(2).getLastModified().getTime();
        assertTrue(fileTime0.compareTo(fileTime2) > 0);
    }

    @Test
    public void testListFilesIncludingPom() throws Exception {
        deployArtifact(gavBackend);

        //This installs a POM
        GAV gavBackendParent = new GAV("org.kie.guvnor",
                                       "guvnor-m2repo-editor-backend-parent",
                                       "0.0.1-SNAPSHOT");
        InputStream is = this.getClass().getResourceAsStream("guvnor-m2repo-editor-backend-test-pom.xml");
        repo.deployPom(is,
                       gavBackendParent);

        assertFilesCount(null,
                         null,
                         null,
                         false,
                         3);
    }

    @Test
    public void testListFilesWhenNoneExist() throws Exception {
        assertFilesCount(null,
                         null,
                         JarListPageRequest.COLUMN_GAV,
                         false,
                         0);
    }

    @Test
    public void testListFilesWithPageSize() throws Exception {
        //Deploy 2 files (equating to 4 files)
        deployArtifact(gavBackend1);
        deployArtifact(gavBackend2);

        final JarListPageRequest request = new JarListPageRequest(0,
                                                                  10,
                                                                  null,
                                                                  null,
                                                                  null,
                                                                  true);
        final PageResponse<JarListPageRow> response = service.listArtifacts(request);
        assertEquals(4,
                     response.getPageRowList().size());
    }

    @Test
    public void testListFilesWithStartBeyondMaximum() throws Exception {
        //Deploy 2 files (equating to 4 files)
        deployArtifact(gavBackend1);
        deployArtifact(gavBackend2);

        final JarListPageRequest request = new JarListPageRequest(10,
                                                                  10,
                                                                  null,
                                                                  null,
                                                                  null,
                                                                  true);
        final PageResponse<JarListPageRow> response = service.listArtifacts(request);
        assertEquals(0,
                     response.getPageRowList().size());
    }

    @Test
    public void testCheckArtifactExistsReturnsTrueForExistingArtifact() {
        deployArtifact(gavBackend);
        assertTrue(repo.containsArtifact(gavBackend));
    }

    @Test
    public void testCheckArtifactExistsReturnsFalseForNonExistingArtifact() {
        assertFalse(repo.containsArtifact(new GAV("org.guvnor:non-existing-jar:1.0.Final")));
    }

    @Test
    public void testGetPomTextRejectsTraversingPaths() {
        service.getPomText("dir/name.jar");
        service.getPomText("dir/name.kjar");
        service.getPomText("dir/name.pom");

        exception.expect(RuntimeException.class);
        service.getPomText("path/../file.pom");
    }

    @Test
    public void testLoadGAVFromJarRejectsTraversingPaths() {
        exception.expect(RuntimeException.class);
        service.loadGAVFromJar("path/../file.jar");
    }

    private PageResponse<JarListPageRow> assertFilesCount(final String filters,
                                                          final List<String> fileFormats,
                                                          final String dataSourceName,
                                                          final boolean isAscending,
                                                          final int filesCount) {
        final JarListPageRequest request = new JarListPageRequest(0,
                                                                  null,
                                                                  filters,
                                                                  fileFormats,
                                                                  dataSourceName,
                                                                  isAscending);
        final PageResponse<JarListPageRow> response = service.listArtifacts(request);
        assertEquals(filesCount,
                     response.getPageRowList().size());
        return response;
    }

    private void deployArtifact(GAV gav) {
        //This installs a JAR and a POM
        InputStream is = this.getClass().getResourceAsStream("guvnor-m2repo-editor-backend-test-without-pom.jar");
        repo.deployArtifact(is,
                            gav,
                            false);
    }

    //Create a mock FileItem setting an InputStream to test with
    @SuppressWarnings("serial")
    class MockFileItem implements FileItem {

        private final String fileName;
        private final InputStream fileStream;

        MockFileItem(final String fileName,
                     final InputStream fileStream) {
            this.fileName = fileName;
            this.fileStream = fileStream;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return fileStream;
            //return this.getClass().getResourceAsStream( "guvnor-m2repo-editor-backend-test-without-pom.jar" );
        }

        @Override
        public String getContentType() {
            return null;
        }

        @Override
        public String getName() {
            return fileName;
        }

        @Override
        public boolean isInMemory() {
            return false;
        }

        @Override
        public long getSize() {
            return 0;
        }

        @Override
        public byte[] get() {
            return null;
        }

        @Override
        public String getString(String encoding) throws UnsupportedEncodingException {
            return null;
        }

        @Override
        public String getString() {
            return null;
        }

        @Override
        public void write(File file) throws Exception {
        }

        @Override
        public void delete() {
        }

        @Override
        public String getFieldName() {
            return null;
        }

        @Override
        public void setFieldName(String name) {
        }

        @Override
        public boolean isFormField() {
            return false;
        }

        @Override
        public void setFormField(boolean state) {
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            return null;
        }

        @Override
        public FileItemHeaders getHeaders() {
            return null;
        }

        @Override
        public void setHeaders(FileItemHeaders fileItemHeaders) {
        }
    }
}
