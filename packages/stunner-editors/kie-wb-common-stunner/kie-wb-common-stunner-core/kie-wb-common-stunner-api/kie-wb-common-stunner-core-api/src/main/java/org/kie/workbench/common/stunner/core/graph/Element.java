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

package org.kie.workbench.common.stunner.core.graph;

import java.util.Set;

/**
 * <p>This interface provides the basic contract for any element of a graph (node/edge).</p>
 * <p>
 * <p>The graph implementation is based on the <b>Labeled Property Graph Model</b>, so all graph elements
 * must have a unique identifier, a generic content, a collection of properties (based on the content, not always present) and a set of labels.
 * The content gives the graph the different processing semantics, or some view representation for the element on the canvas,</p>
 */
public interface Element<C> {

    String getUUID();

    Set<String> getLabels();

    C getContent();

    void setContent(final C content);

    Node<C, Edge> asNode();

    Edge<C, Node> asEdge();
}
