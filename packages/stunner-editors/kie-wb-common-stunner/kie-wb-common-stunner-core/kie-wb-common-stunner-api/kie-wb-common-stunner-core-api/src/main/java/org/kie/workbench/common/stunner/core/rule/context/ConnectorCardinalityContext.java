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


package org.kie.workbench.common.stunner.core.rule.context;

import java.util.Optional;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

/**
 * This rule evaluation context provides the runtime information
 * that allows the evaluation for cardinality operations about
 * edges or connectors.
 */
public interface ConnectorCardinalityContext extends GraphEvaluationContext {

    /**
     * The connector's graph element.
     */
    Edge<? extends View<?>, Node> getEdge();

    /**
     * The candidate element to add or remove from the graph.
     * If not candidate present, it checks the cardinality
     * for the whole graph elements.
     */
    Element<? extends View<?>> getCandidate();

    /**
     * The operation to be performed on the candidate.
     * If not candidate present, the operation value is
     * discarded although being present.
     */
    Optional<CardinalityContext.Operation> getOperation();

    /**
     * The direction.
     */
    EdgeCardinalityContext.Direction getDirection();
}
