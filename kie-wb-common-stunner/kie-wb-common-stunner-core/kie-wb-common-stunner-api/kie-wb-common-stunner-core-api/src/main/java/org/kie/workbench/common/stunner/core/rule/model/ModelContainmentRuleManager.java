/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.rule.model;

import java.util.Set;

import org.kie.workbench.common.stunner.core.rule.ContainmentRuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;

/**
 * Manager for containment rules specific for the Stunner's domain model.
 */
public interface ModelContainmentRuleManager extends ContainmentRuleManager {

    /**
     * It checks containment rules and evaluates if the given candidate role can be added or removed
     * into/from another node.
     * @param targetId The parent node definition's identifier.
     * @param candidateRoles The roles for the candidate child node.
     */
    RuleViolations evaluate(final String targetId,
                            final Set<String> candidateRoles);
}
