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


package org.kie.workbench.common.stunner.svg.gen.translator.impl;

import org.kie.workbench.common.stunner.svg.gen.model.StyleDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.TransformDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.ViewRefDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.impl.CircleDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.impl.GroupDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.impl.MultiPathDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.impl.RectDefinition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Helper class for testing SVG translators.
 * It provides the assertions for all SVG attribute values for the elements in
 * the svg test-scoped files.
 */
public class SVGTranslationTestAssertions {

    public static final String SVG_TEST_PATH = "org/kie/workbench/common/stunner/svg/gen/svg-elements-test.svg";
    public static final String SVG_TEST_PATH_ERRORS = "org/kie/workbench/common/stunner/svg/gen/svg-elements-test-errors.svg";

    public static void assertPath(final MultiPathDefinition pathDefinition) {
        assertEquals("M150 0 L75 200 L225 200 Z",
                     pathDefinition.getPath());
        assertEquals("p1",
                     pathDefinition.getId());
        assertEquals(1d,
                     pathDefinition.getAlpha(),
                     0d);
        assertEquals(0d,
                     pathDefinition.getX(),
                     0d);
        assertEquals(0d,
                     pathDefinition.getY(),
                     0d);
        final TransformDefinition transformDefinition = pathDefinition.getTransformDefinition();
        assertTransformDef(transformDefinition,
                           0.25d,
                           0.25d,
                           10d,
                           10d);
        final StyleDefinition styleDefinition = pathDefinition.getStyleDefinition();
        assertStyleDef(styleDefinition,
                       1d,
                       "#ff0000",
                       1d,
                       "#0000ff",
                       1d,
                       2d);
    }

    public static void assertRectangle(final RectDefinition rectDefinition) {
        assertNotNull(rectDefinition);
        assertEquals("r1",
                     rectDefinition.getId());
        assertEquals(1d,
                     rectDefinition.getAlpha(),
                     0d);
        assertEquals(1d,
                     rectDefinition.getX(),
                     0d);
        assertEquals(2d,
                     rectDefinition.getY(),
                     0d);
        assertEquals(200d,
                     rectDefinition.getWidth(),
                     0d);
        assertEquals(230d,
                     rectDefinition.getHeight(),
                     0d);
        assertEquals(15d,
                     rectDefinition.getCornerRadius(),
                     0d);
        final TransformDefinition transformDefinition = rectDefinition.getTransformDefinition();
        assertTransformDef(transformDefinition,
                           0.5d,
                           0.5d,
                           25d,
                           30d);
        final StyleDefinition styleDefinition = rectDefinition.getStyleDefinition();
        assertStyleDef(styleDefinition,
                       1d,
                       "#0000ff",
                       1d,
                       "#000000",
                       1d,
                       3d);
    }

    public static void assertCircle(final CircleDefinition circleDefinition) {
        assertNotNull(circleDefinition);
        assertEquals("c1",
                     circleDefinition.getId());
        assertEquals(1d,
                     circleDefinition.getAlpha(),
                     0d);
        assertEquals(50d,
                     circleDefinition.getX(),
                     0d);
        assertEquals(51d,
                     circleDefinition.getY(),
                     0d);
        assertEquals(40d,
                     circleDefinition.getRadius(),
                     0d);
        final TransformDefinition transformDefinition = circleDefinition.getTransformDefinition();
        assertTransformDef(transformDefinition,
                           0.85d,
                           0.67d,
                           4.5d,
                           2.39d);
        final StyleDefinition styleDefinition = circleDefinition.getStyleDefinition();
        assertStyleDef(styleDefinition,
                       1d,
                       "#ff0000",
                       1d,
                       "#ffff00",
                       1d,
                       5d);
    }

    public static void assertGroupRef(final GroupDefinition groupDefinition) {
        assertNotNull(groupDefinition);
        assertEquals("g1",
                     groupDefinition.getId());
        assertEquals(0d,
                     groupDefinition.getAlpha(),
                     0d);
        assertEquals(77d,
                     groupDefinition.getX(),
                     0d);
        assertEquals(88d,
                     groupDefinition.getY(),
                     0d);
    }

    public static void assertViewRef(final ViewRefDefinition viewRefDefinition) {
        assertNotNull(viewRefDefinition);
        assertEquals("Layer_1",
                     viewRefDefinition.getViewRefId());
        assertEquals("another-svg.svg",
                     viewRefDefinition.getFilePath());
        assertEquals("g1",
                     viewRefDefinition.getParent());
    }

    public static void assertStyleDef(final StyleDefinition styleDefinition,
                                      final double alpha,
                                      final String fillColor,
                                      final double fillAlpha,
                                      final String strokeColor,
                                      final double strokeAlpha,
                                      final double strokeWidth) {
        final double a = styleDefinition.getAlpha();
        final String fc = styleDefinition.getFillColor();
        final double fa = styleDefinition.getFillAlpha();
        final String sc = styleDefinition.getStrokeColor();
        final double sa = styleDefinition.getStrokeAlpha();
        final double sw = styleDefinition.getStrokeWidth();
        assertEquals(alpha,
                     a,
                     0d);
        assertEquals(fillColor,
                     fc);
        assertEquals(fillAlpha,
                     fa,
                     0d);
        assertEquals(strokeColor,
                     sc);
        assertEquals(strokeAlpha,
                     sa,
                     0d);
        assertEquals(strokeWidth,
                     sw,
                     0d);
    }

    public static void assertTransformDef(final TransformDefinition transformDefinition,
                                          final double scaleX,
                                          final double scaleY,
                                          final double translateX,
                                          final double translateY) {
        final double sx = transformDefinition.getScaleX();
        final double sy = transformDefinition.getScaleY();
        final double tx = transformDefinition.getTranslateX();
        final double ty = transformDefinition.getTranslateY();
        assertEquals(scaleX,
                     sx,
                     0d);
        assertEquals(scaleY,
                     sy,
                     0d);
        assertEquals(translateX,
                     tx,
                     0d);
        assertEquals(translateY,
                     ty,
                     0d);
    }
}
