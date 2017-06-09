/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datasource.management.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import javax.enterprise.context.ApplicationScoped;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;
import org.appformer.maven.integration.embedder.MavenProjectLoader;
import org.guvnor.common.services.project.backend.server.utils.POMContentHandler;
import org.guvnor.common.services.project.model.Dependency;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class MavenArtifactResolver {

    private static final Logger logger = LoggerFactory.getLogger( MavenArtifactResolver.class );

    private POMContentHandler pomContentHandler = new POMContentHandler();

    public MavenArtifactResolver() {
    }

    public URI resolve( final String groupId, final String artifactId, final String version ) throws Exception {

        final POM projectPom = new POM( new GAV( "resolver-dummy-group",
                "resolver-dummy-artifact",
                "resolver-dummy-version" ) );

        projectPom.getDependencies().add( new Dependency( new GAV( groupId, artifactId, version ) ) );

        try {

            final String pomXML = pomContentHandler.toString( projectPom );

            final InputStream pomStream = new ByteArrayInputStream( pomXML.getBytes( StandardCharsets.UTF_8 ) );
            final MavenProject mavenProject = MavenProjectLoader.parseMavenPom( pomStream );

            for ( Artifact mavenArtifact : mavenProject.getArtifacts() ) {
                if ( groupId.equals( mavenArtifact.getGroupId() ) &&
                        artifactId.equals( mavenArtifact.getArtifactId() ) &&
                        version.equals( mavenArtifact.getVersion() ) &&
                        mavenArtifact.getFile().exists() ) {
                    return mavenArtifact.getFile().toURI();
                }
            }

            return null;
        } catch ( IOException e ) {
            logger.error( "Unable to get artifact: " + groupId + ":" + artifactId + ":" + version +
                    " from maven repository", e );
            throw new Exception( "Unable to get artifact: " + groupId + ":" + artifactId + ":" + version +
                    " from maven repository", e );
        }
    }

}
