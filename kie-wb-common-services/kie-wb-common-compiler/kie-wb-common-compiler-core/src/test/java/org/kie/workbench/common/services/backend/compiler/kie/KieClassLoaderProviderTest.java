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

package org.kie.workbench.common.services.backend.compiler.kie;

import java.io.IOException;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.kie.workbench.common.services.backend.compiler.AFCompiler;
import org.kie.workbench.common.services.backend.compiler.CompilationRequest;
import org.kie.workbench.common.services.backend.compiler.CompilationResponse;
import org.kie.workbench.common.services.backend.compiler.TestUtilMaven;
import org.kie.workbench.common.services.backend.compiler.configuration.KieDecorator;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.impl.WorkspaceCompilationInfo;
import org.kie.workbench.common.services.backend.compiler.impl.classloader.CompilerClassloaderUtils;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieMavenCompilerFactory;
import org.kie.workbench.common.services.backend.compiler.impl.utils.MavenUtils;
import org.kie.workbench.common.services.backend.constants.ResourcesConstants;
import org.kie.workbench.common.services.backend.utils.LoadProjectDependencyUtil;
import org.kie.workbench.common.services.backend.utils.TestUtil;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class KieClassLoaderProviderTest {

    private String mavenRepo;
    private Path tmpRoot;
    private Path uberfireTmp;
    private Path tmp;

    @Rule
    public TestName testName = new TestName();

    @Before
    public void setUp() throws Exception {
        mavenRepo = TestUtilMaven.getMavenRepo();
    }

    @After
    public void clean() throws Exception{
        if (tmpRoot != null) {
            TestUtil.rm(tmpRoot.toFile());
        }
    }

    private CompilationResponse compileProjectInRepo(String... mavenPhases) throws IOException {
        tmpRoot = Files.createTempDirectory("repo");
        tmp = TestUtil.createAndCopyToDirectory(tmpRoot, "dummy", ResourcesConstants.DUMMY_KIE_MULTIMODULE_CLASSLOADER_DIR);

        uberfireTmp = Paths.get(tmp.toAbsolutePath().toString());

        AFCompiler compiler = KieMavenCompilerFactory.getCompiler(KieDecorator.NONE);
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(uberfireTmp);
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo,
                                                               info,
                                                               mavenPhases,
                                                               Boolean.FALSE);
        return compiler.compile(req);
    }

    @Test
    public void loadProjectClassloaderTest() throws Exception {
        //we use NIO for this part of the test because Uberfire lack the implementation to copy a tree
        CompilationResponse res = compileProjectInRepo(MavenCLIArgs.CLEAN, MavenCLIArgs.COMPILE, MavenCLIArgs.INSTALL);
        TestUtil.saveMavenLogIfCompilationResponseNotSuccessfull(tmp, res, this.getClass(), testName);
        assertThat(res.isSuccessful()).isTrue();

        List<String> pomList = MavenUtils.searchPoms(Paths.get(ResourcesConstants.DUMMY_KIE_MULTIMODULE_CLASSLOADER_DIR));
        Optional<ClassLoader> clazzLoader = CompilerClassloaderUtils.loadDependenciesClassloaderFromProject(pomList,
                                                                                                            mavenRepo);
        assertThat(clazzLoader).isPresent();
        ClassLoader prjClassloader = clazzLoader.get();

        LoadProjectDependencyUtil.loadLoggerFactory(prjClassloader);
    }

    @Test
    public void loadProjectClassloaderFromStringTest() throws Exception {
        //we use NIO for this part of the test because Uberfire lack the implementation to copy a tree
        CompilationResponse res =  compileProjectInRepo(MavenCLIArgs.CLEAN, MavenCLIArgs.COMPILE, MavenCLIArgs.INSTALL);
        TestUtil.saveMavenLogIfCompilationResponseNotSuccessfull(tmp, res, this.getClass(), testName);
        assertThat(res.isSuccessful()).isTrue();

        Optional<ClassLoader> clazzLoader = CompilerClassloaderUtils.loadDependenciesClassloaderFromProject(uberfireTmp.toAbsolutePath().toString(),
                                                                                                            mavenRepo);
        assertThat(clazzLoader).isPresent();

        LoadProjectDependencyUtil.loadLoggerFactory(clazzLoader.get());
    }

    @Test
    public void loadTargetFolderClassloaderTest() throws Exception {
        //we use NIO for this part of the test because Uberfire lack the implementation to copy a tree
        CompilationResponse res =  compileProjectInRepo(MavenCLIArgs.COMPILE);
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
                                                                                                               mavenRepo);
        assertThat(classloaderOptional).isPresent();
        ClassLoader classloader = classloaderOptional.get();
        URLClassLoader urlsc = (URLClassLoader) classloader;
        assertThat(urlsc.getURLs()).hasSize(4);
    }

    @Test
    public void getClassloaderFromAllDependenciesTestComplex() {
        Path path = Paths.get(".").resolve(ResourcesConstants.DUMMY_DEPS_COMPLEX_DIR);
        Optional<ClassLoader> classloaderOptional = CompilerClassloaderUtils.getClassloaderFromAllDependencies(path.toAbsolutePath().toString(),
                                                                                                               mavenRepo);
        assertThat(classloaderOptional).isPresent();
        ClassLoader classloader = classloaderOptional.get();
        URLClassLoader urlsc = (URLClassLoader) classloader;
        assertThat(urlsc.getURLs()).hasSize(7);
    }
}
