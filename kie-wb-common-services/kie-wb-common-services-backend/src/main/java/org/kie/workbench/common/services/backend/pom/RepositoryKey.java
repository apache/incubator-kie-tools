/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.backend.pom;

import java.util.Objects;

import org.apache.maven.model.Repository;
import org.kie.soup.commons.validation.PortablePreconditions;

public class RepositoryKey {

    private Repository repository;

    public RepositoryKey(Repository repo) {
        PortablePreconditions.checkNotNull("RepositoryKey", repo);
        this.repository = repo;
    }

    public Repository getRepository() {
        return repository;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (!(o instanceof RepositoryKey)) {
            return false;
        }
        RepositoryKey that = (RepositoryKey) o;
        return Objects.equals(repository, that.repository);
    }

    @Override
    public int hashCode() {
        return Objects.hash(repository);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RepositoryKey{");
        sb.append("repository=").append(repository);
        sb.append('}');
        return sb.toString();
    }
}
