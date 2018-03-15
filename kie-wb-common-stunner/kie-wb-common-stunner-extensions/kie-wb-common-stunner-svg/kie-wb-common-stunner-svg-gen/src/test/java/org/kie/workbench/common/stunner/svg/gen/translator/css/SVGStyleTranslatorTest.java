/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.svg.gen.translator.css;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.w3c.dom.Element;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SVGStyleTranslatorTest {

    private static final String ID_ELEMENT = "e1";
    private static final String CLASS_ELEMENT = "ce1 ce2";
    private static final String ID_PARENT = "p1";
    private static final String CLASS_PARENT = "cp1 cp2";
    private static final String ID_ROOT = "r1";
    private static final String CLASS_ROOT = "cr1 cr2";

    @Mock
    private Element element;

    @Mock
    private Element parent;

    @Mock
    private Element root;

    @Before
    public void init() {
        when(element.getParentNode()).thenReturn(parent);
        when(parent.getParentNode()).thenReturn(root);
        when(root.getParentNode()).thenReturn(null);
        when(element.getAttribute(eq(SVGStyleTranslator.ID))).thenReturn(ID_ELEMENT);
        when(element.getAttribute(eq(SVGStyleTranslator.CSS_CLASS))).thenReturn(CLASS_ELEMENT);
        when(parent.getAttribute(eq(SVGStyleTranslator.ID))).thenReturn(ID_PARENT);
        when(parent.getAttribute(eq(SVGStyleTranslator.CSS_CLASS))).thenReturn(CLASS_PARENT);
        when(root.getAttribute(eq(SVGStyleTranslator.ID))).thenReturn(ID_ROOT);
        when(root.getAttribute(eq(SVGStyleTranslator.CSS_CLASS))).thenReturn(CLASS_ROOT);
    }

    @Test
    public void testGetParentsTree() {
        List<Element> tree = SVGStyleTranslator.getElementsTree(element);
        assertNotNull(tree);
        assertEquals(3, tree.size());
        assertEquals(element, tree.get(0));
        assertEquals(parent, tree.get(1));
        assertEquals(root, tree.get(2));
    }

    @Test
    public void testParseElementSelectorsForElement() {
        Collection<String> selectors = SVGStyleTranslator.parseElementSelectors(element);
        assertNotNull(selectors);
        assertEquals(3, selectors.size());
        assertTrue(selectors.contains("#" + ID_ELEMENT));
        assertTrue(selectors.contains(".ce1"));
        assertTrue(selectors.contains(".ce2"));
    }

    @Test
    public void testParseElementSelectorsForParent() {
        Collection<String> selectors = SVGStyleTranslator.parseElementSelectors(parent);
        assertNotNull(selectors);
        assertEquals(3, selectors.size());
        assertTrue(selectors.contains("#" + ID_PARENT));
        assertTrue(selectors.contains(".cp1"));
        assertTrue(selectors.contains(".cp2"));
    }

    @Test
    public void testParseElementSelectorsForRoot() {
        Collection<String> selectors = SVGStyleTranslator.parseElementSelectors(root);
        assertNotNull(selectors);
        assertEquals(3, selectors.size());
        assertTrue(selectors.contains("#" + ID_ROOT));
        assertTrue(selectors.contains(".cr1"));
        assertTrue(selectors.contains(".cr2"));
    }

    @Test
    public void testParseAllSelectors() {
        Collection<String> selectors = SVGStyleTranslator.parseAllSelectors(element);
        assertNotNull(selectors);
        assertEquals(48, selectors.size());
        assertArrayEquals(selectors.stream().toArray(String[]::new),
                          ALL_SELECTORS.stream().toArray(String[]::new));
    }

    private static List<String> ALL_SELECTORS = Arrays.asList(".ce1",
                                                              ".ce2",
                                                              "#e1",
                                                              ".cp1 .ce1",
                                                              ".cp1 .ce2",
                                                              ".cp1 #e1",
                                                              ".cp2 .ce1",
                                                              ".cp2 .ce2",
                                                              ".cp2 #e1",
                                                              "#p1 .ce1",
                                                              "#p1 .ce2",
                                                              "#p1 #e1",
                                                              ".cr1 .ce1",
                                                              ".cr1 .ce2",
                                                              ".cr1 #e1",
                                                              ".cr1 .cp1 .ce1",
                                                              ".cr1 .cp1 .ce2",
                                                              ".cr1 .cp1 #e1",
                                                              ".cr1 .cp2 .ce1",
                                                              ".cr1 .cp2 .ce2",
                                                              ".cr1 .cp2 #e1",
                                                              ".cr1 #p1 .ce1",
                                                              ".cr1 #p1 .ce2",
                                                              ".cr1 #p1 #e1",
                                                              ".cr2 .ce1",
                                                              ".cr2 .ce2",
                                                              ".cr2 #e1",
                                                              ".cr2 .cp1 .ce1",
                                                              ".cr2 .cp1 .ce2",
                                                              ".cr2 .cp1 #e1",
                                                              ".cr2 .cp2 .ce1",
                                                              ".cr2 .cp2 .ce2",
                                                              ".cr2 .cp2 #e1",
                                                              ".cr2 #p1 .ce1",
                                                              ".cr2 #p1 .ce2",
                                                              ".cr2 #p1 #e1",
                                                              "#r1 .ce1",
                                                              "#r1 .ce2",
                                                              "#r1 #e1",
                                                              "#r1 .cp1 .ce1",
                                                              "#r1 .cp1 .ce2",
                                                              "#r1 .cp1 #e1",
                                                              "#r1 .cp2 .ce1",
                                                              "#r1 .cp2 .ce2",
                                                              "#r1 .cp2 #e1",
                                                              "#r1 #p1 .ce1",
                                                              "#r1 #p1 .ce2",
                                                              "#r1 #p1 #e1");
}
