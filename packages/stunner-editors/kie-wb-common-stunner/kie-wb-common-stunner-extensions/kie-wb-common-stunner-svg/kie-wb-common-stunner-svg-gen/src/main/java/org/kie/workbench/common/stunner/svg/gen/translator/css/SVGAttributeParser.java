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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ait.lienzo.shared.core.types.Color;
import com.ait.lienzo.shared.core.types.ColorName;
import org.apache.commons.lang3.StringUtils;

public class SVGAttributeParser {

    private static final Pattern RBG_PATTERN = Pattern.compile("rgb *\\( *([0-9]+), *([0-9]+), *([0-9]+) *\\)");

    public static double toPixelValue(final String value,
                                      final double defaultValue) {
        if (isEmpty(value)) {
            return defaultValue;
        }
        return toPixelValue(value);
    }

    public static double toPixelValue(final String value) {
        if (value.endsWith("px") || value.endsWith("PX")) {
            return parseDouble(value.substring(0,
                                               value.length() - 2));
        }
        return parseDouble(value);
    }

    public static String toHexColorString(final String raw) {
        if (raw.startsWith("#")) {
            return "#" + StringUtils.leftPad(raw.substring(1,
                                                           raw.length()),
                                             6,
                                             "0");
        }
        if (raw.startsWith("rgb")) {
            Matcher m = RBG_PATTERN.matcher(raw);
            if (m.matches()) {
                final int r = Integer.valueOf(m.group(1));
                final int g = Integer.valueOf(m.group(2));
                final int b = Integer.valueOf(m.group(3));
                return rgbToHexString(r, g, b);
            }
        }
        final ColorName name = ColorName.lookup(raw);
        final Color color = null != name ? ColorName.lookup(raw).getColor() : null;
        if (null != color) {
            return rgbToHexString(color.getR(), color.getG(), color.getB());
        }
        throw new RuntimeException("RGB value cannot be parsed! [" + raw + "]");
    }

    public static String rgbToHexString(final int r, final int g, final int b) {
        return rgbToHexString(r, g, b, 1);
    }

    public static String rgbToHexString(final int r, final int g, final int b, final int a) {
        final int rgb = toRGB(r, g, b, a);
        return rgbToHexString(rgb);
    }

    public static String rgbToHexString(int rgb) {
        String hex = Integer.toHexString(rgb & 0xffffff);
        if (hex.length() < 6) {
            if (hex.length() == 5) {
                hex = "0" + hex;
            }
            if (hex.length() == 4) {
                hex = "00" + hex;
            }
            if (hex.length() == 3) {
                hex = "000" + hex;
            }
        }
        hex = "#" + hex;
        return toHexColorString(hex);
    }

    public static int toRGB(final int r,
                            final int g,
                            final int b,
                            final int a) {
        return ((a & 0xFF) << 24) |
                ((r & 0xFF) << 16) |
                ((g & 0xFF) << 8) |
                ((b & 0xFF) << 0);
    }

    private static double parseDouble(final String value) {
        return Double.parseDouble(value);
    }

    private static boolean isEmpty(final String s) {
        return StringUtils.isEmpty(s);
    }
}
