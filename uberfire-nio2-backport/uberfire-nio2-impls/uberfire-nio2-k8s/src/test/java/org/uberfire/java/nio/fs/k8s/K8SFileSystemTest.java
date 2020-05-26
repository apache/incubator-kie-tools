/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.java.nio.fs.k8s;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import com.google.common.collect.Lists;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesCrudDispatcher;
import io.fabric8.kubernetes.client.server.mock.KubernetesMockServer;
import io.fabric8.mockwebserver.Context;
import io.fabric8.mockwebserver.ServerRequest;
import io.fabric8.mockwebserver.ServerResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.InvalidPathException;
import org.uberfire.java.nio.file.NoSuchFileException;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.spi.FileSystemProvider;
import org.uberfire.java.nio.fs.cloud.CloudClientConstants;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.uberfire.java.nio.fs.k8s.K8SFileSystemConstants.CFG_MAP_ANNOTATION_FSOBJ_LAST_MODIFIED_TIMESTAMP_KEY;
import static org.uberfire.java.nio.fs.k8s.K8SFileSystemConstants.CFG_MAP_ANNOTATION_FSOBJ_SIZE_KEY;
import static org.uberfire.java.nio.fs.k8s.K8SFileSystemConstants.CFG_MAP_FSOBJ_CONTENT_KEY;
import static org.uberfire.java.nio.fs.k8s.K8SFileSystemConstants.CFG_MAP_LABEL_FSOBJ_TYPE_KEY;
import static org.uberfire.java.nio.fs.k8s.K8SFileSystemUtils.createOrReplaceFSCM;
import static org.uberfire.java.nio.fs.k8s.K8SFileSystemUtils.createOrReplaceParentDirFSCM;
import static org.uberfire.java.nio.fs.k8s.K8SFileSystemUtils.getCreationTime;
import static org.uberfire.java.nio.fs.k8s.K8SFileSystemUtils.getFsObjCM;
import static org.uberfire.java.nio.fs.k8s.K8SFileSystemUtils.getFsObjContentBytes;
import static org.uberfire.java.nio.fs.k8s.K8SFileSystemUtils.getFsObjNameElementLabel;
import static org.uberfire.java.nio.fs.k8s.K8SFileSystemUtils.getPathByFsObjCM;
import static org.uberfire.java.nio.fs.k8s.K8SFileSystemUtils.getSize;
import static org.uberfire.java.nio.fs.k8s.K8SFileSystemUtils.isDirectory;
import static org.uberfire.java.nio.fs.k8s.K8SFileSystemUtils.isFile;
import static org.uberfire.java.nio.fs.k8s.K8SFileSystemUtils.validateAndBuildPathLabel;

public class K8SFileSystemTest {

    public static KubernetesMockServer SERVER =
            new KubernetesMockServer(new Context(), new MockWebServer(), new HashMap<ServerRequest, Queue<ServerResponse>>(), new KubernetesCrudDispatcher(), false);
    // The default namespace for MockKubernetes Server is 'test'
    protected static String TEST_NAMESPACE = "test";
    protected static ThreadLocal<KubernetesClient> CLIENT_FACTORY;

    protected static final FileSystemProvider fsProvider = new K8SFileSystemProvider() {

            @Override
            public KubernetesClient createKubernetesClient() {
                return CLIENT_FACTORY.get();
            }
    };

    protected String newFileWithContent(final Path newFile, final String testFileContent) throws IOException {
        Files.createFile(newFile);
        try (BufferedWriter writer = Files.newBufferedWriter(newFile, StandardCharsets.UTF_8)) {
            writer.write(testFileContent, 0, testFileContent.length());
        }
        return testFileContent;
    }

    @BeforeClass
    public static void setup() {
        SERVER.init();
        CLIENT_FACTORY = ThreadLocal.withInitial(() -> SERVER.createClient());
        //Checking the operating system before test execution
        Assume.assumeFalse("k8s does not support in Windows platform", System.getProperty("os.name").toLowerCase().contains("windows"));
        // Load testing KieServerState ConfigMap data into mock server from file
        CLIENT_FACTORY.get()
                      .configMaps()
                      .inNamespace(TEST_NAMESPACE)
                      .createOrReplace(CLIENT_FACTORY.get().configMaps()
                                                     .load(K8SFileSystemTest.class.getResourceAsStream("/test-k8sfs-dir-r-configmap.yml"))
                                                     .get());
        CLIENT_FACTORY.get()
                      .configMaps()
                      .inNamespace(TEST_NAMESPACE)
                      .createOrReplace(CLIENT_FACTORY.get().configMaps()
                                                     .load(K8SFileSystemTest.class.getResourceAsStream("/test-k8sfs-dir-0-configmap.yml"))
                                                     .get());
        CLIENT_FACTORY.get()
                      .configMaps()
                      .inNamespace(TEST_NAMESPACE)
                      .createOrReplace(CLIENT_FACTORY.get().configMaps()
                                                     .load(K8SFileSystemTest.class.getResourceAsStream("/test-k8sfs-file-configmap.yml"))
                                                     .get());
        CLIENT_FACTORY.get()
                      .configMaps()
                      .inNamespace(TEST_NAMESPACE)
                      .createOrReplace(CLIENT_FACTORY.get().configMaps()
                                                     .load(K8SFileSystemTest.class.getResourceAsStream("/test-k8sfs-dir-00-configmap.yml"))
                                                     .get());
    }

    @AfterClass
    public static void tearDown() {
        CLIENT_FACTORY.get().configMaps().inNamespace(TEST_NAMESPACE).delete();
        CLIENT_FACTORY.get().close();
        SERVER.destroy();
    }
    
    @Test
    public void testSetup() {
        final FileSystem fileSystem = fsProvider.getFileSystem(URI.create("default:///"));
        final Path root = fileSystem.getPath("/");
        assertThat(root.getFileSystem().provider()).isEqualTo(fsProvider);
    }

    @Test
    public void testGetCMByName() {
        assertThat(CLIENT_FACTORY.get().configMaps().inNamespace(TEST_NAMESPACE)
                                 .withName("dummy").get()).isNull();
        assertThat(CLIENT_FACTORY.get().configMaps().inNamespace(TEST_NAMESPACE)
                                 .withName("k8s-fsobj-86403b0c-78b7-11e9-ad76-8c16458eff35").get()).isNotNull();
        assertThat(CLIENT_FACTORY.get().configMaps().inNamespace(TEST_NAMESPACE)
                                 .withName("k8s-fsobj-e6bb5ba5-527f-11e9-8a93-8c16458eff35").get()).isNotNull();
    }
    
    @Test 
    public void testCreateOrReplaceFSCM() throws IOException {
        final FileSystem fileSystem = fsProvider.getFileSystem(URI.create("default:///"));
        final Path testDir = fileSystem.getPath("/testCreateOrReplaceFSCMDir");
        final Path testFile = fileSystem.getPath("/testCreateOrReplaceFSCMDir/testCreateOrReplaceFSCMFile");
        
        // Create a new empty dir under root
        createOrReplaceFSCM(CLIENT_FACTORY.get(), 
                            testDir,
                            createOrReplaceParentDirFSCM(CLIENT_FACTORY.get(), testDir, 0L, false),
                            Collections.emptyMap(),
                            true);
        ConfigMap testDirCM = getFsObjCM(CLIENT_FACTORY.get(), testDir);
        
        ConfigMap rootCM = CLIENT_FACTORY.get().configMaps().inNamespace(TEST_NAMESPACE)
                .withName("k8s-fsobj-e6bb5ba5-527f-11e9-8a93-8c16458eff35").get();

        // Check CM data of the empty dir
        assertThat(testDirCM).isNotNull();
        assertThat(testDirCM.getMetadata().getLabels().get("k8s.fs.nio.java.uberfire.org/fsobj-name-0"))
                                                    .isEqualTo("testCreateOrReplaceFSCMDir");
        assertThat(testDirCM.getMetadata().getLabels().get(CFG_MAP_LABEL_FSOBJ_TYPE_KEY))
                                                    .isEqualTo(K8SFileSystemObjectType.DIR.toString());
        assertThat(testDirCM.getMetadata().getAnnotations().get(CFG_MAP_ANNOTATION_FSOBJ_SIZE_KEY))
                                                    .isEqualTo("0");
        assertThat(testDirCM.getData().isEmpty()).isTrue();
        
        // Check the ref-link to the root CM
        assertThat(testDirCM.getMetadata().getOwnerReferences().get(0).getKind())
            .isEqualTo(rootCM.getKind());
        assertThat(testDirCM.getMetadata().getOwnerReferences().get(0).getName())
            .isEqualTo(rootCM.getMetadata().getName());
        
        // Create new file followed by testing write to and read from the file
        String testFileContent = "Hello World";
        newFileWithContent(testFile, testFileContent);
        
        ConfigMap newDirCM = getFsObjCM(CLIENT_FACTORY.get(), fileSystem.getPath("/testCreateOrReplaceFSCMDir"));
        ConfigMap newFileCM = getFsObjCM(CLIENT_FACTORY.get(), testFile);
        
        assertThat(newDirCM).isNotNull();
        assertThat(newFileCM).isNotNull();
        assertThat(newDirCM.getMetadata().getAnnotations().get(CFG_MAP_ANNOTATION_FSOBJ_LAST_MODIFIED_TIMESTAMP_KEY))
            .isNotNull();
        assertThat(newFileCM.getMetadata().getAnnotations().get(CFG_MAP_ANNOTATION_FSOBJ_LAST_MODIFIED_TIMESTAMP_KEY))
            .isNotNull();
        assertThat(newFileCM.getData().get(CFG_MAP_FSOBJ_CONTENT_KEY)).isEqualTo(testFileContent);
        assertThat(Files.size(testFile)).isEqualTo(testFileContent.length());
    }

    @Test
    public void testGetFsObjCM() {
        final FileSystem fileSystem = fsProvider.getFileSystem(URI.create("default:///"));
        final Path root = fileSystem.getPath("/");
        final Path dir = fileSystem.getPath("/testDir");
        final Path file = fileSystem.getPath("/testDir/testFile");
        
        ConfigMap rootCM = CLIENT_FACTORY.get().configMaps().inNamespace(TEST_NAMESPACE)
                .withName("k8s-fsobj-e6bb5ba5-527f-11e9-8a93-8c16458eff35").get();
        ConfigMap dirCM = CLIENT_FACTORY.get().configMaps().inNamespace(TEST_NAMESPACE)
                .withName("k8s-fsobj-e6bb5ba5-527f-11e9-8a93-8c16458eff36").get();
        ConfigMap fileCM = CLIENT_FACTORY.get().configMaps().inNamespace(TEST_NAMESPACE)
                .withName("k8s-fsobj-86403b0c-78b7-11e9-ad76-8c16458eff35").get();
        
        assertThat(getFsObjCM(CLIENT_FACTORY.get(), root)).isEqualTo(rootCM);
        assertThat(getFsObjCM(CLIENT_FACTORY.get(), dir)).isEqualTo(dirCM);
        assertThat(getFsObjCM(CLIENT_FACTORY.get(), file)).isEqualTo(fileCM);
    }

    @Test
    public void testGetFsObjContentBytes() {
        ConfigMap fileCM = CLIENT_FACTORY.get().configMaps().inNamespace(TEST_NAMESPACE)
                .withName("k8s-fsobj-86403b0c-78b7-11e9-ad76-8c16458eff35").get();
        
        String fileContent = new String(getFsObjContentBytes(fileCM), 
                                        Charset.forName(CloudClientConstants.ENCODING));
        assertThat(fileContent).isEqualTo("This is a test file");
    }
    
    @Test
    public void testGetFsObjNameElement() {
        final FileSystem fileSystem = fsProvider.getFileSystem(URI.create("default:///"));
        final Path aFile = fileSystem.getPath("/testDir/../testDir/./testFile");
        Map<String, String> ne = getFsObjNameElementLabel(((K8SFileSystemProvider)fsProvider).toAbsoluteRealPath(aFile));
        assertThat(ne.size()).isEqualTo(2);
        assertThat(ne.containsValue("testDir")).isTrue();
        assertThat(ne.containsValue("testFile")).isTrue();
    }

    @Test
    public void testGetSize() {
        ConfigMap cfm = CLIENT_FACTORY.get().configMaps().inNamespace(TEST_NAMESPACE)
                                      .withName("k8s-fsobj-86403b0c-78b7-11e9-ad76-8c16458eff35").get();
        assertThat(getSize(cfm)).isEqualTo(19);
    }

    @Test
    public void testGetCreationTime() {
        ConfigMap cfm = CLIENT_FACTORY.get().configMaps().inNamespace(TEST_NAMESPACE)
                                      .withName("k8s-fsobj-86403b0c-78b7-11e9-ad76-8c16458eff35").get();
        assertThat(getCreationTime(cfm)).isEqualTo(0);
    }

    @Test
    public void testGetPathByFsObjCM() {
        final K8SFileSystem kfs = (K8SFileSystem) fsProvider.getFileSystem(URI.create("default:///"));
        final Path f = kfs.getPath("/testDir/testFile");

        assertThat(f.getRoot()).isNotNull();
        assertThat(f.getNameCount()).isEqualTo(2);
        assertThat(f.getParent()).isEqualTo(kfs.getPath("/testDir"));
        assertThat(f.getName(0).toString()).isEqualTo("testDir");
        assertThat(f.getName(1).toString()).isEqualTo("testFile");
        assertThat(f.toUri().toString()).isEqualTo(fsProvider.getScheme() + ":///testDir/testFile");
        
        ConfigMap rootCM = CLIENT_FACTORY.get().configMaps().inNamespace(TEST_NAMESPACE)
                .withName("k8s-fsobj-e6bb5ba5-527f-11e9-8a93-8c16458eff35").get();
        ConfigMap dirCM = CLIENT_FACTORY.get().configMaps().inNamespace(TEST_NAMESPACE)
                .withName("k8s-fsobj-e6bb5ba5-527f-11e9-8a93-8c16458eff36").get();
        ConfigMap fileCM = CLIENT_FACTORY.get().configMaps().inNamespace(TEST_NAMESPACE)
                .withName("k8s-fsobj-86403b0c-78b7-11e9-ad76-8c16458eff35").get();
        
        assertThat(getPathByFsObjCM(kfs, rootCM)).isEqualTo(kfs.getPath("/"));
        assertThat(getPathByFsObjCM(kfs, dirCM)).isEqualTo(kfs.getPath("/testDir"));
        assertThat(getPathByFsObjCM(kfs, fileCM)).isEqualTo(kfs.getPath("/testDir/testFile"));
    }

    @Test
    public void testIsFile() {
        ConfigMap cfm = CLIENT_FACTORY.get()
                                      .configMaps()
                                      .load(K8SFileSystemTest.class.getResourceAsStream("/test-k8sfs-file-configmap.yml"))
                                      .get();
        assertThat(isFile(cfm)).isTrue();
        assertThat(isDirectory(cfm)).isFalse();
    }

    @Test
    public void testIsDir() {
        ConfigMap cfm = CLIENT_FACTORY.get()
                                      .configMaps()
                                      .load(K8SFileSystemTest.class.getResourceAsStream("/test-k8sfs-dir-0-configmap.yml"))
                                      .get();
        assertThat(isFile(cfm)).isFalse();
        assertThat(isDirectory(cfm)).isTrue();
    }

    @Test
    public void testFileMetadata() {
        final K8SFileSystem kfs = (K8SFileSystem) fsProvider.getFileSystem(URI.create("default:///"));
        final Path d = kfs.getPath("/testDir");
        final Path f = kfs.getPath("/testDir/testFile");
        final Path e = kfs.getPath("/doesNotExist");
        
        assertThat(Files.exists(e)).isFalse();
        assertThat(Files.notExists(e)).isTrue();
        assertThat(Files.isDirectory(d)).isTrue();
        assertThat(Files.isRegularFile(d)).isFalse();
        assertThat(Files.isDirectory(f)).isFalse();
        assertThat(Files.isRegularFile(f)).isTrue();
        
        assertThat(Files.isReadable(f)).isTrue();
        assertThat(Files.isWritable(f)).isTrue();
        assertThat(Files.isExecutable(f)).isFalse();
    }
    
    @Test
    public void testDelete() throws IOException {
        final K8SFileSystem kfs = (K8SFileSystem) fsProvider.getFileSystem(URI.create("default:///"));
        final Path f = kfs.getPath("/testDeleteFile");

        String testFileContent = "Hello World";
        newFileWithContent(f, testFileContent);

        assertThat(Files.exists(f)).isTrue();
        Files.delete(f);
        assertThat(Files.exists(f)).isFalse();
    }

    @Test(expected = NoSuchFileException.class)
    public void testDeleteNotExistingFile() {
        final K8SFileSystem kfs = (K8SFileSystem) fsProvider.getFileSystem(URI.create("default:///"));
        final Path f = kfs.getPath("/testDeleteNotExistingFile");

        Files.delete(f);
    }

    @Test
    public void testDeleteIfExists() throws IOException {
        final K8SFileSystem kfs = (K8SFileSystem) fsProvider.getFileSystem(URI.create("default:///"));
        final Path f = kfs.getPath("/testDeleteIfExists");

        assertThat(Files.deleteIfExists(f)).isFalse();

        String testFileContent = "Hello World";
        newFileWithContent(f, testFileContent);

        assertThat(Files.exists(f)).isTrue();
        assertThat(Files.deleteIfExists(f)).isTrue();
        assertThat(Files.exists(f)).isFalse();
    }

    @Test
    public void testCopy() throws IOException {
        final K8SFileSystem kfs = (K8SFileSystem) fsProvider.getFileSystem(URI.create("default:///"));
        final Path src = kfs.getPath("/testCopySrc");
        final Path target = kfs.getPath("/testCopyTarget");
        
        String testFileContent = "Test copy capability";
        newFileWithContent(src, testFileContent);
        
        Files.copy(src, target);
        
        assertThat(Files.exists(target)).isTrue();
        assertThat(getFsObjCM(CLIENT_FACTORY.get(), target).getData()
                   .get(CFG_MAP_FSOBJ_CONTENT_KEY)).isEqualTo(testFileContent);
    }

    @Test
    public void testMove() throws IOException {
        final K8SFileSystem kfs = (K8SFileSystem) fsProvider.getFileSystem(URI.create("default:///"));
        final Path src = kfs.getPath("/testMoveSrc");
        final Path target = kfs.getPath("/testMoveTarget");
        
        String testFileContent = "Test move capability";
        newFileWithContent(src, testFileContent);
        
        Files.move(src, target);
        
        assertThat(Files.notExists(src)).isTrue();
        assertThat(Files.exists(target)).isTrue();
        assertThat(getFsObjCM(CLIENT_FACTORY.get(), target).getData()
                   .get(CFG_MAP_FSOBJ_CONTENT_KEY)).isEqualTo(testFileContent);
    }

    @Test
    public void testCreateAndReadDir() {
        final K8SFileSystem kfs = (K8SFileSystem) fsProvider.getFileSystem(URI.create("default:///"));
        final Path testDir = kfs.getPath("/testDir");
        final Path testFile = kfs.getPath("/testDir/testFile");

        assertThat(Files.exists(testDir)).isTrue();
        assertThat(Files.isDirectory(testDir)).isTrue();
        assertThat(Files.isHidden(testDir)).isFalse();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(testDir)) {
            ArrayList<Path> dirContent = Lists.newArrayList(stream);
            assertThat(dirContent).asList().containsExactly(testFile);
        }
    }

    @Test
    public void testCreateAndReadHiddenDir() {
        final K8SFileSystem kfs = (K8SFileSystem) fsProvider.getFileSystem(URI.create("default:///"));
        final Path aDir = kfs.getPath("/.testCreateAndReadDir");
        final Path root = aDir.getRoot();
        
        Files.createDirectory(aDir);
        
        assertThat(Files.exists(aDir)).isTrue();
        assertThat(Files.isDirectory(aDir)).isTrue();
        assertThat(Files.isHidden(aDir)).isTrue();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(root)) {
            ArrayList<Path> dirContent = Lists.newArrayList(stream);
            assertThat(dirContent).asList().contains(aDir);
        }
    }

    @Test
    public void testCreateDuplicateDirectory() {
        final K8SFileSystem kfs = (K8SFileSystem) fsProvider.getFileSystem(URI.create("default:///"));
        final Path testDir = kfs.getPath("/testCreateDuplicateDirectory");

        Files.createDirectory(testDir);

        assertThat(Files.exists(testDir)).isTrue();
        assertThat(Files.isDirectory(testDir)).isTrue();

        assertThat(catchThrowable(() -> Files.createDirectory(testDir)))
            .isInstanceOf(org.uberfire.java.nio.file.FileAlreadyExistsException.class);
    }

    @Test
    public void testOverwriteFile() throws IOException {
        final K8SFileSystem kfs = (K8SFileSystem) fsProvider.getFileSystem(URI.create("default:///"));
        final Path testFile = kfs.getPath("/testOverwriteFile");
        final String content = "Large content, blah, blah, blah...";
        final String smallerContent = "Small";
        
        newFileWithContent(testFile, content);
        assertThat(Files.exists(testFile)).isTrue();

        try (BufferedWriter writer = Files.newBufferedWriter(testFile, Charset.forName("UTF-8"))) {
            writer.write(smallerContent, 0, smallerContent.length());
        }
        
        StringBuffer sb = new StringBuffer();
        try (BufferedReader reader = Files.newBufferedReader(testFile, Charset.forName("UTF-8"))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        
        assertThat(sb.toString()).isEqualTo(smallerContent);
        
        try (BufferedWriter writer = Files.newBufferedWriter(testFile, Charset.forName("UTF-8"))) {
            writer.write(content, 0, content.length());
        }
        
        sb = new StringBuffer();
        try (BufferedReader reader = Files.newBufferedReader(testFile, Charset.forName("UTF-8"))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        
        assertThat(sb.toString()).isEqualTo(content);
    }
    
    @Test 
    public void testParentDirShouldBeUpdatedAfterDelete() throws IOException {
        final FileSystem fileSystem = fsProvider.getFileSystem(URI.create("default:///"));
        final Path root = fileSystem.getPath("/");
        final Path testDir = fileSystem.getPath("/testParentDirShouldBeUpdatedAfterDeleteDir");
        final Path testFile = fileSystem.getPath("/testParentDirShouldBeUpdatedAfterDeleteDir/testParentDirShouldBeUpdatedAfterDeleteFile");
        
        newFileWithContent(testFile, "I'm here");
        assertThat(Files.deleteIfExists(testFile)).isTrue();
        assertThat(Files.size(testDir)).isEqualTo(0);
        
        assertThat(getFsObjCM(CLIENT_FACTORY.get(), testDir).getData().isEmpty()).isTrue();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(root)) {
            ArrayList<Path> dirContent = Lists.newArrayList(stream);
            assertThat(dirContent).asList().contains(testDir);
        }
        
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(testDir)) {
            ArrayList<Path> dirContent = Lists.newArrayList(stream);
            assertThat(dirContent).asList().isEmpty();
        }
    }   
    
    @Test
    public void testFileNameValidation() {
        final K8SFileSystem kfs = (K8SFileSystem) fsProvider.getFileSystem(URI.create("default:///"));
        final Path invalid = kfs.getPath("/#weirdFileName$@#^&*");
        final Path hidden = kfs.getPath("/.testFileNameValidation");
        final Path tooLongFileName = kfs.getPath("/testFileNameValidationForTooLongFileNameWhichIsLongerThanSixtyThreeCharacters");
        
        assertThat(catchThrowable(() -> validateAndBuildPathLabel(new HashMap<String, String>(), invalid.getFileName())))
            .isInstanceOf(InvalidPathException.class);
        assertThat(catchThrowable(() -> validateAndBuildPathLabel(new HashMap<String, String>(), hidden.getFileName())))
            .isNull();
        
        assertThat(catchThrowable(() -> newFileWithContent(invalid, "blah...")))
            .isInstanceOf(org.uberfire.java.nio.IOException.class)
            .hasRootCauseInstanceOf(InvalidPathException.class);
        assertThat(catchThrowable(() -> newFileWithContent(tooLongFileName, "blah...")))
            .isInstanceOf(org.uberfire.java.nio.IOException.class)
            .hasRootCauseInstanceOf(InvalidPathException.class);
    }
    
    @Test
    public void testCreateHiddenFile() throws IOException {
        final K8SFileSystem kfs = (K8SFileSystem) fsProvider.getFileSystem(URI.create("default:///"));
        final Path hidden = kfs.getPath("/.testCreateHiddenDir/.testCreateHiddenFile");
        
        newFileWithContent(hidden, "blah...");
        ConfigMap cm = getFsObjCM(CLIENT_FACTORY.get(), hidden);
        assertThat(Files.exists(hidden)).isTrue();
        assertThat(Files.isHidden(hidden)).isTrue();
        assertThat(getPathByFsObjCM(kfs, cm)).isEqualTo(hidden); 
    }

    @Test
    public void testCopyHiddenFile() throws IOException {
        final K8SFileSystem kfs = (K8SFileSystem) fsProvider.getFileSystem(URI.create("default:///"));
        final Path src = kfs.getPath("/.testCopyHiddenFileSrcFile");
        final Path target = kfs.getPath("/.testCopyHiddenFileTargetFile");

        String testFileContent = "Test copy capability";
        newFileWithContent(src, testFileContent);

        Files.copy(src, target);

        assertThat(Files.exists(target)).isTrue();
        assertThat(Files.isHidden(target)).isTrue();
        assertThat(getFsObjCM(CLIENT_FACTORY.get(), target).getData()
                   .get(CFG_MAP_FSOBJ_CONTENT_KEY)).isEqualTo(testFileContent);
    }

    @Test
    public void testMoveHiddenFile() throws IOException {
        final K8SFileSystem kfs = (K8SFileSystem) fsProvider.getFileSystem(URI.create("default:///"));
        final Path src = kfs.getPath("/.testMoveHiddenFileSrc");
        final Path targetPublic = kfs.getPath("/testMoveHiddenFileTargetPublic");
        final Path targetHidden = kfs.getPath("/.testMoveHiddenFileTargetHidden");

        String testFileContent = "Test move capability";
        newFileWithContent(src, testFileContent);

        Files.move(src, targetPublic);

        assertThat(Files.notExists(src)).isTrue();
        assertThat(Files.exists(targetPublic)).isTrue();
        assertThat(Files.isHidden(targetPublic)).isFalse();
        assertThat(getFsObjCM(CLIENT_FACTORY.get(), targetPublic).getData()
                   .get(CFG_MAP_FSOBJ_CONTENT_KEY)).isEqualTo(testFileContent);

        Files.move(targetPublic, targetHidden);

        assertThat(Files.notExists(targetPublic)).isTrue();
        assertThat(Files.exists(targetHidden)).isTrue();
        assertThat(Files.isHidden(targetHidden)).isTrue();
        assertThat(getFsObjCM(CLIENT_FACTORY.get(), targetHidden).getData()
                   .get(CFG_MAP_FSOBJ_CONTENT_KEY)).isEqualTo(testFileContent);
    }

    @Test
    public void testReadNotExistingFile() throws IOException {
        final K8SFileSystem kfs = (K8SFileSystem) fsProvider.getFileSystem(URI.create("default:///"));
        final Path testFile = kfs.getPath("/testReadNotExistingFile");

        assertThat(catchThrowable(() -> Files.newBufferedReader(testFile, Charset.forName("UTF-8"))))
            .isInstanceOf(org.uberfire.java.nio.file.NoSuchFileException.class);
    }

    @Test
    public void testIsHiddenNotExistingFile() {
        final K8SFileSystem kfs = (K8SFileSystem) fsProvider.getFileSystem(URI.create("default:///"));
        final Path testFile = kfs.getPath("/testIsHiddenNotExistingFile");

        assertThat(catchThrowable(() -> Files.isHidden(testFile)))
            .isInstanceOf(org.uberfire.java.nio.IOException.class);
    }

    @Test
    public void testIsHiddenManyNestedFolders() {
        final K8SFileSystem kfs = (K8SFileSystem) fsProvider.getFileSystem(URI.create("default:///"));
        final Path testFolder11 = kfs.getPath("/testIsHiddenManyNestedFolders/1/2/3/4/5/6/7/8/9/10/11");
        final Path testFolder12 = kfs.getPath("/testIsHiddenManyNestedFolders/1/2/3/4/5/6/7/8/9/10/11/.12");

        Files.createDirectory(testFolder12);

        assertThat(Files.isHidden(testFolder11)).isFalse();
        assertThat(Files.isHidden(testFolder12)).isTrue();
    }

    @Test
    public void testGetDirectoryContentManyNestedFolders() {
        final K8SFileSystem kfs = (K8SFileSystem) fsProvider.getFileSystem(URI.create("default:///"));
        final Path testFolder11 = kfs.getPath("/testGetDirectoryContentManyNestedFolders/1/2/3/4/5/6/7/8/9/.10/11");
        final Path testFolder12 = kfs.getPath("/testGetDirectoryContentManyNestedFolders/1/2/3/4/5/6/7/8/9/.10/11/.12");

        Files.createDirectory(testFolder12);

        Path[] directoryContent = ((K8SFileSystemProvider)fsProvider).getDirectoryContent(testFolder11);
        assertThat(directoryContent).contains(testFolder12);
    }
    
    
}
