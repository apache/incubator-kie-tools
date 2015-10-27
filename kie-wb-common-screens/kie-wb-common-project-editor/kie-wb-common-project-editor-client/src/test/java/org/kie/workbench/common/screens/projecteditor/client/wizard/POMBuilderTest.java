/*
 * Copyright 2015 JBoss Inc
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
package org.kie.workbench.common.screens.projecteditor.client.wizard;

import java.util.ArrayList;

import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Plugin;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class POMBuilderTest {

    private POMBuilder pomBuilder;

    @Before
    public void setUp() throws Exception {
        pomBuilder = new POMBuilder();
    }

    @Test
    public void testDefaultVersion() throws Exception {
        assertEquals( "1.0", pomBuilder.build().getGav().getVersion() );
    }

    @Test
    public void testDefaultPackaging() throws Exception {
        assertEquals( "kjar", pomBuilder.build().getPackaging() );
    }

    @Test
    public void testContainsKieMavenPlugin() throws Exception {

        pomBuilder.addKieBuildPlugin( "1.2.3" );
        ArrayList<Plugin> plugins = pomBuilder.build().getBuild().getPlugins();

        assertEquals( 1, plugins.size() );

        assertEquals( "org.kie", plugins.get( 0 ).getGroupId() );
        assertEquals( "kie-maven-plugin", plugins.get( 0 ).getArtifactId() );
        assertEquals( "1.2.3", plugins.get( 0 ).getVersion() );
    }

    @Test
    public void testSetGAV() throws Exception {
        POM pom = pomBuilder
                .setProjectName( "projectName" )
                .setGroupId( "my.group" )
                .setVersion( "2.0" )
                .build();

        assertEquals( "projectName", pom.getName() );
        assertEquals( "projectName", pom.getGav().getArtifactId() );
        assertEquals( "my.group", pom.getGav().getGroupId() );
        assertEquals( "2.0", pom.getGav().getVersion() );
    }

    @Test
    public void testSetName() throws Exception {
        POM pom = pomBuilder
                .setProjectName( "project name with spaces!" )
                .build();

        assertEquals( "project name with spaces!", pom.getName() );
        assertEquals( "projectnamewithspaces", pom.getGav().getArtifactId() );
    }
}