/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.dmn.api.definition.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.kie.workbench.common.dmn.api.definition.HasComponentWidths;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.QName;

import static java.util.Collections.singletonList;

public abstract class Expression extends DMNElement implements HasTypeRef,
                                                               HasComponentWidths {

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
     */
    public abstract Expression copy();

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
