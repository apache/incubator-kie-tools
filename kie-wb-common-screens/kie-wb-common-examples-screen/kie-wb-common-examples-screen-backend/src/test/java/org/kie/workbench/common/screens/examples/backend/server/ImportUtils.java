/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.kie.workbench.common.screens.examples.backend.server;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.uberfire.backend.vfs.Path;
import org.uberfire.spaces.Space;

import static org.mockito.Mockito.*;

public class ImportUtils {

    public static GitRepository makeGitRepository() {
        final GitRepository repository = new GitRepository("guvnorng-playground",
                                                           new Space("space"));

        final Map<String, Branch> branches = Collections.singletonMap("master",
                                                                      new Branch("master",
                                                                                 mock(Path.class)));
        repository.setBranches(branches);

        return repository;
    }
}
