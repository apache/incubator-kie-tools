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
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;
import org.kie.workbench.common.stunner.core.util.HashUtil;

import static java.util.Objects.isNull;
import static org.kie.workbench.common.dmn.api.definition.model.common.HasTypeRefHelper.getFlatHasTypeRefsFromExpressions;

@Portable
public class List extends Expression {

    private static final int STATIC_COLUMNS = 2;

    private java.util.List<HasExpression> expression;

    public List() {
        this(new Id(),
             new Description(),
             new QName(),
             null);
    }

    public List(final Id id,
                final Description description,
                final QName typeRef,
                final java.util.List<HasExpression> expression) {
        super(id,
              description,
              typeRef);
        this.expression = expression;
    }

    @Override
    public List copy() {
        final List clonedList = new List();
        clonedList.description = Optional.ofNullable(description).map(Description::copy).orElse(null);
        clonedList.typeRef = Optional.ofNullable(typeRef).map(QName::copy).orElse(null);
        clonedList.componentWidths = new ArrayList<>(componentWidths);
        clonedList.expression = expression.stream().map(expressionWrapperMappingFn(clonedList)).collect(Collectors.toList());
        return clonedList;
    }

    @Override
    public List exactCopy() {
        final List exactelyClonedList = new List();
        exactelyClonedList.id = Optional.ofNullable(id).map(Id::copy).orElse(null);
        exactelyClonedList.description = Optional.ofNullable(description).map(Description::copy).orElse(null);
        exactelyClonedList.typeRef = Optional.ofNullable(typeRef).map(QName::copy).orElse(null);
        exactelyClonedList.componentWidths = new ArrayList<>(componentWidths);
        exactelyClonedList.expression = expression.stream().map(hasExpression ->
                HasExpression.wrap(
                        exactelyClonedList,
                        Optional.ofNullable(hasExpression.getExpression()).map(Expression::exactCopy).orElse(null)
                )).collect(Collectors.toList());
        return exactelyClonedList;
    }

    @Override
    public Optional<DomainObject> findDomainObject(final String uuid) {
        return getExpression().stream()
                .filter(hasExpression -> !isNull(hasExpression.getExpression()))
                .map(hasExpression -> hasExpression.getExpression().findDomainObject(uuid))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }

    private Function<HasExpression, HasExpression> expressionWrapperMappingFn(final List clonedList) {
        return exp ->
                HasExpression.wrap(
                        clonedList,
                        Optional.ofNullable(exp.getExpression()).map(Expression::copy).orElse(null)
                );
    }

    public java.util.List<HasExpression> getExpression() {
        if (expression == null) {
            expression = new ArrayList<>();
        }
        return this.expression;
    }

    @Override
    public java.util.List<HasTypeRef> getHasTypeRefs() {
        final java.util.List<HasTypeRef> hasTypeRefs = super.getHasTypeRefs();
        hasTypeRefs.addAll(getFlatHasTypeRefsFromExpressions(getExpression()));
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
        if (!(o instanceof List)) {
            return false;
        }

        final List that = (List) o;

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
        return expression != null ? expression.equals(that.expression) : that.expression == null;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(id != null ? id.hashCode() : 0,
                                         description != null ? description.hashCode() : 0,
                                         typeRef != null ? typeRef.hashCode() : 0,
                                         componentWidths != null ? componentWidths.hashCode() : 0,
                                         expression != null ? expression.hashCode() : 0);
    }
}

