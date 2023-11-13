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


package org.kie.workbench.common.stunner.client.lienzo.shape.view;

import java.util.List;

import com.ait.lienzo.client.core.animation.IAnimationHandle;
import com.ait.lienzo.client.core.shape.Node;

public class MultipleAnimationHandle implements IAnimationHandle {

    private final List<IAnimationHandle> handles;

    public MultipleAnimationHandle(final List<IAnimationHandle> handles) {
        this.handles = handles;
    }

    @Override
    public IAnimationHandle run() {
        handles.forEach(IAnimationHandle::run);
        return this;
    }

    @Override
    public IAnimationHandle stop() {
        handles.forEach(IAnimationHandle::stop);
        return this;
    }

    @Override
    public Node<?> getNode() {
        throw new UnsupportedOperationException("This is a composite animation handle. " +
                                                        "It does not target any specific node, use it just for " +
                                                        "orchestrating multiple animations.");
    }

    @Override
    public boolean isRunning() {
        return handles.stream()
                .filter(IAnimationHandle::isRunning)
                .findAny()
                .isPresent();
    }
}
