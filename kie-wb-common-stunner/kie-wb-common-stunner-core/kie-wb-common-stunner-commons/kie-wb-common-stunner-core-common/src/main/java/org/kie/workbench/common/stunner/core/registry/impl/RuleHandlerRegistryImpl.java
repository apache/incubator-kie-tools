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

package org.kie.workbench.common.stunner.core.registry.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.kie.workbench.common.stunner.core.registry.rule.RuleHandlerRegistry;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationHandler;
import org.kie.workbench.common.stunner.core.rule.ext.RuleExtensionHandler;

public class RuleHandlerRegistryImpl implements RuleHandlerRegistry {

    private final Map<Class<?>, List<RuleEvaluationHandler>> handlers = new HashMap<>(15);
    private final Map<Class<?>, RuleExtensionHandler> extensionHandlers = new LinkedHashMap<>();

    @Override
    public void register(final RuleEvaluationHandler handler) {
        if (handler instanceof RuleExtensionHandler) {
            addExtension((RuleExtensionHandler) handler);
        } else {
            addHandler(handler);
        }
    }

    @Override
    public Collection<RuleEvaluationHandler> getHandlersByContext(final Class<?> contextType) {
        return handlers.get(contextType);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends RuleExtensionHandler> T getExtensionHandler(final Class<T> contextType) {
        return (T) extensionHandlers.get(contextType);
    }

    @Override
    public void clear() {
        handlers.clear();
        extensionHandlers.clear();
    }

    @Override
    public boolean isEmpty() {
        return handlers.isEmpty() && extensionHandlers.isEmpty();
    }

    @Override
    public boolean remove(final RuleEvaluationHandler item) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean contains(final RuleEvaluationHandler item) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void addHandler(final RuleEvaluationHandler handler) {
        final Class<?> contextType = handler.getContextType();
        List<RuleEvaluationHandler> ruleEvaluationHandlers = handlers.get(contextType);
        if (null == ruleEvaluationHandlers) {
            ruleEvaluationHandlers = new LinkedList<>();
            handlers.put(contextType,
                         ruleEvaluationHandlers);
        }
        ruleEvaluationHandlers.add(handler);
    }

    private void addExtension(final RuleExtensionHandler handler) {
        extensionHandlers.put(handler.getExtensionType(),
                              handler);
    }
}
