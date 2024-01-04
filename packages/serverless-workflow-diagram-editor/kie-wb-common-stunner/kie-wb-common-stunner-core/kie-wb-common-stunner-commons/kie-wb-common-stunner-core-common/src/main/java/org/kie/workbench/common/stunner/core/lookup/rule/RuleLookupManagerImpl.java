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


package org.kie.workbench.common.stunner.core.lookup.rule;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.lookup.criteria.AbstractCriteriaLookupManager;
import org.kie.workbench.common.stunner.core.rule.Rule;
import org.kie.workbench.common.stunner.core.rule.context.EdgeCardinalityContext;
import org.kie.workbench.common.stunner.core.rule.impl.CanConnect;
import org.kie.workbench.common.stunner.core.rule.impl.CanContain;
import org.kie.workbench.common.stunner.core.rule.impl.EdgeOccurrences;
import org.kie.workbench.common.stunner.core.rule.impl.Occurrences;

@ApplicationScoped
public class RuleLookupManagerImpl
        extends AbstractCriteriaLookupManager<Rule, Rule, RuleLookupRequest>
        implements RuleLookupManager {

    private static final String TYPE = "type";
    private static final String ROLES = "roles";
    private static final String ID = "id";
    private static final String ROLE = "role";
    private static final String ROLE_IN = "roleIn";
    private static final String EDGE_TYPE = "edgeType";
    private static final String FROM = "from";
    private static final String TO = "to";
    private static final String INCOMING = "incoming";
    private static final String CONTAINMENT = "containment";
    private static final String CONNECTION = "connection";
    private static final String CARDINALITY = "cardinality";
    private static final String EDGECARDINALITY = "edgecardinality";

    private final DefinitionManager definitionManager;

    protected RuleLookupManagerImpl() {
        this(null);
    }

    @Inject
    public RuleLookupManagerImpl(final DefinitionManager definitionManager) {
        this.definitionManager = definitionManager;
    }

    @Override
    protected List<Rule> getItems(final RuleLookupRequest request) {
        final String defSetId = request.getDefinitionSetId();
        final Object defSet = definitionManager.definitionSets().getDefinitionSetById(defSetId);
        if (null != defSet) {
            final Collection<Rule> rules =
                    (Collection<Rule>) definitionManager.adapters().forRules().getRuleSet(defSet).getRules();
            return new LinkedList<>(rules);
        }
        return null;
    }

    @Override
    protected Rule buildResult(final Rule rule) {
        return rule;
    }

    @Override
    protected boolean matches(final String key,
                              final String value,
                              final Rule rule) {
        switch (key) {
            case TYPE:
                return CONTAINMENT.equals(value) && (rule instanceof CanContain) ||
                        CONNECTION.equals(value) && (rule instanceof CanConnect) ||
                        CARDINALITY.equals(value) && (rule instanceof Occurrences) ||
                        EDGECARDINALITY.equals(value) && (rule instanceof EdgeOccurrences);
            case ROLES:
                try {
                    // Permitted roles on containment rules.
                    final CanContain cr = (CanContain) rule;
                    final Collection<String> rolesSet = toCollection(value);
                    if (null != rolesSet) {
                        return isIntersect(cr.getAllowedRoles(),
                                           rolesSet);
                    }
                } catch (final ClassCastException e) {
                    return false;
                }
                return true;
            case ID:
                String _id = null;
                if (rule instanceof CanContain) {
                    final CanContain er = (CanContain) rule;
                    _id = er.getRole();
                } else if (rule instanceof CanConnect) {
                    final CanConnect er = (CanConnect) rule;
                    _id = er.getRole();
                }
                return _id != null && _id.equals(value);
            case ROLE:
                if (rule instanceof EdgeOccurrences) {
                    final EdgeOccurrences er = (EdgeOccurrences) rule;
                    return (er.getRole().equals(value));
                }
                return false;
            case ROLE_IN:
                if (rule instanceof EdgeOccurrences) {
                    final EdgeOccurrences er = (EdgeOccurrences) rule;
                    final Collection<String> set = toCollection(value);
                    if (null != set && !set.isEmpty()) {
                        for (final String s : set) {
                            if (er.getRole().equals(value)) {
                                return true;
                            }
                        }
                    }
                }
                return false;
            case EDGE_TYPE:
                try {
                    final EdgeOccurrences er = (EdgeOccurrences) rule;
                    return er.getDirection().equals(INCOMING.equals(value) ?
                                                            EdgeCardinalityContext.Direction.INCOMING : EdgeCardinalityContext.Direction.OUTGOING);
                } catch (final ClassCastException e) {
                    return false;
                }
            case FROM:
            case TO:
                // Connection rules.
                try {
                    final CanConnect cr = (CanConnect) rule;
                    final Collection<String> fromSet = toCollection(value);
                    Set<String> ruleSet = getRoles(cr.getPermittedConnections(),
                                                   FROM.equals(key));
                    if (null != fromSet) {
                        return isIntersect(fromSet,
                                           ruleSet);
                    }
                } catch (final Exception e) {
                    return false;
                }
        }
        throw new UnsupportedOperationException("Cannot filter rules by key [" + key + "]");
    }

    private Set<String> getRoles(final List<CanConnect.PermittedConnection> connections,
                                 final boolean from) {
        if (null != connections) {
            final HashSet<String> result = new HashSet<>(connections.size());
            for (final CanConnect.PermittedConnection c : connections) {
                if (from) {
                    result.add(c.getStartRole());
                } else {
                    result.add(c.getEndRole());
                }
            }
            return result;
        }
        return null;
    }
}
