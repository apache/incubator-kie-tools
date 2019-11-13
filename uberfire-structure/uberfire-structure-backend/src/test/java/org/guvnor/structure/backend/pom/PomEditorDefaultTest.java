/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.guvnor.structure.backend.pom;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.guvnor.structure.pom.DynamicPomDependency;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;

import static org.assertj.core.api.Assertions.*;

public class PomEditorDefaultTest {

    private final String POM = "pom.xml";
    private PomEditor editor;
    private Path tmpRoot, tmp;
    private static String fileSeparator = "/";

    @Before
    public void setUp() throws Exception {
        editor = new PomEditorDefault();
        tmpRoot = Files.createTempDirectory("repo");
        tmp = TestUtil.createAndCopyToDirectory(tmpRoot,
                                                "dummy",
                                                "target/test-classes/dummy");
    }

    @After
    public void tearDown() {
        if (tmpRoot != null) {
            TestUtil.rm(tmpRoot.toFile());
        }
    }

    @Test
    public void addEmptyDepTest() {
        DynamicPomDependency dep = new DynamicPomDependency("",
                                                            "",
                                                            "",
                                                            "");
        boolean result = editor.addDependency(dep,
                                              PathFactory.newPath(tmp.toAbsolutePath().toString() + fileSeparator + POM,
                                                                  tmp.toUri().toString() + fileSeparator + POM));
        assertThat(result).isFalse();
    }

    @Test
    public void addNullDepTest() {
        boolean result = editor.addDependency(null,
                                              PathFactory.newPath(tmp.toAbsolutePath().toString() + fileSeparator + POM,
                                                                  tmp.toUri().toString() + fileSeparator + POM));
        assertThat(result).isFalse();
    }

    @Test
    public void addNullGroupIDTest() {
        DynamicPomDependency dep = new DynamicPomDependency(null,
                                                            "junit",
                                                            "4.12",
                                                            "");
        boolean result = editor.addDependency(dep,
                                              PathFactory.newPath(tmp.toAbsolutePath().toString() + fileSeparator + POM,
                                                                  tmp.toUri().toString() + fileSeparator + POM));
        assertThat(result).isFalse();
    }

    @Test
    public void addNullArtifactIDTest() {
        DynamicPomDependency dep = new DynamicPomDependency("junit",
                                                            null,
                                                            "4.12",
                                                            "");
        boolean result = editor.addDependency(dep,
                                              PathFactory.newPath(tmp.toAbsolutePath().toString() + fileSeparator + POM,
                                                                  tmp.toUri().toString() + fileSeparator + POM));
        assertThat(result).isFalse();
    }

    @Test
    public void addDepTest() {
        DynamicPomDependency dep = new DynamicPomDependency("junit",
                                                            "junit",
                                                            "4.12",
                                                            "");
        boolean result = editor.addDependency(dep,
                                              PathFactory.newPath(tmp.toAbsolutePath().toString() + fileSeparator + POM,
                                                                  tmp.toUri().toString() + fileSeparator + POM));
        assertThat(result).isTrue();
    }

    @Test
    public void addDepsTest() {
        DynamicPomDependency dep = new DynamicPomDependency("junit",
                                                            "junit",
                                                            "4.12",
                                                            "");
        List<DynamicPomDependency> deps = Arrays.asList(dep);
        boolean result = editor.addDependencies(deps,
                                                PathFactory.newPath(tmp.toAbsolutePath().toString() + fileSeparator + POM,
                                                                    tmp.toUri().toString() + fileSeparator + POM));
        assertThat(result).isTrue();
    }

    @Test
    public void addDuplicatedDepTest() {
        DynamicPomDependency dep = new DynamicPomDependency(TestUtil.GROUP_ID_TEST,
                                                            TestUtil.ARTIFACT_ID_TEST,
                                                            TestUtil.VERSION_ID_TEST,
                                                            "");
        boolean result = editor.addDependency(dep,
                                              PathFactory.newPath(tmp.toAbsolutePath().toString() + fileSeparator + POM,
                                                                  tmp.toUri().toString() + fileSeparator + POM));
        assertThat(result).isFalse();
    }

    @Test
    public void addAndOverrideVersionDepTest() throws Exception {
        //During the scan of the pom if a dep is founded present will be override the version with the version in the json file
        tmp = TestUtil.createAndCopyToDirectory(tmpRoot,
                                                "dummyOverride",
                                                "target/test-classes/dummy");
        DynamicPomDependency dep = new DynamicPomDependency("javax.persistence",
                                                            "javax.persistence-api",
                                                            "1.0.3.Final",
                                                            "");
        List<DynamicPomDependency> deps = Arrays.asList(dep);
        boolean result = editor.addDependencies(deps,
                                                PathFactory.newPath(tmp.toAbsolutePath().toString() + fileSeparator + POM,
                                                                    tmp.toUri().toString() + fileSeparator + POM));
        assertThat(result).isTrue();

        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = reader.read(new ByteArrayInputStream(Files.readAllBytes(Paths.get(tmp.toAbsolutePath().toString() + fileSeparator + POM))));
        Dependency changedDep = getDependency(model.getDependencies(),
                                              "javax.persistence",
                                              "javax.persistence-api");
        assertThat(changedDep.getVersion()).isEqualTo("1.0.3.Final");
    }

    private Dependency getDependency(List<Dependency> deps,
                                     String groupId,
                                     String artifactId) {
        Dependency dependency = new Dependency();
        for (Dependency dep : deps) {
            if (dep.getGroupId().equals(groupId) && dep.getArtifactId().equals(artifactId)) {
                dependency.setGroupId(dep.getGroupId());
                dependency.setArtifactId(dep.getArtifactId());
                dependency.setVersion(dep.getVersion());
                dependency.setScope(dep.getScope());
                break;
            }
        }
        return dependency;
    }

    @Test
    public void addDuplicatedDepsTest() {
        DynamicPomDependency dep = new DynamicPomDependency(TestUtil.GROUP_ID_TEST,
                                                            TestUtil.ARTIFACT_ID_TEST,
                                                            TestUtil.VERSION_ID_TEST,
                                                            "");
        List<DynamicPomDependency> deps = Arrays.asList(dep);
        boolean result = editor.addDependencies(deps,
                                                PathFactory.newPath(tmp.toAbsolutePath().toString() + fileSeparator + POM,
                                                                    tmp.toUri().toString() + fileSeparator + POM));
        assertThat(result).isFalse();
    }

    @Test
    public void addAndOverrideVersionDepsTest() throws Exception {
        //During the scan of the pom if a dep is founded present will be override the version with the version in the json file
        tmp = TestUtil.createAndCopyToDirectory(tmpRoot,
                                                "dummyOverride",
                                                "target/test-classes/dummyOverride");
        DynamicPomDependency dep = new DynamicPomDependency("junit",
                                                            "junit",
                                                            "4.13",
                                                            "");
        DynamicPomDependency depTwo = new DynamicPomDependency("javax.persistence",
                                                               "javax.persistence-api",
                                                               "1.0.3.Final",
                                                               "");
        List<DynamicPomDependency> deps = Arrays.asList(dep,
                                                        depTwo);
        boolean result = editor.addDependencies(deps,
                                                PathFactory.newPath(tmp.toAbsolutePath().toString() + fileSeparator + POM,
                                                                    tmp.toUri().toString() + fileSeparator + POM));
        assertThat(result).isTrue();

        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = reader.read(new ByteArrayInputStream(Files.readAllBytes(Paths.get(tmp.toAbsolutePath().toString() + fileSeparator + POM))));
        Dependency changedDep = getDependency(model.getDependencies(),
                                              "javax.persistence",
                                              "javax.persistence-api");
        assertThat(changedDep.getVersion()).isEqualTo("1.0.3.Final");
        changedDep = getDependency(model.getDependencies(),
                                   "junit",
                                   "junit");
        assertThat(changedDep.getVersion()).isEqualTo("4.13");
    }
}
