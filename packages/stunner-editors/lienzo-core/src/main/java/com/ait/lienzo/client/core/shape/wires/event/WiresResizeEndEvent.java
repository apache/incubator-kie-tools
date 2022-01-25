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

package com.ait.lienzo.client.core.shape.wires.event;

import com.ait.lienzo.client.core.event.NodeDragEndHandler;
import elemental2.dom.HTMLElement;

/**
 * <p>Event that is fired when the drag ends ( drag produced by one of resize control points for a wires shape ).</p>
 */
public class WiresResizeEndEvent extends AbstractWiresResizeEvent<WiresResizeEndHandler, NodeDragEndHandler> {

    public static final Type<WiresResizeEndHandler> TYPE = new Type<>();

    public WiresResizeEndEvent(final HTMLElement relativeElement) {
        super(relativeElement);
    }

    @Override
    public Type<WiresResizeEndHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    public void dispatch(final WiresResizeEndHandler shapeMovedHandler) {
        shapeMovedHandler.onShapeResizeEnd(this);
    }
}
