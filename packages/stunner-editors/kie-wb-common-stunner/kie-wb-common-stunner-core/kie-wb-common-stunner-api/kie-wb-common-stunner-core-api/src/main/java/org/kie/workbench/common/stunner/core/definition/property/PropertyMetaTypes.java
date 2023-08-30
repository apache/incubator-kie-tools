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


package org.kie.workbench.common.stunner.core.definition.property;

/**
 * It gives semantics to model definition properties.for different Stunner behaviors.
 * Stunner provides some built-in features that could require model updates,
 * so this meta-property values are used for this bindings.
 */
public enum PropertyMetaTypes {
    /**
     * No semantics.
     */
    NONE,
    /**
     * Use it for the Definition's property used as name for this bean.
     */
    NAME,
    /**
     * Whatever shape is being representing some Definition bean instance, Stunner considers
     * the size for the shape as the area of its bounding box.
     * These properties indicate the width and height (in pixels) of the bounding box for any shape
     * which represents the bean.
     * If the shape supports resize and it is resized on client side, these properties will be
     * updated with the values for the new bounding box area.
     */
    WIDTH,
    HEIGHT,
    /**
     * Whatever shape is being representing some Definition bean instance, Stunner considers
     * the size for the shape as the area of its bounding box.
     * If the shape supports resize and it is resized on client side, these properties will be
     * updated with the radius value of an arc that produces the new bounding box size.
     */
    RADIUS,
    ID
}
