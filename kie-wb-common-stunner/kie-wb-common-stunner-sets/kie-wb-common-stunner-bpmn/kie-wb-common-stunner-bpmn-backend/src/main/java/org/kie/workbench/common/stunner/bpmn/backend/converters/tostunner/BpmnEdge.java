/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner;

import java.util.List;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public interface BpmnEdge {

    static BpmnEdge.Simple of(
            Edge<? extends View<?>, Node> edge,
            BpmnNode source, Connection sourceConnection,
            List<Point2D> controlPoints,
            BpmnNode target, Connection targetConnection) {
        return new BpmnEdge.Simple(edge, source, sourceConnection, controlPoints, target, targetConnection);
    }

    static BpmnEdge.Docked docked(BpmnNode source, BpmnNode target) {
        return new Docked(source, target);
    }

    BpmnNode getSource();

    BpmnNode getTarget();

    class Simple implements BpmnEdge {

        private final Edge<? extends View<?>, Node> edge;
        private final BpmnNode source;
        private final Connection sourceConnection;
        private final List<Point2D> controlPoints;
        private final BpmnNode target;
        private final Connection targetConnection;

        private Simple(Edge<? extends View<?>, Node> edge, BpmnNode source, Connection sourceConnection, List<Point2D> controlPoints, BpmnNode target, Connection targetConnection) {
            this.edge = edge;
            this.source = source;
            this.sourceConnection = sourceConnection;
            this.controlPoints = controlPoints;
            this.target = target;
            this.targetConnection = targetConnection;
        }

        public Edge<? extends View<?>, Node> getEdge() {
            return edge;
        }

        public BpmnNode getSource() {
            return source;
        }

        public Connection getSourceConnection() {
            return sourceConnection;
        }

        public List<Point2D> getControlPoints() {
            return controlPoints;
        }

        public BpmnNode getTarget() {
            return target;
        }

        public Connection getTargetConnection() {
            return targetConnection;
        }
    }

    class Docked implements BpmnEdge {

        private final BpmnNode source;
        private final BpmnNode target;

        private Docked(BpmnNode source, BpmnNode target) {
            this.source = source;
            this.target = target;
        }

        @Override
        public BpmnNode getSource() {
            return source;
        }

        @Override
        public BpmnNode getTarget() {
            return target;
        }
    }
}
