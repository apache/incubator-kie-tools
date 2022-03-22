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

import org.kie.workbench.common.stunner.svg.gen.model.impl.CircleDefinition;
import org.kie.workbench.common.stunner.svg.gen.translator.SVGTranslatorContext;
import org.kie.workbench.common.stunner.svg.gen.translator.css.SVGAttributeParser;
import org.w3c.dom.Element;

public class SVGCircleTranslator extends AbstractSVGShapeTranslator<Element, CircleDefinition> {

    public static final String CX = "cx";
    public static final String CY = "cy";
    public static final String RADIUS = "r";

    @Override
    public Class<Element> getInputType() {
        return Element.class;
    }

    @Override
    public CircleDefinition doTranslate(final Element rectElement,
                                        final SVGTranslatorContext context) {
        final String radius = rectElement.getAttribute(RADIUS);
        return new CircleDefinition(getId(rectElement),
                                    SVGAttributeParser.toPixelValue(radius));
    }

    @Override
    protected String getXAttributeName() {
        return CX;
    }

    @Override
    protected String getYAttributeName() {
        return CY;
    }

    @Override
    public String getTagName() {
        return "circle";
    }
}
