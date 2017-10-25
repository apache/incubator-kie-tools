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

package org.guvnor.structure.organizationalunit;

import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class RepoRemovedFromOrganizationalUnitEvent extends OrganizationalUnitEventBase {

    private Repository repository;

    public RepoRemovedFromOrganizationalUnitEvent() {
    }

    public RepoRemovedFromOrganizationalUnitEvent(final OrganizationalUnit organizationalUnit,
                                                  final Repository repository,
                                                  final String userName) {
        super(organizationalUnit,
              userName);
        this.repository = repository;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }
}
