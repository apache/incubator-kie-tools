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


package org.kie.workbench.common.stunner.client.lienzo.util;

import com.ait.lienzo.client.core.shape.wires.IControlHandle;
import com.ait.lienzo.client.core.shape.wires.IControlHandleList;

/**
 * This class provides a helper method to limit s the number of visible control points to use when resizing by making only
 * visible the lower right CP. See next comments to understand the reason for this.
 * <p/>
 * Right now all wires shape resize on lienzo can be done by 4 CPs, as example see next rectangle shape:
 * 0----1
 * |    |
 * 3----2
 * Consider as well:
 * - Once CPs for resize are enabled & visible, they allow dragging any of the four points to achieve
 * resizing, as each of the shape's multi path parts are updated.
 * - Stunner considers the CP #0 as the shape coordinates.
 * - Stunner uses the CP #2 in order to obtain the shape bounds.
 * Now imagine dragging the CP #3 two units to the left - what lienzo internal logic does
 * is re-calculate the shape path parts to achieve the new bounds, but it keeps the shape parent group's
 * coordinates using CP #0. So it results in something like:
 * +-0----1
 * |      |
 * +-3----2
 * As you can see, the shape path parts will be rendered for the right "size" but the shape group's
 * coordinates will remain as last CP #0 position. So at this point the CP #0 X coordinate is lower than 0.
 * <p/>
 * Stunner expects that the shape group's coordinates would be always relative to the top left
 * CP for the shape, as other process modelers expect for rendering the BPMN DI.
 * So currently stunner only shows the CP #2 for resizing goals to ensure the shape group's coordinates
 * are not being updated.
 * <p/>
 * TODO: Review this resize behavior on lienzo side, once clarified this probably can be removed.
 * TODO: Keep in mind this only applies with current lienzo release, as it always considers 4 resize
 * control points available for all shapes.
 */
public class ShapeControlPointsHelper {

    public static void showOnlyLowerRightCP(final IControlHandleList controlHandles) {
        controlHandles.show();
        hideCP(controlHandles.getHandle(0));
        hideCP(controlHandles.getHandle(1));
        hideCP(controlHandles.getHandle(3));
    }

    public static void hideCP(final IControlHandle handle) {
        if (null != handle && null != handle.getControl()) {
            handle.getControl().setVisible(false);
        }
    }
}
