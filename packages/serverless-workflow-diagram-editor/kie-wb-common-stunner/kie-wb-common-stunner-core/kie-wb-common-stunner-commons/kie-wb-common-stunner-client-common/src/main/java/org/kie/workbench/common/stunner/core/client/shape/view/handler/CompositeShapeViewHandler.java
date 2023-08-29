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


package org.kie.workbench.common.stunner.core.client.shape.view.handler;

import java.util.Collection;
import java.util.LinkedList;

import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeViewHandler;

public class CompositeShapeViewHandler<W, V extends ShapeView>
        implements ShapeViewHandler<W, V> {

    private final Collection<ShapeViewHandler<W, V>> handlers;

    public CompositeShapeViewHandler() {
        this.handlers = new LinkedList<>();
    }

    public CompositeShapeViewHandler register(final ShapeViewHandler<W, V> handler) {
        handlers.add(handler);
        return this;
    }

    @Override
    public void handle(final W object,
                       final V view) {
        handlers.forEach(h -> h.handle(object, view));
    }
}
