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
package org.kie.workbench.common.dmn.backend.definition.v1_1.dd;

import org.junit.Test;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bound;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class PointUtilsTest {

    @Test
    public void testConvertToAbsoluteBoundsWhenChild() {
        final Node<View, Edge> parent = new NodeImpl<>(UUID.uuid());
        final View parentView = new ViewImpl<>(new Decision(), Bounds.create(100, 200, 1000, 2000));
        parent.setContent(parentView);

        final Node<View, Edge> child = new NodeImpl<>(UUID.uuid());
        final View childView = new ViewImpl<>(new Decision(), Bounds.create(10, 20, 50, 60));
        child.setContent(childView);

        final Edge<Child, Node> edge = new EdgeImpl<>(UUID.uuid());
        edge.setContent(new Child());
        edge.setSourceNode(parent);
        edge.setTargetNode(child);
        parent.getOutEdges().add(edge);
        child.getInEdges().add(edge);

        PointUtils.convertToAbsoluteBounds(child);

        final Bound ulBound = child.getContent().getBounds().getUpperLeft();
        final Bound lrBound = child.getContent().getBounds().getLowerRight();
        assertThat(ulBound.getX()).isEqualTo(110);
        assertThat(ulBound.getY()).isEqualTo(220);
        assertThat(lrBound.getX()).isEqualTo(150);
        assertThat(lrBound.getY()).isEqualTo(260);
    }

    @Test
    public void testConvertToAbsoluteBoundsWhenNotChild() {
        final Node<View, ?> node = new NodeImpl<>(UUID.uuid());
        final View nodeView = new ViewImpl<>(new Decision(), Bounds.create(10, 20, 50, 60));
        node.setContent(nodeView);

        PointUtils.convertToAbsoluteBounds(node);

        final Bound ulBound = node.getContent().getBounds().getUpperLeft();
        final Bound lrBound = node.getContent().getBounds().getLowerRight();
        assertThat(ulBound.getX()).isEqualTo(10);
        assertThat(ulBound.getY()).isEqualTo(20);
        assertThat(lrBound.getX()).isEqualTo(50);
        assertThat(lrBound.getY()).isEqualTo(60);
    }

    @Test
    public void testConvertToRelativeBoundsWhenChild() {
        final Node<View, Edge> parent = new NodeImpl<>(UUID.uuid());
        final View parentView = new ViewImpl<>(new Decision(), Bounds.create(100, 200, 1000, 2000));
        parent.setContent(parentView);

        final Node<View, Edge> child = new NodeImpl<>(UUID.uuid());
        final View childView = new ViewImpl<>(new Decision(), Bounds.create(110, 220, 150, 260));
        child.setContent(childView);

        final Edge<Child, Node> edge = new EdgeImpl<>(UUID.uuid());
        edge.setContent(new Child());
        edge.setSourceNode(parent);
        edge.setTargetNode(child);
        parent.getOutEdges().add(edge);
        child.getInEdges().add(edge);

        PointUtils.convertToRelativeBounds(child);

        final Bound ulBound = child.getContent().getBounds().getUpperLeft();
        final Bound lrBound = child.getContent().getBounds().getLowerRight();
        assertThat(ulBound.getX()).isEqualTo(10);
        assertThat(ulBound.getY()).isEqualTo(20);
        assertThat(lrBound.getX()).isEqualTo(50);
        assertThat(lrBound.getY()).isEqualTo(60);
    }

    @Test
    public void testConvertToRelativeBoundsWhenNotChild() {
        final Node<View, ?> node = new NodeImpl<>(UUID.uuid());
        final View nodeView = new ViewImpl<>(new Decision(), Bounds.create(10, 20, 50, 60));
        node.setContent(nodeView);

        PointUtils.convertToRelativeBounds(node);

        final Bound ulBound = node.getContent().getBounds().getUpperLeft();
        final Bound lrBound = node.getContent().getBounds().getLowerRight();
        assertThat(ulBound.getX()).isEqualTo(10);
        assertThat(ulBound.getY()).isEqualTo(20);
        assertThat(lrBound.getX()).isEqualTo(50);
        assertThat(lrBound.getY()).isEqualTo(60);
    }
}
