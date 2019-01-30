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

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.stunner.core.util.HashUtil;

import static org.kie.workbench.common.dmn.api.definition.v1_1.common.HasTypeRefHelper.getFlatHasTypeRefs;
import static org.kie.workbench.common.dmn.api.definition.v1_1.common.HasTypeRefHelper.getNotNullHasTypeRefs;

@Portable
public class FunctionDefinition extends Expression implements HasExpression {

    private Expression expression;

    private Kind kind = Kind.FEEL; // same default as per DMN spec.

    private List<InformationItem> formalParameter;

    public FunctionDefinition() {
        this(new Id(),
             new Description(),
             new QName(),
             null);
    }

    public FunctionDefinition(final Id id,
                              final Description description,
                              final QName typeRef,
                              final Expression expression) {
        super(id,
              description,
              typeRef);
        this.expression = expression;
    }

    // -----------------------
    // DMN properties
    // -----------------------

    @Override
    public Expression getExpression() {
        return expression;
    }

    @Override
    public void setExpression(final Expression expression) {
        this.expression = expression;
    }

    @Override
    public DMNModelInstrumentedBase asDMNModelInstrumentedBase() {
        return this;
    }

    public List<InformationItem> getFormalParameter() {
        if (formalParameter == null) {
            formalParameter = new ArrayList<>();
        }
        return this.formalParameter;
    }

    public Kind getKind() {
        return kind;
    }

    public void setKind(Kind kind) {
        this.kind = kind;
    }

    @Override
    public List<HasTypeRef> getHasTypeRefs() {

        final List<HasTypeRef> hasTypeRefs = super.getHasTypeRefs();

        hasTypeRefs.addAll(getNotNullHasTypeRefs(getExpression()));
        hasTypeRefs.addAll(getFlatHasTypeRefs(getFormalParameter()));

        return hasTypeRefs;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FunctionDefinition)) {
            return false;
        }

        final FunctionDefinition that = (FunctionDefinition) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (description != null ? !description.equals(that.description) : that.description != null) {
            return false;
        }
        if (typeRef != null ? !typeRef.equals(that.typeRef) : that.typeRef != null) {
            return false;
        }
        if (expression != null ? !expression.equals(that.expression) : that.expression != null) {
            return false;
        }
        return formalParameter != null ? formalParameter.equals(that.formalParameter) : that.formalParameter == null;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(id != null ? id.hashCode() : 0,
                                         description != null ? description.hashCode() : 0,
                                         typeRef != null ? typeRef.hashCode() : 0,
                                         expression != null ? expression.hashCode() : 0,
                                         formalParameter != null ? formalParameter.hashCode() : 0);
    }

    @Portable
    public enum Kind {
        FEEL("F"),
        JAVA("J"),
        PMML("P");

        private final String code;

        Kind(final String code) {
            this.code = code;
        }

        public String code() {
            return code;
        }

        public static FunctionDefinition.Kind determineFromString(final String code) {
            return code == null ? null : (FEEL.code.equals(code) ? FEEL : (JAVA.code.equals(code) ? JAVA : (PMML.code.equals(code) ? PMML : null)));
        }
    }
}
