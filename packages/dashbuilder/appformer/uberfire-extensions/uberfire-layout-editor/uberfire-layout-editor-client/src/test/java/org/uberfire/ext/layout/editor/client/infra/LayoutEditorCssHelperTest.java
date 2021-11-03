/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.ext.layout.editor.client.infra;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.ext.layout.editor.api.css.CssAllowedValue;
import org.uberfire.ext.layout.editor.api.css.CssFontSize;
import org.uberfire.ext.layout.editor.api.css.CssProperty;
import org.uberfire.ext.layout.editor.api.css.CssValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@RunWith(GwtMockitoTestRunner.class)
public class LayoutEditorCssHelperTest {

    private LayoutEditorCssHelper cssHelper;

    @Before
    public void setup() {
        cssHelper = spy(new LayoutEditorCssHelper());
    }

    @Test
    public void testReadFromMap() {
        Map<String,String> properties = new HashMap<>();
        properties.put(CssProperty.MARGIN_TOP.getName(), "100px");
        properties.put("prop1", "v1");
        List<CssValue> cssValueList = cssHelper.readCssValues(properties);

        assertEquals(cssValueList.size(), 1);
        assertEquals(cssValueList.get(0).getProperty(), "margin-top");
        assertEquals(cssValueList.get(0).getValue(), "100px");
    }

    @Test
    public void testParseAllowedValue() {
        when(cssHelper.formatCssAllowedValue(CssProperty.FONT_SIZE, CssFontSize.XX_LARGE)).thenReturn("Extra Large");
        CssAllowedValue cssAllowedValue = cssHelper.parseCssAllowedValue(CssProperty.FONT_SIZE, "Extra Large");
        assertEquals(cssAllowedValue.getName(), "xx-large");
    }
}