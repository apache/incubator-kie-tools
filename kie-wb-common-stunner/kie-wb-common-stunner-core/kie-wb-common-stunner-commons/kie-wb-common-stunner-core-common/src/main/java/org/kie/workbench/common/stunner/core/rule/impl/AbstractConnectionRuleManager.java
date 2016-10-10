/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.rule.impl;

import org.kie.workbench.common.stunner.core.rule.*;

public abstract class AbstractConnectionRuleManager<A, B> extends AbstractRuleManager<ConnectionRule> implements ConnectionRuleManager<A, B> {

    protected abstract RuleViolations doEvaluate( A edgeId, B outgoingLabels, B incomingLabels );

    @Override
    public boolean supports( final Rule rule ) {
        return rule instanceof ConnectionRule;
    }

    @Override
    public RuleViolations evaluate( A edgeId, B outgoingLabels, B incomingLabels ) {
        if ( rules.isEmpty() ) {
            return new DefaultRuleViolations();
        }
        return doEvaluate( edgeId, outgoingLabels, incomingLabels );
    }

}
