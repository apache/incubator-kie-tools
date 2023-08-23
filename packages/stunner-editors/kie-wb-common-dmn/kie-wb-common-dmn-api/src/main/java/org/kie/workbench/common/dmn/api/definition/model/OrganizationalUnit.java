/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */
package org.kie.workbench.common.dmn.api.definition.model;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
public class OrganizationalUnit extends BusinessContextElement {

    private List<DMNElementReference> decisionMade;
    private List<DMNElementReference> decisionOwned;

    public OrganizationalUnit() {
        this(new Id(),
             new Description(),
             new Name(),
             null,
             null,
             null);
    }

    public OrganizationalUnit(final Id id,
                              final Description description,
                              final Name name,
                              final String uri,
                              final List<DMNElementReference> decisionMade,
                              final List<DMNElementReference> decisionOwned) {
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrganizationalUnit)) {
            return false;
        }

        final OrganizationalUnit that = (OrganizationalUnit) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (description != null ? !description.equals(that.description) : that.description != null) {
            return false;
        }
        if (nameHolder != null ? !nameHolder.equals(that.nameHolder) : that.nameHolder != null) {
            return false;
        }
        if (uri != null ? !uri.equals(that.uri) : that.uri != null) {
            return false;
        }
        if (decisionMade != null ? !decisionMade.equals(that.decisionMade) : that.decisionMade != null) {
            return false;
        }
        return decisionOwned != null ? decisionOwned.equals(that.decisionOwned) : that.decisionOwned == null;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(id != null ? id.hashCode() : 0,
                                         description != null ? description.hashCode() : 0,
                                         nameHolder != null ? nameHolder.hashCode() : 0,
                                         uri != null ? uri.hashCode() : 0,
                                         decisionMade != null ? decisionMade.hashCode() : 0,
                                         decisionOwned != null ? decisionOwned.hashCode() : 0);
    }
}
