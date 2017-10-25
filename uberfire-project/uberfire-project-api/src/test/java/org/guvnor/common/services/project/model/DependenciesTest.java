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

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DependenciesTest {

    private Dependencies dependencies;
    private Dependency droolsCore;
    private Dependency junit;

    @Before
    public void setUp() throws Exception {
        dependencies = new Dependencies();

        droolsCore = new Dependency(new GAV("org.drools:drools-core:5.0"));
        droolsCore.setScope("compile");
        dependencies.add(droolsCore);

        junit = new Dependency(new GAV("junit:junit:4.11"));
        junit.setScope("test");
        dependencies.add(junit);
    }

    @Test
    public void testGetAllGAVs() throws Exception {
        final Collection<GAV> gavs = dependencies.getGavs();
        assertEquals(2,
                     gavs.size());
        assertContains(gavs,
                       "org.drools",
                       "drools-core",
                       "5.0");
        assertContains(gavs,
                       "junit",
                       "junit",
                       "4.11");
    }

    @Test
    public void testFindByGav() throws Exception {
        assertEquals(droolsCore,
                     dependencies.get(new GAV("org.drools:drools-core:5.0")));
    }

    @Test
    public void testNullWhenNoResults() throws Exception {
        assertNull(dependencies.get(new GAV("org.drools:drools-core:112.0")));
    }

    public static void assertContains(final Collection<GAV> gavs,
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

        fail("Could not find " + groupID + ":" + artifactID + ":" + version);
    }

    @Test
    public void testContainsGAV() throws Exception {

        assertTrue(dependencies.containsDependency(new GAV("org.drools:drools-core:5.0")));
        final Dependency dependency = new Dependency(new GAV("org.drools:drools-core:5.0"));
        dependency.setScope("test");
        assertTrue(dependencies.containsDependency(dependency));
        assertFalse(dependencies.containsDependency(new GAV("org.drools:drools-core:4.0")));
    }

    @Test
    public void testGetTestScopedGAVs() throws Exception {
        final Collection<GAV> gavs = dependencies.getGavs("test");
        assertEquals(1,
                     gavs.size());
        assertContains(gavs,
                       "junit",
                       "junit",
                       "4.11");
    }

    @Test
    public void testGetCompileScopedGAVs() throws Exception {
        final Collection<GAV> gavs = dependencies.getGavs("compile");
        assertEquals(1,
                     gavs.size());
        assertContains(gavs,
                       "org.drools",
                       "drools-core",
                       "5.0");
    }

    @Test
    public void testGetCompileScopedGavsMethod() throws Exception {
        final Collection<GAV> gavs = dependencies.getCompileScopedGavs();
        assertEquals(1,
                     gavs.size());
        assertContains(gavs,
                       "org.drools",
                       "drools-core",
                       "5.0");
    }
}