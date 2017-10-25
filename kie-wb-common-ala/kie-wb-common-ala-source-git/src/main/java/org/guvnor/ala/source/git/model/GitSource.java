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

package org.guvnor.ala.source.git.model;

import org.guvnor.ala.config.CloneableConfig;
import org.guvnor.ala.source.Source;
import org.guvnor.ala.source.git.GitRepository;
import org.uberfire.java.nio.file.Path;

/*
 * This class represent a Git Source representation. Containing references to 
 *  the path where the repository is stored and the repository itself. 
*/
public class GitSource implements Source,
                                  CloneableConfig<GitSource> {

    private final GitRepository repository;
    private final Path path;

    public GitSource(final GitRepository repository,
                     final Path path) {
        this.repository = repository;
        this.path = path;
    }

    @Override
    public Path getPath() {
        return path;
    }

    public GitRepository getRepository() {
        return repository;
    }

    @Override
    public GitSource asNewClone(final GitSource source) {
        return new GitSource(source.getRepository(),
                             source.getPath());
    }
}