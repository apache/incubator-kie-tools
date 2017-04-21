/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.svg.gen.codegen.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.svg.gen.model.ViewFactory;
import org.kie.workbench.common.stunner.svg.gen.model.impl.CircleDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.impl.RectDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.impl.ViewBoxDefinitionImpl;
import org.kie.workbench.common.stunner.svg.gen.model.impl.ViewDefinitionImpl;
import org.kie.workbench.common.stunner.svg.gen.model.impl.ViewFactoryImpl;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class SVGViewDefinitionGeneratorTest {

    private static final String ID = "viewDef1";
    private static final double X = 1.5d;
    private static final double Y = 1232.9d;
    private static final double WIDTH = 25.5d;
    private static final double HEIGHT = 225.45d;
    private static final double VBOX_MIN_X = 12.6;
    private static final double VBOX_MIN_Y = 23.4;
    private static final double VBOX_WIDTH = 300;
    private static final double VBOX_HEIGHT = 321.86;

    private SVGViewDefinitionGenerator tested;

    @Before
    public void setup() throws Exception {
        tested = new SVGViewDefinitionGenerator();
    }

    @Test
    public void testGenerate() throws Exception {
        final ViewFactory viewFactory = new ViewFactoryImpl("view1",
                                                            "org.kie.test",
                                                            "MyType.¢lass");
        final RectDefinition mainDef = new RectDefinition("rect1",
                                                          1,
                                                          2,
                                                          0);
        final CircleDefinition circleDefinition = new CircleDefinition("circle1",
                                                                       25);
        final ViewDefinitionImpl viewDefinition =
                new ViewDefinitionImpl(ID,
                                       X,
                                       Y,
                                       WIDTH,
                                       HEIGHT,
                                       new ViewBoxDefinitionImpl(VBOX_MIN_X,
                                                                 VBOX_MIN_Y,
                                                                 VBOX_WIDTH,
                                                                 VBOX_HEIGHT),
                                       mainDef,
                                       circleDefinition);
        viewDefinition.setName("svg-view-test");
        viewDefinition.setPath("svg-view-test.svg");
        final String generated = tested.generate(viewFactory,
                                                 viewDefinition).toString();
        assertTrue(generated.contains("public SVGShapeView svg-view-test(final boolean resizable)"));
        assertTrue(generated.contains("return this.svg-view-test(25.50d, 225.45d, resizable);"));
        assertTrue(generated.contains("public SVGShapeView svg-view-test(final double width, final double height, final boolean resizable) {"));
        assertTrue(generated.contains("final SVGShapeViewImpl view = new SVGShapeViewImpl(\"svg-view-test\", , width, height, resizable)"));
        assertTrue(generated.contains("private SVGBasicShapeView svg-view-testBasicView() {"));
        assertTrue(generated.contains("return this.svg-view-testBasicView(25.50d, 225.45d);"));
        assertTrue(generated.contains("private SVGBasicShapeView svg-view-testBasicView(final double width, final double height) {"));
        assertTrue(generated.contains("final SVGBasicShapeViewImpl view = new SVGBasicShapeViewImpl(\"svg-view-test\", , width, height)"));
    }
}
