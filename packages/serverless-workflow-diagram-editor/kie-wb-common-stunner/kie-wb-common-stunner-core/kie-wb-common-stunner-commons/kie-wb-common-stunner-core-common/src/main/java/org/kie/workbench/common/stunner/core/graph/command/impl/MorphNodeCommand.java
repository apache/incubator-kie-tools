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

package org.kie.workbench.common.stunner.core.graph.command.impl;

import java.util.Objects;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.MorphAdapter;
import org.kie.workbench.common.stunner.core.definition.morph.MorphDefinition;
import org.kie.workbench.common.stunner.core.factory.impl.AbstractElementFactory;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

/**
 * A Command to morph a node.
 */
@Portable
public final class MorphNodeCommand extends AbstractGraphCommand {

    private final String uuid;
    private final MorphDefinition morphDefinition;
    private final String morphTarget;
    private transient String oldMorphTarget;
    private transient Node<Definition, Edge> candidate;

    public MorphNodeCommand(final @MapsTo("uuid") String uuid,
                            final @MapsTo("morphDefinition") MorphDefinition morphDefinition,
                            final @MapsTo("morphTarget") String morphTarget) {
        this.uuid = checkNotNull("uuid", uuid);
        this.morphDefinition = checkNotNull("morphDefinition", morphDefinition);
        this.morphTarget = checkNotNull("morphTarget", morphTarget);
        this.oldMorphTarget = null;
        this.candidate = null;
    }

    public MorphNodeCommand(final Node<Definition, Edge> candidate,
                            final MorphDefinition morphDefinition,
                            final String morphTarget) {
        this(candidate.getUUID(),
             morphDefinition,
             morphTarget);
        this.candidate = checkNotNull("candidate", candidate);
    }

    private static <T> T checkNotNull(String objName, T obj) {
        return Objects.requireNonNull(obj, "Parameter named '" + objName + "' should be not null!");
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResult<RuleViolation> execute(final GraphCommandExecutionContext context) {
        checkSafeCandidate(context);

        final CommandResult<RuleViolation> results = check(context);
        final DefinitionManager definitionManager = context.getDefinitionManager();
        final Object currentDef = candidate.getContent().getDefinition();
        final String currentDefId = definitionManager.adapters().forDefinition().getId(currentDef).value();
        this.oldMorphTarget = currentDefId;
        final MorphAdapter<Object> morphAdapter = context.getDefinitionManager().adapters().registry().getMorphAdapter(currentDef.getClass());
        if (null == morphAdapter) {
            throw new RuntimeException("No morph adapter found for definition [" + currentDef.toString() + "] " +
                                               "and target morph [" + morphTarget + "]");
        }
        // Morph the bean instance.
        final Object targetDef = morphAdapter.morph(currentDef,
                                                    morphDefinition,
                                                    morphTarget);
        if (null == targetDef) {
            throw new RuntimeException("No morph resulting Definition. [ morphSource=" + currentDefId + ", " +
                                               "morphTarget=" + morphTarget + "]");
        }

        // Assign the resulting instance to the node,
        candidate.getContent().setDefinition(targetDef);

        // Update the node's labels.
        final DefinitionAdapter<Object> adapter =
                definitionManager
                        .adapters()
                        .registry()
                        .getDefinitionAdapter(targetDef.getClass());
        candidate.getLabels().clear();
        final String[] labels = AbstractElementFactory.computeLabels(adapter, targetDef);
        for (String label : labels) {
            candidate.getLabels().add(label);
        }
        return results;
    }

    @SuppressWarnings("unchecked")
    protected CommandResult<RuleViolation> check(final GraphCommandExecutionContext context) {
        // TODO: check rules before morphing - see https://issues.jboss.org/browse/JBPM-8524
        return GraphCommandResultBuilder.SUCCESS;
    }

    @Override
    public CommandResult<RuleViolation> undo(final GraphCommandExecutionContext context) {
        final MorphNodeCommand undoCommand = new MorphNodeCommand(uuid,
                                                                  morphDefinition,
                                                                  oldMorphTarget);
        return undoCommand.execute(context);
    }

    private void checkSafeCandidate(final GraphCommandExecutionContext context) {
        if (null == candidate) {
            candidate = super.getNodeNotNull(context, uuid);
        }
    }

    @Override
    public String toString() {
        return "MorphNodeCommand [candidate=" + uuid + ", morphTarget=" + morphTarget + "]";
    }
}