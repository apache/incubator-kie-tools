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

package com.ait.lienzo.shared.core.types;

import java.util.List;

/**
 * CSS color names. They can be used whenever an {@link IColor} is needed.
 *
 * @see IColor
 * @see Color
 * @see <a href="http://www.w3.org/TR/css3-color/">CSS Color Module Level 3</a>
 */
public enum ColorName implements EnumWithValue, IColor
{
    ALICEBLUE("aliceblue", 240, 248, 255), // #f0f8ff
    ANTIQUEWHITE("antiquewhite", 250, 235, 215), // #faebd7
    AQUA("aqua", 0, 255, 255), // #00ffff
    AQUAMARINE("aquamarine", 127, 255, 212), // #7fffd4
    AZURE("azure", 240, 255, 255), // #f0ffff
    BEIGE("beige", 245, 245, 220), // #f5f5dc
    BISQUE("bisque", 255, 228, 196), // #ffe4c4
    BLACK("black", 0, 0, 0), // #000000
    BLANCHEDALMOND("blanchedalmond", 255, 235, 205), // #ffebcd
    BLUE("blue", 0, 0, 255), // #0000ff
    BLUEVIOLET("blueviolet", 138, 43, 226), // #8a2be2
    BROWN("brown", 165, 42, 42), // #a52a2a
    BURLYWOOD("burlywood", 222, 184, 135), // #deb887
    CADETBLUE("cadetblue", 95, 158, 160), // #5f9ea0
    CHARTREUSE("chartreuse", 127, 255, 0), // #7fff00
    CHOCOLATE("chocolate", 210, 105, 30), // #d2691e
    CORAL("coral", 255, 127, 80), // #ff7f50
    CORNFLOWERBLUE("cornflowerblue", 100, 149, 237), // #6495ed
    CORNSILK("cornsilk", 255, 248, 220), // #fff8dc
    CRIMSON("crimson", 220, 20, 60), // #dc143c
    CYAN("cyan", 0, 255, 255), // #00ffff
    DARKBLUE("darkblue", 0, 0, 139), // #00008b
    DARKCYAN("darkcyan", 0, 139, 139), // #008b8b
    DARKGOLDENROD("darkgoldenrod", 184, 134, 11), // #b8860b
    DARKGRAY("darkgray", 169, 169, 169), // #a9a9a9
    DARKGREEN("darkgreen", 0, 100, 0), // #006400
    DARKGREY("darkgrey", 169, 169, 169), // #a9a9a9
    DARKKHAKI("darkkhaki", 189, 183, 107), // #bdb76b
    DARKMAGENTA("darkmagenta", 139, 0, 139), // #8b008b
    DARKOLIVEGREEN("darkolivegreen", 85, 107, 47), // #556b2f
    DARKORANGE("darkorange", 255, 140, 0), // #ff8c00
    DARKORCHID("darkorchid", 153, 50, 204), // #9932cc
    DARKRED("darkred", 139, 0, 0), // #8b0000
    DARKSALMON("darksalmon", 233, 150, 122), // #e9967a
    DARKSEAGREEN("darkseagreen", 143, 188, 143), // #8fbc8f
    DARKSLATEBLUE("darkslateblue", 72, 61, 139), // #483d8b
    DARKSLATEGRAY("darkslategray", 47, 79, 79), // #2f4f4f
    DARKSLATEGREY("darkslategrey", 47, 79, 79), // #2f4f4f
    DARKTURQUOISE("darkturquoise", 0, 206, 209), // #00ced1
    DARKVIOLET("darkviolet", 148, 0, 211), // #9400d3
    DEEPPINK("deeppink", 255, 20, 147), // #ff1493
    DEEPSKYBLUE("deepskyblue", 0, 191, 255), // #00bfff
    DIMGRAY("dimgray", 105, 105, 105), // #696969
    DIMGREY("dimgrey", 105, 105, 105), // #696969
    DODGERBLUE("dodgerblue", 30, 144, 255), // #1e90ff
    FIREBRICK("firebrick", 178, 34, 34), // #b22222
    FLORALWHITE("floralwhite", 255, 250, 240), // #fffaf0
    FORESTGREEN("forestgreen", 34, 139, 34), // #228b22
    FUCHSIA("fuchsia", 255, 0, 255), // #ff00ff
    GAINSBORO("gainsboro", 220, 220, 220), // #dcdcdc
    GHOSTWHITE("ghostwhite", 248, 248, 255), // #f8f8ff
    GOLD("gold", 255, 215, 0), // #ffd700
    GOLDENROD("goldenrod", 218, 165, 32), // #daa520
    GRAY("gray", 128, 128, 128), // #808080
    GREEN("green", 0, 128, 0), // #008000
    GREENYELLOW("greenyellow", 173, 255, 47), // #adff2f
    GREY("grey", 128, 128, 128), // #808080
    HONEYDEW("honeydew", 240, 255, 240), // #f0fff0
    HOTPINK("hotpink", 255, 105, 180), // #ff69b4
    INDIANRED("indianred", 205, 92, 92), // #cd5c5c
    INDIGO("indigo", 75, 0, 130), // #4b0082
    IVORY("ivory", 255, 255, 240), // #fffff0
    KHAKI("khaki", 240, 230, 140), // #f0e68c
    LAVENDER("lavender", 230, 230, 250), // #e6e6fa
    LAVENDERBLUSH("lavenderblush", 255, 240, 245), // #fff0f5
    LAWNGREEN("lawngreen", 124, 252, 0), // #7cfc00
    LEMONCHIFFON("lemonchiffon", 255, 250, 205), // #fffacd
    LIGHTBLUE("lightblue", 173, 216, 230), // #add8e6
    LIGHTCORAL("lightcoral", 240, 128, 128), // #f08080
    LIGHTCYAN("lightcyan", 224, 255, 255), // #e0ffff
    LIGHTGOLDENRODYELLOW("lightgoldenrodyellow", 250, 250, 210), // #fafad2
    LIGHTGRAY("lightgray", 211, 211, 211), // #d3d3d3
    LIGHTGREEN("lightgreen", 144, 238, 144), // #90ee90
    LIGHTGREY("lightgrey", 211, 211, 211), // #d3d3d3
    LIGHTPINK("lightpink", 255, 182, 193), // #ffb6c1
    LIGHTSALMON("lightsalmon", 255, 160, 122), // #ffa07a
    LIGHTSEAGREEN("lightseagreen", 32, 178, 170), // #20b2aa
    LIGHTSKYBLUE("lightskyblue", 135, 206, 250), // #87cefa
    LIGHTSLATEGRAY("lightslategray", 119, 136, 153), // #778899
    LIGHTSLATEGREY("lightslategrey", 119, 136, 153), // #778899
    LIGHTSTEELBLUE("lightsteelblue", 176, 196, 222), // #b0c4de
    LIGHTYELLOW("lightyellow", 255, 255, 224), // #ffffe0
    LIME("lime", 0, 255, 0), // #00ff00
    LIMEGREEN("limegreen", 50, 205, 50), // #32cd32
    LINEN("linen", 250, 240, 230), // #faf0e6
    MAGENTA("MAGENTA", 255, 0, 255), // #FF00FF
    MAROON("maroon", 128, 0, 0), // #800000
    MEDIUMAQUAMARINE("mediumaquamarine", 102, 205, 170), // #66cdaa
    MEDIUMBLUE("mediumblue", 0, 0, 205), // #0000cd
    MEDIUMORCHID("mediumorchid", 186, 85, 211), // #ba55d3
    MEDIUMPURPLE("mediumpurple", 147, 112, 219), // #9370db
    MEDIUMSEAGREEN("mediumseagreen", 60, 179, 113), // #3cb371
    MEDIUMSLATEBLUE("mediumslateblue", 123, 104, 238), // #7b68ee
    MEDIUMSPRINGGREEN("mediumspringgreen", 0, 250, 154), // #00fa9a
    MEDIUMTURQUOISE("mediumturquoise", 72, 209, 204), // #48d1cc
    MEDIUMVIOLETRED("mediumvioletred", 199, 21, 133), // #c71585
    MIDNIGHTBLUE("midnightblue", 25, 25, 112), // #191970
    MINTCREAM("mintcream", 245, 255, 250), // #f5fffa
    MISTYROSE("mistyrose", 255, 228, 225), // #ffe4e1
    MOCCASIN("moccasin", 255, 228, 181), // #ffe4b5
    NAVAJOWHITE("navajowhite", 255, 222, 173), // #ffdead
    NAVY("navy", 0, 0, 128), // #000080
    OLDLACE("oldlace", 253, 245, 230), // #fdf5e6
    OLIVE("olive", 128, 128, 0), // #808000
    OLIVEDRAB("olivedrab", 107, 142, 35), // #6b8e23
    ORANGE("orange", 255, 165, 0), // #ffa500
    ORANGERED("orangered", 255, 69, 0), // #ff4500
    ORCHID("orchid", 218, 112, 214), // #da70d6
    PALEGOLDENROD("palegoldenrod", 238, 232, 170), // #eee8aa
    PALEGREEN("palegreen", 152, 251, 152), // #98fb98
    PALETURQUOISE("paleturquoise", 175, 238, 238), // #afeeee
    PALEVIOLETRED("palevioletred", 219, 112, 147), // #db7093
    PAPAYAWHIP("papayawhip", 255, 239, 213), // #ffefd5
    PEACHPUFF("peachpuff", 255, 218, 185), // #ffdab9
    PERU("peru", 205, 133, 63), // #cd853f
    PINK("pink", 255, 192, 203), // #ffc0cb
    PLUM("plum", 221, 160, 221), // #dda0dd
    POWDERBLUE("powderblue", 176, 224, 230), // #b0e0e6
    PURPLE("purple", 128, 0, 128), // #800080
    RED("red", 255, 0, 0), // #ff0000
    ROSYBROWN("rosybrown", 188, 143, 143), // #bc8f8f
    ROYALBLUE("royalblue", 65, 105, 225), // #4169e1
    SADDLEBROWN("saddlebrown", 139, 69, 19), // #8b4513
    SALMON("salmon", 250, 128, 114), // #fa8072
    SANDYBROWN("sandybrown", 244, 164, 96), // #f4a460
    SEAGREEN("seagreen", 46, 139, 87), // #2e8b57
    SEASHELL("seashell", 255, 245, 238), // #fff5ee
    SIENNA("sienna", 160, 82, 45), // #a0522d
    SILVER("silver", 192, 192, 192), // #c0c0c0
    SKYBLUE("skyblue", 135, 206, 235), // #87ceeb
    SLATEBLUE("slateblue", 106, 90, 205), // #6a5acd
    SLATEGRAY("slategray", 112, 128, 144), // #708090
    SLATEGREY("slategrey", 112, 128, 144), // #708090
    SNOW("snow", 255, 250, 250), // #fffafa
    SPRINGGREEN("springgreen", 0, 255, 127), // #00ff7f
    STEELBLUE("steelblue", 70, 130, 180), // #4682b4
    TAN("tan", 210, 180, 140), // #d2b48c
    TEAL("teal", 0, 128, 128), // #008080
    THISTLE("thistle", 216, 191, 216), // #d8bfd8
    TOMATO("tomato", 255, 99, 71), // #ff6347
    TURQUOISE("turquoise", 64, 224, 208), // #40e0d0
    VIOLET("violet", 238, 130, 238), // #ee82ee
    WHEAT("wheat", 245, 222, 179), // #f5deb3
    WHITE("white", 255, 255, 255), // #ffffff
    WHITESMOKE("whitesmoke", 245, 245, 245), // #f5f5f5
    YELLOW("yellow", 255, 255, 0), // #ffff00
    YELLOWGREEN("yellowgreen", 154, 205, 50), // #9acd32
    TRANSPARENT("transparent", 0, 0, 0, 0);

    private static final EnumStringMap<ColorName> LOOKUP_MAP = Statics.build(ColorName.values());

    private final String m_value;

    private final int m_r;

    private final int m_g;

    private final int m_b;

    private final double m_a;

    private ColorName(final String value, final int r, final int g, final int b)
    {
        m_value = value;

        m_r = r;

        m_g = g;

        m_b = b;

        m_a = 1;
    }

    private ColorName(final String value, final int r, final int g, final int b, final double a)
    {
        m_value = value;

        m_r = r;

        m_g = g;

        m_b = b;

        m_a = Math.max(Math.min(a, 1.0), 0.0);
    }

    public String getHexColor()
    {
        return Color.rgbToBrowserHexColor(getR(), getG(), getB());
    }

    @Override
    public final String getValue()
    {
        return m_value;
    }

    @Override
    public final String toString()
    {
        return m_value;
    }

    @Override
    public final String getColorString()
    {
        return m_value;
    }

    @Override
    public final int getR()
    {
        return m_r;
    }

    @Override
    public final int getG()
    {
        return m_g;
    }

    @Override
    public final int getB()
    {
        return m_b;
    }

    @Override
    public final double getA()
    {
        return m_a;
    }

    public Color getColor()
    {
        if (getA() == 1)
        {
            return new Color(getR(), getG(), getB());
        }
        return new Color(getR(), getG(), getB(), getA());
    }

    public static final ColorName lookup(final String key)
    {
        return Statics.lookup(key, LOOKUP_MAP, null);
    }

    public static final List<String> getKeys()
    {
        return Statics.getKeys(ColorName.values());
    }

    public static final List<ColorName> getValues()
    {
        return Statics.getValues(ColorName.values());
    }
}
