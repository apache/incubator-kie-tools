/*
 * Copyright 2016 JBoss Inc
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
package org.guvnor.common.services.project.model;

import java.util.Collection;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DependenciesTest {

    private Dependencies dependencies;
    private Dependency droolsCore;

    @Before
    public void setUp() {
        dependencies = new Dependencies();

        droolsCore = new Dependency(new GAV("org.drools:drools-core:5.0"));
        droolsCore.setScope("compile");
        dependencies.add(droolsCore);

        Dependency junit = new Dependency(new GAV("junit:junit:4.11"));
        junit.setScope("test");
        dependencies.add(junit);

        Dependency depWithoutScope = new Dependency(new GAV("mygroup:depWithoutScope:1.0"));
        dependencies.add(depWithoutScope);
    }

    @Test
    public void testGetAllGAVs() {
        final Collection<GAV> gavs = dependencies.getGavs();
        assertThat(gavs.size()).isEqualTo(3);
        assertContains(gavs,
                       "org.drools",
                       "drools-core",
                       "5.0");
        assertContains(gavs,
                       "junit",
                       "junit",
                       "4.11");
        assertContains(gavs,
                       "mygroup",
                       "depWithoutScope",
                       "1.0");
    }

    @Test
    public void testFindByGav() {
        assertThat(dependencies.get(new GAV("org.drools:drools-core:5.0")))
                .isEqualTo(droolsCore);
    }

    @Test
    public void testNullWhenNoResults() {
        assertThat(dependencies.get(new GAV("org.drools:drools-core:112.0"))).isNull();
    }

    private static void assertContains(final Collection<GAV> gavs,
                                       final String groupID,
                                       final String artifactID,
                                       final String version) {
        for (GAV gav : gavs) {
            if (gav.getArtifactId().equals(artifactID)
                    && gav.getGroupId().equals(groupID)
                    && gav.getVersion().equals(version)) {
                return;
            }
        }

        Assertions.fail("Could not find " + groupID + ":" + artifactID + ":" + version);
    }

    @Test
    public void testContainsGAV() {

        assertThat(dependencies.containsDependency(new GAV("org.drools:drools-core:5.0"))).isTrue();
        final Dependency dependency = new Dependency(new GAV("org.drools:drools-core:5.0"));
        dependency.setScope("test");
        assertThat(dependencies.containsDependency(dependency)).isTrue();
        assertThat(dependencies.containsDependency(new GAV("org.drools:drools-core:4.0"))).isFalse();
    }

    @Test
    public void testGetTestScopedGAVs() {
        final Collection<GAV> gavs = dependencies.getGavs("test");
        assertThat(gavs).hasSize(1);
        assertContains(gavs,
                       "junit",
                       "junit",
                       "4.11");
    }

    @Test
    public void testGetCompileScopedGAVs() {
        final Collection<GAV> gavs = dependencies.getGavs("compile");
        assertThat(gavs).hasSize(1);
        assertContains(gavs,
                       "org.drools",
                       "drools-core",
                       "5.0");
    }

    @Test
    public void testGetCompileScopedGavsMethod() {
        final Collection<GAV> gavs = dependencies.getCompileScopedGavs();
        assertThat(gavs).hasSize(2);
        assertContains(gavs,
                       "org.drools",
                       "drools-core",
                       "5.0");
        assertContains(gavs,
                       "mygroup",
                       "depWithoutScope",
                       "1.0");
    }
}