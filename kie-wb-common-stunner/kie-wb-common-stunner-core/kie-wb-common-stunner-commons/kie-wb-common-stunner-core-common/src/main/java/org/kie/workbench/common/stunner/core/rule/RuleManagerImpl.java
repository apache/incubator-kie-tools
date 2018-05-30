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
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.registry.RegistryFactory;
import org.kie.workbench.common.stunner.core.registry.rule.RuleHandlerRegistry;
import org.kie.workbench.common.stunner.core.rule.ext.RuleExtension;
import org.kie.workbench.common.stunner.core.rule.ext.RuleExtensionHandler;
import org.kie.workbench.common.stunner.core.rule.violations.ContextOperationNotAllowedViolation;
import org.kie.workbench.common.stunner.core.rule.violations.DefaultRuleViolations;

@Dependent
@Typed(RuleManagerImpl.class)
public class RuleManagerImpl implements RuleManager {

    private final RuleHandlerRegistry registry;
    private RulesProvider rulesProvider;
    private RuleEvaluator ruleEvaluator;

    @Inject
    public RuleManagerImpl(final RegistryFactory registryFactory) {
        this.registry = null != registryFactory ? registryFactory.newRuleHandlerRegistry() : null;
        this.rulesProvider = new RulesFromRuleSetProvider();
        this.ruleEvaluator = new DefaultRuleEvaluator();
    }

    public RuleManagerImpl setRulesProvider(final RulesProvider rulesProvider) {
        this.rulesProvider = rulesProvider;
        return this;
    }

    public RuleManagerImpl setRuleEvaluator(final RuleEvaluator ruleEvaluator) {
        this.ruleEvaluator = ruleEvaluator;
        return this;
    }

    @Override
    public RuleViolations evaluate(final RuleSet ruleSet,
                                   final RuleEvaluationContext context) {
        /*
            Consider:
            - If no rules present on the rule set, no resulting rule violation instances
            are expected
            - If rules present but no rule accepts the runtime context inputs, the context type
            defines if allow/or deny the evaluation
            - Otherwise return the rule violations produced by the handlers or extensions
         */
        final DefaultRuleViolations results = new DefaultRuleViolations();
        final Collection<Rule> rules = rulesProvider.get(ruleSet,
                                                         context);
        if (rules.isEmpty()) {
            return results;
        }
        final Collection<RuleEvaluationHandler> handlers = registry.getHandlersByContext(context.getType());
        final List<Optional<RuleViolations>> handlersViolations = rules.stream()
                .map(rule -> evaluate(rule,
                                      handlers,
                                      context))
                .collect(Collectors.toList());
        final List<RuleViolations> violations = handlersViolations.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        final boolean anyHandlerProcessed = handlersViolations.size() > 0 && violations.size() > 0;
        if (!anyHandlerProcessed && context.isDefaultDeny()) {
            return getDefaultViolationForContext(context);
        }
        violations.forEach(results::addViolations);
        return results;
    }

    private Optional<RuleViolations> evaluate(final Rule rule,
                                              final Collection<RuleEvaluationHandler> handlers,
                                              final RuleEvaluationContext context) {
        return isRuleExtension().test(rule) ?
                evaluateExtension((RuleExtension) rule,
                                  context) :
                evaluateRule(rule,
                             handlers,
                             context);
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

    @PreDestroy
    public void destroy() {
        registry().clear();
        rulesProvider = null;
        ruleEvaluator = null;
    }

    @SuppressWarnings("unchecked")
    private Optional<RuleViolations> evaluateRule(final Rule rule,
                                                  final Collection<RuleEvaluationHandler> evaluationHandlers,
                                                  final RuleEvaluationContext context) {
        final Collection<RuleEvaluationHandler> handlers = getHandler(rule,
                                                                      evaluationHandlers,
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
        final Optional<RuleExtensionHandler> handler = getExtensionHandler(rule,
                                                                           context);
        return handler.isPresent() ? Optional.of(handler.get().evaluate(rule,
                                                                        context)) : Optional.empty();
    }

    private Collection<RuleEvaluationHandler> getHandler(final Rule rule,
                                                         final Collection<RuleEvaluationHandler> handlers,
                                                         final RuleEvaluationContext context) {
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

    private boolean accepts(final RuleEvaluationHandler handler,
                            final Rule rule,
                            final RuleEvaluationContext context) {
        return ruleEvaluator.accepts(handler,
                                     rule,
                                     context);
    }

    public interface RulesProvider {

        Collection<Rule> get(RuleSet ruleSet,
                             RuleEvaluationContext context);
    }

    public static class RulesFromRuleSetProvider implements RulesProvider {

        @Override
        public Collection<Rule> get(final RuleSet ruleSet,
                                    final RuleEvaluationContext context) {
            return ruleSet.getRules();
        }
    }

    public interface RuleEvaluator {

        boolean accepts(RuleEvaluationHandler handler,
                        Rule rule,
                        RuleEvaluationContext context);
    }

    /**
     * Handler acceptance based on:
     * 1- Rule and context types - for performance and computing purposes.
     * 2- Once types are known accepted - do a second acceptance evaluation based
     * on the context's state at runtime.
     * 3.- Once 1) AND 2) - the handler is able to perform more complex runtime
     * evaluation, the evaluation can be delegated to it.
     */
    public static class DefaultRuleEvaluator implements RuleEvaluator {

        @Override
        @SuppressWarnings("unchecked")
        public boolean accepts(final RuleEvaluationHandler handler,
                               final Rule rule,
                               final RuleEvaluationContext context) {
            return accepts(handler, rule)
                    && accepts(handler, context)
                    && handler.accepts(rule,
                                       context);
        }

        public boolean accepts(final RuleEvaluationHandler handler,
                               final Rule rule) {
            return RuleManagerImpl.isRuleTypeAllowed().test(rule, handler);
        }

        public boolean accepts(final RuleEvaluationHandler handler,
                               final RuleEvaluationContext context) {
            return RuleManagerImpl.accepts(handler, context);
        }
    }

    public static Predicate<Rule> isRuleExtension() {
        return rule -> rule instanceof RuleExtension;
    }

    public static BiPredicate<Rule, RuleEvaluationHandler> isRuleTypeAllowed() {
        return (rule, handler) -> handler.getRuleType().equals(rule.getClass());
    }

    public static boolean accepts(final RuleEvaluationHandler handler,
                                  final RuleEvaluationContext context) {
        return (handler.getContextType().equals(context.getType())
                || RuleEvaluationContext.class.equals(handler.getContextType()));
    }
}
