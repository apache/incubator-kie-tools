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

import com.ait.lienzo.client.core.shape.MultiPath;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.svg.gen.model.impl.MultiPathDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.impl.StyleDefinitionImpl;
import org.kie.workbench.common.stunner.svg.gen.model.impl.TransformDefinitionImpl;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class MultiPathDefinitionGeneratorTest {

    private static final String ID = "path1";
    private static final String PATH = "path-value1";
    private static final double X = 1.5d;
    private static final double Y = 1232.9d;
    private static final double ALPHA = 0.7d;
    private static final String FILL_COLOR = "#123456";
    private static final double FILL_ALPHA = 0.45d;
    private static final String STROKE_COLOR = "#654321";
    private static final double STROKE_ALPHA = 0.12d;
    private static final double STROKE_WIDTH = 5.6d;
    private static final String STROKE_DASHARRAY = "5.00,5.00";
    private static final boolean LISTENING = false;
    private static final double VBOX_MIN_X = 12.6;
    private static final double VBOX_MIN_Y = 23.4;
    private static final double VBOX_WIDTH = 300;
    private static final double VBOX_HEIGHT = 321.86;

    private MultiPathDefinitionGenerator tested;

    @Before
    public void setup() {
        tested = new MultiPathDefinitionGenerator();
    }

    @Test
    public void testGetters() {
        assertEquals(MultiPathDefinition.class,
                     tested.getDefinitionType());
        assertEquals("MultiPath",
                     tested.getTemplatePath());
    }

    @Test
    public void testGenerate() throws Exception {
        final MultiPathDefinition definition = new MultiPathDefinition(ID,
                                                                       PATH);
        definition.setX(X);
        definition.setY(Y);
        definition.setAlpha(ALPHA);
        definition.setStyleDefinition(new StyleDefinitionImpl.Builder()
                                              .setAlpha(ALPHA)
                                              .setFillColor(FILL_COLOR)
                                              .setFillAlpha(FILL_ALPHA)
                                              .setStrokeColor(STROKE_COLOR)
                                              .setStrokeAlpha(STROKE_ALPHA)
                                              .setStrokeWidth(STROKE_WIDTH)
                                              .setStrokeDashArray(STROKE_DASHARRAY)
                                              .build());
        definition.setTransformDefinition(new TransformDefinitionImpl(VBOX_WIDTH,
                                                                      VBOX_HEIGHT,
                                                                      VBOX_MIN_X,
                                                                      VBOX_MIN_Y));
        final String generated = tested.generate(definition).toString();
        assertTrue(generated.contains("new " + MultiPath.class.getName() + "(" + SVGViewFactoryGenerator.FIELD_STATICS + ID.toUpperCase() + ")"));
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
        GeneratorAssertions.assertStrokeDashArray(generated,
                                                  STROKE_DASHARRAY);
        GeneratorAssertions.assertListening(generated,
                                            LISTENING);
    }
}
