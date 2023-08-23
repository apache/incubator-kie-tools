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
package org.kie.workbench.common.dmn.api.definition;

import java.util.Objects;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.model.Expression;

public interface HasExpression {

    Expression getExpression();

    void setExpression(final Expression expression);

    DMNModelInstrumentedBase asDMNModelInstrumentedBase();

    /**
     * Can the Expression be cleared on the HasExpression instance.
     * @return true if the Expression can be cleared.
     */
    default boolean isClearSupported() {
        return true;
    }

    @Portable
    class WrappedHasExpression implements HasExpression {

        private DMNModelInstrumentedBase parent;
        private Expression expression;

        private WrappedHasExpression(@MapsTo("parent") final DMNModelInstrumentedBase parent,
                                     @MapsTo("expression") final Expression expression) {
            this.parent = Objects.requireNonNull(parent);
            this.expression = expression;
        }

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
            return parent;
        }
    }

    static HasExpression wrap(final DMNModelInstrumentedBase parent,
                              final Expression expression) {
        return new WrappedHasExpression(parent, expression);
    }

    HasExpression NOP = new HasExpression() {
        @Override
        public Expression getExpression() {
            return null;
        }

        @Override
        public void setExpression(final Expression expression) {

        }

        @Override
        public DMNModelInstrumentedBase asDMNModelInstrumentedBase() {
            return null;
        }
    };
}
