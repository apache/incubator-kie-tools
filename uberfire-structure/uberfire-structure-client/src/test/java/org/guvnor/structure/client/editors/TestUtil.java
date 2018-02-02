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

package org.guvnor.structure.client.editors;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.uberfire.backend.vfs.Path;

import static org.mockito.Mockito.*;

public class TestUtil {

    public static Repository makeRepository(final String alias,
                                            final String... branches) {
        final Repository repository = mock(Repository.class);

        when(repository.getAlias()).thenReturn(alias);

        final List<Branch> branchList = new ArrayList<>();
        for (final String branchName : branches) {
            branchList.add(new Branch(branchName,
                                      mock(Path.class)));
        }

        when(repository.getBranches()).thenReturn(branchList);

        when(repository.getDefaultBranch()).thenReturn(Optional.of(new Branch("master",
                                                                              mock(Path.class))));

        return repository;
    }
}
