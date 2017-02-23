/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.svg.gen.translator.impl;

import org.kie.workbench.common.stunner.svg.gen.exception.TranslatorException;
import org.kie.workbench.common.stunner.svg.gen.model.StyleDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.impl.AbstractShapeDefinition;
import org.kie.workbench.common.stunner.svg.gen.translator.SVGTranslatorContext;
import org.w3c.dom.Element;

public abstract class AbstractSVGShapeTranslator<E extends Element, O extends AbstractShapeDefinition<?>>
        extends AbstractSVGPrimitiveTranslator<E, O> {

    protected StyleDefinition translateStyles(final E element,
                                              final O def,
                                              final SVGTranslatorContext context) throws TranslatorException {
        final StyleDefinition styleDefinition = super.translateStyles(element,
                                                                      def,
                                                                      context);
        def.setStyleDefinition(styleDefinition);
        return styleDefinition;
    }
}