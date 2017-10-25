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

import com.google.gwt.user.client.Command;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.RemoteCallback;

public interface OrganizationalUnitManagerPresenter {

    void loadOrganizationalUnits();

    void organizationalUnitSelected(final OrganizationalUnit organizationalUnit);

    void addNewOrganizationalUnit();

    void createNewOrganizationalUnit(final String organizationalUnitName,
                                     final String organizationalUnitOwner,
                                     final String defaultGroupId);

    void editOrganizationalUnit(final OrganizationalUnit organizationalUnit);

    void saveOrganizationalUnit(final String organizationalUnitName,
                                final String organizationalUnitOwner,
                                final String defaultGroupId);

    void deleteOrganizationalUnit(final OrganizationalUnit organizationalUnit);

    void addOrganizationalUnitRepository(final OrganizationalUnit organizationalUnit,
                                         final Repository repository);

    void removeOrganizationalUnitRepository(final OrganizationalUnit organizationalUnit,
                                            final Repository repository);

    void checkIfOrganizationalUnitExists(final String organizationalUnitName,
                                         final Command onSuccessCommand,
                                         final Command onFailureCommand);

    void checkValidGroupId(final String proposedGroupId,
                           RemoteCallback<Boolean> callback);

    void getSanitizedGroupId(final String proposedGroupId,
                             RemoteCallback<String> callback);
}
