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

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.core.rule.KieModuleMetaInfo;
import org.drools.core.rule.TypeMetaInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.kie.api.builder.KieModule;
import org.kie.scanner.KieModuleMetaData;
import org.kie.scanner.KieModuleMetaDataImpl;
import org.kie.workbench.common.services.backend.compiler.AFCompiler;
import org.kie.workbench.common.services.backend.compiler.CompilationRequest;
import org.kie.workbench.common.services.backend.compiler.TestUtilMaven;
import org.kie.workbench.common.services.backend.compiler.configuration.KieDecorator;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.impl.WorkspaceCompilationInfo;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieMavenCompilerFactory;
import org.kie.workbench.common.services.backend.constants.ResourcesConstants;
import org.kie.workbench.common.services.backend.utils.TestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class KieMetadataTest {

    private String mavenRepoPath;
    private String alternateSettingsAbsPath;
    private Path tmpRoot;
    private Logger logger = LoggerFactory.getLogger(KieMetadataTest.class);

    @Rule
    public TestName testName = new TestName();

    @After
    public void tearDown() {
        if (tmpRoot != null) {
            TestUtil.rm(tmpRoot.toFile());
        }
        mavenRepoPath = null;
    }

    @Before
    public void setUp() throws Exception {
        mavenRepoPath = TestUtilMaven.getMavenRepo();
        alternateSettingsAbsPath = TestUtilMaven.getSettingsFile();
        tmpRoot = Files.createTempDirectory("repo");
    }

    @Test //AF-1459 it tooks 30% of the time of the time spent by all module's test (108), alone it took 30 sec
    public void compileAndLoadKieJarMetadataAllResourcesPackagedJar() throws Exception {
        /**
         * If the test fail check if the Drools core classes used, KieModuleMetaInfo and TypeMetaInfo implements Serializable
         * */
        Path temp = TestUtil.createAndCopyToDirectory(tmpRoot, "dummy", ResourcesConstants.KJAR_2_ALL_RESOURCES);
        final AFCompiler compiler = KieMavenCompilerFactory.getCompiler(EnumSet.of(KieDecorator.ENABLE_LOGGING, KieDecorator.STORE_KIE_OBJECTS, KieDecorator.STORE_BUILD_CLASSPATH , KieDecorator.ENABLE_INCREMENTAL_BUILD));
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(temp);
        CompilationRequest req = new DefaultCompilationRequest(mavenRepoPath,
                                                               info,
                                                               new String[]{MavenCLIArgs.COMPILE, MavenCLIArgs.ALTERNATE_USER_SETTINGS + alternateSettingsAbsPath},
                                                               Boolean.FALSE);
        KieCompilationResponse res = (KieCompilationResponse) compiler.compile(req);
        TestUtil.saveMavenLogIfCompilationResponseNotSuccessfull(temp, res, this.getClass(), testName);
        if (!res.isSuccessful()) {
            List<String> msgs = res.getMavenOutput();
            for (String msg : msgs) {
                logger.info(msg);
            }
        }

        assertThat(res.isSuccessful()).isTrue();

        Optional<KieModuleMetaInfo> metaDataOptional = res.getKieModuleMetaInfo();
        assertThat(metaDataOptional).isPresent();
        KieModuleMetaInfo kieModuleMetaInfo = metaDataOptional.get();
        assertThat(kieModuleMetaInfo).isNotNull();

        Map<String, Set<String>> rulesBP = kieModuleMetaInfo.getRulesByPackage();
        assertThat(rulesBP).hasSize(6);
        Map<String, TypeMetaInfo> typesMI = kieModuleMetaInfo.getTypeMetaInfos();
        // This is a "magic number" test and may or may not be valid since changes
        // to the mechanism for generating classes, especially in PMML processing,
        // may cause this value to change.
        assertThat(typesMI).hasSize(22);

        Optional<KieModule> kieModuleOptional = res.getKieModule();
        assertThat(kieModuleOptional).isPresent();

        assertThat(res.getDependenciesAsURI()).hasSize(4);
        KieModule kModule = kieModuleOptional.get();

        KieModuleMetaData kieModuleMetaData = new KieModuleMetaDataImpl((InternalKieModule) kModule,
                                                                        res.getDependenciesAsURI());
        assertThat(kieModuleMetaData).isNotNull();
    }

    @Test
    public void compileAndloadKieJarSingleMetadata() {
        /**
         * If the test fail check if the Drools core classes used, KieModuleMetaInfo and TypeMetaInfo implements Serializable
         * */
        try {
            Path tmp = TestUtil.createAndCopyToDirectory(tmpRoot, "dummy", ResourcesConstants.KJAR_2_SINGLE_RESOURCES);

            final AFCompiler compiler = KieMavenCompilerFactory.getCompiler(EnumSet.of(KieDecorator.ENABLE_LOGGING, KieDecorator.STORE_KIE_OBJECTS, KieDecorator.STORE_BUILD_CLASSPATH, KieDecorator.ENABLE_INCREMENTAL_BUILD ));
            WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(Paths.get(tmp.toUri()));
            CompilationRequest req = new DefaultCompilationRequest(mavenRepoPath,
                                                                   info,
                                                                   new String[]{MavenCLIArgs.COMPILE, MavenCLIArgs.ALTERNATE_USER_SETTINGS + alternateSettingsAbsPath},
                                                                   Boolean.FALSE);
            KieCompilationResponse res = (KieCompilationResponse) compiler.compile(req);
            TestUtil.saveMavenLogIfCompilationResponseNotSuccessfull(tmp, res, this.getClass(), testName);

            if (!res.isSuccessful()) {
                List<String> msgs = res.getMavenOutput();
                for (String msg : msgs) {
                    logger.info(msg);
                }
            }

            assertThat(res.isSuccessful()).isTrue();

            Optional<KieModuleMetaInfo> metaDataOptional = res.getKieModuleMetaInfo();
            assertThat(metaDataOptional).isPresent();
            KieModuleMetaInfo kieModuleMetaInfo = metaDataOptional.get();
            assertThat(kieModuleMetaInfo).isNotNull();

            Optional<KieModule> kieModuleOptional = res.getKieModule();
            assertThat(kieModuleOptional).isPresent();

            assertThat(res.getDependenciesAsURI()).hasSize(4);

            //comment if you want read the log file after the test run
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void compileAndloadKieJarSingleMetadataWithPackagedJar() throws Exception {
        /**
         * If the test fail check if the Drools core classes used, KieModuleMetaInfo and TypeMetaInfo implements Serializable
         * */
        Path tmp = TestUtil.createAndCopyToDirectory(tmpRoot, "dummy", ResourcesConstants.KJAR_2_SINGLE_RESOURCES);

        final AFCompiler compiler = KieMavenCompilerFactory.getCompiler(EnumSet.of(KieDecorator.STORE_KIE_OBJECTS, KieDecorator.STORE_BUILD_CLASSPATH, KieDecorator.ENABLE_INCREMENTAL_BUILD ));
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(Paths.get(tmp.toUri()));
        CompilationRequest req = new DefaultCompilationRequest(mavenRepoPath,
                                                               info,
                                                               new String[]{MavenCLIArgs.COMPILE, MavenCLIArgs.ALTERNATE_USER_SETTINGS + alternateSettingsAbsPath},
                                                               Boolean.FALSE);
        KieCompilationResponse res = (KieCompilationResponse) compiler.compile(req);
        TestUtil.saveMavenLogIfCompilationResponseNotSuccessfull(tmp, res, this.getClass(), testName);

        if (!res.isSuccessful()) {
            List<String> msgs = res.getMavenOutput();
            for (String msg : msgs) {
                logger.info(msg);
            }
        }

        assertThat(res.isSuccessful()).isTrue();

        Optional<KieModuleMetaInfo> metaDataOptional = res.getKieModuleMetaInfo();
        assertThat(metaDataOptional).isPresent();
        KieModuleMetaInfo kieModuleMetaInfo = metaDataOptional.get();
        assertThat(kieModuleMetaInfo).isNotNull();

        Optional<KieModule> kieModuleOptional = res.getKieModule();
        assertThat(kieModuleOptional).isPresent();
        KieModule kModule = kieModuleOptional.get();

        assertThat(res.getDependenciesAsURI()).hasSize(4);

        KieModuleMetaData kieModuleMetaData = new KieModuleMetaDataImpl((InternalKieModule) kModule,
                                                                        res.getDependenciesAsURI());

        assertThat(kieModuleMetaData).isNotNull();

        //comment if you want read the log file after the test run
    }
}

