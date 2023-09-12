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

package org.uberfire.ext.layout.editor.client.infra;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.ext.layout.editor.api.css.CssAllowedValue;
import org.uberfire.ext.layout.editor.api.css.CssFontSize;
import org.uberfire.ext.layout.editor.api.css.CssProperty;
import org.uberfire.ext.layout.editor.api.css.CssValue;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

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

        assertEquals(1, cssValueList.size());
        assertEquals("margin-top", cssValueList.get(0).getProperty());
        assertEquals("100px", cssValueList.get(0).getValue());
    }

    @Test
    public void testParseAllowedValue() {
        when(cssHelper.formatCssAllowedValue(CssProperty.FONT_SIZE, CssFontSize.XX_LARGE)).thenReturn("Extra Large");
        CssAllowedValue cssAllowedValue = cssHelper.parseCssAllowedValue(CssProperty.FONT_SIZE, "Extra Large");
        assertEquals("xx-large", cssAllowedValue.getName());
    }
}