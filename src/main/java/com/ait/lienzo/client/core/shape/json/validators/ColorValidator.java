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

package com.ait.lienzo.client.core.shape.json.validators;

import com.ait.lienzo.shared.core.types.ColorName;
import com.google.gwt.regexp.shared.RegExp;

/**
 * Validates CSS3 color attributes.
 * 
 * Note that the CSS3 color spec allows values outside the nominal range.
 * E.g. RGB integer values of -10 and 310 will be "clipped" to 0 and 255 respectively.
 * 
 * @see <a href="http://www.w3.org/TR/css3-color/">CSS Color Module Level 3</a>
 */
public class ColorValidator extends AbstractAttributeTypeValidator
{
    private static final String[]       SPECIAL_COLOR_NAMES = { "transparent", "currentcolor", "inherit" };

    // integer 0 - 255
    private static final String        I                   = "(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])";

    // floating point value with optional plus/minus
    // TODO: still accepts leading zeroes e.g. 001.12
    private static final String        F                   = "(?:[-+]?\\d+(\\.\\d+)?)";

    // percentage - float with percent sign %
    private static final String        P                   = "(?:" + F + "%)";

    // I or P - integer (0-255) or percentage
    private static final String        IP                  = "(?:" + I + "|" + P + ")";

    // alpha - float value between 0 and 1
    private static final String         A                   = F;

    private static final String         RGB                 = "\\s*" + IP + "\\s*,\\s*" + IP + "\\s*,\\s*" + IP + "\\s*";

    private static final String         HSL                 = "\\s*" + F + "\\s*,\\s*" + P + "\\s*,\\s*" + P + "\\s*";

    private static final String         COLOR               = "#[0-9A-Fa-f]{3}|#[0-9A-Fa-f]{6}|rgb\\(" + RGB + "\\)|rgba\\(" + RGB + "\\s*,\\s*" + A + "\\)|hsl\\(" + HSL + "\\)|hsla\\(" + HSL + "\\s*,\\s*" + A + "\\)";

    private static final RegExp COLOR_RE            = RegExp.compile("^(?:" + COLOR + ")$");

    public static final  ColorValidator INSTANCE            = new ColorValidator();

    public ColorValidator()
    {
        super("Color");

    }

    @Override
    public void validate(Object jval, ValidationContext ctx) throws ValidationException
    {
        if (null == jval)
        {
            ctx.addBadTypeError(getTypeName());

            return;
        }
        // FIXME serialization (mdp)
//        JSONString s = jval.isString();
//
//        if (null == s)
//        {
//            ctx.addBadTypeError(getTypeName());
//
//            return;
//        }
//
//        // see http://www.w3.org/TR/css3-color/
//        // "#00f", "#0f0f0f", "#00F", "#0F0F0F", "rgb(255,0,0)", "rgba(0,0,0,0)", "red",
//        // "rgb(100%, 0%, 0%)", "hsl(0, 100%, 50%)", "hsla(120, 100%, 50%, 1)",
//        // "transparent", "inherit", "currentcolor"
//
//        // White space characters are allowed around the numerical values.
//        // Alpha should be between 0.0 and 1.0 inclusive
//        // All color names are a single word (no spaces or special characters)
//        // Color names are case-insensitive.
//
//        if (false == isValidColorName(s.stringValue()))
//        {
//            ctx.addBadValueError(getTypeName(), jval);
//        }
    }

    /**
     * Checks whether the colorName is a valid CSS color.
     * This includes the "special" colors: "transparent", "inherit" or "currentcolor".
     * Color names are case-insensitive. 
     * 
     * <br/><br/>
     * Here are some examples of valid colors:
     * "#00f", "#0f0f0f", "#00F", "#0F0F0F", "rgb(255,0,0)", "rgba(0,0,0,0)", "red", 
     * "rgb(100%, 0%, 0%)", "hsl(0, 100%, 50%)", "hsla(120, 100%, 50%, 1)", 
     * "transparent", "inherit", "currentcolor".
     * 
     * @param colorName
     * 
     * @return Whether the colorName is a valid CSS color. 
     *   
     * @see <a href="http://www.w3.org/TR/css3-color/">CSS Color Module Level 3</a>
     */

    public static boolean isValidColorName(String colorName)
    {
        if (null == colorName)
        {
            return false;
        }
        String str = colorName.toLowerCase();

        return (isSpecialColorName(str) || ColorName.lookup(str) != null || COLOR_RE.test(str));
    }

    /**
     * The test is case-sensitive. It assumes the value was already converted to lower-case.
     * 
     * @param colorName
     * 
     * @return Whether the colorName is one of the "special" color names that are allowed as color values,
     *   but are not actually colors, i.e. "transparent", "inherit" or "currentcolor".
     */
    public static boolean isSpecialColorName(String colorName)
    {
        for (String name : SPECIAL_COLOR_NAMES)
        {
            if (name.equalsIgnoreCase(colorName))
            {
                return true;
            }
        }
        return false;
    }
}
