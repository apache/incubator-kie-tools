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

package org.kie.workbench.common.stunner.bpmn.backend.marshall.json.builder;

import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.Bpmn2OryxManager;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.impl.GraphCommandFactory;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

import java.util.Collection;

public interface GraphObjectBuilder<W, T extends Element<View<W>>> {

    GraphObjectBuilder<W, T> nodeId( String nodeId );

    GraphObjectBuilder<W, T> stencil( String stencilId );

    GraphObjectBuilder<W, T> property( String key, String value );

    GraphObjectBuilder<W, T> out( String nodeId );

    GraphObjectBuilder<W, T> boundUL( Double x, Double y );

    GraphObjectBuilder<W, T> boundLR( Double x, Double y );

    GraphObjectBuilder<W, T> docker( Double x, Double y );

    T build( BuilderContext context );

    interface BuilderContext {

        BuilderContext init( Graph<DefinitionSet, Node> graph );

        Index<?, ?> getIndex();

        Collection<GraphObjectBuilder<?, ?>> getBuilders();

        DefinitionManager getDefinitionManager();

        FactoryManager getFactoryManager();

        GraphUtils getGraphUtils();

        Bpmn2OryxManager getOryxManager();

        GraphCommandFactory getCommandFactory();

        CommandResult<RuleViolation> execute( Command<GraphCommandExecutionContext, RuleViolation> command );

    }

}
