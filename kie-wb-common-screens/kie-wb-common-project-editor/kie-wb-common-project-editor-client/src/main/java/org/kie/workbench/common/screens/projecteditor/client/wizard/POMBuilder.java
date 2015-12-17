/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import org.guvnor.common.services.project.model.Build;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Plugin;

/**
 * The Project Name is used to generate the folder name and hence is only checked to be a valid file name.
 * The ArtifactID is initially set to the project name, subsequently validated against the maven regex,
 * and preserved as is in the pom.xml file. However, as it is used to construct the default workspace and
 * hence package names, it is sanitized in the ProjectService.newProject() method.
 */
public class POMBuilder {

    private static final String KIE_MAVEN_PLUGIN_GROUP_ID = "org.kie";
    private static final String KIE_MAVEN_PLUGIN_ARTIFACT_ID = "kie-maven-plugin";

    private final POM pom;

    public POMBuilder() {
        this( new POM() );
    }

    public POMBuilder( final POM pom ) {
        this.pom = pom;
        setDefaultPackaging( pom );
        setDefaultVersion( pom );
    }

    private void setDefaultVersion( POM pom ) {
        if ( pom.getGav().getVersion() == null ) {
            this.pom.getGav().setVersion( "1.0" );
        }
    }

    private void setDefaultPackaging( POM pom ) {
        if ( pom.getPackaging() == null ) {
            this.pom.setPackaging( "kjar" );
        }
    }

    public POMBuilder setProjectName( final String projectName ) {
        pom.setName( projectName );
        if ( projectName != null ) {
            pom.getGav().setArtifactId( sanitizeProjectName( projectName ) );
        }
        return this;
    }

    public POMBuilder setGroupId( final String groupId ) {
        pom.getGav().setGroupId( groupId );
        return this;
    }

    public POMBuilder setVersion( final String version ) {
        pom.getGav().setVersion( version );
        return this;
    }

    public POMBuilder setPackaging( final String packaging ) {
        pom.setPackaging( packaging );
        return this;
    }

    public POMBuilder addKieBuildPlugin( final String kieVersion ) {
        if ( pom.getBuild() == null ) {
            pom.setBuild( new Build() );
        }

        pom.getBuild().getPlugins().add( getKieMavenPlugin( kieVersion ) );
        return this;
    }

    public POM build() {
        return pom;
    }

    /**
     * The projectName has been validated as a FileSystem folder name, which may not be consistent with Maven ArtifactID
     * naming restrictions (see org.apache.maven.model.validation.DefaultModelValidator.java::ID_REGEX). Therefore we'd
     * best sanitize the projectName
     */
    private String sanitizeProjectName( final String projectName ) {
        //Only [A-Za-z0-9_\-.] are valid so strip everything else out
        return projectName != null ? projectName.replaceAll( "[^A-Za-z0-9_\\-.]", "" ) : projectName;
    }

    private Plugin getKieMavenPlugin( final String kieVersion ) {
        final Plugin plugin = new Plugin();
        plugin.setGroupId( KIE_MAVEN_PLUGIN_GROUP_ID );
        plugin.setArtifactId( KIE_MAVEN_PLUGIN_ARTIFACT_ID );
        plugin.setVersion( kieVersion );
        plugin.setExtensions( true );
        return plugin;
    }

}
