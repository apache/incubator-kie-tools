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

import org.kie.workbench.common.stunner.core.rule.ConnectionRuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;

/**
 * Manager for connection rules specific for the Stunner's domain model.
 */
public interface ModelConnectionRuleManager extends ConnectionRuleManager {

    /**
     * It checks connection rules and evaluates if the given connector candidate identifier can
     * be attached to the given source/target node.
     * @param edgeId The connector definition's identifier.
     * @param outgoingLabels The roles/labels for the outgoing node.
     * @param incomingLabels The roles/labels for the incoming node.
     */
    RuleViolations evaluate(final String edgeId,
                            final Set<String> outgoingLabels,
                            final Set<String> incomingLabels);
}
