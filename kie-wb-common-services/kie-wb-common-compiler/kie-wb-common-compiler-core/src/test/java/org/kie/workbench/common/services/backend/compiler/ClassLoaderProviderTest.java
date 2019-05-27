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

package org.kie.workbench.common.services.backend.compiler;

import java.io.IOException;
import java.net.URLClassLoader;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.core.rule.KieModuleMetaInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.kie.api.builder.KieModule;
import org.kie.scanner.KieModuleMetaData;
import org.kie.scanner.KieModuleMetaDataImpl;
import org.kie.workbench.common.services.backend.compiler.configuration.KieDecorator;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.impl.WorkspaceCompilationInfo;
import org.kie.workbench.common.services.backend.compiler.impl.classloader.CompilerClassloaderUtils;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieMavenCompilerFactory;
import org.kie.workbench.common.services.backend.compiler.impl.utils.MavenUtils;
import org.kie.workbench.common.services.backend.compiler.utils.MavenUtilsTest;
import org.kie.workbench.common.services.backend.constants.ResourcesConstants;
import org.kie.workbench.common.services.backend.utils.LoadProjectDependencyUtil;
import org.kie.workbench.common.services.backend.utils.TestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class ClassLoaderProviderTest {

    private String mavenRepoPath;
    private Path tmpRoot;
    private Path tmp;
    private Path uberfireTmp;
    private final String MAVEN_MAIN_SKIP = "maven.main.skip";
    private Logger logger = LoggerFactory.getLogger(ClassLoaderProviderTest.class);

    @Rule
    public TestName testName = new TestName();

    @Before
    public void setUp() throws Exception {
        mavenRepoPath = TestUtilMaven.getMavenRepo();
    }

    @After
    public void clean() {
        System.clearProperty(MAVEN_MAIN_SKIP);
        if (tmpRoot != null) {
            TestUtil.rm(tmpRoot.toFile());
        }
    }

    private CompilationResponse compileProjectInRepo(String... mavenPhases) throws IOException {
        //we use NIO for this part of the test because Uberfire lack the implementation to copy a tree
        tmpRoot = Files.createTempDirectory("repo");
        tmp = TestUtil.createAndCopyToDirectory(tmpRoot, "dummy", ResourcesConstants.DUMMY_KIE_MULTIMODULE_CLASSLOADER_DIR);

        uberfireTmp = Paths.get(tmp.toAbsolutePath().toString());
        final AFCompiler compiler = KieMavenCompilerFactory.getCompiler(new HashSet<>());
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(uberfireTmp);
        CompilationRequest req = new DefaultCompilationRequest(mavenRepoPath,
                                                               info,
                                                               mavenPhases,
                                                               Boolean.FALSE);
        return compiler.compile(req);
    }

    @Test
    public void loadProjectClassloaderTest() throws Exception {
        CompilationResponse res = compileProjectInRepo(MavenCLIArgs.COMPILE, MavenCLIArgs.ALTERNATE_USER_SETTINGS + TestUtilMaven.getSettingsFile());
        TestUtil.saveMavenLogIfCompilationResponseNotSuccessfull(tmp, res, this.getClass(), testName);
        assertThat(res.isSuccessful()).isTrue();

        List<String> pomList = MavenUtils.searchPoms(Paths.get(ResourcesConstants.DUMMY_KIE_MULTIMODULE_CLASSLOADER_DIR));
        Optional<ClassLoader> clazzLoader = CompilerClassloaderUtils.loadDependenciesClassloaderFromProject(pomList,
                                                                                                            mavenRepoPath);
        assertThat(clazzLoader).isPresent();

        LoadProjectDependencyUtil.loadLoggerFactory(clazzLoader.get());
    }

    @Test
    public void loadProjectClassloaderFromStringTest() throws Exception {
        CompilationResponse res = compileProjectInRepo(MavenCLIArgs.COMPILE, MavenCLIArgs.ALTERNATE_USER_SETTINGS + TestUtilMaven.getSettingsFile());
        TestUtil.saveMavenLogIfCompilationResponseNotSuccessfull(tmp, res, this.getClass(), testName);

        assertThat(res.isSuccessful()).isTrue();

        Optional<ClassLoader> clazzLoader = CompilerClassloaderUtils.loadDependenciesClassloaderFromProject(uberfireTmp.toAbsolutePath().toString(),
                                                                                                            mavenRepoPath);
        assertThat(clazzLoader).isPresent();

        LoadProjectDependencyUtil.loadLoggerFactory(clazzLoader.get());
    }

    @Test
    public void loadTargetFolderClassloaderTest() throws Exception {
        CompilationResponse res = compileProjectInRepo(MavenCLIArgs.COMPILE, MavenCLIArgs.ALTERNATE_USER_SETTINGS + TestUtilMaven.getSettingsFile());
        TestUtil.saveMavenLogIfCompilationResponseNotSuccessfull(tmp, res, this.getClass(), testName);
        assertThat(res.isSuccessful()).isTrue();

        List<String> pomList = MavenUtils.searchPoms(uberfireTmp);
        Optional<ClassLoader> clazzLoader = CompilerClassloaderUtils.getClassloaderFromProjectTargets(pomList);
        assertThat(clazzLoader).isPresent();

        LoadProjectDependencyUtil.loadDummyB(clazzLoader.get());
    }

    @Test
    public void getClassloaderFromAllDependenciesTestSimple() {
        Path path = Paths.get(".").resolve(ResourcesConstants.DUMMY_DEPS_SIMPLE_DIR);
        Optional<ClassLoader> classloaderOptional = CompilerClassloaderUtils.getClassloaderFromAllDependencies(path.toAbsolutePath().toString(),
                                                                                                               mavenRepoPath,
                                                                                                               TestUtilMaven.getSettingsFile());
        assertThat(classloaderOptional).isPresent();
        ClassLoader classloader = classloaderOptional.get();
        URLClassLoader urlsc = (URLClassLoader) classloader;
        assertThat(urlsc.getURLs()).hasSize(4);
    }

    @Test
    public void getClassloaderFromAllDependenciesTestComplex() {
        Path path = Paths.get(".").resolve(ResourcesConstants.DUMMY_DEPS_COMPLEX_DIR);
        Optional<ClassLoader> classloaderOptional = CompilerClassloaderUtils.getClassloaderFromAllDependencies(path.toAbsolutePath().toString(),
                                                                                                               mavenRepoPath,
                                                                                                               TestUtilMaven.getSettingsFile());
        assertThat(classloaderOptional).isPresent();
        ClassLoader classloader = classloaderOptional.get();
        URLClassLoader urlsc = (URLClassLoader) classloader;
        assertThat(urlsc.getURLs()).hasSize(7);
    }

    @Test
    public void getResourcesFromADroolsPRJ() throws Exception {
        /**
         * If the test fail check if the Drools core classes used, KieModuleMetaInfo and TypeMetaInfo implements Serializable
         * */
        String alternateSettingsAbsPath = TestUtilMaven.getSettingsFile();
        Path tmpRoot = Files.createTempDirectory("repo");
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

        List<String> resources = CompilerClassloaderUtils.getStringFromTargets(tmpRoot);
        assertThat(resources).hasSize(3);
    }

    @Test
    public void getResourcesFromADroolsPRJWithError() throws Exception {
        /**
         * If the test fail check if the Drools core classes used, KieModuleMetaInfo and TypeMetaInfo implements Serializable
         * */
        Path tmpRoot = Files.createTempDirectory("repo");
        Path tmp = TestUtil.createAndCopyToDirectory(tmpRoot, "dummy", ResourcesConstants.KJAR_2_SINGLE_RESOURCES_WITH_ERROR);

        AFCompiler compiler = KieMavenCompilerFactory.getCompiler(EnumSet.of(KieDecorator.STORE_KIE_OBJECTS, KieDecorator.ENABLE_INCREMENTAL_BUILD));
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(Paths.get(tmp.toUri()));
        CompilationRequest req = new DefaultCompilationRequest(mavenRepoPath,
                                                               info,
                                                               new String[]{MavenCLIArgs.INSTALL, MavenCLIArgs.ALTERNATE_USER_SETTINGS + TestUtilMaven.getSettingsFile()},
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
        List<String> classloaderOptional = CompilerClassloaderUtils.getStringFromTargets(tmpRoot);
        assertThat(classloaderOptional).hasSize(3);
    }

    @Test
    public void getResourcesFromADroolsPRJWithErrorWithMavenSkip() throws Exception {
        System.setProperty(MAVEN_MAIN_SKIP, Boolean.TRUE.toString());
        /**
         * If the test fail check if the Drools core classes used, KieModuleMetaInfo and TypeMetaInfo implements Serializable
         * */
        Path tmpRoot = Files.createTempDirectory("repo");
        Path tmp = TestUtil.createAndCopyToDirectory(tmpRoot, "dummy", ResourcesConstants.KJAR_2_SINGLE_RESOURCES_WITH_ERROR);

        final AFCompiler compiler = KieMavenCompilerFactory.getCompiler(EnumSet.of(KieDecorator.STORE_KIE_OBJECTS, KieDecorator.ENABLE_INCREMENTAL_BUILD));
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(Paths.get(tmp.toUri()));
        CompilationRequest req = new DefaultCompilationRequest(mavenRepoPath,
                                                               info,
                                                               new String[]{MavenCLIArgs.COMPILE, MavenCLIArgs.ALTERNATE_USER_SETTINGS + TestUtilMaven.getSettingsFile()},
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
        List<String> classloaderOptional = CompilerClassloaderUtils.getStringFromTargets(tmpRoot);
        assertThat(classloaderOptional).hasSize(3);
    }
}
