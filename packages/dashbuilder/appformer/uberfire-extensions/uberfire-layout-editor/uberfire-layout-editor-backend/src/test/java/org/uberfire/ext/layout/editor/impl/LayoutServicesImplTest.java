/*
 * Copyright 2015 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.layout.editor.impl;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;

import static org.junit.Assert.*;

public class LayoutServicesImplTest {

    private LayoutServicesImpl layoutServices;

    private static String loadSample(String file) {
        try {
            return IOUtils.toString(new LayoutServicesImplTest().getClass().getResourceAsStream(file),
                                    "UTF-8");
        } catch (IOException e) {
            return "";
        }
    }

    @Before
    public void setup() {
        layoutServices = new LayoutServicesImpl();
        layoutServices.init();
    }

    @Test
    public void layoutMarshaller12withHTMLComponent() {
        String expected = loadSample("12withHTMLComponent.txt");
        LayoutTemplate template = layoutServices.fromJson(expected);
        String actual = layoutServices.toJson(template);
        assertEquals(expected,
                     actual);
    }

    @Test
    public void layoutMarshallerBigLayout() {
        String expected = loadSample("BigLayout.txt");
        LayoutTemplate template = layoutServices.fromJson(expected);
        String actual = layoutServices.toJson(template);
        assertEquals(expected,
                     actual);
    }

    @Test
    public void layoutMarshallerSubColumns() {
        String expected = loadSample("SubColumnsLayout.txt");
        LayoutTemplate template = layoutServices.fromJson(expected);
        String actual = layoutServices.toJson(template);
        assertEquals(expected,
                     actual);
    }
    
    @Test
    public void layoutMarshallerComponentWithParts() {
        String expected = loadSample("LayoutWithComponentWithParts.txt");
        LayoutTemplate template = layoutServices.fromJson(expected);
        String actual = layoutServices.toJson(template);
        assertEquals(expected,
                     actual);
    }
}