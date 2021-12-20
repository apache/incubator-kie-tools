/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.shapes.client;

import javax.enterprise.context.Dependent;

import com.ait.lienzo.client.core.shape.Arrow;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.ArrowType;
import org.kie.workbench.common.stunner.client.lienzo.components.glyph.LienzoGlyphRenderer;
import org.kie.workbench.common.stunner.shapes.def.ConnectorGlyph;

@Dependent
public class ConnectorGlyphLienzoRenderer implements LienzoGlyphRenderer<ConnectorGlyph> {

    private static final double STROKE_SIZE = 2.5;

    @Override
    public Class<ConnectorGlyph> getGlyphType() {
        return ConnectorGlyph.class;
    }

    @Override
    public Group render(final ConnectorGlyph glyph,
                        final double width,
                        final double height) {
        final Group group = new Group();
        final Arrow arrow = new Arrow(new Point2D(STROKE_SIZE,
                                                  height),
                                      new Point2D(width,
                                                  STROKE_SIZE),
                                      5,
                                      10,
                                      45,
                                      45,
                                      ArrowType.AT_END)
                .setStrokeWidth(STROKE_SIZE)
                .setFillColor(glyph.getColor())
                .setStrokeColor(glyph.getColor())
                .setDraggable(true);
        group.add(arrow);
        scaleTo(group,
                width - arrow.getStrokeWidth(),
                height - arrow.getStrokeWidth());

        return group;
    }

    private void scaleTo(final Group group,
                         final double width,
                         final double height) {
        final BoundingBox bb = group.getBoundingBox();
        final double w = bb.getWidth();
        final double h = bb.getHeight();
        final double sw = w > 0 ? (width / w) : 1;
        final double sh = h > 0 ? (height / h) : 1;
        group.setScale(sw,
                       sh);
    }
}
