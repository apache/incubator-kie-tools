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
package org.guvnor.common.services.project.service;

import java.util.HashSet;
import java.util.Set;

import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.MavenRepositoryMetadata;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.soup.commons.validation.PortablePreconditions;

/**
 * Exception for when a proposed GAV for a Project resolves against Maven's {@link org.eclipse.aether.repository.LocalRepository} and any
 * {@link org.eclipse.aether.repository.RemoteRepository} configured in the Project's POM, {@code <distributionManagement>} or {@code settings.xml}
 */
@Portable
public class GAVAlreadyExistsException extends RuntimeException {

    private GAV gav;
    private Set<MavenRepositoryMetadata> repositories = new HashSet<MavenRepositoryMetadata>();

    public GAVAlreadyExistsException() {
        super();
    }

    public GAVAlreadyExistsException(final GAV gav,
                                     final Set<MavenRepositoryMetadata> repositories) {
        super("Requested GAV (" + gav + ") already exists. Please check 'repositories' collection.");
        this.gav = PortablePreconditions.checkNotNull("gav",
                                                      gav);
        this.repositories.addAll(PortablePreconditions.checkNotNull("repositories",
                                                                    repositories));
    }

    public GAV getGAV() {
        return gav;
    }

    public Set<MavenRepositoryMetadata> getRepositories() {
        return repositories;
    }
}
