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

package org.kie.workbench.common.stunner.core.rule.impl.model;

import org.kie.workbench.common.stunner.core.rule.ConnectionRule;
import org.kie.workbench.common.stunner.core.rule.DefaultRuleViolations;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.impl.AbstractConnectionRuleManager;
import org.kie.workbench.common.stunner.core.rule.impl.violations.ConnectionRuleViolation;
import org.kie.workbench.common.stunner.core.rule.model.ModelConnectionRuleManager;
import org.uberfire.commons.data.Pair;

import javax.enterprise.context.Dependent;
import java.util.HashSet;
import java.util.Set;

@Dependent
public class ModelConnectionRuleManagerImpl extends AbstractConnectionRuleManager implements ModelConnectionRuleManager {

    private static final String NAME = "Domain Model Connection Rule Manager";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public RuleViolations evaluate( final String edgeId,
                                    final Set<String> outgoingLabels,
                                    final Set<String> incomingLabels ) {
        if ( rules.isEmpty() ) {
            return new DefaultRuleViolations();
        }
        final DefaultRuleViolations results = new DefaultRuleViolations();
        final Set<Pair<String, String>> couples = new HashSet<Pair<String, String>>();
        for ( ConnectionRule rule : rules ) {
            if ( edgeId.equals( rule.getId() ) ) {
                for ( ConnectionRule.PermittedConnection pc : rule.getPermittedConnections() ) {
                    couples.add( new Pair<String, String>( pc.getStartRole(), pc.getEndRole() ) );
                    if ( outgoingLabels.contains( pc.getStartRole() ) ) {
                        if ( incomingLabels.contains( pc.getEndRole() ) ) {
                            return results;
                        }
                    }
                }
            }
        }
        results.addViolation( new ConnectionRuleViolation( edgeId, couples ) );
        return results;

    }

}
