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
package org.kie.workbench.common.services.backend.maven.plugins.dependency;

import java.io.File;
import java.net.URLClassLoader;
import java.util.EnumSet;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.workbench.common.services.backend.compiler.AFCompiler;
import org.kie.workbench.common.services.backend.compiler.CompilationRequest;
import org.kie.workbench.common.services.backend.compiler.CompilationResponse;
import org.kie.workbench.common.services.backend.compiler.TestUtilMaven;
import org.kie.workbench.common.services.backend.compiler.configuration.KieDecorator;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenConfig;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.impl.WorkspaceCompilationInfo;
import org.kie.workbench.common.services.backend.compiler.impl.classloader.CompilerClassloaderUtils;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieMavenCompilerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class BuildInMemoryClasspathMojoTest {

    private static Path tmpRoot;
    private String mavenRepo;
    private static Logger logger = LoggerFactory.getLogger(BuildInMemoryClasspathMojoTest.class);
    private String alternateSettingsAbsPath;

    @BeforeClass
    public static void setup() {
        System.setProperty("org.uberfire.nio.git.daemon.enabled", "false");
        System.setProperty("org.uberfire.nio.git.ssh.enabled", "false");
    }

    @Before
    public void setUp() throws Exception {
        mavenRepo = TestUtilMaven.getMavenRepo();
        tmpRoot = Files.createTempDirectory("repo");
        alternateSettingsAbsPath = TestUtilMaven.getSettingsFile();
    }


    @Test
    public void getClassloaderFromAllDependenciesSimpleTest(){

        Path path = Paths.get(".").resolve("target/test-classes/dummy_deps_simple");
        final AFCompiler compiler = KieMavenCompilerFactory.getCompiler(EnumSet.of(KieDecorator.STORE_BUILD_CLASSPATH ));
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(path);
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo,
                                                               info,
                                                               new String[]{ MavenCLIArgs.ALTERNATE_USER_SETTINGS + alternateSettingsAbsPath, MavenConfig.DEPS_IN_MEMORY_BUILD_CLASSPATH},
                                                               Boolean.FALSE);

        CompilationResponse res = compiler.compile(req);
        assertThat(res.isSuccessful()).isTrue();
        assertThat(res.getDependencies()).isNotEmpty();
        assertThat(res.getDependencies()).hasSize(4);
    }

    @Test
    public void getClassloaderFromAllDependenciesComplexTest() {

        Path path = Paths.get(".").resolve("target/test-classes/dummy_deps_complex");
        final AFCompiler compiler = KieMavenCompilerFactory.getCompiler(EnumSet.of(KieDecorator.STORE_BUILD_CLASSPATH ));
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(path);
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo,
                                                               info,
                                                               new String[]{MavenCLIArgs.ALTERNATE_USER_SETTINGS + alternateSettingsAbsPath, MavenConfig.DEPS_IN_MEMORY_BUILD_CLASSPATH},
                                                               Boolean.FALSE);

        CompilationResponse res = compiler.compile(req);
        assertThat(res.isSuccessful()).isTrue();
        assertThat(res.getDependencies()).isNotEmpty();
        assertThat(res.getDependencies()).hasSize(7);
    }

    @Test
    public void testCompilerClassloaderUtilsTests(){
        Path path = Paths.get(".").resolve("target/test-classes//dummy_deps_complex");
        Optional<ClassLoader> classloaderOptional = CompilerClassloaderUtils.getClassloaderFromAllDependencies(path.toAbsolutePath().toString(),
                                                                                                               mavenRepo);
        assertThat(classloaderOptional).isPresent();
        ClassLoader classloader = classloaderOptional.get();
        URLClassLoader urlsc = (URLClassLoader) classloader;
        assertThat(urlsc.getURLs()).hasSize(7);
    }

    @AfterClass
    public static void tearDown() {
        System.clearProperty("org.uberfire.nio.git.daemon.enabled");
        System.clearProperty("org.uberfire.nio.git.ssh.enabled");
        if(tmpRoot!= null) {
            rm(tmpRoot.toFile());
        }
    }

    public static void rm(File f) {
        try{
            FileUtils.deleteDirectory(f);
        }catch (Exception e){
            logger.error("Couldn't delete file {}", f);
            logger.error(e.getMessage(), e);
        }
    }


}
