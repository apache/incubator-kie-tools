/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.backend.marshall.json.builder;

import java.util.Collection;

import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.OryxManager;
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
import org.kie.workbench.common.stunner.core.registry.impl.DefinitionsCacheRegistry;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

public interface GraphObjectBuilder<W, T extends Element<View<W>>> {

    GraphObjectBuilder<W, T> nodeId(final String nodeId);

    GraphObjectBuilder<W, T> stencil(final String stencilId);

    GraphObjectBuilder<W, T> property(final String key,
                                      final String value);

    GraphObjectBuilder<W, T> out(final String nodeId);

    GraphObjectBuilder<W, T> boundUL(final Double x,
                                     final Double y);

    GraphObjectBuilder<W, T> boundLR(final Double x,
                                     final Double y);

    GraphObjectBuilder<W, T> docker(final Double x,
                                    final Double y);

    T build(final BuilderContext context);

    interface BuilderContext {

        BuilderContext init(final Graph<DefinitionSet, Node> graph);

        Index<?, ?> getIndex();

        Collection<GraphObjectBuilder<?, ?>> getBuilders();

        DefinitionManager getDefinitionManager();

        FactoryManager getFactoryManager();

        DefinitionsCacheRegistry getDefinitionsRegistry();

        OryxManager getOryxManager();

        GraphCommandFactory getCommandFactory();

        CommandResult<RuleViolation> execute(final Command<GraphCommandExecutionContext, RuleViolation> command);
    }
}
