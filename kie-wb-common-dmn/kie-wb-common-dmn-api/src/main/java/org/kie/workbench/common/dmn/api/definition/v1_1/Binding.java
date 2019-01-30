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
import org.kie.workbench.common.dmn.api.definition.HasTypeRefs;
import org.kie.workbench.common.dmn.api.definition.HasVariable;
import org.kie.workbench.common.stunner.core.util.HashUtil;

import static org.kie.workbench.common.dmn.api.definition.v1_1.common.HasTypeRefHelper.getNotNullHasTypeRefs;

@Portable
public class Binding extends DMNModelInstrumentedBase implements HasExpression,
                                                                 HasTypeRefs,
                                                                 HasVariable<InformationItem> {

    private InformationItem parameter;
    private Expression expression;

    public InformationItem getParameter() {
        return parameter;
    }

    public void setParameter(final InformationItem value) {
        this.parameter = value;
    }

    @Override
    //Proxy for getParameter() to allow use as HasVariable
    public InformationItem getVariable() {
        return getParameter();
    }

    @Override
    //Proxy for setParameter(..) to allow use as HasVariable
    public void setVariable(final InformationItem informationItem) {
        setParameter(informationItem);
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
        if (!(o instanceof Binding)) {
            return false;
        }

        final Binding binding = (Binding) o;

        if (parameter != null ? !parameter.equals(binding.parameter) : binding.parameter != null) {
            return false;
        }
        return expression != null ? expression.equals(binding.expression) : binding.expression == null;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(parameter != null ? parameter.hashCode() : 0,
                                         expression != null ? expression.hashCode() : 0);
    }

    @Override
    public List<HasTypeRef> getHasTypeRefs() {

        final List<HasTypeRef> hasTypeRefs = new ArrayList<>();

        hasTypeRefs.addAll(getNotNullHasTypeRefs(getExpression()));
        hasTypeRefs.addAll(getNotNullHasTypeRefs(getParameter()));

        return hasTypeRefs;
    }
}
