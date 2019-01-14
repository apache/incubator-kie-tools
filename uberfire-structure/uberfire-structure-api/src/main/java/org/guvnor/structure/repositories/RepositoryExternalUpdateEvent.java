/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
import org.uberfire.commons.clusterapi.Clustered;
import org.uberfire.spaces.Space;

@Portable
@Clustered
public class RepositoryExternalUpdateEvent {

    private Repository repository;

    public RepositoryExternalUpdateEvent() {
    }

    public RepositoryExternalUpdateEvent(final Repository repository) {
        this.repository = repository;
    }

    public Repository getRepository() {
        return repository;
    }
}
