/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.backend.compiler.plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.core.rule.KieModuleMetaInfo;
import org.drools.core.rule.TypeMetaInfo;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.builder.KieModule;
import org.kie.scanner.KieModuleMetaData;
import org.kie.scanner.KieModuleMetaDataImpl;
import org.kie.workbench.common.services.backend.compiler.KieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.TestUtil;
import org.kie.workbench.common.services.backend.compiler.configuration.KieDecorator;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs;
import org.kie.workbench.common.services.backend.compiler.internalNIO.InternalNIOCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.internalNIO.InternalNIOKieMavenCompiler;
import org.kie.workbench.common.services.backend.compiler.internalNIO.InternalNIOTestUtil;
import org.kie.workbench.common.services.backend.compiler.internalNIO.InternalNIOWorkspaceCompilationInfo;
import org.kie.workbench.common.services.backend.compiler.internalNIO.impl.InternalNIODefaultCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.internalNIO.impl.kie.InternalNIOKieMavenCompilerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;

public class InternalNIOKieMetadataTest {

    private Path mavenRepo;

    private Logger logger = LoggerFactory.getLogger(InternalNIOKieMetadataTest.class);

    @After
    public void tearDown() {
        mavenRepo = null;
    }

    @Before
    public void setUp() throws Exception {
        mavenRepo = Paths.get(System.getProperty("user.home"),
                              ".m2/repository");

        if (!Files.exists(mavenRepo)) {
            logger.info("Creating a m2_repo into:" + mavenRepo.toString());
            if (!Files.exists(Files.createDirectories(mavenRepo))) {
                throw new Exception("Folder not writable in the project");
            }
        }
    }

    @Test
    public void compileAndLoadKieJarMetadataAllResourcesPackagedJar() throws Exception {
        /**
         * If the test fail check if the Drools core classes used, KieModuleMetaInfo and TypeMetaInfo implements Serializable
         * */
        //compile and install
        Path tmpRoot = Files.createTempDirectory("repo");
        Path tmp = Files.createDirectories(Paths.get(tmpRoot.toString(),
                                                     "dummy"));

        //NIO creation and copy content
        java.nio.file.Path temp = java.nio.file.Files.createDirectories(java.nio.file.Paths.get(tmpRoot.toString(),
                                                                                                "dummy"));
        TestUtil.copyTree(java.nio.file.Paths.get("src/test/projects/kjar-2-all-resources"),
                          temp);
        //end NIO

        InternalNIOKieMavenCompiler compiler = InternalNIOKieMavenCompilerFactory.getCompiler(
                KieDecorator.KIE_AFTER);

        InternalNIOWorkspaceCompilationInfo info = new InternalNIOWorkspaceCompilationInfo(tmp);
        InternalNIOCompilationRequest req = new InternalNIODefaultCompilationRequest(mavenRepo.toAbsolutePath().toString(),
                                                                                     info,
                                                                                     new String[]{MavenCLIArgs.INSTALL},
                                                                                     new HashMap<>(),
                                                                                     Boolean.FALSE);
        KieCompilationResponse res = compiler.compileSync(req);

        if (res.getMavenOutput().isPresent() && !res.isSuccessful()) {
            TestUtil.writeMavenOutputIntoTargetFolder(res.getMavenOutput().get(),
                                                      "InternalNIOKieMetadataTest.compileAndLoadKieJarMetadataAllResourcesPackagedJar");
        }

        if (res.getErrorMessage().isPresent()) {
            logger.info("Error:" + res.getErrorMessage().get());
        }

        Assert.assertTrue(res.isSuccessful());

        Optional<KieModuleMetaInfo> metaDataOptional = res.getKieModuleMetaInfo();
        Assert.assertTrue(metaDataOptional.isPresent());
        KieModuleMetaInfo kieModuleMetaInfo = metaDataOptional.get();
        Assert.assertNotNull(kieModuleMetaInfo);

        Map<String, Set<String>> rulesBP = kieModuleMetaInfo.getRulesByPackage();
        Assert.assertEquals(rulesBP.size(),
                            8);
        Map<String, TypeMetaInfo> typesMI = kieModuleMetaInfo.getTypeMetaInfos();
        Assert.assertEquals(typesMI.size(),
                            35);

        Optional<KieModule> kieModuleOptional = res.getKieModule();
        Assert.assertTrue(kieModuleOptional.isPresent());

        Assert.assertTrue(res.getProjectDependencies().isPresent());
        Assert.assertTrue(res.getProjectDependencies().get().size() == 5);
        KieModule kModule = kieModuleOptional.get();

        KieModuleMetaData kieModuleMetaData = new KieModuleMetaDataImpl((InternalKieModule) kModule,
                                                                        res.getProjectDependencies().get());
        Assert.assertNotNull(kieModuleMetaData);
        //comment if you want read the log file after the test run
        InternalNIOTestUtil.rm(tmpRoot.toFile());
    }

    @Test
    public void compileAndloadKieJarSingleMetadata() {
        /**
         * If the test fail check if the Drools core classes used, KieModuleMetaInfo and TypeMetaInfo implements Serializable
         * */

        try {
            java.nio.file.Path tmpRoot = java.nio.file.Files.createTempDirectory("repo");
            java.nio.file.Path tmp = java.nio.file.Files.createDirectories(java.nio.file.Paths.get(tmpRoot.toString(),
                                                                                                   "dummy"));
            TestUtil.copyTree(java.nio.file.Paths.get("src/test/projects/kjar-2-single-resources"),
                              tmp);

            InternalNIOKieMavenCompiler compiler = InternalNIOKieMavenCompilerFactory.getCompiler(
                    KieDecorator.KIE_AND_LOG_AFTER);

            InternalNIOWorkspaceCompilationInfo info = new InternalNIOWorkspaceCompilationInfo(Paths.get(tmp.toUri()));

            InternalNIOCompilationRequest req = new InternalNIODefaultCompilationRequest(mavenRepo.toAbsolutePath().toString(),
                                                                                         info,
                                                                                         new String[]{MavenCLIArgs.INSTALL},
                                                                                         new HashMap<>(),
                                                                                         Boolean.TRUE);
            KieCompilationResponse res = compiler.compileSync(req);

            if (res.getMavenOutput().isPresent() && !res.isSuccessful()) {
                TestUtil.writeMavenOutputIntoTargetFolder(res.getMavenOutput().get(),
                                                          "InternalNIOKieMetadataTest.compileAndloadKieJarSingleMetadata");
            }

            Assert.assertTrue(res.getMavenOutput().isPresent());
            if (res.getErrorMessage().isPresent()) {
                logger.info(res.getErrorMessage().get());
            }

            Assert.assertTrue(res.isSuccessful());

            Optional<KieModuleMetaInfo> metaDataOptional = res.getKieModuleMetaInfo();
            Assert.assertTrue(metaDataOptional.isPresent());
            KieModuleMetaInfo kieModuleMetaInfo = metaDataOptional.get();
            Assert.assertNotNull(kieModuleMetaInfo);

            Map<String, Set<String>> rulesBP = kieModuleMetaInfo.getRulesByPackage();
            Assert.assertEquals(rulesBP.size(),
                                1);

            Optional<KieModule> kieModuleOptional = res.getKieModule();
            Assert.assertTrue(kieModuleOptional.isPresent());

            Assert.assertTrue(res.getProjectDependencies().isPresent());
            Assert.assertTrue(res.getProjectDependencies().get().size() == 5);

            //comment if you want read the log file after the test run
            TestUtil.rm(tmpRoot.toFile());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void compileAndloadKieJarSingleMetadataWithPackagedJar() throws Exception {
        /**
         * If the test fail check if the Drools core classes used, KieModuleMetaInfo and TypeMetaInfo implements Serializable
         * */
        java.nio.file.Path tmpRoot = java.nio.file.Files.createTempDirectory("repo");
        java.nio.file.Path tmp = java.nio.file.Files.createDirectories(java.nio.file.Paths.get(tmpRoot.toString(),
                                                                                               "dummy"));
        TestUtil.copyTree(java.nio.file.Paths.get("src/test/projects/kjar-2-single-resources"),
                          tmp);

        InternalNIOKieMavenCompiler compiler = InternalNIOKieMavenCompilerFactory.getCompiler(KieDecorator.KIE_AFTER);

        InternalNIOWorkspaceCompilationInfo info = new InternalNIOWorkspaceCompilationInfo(Paths.get(tmp.toUri()));
        InternalNIOCompilationRequest req = new InternalNIODefaultCompilationRequest(mavenRepo.toAbsolutePath().toString(),
                                                                                     info,
                                                                                     new String[]{MavenCLIArgs.INSTALL},
                                                                                     new HashMap<>(),
                                                                                     Boolean.FALSE);
        KieCompilationResponse res = compiler.compileSync(req);
        if (res.getMavenOutput().isPresent() && !res.isSuccessful()) {
            TestUtil.writeMavenOutputIntoTargetFolder(res.getMavenOutput().get(),
                                                      "InternalNIOKieMetadataTest.compileAndloadKieJarSingleMetadataWithPackagedJar");
        }
        if (res.getErrorMessage().isPresent()) {
            logger.info(res.getErrorMessage().get());
        }

        Assert.assertTrue(res.isSuccessful());

        Optional<KieModuleMetaInfo> metaDataOptional = res.getKieModuleMetaInfo();
        Assert.assertTrue(metaDataOptional.isPresent());
        KieModuleMetaInfo kieModuleMetaInfo = metaDataOptional.get();
        Assert.assertNotNull(kieModuleMetaInfo);

        Map<String, Set<String>> rulesBP = kieModuleMetaInfo.getRulesByPackage();
        Assert.assertEquals(rulesBP.size(),
                            1);

        Optional<KieModule> kieModuleOptional = res.getKieModule();
        Assert.assertTrue(kieModuleOptional.isPresent());
        KieModule kModule = kieModuleOptional.get();

        Assert.assertTrue(res.getProjectDependencies().isPresent());
        Assert.assertTrue(res.getProjectDependencies().get().size() == 5);

        KieModuleMetaData kieModuleMetaData = new KieModuleMetaDataImpl((InternalKieModule) kModule,
                                                                        res.getProjectDependencies().get());

        //KieModuleMetaData kieModuleMetaData = KieModuleMetaData.Factory.newKieModuleMetaData(kModule); // broken
        Assert.assertNotNull(kieModuleMetaData);

        //comment if you want read the log file after the test run
        TestUtil.rm(tmpRoot.toFile());
    }
}

