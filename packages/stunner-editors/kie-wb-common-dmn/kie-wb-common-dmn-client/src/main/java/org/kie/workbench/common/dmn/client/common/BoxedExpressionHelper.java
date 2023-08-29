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

package org.kie.workbench.common.dmn.client.common;

import java.util.Optional;

import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

public class BoxedExpressionHelper {

    public Optional<HasExpression> getOptionalHasExpression(final Node<View, Edge> node) {

        final Object definition = DefinitionUtils.getElementDefinition(node);
        final HasExpression expression;

        if (definition instanceof BusinessKnowledgeModel) {
            expression = ((BusinessKnowledgeModel) definition).asHasExpression();
        } else if (definition instanceof Decision) {
            expression = (Decision) definition;
        } else {
            expression = null;
        }

        return Optional.ofNullable(expression);
    }

    public Optional<Expression> getOptionalExpression(final Node<View, Edge> node) {
        return Optional.ofNullable(getExpression(node));
    }

    public Expression getExpression(final Node<View, Edge> node) {
        return getHasExpression(node).getExpression();
    }

    public HasExpression getHasExpression(final Node<View, Edge> node) {
        return getOptionalHasExpression(node).orElseThrow(UnsupportedOperationException::new);
    }
}
