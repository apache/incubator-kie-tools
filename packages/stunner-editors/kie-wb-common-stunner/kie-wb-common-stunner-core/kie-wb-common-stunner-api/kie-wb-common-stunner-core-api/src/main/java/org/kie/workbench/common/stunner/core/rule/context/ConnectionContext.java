/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.rule.context;

import java.util.Optional;
import java.util.Set;

import org.kie.workbench.common.stunner.core.rule.RuleEvaluationContext;

/**
 * This rule evaluation context provides the runtime information
 * that allows the evaluation for connection related operations.
 */
public interface ConnectionContext extends RuleEvaluationContext {

    /**
     * The role that the rules apply for.
     */
    String getConnectorRole();

    /**
     * The set of roles that the source node can satisfy
     * in order to allow the connection.
     */
    Optional<Set<String>> getSourceRoles();

    /**
     * The set of roles that the target node can satisfy
     * in order to allow the connection.
     */
    Optional<Set<String>> getTargetRoles();
}
