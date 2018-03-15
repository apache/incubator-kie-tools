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

package org.kie.workbench.common.stunner.svg.gen.translator.css;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class SVGAttributeParserTest {

    @Test
    public void testPixelValues() {
        assertEquals(0d,
                     SVGAttributeParser.toPixelValue("0"),
                     0d);
        assertEquals(0d,
                     SVGAttributeParser.toPixelValue("0px"),
                     0d);
        assertEquals(10d,
                     SVGAttributeParser.toPixelValue("10px"),
                     0d);
        assertEquals(1328.5d,
                     SVGAttributeParser.toPixelValue("1328.5"),
                     0d);
        assertEquals(1328.5d,
                     SVGAttributeParser.toPixelValue("1328.5px"),
                     0d);
        assertEquals(0d,
                     SVGAttributeParser.toPixelValue("",
                                                     0d),
                     0d);
        assertEquals(12.3d,
                     SVGAttributeParser.toPixelValue("",
                                                     12.3d),
                     0d);
        assertEquals(3d,
                     SVGAttributeParser.toPixelValue(null,
                                                     3d),
                     0d);
    }

    @Test
    public void testToHexColorString() {
        assertEquals("#000000",
                     SVGAttributeParser.toHexColorString("#000000"));
        assertEquals("#123456",
                     SVGAttributeParser.toHexColorString("#123456"));
        assertEquals("#0000ff",
                     SVGAttributeParser.toHexColorString("#ff"));
        assertEquals("#0000ff",
                     SVGAttributeParser.toHexColorString("blue"));
        assertEquals("#ff0000",
                     SVGAttributeParser.toHexColorString("red"));
        assertEquals("#000000",
                     SVGAttributeParser.toHexColorString("black"));
        assertEquals("#ff0000",
                     SVGAttributeParser.toHexColorString("rgb(255,0,0)"));
        assertEquals("#0000ff",
                     SVGAttributeParser.toHexColorString("rgb(0,0,255)"));
    }

    @Test
    public void testRGBToHexString() {
        assertEquals("#000000",
                     SVGAttributeParser.rgbToHexString(0,
                                                       0,
                                                       0,
                                                       1));
        assertEquals("#0000ff",
                     SVGAttributeParser.rgbToHexString(0,
                                                       0,
                                                       255,
                                                       1));
        assertEquals("#ff0000",
                     SVGAttributeParser.rgbToHexString(255,
                                                       0,
                                                       0,
                                                       1));
        assertEquals("#ffffff",
                     SVGAttributeParser.rgbToHexString(255,
                                                       255,
                                                       255,
                                                       1));
    }

    @Test
    public void testToRGB() {
        assertEquals(16777216,
                     SVGAttributeParser.toRGB(0,
                                              0,
                                              0,
                                              1));
        assertEquals(16777471,
                     SVGAttributeParser.toRGB(0,
                                              0,
                                              255,
                                              1));
        assertEquals(33488896,
                     SVGAttributeParser.toRGB(255,
                                              0,
                                              0,
                                              1));
        assertEquals(33554431,
                     SVGAttributeParser.toRGB(255,
                                              255,
                                              255,
                                              1));
    }
}
