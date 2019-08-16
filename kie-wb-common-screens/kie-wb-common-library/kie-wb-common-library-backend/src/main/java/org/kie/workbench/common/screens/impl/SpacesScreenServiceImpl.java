/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.guvnor.structure.contributors.Contributor;
import org.guvnor.structure.contributors.ContributorType;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.kie.workbench.common.screens.library.api.SpacesScreenService;
import org.uberfire.rpc.SessionInfo;

@ApplicationScoped
public class SpacesScreenServiceImpl implements SpacesScreenService {

    private OrganizationalUnitService organizationalUnitService;

    private SessionInfo sessionInfo;

    public SpacesScreenServiceImpl() {
    }

    @Inject
    public SpacesScreenServiceImpl(final OrganizationalUnitService organizationalUnitService,
                                   final SessionInfo sessionInfo) {
        this.organizationalUnitService = organizationalUnitService;
        this.sessionInfo = sessionInfo;
    }

    @Override
    public Collection<OrganizationalUnit> getSpaces() {
        return organizationalUnitService.getOrganizationalUnits(true);
    }

    @Override
    public OrganizationalUnit getSpace(final String name) {
        return organizationalUnitService.getOrganizationalUnit(name, true);
    }

    @Override
    public boolean isValidGroupId(final String groupId) {
        return organizationalUnitService.isValidGroupId(groupId);
    }

    @Override
    public Response postSpace(final NewSpace newSpace) {
        organizationalUnitService.createOrganizationalUnit(newSpace.name, newSpace.groupId, new ArrayList<>(), getContributors());
        return Response.status(201).build();
    }

    private List<Contributor> getContributors() {
        final List<Contributor> contributors = new ArrayList<>();
        contributors.add(new Contributor(sessionInfo.getIdentity().getIdentifier(), ContributorType.OWNER));

        return contributors;
    }
}
