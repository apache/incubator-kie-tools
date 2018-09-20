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
package org.kie.workbench.common.dmn.api.definition.v1_1;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
public class OutputClause extends DMNElement implements HasTypeRef {

    private UnaryTests outputValues;
    private LiteralExpression defaultOutputEntry;
    private String name;
    private QName typeRef;

    public OutputClause() {
        this(new Id(),
             new Description(),
             null,
             null,
             null,
             new QName());
    }

    public OutputClause(final Id id,
                        final Description description,
                        final UnaryTests outputValues,
                        final LiteralExpression defaultOutputEntry,
                        final String name,
                        final QName typeRef) {
        super(id,
              description);
        this.outputValues = outputValues;
        this.defaultOutputEntry = defaultOutputEntry;
        this.name = name;
        this.typeRef = typeRef;
    }

    public UnaryTests getOutputValues() {
        return outputValues;
    }

    public void setOutputValues(final UnaryTests value) {
        this.outputValues = value;
    }

    public LiteralExpression getDefaultOutputEntry() {
        return defaultOutputEntry;
    }

    public void setDefaultOutputEntry(final LiteralExpression value) {
        this.defaultOutputEntry = value;
    }

    public String getName() {
        return name;
    }

    public void setName(final String value) {
        this.name = value;
    }

    @Override
    public QName getTypeRef() {
        return typeRef;
    }

    @Override
    public void setTypeRef(final QName value) {
        this.typeRef = value;
    }

    @Override
    public DMNModelInstrumentedBase asDMNModelInstrumentedBase() {
        return this;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OutputClause)) {
            return false;
        }

        final OutputClause that = (OutputClause) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (description != null ? !description.equals(that.description) : that.description != null) {
            return false;
        }
        if (outputValues != null ? !outputValues.equals(that.outputValues) : that.outputValues != null) {
            return false;
        }
        if (defaultOutputEntry != null ? !defaultOutputEntry.equals(that.defaultOutputEntry) : that.defaultOutputEntry != null) {
            return false;
        }
        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        return typeRef != null ? typeRef.equals(that.typeRef) : that.typeRef == null;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(id != null ? id.hashCode() : 0,
                                         description != null ? description.hashCode() : 0,
                                         outputValues != null ? outputValues.hashCode() : 0,
                                         defaultOutputEntry != null ? defaultOutputEntry.hashCode() : 0,
                                         name != null ? name.hashCode() : 0,
                                         typeRef != null ? typeRef.hashCode() : 0);
    }
}
