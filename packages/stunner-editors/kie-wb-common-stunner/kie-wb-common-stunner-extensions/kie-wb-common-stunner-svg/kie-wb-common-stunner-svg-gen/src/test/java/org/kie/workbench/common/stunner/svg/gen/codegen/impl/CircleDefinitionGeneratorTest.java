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

package org.kie.workbench.common.stunner.svg.gen.codegen.impl;

import com.ait.lienzo.client.core.shape.Circle;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.svg.gen.model.impl.CircleDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.impl.StyleDefinitionImpl;
import org.kie.workbench.common.stunner.svg.gen.model.impl.TransformDefinitionImpl;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class CircleDefinitionGeneratorTest {

    private static final String ID = "circle1";
    private static final double X = 1.5d;
    private static final double Y = 1232.9d;
    private static final double RADIUS = 25.5d;
    private static final double ALPHA = 0.7d;
    private static final String FILL_COLOR = "#123456";
    private static final double FILL_ALPHA = 0.45d;
    private static final String STROKE_COLOR = "#654321";
    private static final double STROKE_ALPHA = 0.12d;
    private static final double STROKE_WIDTH = 5.6d;
    private static final boolean LISTENING = false;
    private static final double VBOX_MIN_X = 12.6;
    private static final double VBOX_MIN_Y = 23.4;
    private static final double VBOX_WIDTH = 300;
    private static final double VBOX_HEIGHT = 321.86;

    private CircleDefinitionGenerator tested;

    @Before
    public void setup() throws Exception {
        tested = new CircleDefinitionGenerator();
    }

    @Test
    public void testGetters() throws Exception {
        assertEquals(CircleDefinition.class,
                     tested.getDefinitionType());
        assertEquals("Circle",
                     tested.getTemplatePath());
    }

    @Test
    public void testGenerate() throws Exception {
        final CircleDefinition circleDefinition = new CircleDefinition(ID,
                                                                       RADIUS);
        circleDefinition.setX(X);
        circleDefinition.setY(Y);
        circleDefinition.setAlpha(ALPHA);
        circleDefinition.setStyleDefinition(new StyleDefinitionImpl.Builder()
                                                    .setAlpha(ALPHA)
                                                    .setFillColor(FILL_COLOR)
                                                    .setFillAlpha(FILL_ALPHA)
                                                    .setStrokeColor(STROKE_COLOR)
                                                    .setStrokeAlpha(STROKE_ALPHA)
                                                    .setStrokeWidth(STROKE_WIDTH)
                                                    .build());
        circleDefinition.setTransformDefinition(new TransformDefinitionImpl(VBOX_WIDTH,
                                                                            VBOX_HEIGHT,
                                                                            VBOX_MIN_X,
                                                                            VBOX_MIN_Y));
        final String generated = tested.generate(circleDefinition).toString();
        assertTrue(generated.contains("new " + Circle.class.getName() + "(" + GeneratorAssertions.formatDouble(RADIUS) + ")"));
        GeneratorAssertions.assertDraggable(generated);
        GeneratorAssertions.assertID(generated,
                                     ID);
        GeneratorAssertions.assertX(generated,
                                    X);
        GeneratorAssertions.assertY(generated,
                                    Y);
        GeneratorAssertions.assertAlpha(generated,
                                        ALPHA);
        GeneratorAssertions.assertScale(generated,
                                        VBOX_WIDTH,
                                        VBOX_HEIGHT);
        GeneratorAssertions.assertFillColor(generated,
                                            FILL_COLOR);
        GeneratorAssertions.assertFillAlpha(generated,
                                            FILL_ALPHA);
        GeneratorAssertions.assertStrokeColor(generated,
                                              STROKE_COLOR);
        GeneratorAssertions.assertStrokeAlpha(generated,
                                              STROKE_ALPHA);
        GeneratorAssertions.assertStrokeWidth(generated,
                                              STROKE_WIDTH);
        GeneratorAssertions.assertListening(generated,
                                            LISTENING);
    }
}
