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

package org.guvnor.m2repo.backend.server.helpers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.maven.project.ProjectBuildingException;
import org.appformer.maven.integration.embedder.MavenEmbedderException;
import org.appformer.maven.support.PomModel;
import org.appformer.maven.support.AFReleaseId;
import org.appformer.maven.support.AFReleaseIdImpl;
import org.guvnor.m2repo.backend.server.GuvnorM2Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PomModelResolver {

    private static final Logger log = LoggerFactory.getLogger(PomModelResolver.class);

    /**
     * Construct a PomModel from a JAR by parsing first the pom.xml file within the JAR
     * and if not present a pom.properties file in the JAR.
     * @param jarStream InputStream to the JAR
     * @return a populated PomModel or null if neither pom.xml or pom.properties existed in the JAR
     */
    public static PomModel resolveFromJar(InputStream jarStream) {
        //Attempt to load JAR's POM information from it's pom.xml file
        PomModel pomModel = null;
        try {
            String pomXML = GuvnorM2Repository.loadPomFromJar(jarStream);
            if (pomXML != null) {
                pomModel = PomModel.Parser.parse("pom.xml",
                                                 new ByteArrayInputStream(pomXML.getBytes()));
            }
        } catch (Exception e) {
            log.info("Failed to parse pom.xml for GAV information. Falling back to pom.properties.",
                     e);
        }

        //Attempt to load JAR's POM information from it's pom.properties file
        if (pomModel == null) {
            try {
                jarStream.reset();
                String pomProperties = GuvnorM2Repository.loadPomPropertiesFromJar(jarStream);
                if (pomProperties != null) {
                    final AFReleaseId releaseId = AFReleaseIdImpl.fromPropertiesString(pomProperties);
                    pomModel = new PomModel.InternalModel();
                    ((PomModel.InternalModel) pomModel).setReleaseId(releaseId);
                }
            } catch (Exception e) {
                log.info("Failed to parse pom.properties for GAV information.");
            }
        }
        return pomModel;
    }

    /**
     * Construct a PomModel from a pom.xml file
     * @param pomStream InputStream to the pom.xml file
     * @return a populated PomModel or throws exception if the file could not be parsed or deployed
     */
    public static PomModel resolveFromPom(InputStream pomStream) throws Exception {

        try {
            return PomModel.Parser.parse("pom.xml",
                                         pomStream);
        } catch (final Exception e) {
            if (e.getCause() != null) {
                if (e.getCause() instanceof ProjectBuildingException) {
                    throw (ProjectBuildingException) e.getCause();
                }
                if (e.getCause() instanceof MavenEmbedderException) {
                    throw (MavenEmbedderException) e.getCause();
                }
            }

            log.info("Failed to process pom.xml for GAV information.",
                     e);
            throw e;
        }
    }
}
