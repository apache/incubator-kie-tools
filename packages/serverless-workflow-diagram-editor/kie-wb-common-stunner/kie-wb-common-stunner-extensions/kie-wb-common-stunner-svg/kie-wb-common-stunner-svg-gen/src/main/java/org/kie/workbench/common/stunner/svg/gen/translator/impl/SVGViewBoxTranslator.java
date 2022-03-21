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
import org.kie.workbench.common.stunner.svg.gen.model.ViewDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.impl.ViewBoxDefinitionImpl;
import org.kie.workbench.common.stunner.svg.gen.translator.css.SVGAttributeParser;

/**
 * A really basic parser implementation for an svg viewBox attribute.
 */
public class SVGViewBoxTranslator {

    public static ViewDefinition.ViewBoxDefinition translate(final String raw) throws TranslatorException {
        final String[] parsed = parse(raw);
        return build(parsed[0],
                     parsed[1],
                     parsed[2],
                     parsed[3]);
    }

    private static String[] parse(final String raw) throws TranslatorException {
        final String[] p = _parse(raw);
        if (p.length != 4) {
            throw new TranslatorException("ViewBox definition with value [" + raw + "] is not valid.");
        }
        return p;
    }

    private static String[] _parse(final String raw) {
        return raw.replaceAll(",",
                              " ").split(" ");
    }

    private static ViewDefinition.ViewBoxDefinition build(final String x,
                                                          final String y,
                                                          final String width,
                                                          final String height) {
        return new ViewBoxDefinitionImpl(SVGAttributeParser.toPixelValue(x,
                                                                         0d),
                                         SVGAttributeParser.toPixelValue(y,
                                                                         0d),
                                         SVGAttributeParser.toPixelValue(width),
                                         SVGAttributeParser.toPixelValue(height));
    }
}
