/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.m2repo.backend.server;

import java.io.File;
import java.lang.reflect.Method;

import javax.enterprise.inject.Instance;

import org.apache.commons.io.FileUtils;
import org.guvnor.m2repo.backend.server.helpers.FormData;
import org.guvnor.m2repo.backend.server.helpers.HttpPostHelper;
import org.guvnor.m2repo.backend.server.repositories.ArtifactRepository;
import org.guvnor.m2repo.backend.server.repositories.ArtifactRepositoryProducer;
import org.guvnor.m2repo.backend.server.repositories.ArtifactRepositoryService;
import org.guvnor.m2repo.preferences.ArtifactRepositoryPreference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.cdi.workspace.WorkspaceNameResolver;
import org.uberfire.mocks.MockInstanceImpl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class M2RepoServiceCreator {

    private static final Logger log = LoggerFactory.getLogger(M2RepoServiceCreator.class);

    private GuvnorM2Repository repo;
    private M2RepoServiceImpl service;
    private HttpPostHelper helper;
    private java.lang.reflect.Method helperMethod;

    public GuvnorM2Repository getRepo() {
        return repo;
    }

    public M2RepoServiceImpl getService() {
        return service;
    }

    public HttpPostHelper getHelper() {
        return helper;
    }

    public Method getHelperMethod() {
        return helperMethod;
    }

    M2RepoServiceCreator() throws Exception {
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
                                        repo);
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

    static boolean deleteDir(File dir) {
        try {
            FileUtils.deleteDirectory(dir);
        } catch (Exception e) {
            log.error("Couldn't delete file {}",
                      dir);
            log.error(e.getMessage(),
                      e);
        }

        // The directory is now empty so delete it
        return dir.delete();
    }
}
