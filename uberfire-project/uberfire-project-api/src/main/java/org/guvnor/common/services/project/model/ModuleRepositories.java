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
package org.guvnor.common.services.project.model;

import java.util.HashSet;
import java.util.Set;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.soup.commons.validation.PortablePreconditions;

@Portable
public class ModuleRepositories {

    private Set<ModuleRepository> repositories;

    public ModuleRepositories() {
        this.repositories = new HashSet<>();
    }

    public ModuleRepositories(final @MapsTo("repositories") Set<ModuleRepository> repositories) {
        this.repositories = repositories;
    }

    public Set<ModuleRepository> getRepositories() {
        return repositories;
    }

    public MavenRepositoryMetadata[] filterByIncluded() {
        final Set<MavenRepositoryMetadata> filter = new HashSet<>();
        for (ModuleRepository pr : repositories) {
            if (pr.isIncluded()) {
                filter.add(pr.getMetadata());
            }
        }
        final MavenRepositoryMetadata[] aFilter = new MavenRepositoryMetadata[filter.size()];
        filter.toArray(aFilter);
        return aFilter;
    }

    @Portable
    public static class ModuleRepository {

        private boolean include;
        private MavenRepositoryMetadata metadata;

        public ModuleRepository(final @MapsTo("include") boolean include,
                                final @MapsTo("metadata") MavenRepositoryMetadata metadata) {
            this.include = include;
            this.metadata = PortablePreconditions.checkNotNull("metadata",
                                                               metadata);
        }

        public boolean isIncluded() {
            return include;
        }

        public void setIncluded(final boolean include) {
            this.include = include;
        }

        public MavenRepositoryMetadata getMetadata() {
            return metadata;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof ModuleRepository)) {
                return false;
            }

            ModuleRepository that = (ModuleRepository) o;

            if (include != that.include) {
                return false;
            }
            return metadata.equals(that.metadata);
        }

        @Override
        public int hashCode() {
            int result = (include ? 1 : 0);
            result = 31 * result + metadata.hashCode();
            result = ~~result;
            return result;
        }
    }
}
