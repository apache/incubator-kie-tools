/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
 */

package org.kie.workbench.common.screens.library.api;

import java.util.Objects;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.commons.clusterapi.Clustered;

@Portable
@Clustered
public class RepositoryFileListUpdatedEvent {

    private final String repositoryId;

    private final String branchName;

    public RepositoryFileListUpdatedEvent(final @MapsTo("repositoryId") String repositoryId,
                                          final @MapsTo("branchName") String branchName) {
        this.repositoryId = repositoryId;
        this.branchName = branchName;
    }

    public String getRepositoryId() {
        return repositoryId;
    }

    public String getBranchName() {
        return branchName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RepositoryFileListUpdatedEvent that = (RepositoryFileListUpdatedEvent) o;
        return repositoryId.equals(that.repositoryId) &&
                Objects.equals(branchName, that.branchName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(repositoryId,
                            branchName);
    }
}
