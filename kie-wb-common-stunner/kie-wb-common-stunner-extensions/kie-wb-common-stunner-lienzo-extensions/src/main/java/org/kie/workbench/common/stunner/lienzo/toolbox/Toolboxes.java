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

package org.kie.workbench.common.stunner.lienzo.toolbox;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import org.kie.workbench.common.stunner.lienzo.toolbox.builder.On;

public class Toolboxes {

    public static On hoverToolBoxFor(final Layer layer,
                                     final WiresShape shape) {
        return new HoverToolbox.HoverToolboxBuilder(layer,
                                                    shape);
    }

    public static On staticToolBoxFor(final Layer layer,
                                      final WiresShape shape) {
        return new StaticToolbox.StaticToolboxBuilder(layer,
                                                      shape);
    }
}
