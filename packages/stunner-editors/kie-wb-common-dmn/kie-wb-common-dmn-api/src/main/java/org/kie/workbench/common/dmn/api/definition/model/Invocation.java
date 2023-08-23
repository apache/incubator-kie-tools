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

import static org.kie.workbench.common.dmn.api.definition.model.common.DomainObjectSearcherHelper.find;
import static org.kie.workbench.common.dmn.api.definition.model.common.HasTypeRefHelper.getFlatHasTypeRefs;
import static org.kie.workbench.common.dmn.api.definition.model.common.HasTypeRefHelper.getNotNullHasTypeRefs;

@Portable
public class Invocation extends Expression implements HasExpression {

    private static final int STATIC_COLUMNS = 3;

    private Expression expression;
    private List<Binding> binding;

    public Invocation() {
        this(new Id(),
             new Description(),
             new QName(),
             null,
             null);
    }

    public Invocation(final Id id,
                      final Description description,
                      final QName typeRef,
                      final Expression expression,
                      final List<Binding> binding) {
        super(id,
              description,
              typeRef);
        this.expression = expression;
        this.binding = binding;
    }

    @Override
    public Invocation copy() {
        final Invocation clonedInvocation = new Invocation();
        clonedInvocation.description = Optional.ofNullable(description).map(Description::copy).orElse(null);
        clonedInvocation.typeRef = Optional.ofNullable(typeRef).map(QName::copy).orElse(null);
        clonedInvocation.componentWidths = new ArrayList<>(componentWidths);
        clonedInvocation.expression = Optional.ofNullable(expression).map(Expression::copy).orElse(null);
        clonedInvocation.binding = binding.stream().map(Binding::copy).collect(Collectors.toList());
        return clonedInvocation;
    }

    @Override
    public Invocation exactCopy() {
        final Invocation exactelyClonedInvocation = new Invocation();
        exactelyClonedInvocation.id = Optional.ofNullable(id).map(Id::copy).orElse(null);
        exactelyClonedInvocation.description = Optional.ofNullable(description).map(Description::copy).orElse(null);
        exactelyClonedInvocation.typeRef = Optional.ofNullable(typeRef).map(QName::copy).orElse(null);
        exactelyClonedInvocation.componentWidths = new ArrayList<>(componentWidths);
        exactelyClonedInvocation.expression = Optional.ofNullable(expression).map(Expression::exactCopy).orElse(null);
        exactelyClonedInvocation.binding = binding.stream().map(Binding::exactCopy).collect(Collectors.toList());
        return exactelyClonedInvocation;
    }

    @Override
    public Optional<DomainObject> findDomainObject(final String uuid) {

        Optional<DomainObject> domainObject = Optional.empty();
        if (!Objects.isNull(getExpression())) {
            domainObject = getExpression().findDomainObject(uuid);
        }

        if (domainObject.isPresent()) {
            return domainObject;
        }

        return find(getBinding(), uuid);
    }

    @Override
    public Expression getExpression() {
        return expression;
    }

    @Override
    public void setExpression(final Expression value) {
        this.expression = value;
    }

    @Override
    public DMNModelInstrumentedBase asDMNModelInstrumentedBase() {
        return this;
    }

    public List<Binding> getBinding() {
        if (binding == null) {
            binding = new ArrayList<>();
        }
        return this.binding;
    }

    @Override
    public List<HasTypeRef> getHasTypeRefs() {

        final List<HasTypeRef> hasTypeRefs = super.getHasTypeRefs();

        hasTypeRefs.addAll(getNotNullHasTypeRefs(getExpression()));
        hasTypeRefs.addAll(getFlatHasTypeRefs(getBinding()));

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
        if (!(o instanceof Invocation)) {
            return false;
        }

        final Invocation that = (Invocation) o;

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
        return binding != null ? binding.equals(that.binding) : that.binding == null;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(id != null ? id.hashCode() : 0,
                                         description != null ? description.hashCode() : 0,
                                         typeRef != null ? typeRef.hashCode() : 0,
                                         componentWidths != null ? componentWidths.hashCode() : 0,
                                         expression != null ? expression.hashCode() : 0,
                                         binding != null ? binding.hashCode() : 0);
    }
}
