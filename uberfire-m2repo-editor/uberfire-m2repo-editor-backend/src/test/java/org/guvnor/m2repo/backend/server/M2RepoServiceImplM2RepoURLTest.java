/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import org.guvnor.m2repo.backend.server.repositories.ArtifactRepositoryService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class M2RepoServiceImplM2RepoURLTest {

    @Mock
    private GuvnorM2Repository repository;
    @Mock
    private Logger logger;

    private M2RepoServiceImpl m2RepoService;

    @Before
    public void setUp() throws Exception {
        m2RepoService = new M2RepoServiceImpl(logger,
                                              repository);
        doReturn("file://path-to-m2").when(repository).getRepositoryURL(ArtifactRepositoryService.GLOBAL_M2_REPO_NAME);
    }

    @After
    public void tearDown() throws Exception {
        System.clearProperty(ArtifactRepositoryService.GLOBAL_M2_REPO_URL);
    }

    @Test
    public void errorDisplayedWhenPropertyIsNotSet() throws Exception {
        verify(logger).error(anyString());
    }

    @Test
    public void localRepoURL() throws Exception {
        assertEquals("file://path-to-m2", m2RepoService.getRepositoryURL());
    }

    @Test
    public void repoURLFromSystemProperty() throws Exception {

        System.setProperty(ArtifactRepositoryService.GLOBAL_M2_REPO_URL, "http://my-url");

        assertEquals("http://my-url", m2RepoService.getRepositoryURL());
    }

    @Test
    public void repoURLFromSystemPropertyEmptyValue() throws Exception {
        reset(logger);

        System.setProperty(ArtifactRepositoryService.GLOBAL_M2_REPO_URL, "");

        assertEquals("file://path-to-m2", m2RepoService.getRepositoryURL());
        verify(logger).warn(anyString());
    }

    @Test
    public void repoURLFromSystemPropertyInvalidURL() throws Exception {
        reset(logger);

        System.setProperty(ArtifactRepositoryService.GLOBAL_M2_REPO_URL, "12345");

        assertEquals("file://path-to-m2", m2RepoService.getRepositoryURL());
        verify(logger).warn(anyString());
    }

    @Test
    public void testGetKModuleAndKieDeploymentDescriptorText() throws Exception {
        File jarFile = new File("src/test/resources/org/guvnor/m2repo/backend/server/evaluation-12.1.1.jar");

        when(repository.getKieDeploymentDescriptorText("evaluation-12.1.1.jar"))
                .thenReturn(GuvnorM2Repository.loadFileTextFromJar(jarFile, "META-INF", "kie-deployment-descriptor.xml"));
        when(repository.getKModuleText("evaluation-12.1.1.jar"))
                .thenReturn(GuvnorM2Repository.loadFileTextFromJar(jarFile, "META-INF", "kmodule.xml"));

        assertNotNull(m2RepoService.getKModuleText("evaluation-12.1.1.jar"));
        assertNotNull(m2RepoService.getKieDeploymentDescriptorText("evaluation-12.1.1.jar"));
    }
}
