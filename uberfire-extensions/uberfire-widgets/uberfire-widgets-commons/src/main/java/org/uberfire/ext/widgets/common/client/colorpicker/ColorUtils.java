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
package org.uberfire.ext.widgets.common.client.colorpicker;

final class ColorUtils {
	private ColorUtils() {}

	static int[] hsl2rgb(int[] hsl) {
		double h = hsl[0] / 360d;
		double s = hsl[1] / 100d;
		double l = hsl[2] / 100d;
		double r = 0d;
		double g = 0d;
		double b;
	
		if (s > 0d) {
			if (h >= 1d) {
				h = 0d;
			}
	
			h = h * 6d;
			double f = h - Math.floor(h);
			double a = Math.round(l * 255d * (1d - s));
			b = Math.round(l * 255d * (1d - (s * f)));
			double c = Math.round(l * 255d * (1d - (s * (1d - f))));
			l = Math.round(l * 255d);
	
			switch ((int) Math.floor(h)) {
				case 0:
					r = l;
					g = c;
					b = a;
					break;
				case 1:
					r = b;
					g = l;
					b = a;
					break;
				case 2:
					r = a;
					g = l;
					b = c;
					break;
				case 3:
					r = a;
					g = b;
					b = l;
					break;
				case 4:
					r = c;
					g = a;
					b = l;
					break;
				case 5:
					r = l;
					g = a;
					break;
			}
			return new int[] { (int) Math.round(r), (int) Math.round(g), (int) Math.round(b) };
		}
	
		l = Math.round(l * 255d);
		return new int[] { (int) l, (int) l, (int) l };
	}

	static String toHex(int v) {
		v = Math.min(Math.max(v, 0), 255);
		return String.valueOf("0123456789abcdef".charAt(((v - v % 16) / 16))) + //$NON-NLS-1$
			String.valueOf("0123456789abcdef".charAt(v % 16)); //$NON-NLS-1$
	}

	static String rgb2hex(int[] rgb) {
		return toHex(rgb[0]) + toHex(rgb[1]) + toHex(rgb[2]);
	}

	static String rgb2hex(int r, int g, int b) {
		return rgb2hex(new int[] { r, g, b });
	}

	static String hsl2hex(int[] hsl) {
		return rgb2hex(hsl2rgb(hsl));
	}

	static String hsl2hex(int h, int s, int l) {
		return hsl2hex(new int[] { h, s, l });
	}
	
	static int[] rgb2hsl(int[] rgb) {
		double max = Math.max(Math.max(rgb[0], rgb[1]), rgb[2]); // 0xdd = 221
		double delta = max - Math.min(Math.min(rgb[0], rgb[1]), rgb[2]); // 153
		double h = 0;
		int s = 0;
		int l = (int) Math.round(max * 100d / 255d); // 87 ok
		if (max != 0) {
			s = (int) Math.round(delta * 100d / max); // 69 ok
			if (max == rgb[0]) {
				h = (rgb[1] - rgb[2]) / delta;
			} else if (max == rgb[1]) {
				h = (rgb[2] - rgb[0]) / delta + 2d;
			} else {
				h = (rgb[0] - rgb[1]) / delta + 4d; // 4.8888888888
			}
			h = Math.min(Math.round(h * 60d), 360d); // 293
			if (h < 0d) {
				h += 360d;
			}
		}
		return new int[] { (int) Math.round(h), Math.round(s), l };
	}
	
	static int[] getRGB(String color) {
		return new int[] {
			Integer.parseInt(color.substring(0, 2), 16),
			Integer.parseInt(color.substring(2, 4), 16),
			Integer.parseInt(color.substring(4, 6), 16)
		};
	}
}
