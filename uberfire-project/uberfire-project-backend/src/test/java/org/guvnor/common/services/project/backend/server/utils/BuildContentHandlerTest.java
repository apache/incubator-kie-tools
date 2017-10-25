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

import org.apache.maven.model.Build;
import org.apache.maven.model.Plugin;
import org.junit.Test;

import static org.guvnor.common.services.project.backend.server.utils.TestUtils.makeGuvnorPlugin;
import static org.guvnor.common.services.project.backend.server.utils.TestUtils.makeMavenPlugin;
import static org.junit.Assert.*;

public class BuildContentHandlerTest {

    @Test
    public void testBuildPluginUpdateExisting() throws Exception {
        org.guvnor.common.services.project.model.Build from = new org.guvnor.common.services.project.model.Build();
        from.getPlugins().add(makeGuvnorPlugin("myGroup",
                                               "myArtifact",
                                               "1.0"));

        Build to = new Build();
        Plugin toPlugin = makeMavenPlugin("myGroup",
                                          "myArtifact",
                                          "0.11.11.12");
        to.getPlugins().add(toPlugin);
        toPlugin.setGoals("someGoal");
        to.setSourceDirectory("someDirectory");

        to = new BuildContentHandler().update(from,
                                              to);

        assertEquals(1,
                     to.getPlugins().size());
        assertEquals("1.0",
                     to.getPlugins().get(0).getVersion());
        assertEquals("someGoal",
                     to.getPlugins().get(0).getGoals());
        assertEquals("someDirectory",
                     to.getSourceDirectory());
    }

    @Test
    public void testBuildPluginDeletePlugin() throws Exception {
        org.guvnor.common.services.project.model.Build from = new org.guvnor.common.services.project.model.Build();
        from.getPlugins().add(makeGuvnorPlugin("myGroup",
                                               "myArtifact",
                                               "1.0"));

        Build to = new Build();
        to.getPlugins().add(makeMavenPlugin("myGroup",
                                            "myArtifact",
                                            "1.0"));
        to.getPlugins().add(makeMavenPlugin("junit",
                                            "junit",
                                            "1.44"));

        to = new BuildContentHandler().update(from,
                                              to);

        assertEquals(1,
                     to.getPlugins().size());
        assertEquals("1.0",
                     to.getPlugins().get(0).getVersion());
    }
}