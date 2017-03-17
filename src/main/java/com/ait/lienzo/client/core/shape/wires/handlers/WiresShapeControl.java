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

/**
 * Shape control handler provides user interaction common functions/logic in a way that they're decoupled
 * from the concrete event types fired, and these calls be reused programatically as well.
 *
 * E.g. when a wires shape is focused some common functions are executed, but this operations are agnostic whether
 * if the event is a MouseClickEvent used on a desktop platform or a TouchEvent used on mobile platforms - in both
 * situations only a certain coordinates are mandatory to execute the function.
 *
 * The default event handlers used on wires shape registrations delegate to this control, so developers
 * can create custom shape controls and provide the instances by using the
 * <code>com.ait.lienzo.client.core.shape.wires.handlers.WiresControlFactory</code> and provide custom
 * user interaction behaviours rather than defaults.
 *
 */
public interface WiresShapeControl extends HasDragControl, HasMouseFocusControl
{

    void setAlignAndDistributeControl( AlignAndDistributeControl alignAndDistributeHandler);

    void setDockingAndContainmentControl( WiresDockingAndContainmentControl m_dockingAndContainmentControl );

}
