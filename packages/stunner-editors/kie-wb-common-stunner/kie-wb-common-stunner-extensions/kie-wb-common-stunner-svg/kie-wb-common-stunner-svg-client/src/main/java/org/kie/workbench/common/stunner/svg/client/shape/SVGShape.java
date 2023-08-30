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



package org.kie.workbench.common.stunner.svg.client.shape;

import org.kie.workbench.common.stunner.core.client.shape.Lifecycle;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.svg.annotation.SVGSource;
import org.kie.workbench.common.stunner.svg.annotation.SVGViewFactory;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;

/**
 * A shape type that handles view instnaces for SVGShapeView type or any of its subtypes.
 * <p/>
 * The SVGShapeView instances are usually generated at compile by parsing
 * and translating SVG images into the concrete view's domain.
 * @param <V> The SVGShapeView type.
 * @See {@link SVGViewFactory}
 * @See {@link SVGSource}
 * <p/>
 * An SVG shape that is not updated as with model updates.
 * This shape will always display the same svg view.
 */
public interface SVGShape<V extends SVGShapeView>
        extends
        Shape<V>,
        Lifecycle {

}
