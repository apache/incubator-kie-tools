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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.steadystate.css.dom.CSSStyleRuleImpl;
import com.steadystate.css.dom.CSSStyleSheetImpl;
import com.steadystate.css.parser.CSSOMParser;
import com.steadystate.css.parser.SACParserCSS3;
import org.apache.commons.lang3.StringUtils;
import org.kie.workbench.common.stunner.svg.gen.exception.TranslatorException;
import org.kie.workbench.common.stunner.svg.gen.model.StyleDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.StyleSheetDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.TransformDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.impl.StyleDefinitionImpl;
import org.kie.workbench.common.stunner.svg.gen.model.impl.TransformDefinitionImpl;
import org.w3c.css.sac.InputSource;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleDeclaration;

public class SVGStyleTranslator {

    public static final String ID = "id";
    public static final String OPACITY = "opacity";
    public static final String FILL = "fill";
    public static final String FILL_OPACITY = "fill-opacity";
    public static final String STROKE = "stroke";
    public static final String STROKE_OPACITY = "stroke-opacity";
    public static final String STROKE_WIDTH = "stroke-width";
    public static final String FONT_FAMILY = "font-family";
    public static final String STROKE_DASHARRAY = "stroke-dasharray";
    public static final String FONT_SIZE = "font-size";
    public static final String STYLE = "style";
    public static final String CSS_CLASS = "class";
    public static final String TRANSFORM = "transform";
    public static final String ATTR_VALUE_NONE = "none";
    public static final String[] ATTR_NAMES = new String[]{
            OPACITY, FILL, FILL_OPACITY, STROKE, STROKE_OPACITY, STROKE_WIDTH, STROKE_DASHARRAY, FONT_FAMILY, FONT_SIZE
    };
    private static final String PATTERN_CLASSNAME_SEPARATOR = "\\s+";
    private static final String TRANSFORM_SCALE = "scale";
    private static final String TRANSFORM_TRANSLATE = "translate";
    private static final Pattern TRANSFORM_PATTERN = Pattern.compile("(.*)\\((.*),(.*)\\)");

    public static TransformDefinition parseTransformDefinition(final Element element) throws TranslatorException {
        final String transformRaw = element.getAttribute(TRANSFORM);
        if (!isEmpty(transformRaw)) {
            final double[] t = parseTransform(transformRaw);
            return new TransformDefinitionImpl(t[0],
                                               t[1],
                                               t[2],
                                               t[3]);
        }
        return new TransformDefinitionImpl();
    }

    public static StyleSheetDefinition parseStyleSheetDefinition(final String cssPath,
                                                                 final InputStream cssStream) throws TranslatorException {
        final CSSStyleSheetImpl sheet = parseStyleSheet(new InputSource(new InputStreamReader(cssStream)));
        final CSSRuleList cssRules = sheet.getCssRules();
        final StyleSheetDefinition result = new StyleSheetDefinition(cssPath);
        for (int i = 0; i < cssRules.getLength(); i++) {
            final CSSRule item = cssRules.item(i);
            if (CSSRule.STYLE_RULE == item.getType()) {
                final CSSStyleRuleImpl rule = (CSSStyleRuleImpl) item;
                final String selectorText = rule.getSelectorText();
                final CSSStyleDeclaration declaration = rule.getStyle();
                final StyleDefinition styleDefinition = parseStyleDefinition(declaration);
                result.addStyle(selectorText, styleDefinition);
            }
        }
        return result;
    }

    // For now only single declaration support, the first one found.
    public static StyleDefinition parseStyleDefinition(final Element element,
                                                       final String svgId,
                                                       final StyleSheetDefinition styleSheetDefinition) throws TranslatorException {
        // Parse from the css class declarations, if a global stylesheet is present.
        final String cssClassRaw = null != styleSheetDefinition ?
                getStyleDeclaration(element) :
                null;
        if (!isEmpty(cssClassRaw)) {
            final List<String> elementSelectors = parseAllSelectors(element);
            final List<String> selectors = new ArrayList<>(elementSelectors);
            for (String elementSelector : elementSelectors) {
                selectors.add("#" + svgId + " " + elementSelector);
            }
            StyleDefinition style = styleSheetDefinition.getStyle(selectors);
            return null != style ? style : createDefaultStyleDefinition();
        } else {
            // Parse styles from element attributes.
            StringBuilder builder = new StringBuilder();
            for (final String key : ATTR_NAMES) {
                final String value = element.getAttribute(key);
                if (!isEmpty(value)) {
                    builder.append(key).append(": ").append(value).append("; ");
                }
            }
            // Parse styles from element's style declaration.
            final String styleRaw = element.getAttribute(STYLE);
            if (!isEmpty(styleRaw)) {
                builder.append(styleRaw);
            }
            if (0 < builder.length()) {
                return parseElementStyleDefinition(builder.toString());
            }
        }
        // Return default styles.
        return createDefaultStyleDefinition();
    }

    public static String[] getClassNames(final Element element) {
        final String raw = getStyleDeclaration(element);
        if (!isEmpty(raw)) {
            return raw.split(PATTERN_CLASSNAME_SEPARATOR);
        }
        return null;
    }

    private static StyleDefinition createDefaultStyleDefinition() {
        return new StyleDefinitionImpl.Builder().build();
    }

    private static double[] parseTransform(final String raw) throws TranslatorException {
        double sx = 1;
        double sy = 1;
        double tx = 0;
        double ty = 0;
        final String[] split = raw.split(" ");
        for (final String transformDec : split) {
            final Matcher m = TRANSFORM_PATTERN.matcher(transformDec);
            if (m.matches()) {
                final String op = m.group(1).trim();
                final String x = m.group(2).trim();
                final String y = m.group(3).trim();
                switch (op) {
                    case TRANSFORM_SCALE:
                        sx = SVGAttributeParser.toPixelValue(x);
                        sy = SVGAttributeParser.toPixelValue(y);
                        break;
                    case TRANSFORM_TRANSLATE:
                        tx = SVGAttributeParser.toPixelValue(x);
                        ty = SVGAttributeParser.toPixelValue(y);
                        break;
                }
            } else {
                throw new TranslatorException("Unrecognized transform attribute value format [" + raw + "]");
            }
        }
        return new double[]{sx, sy, tx, ty};
    }

    private static StyleDefinition parseElementStyleDefinition(final String styleRaw) throws TranslatorException {
        final CSSStyleSheetImpl sheet = parseElementStyleSheet(styleRaw);
        final CSSRuleList cssRules = sheet.getCssRules();
        for (int i = 0; i < cssRules.getLength(); i++) {
            final CSSRule item = cssRules.item(i);
            if (CSSRule.STYLE_RULE == item.getType()) {
                final CSSStyleRuleImpl rule = (CSSStyleRuleImpl) item;
                final CSSStyleDeclaration declaration = rule.getStyle();
                return parseStyleDefinition(declaration);
            }
        }
        return null;
    }

    private static CSSStyleSheetImpl parseElementStyleSheet(final String style) throws TranslatorException {
        final String declaration = ".shape { " + style + "}";
        return parseStyleSheet(new InputSource(new StringReader(declaration)));
    }

    private static CSSStyleSheetImpl parseStyleSheet(final InputSource source) throws TranslatorException {
        try {
            CSSOMParser parser = new CSSOMParser(new SACParserCSS3());
            return (CSSStyleSheetImpl) parser.parseStyleSheet(source,
                                                              null,
                                                              null);
        } catch (final IOException e) {
            throw new TranslatorException("Exception while parsing some style defintion.",
                                          e);
        }
    }

    private static StyleDefinition parseStyleDefinition(final CSSStyleDeclaration declaration) {
        final StyleDefinitionImpl.Builder builder = new StyleDefinitionImpl.Builder();
        boolean isFillNone = false;
        boolean isStrokeNone = false;
        for (int j = 0; j < declaration.getLength(); j++) {
            final String property = declaration.item(j).trim();
            final String value = declaration.getPropertyValue(property).trim();
            switch (property) {
                case OPACITY:
                    builder.setAlpha(SVGAttributeParser.toPixelValue(value));
                    break;
                case FILL:
                    if (ATTR_VALUE_NONE.equals(value)) {
                        isFillNone = true;
                    } else {
                        builder.setFillColor(SVGAttributeParser.toHexColorString(value));
                    }
                    break;
                case FILL_OPACITY:
                    builder.setFillAlpha(SVGAttributeParser.toPixelValue(value));
                    break;
                case STROKE:
                    if (ATTR_VALUE_NONE.equals(value)) {
                        isStrokeNone = true;
                    } else {
                        builder.setStrokeColor(SVGAttributeParser.toHexColorString(value));
                    }
                    break;
                case STROKE_OPACITY:
                    builder.setStrokeAlpha(SVGAttributeParser.toPixelValue(value));
                    break;
                case STROKE_WIDTH:
                    builder.setStrokeWidth(SVGAttributeParser.toPixelValue(value));
                    break;
                case STROKE_DASHARRAY:
                    builder.setStrokeDashArray(value);
                    break;
                case FONT_FAMILY:
                    builder.setFontFamily(value.trim());
                    break;
                case FONT_SIZE:
                    builder.setFontSize(SVGAttributeParser.toPixelValue(value));
                    break;
            }
        }
        if (isFillNone) {
            builder.setFillAlpha(0);
        }
        if (isStrokeNone) {
            builder.setStrokeAlpha(0);
        }
        return builder.build();
    }

    static List<String> parseAllSelectors(final Element element) {
        final List<Element> elements = getElementsTree(element);
        final List<String> result = new LinkedList<>();
        for (final Element candidate : elements) {
            Collection<String> selectors = parseElementSelectors(candidate);
            if (result.isEmpty()) {
                result.addAll(selectors);
            } else {
                ArrayList<String> parentSelectors = new ArrayList<>(result);
                for (String selector : selectors) {
                    for (String parentSelector : parentSelectors) {
                        result.add(selector + " " + parentSelector);
                    }
                }
            }
        }
        return result;
    }

    static List<Element> getElementsTree(final Element element) {
        final List<Element> tree = new LinkedList<>();
        tree.add(element);
        Node parent = element.getParentNode();
        while (null != parent) {
            if (parent instanceof Element) {
                tree.add((Element) parent);
            }
            parent = parent.getParentNode();
        }
        return tree;
    }

    static Collection<String> parseElementSelectors(final Element element) {
        final List<String> result = new LinkedList<>();
        // Class selectors.
        final String cssClassRaw = getStyleDeclaration(element);
        if (!isEmpty(cssClassRaw)) {
            Arrays.stream(cssClassRaw.split(PATTERN_CLASSNAME_SEPARATOR))
                    .map(c -> "." + c)
                    .forEach(result::add);
        }

        // Id selector.
        final String id = element.getAttribute(ID);
        if (!isEmpty(id)) {
            result.add("#" + id);
        }
        return result;
    }

    private static String getStyleDeclaration(final Element element) {
        return element.getAttribute(CSS_CLASS);
    }

    private static boolean isEmpty(final String s) {
        return StringUtils.isEmpty(s);
    }
}
