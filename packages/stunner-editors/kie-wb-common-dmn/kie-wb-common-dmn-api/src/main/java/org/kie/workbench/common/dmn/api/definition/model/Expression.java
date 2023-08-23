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
import java.util.Optional;
import java.util.stream.IntStream;

import org.kie.workbench.common.dmn.api.definition.HasComponentWidths;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;

import static java.util.Collections.singletonList;

public abstract class Expression extends DMNElement implements HasTypeRef,
                                                               HasComponentWidths,
                                                               HasDomainObject {

    protected QName typeRef;

    protected java.util.List<Double> componentWidths = new ArrayList<>();

    public Expression() {
    }

    public Expression(final Id id,
                      final Description description,
                      final QName typeRef) {
        super(id,
              description);
        this.typeRef = typeRef;
    }

    /**
     * It represents a contract for all subclasses of {@link Expression}.
     * Its purpose is to exploit polymorphism when we deeply copy the Expression boxed inside the {@link Decision}
     * It copies the Expression properties only, and NOT the IDs.
     */
    public abstract Expression copy();

    /**
     * It represents a contract for all subclasses of {@link Expression}.
     * Its purpose is to exploit polymorphism when we deeply copy the Expression boxed inside the {@link Decision}
     * It performs a 1:1 Expression copy, with IDs as well
     */
    public abstract Expression exactCopy();

    /**
     * Find a {@link DomainObject} in the expression with the given the UUID.
     * @param uuid The UUID of the {@link DomainObject}.
     * @return The found domain object or empty if the object was not found.
     */
    public abstract Optional<DomainObject> findDomainObject(final String uuid);

    // -----------------------
    // DMN properties
    // -----------------------

    @Override
    public QName getTypeRef() {
        return typeRef;
    }

    @Override
    public void setTypeRef(final QName typeRef) {
        this.typeRef = typeRef;
    }

    @Override
    public java.util.List<Double> getComponentWidths() {
        final int requiredComponentWidthCount = getRequiredComponentWidthCount();
        if (componentWidths.size() != requiredComponentWidthCount) {
            componentWidths = new ArrayList<>(requiredComponentWidthCount);
            IntStream.range(0, requiredComponentWidthCount).forEach(i -> componentWidths.add(null));
        }
        return componentWidths;
    }

    @Override
    public DMNModelInstrumentedBase asDMNModelInstrumentedBase() {
        return this;
    }

    @Override
    public List<HasTypeRef> getHasTypeRefs() {
        return new ArrayList<>(singletonList(this));
    }
}
