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
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
public class Binding extends DMNModelInstrumentedBase implements HasExpression {

    private InformationItem parameter;
    private Expression expression;

    public InformationItem getParameter() {
        return parameter;
    }

    public void setParameter(final InformationItem value) {
        this.parameter = value;
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
}
