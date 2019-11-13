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

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.guvnor.structure.pom.AddPomDependencyEvent;
import org.guvnor.structure.pom.DependencyType;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;

import static org.assertj.core.api.Assertions.*;

public class PomStructureEditorTest {

    private PomStructureEditor editor;
    private Path tmpRoot, tmp;
    private final String POM = "pom.xml";
    private static String fileSeparator = "/";

    @Before
    public void setUp() throws Exception {
        tmpRoot = Files.createTempDirectory("repo");
        tmp = TestUtil.createAndCopyToDirectory(tmpRoot,
                                                "dummy",
                                                "target/test-classes/dummy_empty_deps");
    }

    @Test
    public void onNEwDynamicDependencyEventTest() throws Exception {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = reader.read(new ByteArrayInputStream(Files.readAllBytes(Paths.get(tmp.toAbsolutePath().toString() + fileSeparator + POM))));
        assertThat(model.getDependencies()).hasSize(0);

        editor = new PomStructureEditor();
        AddPomDependencyEvent event = new AddPomDependencyEvent(DependencyType.JPA,
                                                                PathFactory.newPath(tmp.getFileName().toString(),
                                                                                    tmp.toUri().toString() + fileSeparator + POM));
        editor.onNewDynamicDependency(event);

        model = reader.read(new ByteArrayInputStream(Files.readAllBytes(Paths.get(tmp.toAbsolutePath().toString() + fileSeparator + POM))));
        assertThat(model.getDependencies()).hasSize(1);
        Dependency dep = model.getDependencies().get(0);
        assertThat(dep.getGroupId()).containsOnlyOnce("javax.persistence");
        assertThat(dep.getArtifactId()).containsOnlyOnce("javax.persistence-api");
        assertThat(dep.getVersion()).containsOnlyOnce("2.2");
    }
}
