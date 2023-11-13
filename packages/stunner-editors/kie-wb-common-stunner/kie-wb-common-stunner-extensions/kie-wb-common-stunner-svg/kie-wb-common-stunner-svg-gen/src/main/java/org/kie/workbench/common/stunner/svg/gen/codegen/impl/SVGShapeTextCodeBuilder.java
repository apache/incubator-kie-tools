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


package org.kie.workbench.common.stunner.svg.gen.codegen.impl;

import org.kie.workbench.common.stunner.svg.gen.model.StyleDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.StyleSheetDefinition;

public class SVGShapeTextCodeBuilder {

    private static final String CSS_SELECTOR_TEXT = "#text";
    private static final String TITLE_ALPHA = ".setTitleAlpha(%1sd);";
    private static final String TITLE_FONT_FAMILY = ".setTitleFontFamily(\"%1s\");";
    private static final String TITLE_FONT_SIZE = ".setTitleFontSize(%1sd);";
    private static final String TITLE_FONT_COLOR = ".setTitleFontColor(\"%1s\");";
    private static final String TITLE_STROKE_WIDTH = ".setTitleStrokeWidth(%1sd);";
    private static final String TITLE_STROKE_COLOR = ".setTitleStrokeColor(\"%1s\");";

    public static String generate(final String viewInstanceName,
                                  final String viewId,
                                  final StyleSheetDefinition styleSheetDefinition) {
        final StyleDefinition globalStyle = styleSheetDefinition.getStyle(CSS_SELECTOR_TEXT);
        final StyleDefinition customStyle = styleSheetDefinition.getStyle("#" + viewId + " " + CSS_SELECTOR_TEXT);
        return null != globalStyle || null != customStyle ?
                generateTextSetters(viewInstanceName,
                                    globalStyle,
                                    customStyle) : "";
    }

    private static String generateTextSetters(final String viewInstanceName,
                                              final StyleDefinition globalStyle,
                                              final StyleDefinition customStyle) {
        String result = "";
        final Double alpha = null != customStyle ? customStyle.getAlpha() : globalStyle.getAlpha();
        if (null != alpha) {
            result += viewInstanceName + AbstractGenerator.formatDouble(TITLE_ALPHA,
                                                                        alpha);
        }
        final String fontFamily = null != customStyle ? customStyle.getFontFamily() : globalStyle.getFontFamily();
        if (null != fontFamily) {
            result += viewInstanceName + AbstractGenerator.formatString(TITLE_FONT_FAMILY,
                                                                        fontFamily);
        }
        final Double fontSize = null != customStyle ? customStyle.getFontSize() : globalStyle.getFontSize();
        if (null != fontSize) {
            result += viewInstanceName + AbstractGenerator.formatDouble(TITLE_FONT_SIZE,
                                                                        fontSize);
        }
        final String fontColor = null != customStyle ? customStyle.getFillColor() : globalStyle.getFillColor();
        if (null != fontColor) {
            result += viewInstanceName + AbstractGenerator.formatString(TITLE_FONT_COLOR,
                                                                        fontColor);
        }
        final Double strokeWidth = null != customStyle ? customStyle.getStrokeWidth() : globalStyle.getStrokeWidth();
        if (null != strokeWidth) {
            result += viewInstanceName + AbstractGenerator.formatDouble(TITLE_STROKE_WIDTH,
                                                                        strokeWidth);
        }
        final String strokeColor = null != customStyle ? customStyle.getStrokeColor() : globalStyle.getStrokeColor();
        if (null != strokeColor) {
            result += viewInstanceName + AbstractGenerator.formatString(TITLE_STROKE_COLOR,
                                                                        strokeColor);
        }
        return result;
    }
}
