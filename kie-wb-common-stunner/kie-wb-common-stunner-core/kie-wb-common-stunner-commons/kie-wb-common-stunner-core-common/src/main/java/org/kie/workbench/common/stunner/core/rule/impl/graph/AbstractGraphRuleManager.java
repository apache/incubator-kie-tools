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

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.rule.Rule;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.impl.AbstractWrappedRuleManager;

import java.util.Set;

public abstract class AbstractGraphRuleManager<R extends Rule, M extends RuleManager<R>>
        extends AbstractWrappedRuleManager<R, M> {

    protected DefinitionManager definitionManager;

    public AbstractGraphRuleManager( final DefinitionManager definitionManager ) {
        this.definitionManager = definitionManager;
    }

    @SuppressWarnings( "unchecked" )
    protected String getElementDefinitionId( final Element<?> element ) {
        String targetId = null;
        if ( element.getContent() instanceof View ) {
            Object definition = ( ( View ) element.getContent() ).getDefinition();
            targetId = getDefinitionId( definition );
        } else if ( element.getContent() instanceof DefinitionSet ) {
            targetId = ( ( DefinitionSet ) element.getContent() ).getDefinition();
        }
        return targetId;
    }

    @SuppressWarnings( "unchecked" )
    protected String getDefinitionId( final Object definition ) {
        return definitionManager.adapters().forDefinition().getId( definition );
    }

    protected Set<String> getLabels( final Element<? extends Definition<?>> element ) {
        return element != null ? element.getLabels() : null;
    }

}
