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

package org.guvnor.structure.backend.organizationalunit.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.lib.Repository;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.organizationalunit.config.RepositoryConfiguration;
import org.guvnor.structure.organizationalunit.config.RepositoryInfo;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorageRegistry;
import org.guvnor.structure.organizationalunit.config.SpaceInfo;
import org.guvnor.structure.repositories.EnvironmentParameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.fs.jgit.FileSystemLock;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;
import org.uberfire.java.nio.fs.jgit.util.Git;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SpaceConfigCleanUpTest {

    private SpaceConfigCleanUp spaceConfigCleanUp;

    @Mock
    private OrganizationalUnitService orgUnitService;

    @Mock
    private SpaceConfigStorageRegistry spaceConfigStorageRegistry;

    @Mock
    private Git git;

    @Before
    public void setup() throws IOException {
        spaceConfigCleanUp = spy(new SpaceConfigCleanUp(orgUnitService,
                                                        spaceConfigStorageRegistry));

        doReturn(true).when(spaceConfigCleanUp).isGitDefaultFileSystem();

        doReturn(mock(FileSystemLock.class)).when(spaceConfigCleanUp).createLock(any(File.class));
    }

    @Test
    public void cleanUpSpaceConfigStorageWhenAlreadyDoneTest() throws IOException {
        final SpaceConfigStorageImpl spaceConfigStorage = setupConfigStorage();

        setupMarker(true);

        spaceConfigCleanUp.postConstruct();

        verify(spaceConfigStorage, never()).loadSpaceInfo();
        verify(spaceConfigStorage, never()).saveSpaceInfo(any());
        verify(git, never()).resetWithSquash(anyString());
    }

    @Test
    public void cleanUpSpaceConfigStorageWhenUpdateNotNeededTest() throws IOException {
        final SpaceConfigStorageImpl spaceConfigStorage = setupConfigStorage();

        setupMarker(false);

        final SpaceInfo spaceInfo = mock(SpaceInfo.class);
        doReturn(spaceInfo).when(spaceConfigStorage).loadSpaceInfo();

        final RepositoryInfo repositoryInfo = setupRepositoryInfo(createEnvMap(false));
        doReturn(Collections.nCopies(5, repositoryInfo)).when(spaceInfo).getRepositories();

        spaceConfigCleanUp.postConstruct();

        verify(spaceConfigStorage, never()).saveSpaceInfo(any());
        verify(git, never()).resetWithSquash(anyString());
    }

    @Test
    public void cleanUpSpaceConfigStorageWhenUpdateNeededTest() throws IOException {
        final SpaceConfigStorageImpl spaceConfigStorage = setupConfigStorage();

        setupMarker(false);

        final SpaceInfo spaceInfo = mock(SpaceInfo.class);
        doReturn(spaceInfo).when(spaceConfigStorage).loadSpaceInfo();

        final RepositoryInfo repositoryInfo = setupRepositoryInfo(createEnvMap(true));
        doReturn(Collections.nCopies(5, repositoryInfo)).when(spaceInfo).getRepositories();

        spaceConfigCleanUp.postConstruct();

        verify(spaceConfigStorage).saveSpaceInfo(any());
        verify(git).resetWithSquash(anyString());
    }

    @Test
    public void cleanUpSpaceConfigStorageWhenUpdatePartiallyNeededTest() throws IOException {
        final SpaceConfigStorageImpl spaceConfigStorage = setupConfigStorage();

        setupMarker(false);

        final SpaceInfo spaceInfo = mock(SpaceInfo.class);
        doReturn(spaceInfo).when(spaceConfigStorage).loadSpaceInfo();

        final List<RepositoryInfo> repositoryInfos = new ArrayList<>();
        repositoryInfos.addAll(Collections.nCopies(3, setupRepositoryInfo(createEnvMap(true))));
        repositoryInfos.addAll(Collections.nCopies(3, setupRepositoryInfo(createEnvMap(false))));

        doReturn(repositoryInfos).when(spaceInfo).getRepositories();

        spaceConfigCleanUp.postConstruct();

        verify(spaceConfigStorage).saveSpaceInfo(any());
        verify(git).resetWithSquash(anyString());
    }

    private SpaceConfigStorageImpl setupConfigStorage() {
        final String spaceName = "MySpace";

        final OrganizationalUnit orgUnit = mock(OrganizationalUnit.class);
        doReturn(spaceName).when(orgUnit).getName();
        doReturn(Collections.singletonList(orgUnit)).when(orgUnitService).getAllOrganizationalUnits();

        final SpaceConfigStorageImpl spaceConfigStorage = mock(SpaceConfigStorageImpl.class);
        final Path path = mock(Path.class);
        final JGitFileSystem fs = mock(JGitFileSystem.class);
        final Repository repository = mock(Repository.class);
        final File directory = mock(File.class);

        doReturn(path).when(fs).getPath(anyString());
        doReturn(directory).when(directory).getParentFile();
        doReturn(directory).when(repository).getDirectory();
        doReturn(repository).when(git).getRepository();
        doReturn(git).when(fs).getGit();
        doReturn(fs).when(path).getFileSystem();
        doReturn(path).when(spaceConfigStorage).getPath();
        doReturn(spaceConfigStorage).when(spaceConfigStorageRegistry).get(spaceName);

        return spaceConfigStorage;
    }

    private RepositoryInfo setupRepositoryInfo(final Map<String, Object> envMap) {
        final RepositoryInfo repositoryInfo = mock(RepositoryInfo.class);
        final RepositoryConfiguration configuration = mock(RepositoryConfiguration.class);

        doReturn(envMap).when(configuration).getEnvironment();
        doReturn(configuration).when(repositoryInfo).getConfiguration();

        return repositoryInfo;
    }

    private void setupMarker(final boolean exists) {
        final File marker = mock(File.class);
        doReturn(exists).when(marker).exists();
        doReturn(marker).when(spaceConfigCleanUp).createMarker(any(File.class));
    }

    private Map<String, Object> createEnvMap(final boolean includeCredentials) {
        final Map<String, Object> envMap = new HashMap<>();
        envMap.put("foo", true);
        envMap.put("bar", 1);

        if (includeCredentials) {
            envMap.put(EnvironmentParameters.USER_NAME, "user");
            envMap.put(EnvironmentParameters.PASSWORD, "pw");
            envMap.put(EnvironmentParameters.SECURE_PREFIX + EnvironmentParameters.PASSWORD, "spw");
        }

        return envMap;
    }
}
