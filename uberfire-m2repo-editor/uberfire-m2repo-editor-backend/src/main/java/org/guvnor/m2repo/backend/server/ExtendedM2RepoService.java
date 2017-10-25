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

package org.guvnor.m2repo.backend.server;

import java.io.InputStream;

import org.guvnor.common.services.project.model.GAV;
import org.guvnor.m2repo.service.M2RepoService;

public interface ExtendedM2RepoService extends M2RepoService {

    /**
     * Deploy JAR to all repositories (this includes Guvnor's internal Maven Repository,
     * external Repositories configured by Distribution Management in the JAR's pom.xml
     * and external Repositories configured by active Profiles in settings.xml).
     * @param is InputStream holding JAR
     * @param gav GAV representing the JAR
     */
    void deployJar(InputStream is,
                   GAV gav);

    /**
     * Convenience method for unit tests - to avoid deploying to additional (possibly external) repositories
     * @param is InputStream holding JAR
     * @param gav GAV representing the JAR
     */
    void deployJarInternal(InputStream is,
                           GAV gav);

    /**
     * Deploy POM to local Maven Repository and Workbench's remote Maven Repository.
     * @param is InputStream holding POM
     * @param gav GAV representing the POM
     */
    void deployPom(InputStream is,
                   GAV gav);
}
