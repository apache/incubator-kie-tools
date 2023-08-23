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

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
public class DMNDiagramElement {

    private Id id;

    private Name name;

    // -----------------------
    // DMN properties
    // -----------------------

    public DMNDiagramElement() {
        this(new Id(),
             new Name());
    }

    public DMNDiagramElement(final Id id,
                             final Name name) {
        this.id = id;
        this.name = name;
    }

    public Id getId() {
        return id;
    }

    public Name getName() {
        return name;
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof DMNDiagramElement)) {
            return false;
        }

        final DMNDiagramElement that = (DMNDiagramElement) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }

        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(id != null ? id.hashCode() : 0,
                                         name != null ? name.hashCode() : 0);
    }
}
