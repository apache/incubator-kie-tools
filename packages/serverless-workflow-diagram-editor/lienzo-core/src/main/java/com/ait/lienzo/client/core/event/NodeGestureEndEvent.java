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

package com.ait.lienzo.client.core.event;

import elemental2.dom.HTMLElement;

public class NodeGestureEndEvent extends AbstractNodeGestureEvent<NodeGestureEndHandler> {

    private static final Type<NodeGestureEndHandler> TYPE = new Type<>();

    public static final Type<NodeGestureEndHandler> getType() {
        return TYPE;
    }

    public NodeGestureEndEvent(final HTMLElement relativeElement) {
        super(relativeElement);
    }

    @Override
    public final Type<NodeGestureEndHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    public void dispatch(final NodeGestureEndHandler handler) {
        handler.onNodeGestureEnd(this);
    }
}
