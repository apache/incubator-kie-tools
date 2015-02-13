/*
 * Copyright 2015 JBoss Inc
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

import org.uberfire.ext.wires.bpmn.api.model.Content;
import org.uberfire.ext.wires.bpmn.api.model.rules.Rule;
import org.uberfire.ext.wires.bpmn.beliefs.graph.Graph;
import org.uberfire.ext.wires.bpmn.beliefs.graph.GraphNode;
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
     * @param proposed Proposed node
     * @return
     */
    Results checkContainment( final Graph<Content> target,
                              final GraphNode<Content> proposed );

    /**
     * Check whether adding the proposed Node to the target Process breaks any cardinality Rules
     * @param target Target process
     * @param proposed Proposed node
     * @return
     */
    Results checkCardinality( final Graph<Content> target,
                              final GraphNode<Content> proposed );

}
