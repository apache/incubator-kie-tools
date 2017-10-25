/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.guvnor.structure.repositories;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Portable
public class NewBranchEvent {

    private String repositoryAlias;

    private String branchName;
    private Path branchPath;
    private Long timestamp;

    public NewBranchEvent() {
    }

    public NewBranchEvent(final String repositoryAlias,
                          final String branchName,
                          final Path branchPath,
                          final Long timestamp) {
        this.repositoryAlias = checkNotNull("repositoryAlias",
                                            repositoryAlias);
        this.branchName = checkNotNull("branchName",
                                       branchName);
        this.branchPath = checkNotNull("branchPath",
                                       branchPath);
        this.timestamp = checkNotNull("timestamp",
                                      timestamp);
    }

    public String getBranchName() {
        return branchName;
    }

    public String getRepositoryAlias() {
        return repositoryAlias;
    }

    public Path getBranchPath() {
        return branchPath;
    }

    public Long getTimestamp() {
        return timestamp;
    }
}