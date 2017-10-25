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
import java.util.List;

import org.apache.maven.model.Plugin;
import org.junit.Test;

import static org.guvnor.common.services.project.backend.server.utils.TestUtils.makeGuvnorPlugin;
import static org.guvnor.common.services.project.backend.server.utils.TestUtils.makeMavenPlugin;
import static org.junit.Assert.*;

public class MavenPluginUpdaterTest {

    @Test
    public void testIgnorePluginsThatHaveNoGroupIdOrArtifactId() throws Exception {

        List<Plugin> to = new ArrayList<Plugin>();
        ArrayList<org.guvnor.common.services.project.model.Plugin> from = new ArrayList<org.guvnor.common.services.project.model.Plugin>();
        from.add(makeGuvnorPlugin(null,
                                  null,
                                  null));
        from.add(makeGuvnorPlugin(null,
                                  null,
                                  "1.0"));
        from.add(makeGuvnorPlugin("myGroup",
                                  null,
                                  "1.0"));
        from.add(makeGuvnorPlugin(null,
                                  "myArtifact",
                                  "1.0"));

        to = new MavenPluginUpdater(to).update(from);

        assertTrue(to.isEmpty());
    }

    @Test
    public void testChangeVersion() throws Exception {
        List<Plugin> to = new ArrayList<Plugin>();
        to.add(makeMavenPlugin("myPlugin",
                               "myArtifact",
                               "1.0"));
        ArrayList<org.guvnor.common.services.project.model.Plugin> from = new ArrayList<org.guvnor.common.services.project.model.Plugin>();
        from.add(makeGuvnorPlugin("myPlugin",
                                  "myArtifact",
                                  "2.0"));

        to = new MavenPluginUpdater(to).update(from);

        assertEquals(1,
                     to.size());
        assertEquals("2.0",
                     to.get(0).getVersion());
    }

    @Test
    public void testAddNew() throws Exception {
        List<Plugin> to = new ArrayList<Plugin>();
        to.add(makeMavenPlugin("myPlugin",
                               "myArtifact",
                               "1.0"));
        ArrayList<org.guvnor.common.services.project.model.Plugin> from = new ArrayList<org.guvnor.common.services.project.model.Plugin>();
        from.add(makeGuvnorPlugin("myPlugin",
                                  "myArtifact",
                                  "1.0"));
        from.add(makeGuvnorPlugin("junit",
                                  "junit",
                                  "1.44"));

        to = new MavenPluginUpdater(to).update(from);

        assertEquals(2,
                     to.size());
        assertEquals("1.0",
                     to.get(0).getVersion());
        assertEquals("1.44",
                     to.get(1).getVersion());
    }

    @Test
    public void testRemove() throws Exception {
        List<Plugin> to = new ArrayList<Plugin>();
        to.add(makeMavenPlugin("myPlugin",
                               "myArtifact",
                               "1.0"));
        to.add(makeMavenPlugin("junit",
                               "junit",
                               "1.44"));
        ArrayList<org.guvnor.common.services.project.model.Plugin> from = new ArrayList<org.guvnor.common.services.project.model.Plugin>();
        from.add(makeGuvnorPlugin("myPlugin",
                                  "myArtifact",
                                  "1.0"));

        to = new MavenPluginUpdater(to).update(from);

        assertEquals(1,
                     to.size());
        assertEquals("1.0",
                     to.get(0).getVersion());
    }
}