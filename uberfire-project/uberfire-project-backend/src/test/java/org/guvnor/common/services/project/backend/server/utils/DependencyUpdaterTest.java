/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.common.services.project.backend.server.utils;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.maven.model.Model;
import org.guvnor.common.services.project.model.Dependency;
import org.junit.Test;

import static org.junit.Assert.*;

public class DependencyUpdaterTest {

    @Test
    public void testEmptyDependency() throws Exception {

        Model model = new Model();
        ArrayList<Dependency> dependencies = new ArrayList<Dependency>();
        dependencies.add(new Dependency());

        assertTrue(model.getDependencies().isEmpty());

        new DependencyUpdater(model.getDependencies()).updateDependencies(dependencies);

        assertEquals(1,
                     model.getDependencies().size());
        assertNull(model.getDependencies().get(0).getGroupId());
        assertNull(model.getDependencies().get(0).getArtifactId());
        assertNull(model.getDependencies().get(0).getVersion());
    }

    @Test
    public void testAdd() throws Exception {

        Model model = new Model();
        ArrayList<Dependency> dependencies = new ArrayList<Dependency>();
        dependencies.add(makeWorkbenchDependency("group",
                                                 "artifact-id",
                                                 "1.0"));

        assertTrue(model.getDependencies().isEmpty());

        new DependencyUpdater(model.getDependencies()).updateDependencies(dependencies);

        assertEquals(1,
                     model.getDependencies().size());
        assertEquals("group",
                     model.getDependencies().get(0).getGroupId());
        assertEquals("artifact-id",
                     model.getDependencies().get(0).getArtifactId());
        assertEquals("1.0",
                     model.getDependencies().get(0).getVersion());
    }

    @Test
    public void testRemove() throws Exception {

        Model model = new Model();

        model.getDependencies().add(makeMavenDependency("group",
                                                        "artifact-id",
                                                        "1.0"));

        assertFalse(model.getDependencies().isEmpty());

        new DependencyUpdater(model.getDependencies()).updateDependencies(Collections.EMPTY_LIST);

        assertTrue(model.getDependencies().isEmpty());
    }

    @Test
    public void testUpdate() throws Exception {
        Model model = new Model();
        model.getDependencies().add(makeMavenDependency("group",
                                                        "artifact-id",
                                                        "1.0"));

        ArrayList<Dependency> dependencies = new ArrayList<Dependency>();
        dependencies.add(makeWorkbenchDependency("group",
                                                 "artifact-id",
                                                 "2.0"));

        assertFalse(model.getDependencies().isEmpty());

        new DependencyUpdater(model.getDependencies()).updateDependencies(dependencies);

        assertEquals(1,
                     model.getDependencies().size());
        assertEquals("group",
                     model.getDependencies().get(0).getGroupId());
        assertEquals("artifact-id",
                     model.getDependencies().get(0).getArtifactId());
        assertEquals("2.0",
                     model.getDependencies().get(0).getVersion());
    }

    private org.apache.maven.model.Dependency makeMavenDependency(String group,
                                                                  String artifactId,
                                                                  String version) {
        org.apache.maven.model.Dependency dependency = new org.apache.maven.model.Dependency();
        dependency.setGroupId(group);
        dependency.setArtifactId(artifactId);
        dependency.setVersion(version);
        return dependency;
    }

    private Dependency makeWorkbenchDependency(String group,
                                               String artifactId,
                                               String version) {
        Dependency dependency = new Dependency();
        dependency.setGroupId(group);
        dependency.setArtifactId(artifactId);
        dependency.setVersion(version);
        return dependency;
    }
}
