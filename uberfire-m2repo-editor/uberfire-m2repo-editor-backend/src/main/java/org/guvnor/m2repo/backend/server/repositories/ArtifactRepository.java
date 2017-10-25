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
import java.util.List;

import org.eclipse.aether.artifact.Artifact;
import org.guvnor.common.services.project.model.GAV;

/**
 * Represents an artifact repository. Every repository should implement this interface.
 */
public interface ArtifactRepository {

    /**
     * Returns the name of the repository, an identifier
     * @return the name
     */
    String getName();

    /**
     * Return the root dir of a repository, if it doesn't have one, return null
     * @return the root dir of a repository.
     */
    String getRootDir();

    /**
     * List repository files filtered by wildcards
     * @param wildcards the filtering wildcards
     * @return the files
     */
    Collection<File> listFiles(final List<String> wildcards);

    /**
     * List repository artifacts filtered by wildcards
     * @param wildcards the filtering wildcards
     * @return the artifacts
     */
    Collection<Artifact> listArtifacts(final List<String> wildcards);

    /**
     * Deploy a list of artifact into a repository
     * @param pom the artifact pom
     * @param artifacts the list of artifacts
     */
    void deploy(String pom,
                Artifact... artifacts);

    /**
     * Delete an artifact from the repository
     * @param gav the GAV identifier of the artifact to be deleted
     */
    void delete(final GAV gav);

    /**
     * Checks whether this Maven repository contains the specified artifact (GAV).
     * <p>
     * As opposed to ${code {@link #getArtifactFileFromRepository(GAV)}}, this method will not log any WARNings in case
     * the artifact is not present (the Aether exception is only logged as TRACE message).
     * @param gav artifact GAV, never null
     * @return true if the this Maven repo contains the specified artifact, otherwise false
     */
    boolean containsArtifact(final GAV gav);

    /**
     * Return an artifact from the repository
     * @param gav the GAV identifier
     * @return the artifact found
     */
    File getArtifactFileFromRepository(final GAV gav);

    /**
     * Identifies if is a repository that admit artifacts
     * @return
     */
    boolean isRepository();

    /**
     * Identifies if is a repository that admit pom artifacts
     * @return
     */
    boolean isPomRepository();
}
