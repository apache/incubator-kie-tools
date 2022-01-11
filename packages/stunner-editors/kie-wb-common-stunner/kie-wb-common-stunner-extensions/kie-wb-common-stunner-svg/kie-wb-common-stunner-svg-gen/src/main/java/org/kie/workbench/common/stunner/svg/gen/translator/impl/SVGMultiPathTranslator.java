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

import org.kie.workbench.common.stunner.svg.gen.exception.TranslatorException;
import org.kie.workbench.common.stunner.svg.gen.model.impl.MultiPathDefinition;
import org.kie.workbench.common.stunner.svg.gen.translator.SVGTranslatorContext;
import org.w3c.dom.Element;

public class SVGMultiPathTranslator extends AbstractSVGShapeTranslator<Element, MultiPathDefinition> {

    public static final String PATH = "d";

    @Override
    public Class<Element> getInputType() {
        return Element.class;
    }

    @Override
    public MultiPathDefinition doTranslate(final Element pathElement,
                                           final SVGTranslatorContext context) throws TranslatorException {

        final String id = getId(pathElement);
        String path = pathElement.getAttribute(PATH);
        failIfEmpty(PATH,
                    path);
        context.addStaticStringMember(id, path);
        return new MultiPathDefinition(id,
                                       path);
    }

    // The spec says no x/y for paths, does not make sense.
    @Override
    protected void translatePosition(final Element element,
                                     final MultiPathDefinition def,
                                     final SVGTranslatorContext context) {
        def.setX(0d);
        def.setY(0d);
    }

    @Override
    public String getTagName() {
        return "path";
    }
}
