/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.layout.editor.api.css;

import org.junit.Test;

import static org.junit.Assert.*;

public class CssRuleTest {

    @Test
    public void cssRuleTest() {
        CssRule cssRule = new CssRule("#myId");
        cssRule.add(new CssValue("width", "100%"));
        cssRule.add(new CssValue("height", "50%"));
        String toString = cssRule.toString();

        assertEquals(cssRule.getSelector(), "#myId");
        assertEquals(cssRule.size(), 2);
        assertEquals(cssRule.get(0).getProperty(), "width");
        assertEquals(cssRule.get(0).getValue(), "100%");
        assertEquals(cssRule.get(1).getProperty(), "height");
        assertEquals(cssRule.get(1).getValue(), "50%");
        assertEquals(toString, "#myId {\n  width:100%;\n  height:50%;\n}");
    }

    @Test
    public void cssPropertyCamelCase() {
        CssValue cssValue = new CssValue("margin-top", "100px");
        assertEquals("marginTop", cssValue.getPropertyInCamelCase());
    }
}
