/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.structure.events;

import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.kie.soup.commons.validation.PortablePreconditions;

/**
 * An event signalling an Organizational Unit has been created
 */
public class AfterCreateOrganizationalUnitEvent {

    private final OrganizationalUnit organizationalUnit;

    public AfterCreateOrganizationalUnitEvent(final OrganizationalUnit organizationalUnit) {
        this.organizationalUnit = PortablePreconditions.checkNotNull("organizationalUnit",
                                                                     organizationalUnit);
    }

    public OrganizationalUnit getOrganizationalUnit() {
        return organizationalUnit;
    }
}
