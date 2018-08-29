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
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
public class InputClause extends DMNElement {

    private LiteralExpression inputExpression;
    private UnaryTests inputValues;

    public InputClause() {
        this(new Id(),
             new Description(),
             new LiteralExpression(),
             new UnaryTests());
    }

    public InputClause(final Id id,
                       final Description description,
                       final LiteralExpression inputExpression,
                       final UnaryTests inputValues) {
        super(id,
              description);
        this.inputExpression = inputExpression;
        this.inputValues = inputValues;
    }

    public LiteralExpression getInputExpression() {
        return inputExpression;
    }

    public void setInputExpression(final LiteralExpression value) {
        this.inputExpression = value;
    }

    public UnaryTests getInputValues() {
        return inputValues;
    }

    public void setInputValues(final UnaryTests value) {
        this.inputValues = value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InputClause)) {
            return false;
        }

        final InputClause that = (InputClause) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (description != null ? !description.equals(that.description) : that.description != null) {
            return false;
        }
        if (inputExpression != null ? !inputExpression.equals(that.inputExpression) : that.inputExpression != null) {
            return false;
        }
        return inputValues != null ? inputValues.equals(that.inputValues) : that.inputValues == null;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(id != null ? id.hashCode() : 0,
                                         description != null ? description.hashCode() : 0,
                                         inputExpression != null ? inputExpression.hashCode() : 0,
                                         inputValues != null ? inputValues.hashCode() : 0);
    }
}
