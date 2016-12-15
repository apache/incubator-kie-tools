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

package org.kie.workbench.common.stunner.core.rule.impl;

import org.kie.workbench.common.stunner.core.rule.Rule;
import org.kie.workbench.common.stunner.core.rule.RuleManager;

public abstract class AbstractWrappedRuleManager<R extends Rule, M extends RuleManager<R>> implements RuleManager<R> {

    protected abstract M getWrapped();

    @Override
    public boolean supports( final Rule rule ) {
        return getWrapped().supports( rule );
    }

    @Override
    public RuleManager addRule( final R rule ) {
        return getWrapped().addRule( rule );
    }

    @Override
    public RuleManager clearRules() {
        getWrapped().clearRules();
        return this;
    }

}
