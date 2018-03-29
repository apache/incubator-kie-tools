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
package org.guvnor.structure.backend.repositories;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

public class ConfiguredRepositoriesBySpace {

    private Map<String, Repository> repositoriesByAlias = Collections.synchronizedMap(new HashMap<>());
    private Map<Path, Repository> repositoriesByBranchRoot = Collections.synchronizedMap(new HashMap<>());

    public void clear() {

        repositoriesByAlias.clear();
        repositoriesByBranchRoot.clear();
    }

    public void add(Repository repository) {
        repositoriesByAlias.put(repository.getAlias(),
                                repository);

        if (repository.getBranches() != null) {
            for (final Branch branch : repository.getBranches()) {
                repositoriesByBranchRoot.put(Paths.normalizePath(branch.getPath()),
                                             repository);
            }
        }
    }

    public Repository get(String alias) {
        return repositoriesByAlias.get(alias);
    }

    public Map<String, Repository> getRepositoriesByAlias() {
        return repositoriesByAlias;
    }

    public Repository get(Path root) {
        return repositoriesByBranchRoot.get(Paths.normalizePath(root));
    }

    public boolean containsRepository(String alias) {
        return repositoriesByAlias.containsKey(alias);
    }

    Repository remove(final String alias) {

        final Repository removed = repositoriesByAlias.remove(alias);

        removeFromRootByAlias(alias);

        return removed;
    }

    void removeFromRootByAlias(final String alias) {
        for (Path path : findFromRootMapByAlias(alias)) {
            repositoriesByBranchRoot.remove(path);
        }
    }

    private List<Path> findFromRootMapByAlias(final String alias) {
        List<Path> result = new ArrayList<>();
        for (Path path : repositoriesByBranchRoot.keySet()) {
            if (repositoriesByBranchRoot.get(path).getAlias().equals(alias)) {
                result.add(path);
            }
        }
        return result;
    }

    public Collection<Repository> getAllConfiguredRepositories() {
        return repositoriesByAlias.values();
    }
}
