/*
   Copyright (c) 2017 Ahome' Innovation Technologies. All rights reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.ait.lienzo.client.core.shape.wires.handlers;

import java.util.Collection;

import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import java.util.function.Supplier;

/**
 * A control that composites other wires controls, this way ot provides support for operating on
 * multiple wires objects, like shapes and connectors.
 * Eg: It can be used when selecting multiple shapes and/or connectors and performing actions
 * or for grouping capabilities.
 */
public interface WiresCompositeControl extends WiresMoveControl,
                                               WiresMouseControl,
                                               WiresControl,
                                               WiresBoundsConstraintControl {

    interface Context {

        Collection<WiresShape> getShapes();

        Collection<WiresConnector> getConnectors();

    }

    void useIndex(Supplier<WiresLayerIndex> index);

    boolean isAllowed();

    boolean accept();

    WiresContainer getSharedParent();

    Context getContext();

}
