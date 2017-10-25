/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.structure.backend.organizationalunit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitSearchService;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.jboss.errai.bus.server.annotations.Service;

@Service
@ApplicationScoped
public class OrganizationalUnitSearchServiceImpl implements OrganizationalUnitSearchService {

    private OrganizationalUnitService organizationalUnitService;

    @Inject
    public OrganizationalUnitSearchServiceImpl(OrganizationalUnitService organizationalUnitService) {
        this.organizationalUnitService = organizationalUnitService;
    }

    @Override
    public Collection<OrganizationalUnit> searchByName(String pattern,
                                                       int maxItems,
                                                       boolean caseSensitive) {
        List<OrganizationalUnit> results = new ArrayList<>();
        for (OrganizationalUnit unit : organizationalUnitService.getAllOrganizationalUnits()) {
            String name = unit.getName();
            if (caseSensitive ? name.contains(pattern) : name.toLowerCase().contains(pattern.toLowerCase())) {
                results.add(unit);
                if (maxItems > 0 && results.size() >= maxItems) {
                    return results;
                }
            }
        }
        return results;
    }

    @Override
    public Collection<OrganizationalUnit> searchById(Collection<String> ids) {
        List<OrganizationalUnit> results = new ArrayList<>();
        for (OrganizationalUnit unit : organizationalUnitService.getAllOrganizationalUnits()) {
            if (ids.contains(unit.getIdentifier())) {
                results.add(unit);
            }
        }
        return results;
    }
}
