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

package org.kie.workbench.common.dmn.client.shape.view.decisionservice;

import java.util.function.Supplier;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.shape.Line;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.ColorName;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGPrimitiveShape;
import org.kie.workbench.common.stunner.svg.client.shape.view.impl.SVGPrimitiveFactory;

public class DecisionServiceDividerLine extends Line {

    private final Supplier<Double> dynamicWidthSupplier;

    public DecisionServiceDividerLine(final Supplier<Double> dynamicWidthSupplier) {
        super(new Point2D(0, 0), new Point2D(0, 0));
        this.dynamicWidthSupplier = dynamicWidthSupplier;

        setStrokeWidth(1.5);
        setStrokeColor(ColorName.BLACK);
        setLocation(new Point2D(0, 0));
    }

    SVGPrimitiveShape asSVGPrimitiveShape() {
        final SVGPrimitiveShape divider = SVGPrimitiveFactory.newSVGPrimitiveShape(this, false, null);
        divider.setDragEnabled(true);
        return divider;
    }

    @Override
    protected boolean prepare(final Context2D context,
                              final double alpha) {
        context.beginPath();
        context.moveTo(0.0, 0.0);
        context.lineTo(dynamicWidthSupplier.get(), 0.0);

        return true;
    }
}
