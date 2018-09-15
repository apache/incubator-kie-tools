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

import java.nio.charset.StandardCharsets;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.impl.WorkspaceCompilationInfo;
import org.kie.workbench.common.services.backend.compiler.impl.incrementalenabler.DefaultIncrementalCompilerEnabler;
import org.kie.workbench.common.services.backend.constants.TestConstants;
import org.kie.workbench.common.services.backend.utils.TestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;
import org.uberfire.java.nio.file.api.FileSystemProviders;
import org.uberfire.java.nio.file.spi.FileSystemProvider;

import static org.assertj.core.api.Assertions.assertThat;

public class DefaultIncrementalCompilerEnablerTest {

    private String mavenRepoPath;

    @Before
    public void setUp() throws Exception {
        mavenRepoPath = TestUtilMaven.getMavenRepo();
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
        assertThat(pomAsAstring).doesNotContain(TestConstants.TAKARI_LIFECYCLE_ARTIFACT);
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(tmp);
        CompilationRequest req = new DefaultCompilationRequest(mavenRepoPath,
                                                               info,
                                                               new String[]{MavenCLIArgs.COMPILE},
                                                               Boolean.FALSE);
        DefaultIncrementalCompilerEnabler enabler = new DefaultIncrementalCompilerEnabler();
        assertThat(enabler.process(req).getResult()).isTrue();

        encoded = Files.readAllBytes(Paths.get(mainPom.toString()));
        pomAsAstring = new String(encoded,
                                  StandardCharsets.UTF_8);
        assertThat(pomAsAstring).contains(TestConstants.KIE_TAKARI_LIFECYCLE_ARTIFACT);

        assertThat(pomAsAstring.contains("kie-takari-plugin")).isFalse();
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
        assertThat(pomAsAstring).doesNotContain(TestConstants.TAKARI_LIFECYCLE_ARTIFACT);
        assertThat(pomAsAstring).doesNotContain("<packaging>kjar</packaging>");

        byte[] encodedDummyB = Files.readAllBytes(Paths.get(tmp.toAbsolutePath().toString(),
                                                            "/dummyB/pom.xml"));

        String pomAsAstringDummyB = new String(encodedDummyB,
                                               StandardCharsets.UTF_8);
        assertThat(pomAsAstringDummyB).contains("<packaging>kjar</packaging>");

        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(tmp);
        CompilationRequest req = new DefaultCompilationRequest(mavenRepoPath,
                                                               info,
                                                               new String[]{MavenCLIArgs.COMPILE},
                                                               Boolean.FALSE);
        DefaultIncrementalCompilerEnabler enabler = new DefaultIncrementalCompilerEnabler();
        assertThat(enabler.process(req).getResult()).isTrue();

        assertThat(info.isKiePluginPresent()).isTrue();

        encoded = Files.readAllBytes(Paths.get(mainPom.toString()));
        pomAsAstring = new String(encoded,
                                  StandardCharsets.UTF_8);

        assertThat(pomAsAstring).contains("kie-takari-plugin");

        TestUtil.rm(tmpRoot.toFile());
    }
}
