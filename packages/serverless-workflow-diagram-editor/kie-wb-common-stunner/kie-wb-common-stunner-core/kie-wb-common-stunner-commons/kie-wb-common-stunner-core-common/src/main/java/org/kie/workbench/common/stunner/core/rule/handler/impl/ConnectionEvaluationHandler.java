/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package org.kie.workbench.common.stunner.core.rule.handler.impl;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationHandler;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.context.ConnectionContext;
import org.kie.workbench.common.stunner.core.rule.impl.CanConnect;
import org.kie.workbench.common.stunner.core.rule.violations.ConnectionRuleViolation;
import org.kie.workbench.common.stunner.core.rule.violations.DefaultRuleViolations;
import org.uberfire.commons.Pair;

@ApplicationScoped
public class ConnectionEvaluationHandler implements RuleEvaluationHandler<CanConnect, ConnectionContext> {

    @Override
    public Class<CanConnect> getRuleType() {
        return CanConnect.class;
    }

    @Override
    public Class<ConnectionContext> getContextType() {
        return ConnectionContext.class;
    }

    @Override
    public boolean accepts(final CanConnect rule,
                           final ConnectionContext context) {
        return rule.getRole().equals(context.getConnectorRole());
    }

    @Override
    public RuleViolations evaluate(final CanConnect rule,
                                   final ConnectionContext context) {
        final List<CanConnect.PermittedConnection> permittedConnections = rule.getPermittedConnections();
        final String currentConnectorRole = context.getConnectorRole();
        final Set<String> incomingLabels = context.getTargetRoles().orElse(Collections.emptySet());
        final Set<String> outgoingLabels = context.getSourceRoles().orElse(Collections.emptySet());
        final DefaultRuleViolations results = new DefaultRuleViolations();
        final Set<Pair<String, String>> couples = new LinkedHashSet<>();
        for (CanConnect.PermittedConnection pc : permittedConnections) {
            final boolean startMatch = outgoingLabels.contains(pc.getStartRole());
            final boolean endMatch = startMatch && incomingLabels.contains(pc.getEndRole());
            if (endMatch) {
                return results;
            }
            couples.add(new Pair<>(pc.getStartRole(),
                                   pc.getEndRole()));
        }
        results.addViolation(new ConnectionRuleViolation(currentConnectorRole,
                                                         serializeAllowedConnections(couples)));
        return results;
    }

    private Set<String> serializeAllowedConnections(final Set<Pair<String, String>> couples) {
        return couples.stream()
                .map(p -> "{'" + p.getK1() + "' ->'" + p.getK2() + "'}")
                .collect(Collectors.toSet());
    }
}
