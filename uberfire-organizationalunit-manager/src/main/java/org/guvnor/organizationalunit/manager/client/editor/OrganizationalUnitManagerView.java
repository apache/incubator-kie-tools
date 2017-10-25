/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.guvnor.organizationalunit.manager.client.editor;

import java.util.Collection;

import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;

public interface OrganizationalUnitManagerView extends UberView<OrganizationalUnitManagerPresenter>,
                                                       HasBusyIndicator {

    void reset();

    void setOrganizationalUnits(final Collection<OrganizationalUnit> organizationalUnits);

    void setOrganizationalUnitRepositories(final Collection<Repository> repositories,
                                           final Collection<Repository> availableRepositories);

    void addOrganizationalUnit(final OrganizationalUnit organizationalUnit);

    void deleteOrganizationalUnit(final OrganizationalUnit organizationalUnit);

    void setAddOrganizationalUnitEnabled(boolean enabled);

    void setEditOrganizationalUnitEnabled(boolean enabled);

    void setDeleteOrganizationalUnitEnabled(boolean enabled);
}
