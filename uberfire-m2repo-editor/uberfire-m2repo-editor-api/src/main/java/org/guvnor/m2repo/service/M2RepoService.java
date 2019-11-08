/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.m2repo.service;

import org.guvnor.common.services.project.model.GAV;
import org.guvnor.m2repo.model.JarListPageRequest;
import org.guvnor.m2repo.model.JarListPageRow;
import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.paging.PageResponse;

@Remote
public interface M2RepoService {

    /**
     * Retrieve the pom.xml from the given Path. The path may represent either a JAR, KJAR or pom.xml file
     * @param path The path to the file
     * @return The pom.xml text
     */
    String getPomText(String path);

    /**
     * Retrieve the GAV details from a JAR or KJAR
     * @param path The path to the artifact
     * @return The GAV within the artifact
     */
    GAV loadGAVFromJar(String path);

    /**
     * Query the repository for a list of artifacts
     * @param pageRequest Request for required artifacts
     * @return Response containing artifacts
     */
    PageResponse<JarListPageRow> listArtifacts(JarListPageRequest pageRequest);

    /**
     * Get the repository's URL
     * @return A String representing the repository's URL relative to the container's root
     */
    String getRepositoryURL();

    /**
     * Retrieve the kmodule.xml from the given Path.
     * @param path The path to the file
     * @return The kmodule.xml text
     */
    String getKModuleText(String path);

    /**
     * Retrieve the kie-deployment-descriptor.xml from the given Path.
     * @param path The path to the file
     * @return The kie-deployment-descriptor.xml text
     */
    String getKieDeploymentDescriptorText(String path);
}
