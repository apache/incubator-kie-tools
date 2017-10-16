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

package org.kie.workbench.common.stunner.core.rule;

import java.util.Collection;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.registry.RegistryFactory;
import org.kie.workbench.common.stunner.core.registry.rule.RuleHandlerRegistry;
import org.kie.workbench.common.stunner.core.rule.ext.RuleExtension;
import org.kie.workbench.common.stunner.core.rule.ext.RuleExtensionHandler;
import org.kie.workbench.common.stunner.core.rule.violations.ContextOperationNotAllowedViolation;
import org.kie.workbench.common.stunner.core.rule.violations.DefaultRuleViolations;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@ApplicationScoped
public class RuleManagerImpl implements RuleManager {

    private static Logger LOGGER = Logger.getLogger(RuleManagerImpl.class.getName());

    private final RuleHandlerRegistry registry;

    protected RuleManagerImpl() {
        this(null);
    }

    @Inject
    public RuleManagerImpl(final RegistryFactory registryFactory) {
        this.registry = null != registryFactory ? registryFactory.newRuleHandlerRegistry() : null;
    }

    @Override
    public RuleViolations evaluate(final RuleSet ruleSet,
                                   final RuleEvaluationContext context) {
        checkNotNull("ruleSet",
                     ruleSet);
        checkNotNull("context",
                     context);
        /*
            Consider:
            - If no rules present on the rule set, no resulting rule violation instances
            are expected
            - If rules present but no rule accepts the runtime context inputs, the context type
            defines if allow/or deny the evaluation
            - Otherwise return the rule violations produced by the handlers or extensions
         */
        final DefaultRuleViolations results = new DefaultRuleViolations();
        final boolean hasRules = ruleSet.getRules().iterator().hasNext();
        if (hasRules) {
            final boolean[] hasEvaluations = {false};
            ruleSet.getRules().forEach(rule -> {
                final Optional<RuleViolations> violations = evaluate(rule,
                                                                     context);
                if (violations.isPresent()) {
                    hasEvaluations[0] = true;
                    LOGGER.info("Rule Evaluation [" + rule + ", " + violations + "]");
                    results.addViolations(violations.get());
                }
            });
            if (!hasEvaluations[0] && context.isDefaultDeny()) {
                return getDefaultViolationForContext(context);
            }
        }
        return results;
    }

    private RuleViolations getDefaultViolationForContext(final RuleEvaluationContext context) {
        return new DefaultRuleViolations().addViolation(
                new ContextOperationNotAllowedViolation(context)
        );
    }

    @Override
    public RuleHandlerRegistry registry() {
        return registry;
    }

    private Optional<RuleViolations> evaluate(final Rule rule,
                                              final RuleEvaluationContext context) {
        if (rule instanceof RuleExtension) {
            return evaluateExtension((RuleExtension) rule,
                                     context);
        }
        return evaluateRule(rule,
                            context);
    }

    @SuppressWarnings("unchecked")
    private Optional<RuleViolations> evaluateRule(final Rule rule,
                                                  final RuleEvaluationContext context) {
        checkNotNull("rule",
                     rule);
        checkNotNull("context",
                     context);
        final Collection<RuleEvaluationHandler> handlers = getHandler(rule,
                                                                      context);
        final DefaultRuleViolations results = new DefaultRuleViolations();
        if (!handlers.isEmpty()) {
            handlers.forEach(h -> results.addViolations(h.evaluate(rule,
                                                                   context)));
            return Optional.of(results);
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    private Optional<RuleViolations> evaluateExtension(final RuleExtension rule,
                                                       final RuleEvaluationContext context) {
        checkNotNull("rule",
                     rule);
        checkNotNull("context",
                     context);
        final Optional<RuleExtensionHandler> handler = getExtensionHandler(rule,
                                                                           context);
        return handler.isPresent() ? Optional.of(handler.get().evaluate(rule,
                                                                        context)) : Optional.empty();
    }

    private Collection<RuleEvaluationHandler> getHandler(final Rule rule,
                                                         final RuleEvaluationContext context) {
        final Collection<RuleEvaluationHandler> handlers = registry.getHandlersByContext(context.getType());
        return handlers.stream()
                .filter(h -> accepts(h,
                                     rule,
                                     context))
                .collect(Collectors.toList());
    }

    private Optional<RuleExtensionHandler> getExtensionHandler(final RuleExtension rule,
                                                               final RuleEvaluationContext context) {
        final RuleExtensionHandler handler = registry.getExtensionHandler(rule.getHandlerType());
        return null != handler
                && accepts(handler,
                           rule,
                           context) ? Optional.of(handler) : Optional.empty();
    }

    /**
     * Handler acceptance based on:
     * 1- Rule and context types - for performance and computing purposes.
     * 2- Once types are known accepted - do a second acceptance evaluation based
     * on the context's state at runtime.
     * 3.- Once 1) AND 2) - the handler is able to perform more complex runtime
     * evaluation, the evaluation can be delegated to it.
     */
    @SuppressWarnings("unchecked")
    private boolean accepts(final RuleEvaluationHandler handler,
                            final Rule rule,
                            final RuleEvaluationContext context) {
        return handler.getRuleType().equals(rule.getClass())
                && (handler.getContextType().equals(context.getType())
                || RuleEvaluationContext.class.equals(handler.getContextType()))
                && handler.accepts(rule,
                                   context);
    }
}
