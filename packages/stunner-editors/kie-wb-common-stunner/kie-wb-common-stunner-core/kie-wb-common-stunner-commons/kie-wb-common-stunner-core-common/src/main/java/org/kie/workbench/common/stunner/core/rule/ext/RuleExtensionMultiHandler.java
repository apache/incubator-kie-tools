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


package org.kie.workbench.common.stunner.core.rule.ext;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.stunner.core.rule.RuleEvaluationContext;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.violations.DefaultRuleViolations;

/**
 * A rule extension handler that provides multi-context evaluation for a
 * single rule type.
 */
@Dependent
public class RuleExtensionMultiHandler
        extends RuleExtensionHandler<RuleExtensionHandler, RuleEvaluationContext> {

    private final List<RuleExtensionHandler> handlers = new LinkedList<>();

    public boolean addHandler(final RuleExtensionHandler handler) {
        return handlers.add(handler);
    }

    @Override
    public Class<RuleExtensionHandler> getExtensionType() {
        return RuleExtensionHandler.class;
    }

    @Override
    public Class<RuleEvaluationContext> getContextType() {
        return RuleEvaluationContext.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean accepts(final RuleExtension rule,
                           final RuleEvaluationContext context) {
        return handlers.stream().anyMatch(h -> isHandlerAccepted(h, rule, context));
    }

    @Override
    @SuppressWarnings("unchecked")
    public RuleViolations evaluate(final RuleExtension rule,
                                   final RuleEvaluationContext context) {
        final List<RuleExtensionHandler> candidates =
                handlers.stream()
                        .filter(h -> isHandlerAccepted(h,
                                                       rule,
                                                       context))
                        .collect(Collectors.toList());
        final DefaultRuleViolations result = new DefaultRuleViolations();
        candidates.forEach(candidate ->
                                   result.addViolations(candidate.evaluate(rule,
                                                                           context)));
        return result;
    }

    @SuppressWarnings("unchecked")
    private boolean isHandlerAccepted(final RuleExtensionHandler handler,
                                      final RuleExtension rule,
                                      final RuleEvaluationContext context) {
        return handler.getContextType().equals(context.getType())
                && handler.accepts(rule, context);
    }
}
