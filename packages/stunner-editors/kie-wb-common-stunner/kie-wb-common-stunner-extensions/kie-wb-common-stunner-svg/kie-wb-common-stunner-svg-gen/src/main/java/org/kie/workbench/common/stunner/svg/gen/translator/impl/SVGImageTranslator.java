/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import org.kie.workbench.common.stunner.svg.gen.model.impl.ImageDefinition;
import org.kie.workbench.common.stunner.svg.gen.translator.SVGTranslatorContext;
import org.w3c.dom.Element;

public class SVGImageTranslator extends AbstractSVGShapeTranslator<Element, ImageDefinition> {

    public static final String HREF = "href";

    @Override
    public Class<Element> getInputType() {
        return Element.class;
    }

    @Override
    public ImageDefinition doTranslate(final Element rectElement,
                                       final SVGTranslatorContext context) {
        final String href = rectElement.getAttribute(HREF);
        return new ImageDefinition(getId(rectElement),
                                   href);
    }

    @Override
    public String getTagName() {
        return "image";
    }
}
