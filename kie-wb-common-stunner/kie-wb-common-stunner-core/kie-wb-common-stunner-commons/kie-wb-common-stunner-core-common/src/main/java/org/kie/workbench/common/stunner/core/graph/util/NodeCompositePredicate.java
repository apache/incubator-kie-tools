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

package org.kie.workbench.common.stunner.core.graph.util;

import java.util.function.BiPredicate;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

class NodeCompositePredicate implements BiPredicate<Node<? extends View<?>, ? extends Edge>, Node<? extends View<?>, ? extends Edge>> {

    private BiPredicate predicateA;
    private BiPredicate predicateB;

    public NodeCompositePredicate(BiPredicate<Node<? extends View<?>, ? extends Edge>, Node<? extends View<?>, ? extends Edge>> predicateA,
                                  BiPredicate<Node<? extends View<?>, ? extends Edge>, Node<? extends View<?>, ? extends Edge>> predicateB) {
        this.predicateA = predicateA;
        this.predicateB = predicateB;
    }

    @Override
    public boolean test(Node<? extends View<?>, ? extends Edge> node, Node<? extends View<?>, ? extends Edge> node2) {
        return predicateA.test(node, node2) && predicateB.test(node, node2);
    }
}
