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


package org.kie.workbench.common.stunner.client.lienzo.components.glyph;

import java.util.function.Consumer;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.shared.core.types.ColorName;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;

import static org.kie.workbench.common.stunner.client.lienzo.util.LienzoShapeUtils.scale;

public abstract class AbstractLienzoShapeGlyphRenderer<G extends Glyph, S extends Shape>
        implements LienzoGlyphRenderer<G> {

    protected abstract void getShape(final G glyph,
                                     final double width,
                                     final double height,
                                     final Consumer<S> shapeConsumer);

    @Override
    public Group render(final G glyph,
                        final double width,
                        final double height) {
        final Group group = new Group();
        final Rectangle decorator =
                new Rectangle(width,
                              height)
                        .setCornerRadius(5)
                        .setFillColor(ColorName.LIGHTGREY)
                        .setFillAlpha(0.7d);
        group.add(decorator);
        getShape(glyph,
                 width,
                 height,
                 shape -> {
                     group.add(shape);
                     scaleShape(shape,
                                width,
                                height);
                     group.remove(decorator);
                 });
        return group;
    }

    protected void scaleShape(final S shape,
                              final double width,
                              final double height) {
        scale(shape,
              width,
              height);
    }
}
