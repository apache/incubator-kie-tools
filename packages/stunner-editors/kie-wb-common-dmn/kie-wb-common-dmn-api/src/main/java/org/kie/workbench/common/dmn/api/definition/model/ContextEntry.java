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

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.definition.HasTypeRefs;
import org.kie.workbench.common.dmn.api.definition.HasVariable;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;
import org.kie.workbench.common.stunner.core.util.HashUtil;

import static org.kie.workbench.common.dmn.api.definition.model.common.DomainObjectSearcherHelper.matches;
import static org.kie.workbench.common.dmn.api.definition.model.common.HasTypeRefHelper.getNotNullHasTypeRefs;

@Portable
public class ContextEntry extends DMNModelInstrumentedBase implements HasExpression,
                                                                      HasTypeRefs,
                                                                      HasVariable<InformationItem>,
                                                                      HasDomainObject {

    private InformationItem variable;
    private Expression expression;

    public ContextEntry copy() {
        final ContextEntry clonedContextEntry = new ContextEntry();
        clonedContextEntry.variable = Optional.ofNullable(variable).map(InformationItem::copy).orElse(null);
        clonedContextEntry.expression = Optional.ofNullable(expression).map(Expression::copy).orElse(null);
        return clonedContextEntry;
    }

    public ContextEntry exactCopy() {
        final ContextEntry exactelyClonedContextEntry = new ContextEntry();
        exactelyClonedContextEntry.variable = Optional.ofNullable(variable).map(InformationItem::exactCopy).orElse(null);
        exactelyClonedContextEntry.expression = Optional.ofNullable(expression).map(Expression::exactCopy).orElse(null);
        return exactelyClonedContextEntry;
    }

    @Override
    public InformationItem getVariable() {
        return variable;
    }

    @Override
    public void setVariable(final InformationItem value) {
        this.variable = value;
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ContextEntry)) {
            return false;
        }

        final ContextEntry that = (ContextEntry) o;

        if (variable != null ? !variable.equals(that.variable) : that.variable != null) {
            return false;
        }
        return expression != null ? expression.equals(that.expression) : that.expression == null;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(variable != null ? variable.hashCode() : 0,
                                         expression != null ? expression.hashCode() : 0);
    }

    @Override
    public List<HasTypeRef> getHasTypeRefs() {

        final List<HasTypeRef> hasTypeRefs = new ArrayList<>();

        hasTypeRefs.addAll(getNotNullHasTypeRefs(getExpression()));
        hasTypeRefs.addAll(getNotNullHasTypeRefs(getVariable()));

        return hasTypeRefs;
    }

    @Override
    public Optional<DomainObject> findDomainObject(final String uuid) {

        if (matches(variable, uuid)) {
            return Optional.of(variable);
        }

        if (!Objects.isNull(expression)) {
            return expression.findDomainObject(uuid);
        }

        return Optional.empty();
    }
}
