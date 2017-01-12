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

package org.kie.workbench.common.stunner.core.rule.impl.graph;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.rule.ContainmentRule;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.graph.GraphContainmentRuleManager;
import org.kie.workbench.common.stunner.core.rule.model.ModelContainmentRuleManager;

@Dependent
public class GraphContainmentRuleManagerImpl extends AbstractGraphRuleManager<ContainmentRule, ModelContainmentRuleManager>
        implements GraphContainmentRuleManager {

    private static final String NAME = "Graph Containment Rule Manager";

    private final ModelContainmentRuleManager modelContainmentRuleManager;

    protected GraphContainmentRuleManagerImpl() {
        this( null,
              null );
    }

    @Inject
    public GraphContainmentRuleManagerImpl( final DefinitionManager definitionManager,
                                            final ModelContainmentRuleManager modelContainmentRuleManager ) {
        super( definitionManager );
        this.modelContainmentRuleManager = modelContainmentRuleManager;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected ModelContainmentRuleManager getWrapped() {
        return modelContainmentRuleManager;
    }

    @Override
    public RuleViolations evaluate( final Element<?> target,
                                    final Element<? extends Definition<?>> candidate ) {
        final String targetId = getElementDefinitionId( target );
        return modelContainmentRuleManager.evaluate( targetId,
                                                     getLabels( candidate ) );
    }
}
