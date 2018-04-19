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
package org.kie.workbench.common.stunner.core.graph.command.impl;

import java.util.HashSet;
import java.util.Set;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.definition.adapter.MorphAdapter;
import org.kie.workbench.common.stunner.core.definition.morph.MorphDefinition;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

/**
 * A Command to morph a node in a graph.
 */
@Portable
public final class MorphNodeCommand extends AbstractGraphCommand {

    private Node<Definition, Edge> candidate;
    private MorphDefinition morphDefinition;
    private String morphTarget;
    private String oldMorphTarget;

    public MorphNodeCommand(final @MapsTo("candidate") Node<Definition, Edge> candidate,
                            final @MapsTo("morphDefinition") MorphDefinition morphDefinition,
                            final @MapsTo("morphTarget") String morphTarget) {
        this.candidate = PortablePreconditions.checkNotNull("candidate",
                                                            candidate);
        this.morphDefinition = PortablePreconditions.checkNotNull("morphDefinition",
                                                                  morphDefinition);
        this.morphTarget = PortablePreconditions.checkNotNull("morphTarget",
                                                              morphTarget);
        this.oldMorphTarget = null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResult<RuleViolation> execute(final GraphCommandExecutionContext context) {
        final CommandResult<RuleViolation> results = allow(context);
        if (!results.getType().equals(CommandResult.Type.ERROR)) {
            final DefinitionManager definitionManager = context.getDefinitionManager();
            final Object currentDef = candidate.getContent().getDefinition();
            final String currentDefId = definitionManager.adapters().forDefinition().getId(currentDef);
            this.oldMorphTarget = currentDefId;
            final MorphAdapter<Object> morphAdapter = context.getDefinitionManager().adapters().registry().getMorphAdapter(currentDef.getClass());
            if (null == morphAdapter) {
                throw new RuntimeException("No morph adapter found for definition [" + currentDef.toString() + "] " +
                                                   "and target morph [" + morphTarget + "]");
            }
            final Object newDef = morphAdapter.morph(currentDef,
                                                     morphDefinition,
                                                     morphTarget);
            if (null == newDef) {
                throw new RuntimeException("No morph resulting Definition. [ morphSource=" + currentDefId + ", " +
                                                   "morphTarget=" + morphTarget + "]");
            }

            // Morph the node definition to the new one.
            candidate.getContent().setDefinition(newDef);
            // Update candidate roles.
            final Set<String> newLabels = new HashSet<>();
            newLabels.add(definitionManager.adapters().forDefinition().getId(newDef));
            newLabels.addAll(definitionManager.adapters().forDefinition().getLabels(newDef));

            candidate.getLabels().clear();
            candidate.getLabels().addAll(newLabels);
        }
        return results;
    }

    @SuppressWarnings("unchecked")
    protected CommandResult<RuleViolation> check(final GraphCommandExecutionContext context) {
        // TODO: check rules before morphing?
        return GraphCommandResultBuilder.SUCCESS;
    }

    @Override
    public CommandResult<RuleViolation> undo(final GraphCommandExecutionContext context) {
        final MorphNodeCommand undoCommand = new MorphNodeCommand(candidate,
                                                                  morphDefinition,
                                                                  oldMorphTarget);
        return undoCommand.execute(context);
    }

    @Override
    public String toString() {
        return "MorphNodeCommand [candidate=" + candidate.getUUID() + ", morphTarget=" + morphTarget + "]";
    }
}

