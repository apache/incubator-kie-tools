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

package org.kie.workbench.common.stunner.core.rule;

/**
 * Base manager type for docking rules.
 * <p/>
 * It checks cardinality rules and evaluates if the given candidate can be dock into another node.
 * Docking rules are role based.
 * <p/>
 * Each domain specific implementation can provide its own argument types to evaluate internally the rules.
 * See <a>org.kie.workbench.common.stunner.core.rule.model.ModelDockingRuleManager</a>
 * See <a>org.kie.workbench.common.stunner.core.rule.graph.GraphDockingRuleManager</a>
 */
public interface DockingRuleManager extends RuleManager<DockingRule> {

}
