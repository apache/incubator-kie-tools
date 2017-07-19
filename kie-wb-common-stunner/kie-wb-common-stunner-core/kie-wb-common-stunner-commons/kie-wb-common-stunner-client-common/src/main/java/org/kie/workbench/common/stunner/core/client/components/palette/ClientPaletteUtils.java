/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.components.palette;

import java.util.List;

import org.kie.workbench.common.stunner.core.client.components.palette.model.GlyphPaletteItem;

public class ClientPaletteUtils {

    public static String getLongestText(final List<GlyphPaletteItem> paletteItems) {
        if (null == paletteItems || paletteItems.isEmpty()) {
            return null;
        }
        String longestTitle = "";
        for (final GlyphPaletteItem item : paletteItems) {
            final String iTitle = item.getTitle();
            if (null != iTitle && iTitle.length() > longestTitle.length()) {
                longestTitle = iTitle;
            }
        }
        return longestTitle.length() > 0 ? longestTitle : null;
    }

    public static double[] computeSizeForVerticalLayout(final int itemsSize,
                                                        final int iconSize,
                                                        final int padding,
                                                        final int textLength) {
        return computeSizeForLayout(itemsSize,
                                    iconSize,
                                    padding,
                                    textLength,
                                    true);
    }

    public static double[] computeSizeForHorizontalLayout(final int itemsSize,
                                                          final int iconSize,
                                                          final int padding,
                                                          final int textLength) {
        return computeSizeForLayout(itemsSize,
                                    iconSize,
                                    padding,
                                    textLength,
                                    false);
    }

    public static double computeFontSize(final double width,
                                         final double height,
                                         final int textLength) {
        // TODO
        return 10;
    }

    public static double[] computeFontBoundingBoxSize(final double fontSize,
                                                      final int textLength) {
        // TODO
        return new double[]{100, 50};
    }

    private static double[] computeSizeForLayout(final int itemsSize,
                                                 final int iconSize,
                                                 final int padding,
                                                 final int textLength,
                                                 final boolean verticalLayout) {
        double width = 0;
        double height = 0;
        final double fixedSize = iconSize + (padding * 2);
        final double dynSize = (iconSize * itemsSize) + (padding * 2 * itemsSize);
        width = verticalLayout ? fixedSize : dynSize;
        height = verticalLayout ? dynSize : fixedSize;
        if (textLength > 0) {
            final double fontSize = computeFontSize(fixedSize,
                                                    fixedSize,
                                                    textLength);
            final double[] fontBBSize = computeFontBoundingBoxSize(fontSize,
                                                                   textLength);
            width += verticalLayout ? fontBBSize[0] : 0;
            height += !verticalLayout ? fontBBSize[1] : 0;
        }
        return new double[]{width, height};
    }
}
