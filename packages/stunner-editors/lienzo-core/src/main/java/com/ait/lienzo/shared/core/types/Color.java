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

import com.ait.lienzo.client.core.types.ColorKeyRotor;
import com.ait.lienzo.client.core.util.StringFormatter;

/**
 * Color implements the {@link IColor} interface (just like {@link ColorName})
 * so it can be used in all the Lienzo methods that required colors.
 * <p>
 * Internally, it stores Red, Green and Blue (RGB) values as integers between 0 and 255.
 * It stores an Alpha value (a.k.a. opacity) as a double between 0 and 1, where 0 is invisible and 1 is fully visible.
 * <p>
 * This class provides additional utility methods, e.g.:
 * <ul>
 *  <li>{@link #fromHSL(double, double, double) fromHSL} - convert colors from the HSL model to the RGB model
 *  <li>{@link #fromColorString(String) fromColorString} - converts any CSS3 compliant color string to an RGB Color
 * </ul>
 *
 * @see IColor
 * @see ColorName
 * @see <a href="http://www.w3.org/TR/css3-color/">CSS Color Module Level 3</a>
 */
public class Color implements IColor {

    private int m_r;

    private int m_g;

    private int m_b;

    private static int s_r = 0;

    private static int s_g = 0;

    private static int s_b = 0;

    private double m_a = 1.0;

    /**
     * Constructs a Color from RGB values.
     * The RGB values are normalized to [0,255].
     * The alpha value (A) is set to 1.
     *
     * @param r int between 0 and 255
     * @param g int between 0 and 255
     * @param b int between 0 and 255
     */
    public Color(final int r, final int g, final int b) {
        setR(r);

        setG(g);

        setB(b);
    }

    /**
     * Constructs a Color from RGB values and alpha (transparency).
     * The RGB values are normalized to [0,255].
     * Alpha is normalized to [0,1]
     *
     * @param r int between 0 and 255
     * @param g int between 0 and 255
     * @param b int between 0 and 255
     * @param a double between 0 and 1
     */
    public Color(final int r, final int g, final int b, final double a) {
        setR(r);

        setG(g);

        setB(b);

        setA(a);
    }

    public Color brightness(final double brightness) {
        int r = calculateBrightnessScale(getR(), brightness);

        int g = calculateBrightnessScale(getG(), brightness);

        int b = calculateBrightnessScale(getB(), brightness);

        return new Color(r, g, b, getA());
    }

    private static int calculateBrightnessScale(final int color, final double brightness) {
        return (int) Math.max(Math.min((color + (brightness * 255) + 0.5), 255), 0);
    }

    public Color percent(double percent) {
        if (percent < -1) {
            return new Color(0, 0, 0, getA());
        }
        if (percent > 1) {
            return new Color(255, 255, 255, getA());
        }
        if (percent < 0) {
            percent = 1 + percent;

            int r = Math.max(0, Math.min(255, (int) ((getR() * percent) + 0.5)));

            int g = Math.max(0, Math.min(255, (int) ((getG() * percent) + 0.5)));

            int b = Math.max(0, Math.min(255, (int) ((getB() * percent) + 0.5)));

            return new Color(r, g, b, getA());
        } else if (percent > 0) {
            int r = Math.max(0, Math.min(255, (int) (((255 - getR()) * percent) + getR() + 0.5)));

            int g = Math.max(0, Math.min(255, (int) (((255 - getG()) * percent) + getG() + 0.5)));

            int b = Math.max(0, Math.min(255, (int) (((255 - getB()) * percent) + getB() + 0.5)));

            return new Color(r, g, b, getA());
        }
        return new Color(getR(), getG(), getB(), getA());
    }

    /**
     * Generates a unique RGB color key, e.g. "rgb(12,34,255)".
     *
     * @return String
     * @Deprecated This is used internally. As public API use {@link Color#getRGB()} method.
     * <p>
     * Note: Replaced by {@link ColorKeyRotor#next()} method.
     */
    @Deprecated
    public static final String getHEXColorKey() {
        s_r++;

        if (s_r == 256) {
            s_r = 0;

            s_g++;

            if (s_g == 256) {
                s_g = 0;

                s_b++;

                if (s_b == 256) {
                    s_b = 0;

                    return getHEXColorKey();
                }
            }
        }
        return rgbToBrowserHexColor(s_r, s_g, s_b);
    }

    /**
     * Converts RGB integer values to a browser-compliance rgb format.
     *
     * @param r int between 0 and 255
     * @param g int between 0 and 255
     * @param b int between 0 and 255
     * @return String e.g. "rgb(12,34,255)"
     */
    public static final String toBrowserRGB(final int r, final int g, final int b) {
        return "rgb(" + fixRGB(r) + "," + fixRGB(g) + "," + fixRGB(b) + ")";
    }

    /**
     * Converts RGBA values to a browser-compliance rgba format.
     *
     * @param r int between 0 and 255
     * @param g int between 0 and 255
     * @param b int between 0 and 255
     * @param b double between 0 and 1
     * @return String e.g. "rgba(12,34,255,0.5)"
     */
    public static final String toBrowserRGBA(final int r, final int g, final int b, final double a) {
        return "rgba(" + fixRGB(r) + "," + fixRGB(g) + "," + fixRGB(b) + "," + fixAlpha(a) + ")";
    }

    /**
     * Converts HSL (hue, saturation, lightness) to RGB Color.
     * HSL values are not normalized yet.
     *
     * @param h in [0,360] degrees
     * @param s in [0,100] percent
     * @param l in [0,100] percent
     * @return Color with RGB values
     */
    public static final Color fromHSL(double h, double s, double l) {
        h = (((h % 360) + 360) % 360) / 360;

        s = convertPercents(s);

        l = convertPercents(l);

        return fromNormalizedHSL(h, s, l);
    }

    private static double convertPercents(final double value) {
        if (value < 0) {
            return 0;
        } else if (value > 100) {
            return 1;
        }
        return value / 100;
    }

    /**
     * Converts HSL (hue, saturation, lightness) to RGB.
     * HSL values should already be normalized to [0,1]
     *
     * @param h in [0,1]
     * @param s in [0,1]
     * @param l in [0,1]
     * @return Color with RGB values
     */
    public static final Color fromNormalizedHSL(final double h, final double s, final double l) {
        // see http://www.w3.org/TR/css3-color/
        //
        // HOW TO RETURN hsl.to.rgb(h, s, l):
        // SELECT:
        // l<=0.5: PUT l*(s+1) IN m2
        // ELSE: PUT l+s-l*s IN m2
        // PUT l*2-m2 IN m1
        // PUT hue.to.rgb(m1, m2, h+1/3) IN r
        // PUT hue.to.rgb(m1, m2, h ) IN g
        // PUT hue.to.rgb(m1, m2, h-1/3) IN b
        // RETURN (r, g, b)

        final double m2 = (l <= 0.5) ? (l * (s + 1)) : (l + s - l * s);

        final double m1 = l * 2 - m2;

        return new Color(fixRGB((int) Math.round(255 * hueToRGB(m1, m2, h + 1.0 / 3))), fixRGB((int) Math.round(255 * hueToRGB(m1, m2, h))), fixRGB((int) Math.round(255 * hueToRGB(m1, m2, h - 1.0 / 3))));
    }

    /**
     * Parses a CSS color string and returns a Color object.
     *
     * @param cssColorString Any valid color string for use in HTML 5 canvas
     *                       (as defined by "CSS Color Module Level 3")
     *                       except for "inherit" and "currentcolor".
     * @return null if cssColorString could not be parsed
     * @see <a href="http://www.w3.org/TR/css3-color/">CSS Color Module Level 3</a>
     */
    public static Color fromColorString(final String cssColorString) {
        String str = cssColorString.toLowerCase().replaceAll(" ", "");

        try {
            if (str.startsWith("#")) {
                return hex2RGB(str);
            } else {
                if (str.endsWith(")")) {
                    if (str.startsWith("rgb(")) {
                        final String[] rgb = str.substring(4, str.length() - 1).split(",");

                        if (rgb.length != 3) {
                            return null;
                        }
                        final int r = intOrPct(rgb[0], 255);

                        final int g = intOrPct(rgb[1], 255);

                        final int b = intOrPct(rgb[2], 255);

                        return new Color(r, g, b);
                    }
                    if (str.startsWith("rgba(")) {
                        final String[] rgba = str.substring(5, str.length() - 1).split(",");

                        if (rgba.length != 4) {
                            return null;
                        }
                        final int r = intOrPct(rgba[0], 255);

                        final int g = intOrPct(rgba[1], 255);

                        final int b = intOrPct(rgba[2], 255);

                        final double a = doubleOrPct(rgba[3], 1);

                        return new Color(r, g, b, a);
                    }
                    if (str.startsWith("hsl(")) {
                        final String[] hsl = str.substring(4, str.length() - 1).split(",");

                        if (hsl.length != 3) {
                            return null;
                        }
                        final double h = hueOrPct(hsl[0]);

                        final double s = percentage(hsl[1], 1);

                        final double l = percentage(hsl[2], 1);

                        return fromNormalizedHSL(h, s, l);
                    }
                    if (str.startsWith("hsla(")) {
                        final String[] hsla = str.substring(5, str.length() - 1).split(",");

                        if (hsla.length != 4) {
                            return null;
                        }
                        final double h = hueOrPct(hsla[0]);

                        final double s = percentage(hsla[1], 1);

                        final double l = percentage(hsla[2], 1);

                        final double a = doubleOrPct(hsla[3], 1);

                        final Color col = fromNormalizedHSL(h, s, l);

                        col.setA(a);

                        return col;
                    }
                }
                final ColorName name = ColorName.lookup(str);

                if (name != null) {
                    return name.getColor();
                }
            }
            return null;// unknown format
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static final int intOrPct(String s, int max) {
        if (s.endsWith("%")) {
            s = s.substring(0, s.length() - 1);

            double val = Double.parseDouble(s);

            if (val < 0) {
                return 0;
            }

            if (val >= 100) {
                return max;
            }

            return (int) Math.round(val * max / 100.0);
        } else {
            int val = Integer.parseInt(s);

            if (val < 0) {
                return 0;
            }

            if (val > max) {
                return max;
            }

            return val;
        }
    }

    private static final double percentage(String s, double max) {
        if (s.endsWith("%")) {
            s = s.substring(0, s.length() - 1);

            double val = Double.parseDouble(s);

            if (val < 0) {
                return 0;
            }

            if (val >= 100) {
                return max;
            }

            return val * max / 100.0;
        }
        throw new IllegalArgumentException("invalid percentage [" + s + "]");
    }

    private static final double doubleOrPct(String s, double max) {
        if (s.endsWith("%")) {
            return percentage(s, max);
        } else {
            double val = Double.parseDouble(s);

            if (val < 0) {
                return 0;
            }

            if (val > max) {
                return max;
            }

            return val;
        }
    }

    private static final double hueOrPct(String s) {
        if (s.endsWith("%")) {
            return percentage(s, 1);
        } else {
            double h = Double.parseDouble(s);

            h = (((h % 360) + 360) % 360);

            return h / 360;
        }
    }

    /**
     * Generates a random hex color, e.g. "#1234EF"
     *
     * @return String
     */
    public static final String getRandomHexColor() {
        int r = randomRGB();

        int g = randomRGB();

        int b = randomRGB();

        return rgbToBrowserHexColor(r, g, b);
    }

    private static final int randomRGB() {
        return (int) Math.round(Math.random() * 255);
    }

    /**
     * Converts RGB to hex browser-compliance color, e.g. "#1234EF"
     *
     * @param r int between 0 and 255
     * @param g int between 0 and 255
     * @param b int between 0 and 255
     * @return String
     */
    public static final String rgbToBrowserHexColor(final int r, final int g, final int b) {
        return "#" + toBrowserHexValue(r) + toBrowserHexValue(g) + toBrowserHexValue(b);
    }

    /**
     * Converts Hex string to RGB. Assumes
     *
     * @param hex String of length 7, e.g. "#1234EF"
     * @return {@link Color}
     */
    public static final Color hex2RGB(final String hex) {
        String r;
        String g;
        String b;

        final int len = hex.length();

        if (len == 7) {
            r = hex.substring(1, 3);

            g = hex.substring(3, 5);

            b = hex.substring(5, 7);
        } else if (len == 4) {
            r = hex.substring(1, 2);

            g = hex.substring(2, 3);

            b = hex.substring(3, 4);

            r = r + r;

            g = g + g;

            b = b + b;
        } else {
            return null;// error - invalid length
        }
        try {
            return new Color(Integer.valueOf(r, 16), Integer.valueOf(g, 16), Integer.valueOf(b, 16));
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    /**
     * Returns the Red component of the RGB color.
     *
     * @return int between 0 and 255
     */
    @Override
    public int getR() {
        return m_r;
    }

    /**
     * Sets the Red component of the RGB color.
     * The value is normalized to [0,255].
     *
     * @param r int between 0 and 255
     * @return this Color
     */
    public Color setR(final int r) {
        m_r = fixRGB(r);

        return this;
    }

    /**
     * Returns the Green component of the RGB color.
     *
     * @return int between 0 and 255
     */
    @Override
    public int getG() {
        return m_g;
    }

    /**
     * Sets the Green component of the RGB color.
     * The value is normalized to [0,255].
     *
     * @param g int between 0 and 255
     * @return this Color
     */
    public Color setG(final int g) {
        m_g = fixRGB(g);

        return this;
    }

    /**
     * Returns the Blue component of the RGB color.
     *
     * @return int between 0 and 255
     */
    @Override
    public int getB() {
        return m_b;
    }

    /**
     * Sets the Blue component of the RGB color.
     * The value is normalized to [0,255].
     *
     * @param b int between 0 and 255
     * @return this Color
     */
    public Color setB(final int b) {
        m_b = fixRGB(b);

        return this;
    }

    /**
     * Returns the Alpha component (transparency) of the RGB color, between 0 and 1.
     *
     * @return double between 0 and 1
     */
    @Override
    public double getA() {
        return m_a;
    }

    /**
     * Sets the alpha channel.
     * The value is normalized to [0,1].
     *
     * @param a between 0 and 1
     * @return this Color
     */
    public Color setA(final double a) {
        m_a = fixAlpha(a);

        return this;
    }

    /**
     * Returns an RGB color string, e.g. "rgb(255,255,255)"
     *
     * @return String
     */
    public String getRGB() {
        return "rgb(" + getR() + "," + getG() + "," + getB() + ")";
    }

    /**
     * Returns RGBA color string, e.g. "rgba(255,255,255,0.5)
     *
     * @return String
     */
    public String getRGBA() {
        return "rgba(" + getR() + "," + getG() + "," + getB() + "," + getA() + ")";
    }

    /**
     * Returns a CCS compliant color string that can be set as a color on
     * an HTML5 canvas, e.g. "rgb(255,255,255)" if alpha is 1, or
     * "rgba(255,255,255,0.2)" otherwise.
     *
     * @return String e.g. "rgb(255,255,255)", "rgba(255,255,255,0.2)"
     */
    @Override
    public String getColorString() {
        if (getA() == 1) {
            return getRGB();
        }
        return getRGBA();
    }

    /**
     * Converts the number to a two-digit hex string,
     * e.g. 0 becomes "00" and 255 becomes "FF".
     *
     * @param number int between 0 and 255
     * @return String
     */
    private static final String toBrowserHexValue(final int number) {
        final String chex = Integer.toHexString(fixRGB(number) & 0xFF).toUpperCase();

        if (chex.length() < 2) {
            return "0" + chex;
        }
        return chex;
    }

    private static int fixRGB(final int c) {
        if (c < 0) {
            return 0;
        }
        if (c > 255) {
            return 255;
        }
        return c;
    }

    private static double fixAlpha(final double a) {
        if (a < 0) {
            return 0;
        }
        if (a > 1.0) {
            return 1.0;
        }
        return a;
    }

    /**
     * Used by {@link #fromNormalizedHSL(double, double, double)}
     *
     * @param m1
     * @param m2
     * @param h
     * @return
     */
    private static double hueToRGB(double m1, double m2, double h) {
        // see http://www.w3.org/TR/css3-color/
        //
        // HOW TO RETURN hue.to.rgb(m1, m2, h):
        // IF h<0: PUT h+1 IN h
        // IF h>1: PUT h-1 IN h
        // IF h*6<1: RETURN m1+(m2-m1)*h*6
        // IF h*2<1: RETURN m2
        // IF h*3<2: RETURN m1+(m2-m1)*(2/3-h)*6
        // RETURN m1

        if (h < 0) {
            h++;
        }

        if (h > 1) {
            h--;
        }

        if (h * 6 < 1) {
            return m1 + (m2 - m1) * h * 6;
        }

        if (h * 2 < 1) {
            return m2;
        }

        if (h * 3 < 2) {
            return m1 + (m2 - m1) * (2.0 / 3 - h) * 6;
        }

        return m1;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Color)) {
            return false;
        }
        if (this == other) {
            return true;
        }
        Color that = ((Color) other);

        return (that.getR() == getR()) && (that.getG() == getG()) && (that.getB() == getB()) && (that.getA() == getA());
    }

    @Override
    public int hashCode() {
        return getRGBA().hashCode();
    }

    @Override
    public String toString() {
        return "Color{r=" + m_r + ", g=" + m_g + ", b=" + m_b + '}';
    }

    public final HSL getHSL() {
        return getHSLFromRGB(getR(), getG(), getB());
    }

    public static final class HSL {

        private final double m_h;

        private final double m_s;

        private final double m_l;

        public HSL(final double h, final double s, final double l) {
            m_h = h;

            m_s = s;

            m_l = l;
        }

        public final double getH() {
            return m_h;
        }

        public final double getS() {
            return m_s;
        }

        public final double getL() {
            return m_l;
        }

        @Override
        public final String toString() {
            return "hsl(" + getH() + "," + getS() + "," + getL() + ")";
        }

        public final String toBrowserHSL() {
            return "hsl(" + StringFormatter.toFixed(getH() * 360, 1) + "," + StringFormatter.toFixed(getS() * 100, 1) + "%," + StringFormatter.toFixed(getL() * 100, 1) + "%)";
        }
    }

    public static final HSL getHSLFromRGB(double r, double g, double b) {
        r = (r / 255.0);

        g = (g / 255.0);

        b = (b / 255.0);

        final double vmin = Math.min(r, Math.min(g, b));

        final double vmax = Math.max(r, Math.max(g, b));

        final double diff = vmax - vmin;

        double h = 0;

        double s = 0;

        final double l = (vmax + vmin) / 2.0;

        if (diff != 0) {
            if (l < 0.5) {
                s = diff / (vmax + vmin);
            } else {
                s = diff / (2 - vmax - vmin);
            }
            final double delr = (((vmax - r) / 6.0) + (diff / 2.0)) / diff;

            final double delg = (((vmax - g) / 6.0) + (diff / 2.0)) / diff;

            final double delb = (((vmax - b) / 6.0) + (diff / 2.0)) / diff;

            if (r == vmax) {
                h = delb - delg;
            } else if (g == vmax) {
                h = (1.0 / 3.0) + delr - delb;
            } else if (b == vmax) {
                h = (2.0 / 3.0) + delg - delr;
            }
            if (h < 0) {
                h += 1;
            }
            if (h > 1) {
                h -= 1;
            }
        }
        return new HSL(h, s, l);
    }
}
