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

import java.util.Collection;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public interface GraphEvaluationState {

    Graph<?, ? extends Node> getGraph();

    CardinalityState getCardinalityState();

    ConnectorCardinalityState getConnectorCardinalityState();

    ConnectionState getConnectionState();

    ContainmentState getContainmentState();

    DockingState getDockingState();

    interface CardinalityState {

        Iterable<Node> nodes();
    }

    interface ConnectorCardinalityState {

        Collection<Edge<? extends View<?>, Node>> getIncoming(Node<? extends View<?>, Edge> node);

        Collection<Edge<? extends View<?>, Node>> getOutgoing(Node<? extends View<?>, Edge> node);
    }

    interface ConnectionState {

        Node<? extends View<?>, ? extends Edge> getSource(Edge<? extends View<?>, ? extends Node> edge);

        Node<? extends View<?>, ? extends Edge> getTarget(Edge<? extends View<?>, ? extends Node> edge);
    }

    interface ContainmentState {

        Element<? extends Definition<?>> getParent(Node<? extends Definition<?>, ? extends Edge> node);
    }

    interface DockingState {

        Element<? extends Definition<?>> getDockedTo(Node<? extends Definition<?>, ? extends Edge> node);
    }
}
