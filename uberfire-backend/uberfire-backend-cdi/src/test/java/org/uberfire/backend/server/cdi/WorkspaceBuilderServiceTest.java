/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.backend.server.cdi;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.cdi.model.WorkspaceImpl;
import org.uberfire.backend.server.cdi.workspace.WorkspaceManager;
import org.uberfire.backend.server.cdi.workspace.WorkspaceNameResolver;
import org.uberfire.backend.server.cdi.workspace.WorkspaceScopedExtension;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProvider;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.rpc.impl.SessionInfoImpl;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class WorkspaceBuilderServiceTest {

    @Deployment
    public static JavaArchive createDeployment() {

        System.setProperty("errai.marshalling.force_static_marshallers",
                           Boolean.toString(true));

        return ShrinkWrap.create(JavaArchive.class)
                .addPackages(true,
                             "org.uberfire.preferences")
                .addPackages(true,
                             "org.uberfire.backend.server.spaces")
                .addPackages(true,
                             "org.uberfire.mvp")
                .addPackages(true,
                             "org.uberfire.commons")
                .addPackages(true,
                             "org.uberfire.backend.java")
                .addPackages(true,
                             "org.uberfire.backend.server.cdi")
                .addPackages(true,
                             "org.uberfire.backend.server.cluster")
                .addPackages(true,
                             "org.uberfire.backend.server.io")
                .addPackages(true,
                             "org.uberfire.java.nio.fs.jgit")
                .addClass(JGitFileSystemProvider.class)
                .addAsManifestResource(EmptyAsset.INSTANCE,
                                       "beans.xml")
                .addAsResource("META-INF/ErraiApp.properties",
                               "ErraiApp.properties")
                .addAsManifestResource("META-INF/services/org.uberfire.java.nio.file.spi.FileSystemProvider",
                                       "services/org.uberfire.java.nio.file.spi.FileSystemProvider")
                .addAsServiceProvider(Extension.class,
                                      WorkspaceScopedExtension.class);
    }

    @Inject
    private WorkspaceManager workspaceManager;

    @Inject
    SessionBasedBean bean;

    @Inject
    WorkspaceBuilderService workspaceBuilderService;

    @Produces
    protected Logger createLogger(InjectionPoint injectionPoint) {
        return LoggerFactory.getLogger(injectionPoint.getMember().getDeclaringClass().getSimpleName());
    }

    @Produces
    protected SessionInfo createSessionInfo(InjectionPoint injectionPoint) {
        return new SessionInfoImpl(new UserImpl(Thread.currentThread().getName()));
    }

    @Before
    public void setUp() {
    }

    @Test
    public void testConcurrentWorkspaceBeans() {

        String THREAD_NAME_2 = "ray vaughan";
        String THREAD_NAME_1 = "hendrix";

        CountDownLatch latch = new CountDownLatch(2);

        Thread thread1 = createThread(bean,
                                      "a:b:c",
                                      latch);
        Thread thread2 = createThread(bean,
                                      "d:e:f",
                                      latch);

        thread1.setName(THREAD_NAME_1);
        thread2.setName(THREAD_NAME_2);
        thread1.start();
        thread2.start();

        try {
            latch.await(7000,
                        TimeUnit.SECONDS);
            final WorkspaceImpl workspace1 = (WorkspaceImpl) workspaceManager.getWorkspace(THREAD_NAME_1);
            assertEquals(1,
                         workspaceManager.getBeansCount(workspace1));

            final WorkspaceImpl workspace2 = (WorkspaceImpl) workspaceManager.getWorkspace(THREAD_NAME_2);
            assertEquals(1,
                         workspaceManager.getBeansCount(workspace2));

            assertEquals(2,
                         workspaceManager.getWorkspaceCount());
        } catch (InterruptedException e) {
            fail();
        }
    }

    private Thread createThread(final SessionBasedBean bean,
                                final String gav,
                                final CountDownLatch latch) {
        return new Thread(() -> {
            bean.build(gav);
            latch.countDown();
        });
    }
}
