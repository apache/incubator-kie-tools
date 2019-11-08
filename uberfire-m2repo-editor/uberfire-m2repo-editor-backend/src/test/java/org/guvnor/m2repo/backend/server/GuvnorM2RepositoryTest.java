/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.inject.Instance;

import org.appformer.maven.integration.Aether;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.deployment.DeployRequest;
import org.eclipse.aether.installation.InstallRequest;
import org.eclipse.aether.installation.InstallResult;
import org.eclipse.aether.installation.InstallationException;
import org.eclipse.aether.repository.Authentication;
import org.eclipse.aether.repository.RemoteRepository;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.m2repo.backend.server.repositories.ArtifactRepository;
import org.guvnor.m2repo.backend.server.repositories.ArtifactRepositoryProducer;
import org.guvnor.m2repo.backend.server.repositories.ArtifactRepositoryService;
import org.guvnor.m2repo.preferences.ArtifactRepositoryPreference;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.cdi.workspace.WorkspaceNameResolver;
import org.uberfire.mocks.MockInstanceImpl;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Aether.class)
@PowerMockIgnore({"javax.crypto.*", "javax.net.ssl.*", "javax.net.*", "javax.security.auth.x500.X500Principal"})
public class GuvnorM2RepositoryTest {

    public static final String KIE_SETTINGS_CUSTOM_KEY = "kie.maven.settings.custom";
    public static final String SETTINGS_SECURITY_KEY = "settings.security";

    private static final Logger log = LoggerFactory.getLogger(GuvnorM2RepositoryTest.class);
    private static String settingsSecurityOriginalValue;
    private static String kieSettingsCustomOriginalValue;

    private GuvnorM2Repository repo;
    private RepositorySystem repositorySystem = mock(RepositorySystem.class);
    private RepositorySystemSession repositorySystemSession = mock(RepositorySystemSession.class);

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @BeforeClass
    public static void setupClass() throws IOException, URISyntaxException {
        settingsSecurityOriginalValue = System.getProperty(SETTINGS_SECURITY_KEY);
        System.setProperty(SETTINGS_SECURITY_KEY,
                           resolveFilePath("settings-security.xml"));
        kieSettingsCustomOriginalValue = System.getProperty(KIE_SETTINGS_CUSTOM_KEY);
        System.setProperty(KIE_SETTINGS_CUSTOM_KEY,
                           resolveFilePath("settings.xml"));
    }

    private static String resolveFilePath(String value) throws URISyntaxException {
        final URL url = GuvnorM2RepositoryTest.class.getResource(value);
        final URI uri = url.toURI();
        final File f = new File(uri);
        return f.getAbsolutePath();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        nullSafeSetProperty(SETTINGS_SECURITY_KEY,
                            settingsSecurityOriginalValue);
        nullSafeSetProperty(KIE_SETTINGS_CUSTOM_KEY,
                            kieSettingsCustomOriginalValue);
    }

    private static void nullSafeSetProperty(final String propertyKey,
                                            final String propertyValue) {
        System.setProperty(propertyKey,
                           propertyValue == null ? "" : propertyValue);
    }

    @Before
    public void setup() throws Exception {
        log.info("Deleting existing Repositories instance..");
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

        Aether aether = mock(Aether.class);
        when(aether.getSession()).thenReturn(repositorySystemSession);
        when(aether.getSystem()).thenReturn(repositorySystem);

        mockStatic(Aether.class);
        when(Aether.getAether()).thenReturn(aether);

        try {
            when(repositorySystem.install(any(RepositorySystemSession.class),
                                          any(InstallRequest.class)))
                    .thenAnswer(new Answer<InstallResult>() {
                        @Override
                        public InstallResult answer(InvocationOnMock invocation) throws Throwable {
                            return new InstallResult((InstallRequest) invocation.getArguments()[1]);
                        }
                    });
        } catch (InstallationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDeployArtifactWithDeployArtifactDistributionManagement() throws Exception {
        final GAV gav = new GAV("org.kie.guvnor",
                                "guvnor-m2repo-editor-backend",
                                "0.0.1-SNAPSHOT");

        final InputStream is = this.getClass().getResourceAsStream("guvnor-m2repo-editor-backend-test-with-distribution-management.jar");
        repo.deployArtifact(is,
                            gav,
                            true);

        verify(repositorySystem,
               times(1)).deploy(any(RepositorySystemSession.class),
                                argThat(new BaseMatcher<DeployRequest>() {

                                    @Override
                                    public void describeTo(Description description) {
                                    }

                                    @Override
                                    public boolean matches(Object item) {
                                        DeployRequest request = (DeployRequest) item;
                                        return "global-m2-repo".equals(request.getRepository().getId());
                                    }
                                }));
        verify(repositorySystem,
               times(1)).deploy(any(RepositorySystemSession.class),
                                argThat(new BaseMatcher<DeployRequest>() {

                                    @Override
                                    public void describeTo(Description description) {
                                    }

                                    @Override
                                    public boolean matches(Object item) {
                                        DeployRequest request = (DeployRequest) item;
                                        String string = "example.project.http";
                                        RemoteRepository repo = request.getRepository();
                                        boolean equals = string.equals(repo.getId());
                                        if (!equals) {
                                            return false;
                                        }
                                        Authentication auth = repo.getAuthentication();
                                        Class<? extends Authentication> class1 = auth.getClass();
                                        try {
                                            Field declaredField = class1.getDeclaredField("authentications");
                                            declaredField.setAccessible(true);
                                            Authentication[] object = (Authentication[]) declaredField.get(auth);
                                            Authentication authentication = object[1];
                                            Class<? extends Authentication> class2 = authentication.getClass();
                                            boolean equals3 = "SecretAuthentication".equals(class2.getSimpleName());
                                            if (equals3) {
                                                Field valueField = class2.getDeclaredField("value");
                                                valueField.setAccessible(true);
                                                // length of plaintext password, obviously not
                                                // length of encrypted password
                                                assertEquals("Plaintext pw (repopw) length expected.",
                                                             6,
                                                             ((char[]) valueField.get(authentication)).length);
                                            }
                                            return "StringAuthentication".equals(object[0].getClass().getSimpleName()) && equals3;
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            throw new RuntimeException(e);
                                        }
                                    }
                                }));
    }
    
    @Test
    public void testDeployArtifactFilteredOutAllRepositories() throws Exception {
        final GAV gav = new GAV("org.kie.guvnor",
                                "guvnor-m2repo-editor-backend",
                                "0.0.1-SNAPSHOT");

        final InputStream is = this.getClass().getResourceAsStream("guvnor-m2repo-editor-backend-test-with-distribution-management.jar");
        repo.deployArtifact(is,
                            gav,
                            true,
                            (repo) -> false);

        verify(repositorySystem,
               never()).deploy(any(RepositorySystemSession.class),
                                any());
    }
    
    @Test
    public void testContainArtifact() throws Exception {
        final GAV gav = new GAV("org.kie.guvnor",
                                "guvnor-m2repo-editor-backend",
                                "0.0.1-SNAPSHOT");

        
        repo.containsArtifact(gav);

        verify(repositorySystem,
               times(1)).resolveArtifact(any(),
                                        any());
    }
    
    @Test
    public void testContainArtifactFilteredOutAllRepositories() throws Exception {
        final GAV gav = new GAV("org.kie.guvnor",
                                "guvnor-m2repo-editor-backend",
                                "0.0.1-SNAPSHOT");

        
        repo.containsArtifact(gav,
                              (repo) -> false);

        verify(repositorySystem,
               never()).resolveArtifact(any(),
                                        any());
    }

    @Test
    public void testListFilesWithoutParameters() {
        List<String> wildcards = new ArrayList<String>();
        wildcards.add("*.jar");
        wildcards.add("*.kjar");
        wildcards.add("*.pom");

        GuvnorM2Repository spiedRepo = spy(repo);

        doReturn(new ArrayList<String>()).when(spiedRepo).getFiles(Matchers.<List<String>>any());

        spiedRepo.listFiles();
        verify(spiedRepo).getFiles(wildcards);
    }

    @Test
    public void testListFilesWithFilter() {
        final String filter = "filter";

        List<String> wildcards = new ArrayList<String>();
        wildcards.add("*" + filter + "*.jar");
        wildcards.add("*" + filter + "*.kjar");
        wildcards.add("*" + filter + "*.pom");

        GuvnorM2Repository spiedRepo = spy(repo);

        doReturn(new ArrayList<String>()).when(spiedRepo).getFiles(Matchers.<List<String>>any());

        spiedRepo.listFiles(filter);
        verify(spiedRepo).getFiles(wildcards);
    }

    @Test
    public void testListFilesWithFilterAndFileFormats() {
        final String filter = "filter";

        List<String> fileFormats = new ArrayList<String>();
        fileFormats.add("xml");
        fileFormats.add("war");

        List<String> wildcards = new ArrayList<String>();
        wildcards.add("*" + filter + "*.xml");
        wildcards.add("*" + filter + "*.war");

        GuvnorM2Repository spiedRepo = spy(repo);

        doReturn(new ArrayList<String>()).when(spiedRepo).getFiles(Matchers.<List<String>>any());

        spiedRepo.listFiles(filter,
                            fileFormats);
        verify(spiedRepo).getFiles(wildcards);
    }

    @Test
    public void testGetPomTextVerifiesPath() {
        repo.getPomText("dir/name.pom");
        repo.getPomText("dir/name.kjar");
        repo.getPomText("dir/name.jar");

        exception.expect(RuntimeException.class);
        repo.getPomText("dir/name.foo");
    }

    @Test
    public void testLoadFileTextFromJar() {
        File jarFile = new File("src/test/resources/org/guvnor/m2repo/backend/server/evaluation-12.1.1.jar");
        assertNotNull(GuvnorM2Repository.loadFileTextFromJar(jarFile, GuvnorM2Repository.META_INF, GuvnorM2Repository.KIE_DEPLOYMENT_DESCRIPTOR_XML));
        assertNotNull(GuvnorM2Repository.loadFileTextFromJar(jarFile, GuvnorM2Repository.META_INF, GuvnorM2Repository.KMODULE_XML));

        assertNull(GuvnorM2Repository.loadFileTextFromJar(jarFile, GuvnorM2Repository.META_INF, "kie-descriptor.xml"));
        assertNull(GuvnorM2Repository.loadFileTextFromJar(jarFile, GuvnorM2Repository.META_INF, "modu.xml"));

        assertNull(GuvnorM2Repository.loadFileTextFromJar(jarFile, GuvnorM2Repository.META_INF, ""));
        assertNull(GuvnorM2Repository.loadFileTextFromJar(jarFile, GuvnorM2Repository.META_INF, ""));

        assertNull(GuvnorM2Repository.loadFileTextFromJar(jarFile, GuvnorM2Repository.META_INF, null));
        assertNull(GuvnorM2Repository.loadFileTextFromJar(jarFile, GuvnorM2Repository.META_INF, null));
    }
}
