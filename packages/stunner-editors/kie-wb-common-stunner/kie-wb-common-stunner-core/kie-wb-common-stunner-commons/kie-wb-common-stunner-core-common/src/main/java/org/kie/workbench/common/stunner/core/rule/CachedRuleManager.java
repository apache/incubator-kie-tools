/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.registry.rule.RuleHandlerRegistry;

@Dependent
@Typed(CachedRuleManager.class)
public class CachedRuleManager implements RuleManager {

    private final RuleManagerImpl ruleManager;
    private final Map<String, CachedContextRules> cachedContextRules;

    @Inject
    public CachedRuleManager(final RuleManagerImpl ruleManager) {
        this.ruleManager = ruleManager;
        this.cachedContextRules = new HashMap<>(4);
    }

    @PostConstruct
    public void init() {
        ruleManager.setRulesProvider(this::getRulesByContext)
                .setRuleEvaluator(new RuleManagerImpl.DefaultRuleEvaluator() {
                    @Override
                    public boolean accepts(final RuleEvaluationHandler handler,
                                           final Rule rule) {
                        // Acceptance by rule type is already satisfied due to the cache. No need to evaluate again.
                        return true;
                    }
                });
    }

    @Override
    public RuleHandlerRegistry registry() {
        return ruleManager.registry();
    }

    @Override
    public RuleViolations evaluate(final RuleSet ruleSet,
                                   final RuleEvaluationContext context) {
        return ruleManager.evaluate(ruleSet,
                                    context);
    }

    @PreDestroy
    public void destroy() {
        cachedContextRules.values().forEach(CachedContextRules::clear);
        cachedContextRules.clear();
    }

    private Collection<Rule> getRulesByContext(final RuleSet ruleSet,
                                               final RuleEvaluationContext context) {
        CachedContextRules crs = cachedContextRules.get(ruleSet.getName());
        if (null == crs) {
            crs = new CachedContextRules();
            cachedContextRules.put(ruleSet.getName(),
                                   crs);
        }
        return crs.getRulesByContext(ruleSet,
                                     context);
    }

    private class CachedContextRules {

        private final Map<Class<? extends RuleEvaluationContext>, List<Rule>> rulesByContent;

        public CachedContextRules() {
            this.rulesByContent = new HashMap<>(15);
        }

        public Collection<Rule> getRulesByContext(final RuleSet ruleSet,
                                                  final RuleEvaluationContext context) {
            List<Rule> rules = rulesByContent.get(context.getClass());
            if (null == rules) {
                return cacheRulesByContext(ruleSet,
                                           context);
            }
            return rules;
        }

        public Collection<Rule> cacheRulesByContext(final RuleSet ruleSet,
                                                    final RuleEvaluationContext context) {
            final Collection<RuleEvaluationHandler> handlers = registry().getHandlersByContext(context.getType());
            final List<Rule> rules = ruleSet.getRules().stream()
                    .filter(rule -> accepts(handlers,
                                            rule))
                    .collect(Collectors.toList());
            rulesByContent.put(context.getClass(), rules);
            return rules;
        }

        public void clear() {
            rulesByContent.clear();
        }

        private boolean accepts(final Collection<RuleEvaluationHandler> handlers,
                                final Rule rule) {
            return RuleManagerImpl.isRuleExtension().test(rule) ||
                    handlers.stream()
                            .anyMatch(handler -> RuleManagerImpl.isRuleTypeAllowed().test(rule, handler));
        }
    }
}
