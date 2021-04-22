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

/**
 * A rule set provides several rule instances
 * logically grouped to achieve some semantics.
 * @See {@link Rule}
 */
public interface RuleSet {

    /**
     * A name for the set of rules.
     */
    String getName();

    /**
     * Returns the rule instances for this rule set.
     */
    Collection<Rule> getRules();
}
