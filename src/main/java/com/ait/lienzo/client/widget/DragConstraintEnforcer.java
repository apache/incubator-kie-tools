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

package com.ait.lienzo.client.widget;

import com.ait.lienzo.client.core.types.Point2D;

/**
 * DragConstraintEnforcer can be used to restrict where a Node can be dragged to.
 *
 */
public interface DragConstraintEnforcer
{
    /**
     * Called when the drag operation starts.
     * 
     * @param dragContext
     */
    void startDrag(DragContext dragContext);

    /**
     * Adjust the drag offset (dx,dy)
     * 
     * @param dxy (dx,dy) specified in local coordinates, 
     *      i.e. in the parent node's coordinate system
     */
    boolean adjust(Point2D dxy);
}
