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

package org.kie.workbench.common.stunner.svg.gen.translator.impl;

import org.kie.workbench.common.stunner.svg.gen.model.impl.RectDefinition;
import org.kie.workbench.common.stunner.svg.gen.translator.SVGTranslatorContext;
import org.kie.workbench.common.stunner.svg.gen.translator.css.SVGAttributeParser;
import org.w3c.dom.Element;

public class SVGRectTranslator extends AbstractSVGShapeTranslator<Element, RectDefinition> {

    public static final String WIDTH = "width";
    public static final String HEIGHT = "height";
    public static final String RX = "rx";
    public static final String RY = "ry";

    @Override
    public Class<Element> getInputType() {
        return Element.class;
    }

    @Override
    public RectDefinition doTranslate(final Element rectElement,
                                      final SVGTranslatorContext context) {
        final String rx = rectElement.getAttribute(RX);
        final String ry = rectElement.getAttribute(RY);
        final String width = rectElement.getAttribute(WIDTH);
        final String height = rectElement.getAttribute(HEIGHT);
        return new RectDefinition(getId(rectElement),
                                  SVGAttributeParser.toPixelValue(width),
                                  SVGAttributeParser.toPixelValue(height),
                                  getCornerRadius(rx,
                                                  ry));
    }

    @Override
    public String getTagName() {
        return "rect";
    }

    private double getCornerRadius(final String rx,
                                   final String ry) {
        final double cx = SVGAttributeParser.toPixelValue(rx,
                                                          0d);
        final double cy = SVGAttributeParser.toPixelValue(ry,
                                                          0d);
        return cx > cy ? cx : cy;
    }
}
