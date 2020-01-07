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

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.commons.clusterapi.Clustered;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Portable
@Clustered
public class NewBranchEvent {

    private final Repository repository;
    private final String newBranchName;
    private final String fromBranchName;
    private final String userIdentifier;

    public NewBranchEvent(@MapsTo("repository") final Repository repository,
                          @MapsTo("newBranchName") final String newBranchName,
                          @MapsTo("fromBranchName") final String fromBranchName,
                          @MapsTo("userIdentifier") final String userIdentifier) {

        this.repository = checkNotNull("repository", repository);
        this.newBranchName = checkNotNull("newBranchName", newBranchName);
        this.fromBranchName = checkNotNull("fromBranchName", fromBranchName);
        this.userIdentifier = checkNotNull("userIdentifier", userIdentifier);
    }

    public Repository getRepository() {
        return repository;
    }

    public String getNewBranchName() {
        return newBranchName;
    }

    public String getFromBranchName() {
        return fromBranchName;
    }

    public String getUserIdentifier() {
        return userIdentifier;
    }
}
