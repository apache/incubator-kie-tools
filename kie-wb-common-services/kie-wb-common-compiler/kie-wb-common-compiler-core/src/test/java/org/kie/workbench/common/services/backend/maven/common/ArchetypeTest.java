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
package org.kie.workbench.common.services.backend.maven.common;

import java.util.EnumSet;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.kie.workbench.common.services.backend.compiler.AFCompiler;
import org.kie.workbench.common.services.backend.compiler.CompilationRequest;
import org.kie.workbench.common.services.backend.compiler.CompilationResponse;
import org.kie.workbench.common.services.backend.compiler.configuration.KieDecorator;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenConfig;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.impl.WorkspaceCompilationInfo;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieMavenCompilerFactory;
import org.kie.workbench.common.services.backend.utils.TestUtil;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class ArchetypeTest {

    private Path mavenRepoPath;

    private String groupId = "org.kie.wonderland";
    private String artifactId = "maven.archetype";
    private String archetypeArtifactId = "maven-archetype-quickstart";

    @Rule
    public TestName testName = new TestName();

    @Before
    public void setUp() throws Exception {
        mavenRepoPath = Paths.get(System.getProperty("user.home"), ".m2", "repository");

        if (!Files.exists(mavenRepoPath)) {
            if (!Files.exists(Files.createDirectories(mavenRepoPath))) {
                throw new Exception("Folder not writable in the project");
            }
        }
    }

    @Test
    public void testArchetypeGenerate() throws Exception {
        Path tmpRoot = Files.createTempDirectory("repo");

        Path tmp = Paths.get(tmpRoot.toAbsolutePath().toString());
        assertThat(isDirEmpty(tmpRoot)).isTrue();

        final AFCompiler compiler = KieMavenCompilerFactory.getCompiler(EnumSet.of(KieDecorator.ENABLE_LOGGING ));
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(tmp);
        CompilationRequest req = new DefaultCompilationRequest(mavenRepoPath.toAbsolutePath().toString(),
                                                               info,
                                                               new String[]{
                                                                       MavenConfig.ARCHETYPE_GENERATE,
                                                                       MavenConfig.ARCHETYPE_GENERATE_BLANK,
                                                                       MavenConfig.GROUP_ID + groupId,
                                                                       MavenConfig.ARTIFACT_ID + artifactId,
                                                                       MavenConfig.ARCHETYPE_ARTIFACT_ID + archetypeArtifactId
                                                               },
                                                               Boolean.TRUE);
        CompilationResponse res = compiler.compile(req);

        TestUtil.saveMavenLogIfCompilationResponseNotSuccessfull(tmpRoot, res, this.getClass(), testName);
        assertThat(res.isSuccessful()).isTrue();
        assertThat(res.getMavenOutput().size()).isGreaterThan(0);
        assertThat(isDirEmpty(tmpRoot)).isFalse();

        Path prj = Paths.get(tmpRoot.toAbsolutePath().toString(), artifactId);
        assertThat(isDirEmpty(prj)).isFalse();

        Path appDir = Paths.get(prj.toAbsolutePath().toString(), "/src/main/java/" + (groupId.replace(".", "/")));
        assertThat(isDirEmpty(appDir)).isFalse();

        Path testDir = Paths.get(prj.toAbsolutePath().toString(), "/src/test/java/" + (groupId.replace(".", "/")));
        assertThat(isDirEmpty(testDir)).isFalse();

        Path pom = Paths.get(tmpRoot.toAbsolutePath().toString(), artifactId + "/pom.xml");
        assertThat(Files.exists(pom)).isTrue();

        TestUtil.rm(tmpRoot.toFile());
    }

    private boolean isDirEmpty(final Path directory) {
        try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(directory)) {
            return !dirStream.iterator().hasNext();
        }
    }
}
