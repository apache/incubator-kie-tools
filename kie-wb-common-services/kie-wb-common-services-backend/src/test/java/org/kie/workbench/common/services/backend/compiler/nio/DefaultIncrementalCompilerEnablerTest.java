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

package org.kie.workbench.common.services.backend.compiler.nio;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.services.backend.compiler.TestUtil;
import org.kie.workbench.common.services.backend.compiler.configuration.Compilers;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs;
import org.kie.workbench.common.services.backend.compiler.nio.impl.NIODefaultCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.nio.impl.NIODefaultIncrementalCompilerEnabler;
import org.kie.workbench.common.services.backend.compiler.nio.impl.NIOWorkspaceCompilationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultIncrementalCompilerEnablerTest {

    private Path mavenRepo;
    private Logger logger = LoggerFactory.getLogger(DefaultIncrementalCompilerEnablerTest.class);

    @Before
    public void setUp() throws Exception {
        mavenRepo = Paths.get(System.getProperty("user.home"),
                              "/.m2/repository");

        if (!Files.exists(mavenRepo)) {
            logger.info("Creating a m2_repo into " + mavenRepo);
            if (!Files.exists(Files.createDirectories(mavenRepo))) {
                throw new Exception("Folder not writable in the project");
            }
        }
    }

    @Test
    public void testReadPomsInaPrjTest() throws Exception {
        Path tmpRoot = Files.createTempDirectory("repo");
        Path tmp = Files.createDirectories(Paths.get(tmpRoot.toString(),
                                                     "dummy"));
        TestUtil.copyTree(Paths.get("src/test/projects/dummy_multimodule_untouched"),
                          tmp);
        Path mainPom = Paths.get(tmp.toAbsolutePath().toString(),
                                 "pom.xml");

        byte[] encoded = Files.readAllBytes(Paths.get(tmp.toAbsolutePath().toString(),
                                                      "pom.xml"));
        String pomAsAstring = new String(encoded,
                                         StandardCharsets.UTF_8);
        Assert.assertFalse(pomAsAstring.contains("<artifactId>takari-lifecycle-plugin</artifactId>"));

        NIOWorkspaceCompilationInfo info = new NIOWorkspaceCompilationInfo(tmp);
        NIOCompilationRequest req = new NIODefaultCompilationRequest(mavenRepo.toAbsolutePath().toString(),
                                                                     info,
                                                                     new String[]{MavenCLIArgs.CLEAN, MavenCLIArgs.COMPILE},
                                                                     new HashMap<>(),
                                                                     Boolean.FALSE);
        NIODefaultIncrementalCompilerEnabler enabler = new NIODefaultIncrementalCompilerEnabler(Compilers.JAVAC);
        Assert.assertTrue(enabler.process(req).getResult());

        encoded = Files.readAllBytes(Paths.get(mainPom.toString()));
        pomAsAstring = new String(encoded,
                                  StandardCharsets.UTF_8);
        Assert.assertTrue(pomAsAstring.contains("<artifactId>takari-lifecycle-plugin</artifactId>"));

        Assert.assertFalse(pomAsAstring.contains("kie-takari-plugin"));

        TestUtil.rm(tmpRoot.toFile());
    }

    @Test
    public void testReadKiePluginTest() throws Exception {
        Path tmpRoot = Files.createTempDirectory("repo");
        Path tmp = Files.createDirectories(Paths.get(tmpRoot.toString(),
                                                     "dummy"));
        TestUtil.copyTree(Paths.get("src/test/projects/dummy_kie_multimodule_untouched"),
                          tmp);
        Path mainPom = Paths.get(tmp.toAbsolutePath().toString(),
                                 "pom.xml");

        byte[] encoded = Files.readAllBytes(Paths.get(tmp.toAbsolutePath().toString(),
                                                      "pom.xml"));
        String pomAsAstring = new String(encoded,
                                         StandardCharsets.UTF_8);
        Assert.assertFalse(pomAsAstring.contains("<artifactId>takari-lifecycle-plugin</artifactId>"));
        Assert.assertFalse(pomAsAstring.contains("<packaging>kjar</packaging>"));

        byte[] encodedDummyB = Files.readAllBytes(Paths.get(tmp.toAbsolutePath().toString(),
                                                            "/dummyB/pom.xml"));

        String pomAsAstringDummyB = new String(encodedDummyB,
                                               StandardCharsets.UTF_8);
        Assert.assertTrue(pomAsAstringDummyB.contains("<packaging>kjar</packaging>"));

        NIOWorkspaceCompilationInfo info = new NIOWorkspaceCompilationInfo(tmp);
        NIOCompilationRequest req = new NIODefaultCompilationRequest(mavenRepo.toAbsolutePath().toString(),
                                                                     info,
                                                                     new String[]{MavenCLIArgs.CLEAN, MavenCLIArgs.COMPILE},
                                                                     new HashMap<>(),
                                                                     Boolean.FALSE);
        NIODefaultIncrementalCompilerEnabler enabler = new NIODefaultIncrementalCompilerEnabler(Compilers.JAVAC);
        Assert.assertTrue(enabler.process(req).getResult());

        Assert.assertTrue(info.isKiePluginPresent());

        encoded = Files.readAllBytes(Paths.get(mainPom.toString()));
        pomAsAstring = new String(encoded,
                                  StandardCharsets.UTF_8);
        Assert.assertTrue(pomAsAstring.contains("kie-takari-plugin"));

        Assert.assertTrue(pomAsAstring.contains("<artifactId>takari-lifecycle-plugin</artifactId>"));

        TestUtil.rm(tmpRoot.toFile());
    }
}
