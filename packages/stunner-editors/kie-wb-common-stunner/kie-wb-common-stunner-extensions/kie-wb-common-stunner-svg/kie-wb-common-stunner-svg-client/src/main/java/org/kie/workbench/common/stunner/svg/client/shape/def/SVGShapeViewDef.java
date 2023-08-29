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
import org.kie.workbench.common.stunner.core.definition.shape.ShapeViewDef;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;

/**
 * An SVG Shape Definition type that allows runtime updates
 * and can be composed by other SVG shape view instances.
 * <p/>
 * Once the SVG shape view instance has been built, this type
 * provides the binding between the definition and the view instance
 * for different attributes. The size for the view can be
 * changed at runtime as well.
 * @param <W> The bean type.
 */
public interface SVGShapeViewDef<W, F> extends SVGShapeDef<W, F>,
                                               ShapeViewDef<W, SVGShapeView> {

    @Override
    default Class<? extends ShapeDef> getType() {
        return SVGShapeViewDef.class;
    }
}
