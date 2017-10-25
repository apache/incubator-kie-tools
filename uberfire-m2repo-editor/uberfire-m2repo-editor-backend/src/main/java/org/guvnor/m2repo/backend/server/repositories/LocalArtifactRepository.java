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

import org.appformer.maven.integration.Aether;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.installation.InstallRequest;
import org.eclipse.aether.installation.InstallationException;
import org.guvnor.common.services.project.model.GAV;

public class LocalArtifactRepository implements ArtifactRepository {

    private String name;

    public LocalArtifactRepository() {
    }

    public LocalArtifactRepository(final String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getRootDir() {
        return Aether.getAether()
                .getLocalRepository()
                .getUrl()
                .replaceAll("^file:",
                            "")
                .replaceAll(File.separatorChar + "$",
                            "");
    }

    @Override
    public Collection<File> listFiles(final List<String> wildcards) {
        return Collections.EMPTY_LIST;
    }

    @Override
    public Collection<Artifact> listArtifacts(final List<String> wildcards) {
        return Collections.EMPTY_LIST;
    }

    @Override
    public boolean containsArtifact(final GAV gav) {
        return false;
    }

    @Override
    public File getArtifactFileFromRepository(final GAV gav) {
        return null;
    }

    @Override
    public boolean isRepository() {
        return true;
    }

    @Override
    public boolean isPomRepository() {
        return true;
    }

    @Override
    public void deploy(final String pom,
                       Artifact... artifacts) {

        try {
            final InstallRequest installRequest = new InstallRequest();
            for (Artifact artifact : artifacts) {
                installRequest
                        .addArtifact(artifact);
            }
            Aether.getAether().getSystem().install(Aether.getAether().getSession(),
                                                   installRequest);
        } catch (InstallationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(final GAV gav) {

    }
}
