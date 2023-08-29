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

import java.util.Collection;
import java.util.Set;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.violations.AbstractGraphViolation;

public class GraphEvaluationHandlerUtils {

    private final DefinitionManager definitionManager;

    public GraphEvaluationHandlerUtils(final DefinitionManager definitionManager) {
        this.definitionManager = definitionManager;
    }

    @SuppressWarnings("unchecked")
    public String getElementDefinitionId(final Element<?> element) {
        String targetId = null;
        if (element.getContent() instanceof Definition) {
            Object definition = ((Definition) element.getContent()).getDefinition();
            targetId = getDefinitionId(definition);
        } else if (element.getContent() instanceof DefinitionSet) {
            targetId = ((DefinitionSet) element.getContent()).getDefinition();
        }
        return targetId;
    }

    public String getDefinitionId(final Object definition) {
        return definitionManager.adapters().forDefinition().getId(definition).value();
    }

    public Set<String> getLabels(final Element<? extends Definition<?>> element) {
        return GraphUtils.getLabels(element);
    }

    public int countEdges(final String edgeId,
                          final Collection<? extends Edge> edges) {
        return GraphUtils.countEdges(definitionManager,
                                     edgeId,
                                     edges);
    }

    public static RuleViolations addViolationsSourceUUID(final String uuid,
                                                         final RuleViolations result) {
        result.violations().forEach(v -> addViolationSourceUUID(uuid,
                                                                v));
        return result;
    }

    public static RuleViolation addViolationSourceUUID(final String uuid,
                                                       final RuleViolation violation) {
        if (violation instanceof AbstractGraphViolation) {
            ((AbstractGraphViolation) violation).setUUID(uuid);
        }
        return violation;
    }
}
