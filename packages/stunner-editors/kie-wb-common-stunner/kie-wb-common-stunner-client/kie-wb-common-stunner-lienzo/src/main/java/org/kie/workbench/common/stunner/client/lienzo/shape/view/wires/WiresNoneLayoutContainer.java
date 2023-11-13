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


package org.kie.workbench.common.stunner.client.lienzo.shape.view.wires;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.wires.LayoutContainer;
import com.ait.lienzo.client.core.types.Point2D;

public class WiresNoneLayoutContainer implements LayoutContainer {

    private final Group group;

    public WiresNoneLayoutContainer() {
        this.group = new Group().setDraggable(false);
    }

    @Override
    public LayoutContainer setOffset(final Point2D offset) {
        return this;
    }

    @Override
    public LayoutContainer setSize(final double width,
                                   final double height) {
        return this;
    }

    @Override
    public LayoutContainer add(final IPrimitive<?> child) {
        group.add(child);
        return this;
    }

    @Override
    public LayoutContainer add(final IPrimitive<?> child,
                               final Layout layout) {
        group.add(child);
        return this;
    }

    @Override
    public LayoutContainer remove(final IPrimitive<?> child) {
        group.remove(child);
        return this;
    }

    @Override
    public LayoutContainer execute() {
        return this;
    }

    @Override
    public LayoutContainer refresh() {
        return this;
    }

    @Override
    public Group getGroup() {
        return group;
    }

    @Override
    public void destroy() {
        group.removeFromParent();
    }
}
