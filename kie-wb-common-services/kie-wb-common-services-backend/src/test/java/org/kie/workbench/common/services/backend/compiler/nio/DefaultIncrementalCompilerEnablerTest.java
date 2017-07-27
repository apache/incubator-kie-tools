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
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.services.backend.compiler.TestUtil;
import org.kie.workbench.common.services.backend.compiler.configuration.Compilers;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs;
import org.kie.workbench.common.services.backend.compiler.nio.impl.DefaultCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.nio.impl.DefaultIncrementalCompilerEnabler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;
import org.uberfire.java.nio.file.api.FileSystemProviders;
import org.uberfire.java.nio.file.spi.FileSystemProvider;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

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

        FileSystemProvider fs = FileSystemProviders.getDefaultProvider();

        Path tmpRoot = Files.createTempDirectory("repo");
        //NIO creation and copy content
        Path temp = Files.createDirectories(Paths.get(tmpRoot.toString(),
                                                      "dummy"));
        TestUtil.copyTree(Paths.get("src/test/projects/dummy_multimodule_untouched"),
                          temp);
        //end NIO
        Path tmp = Paths.get(tmpRoot.toAbsolutePath().toString(),
                             "dummy");

        Path mainPom = Paths.get(temp.toAbsolutePath().toString(),
                                 "pom.xml");

        byte[] encoded = Files.readAllBytes(Paths.get(temp.toAbsolutePath().toString(),
                                                      "pom.xml"));
        String pomAsAstring = new String(encoded,
                                         StandardCharsets.UTF_8);
        assertFalse(pomAsAstring.contains("<artifactId>takari-lifecycle-plugin</artifactId>"));
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(tmp);

        CompilationRequest req = new DefaultCompilationRequest(mavenRepo.toAbsolutePath().toString(),
                                                               info,
                                                               new String[]{MavenCLIArgs.CLEAN, MavenCLIArgs.COMPILE},
                                                               new HashMap<>(),
                                                               Boolean.FALSE);
        DefaultIncrementalCompilerEnabler enabler = new DefaultIncrementalCompilerEnabler(Compilers.JAVAC);
        Assert.assertTrue(enabler.process(req).getResult());

        encoded = Files.readAllBytes(Paths.get(mainPom.toString()));
        pomAsAstring = new String(encoded,
                                  StandardCharsets.UTF_8);
        Assert.assertTrue(pomAsAstring.contains("<artifactId>takari-lifecycle-plugin</artifactId>"));

        assertFalse(pomAsAstring.contains("kie-takari-plugin"));
        TestUtil.rm(tmpRoot.toFile());
    }

    @Test
    public void testReadKiePluginTest() throws Exception {

        Path tmpRoot = Files.createTempDirectory("repo");

        //NIO creation and copy content
        Path temp = Files.createDirectories(Paths.get(tmpRoot.toString(),
                                                      "dummy"));
        TestUtil.copyTree(Paths.get("src/test/projects/dummy_kie_multimodule_untouched"),
                          temp);
        //end NIO
        Path tmp = Paths.get(tmpRoot.toAbsolutePath().toString(),
                             "dummy");

        Path mainPom = Paths.get(tmp.toAbsolutePath().toString(),
                                 "pom.xml");
        byte[] encoded = Files.readAllBytes(Paths.get(tmp.toAbsolutePath().toString(),
                                                      "pom.xml"));
        String pomAsAstring = new String(encoded,
                                         StandardCharsets.UTF_8);
        assertFalse(pomAsAstring.contains("<artifactId>takari-lifecycle-plugin</artifactId>"));
        assertFalse(pomAsAstring.contains("<packaging>kjar</packaging>"));

        byte[] encodedDummyB = Files.readAllBytes(Paths.get(tmp.toAbsolutePath().toString(),
                                                            "/dummyB/pom.xml"));

        String pomAsAstringDummyB = new String(encodedDummyB,
                                               StandardCharsets.UTF_8);
        assertTrue(pomAsAstringDummyB.contains("<packaging>kjar</packaging>"));

        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(tmp);
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo.toAbsolutePath().toString(),
                                                               info,
                                                               new String[]{MavenCLIArgs.CLEAN, MavenCLIArgs.COMPILE, "-X"},
                                                               new HashMap<>(),
                                                               Boolean.FALSE);
        DefaultIncrementalCompilerEnabler enabler = new DefaultIncrementalCompilerEnabler(Compilers.JAVAC);
        assertTrue(enabler.process(req).getResult());

        assertTrue(info.isKiePluginPresent());

        encoded = Files.readAllBytes(Paths.get(mainPom.toString()));
        pomAsAstring = new String(encoded,
                                  StandardCharsets.UTF_8);

        assertTrue(pomAsAstring.contains("kie-takari-plugin"));

        TestUtil.rm(tmpRoot.toFile());
    }
}
