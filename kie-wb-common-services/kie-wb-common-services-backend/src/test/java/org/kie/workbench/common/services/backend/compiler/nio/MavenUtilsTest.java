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

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.junit.Test;
import org.kie.workbench.common.services.backend.compiler.configuration.Compilers;
import org.kie.workbench.common.services.backend.compiler.nio.impl.DefaultIncrementalCompilerEnabler;
import org.kie.workbench.common.services.backend.compiler.nio.impl.MavenUtils;
import org.uberfire.java.nio.file.Paths;

import static org.junit.Assert.*;

public class MavenUtilsTest {

    @Test
    public void presenceOfDepInThePrj() throws Exception {
        DefaultIncrementalCompilerEnabler enabler = new DefaultIncrementalCompilerEnabler(Compilers.JAVAC);
        List<String> pomList = new ArrayList<>();
        MavenUtils.searchPoms(Paths.get("src/test/projects/dummy_kie_multimodule_untouched/"),
                              pomList);
        assertTrue(pomList.size() == 3);
        List<Artifact> deps = MavenUtils.resolveDependenciesFromMultimodulePrj(pomList);
        assertTrue(deps.size() == 1);
        Artifact artifact = deps.get(0);
        assertTrue(artifact.getArtifactId().equals("kie-api"));
        assertTrue(artifact.getGroupId().equals("org.kie"));
        assertTrue(artifact.getVersion().equals("6.5.0.Final"));
        assertTrue(artifact.getType().equals("jar"));
        assertTrue(artifact.toString().equals("org.kie:kie-api:jar:6.5.0.Final"));
    }
}
