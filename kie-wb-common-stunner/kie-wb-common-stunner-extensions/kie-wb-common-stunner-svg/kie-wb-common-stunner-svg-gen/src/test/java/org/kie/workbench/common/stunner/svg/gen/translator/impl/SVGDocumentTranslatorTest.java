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

package org.kie.workbench.common.stunner.svg.gen.translator.impl;

import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;
import org.kie.workbench.common.stunner.svg.gen.codegen.impl.ViewGenerators;
import org.kie.workbench.common.stunner.svg.gen.exception.TranslatorException;
import org.kie.workbench.common.stunner.svg.gen.model.PrimitiveDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.StyleSheetDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.ViewDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.ViewRefDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.impl.CircleDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.impl.GroupDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.impl.MultiPathDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.impl.RectDefinition;
import org.kie.workbench.common.stunner.svg.gen.translator.SVGDocumentTranslator;
import org.kie.workbench.common.stunner.svg.gen.translator.SVGTranslatorContext;
import org.mockito.runners.MockitoJUnitRunner;
import org.w3c.dom.Document;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class SVGDocumentTranslatorTest {

    private static Document svgTest;
    private static Document svgTestError;
    private SVGDocumentTranslator translator;
    private static final StyleSheetDefinition styleSheetDefinition =
            new StyleSheetDefinition("svg-test-css");

    @BeforeClass
    public static void init() throws Exception {
        svgTest = parse(loadStream(SVGTranslationTestAssertions.SVG_TEST_PATH));
        svgTestError = parse(loadStream(SVGTranslationTestAssertions.SVG_TEST_PATH_ERRORS));
    }

    @Before
    public void setup() {
        translator = ViewGenerators.newTranslator();
    }

    @Test
    public void testTranslate() throws Exception {
        final ViewDefinition<SVGShapeView> viewDefinition =
                translator.translate(new SVGTranslatorContext(svgTest,
                                                              "",
                                                              styleSheetDefinition));
        assertNotNull(viewDefinition);
        assertEquals("svg-test-file",
                     viewDefinition.getId());
        // View definition's viewBox.
        final ViewDefinition.ViewBoxDefinition viewBox = viewDefinition.getViewBox();
        assertNotNull(viewBox);
        final double minX = viewBox.getMinX();
        final double minY = viewBox.getMinY();
        final double width = viewBox.getWidth();
        final double height = viewBox.getHeight();
        assertEquals(minX,
                     0d,
                     0d);
        assertEquals(minY,
                     0d,
                     0d);
        assertEquals(width,
                     448d,
                     0d);
        assertEquals(height,
                     448d,
                     0d);
        // View definition's main shape.
        final PrimitiveDefinition mainShapeDef = viewDefinition.getMain();
        assertNotNull(mainShapeDef);
        assertTrue(mainShapeDef instanceof MultiPathDefinition);
        final MultiPathDefinition mainPathDef = (MultiPathDefinition) mainShapeDef;
        SVGTranslationTestAssertions.assertPath(mainPathDef);
        assertTrue(mainPathDef.isListening());
        // View definition's child shapes.
        final List<PrimitiveDefinition> childrenDefs = viewDefinition.getChildren();
        assertNotNull(childrenDefs);
        assertTrue(childrenDefs.size() == 3);
        final RectDefinition rectDefinition = (RectDefinition) childrenDefs.get(0);
        SVGTranslationTestAssertions.assertRectangle(rectDefinition);
        assertFalse(rectDefinition.isListening());
        final CircleDefinition circleDefinition = (CircleDefinition) childrenDefs.get(1);
        SVGTranslationTestAssertions.assertCircle(circleDefinition);
        assertFalse(circleDefinition.isListening());
        // Assert other svg reference elements.
        final GroupDefinition groupDefinition = (GroupDefinition) childrenDefs.get(2);
        assertFalse(groupDefinition.isListening());
        SVGTranslationTestAssertions.assertGroupRef(groupDefinition);
        final List<ViewRefDefinition> svgViewRefs = viewDefinition.getSVGViewRefs();
        assertNotNull(svgViewRefs);
        assertTrue(svgViewRefs.size() == 1);
        final ViewRefDefinition viewRefDef = svgViewRefs.get(0);
        SVGTranslationTestAssertions.assertViewRef(viewRefDef);
    }

    @Test(expected = TranslatorException.class)
    public void testCheckTranslateErrors() throws Exception {
        translator.translate(new SVGTranslatorContext(svgTestError,
                                                      "",
                                                      styleSheetDefinition));
    }

    private static Document parse(final InputStream inputStream) throws Exception {
        final Document root = newBuilder().parse(inputStream);
        root.getDocumentElement().normalize();
        return root;
    }

    private static DocumentBuilder newBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory
                .newInstance();
        documentFactory.setNamespaceAware(true);
        return documentFactory.newDocumentBuilder();
    }

    private static InputStream loadStream(final String path) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
    }
}
