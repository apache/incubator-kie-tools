/*
 * Copyright 2015 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.ext.wires.bpmn.client.rules;

import org.uberfire.ext.wires.bpmn.api.model.BpmnEdge;
import org.uberfire.ext.wires.bpmn.api.model.BpmnGraph;
import org.uberfire.ext.wires.bpmn.api.model.BpmnGraphNode;
import org.uberfire.ext.wires.bpmn.api.model.rules.Rule;
import org.uberfire.ext.wires.bpmn.client.commands.Results;

/**
 * Rule Manager to report validation issues when attempting to mutate Elements
 */
public interface RuleManager {

    /**
     * Add a rule to the Rule Manager
     * @param rule
     */
    void addRule( final Rule rule );

    /**
     * Check whether adding the proposed Node to the target Process breaks any containment Rules
     * @param target Target process
     * @param candidate Candidate node
     * @return
     */
    Results checkContainment( final BpmnGraph target,
                              final BpmnGraphNode candidate );

    /**
     * Check whether adding the proposed Node to the target Process breaks any cardinality Rules
     * @param target Target process
     * @param candidate Candidate node
     * @param operation Is the candidate Node being added or removed
     * @return
     */
    Results checkCardinality( final BpmnGraph target,
                              final BpmnGraphNode candidate,
                              final Operation operation );

    /**
     * Check whether adding the proposed Edge to the target Process breaks any connection Rules
     * @param outgoingNode Node from which the Edge will emanate
     * @param incomingNode Node to which the Edge will terminate
     * @param edge Candidate edge
     * @return Is the Edge being added or removed
     */
    Results checkConnectionRules( final BpmnGraphNode outgoingNode,
                                  final BpmnGraphNode incomingNode,
                                  final BpmnEdge edge );

    /**
     * Check whether adding the proposed Edge to the target Process breaks any cardinality Rules
     * @param outgoingNode Node from which the Edge will emanate
     * @param incomingNode Node to which the Edge will terminate
     * @param edge Candidate edge
     * @param operation
     * @return Is the Edge being added or removed
     */
    Results checkCardinality( final BpmnGraphNode outgoingNode,
                              final BpmnGraphNode incomingNode,
                              final BpmnEdge edge,
                              final Operation operation );

    /**
     * Rules are applied against an unmodified Graph to check whether the proposed mutated state is valid.
     * This is deliberate to avoid, for example, costly "undo" operations if we were to mutate the state
     * first and then validate. An invalidate state would need to be reverted. If we decided to change
     * this we'd need to mutate the graph state first and then validate the whole graph.
     */
    public enum Operation {
        ADD,
        DELETE
    }

}
