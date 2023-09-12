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

package org.kie.workbench.common.dmn.api.rules;

import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import org.kie.workbench.common.forms.adf.definitions.DynamicReadOnly;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.context.GraphConnectionContext;
import org.kie.workbench.common.stunner.core.rule.ext.RuleExtension;
import org.kie.workbench.common.stunner.core.rule.ext.RuleExtensionHandler;
import org.kie.workbench.common.stunner.core.rule.violations.DefaultRuleViolations;
import org.kie.workbench.common.stunner.core.rule.violations.RuleViolationImpl;

@ApplicationScoped
public class NoInputNodesInImportedDecisionRule extends RuleExtensionHandler<NoInputNodesInImportedDecisionRule, GraphConnectionContext> {

    static final String ERROR_MESSAGE = "Imported nodes can not have input nodes.";

    @Override
    public Class<NoInputNodesInImportedDecisionRule> getExtensionType() {
        return NoInputNodesInImportedDecisionRule.class;
    }

    @Override
    public Class<GraphConnectionContext> getContextType() {
        return GraphConnectionContext.class;
    }

    @Override
    public boolean accepts(final RuleExtension rule,
                           final GraphConnectionContext context) {
        if (context.getTarget().isPresent()) {
            final Node<? extends View<?>, ? extends Edge> target = context.getTarget().get();
            final View<?> content = target.getContent();
            if (Objects.isNull(content)) {
                return false;
            }
            final Object definition = content.getDefinition();
            return Objects.equals(definition.getClass().getName(), rule.getId());
        }
        return false;
    }

    @Override
    public RuleViolations evaluate(final RuleExtension rule,
                                   final GraphConnectionContext context) {
        final Optional<Node<? extends View<?>, ? extends Edge>> oSource = context.getSource();
        final Optional<Node<? extends View<?>, ? extends Edge>> oTarget = context.getTarget();
        final DefaultRuleViolations result = new DefaultRuleViolations();

        if (!(oSource.isPresent() && oTarget.isPresent())) {
            return result;
        }

        if (isReadOnly(oTarget)) {
            result.addViolation(new RuleViolationImpl(ERROR_MESSAGE));
        }

        return result;
    }

    boolean isReadOnly(final Optional<Node<? extends View<?>, ? extends Edge>> oTarget) {
        final Node<? extends View<?>, ? extends Edge> target = oTarget.get();
        final View<?> content = target.getContent();
        if (Objects.isNull(content)) {
            return false;
        }
        final Object definition = content.getDefinition();
        if (definition instanceof DynamicReadOnly) {
            if (((DynamicReadOnly) definition).isAllowOnlyVisualChange()) {
                return true;
            }
        }

        return false;
    }
}
