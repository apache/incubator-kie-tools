/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.components.toolbox.builder;

import org.kie.workbench.common.stunner.core.client.canvas.Layer;
import org.kie.workbench.common.stunner.core.client.components.toolbox.Toolbox;
import org.kie.workbench.common.stunner.core.client.components.toolbox.ToolboxButton;
import org.kie.workbench.common.stunner.core.client.components.toolbox.ToolboxButtonGrid;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;

public interface ToolboxBuilder<T, G extends ToolboxButtonGrid, V> {

    enum Direction {
        NORTH,
        SOUTH,
        EAST,
        WEST,
        NONE,
        NORTH_EAST,
        SOUTH_EAST,
        SOUTH_WEST,
        NORTH_WEST;
    }

    T forLayer( Layer layer );

    T forView( ShapeView<?> view );

    T direction( Direction on, Direction towards );

    T grid( G grid );

    T add( ToolboxButton<V> button );

    Toolbox build();

}
