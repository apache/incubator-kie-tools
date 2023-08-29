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


package org.kie.workbench.common.stunner.svg.client.shape.def;

import org.kie.workbench.common.stunner.core.definition.shape.ShapeDef;
import org.kie.workbench.common.stunner.svg.annotation.SVGSource;
import org.kie.workbench.common.stunner.svg.annotation.SVGViewFactory;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;

/**
 * A Shape Definition type that handles SVGShapeView types.
 * <p/>
 * The SVGShapeView instances are usually generated at compile by parsing
 * and translating SVG images into the concrete view's domain.
 * @param <W> The bean type.
 * @See {@link SVGViewFactory}
 * @See {@link SVGSource}
 * <p/>
 * This shape definition type provides the binding between
 * a runtime definition and an SVGShapeView instance.
 */
public interface SVGShapeDef<W, F> extends ShapeDef<W> {

    /**
     * Returns a factory bean type for building the view instance.
     * @return A view factory type.
     */
    Class<F> getViewFactoryType();

    /**
     * Builds a new SVGShapeView instance for the <code>element</code> Definition
     * using the <code>factory</code>.
     * @param factory The view fctory instance.
     * @param element The definition instance.
     * @return An SVGShapeView instance for the given Definition instance.
     */
    SVGShapeView<?> newViewInstance(F factory,
                                    W element);

    @Override
    default Class<? extends ShapeDef> getType() {
        return SVGShapeDef.class;
    }
}
