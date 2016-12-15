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

import org.kie.workbench.common.stunner.core.rule.*;

public abstract class AbstractRulesManager<C extends ContainmentRuleManager,
        L extends ConnectionRuleManager,
        K extends CardinalityRuleManager,
        E extends EdgeCardinalityRuleManager,
        D extends DockingRuleManager> implements RulesManager<C, L, K, E, D> {

    protected final C containmentRuleManager;
    protected final L connectionRuleManager;
    protected final K cardinalityRuleManager;
    protected final E edgeCardinalityRuleManager;
    protected final D dockingRuleManager;

    public AbstractRulesManager( final C containmentRuleManager,
                                 final L connectionRuleManager,
                                 final K cardinalityRuleManager,
                                 final E edgeCardinalityRuleManager,
                                 final D dockingRuleManager ) {
        this.containmentRuleManager = containmentRuleManager;
        this.connectionRuleManager = connectionRuleManager;
        this.cardinalityRuleManager = cardinalityRuleManager;
        this.edgeCardinalityRuleManager = edgeCardinalityRuleManager;
        this.dockingRuleManager = dockingRuleManager;
    }

    @Override
    public boolean supports( final Rule rule ) {
        return connectionRuleManager.supports( rule ) ||
                containmentRuleManager.supports( rule ) ||
                cardinalityRuleManager.supports( rule ) ||
                edgeCardinalityRuleManager.supports( rule ) ||
                dockingRuleManager.supports( rule );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public RuleManager addRule( final Rule rule ) {
        if ( connectionRuleManager.supports( rule ) ) {
            connectionRuleManager.addRule( ( ConnectionRule ) rule );

        } else if ( containmentRuleManager.supports( rule ) ) {
            containmentRuleManager.addRule( ( ContainmentRule ) rule );

        } else if ( cardinalityRuleManager.supports( rule ) ) {
            cardinalityRuleManager.addRule( ( CardinalityRule ) rule );

        } else if ( edgeCardinalityRuleManager.supports( rule ) ) {
            edgeCardinalityRuleManager.addRule( ( EdgeCardinalityRule ) rule );

        } else if ( dockingRuleManager.supports( rule ) ) {
            dockingRuleManager.addRule( ( DockingRule ) rule );

        }
        return this;
    }

    @Override
    public RuleManager clearRules() {
        containmentRuleManager.clearRules();
        connectionRuleManager.clearRules();
        cardinalityRuleManager.clearRules();
        edgeCardinalityRuleManager.clearRules();
        dockingRuleManager.clearRules();
        return this;
    }

    @Override
    public C containment() {
        return containmentRuleManager;
    }

    @Override
    public L connection() {
        return connectionRuleManager;
    }

    @Override
    public K cardinality() {
        return cardinalityRuleManager;
    }

    @Override
    public E edgeCardinality() {
        return edgeCardinalityRuleManager;
    }

    @Override
    public D docking() {
        return dockingRuleManager;
    }

}
