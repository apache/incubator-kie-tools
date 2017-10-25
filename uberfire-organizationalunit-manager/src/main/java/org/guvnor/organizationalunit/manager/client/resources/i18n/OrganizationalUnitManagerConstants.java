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
package org.guvnor.organizationalunit.manager.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

/**
 * i18n constants for Organizational Unit Manager
 */
public interface OrganizationalUnitManagerConstants
        extends
        Messages {

    OrganizationalUnitManagerConstants INSTANCE = GWT.create(OrganizationalUnitManagerConstants.class);

    String OrganizationalUnits();

    String OrganizationalUnitRepositories();

    String AllRepositories();

    String OrganizationalUnitManagerTitle();

    String AddOrganizationalUnitPopupTitle();

    String EditOrganizationalUnitPopupTitle();

    String OrganizationalUnitNameIsMandatory();

    String DefaultGroupIdIsMandatory();

    String InvalidGroupId();

    String DefaultGroupId();

    String GroupIdInfo();

    String NoOrganizationalUnitsDefined();

    String NoRepositoriesDefined();

    String NoRepositoriesAvailable();

    String NoOrganizationalUnitSelected();

    String AddOrganizationalUnit();

    String DeleteOrganizationalUnit();

    String ConfirmOrganizationalUnitDeletion0(String organizationalUnitName);

    String OrganizationalUnitAlreadyExists();

    String EditOrganizationalUnit();

    String Wait();

    String OrganizationalUnitInformation();

    String Name();

    String OrganizationalUnitName();

    String DefaultGroupID();

    String Owner();

    String OrganizationalUnitOwner();
}