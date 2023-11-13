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

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.definition.morph.MorphDefinition;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.lookup.domain.DomainLookupFunctions.FilterConnectionTargetDefinitions;
import org.kie.workbench.common.stunner.core.lookup.domain.DomainLookupFunctions.LookupAllowedDefinitionsByLabels;
import org.kie.workbench.common.stunner.core.lookup.domain.DomainLookupFunctions.LookupTargetConnectors;
import org.kie.workbench.common.stunner.core.lookup.domain.DomainLookupFunctions.LookupTargetRoles;
import org.kie.workbench.common.stunner.core.registry.impl.DefinitionsCacheRegistry;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

@Dependent
public class CommonDomainLookups {

    private final DefinitionUtils definitionUtils;
    private final DefinitionsCacheRegistry definitionsRegistry;
    private final RuleManager ruleManager;
    private final Function<String, DomainLookupsCache> cacheBuilder;

    private DomainLookupsCache cache;

    @Inject
    public CommonDomainLookups(final DefinitionUtils definitionUtils,
                               final DefinitionsCacheRegistry definitionsRegistry,
                               final RuleManager ruleManager) {
        this(definitionUtils,
             definitionsRegistry,
             ruleManager,
             defSetId -> new DomainLookupsCache(definitionUtils.getDefinitionManager(),
                                                definitionsRegistry,
                                                defSetId));
    }

    CommonDomainLookups(final DefinitionUtils definitionUtils,
                        final DefinitionsCacheRegistry definitionsRegistry,
                        final RuleManager ruleManager,
                        final Function<String, DomainLookupsCache> cacheBuilder) {
        this.definitionUtils = definitionUtils;
        this.definitionsRegistry = definitionsRegistry;
        this.ruleManager = ruleManager;
        this.cacheBuilder = cacheBuilder;
    }

    public CommonDomainLookups setDomain(final String defSetId) {
        assert null == cache;
        this.cache = cacheBuilder.apply(defSetId);
        return this;
    }

    public Set<String> lookupTargetConnectors(final Node<? extends Definition<Object>, ? extends Edge> sourceNode) {
        final DomainLookupContext context = newContext();
        return new LookupTargetConnectors(sourceNode)
                .execute(context);
    }

    private DomainLookupContext newContext() {
        return new DomainLookupContext(getDefinitionManager(),
                                       definitionsRegistry,
                                       ruleManager,
                                       cache);
    }

    public Set<String> lookupTargetNodes(final Graph<?, ? extends Node> graph,
                                         final Node<? extends Definition<Object>, ? extends Edge> sourceNode,
                                         final String edgeId) {
        return lookupTargetNodes(graph,
                                 sourceNode,
                                 edgeId,
                                 def -> true);
    }

    public Set<String> lookupTargetNodes(final Graph<?, ? extends Node> graph,
                                         final Node<? extends Definition<Object>, ? extends Edge> sourceNode,
                                         final String edgeId,
                                         final Predicate<String> definitionIdsAllowedFilter) {
        final DomainLookupContext context = newContext();
        final Set<String> targetRoles =
                new LookupTargetRoles(sourceNode,
                                      edgeId)
                        .execute(context);

        final Set<String> allowedTargetDefinitions =
                new LookupAllowedDefinitionsByLabels(graph,
                                                     targetRoles,
                                                     definitionIdsAllowedFilter)
                        .execute(context);

        return new FilterConnectionTargetDefinitions(edgeId, allowedTargetDefinitions).execute(context);
    }

    public Set<String> lookupMorphBaseDefinitions(final Set<String> definitionIds) {
        final Set<String> result = new LinkedHashSet<>();
        for (final String definitionId : definitionIds) {
            final Object definition = definitionsRegistry.getDefinitionById(definitionId);
            final MorphDefinition morphDefinition = definitionUtils.getMorphDefinition(definition);
            final boolean hasMorphBase = null != morphDefinition;
            final String id = hasMorphBase ? morphDefinition.getDefault() : definitionId;
            result.add(id);
        }
        return result;
    }

    @PreDestroy
    public void destroy() {
        if (null != cache) {
            cache.clear();
            cache = null;
        }
    }

    DomainLookupsCache getCache() {
        return cache;
    }

    private DefinitionManager getDefinitionManager() {
        return definitionUtils.getDefinitionManager();
    }
}
