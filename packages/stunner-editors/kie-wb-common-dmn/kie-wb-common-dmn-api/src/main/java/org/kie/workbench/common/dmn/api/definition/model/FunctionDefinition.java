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
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;
import org.kie.workbench.common.stunner.core.util.HashUtil;

import static org.kie.workbench.common.dmn.api.definition.model.common.DomainObjectSearcherHelper.getDomainObject;
import static org.kie.workbench.common.dmn.api.definition.model.common.HasTypeRefHelper.getFlatHasTypeRefs;
import static org.kie.workbench.common.dmn.api.definition.model.common.HasTypeRefHelper.getNotNullHasTypeRefs;

@Portable
public class FunctionDefinition extends Expression implements HasExpression {

    private static final int STATIC_COLUMNS = 2;

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

    @Override
    public FunctionDefinition copy() {
        final FunctionDefinition clonedFunctionDefinition = new FunctionDefinition();
        clonedFunctionDefinition.description = Optional.ofNullable(description).map(Description::copy).orElse(null);
        clonedFunctionDefinition.typeRef = Optional.ofNullable(typeRef).map(QName::copy).orElse(null);
        clonedFunctionDefinition.componentWidths = new ArrayList<>(componentWidths);
        clonedFunctionDefinition.expression = Optional.ofNullable(expression).map(Expression::copy).orElse(null);
        clonedFunctionDefinition.formalParameter = cloneFormalParameterList();
        clonedFunctionDefinition.kind = kind;
        clonedFunctionDefinition.getAdditionalAttributes().putAll(cloneAdditionalAttributes());
        return clonedFunctionDefinition;
    }

    @Override
    public FunctionDefinition exactCopy() {
        final FunctionDefinition exactelyClonedFunctionDefinition = new FunctionDefinition();
        exactelyClonedFunctionDefinition.id = Optional.ofNullable(id).map(Id::copy).orElse(null);
        exactelyClonedFunctionDefinition.description = Optional.ofNullable(description).map(Description::copy).orElse(null);
        exactelyClonedFunctionDefinition.typeRef = Optional.ofNullable(typeRef).map(QName::copy).orElse(null);
        exactelyClonedFunctionDefinition.componentWidths = new ArrayList<>(componentWidths);
        exactelyClonedFunctionDefinition.expression = Optional.ofNullable(expression).map(Expression::exactCopy).orElse(null);
        exactelyClonedFunctionDefinition.formalParameter = getFormalParameter()
                .stream()
                .map(InformationItem::exactCopy)
                .collect(Collectors.toList());
        exactelyClonedFunctionDefinition.kind = kind;
        exactelyClonedFunctionDefinition.getAdditionalAttributes().putAll(cloneAdditionalAttributes());
        return exactelyClonedFunctionDefinition;
    }

    @Override
    public Optional<DomainObject> findDomainObject(final String uuid) {

        final Optional<DomainObject> domainObject = getDomainObject(getFormalParameter(), uuid);
        if (domainObject.isPresent()) {
            return domainObject;
        }

        if (!Objects.isNull(getExpression())) {
            return getExpression().findDomainObject(uuid);
        }

        return Optional.empty();
    }

    private List<InformationItem> cloneFormalParameterList() {
        return getFormalParameter()
                .stream()
                .map(InformationItem::copy)
                .collect(Collectors.toList());
    }

    private Map<QName, String> cloneAdditionalAttributes() {
        return getAdditionalAttributes().keySet()
                .stream()
                .map(QName::copy)
                .collect(Collectors.toMap(
                        typeRef -> typeRef,
                        typeRef -> getAdditionalAttributes().get(typeRef)
                ));
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
    public int getRequiredComponentWidthCount() {
        return STATIC_COLUMNS;
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
        if (componentWidths != null ? !componentWidths.equals(that.componentWidths) : that.componentWidths != null) {
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
                                         componentWidths != null ? componentWidths.hashCode() : 0,
                                         expression != null ? expression.hashCode() : 0,
                                         formalParameter != null ? formalParameter.hashCode() : 0);
    }

    @Portable
    public enum Kind {
        FEEL("F", "FEEL"),
        JAVA("J", "Java"),
        PMML("P", "PMML");

        private final String code;
        private final String value;

        Kind(final String code,
             final String value) {
            this.code = code;
            this.value = value;
        }

        public String code() {
            return code;
        }

        public String getValue() { return value; }

        public static Kind fromValue(final String value) {
            for (final Kind kind : Kind.values()) {
                if (Objects.equals(kind.value, value)) {
                    return kind;
                }
            }
            return FEEL;
        }

        public static FunctionDefinition.Kind determineFromString(final String code) {
            return code == null ? null : Objects.equals(FEEL.code, code) ? FEEL : (Objects.equals(JAVA.code, code) ? JAVA : (Objects.equals(PMML.code, code) ? PMML : null));
        }
    }
}
