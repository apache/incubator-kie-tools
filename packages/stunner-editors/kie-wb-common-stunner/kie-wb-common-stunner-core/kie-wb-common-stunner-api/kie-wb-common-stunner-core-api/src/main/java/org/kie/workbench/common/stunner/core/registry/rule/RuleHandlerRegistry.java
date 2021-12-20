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

package org.kie.workbench.common.stunner.core.registry.rule;

import java.util.Collection;

import org.kie.workbench.common.stunner.core.registry.DynamicRegistry;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationHandler;
import org.kie.workbench.common.stunner.core.rule.ext.RuleExtensionHandler;

/**
 * A registry type that contains rule evaluations handlers.
 */
public interface RuleHandlerRegistry extends DynamicRegistry<RuleEvaluationHandler> {

    /**
     * Returns a collection of rule evaluation handlers that support
     * the given context type.
     */
    Collection<RuleEvaluationHandler> getHandlersByContext(final Class<?> context);

    /**
     * Returns the rule extension handler by its type.
     */
    <T extends RuleExtensionHandler> T getExtensionHandler(final Class<T> type);

    void clear();
}
