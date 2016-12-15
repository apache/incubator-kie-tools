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

import org.kie.workbench.common.stunner.core.rule.EdgeCardinalityRule;
import org.kie.workbench.common.stunner.core.rule.EdgeCardinalityRuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;

import java.util.Set;

/**
 * Manager for connector's cardinality rules specific for the Stunner's domain model.
 */
public interface ModelEdgeCardinalityRuleManager
        extends EdgeCardinalityRuleManager {

    /**
     * It checks cardinality rules and evaluates if the given connector candidate added or removed into the structure.
     *
     * @param edgeId The connector definition's identifier.
     * @param labels The roles/labels for the node.
     * @param count The current connector's count for the node.
     * @param ruleType if it's an incoming or outgoing connection.
     * @param operation Can be adding a new connector, removing an existing one, or NONE, eg: just to validate rules
     *                  against current structure.
     */
    RuleViolations evaluate( String edgeId,
                             Set<String> labels,
                             int count,
                             EdgeCardinalityRule.Type ruleType,
                             Operation operation );

}
