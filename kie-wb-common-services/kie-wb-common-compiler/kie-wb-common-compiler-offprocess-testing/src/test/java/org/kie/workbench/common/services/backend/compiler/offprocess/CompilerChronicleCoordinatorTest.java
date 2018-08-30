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
package org.kie.workbench.common.services.backend.compiler.offprocess;

import java.util.UUID;

import net.openhft.chronicle.core.io.IOTools;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.workbench.common.services.backend.compiler.CompilationRequest;
import org.kie.workbench.common.services.backend.compiler.CompilationResponse;
import org.kie.workbench.common.services.backend.compiler.TestUtilMaven;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultKieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.WorkspaceCompilationInfo;
import org.kie.workbench.common.services.backend.compiler.offprocess.impl.CompilerIPCCoordinatorImpl;
import org.kie.workbench.common.services.backend.compiler.offprocess.impl.QueueProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class CompilerChronicleCoordinatorTest {

    private static Logger logger = LoggerFactory.getLogger(CompilerChronicleCoordinatorTest.class);
    private static Path prjPath;
    private static String mavenRepo;
    private static String alternateSettingsAbsPath;
    private static String queueName = "offprocess-queue-test";
    private static QueueProvider queueProvider;

    @BeforeClass
    public static void setup() throws Exception{
        queueProvider = new QueueProvider(queueName);
        logger.info("queue on test setup:{}", queueProvider.getAbsolutePath());
        mavenRepo = TestUtilMaven.getMavenRepo();
        System.setProperty("org.uberfire.nio.git.daemon.enabled", "false");
        System.setProperty("org.uberfire.nio.git.ssh.enabled", "false");
        prjPath = Paths.get("file://"+System.getProperty("user.dir")+"/target/test-classes/kjar-2-single-resources");
        alternateSettingsAbsPath = TestUtilMaven.getSettingsFile();
    }

    @AfterClass
    public static void tearDownClass() {
        System.clearProperty("org.uberfire.nio.git.daemon.enabled");
        System.clearProperty("org.uberfire.nio.git.ssh.enabled");
        IOTools.shallowDeleteDirWithFiles(queueProvider.getAbsolutePath());
    }

    @Test
    public void offProcessOneBuildTest() {
        CompilerIPCCoordinator compiler = new CompilerIPCCoordinatorImpl(queueProvider);
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(prjPath);
        String uuid = UUID.randomUUID().toString();
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo,
                                                               info,
                                                               new String[]{
                                                                       MavenCLIArgs.COMPILE,
                                                                       MavenCLIArgs.ALTERNATE_USER_SETTINGS + alternateSettingsAbsPath
                                                               },
                                                               Boolean.FALSE, uuid);
        CompilationResponse res = compiler.compile(req);
        logger.info("offProcessOneBuildTest first build completed");
        assertThat(res).isNotNull();
        assertThat(res.isSuccessful()).isTrue();
        assertThat(res.getMavenOutput()).isNotEmpty();
        DefaultKieCompilationResponse kres = (DefaultKieCompilationResponse) res;
        assertThat(uuid).isEqualToIgnoringCase( kres.getRequestUUID());
    }

    @Test
    public void offProcessTwoBuildTest() {
        CompilerIPCCoordinator compiler = new CompilerIPCCoordinatorImpl(queueProvider);
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(prjPath);

        // First Build
        String uuid = UUID.randomUUID().toString();
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo,
                                                               info,
                                                               new String[]{
                                                                       MavenCLIArgs.COMPILE,
                                                                       MavenCLIArgs.ALTERNATE_USER_SETTINGS + alternateSettingsAbsPath
                                                               },
                                                               Boolean.FALSE, uuid);
        CompilationResponse res = compiler.compile(req);
        logger.info("offProcessTwoBuildTest first build completed");
        assertThat(res).isNotNull();
        assertThat(res.isSuccessful()).isTrue();
        assertThat(res.getMavenOutput()).isNotEmpty();
        DefaultKieCompilationResponse kres = (DefaultKieCompilationResponse) res;
        assertThat(uuid).isEqualToIgnoringCase( kres.getRequestUUID());

        // Second Build
        String secondUuid = UUID.randomUUID().toString();
        DefaultCompilationRequest secondRequest = new DefaultCompilationRequest(mavenRepo,
                                                                                info,
                                                                                new String[]{
                                                                                        MavenCLIArgs.COMPILE,
                                                                                        MavenCLIArgs.ALTERNATE_USER_SETTINGS + alternateSettingsAbsPath
                                                                                },
                                                                                Boolean.FALSE, secondUuid);
        CompilationResponse secondRes = compiler.compile(secondRequest);
        logger.info("offProcessTwoBuildTest second build completed");
        assertThat(secondRes).isNotNull();
        assertThat(secondRes.isSuccessful()).isTrue();
        DefaultKieCompilationResponse secondKres = (DefaultKieCompilationResponse) secondRes;
        assertThat(secondUuid).isEqualToIgnoringCase(secondKres.getRequestUUID());
    }
}