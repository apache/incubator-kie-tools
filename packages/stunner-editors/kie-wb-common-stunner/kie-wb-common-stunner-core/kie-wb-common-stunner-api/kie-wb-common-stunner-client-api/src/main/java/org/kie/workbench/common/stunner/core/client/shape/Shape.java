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


package org.kie.workbench.common.stunner.core.client.shape;

import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;

/**
 * A Shape type provides the bride between the canvas handler and the shape view.
 * <p>
 * It should not contain any specific view code, so this way the same Shape can handle different views.
 * This type of shape is not mutable by default, so the shape attributes are not changed as per
 * model updates. Once it gets rendered, it will not change anymore, rather than
 * if any of the ShapeStates has some visual feedback.
 * @param <V> The Shape View type.
 */
public interface Shape<V extends ShapeView> extends HasShapeState {

    /**
     * Sets a unique identifier for the shape in a canvas.
     */
    void setUUID(final String uuid);

    /**
     * The unique identifier for the shape in a canvas.
     */
    String getUUID();

    /**
     * Returns the view representation on the canvas for the shape.
     */
    V getShapeView();
}
