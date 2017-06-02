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
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.ProjectRepositories;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.GAVAlreadyExistsException;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.project.service.ProjectRepositoriesService;
import org.guvnor.common.services.project.service.ProjectRepositoryResolver;
import org.guvnor.m2repo.backend.server.ExtendedM2RepoService;
import org.guvnor.test.TestFileSystem;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.builder.KieFileSystem;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
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

    private POMService pomService;

    @Mock
    private ExtendedM2RepoService m2RepoService;

    @Mock
    private ProjectRepositoryResolver repositoryResolver;

    @Mock
    private ProjectRepositoriesService projectRepositoriesService;

    @Mock
    private Instance<PostBuildHandler> handlers;

    @Mock
    protected Instance<User> identity;

    @Mock
    protected User user;

    private LRUBuilderCache cache;

    private TestFileSystem testFileSystem;

    private KieProjectService projectService;

    private BuildHelper buildHelper;

    private DeploymentVerifier deploymentVerifier;

    private Path rootPath;

    private Path snapshotRootPath;

    private KieProject project;

    @Mock
    private POM pom;

    @Mock
    private GAV gav;

    @Mock
    private Path repositoriesPath;

    @Mock
    private ProjectRepositories projectRepositories;

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
        projectService = testFileSystem.getReference(KieProjectService.class);
        pomService = testFileSystem.getReference(POMService.class);
        cache = testFileSystem.getReference(LRUBuilderCache.class);
        deploymentVerifier = new DeploymentVerifier(repositoryResolver,
                                                    projectRepositoriesService);
        buildHelper = spy(new BuildHelper(pomService,
                                          m2RepoService,
                                          projectService,
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

        buildHelper.buildAndDeploy(project);

        verify(buildHelper,
               times(1)).buildAndDeploy(eq(project),
                                        eq(DeploymentMode.VALIDATED));
        verifyBuildAndDeploy(project,
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
            buildHelper.buildAndDeploy(project);
        } catch (Exception e) {
            exception = e;
        }

        verify(buildHelper,
               times(1)).buildAndDeploy(eq(project),
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

        buildHelper.buildAndDeploy(project);

        verify(buildHelper,
               times(1)).buildAndDeploy(eq(project),
                                        eq(DeploymentMode.VALIDATED));
        verifyBuildAndDeploySnapshot(project,
                                     gav);
    }

    @Test
    public void testBuildAndDeploySuppressHandlersNonSnapshot() {
        final GAV gav = new GAV(GROUP_ID,
                                ARTIFACT_ID,
                                VERSION);
        prepareBuildAndDeploy(rootPath,
                              gav);

        buildHelper.buildAndDeploy(project,
                                   true);

        verify(buildHelper,
               times(1)).buildAndDeploy(eq(project),
                                        eq(true),
                                        eq(DeploymentMode.VALIDATED));
        verifyBuildAndDeploy(project,
                             gav);
    }

    @Test
    public void testBuildAndDeploySuppressHandlersSnapshot() {
        final GAV gav = new GAV(GROUP_ID,
                                ARTIFACT_ID,
                                SNAPSHOT_VERSION);
        prepareBuildAndDeploy(snapshotRootPath,
                              gav);

        buildHelper.buildAndDeploy(project,
                                   true);

        verify(buildHelper,
               times(1)).buildAndDeploy(eq(project),
                                        eq(true),
                                        eq(DeploymentMode.VALIDATED));
        verifyBuildAndDeploySnapshot(project,
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
        project = projectService.resolveProject(rootPath);

        repositories = new HashSet<>();
        if (isDeployed) {
            repositories.add(repositoryMetadata1);
            repositories.add(repositoryMetadata2);
        }
        when(projectRepositoriesService.load(project.getRepositoriesPath())).thenReturn(projectRepositories);
        when(repositoryResolver.getRepositoriesResolvingArtifact(eq(gav),
                                                                 eq(project),
                                                                 any(MavenRepositoryMetadata[].class))).thenReturn(repositories);
    }

    private void verifyBuildAndDeploy(KieProject project,
                                      GAV gav) {
        verify(projectRepositoriesService,
               times(1)).load(any(Path.class));
        verify(repositoryResolver,
               times(1)).getRepositoriesResolvingArtifact(eq(gav),
                                                          eq(project),
                                                          any(MavenRepositoryMetadata[].class));
        verifyBuilder(project,
                      gav);
    }

    private void verifyBuildAndDeploySnapshot(KieProject project,
                                              GAV gav) {
        verify(projectRepositoriesService,
               never()).load(any(Path.class));
        verify(repositoryResolver,
               never()).getRepositoriesResolvingArtifact(eq(gav),
                                                         eq(project));
        verifyBuilder(project,
                      gav);
    }

    private void verifyBuilder(KieProject project,
                               GAV gav) {
        Builder builder = cache.getBuilder(project);
        assertNotNull(builder);
        assertTrue(builder.isBuilt());
        verify(m2RepoService,
               times(1)).deployJar(any(InputStream.class),
                                   eq(gav));
    }

    @Test
    public void testBuildThatDoesNotUpdateTheCache() throws Exception {
        final Path path = path();

        buildHelper.build(projectService.resolveProject(path));

        assertTrue(cachedFileSystemDoesNotChange());
    }

    @Test
    public void testUpdatePackageResourceThatDoesNotUpdateTheCache() throws Exception {
        final Path path = path();

        buildHelper.build(projectService.resolveProject(path));
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
        final Builder builder = cache.assertBuilder(projectService.resolveProject(path()));
        final KieFileSystem fileSystem = builder.getKieFileSystem();
        final String fileContent = new String(fileSystem.read("src/main/resources/rule2.drl"),
                                              Charsets.UTF_8);

        return fileContent.contains("Bean");
    }
}