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


package org.kie.workbench.common.stunner.core.lookup.domain;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.registry.impl.DefinitionsCacheRegistry;
import org.kie.workbench.common.stunner.core.rule.Rule;
import org.kie.workbench.common.stunner.core.rule.RuleSet;
import org.kie.workbench.common.stunner.core.rule.impl.CanConnect;

class DomainLookupsCache {

    private final String defSetId;
    private final RuleSet ruleSet;
    private final Map<String, Set<String>> definitionsByLabel;
    private final List<CanConnect> connectionRules;

    DomainLookupsCache(final DefinitionManager definitionManager,
                       final DefinitionsCacheRegistry definitionsRegistry,
                       final String defSetId) {
        this.defSetId = defSetId;
        this.definitionsByLabel = new HashMap<>(200);
        final Object definitionSet = definitionManager.definitionSets().getDefinitionSetById(defSetId);
        this.ruleSet = definitionManager.adapters().forRules().getRuleSet(definitionSet);
        this.connectionRules = ruleSet.getRules()
                .stream()
                .map(DomainLookupsCache::isConnectionRule)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        registerDefinitions(definitionManager,
                            definitionsRegistry,
                            definitionSet);
    }

    private static Optional<CanConnect> isConnectionRule(final Rule rule) {
        return rule instanceof CanConnect ?
                Optional.of((CanConnect) rule) :
                Optional.empty();
    }

    public Set<String> getDefinitions(final String label) {
        final Set<String> ids = definitionsByLabel.get(label);
        return null != ids ? ids : Collections.emptySet();
    }

    public void clear() {
        definitionsByLabel.clear();
        connectionRules.clear();
    }

    public String getDefinitionSetId() {
        return defSetId;
    }

    public List<CanConnect> getConnectionRules() {
        return connectionRules;
    }

    public RuleSet getRuleSet() {
        return ruleSet;
    }

    private void registerDefinitions(final DefinitionManager definitionManager,
                                     final DefinitionsCacheRegistry definitionsRegistry,
                                     final Object definitionSet) {
        definitionManager
                .adapters()
                .forDefinitionSet()
                .getDefinitions(definitionSet)
                .forEach(id -> registerDefinition(definitionsRegistry, id));
    }

    private void registerDefinition(final DefinitionsCacheRegistry definitionsRegistry,
                                    final String id) {
        definitionsRegistry.getLabels(id).forEach(label -> registerDefinition(label, id));
    }

    private void registerDefinition(final String label,
                                    final String id) {
        Set<String> ids = definitionsByLabel.computeIfAbsent(label, k -> new LinkedHashSet<>());
        ids.add(id);
    }
}
