/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.backend.builder.core;

import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.enterprise.inject.Instance;

import com.google.common.base.Charsets;
import org.guvnor.common.services.project.builder.service.PostBuildHandler;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.MavenRepositoryMetadata;
import org.guvnor.common.services.project.model.ModuleRepositories;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.GAVAlreadyExistsException;
import org.guvnor.common.services.project.service.ModuleRepositoriesService;
import org.guvnor.common.services.project.service.ModuleRepositoryResolver;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.m2repo.backend.server.ExtendedM2RepoService;
import org.guvnor.test.TestFileSystem;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.builder.KieFileSystem;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BuildHelperTest {

    private static final String GROUP_ID = "org.kie.workbench.common.services.builder.tests";

    private static final String ARTIFACT_ID = "build-helper-test";

    private static final String VERSION = "1.0.0";

    private static final String SNAPSHOT_VERSION = "1.0.0-SNAPSHOT";

    @Mock
    protected Instance<User> identity;
    @Mock
    protected User user;
    private POMService pomService;
    @Mock
    private ExtendedM2RepoService m2RepoService;
    @Mock
    private ModuleRepositoryResolver repositoryResolver;
    @Mock
    private ModuleRepositoriesService moduleRepositoriesService;
    @Mock
    private Instance<PostBuildHandler> handlers;
    private LRUBuilderCache cache;

    private TestFileSystem testFileSystem;

    private KieModuleService moduleService;

    private BuildHelper buildHelper;

    private DeploymentVerifier deploymentVerifier;

    private Path rootPath;

    private Path snapshotRootPath;

    private KieModule module;

    @Mock
    private POM pom;

    @Mock
    private GAV gav;

    @Mock
    private Path repositoriesPath;

    @Mock
    private ModuleRepositories moduleRepositories;

    private Set<MavenRepositoryMetadata> repositories;

    @Mock
    private MavenRepositoryMetadata repositoryMetadata1;

    @Mock
    private MavenRepositoryMetadata repositoryMetadata2;

    @BeforeClass
    public static void setupSystemProperties() {
        //These are not needed for the tests
        System.setProperty("org.uberfire.nio.git.daemon.enabled",
                           "false");
        System.setProperty("org.uberfire.nio.git.ssh.enabled",
                           "false");
        System.setProperty("org.uberfire.sys.repo.monitor.disabled",
                           "true");
    }

    @Before
    public void setUp() throws Exception {
        testFileSystem = new TestFileSystem();
        moduleService = testFileSystem.getReference(KieModuleService.class);
        pomService = testFileSystem.getReference(POMService.class);
        cache = testFileSystem.getReference(LRUBuilderCache.class);
        deploymentVerifier = new DeploymentVerifier(repositoryResolver,
                                                    moduleRepositoriesService);
        buildHelper = spy(new BuildHelper(pomService,
                                          m2RepoService,
                                          moduleService,
                                          deploymentVerifier,
                                          cache,
                                          handlers,
                                          identity));

        when(identity.get()).thenReturn(user);
        when(user.getIdentifier()).thenReturn("test-user");

        URL rootUrl = this.getClass().getResource("/BuildHelperTest");
        rootPath = Paths.convert(testFileSystem.fileSystemProvider.getPath(rootUrl.toURI()));

        rootUrl = this.getClass().getResource("/BuildHelperTestSnapshot");
        snapshotRootPath = Paths.convert(testFileSystem.fileSystemProvider.getPath(rootUrl.toURI()));

        Iterator<PostBuildHandler> mockIterator = mock(Iterator.class);
        when(handlers.iterator()).thenReturn(mockIterator);
        when(mockIterator.hasNext()).thenReturn(false);
    }

    @After
    public void tearDown() throws Exception {
        testFileSystem.tearDown();
    }

    @Test
    public void testBuildAndDeployNonSnapshotNotDeployed() {
        final GAV gav = new GAV(GROUP_ID,
                                ARTIFACT_ID,
                                VERSION);
        prepareBuildAndDeploy(rootPath,
                              gav,
                              false);

        buildHelper.buildAndDeploy(module);

        verify(buildHelper,
               times(1)).buildAndDeploy(eq(module),
                                        eq(DeploymentMode.VALIDATED));
        verifyBuildAndDeploy(module,
                             gav);
    }

    @Test
    public void testBuildAndDeployNonSnapshotAlreadyDeployed() {
        final GAV gav = new GAV(GROUP_ID,
                                ARTIFACT_ID,
                                VERSION);
        prepareBuildAndDeploy(rootPath,
                              gav,
                              true);
        Exception exception = null;
        try {
            buildHelper.buildAndDeploy(module);
        } catch (Exception e) {
            exception = e;
        }

        verify(buildHelper,
               times(1)).buildAndDeploy(eq(module),
                                        eq(DeploymentMode.VALIDATED));

        assertNotNull(exception);
        assertTrue(exception instanceof GAVAlreadyExistsException);
        assertEquals(gav,
                     ((GAVAlreadyExistsException) exception).getGAV());
    }

    @Test
    public void testBuildAndDeploySnapshot() {
        final GAV gav = new GAV(GROUP_ID,
                                ARTIFACT_ID,
                                SNAPSHOT_VERSION);
        prepareBuildAndDeploy(snapshotRootPath,
                              gav);

        buildHelper.buildAndDeploy(module);

        verify(buildHelper,
               times(1)).buildAndDeploy(eq(module),
                                        eq(DeploymentMode.VALIDATED));
        verifyBuildAndDeploySnapshot(module,
                                     gav);
    }

    @Test
    public void testBuildAndDeploySuppressHandlersNonSnapshot() {
        final GAV gav = new GAV(GROUP_ID,
                                ARTIFACT_ID,
                                VERSION);
        prepareBuildAndDeploy(rootPath,
                              gav);

        buildHelper.buildAndDeploy(module,
                                   true);

        verify(buildHelper,
               times(1)).buildAndDeploy(eq(module),
                                        eq(true),
                                        eq(DeploymentMode.VALIDATED));
        verifyBuildAndDeploy(module,
                             gav);
    }

    @Test
    public void testBuildAndDeploySuppressHandlersSnapshot() {
        final GAV gav = new GAV(GROUP_ID,
                                ARTIFACT_ID,
                                SNAPSHOT_VERSION);
        prepareBuildAndDeploy(snapshotRootPath,
                              gav);

        buildHelper.buildAndDeploy(module,
                                   true);

        verify(buildHelper,
               times(1)).buildAndDeploy(eq(module),
                                        eq(true),
                                        eq(DeploymentMode.VALIDATED));
        verifyBuildAndDeploySnapshot(module,
                                     gav);
    }

    private void prepareBuildAndDeploy(Path rootPath,
                                       GAV gav) {
        prepareBuildAndDeploy(rootPath,
                              gav,
                              false);
    }

    private void prepareBuildAndDeploy(Path rootPath,
                                       GAV gav,
                                       boolean isDeployed) {
        module = moduleService.resolveModule(rootPath);

        repositories = new HashSet<>();
        if (isDeployed) {
            repositories.add(repositoryMetadata1);
            repositories.add(repositoryMetadata2);
        }
        when(moduleRepositoriesService.load(module.getRepositoriesPath())).thenReturn(moduleRepositories);
        when(repositoryResolver.getRepositoriesResolvingArtifact(eq(gav),
                                                                 eq(module),
                                                                 any(MavenRepositoryMetadata[].class))).thenReturn(repositories);
    }

    private void verifyBuildAndDeploy(KieModule module,
                                      GAV gav) {
        verify(moduleRepositoriesService,
               times(1)).load(any(Path.class));
        verify(repositoryResolver,
               times(1)).getRepositoriesResolvingArtifact(eq(gav),
                                                          eq(module),
                                                          any(MavenRepositoryMetadata[].class));
        verifyBuilder(module,
                      gav);
    }

    private void verifyBuildAndDeploySnapshot(KieModule module,
                                              GAV gav) {
        verify(moduleRepositoriesService,
               never()).load(any(Path.class));
        verify(repositoryResolver,
               never()).getRepositoriesResolvingArtifact(eq(gav),
                                                         eq(module));
        verifyBuilder(module,
                      gav);
    }

    private void verifyBuilder(KieModule module,
                               GAV gav) {
        Builder builder = cache.getBuilder(module);
        assertNotNull(builder);
        assertTrue(builder.isBuilt());
        verify(m2RepoService,
               times(1)).deployJar(any(InputStream.class),
                                   eq(gav));
    }

    @Test
    public void testBuildThatDoesNotUpdateTheCache() throws Exception {
        final Path path = path();

        buildHelper.build(moduleService.resolveModule(path));

        assertTrue(cachedFileSystemDoesNotChange());
    }

    @Test
    public void testUpdatePackageResourceThatDoesNotUpdateTheCache() throws Exception {
        final Path path = path();

        buildHelper.build(moduleService.resolveModule(path));
        buildHelper.updatePackageResource(path);

        assertTrue(cachedFileSystemDoesNotChange());
    }

    private Path path() throws URISyntaxException {
        final URL urlToValidate = this.getClass().getResource("/GuvnorM2RepoDependencyExample1/src/main/resources/rule2.drl");
        return Paths.convert(testFileSystem.fileSystemProvider.getPath(urlToValidate.toURI()));
    }

    private String content() {
        return "package org.kie.workbench.common.services.builder.tests.test1\n" +
                "\n" +
                "rule R2\n" +
                "when\n" +
                "Ban()\n" +
                "then\n" +
                "end";
    }

    private boolean cachedFileSystemDoesNotChange() throws URISyntaxException {
        final Builder builder = cache.assertBuilder(moduleService.resolveModule(path()));
        final KieFileSystem fileSystem = builder.getKieFileSystem();
        final String fileContent = new String(fileSystem.read("src/main/resources/rule2.drl"),
                                              Charsets.UTF_8);

        return fileContent.contains("Bean");
    }
}