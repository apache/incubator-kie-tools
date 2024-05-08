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

package com.ait.lienzo.client.core.shape.wires.handlers;

import java.util.Set;

import com.ait.lienzo.client.core.shape.wires.AlignAndDistribute;
import com.ait.lienzo.client.core.types.Point2D;

/**
 * The Align and Distribute control handler provides user interaction common functions/logic in a way that they're decoupled
 * from the concrete event types fired, and these calls be reused programatically as well. So common logic
 * can be shared to provide drag and the operations support and attached to the necessary event handler type.
 */
public interface AlignAndDistributeControl {

    void refresh();

    void refresh(boolean transforms, boolean attributes);

    void reset();

    void dragStart();

    void dragEnd();

    boolean dragAdjust(Point2D dxy);

    void remove();

    Set<AlignAndDistribute.DistributionEntry> getHorizontalDistributionEntries();

    Set<AlignAndDistribute.DistributionEntry> getVerticalDistributionEntries();

    double getLeft();

    double getRight();

    double getTop();

    double getBottom();

    double getHorizontalCenter();

    double getVerticalCenter();

    boolean isIndexed();

    void setIndexed(boolean indexed);

    void updateIndex();
}
