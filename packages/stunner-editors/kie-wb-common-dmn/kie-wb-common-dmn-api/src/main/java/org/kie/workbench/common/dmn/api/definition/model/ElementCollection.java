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
public class ElementCollection extends NamedElement {

    private List<DMNElementReference> drgElement;

    public ElementCollection() {
        this(new Id(),
             new Description(),
             new Name(),
             null);
    }

    public ElementCollection(final Id id,
                             final Description description,
                             final Name name,
                             final List<DMNElementReference> drgElement) {
        super(id,
              description,
              name);
        this.drgElement = drgElement;
    }

    public List<DMNElementReference> getDrgElement() {
        if (drgElement == null) {
            drgElement = new ArrayList<>();
        }
        return this.drgElement;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ElementCollection)) {
            return false;
        }

        final ElementCollection that = (ElementCollection) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (description != null ? !description.equals(that.description) : that.description != null) {
            return false;
        }
        if (nameHolder != null ? !nameHolder.equals(that.nameHolder) : that.nameHolder != null) {
            return false;
        }
        return drgElement != null ? drgElement.equals(that.drgElement) : that.drgElement == null;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(id != null ? id.hashCode() : 0,
                                         description != null ? description.hashCode() : 0,
                                         nameHolder != null ? nameHolder.hashCode() : 0,
                                         drgElement != null ? drgElement.hashCode() : 0);
    }
}
