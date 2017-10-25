/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
 *
 */

package org.guvnor.m2repo.backend.server.repositories;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.aether.artifact.Artifact;
import org.guvnor.common.services.project.model.GAV;

public class NullArtifactRepository implements ArtifactRepository {

    private static final String NULL_ARTIFACT_REPOSITORY_NAME = "null-m2-repo";

    @Override
    public String getName() {
        return NULL_ARTIFACT_REPOSITORY_NAME;
    }

    @Override
    public String getRootDir() {
        return null;
    }

    @Override
    public Collection<File> listFiles(List<String> wildcards) {
        return Collections.emptyList();
    }

    @Override
    public Collection<Artifact> listArtifacts(List<String> wildcards) {
        return Collections.emptyList();
    }

    @Override
    public void deploy(String pom,
                       Artifact... artifacts) {

    }

    @Override
    public void delete(GAV gav) {

    }

    @Override
    public boolean containsArtifact(GAV gav) {
        return false;
    }

    @Override
    public File getArtifactFileFromRepository(GAV gav) {
        return null;
    }

    @Override
    public boolean isRepository() {
        return false;
    }

    @Override
    public boolean isPomRepository() {
        return false;
    }
}
