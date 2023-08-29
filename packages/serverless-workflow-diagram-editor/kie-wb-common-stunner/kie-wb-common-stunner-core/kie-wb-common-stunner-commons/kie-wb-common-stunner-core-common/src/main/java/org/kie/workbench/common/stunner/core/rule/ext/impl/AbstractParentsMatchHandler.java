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


package org.kie.workbench.common.stunner.core.rule.ext.impl;

import java.util.Optional;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationContext;
import org.kie.workbench.common.stunner.core.rule.ext.RuleExtension;
import org.kie.workbench.common.stunner.core.rule.ext.RuleExtensionHandler;
import org.kie.workbench.common.stunner.core.rule.violations.DefaultRuleViolations;
import org.kie.workbench.common.stunner.core.rule.violations.RuleViolationImpl;

public abstract class AbstractParentsMatchHandler<T extends AbstractParentsMatchHandler, C extends RuleEvaluationContext>
        extends RuleExtensionHandler<T, C> {

    protected String getViolationMessage(final RuleExtension rule) {
        final String[] arguments = rule.getArguments();
        if (null != arguments && arguments.length > 0) {
            return arguments[0];
        }
        return "Violation produced by {" + getClass().getName() + "]";
    }

    protected void addViolation(final String uuid,
                                final RuleExtension rule,
                                final DefaultRuleViolations result) {
        final RuleViolationImpl violation = new RuleViolationImpl(getViolationMessage(rule));
        violation.setUUID(uuid);
        result.addViolation(violation);
    }

    protected static Optional<String> getId(final DefinitionManager definitionManager,
                                            final Edge edge) {
        final Object content = edge.getContent();
        if (content instanceof Definition) {
            final Definition holder = (Definition) content;
            return Optional.of(definitionManager.adapters().forDefinition().getId(holder.getDefinition()).value());
        }
        return Optional.empty();
    }
}
