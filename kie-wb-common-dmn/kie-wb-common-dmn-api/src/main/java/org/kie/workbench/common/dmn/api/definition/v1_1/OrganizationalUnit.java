/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.dmn.api.definition.v1_1;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;

@Portable
public class OrganizationalUnit extends BusinessContextElement {

    private List<DMNElementReference> decisionMade;
    private List<DMNElementReference> decisionOwned;

    public OrganizationalUnit() {
        this(new Id(),
             new Description(),
             new Name(),
             "",
             new ArrayList<>(),
             new ArrayList<>());
    }

    public OrganizationalUnit(final @MapsTo("id") Id id,
                              final @MapsTo("description") Description description,
                              final @MapsTo("name") Name name,
                              final @MapsTo("uri") String uri,
                              final @MapsTo("decisionMade") List<DMNElementReference> decisionMade,
                              final @MapsTo("decisionOwned") List<DMNElementReference> decisionOwned) {
        super(id,
              description,
              name,
              uri);
        this.decisionMade = decisionMade;
        this.decisionOwned = decisionOwned;
    }

    public List<DMNElementReference> getDecisionMade() {
        if (decisionMade == null) {
            decisionMade = new ArrayList<>();
        }
        return this.decisionMade;
    }

    public List<DMNElementReference> getDecisionOwned() {
        if (decisionOwned == null) {
            decisionOwned = new ArrayList<>();
        }
        return this.decisionOwned;
    }
}
