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


package org.kie.workbench.common.stunner.core.client.shape.impl;

import java.util.LinkedList;
import java.util.List;

import org.kie.workbench.common.stunner.core.client.shape.HasChildren;
import org.kie.workbench.common.stunner.core.client.shape.Shape;

public abstract class AbstractContainerShape<V extends HasChildren, S extends Shape<?>>
        implements
        HasChildren<S> {

    private final List<S> children = new LinkedList<S>();

    protected abstract V getCompositeShapeView();

    @Override
    @SuppressWarnings("unchecked")
    public void addChild(final S child,
                         final Layout layout) {
        final V view = getCompositeShapeView();
        children.add(child);
        view.addChild(child.getShapeView(),
                      layout);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void removeChild(final S child) {
        final V view = getCompositeShapeView();
        children.remove(child);
        view.removeChild(child.getShapeView());
    }

    @Override
    public Iterable<S> getChildren() {
        return children;
    }
}
