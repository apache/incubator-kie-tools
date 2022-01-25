/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.svg.client.shape;

import org.kie.workbench.common.stunner.core.client.shape.Lifecycle;
import org.kie.workbench.common.stunner.core.client.shape.NodeShape;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;

/**
 * A mutable Shape Definition type  for nodes that handles a
 * SVGShapeView type. or any of its subtypes.
 * <p/>
 * This type allows runtime updates and it can be composed
 * by other SVGShapeView instances.
 * <p/>
 * Once the SVG shape view instance has been built, this type
 * provides the binding between the definition and the view instance
 * for different attributes. The size for the view can be
 * changed at runtime as well.
 * @param <W> The definition type.
 * @param <V> The SVGShapeView type.
 */
public interface SVGMutableShape<W, V extends SVGShapeView>
        extends
        SVGShape<V>,
        NodeShape<W, View<W>, Node<View<W>, Edge>, V>,
        Lifecycle {

}
