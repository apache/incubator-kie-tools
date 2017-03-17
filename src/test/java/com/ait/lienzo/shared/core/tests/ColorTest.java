/*
   Copyright (c) 2017 Ahome' Innovation Technologies. All rights reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.ait.lienzo.shared.core.tests;

import static com.ait.lienzo.shared.core.types.Color.fromColorString;
import static com.ait.lienzo.shared.core.types.ColorName.ANTIQUEWHITE;
import static com.ait.lienzo.shared.core.types.ColorName.BEIGE;
import static com.ait.lienzo.shared.core.types.ColorName.BISQUE;
import static com.ait.lienzo.shared.core.types.ColorName.BLACK;
import static com.ait.lienzo.shared.core.types.ColorName.BLUE;
import static com.ait.lienzo.shared.core.types.ColorName.BLUEVIOLET;
import static com.ait.lienzo.shared.core.types.ColorName.BROWN;
import static com.ait.lienzo.shared.core.types.ColorName.CHARTREUSE;
import static com.ait.lienzo.shared.core.types.ColorName.LIME;
import static com.ait.lienzo.shared.core.types.ColorName.PALEVIOLETRED;
import static com.ait.lienzo.shared.core.types.ColorName.RED;
import static com.ait.lienzo.shared.core.types.ColorName.SALMON;
import static com.ait.lienzo.shared.core.types.ColorName.SIENNA;
import static com.ait.lienzo.shared.core.types.ColorName.WHITE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.ait.lienzo.shared.core.types.Color;
import com.ait.lienzo.shared.core.types.ColorName;

public class ColorTest
{
    @Test
    public void testHex2RGB()
    {
        Color white = Color.hex2RGB("#fFFffF");
        assertEquals(WHITE.getColor(), white);

        Color black = Color.hex2RGB("#000000");
        assertEquals(BLACK.getColor(), black);

        Color maximumRed = Color.hex2RGB("#ff0000");
        assertEquals(RED.getColor(), maximumRed);

        Color maximumGreen = Color.hex2RGB("#00FF00");
        assertEquals(LIME.getColor(), maximumGreen);
        maximumGreen = Color.hex2RGB("#0f0");
        assertEquals(LIME.getColor(), maximumGreen);

        Color maximumBlue = Color.hex2RGB("#0000fF");
        assertEquals(BLUE.getColor(), maximumBlue);
        maximumBlue = Color.hex2RGB("#00f");
        assertEquals(BLUE.getColor(), maximumBlue);

        assertEquals(BEIGE.getColor(), Color.hex2RGB(BEIGE.getHexColor()));

        assertNull(Color.hex2RGB("#XYZ"));

    }

    @Test
    public void testGetRGB()
    {
        Color color = ANTIQUEWHITE.getColor();
        final String ANTIQUEWHITE_RGB = String.format("rgb(%s,%s,%s)", ANTIQUEWHITE.getR(), ANTIQUEWHITE.getG(), ANTIQUEWHITE.getB());
        assertEquals(ANTIQUEWHITE_RGB, color.getRGB());
    }

    @Test
    public void testGetRGBA()
    {
        Color color = BISQUE.getColor();
        final String BISQUE_RGBA = String.format("rgba(%s,%s,%s,%s)", BISQUE.getR(), BISQUE.getG(), BISQUE.getB(), BISQUE.getA());
        assertEquals(BISQUE_RGBA, color.getRGBA());
    }

    @Test
    public void testGetRandomHexColor()
    {
        for (int i = 0; i < 1000; i++)
        {
            String hex = Color.getRandomHexColor();
            assertEquals(7, hex.length());
            assertTrue(hex.startsWith("#"));
            assertFalse(hex.contains("-"));

            String hexWithoutPound = hex.substring(1, hex.length());
            Integer.parseInt(hexWithoutPound, 16);
        }
    }

    @Test
    public void testBrightness()
    {
        Color color = BROWN.getColor();
        assertEquals("rgb(165,42,42)", color.getRGB());
        assertEquals("rgb(216,93,93)", color.brightness(0.2).getRGB());
        assertEquals("rgb(114,0,0)", color.brightness(-0.2).getRGB());

        assertEquals(WHITE.getColor(), color.brightness(5));
        assertEquals(BLACK.getColor(), color.brightness(-5));
    }

    @Test
    public void testToBrowserRGB()
    {
        assertEquals(BISQUE.getColor().getRGB(), Color.toBrowserRGB(BISQUE.getR(), BISQUE.getG(), BISQUE.getB()));
    }

    @Test
    public void testFromColorString()
    {
        assertEquals(ColorName.TRANSPARENT.getColor(), fromColorString("transparent"));

        assertNull(fromColorString("#1234567890"));
        assertNull(fromColorString("#aWaaaa"));
        assertEquals(BISQUE.getColor(), fromColorString("#ffe4c4"));
        assertEquals(LIME.getColor(), fromColorString("#0f0"));

        assertEquals(ANTIQUEWHITE.getColor(), fromColorString("antiquewhite"));

        assertEquals(BEIGE.getColor(), fromColorString("rgb(245, 245, 220)"));
        assertEquals(SIENNA.getColor(), fromColorString("rgb(62.7%, 32.2%, 17.6%)"));
        assertEquals(BEIGE.getColor().setA(0.23), fromColorString("rgba(245, 245, 220, 0.23)"));
        assertEquals(SIENNA.getColor().setA(0.67), fromColorString("rgba(62.7%, 32.2%, 17.6%, 0.67)"));
        assertEquals(SIENNA.getColor().setA(0.99), fromColorString("rgba(62.7%, 32.2%, 17.6%, 99%)"));
        assertEquals(SIENNA.getColor(), fromColorString("hsl(19.3, 56.1%, 40.2%)"));
        String hslWithCalculatedPercent = String.format("hsl(%.6f%%, 56.1%%, 40.2%%)", 19.3 / 360.0 * 100);
        assertEquals(SIENNA.getColor(), fromColorString(hslWithCalculatedPercent));
        assertEquals(SIENNA.getColor().setA(0.42), fromColorString("hsla(19.3, 56.1%, 40.2%, 0.42)"));

        assertNull(fromColorString("asdf7ya_!+_)_"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFromColorStringWithIllegalArgumentException()
    {
        assertEquals(SIENNA.getColor(), fromColorString("hsl(19.3, 56.1, 40.2)"));
    }

    @Test
    public void testFromHSL()
    {
        assertEquals(SIENNA.getColor(), Color.fromHSL(19.3, 56.1, 40.2));
        assertEquals(BLACK.getColor(), Color.fromHSL(0, 0, 0));
        assertEquals(WHITE.getColor(), Color.fromHSL(0.0, 0.0, 100.0));
    }

    @Test
    public void test()
    {
        Color color = SIENNA.getColor();
        final String SIENNA_RGB = String.format("rgb(%s,%s,%s)", SIENNA.getR(), SIENNA.getG(), SIENNA.getB());
        assertEquals(color.getColorString(), SIENNA_RGB);

        color = BEIGE.getColor().setA(0.2);
        final String BEIGE_RGBA = String.format("rgba(%s,%s,%s,0.2)", BEIGE.getR(), BEIGE.getG(), BEIGE.getB());
        assertEquals(color.getColorString(), BEIGE_RGBA);
    }

    @Test
    public void testEquals()
    {
        Color color = BEIGE.getColor();
        assertTrue(color.equals(color));
        assertTrue(color.equals(BEIGE.getColor()));
        assertFalse(color.equals(null));
        assertFalse(color.equals(SALMON.getColor()));
    }

    public void testGetHSLFromRGB()
    {
        final String SIENNA_HSL = "hsl(19.3,56.1%,40.2%)";
        Color sienna = fromColorString(SIENNA_HSL);
        assertEquals(SIENNA.getColor(), sienna);
        Color.HSL siennaHSLFromRGB = Color.getHSLFromRGB(sienna.getR(), sienna.getG(), sienna.getB());
        assertEquals(SIENNA_HSL, siennaHSLFromRGB.toBrowserHSL());

        final String PALEVIOLETRED_HSL = "hsl(340.4,59.8%,64.9%)";
        Color palevioletred = fromColorString(PALEVIOLETRED_HSL);
        assertEquals(PALEVIOLETRED.getColor(), palevioletred);
        Color.HSL paleVioletRedHSLFromGRG = Color.getHSLFromRGB(palevioletred.getR(), palevioletred.getG(), palevioletred.getB());
        assertEquals(PALEVIOLETRED_HSL, paleVioletRedHSLFromGRG.toBrowserHSL());

        final String CHARTREUSE_HSL = "hsl(90.1,100.0%,50.0%)";
        Color chartreuse = fromColorString(CHARTREUSE_HSL);
        assertEquals(CHARTREUSE.getColor(), chartreuse);
        Color.HSL chartreuseHSLFromGRG = Color.getHSLFromRGB(chartreuse.getR(), chartreuse.getG(), chartreuse.getB());
        assertEquals(CHARTREUSE_HSL, chartreuseHSLFromGRG.toBrowserHSL());

        final String BLUEVIOLET_HSL = "hsl(271.1,75.9%,52.7%)";
        Color blueviolet = fromColorString(BLUEVIOLET_HSL);
        assertEquals(BLUEVIOLET.getColor(), blueviolet);
        Color.HSL bluevioletHSLFromRGB = Color.getHSLFromRGB(blueviolet.getR(), blueviolet.getG(), blueviolet.getB());
        assertEquals(BLUEVIOLET_HSL, bluevioletHSLFromRGB.toBrowserHSL());
    }

    @Test
    public void testFixRGB()
    {
        Color red = new Color(256, -1, 0);
        assertEquals(RED.getColor(), red);
    }

    @Test
    public void testFixAlpha()
    {
        Color lime = new Color(0, 10000, -1000, -0.1);
        assertEquals(LIME.getColor().setA(0), lime);

        Color blue = new Color(-256, 0, 10000000, 2);
        assertEquals(BLUE.getColor(), blue);
    }

    public void testGetHSL()
    {
        assertEquals("hsl(271.1,75.9%,52.7%)", BLUEVIOLET.getColor().getHSL().toBrowserHSL());
    }
}
