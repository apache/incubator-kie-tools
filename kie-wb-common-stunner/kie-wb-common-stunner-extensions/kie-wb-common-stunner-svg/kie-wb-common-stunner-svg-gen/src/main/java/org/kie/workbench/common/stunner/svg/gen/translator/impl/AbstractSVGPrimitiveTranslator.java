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

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.kie.workbench.common.stunner.svg.gen.exception.TranslatorException;
import org.kie.workbench.common.stunner.svg.gen.model.LayoutDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.StyleDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.TransformDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.impl.AbstractPrimitiveDefinition;
import org.kie.workbench.common.stunner.svg.gen.translator.SVGDocumentTranslator;
import org.kie.workbench.common.stunner.svg.gen.translator.SVGElementTranslator;
import org.kie.workbench.common.stunner.svg.gen.translator.SVGTranslatorContext;
import org.kie.workbench.common.stunner.svg.gen.translator.css.SVGAttributeParser;
import org.kie.workbench.common.stunner.svg.gen.translator.css.SVGStyleTranslator;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class AbstractSVGPrimitiveTranslator<E extends Element, O extends AbstractPrimitiveDefinition<?>>
        implements SVGElementTranslator<E, O> {

    protected static final String ID = "id";
    protected static final String X = "x";
    protected static final String Y = "y";

    protected abstract O doTranslate(final E pathElement,
                                     final SVGTranslatorContext context) throws TranslatorException;

    @Override
    public O translate(final E element,
                       final SVGTranslatorContext context) throws TranslatorException {
        // Check for excluded svg elements.
        final boolean excluded = translatePrimitiveExcluded(element);
        if (excluded) {
            return null;
        }

        // Delegate to subtypes the instantiation and initial population of the definition.
        final O def = doTranslate(element,
                                  context);
        if (null != def) {
            // Parse the stunner's specific primitives attributes.
            translatePrimitiveDefinition(element,
                                         def,
                                         context);
            // Parse the stunner's specific transform attributes.
            translateTransformDefinition(element, def);
            // Parse the stunner's child layout, if any.
            translateLayout(element, def);
            // Parse and populate the definition with styles.
            translateStyles(element,
                            def,
                            context);
            // Same for transforms.
            translateTransforms(element, def);
        }
        return def;
    }

    protected boolean translatePrimitiveExcluded(final E element) {

        final String shapeAttributeValue = getShapeAttributeValue(element);
        return !isEmpty(shapeAttributeValue) &&
                SVGDocumentTranslator.STUNNER_ATTR_SHAPE_EXCLUDE.equalsIgnoreCase(shapeAttributeValue);
    }

    protected void translatePrimitiveDefinition(final E element,
                                                final O def,
                                                final SVGTranslatorContext context) throws TranslatorException {
        // Position.
        translatePosition(element,
                          def,
                          context);
    }

    String getShapeAttributeValue(final E element) {
        return element.getAttributeNS(SVGDocumentTranslator.STUNNER_URI,
                                      SVGDocumentTranslator.STUNNER_ATTR_NS_SHAPE);
    }

    String getShapeStateAttributeValue(final E element) {
        return element.getAttributeNS(SVGDocumentTranslator.STUNNER_URI,
                                      SVGDocumentTranslator.STUNNER_ATTR_NS_STATE);
    }

    protected void translateTransformDefinition(final E element, final O def) {
        boolean result = true;
        Node candidate = element;
        while (null != candidate) {
            if (candidate instanceof Element) {
                Optional<Boolean> scalable = isScalable((Element) candidate);
                if (scalable.isPresent()) {
                    result = scalable.get();
                    break;
                }
                candidate = candidate.getParentNode();
            } else {
                candidate = null;
            }
        }
        def.setScalable(result);
    }

    private Optional<Boolean> isScalable(final Element element) {
        final String shapeRaw = element.getAttributeNS(SVGDocumentTranslator.STUNNER_URI,
                                                       SVGDocumentTranslator.STUNNER_ATTR_NS_TRANSFORM);
        boolean empty = isEmpty(shapeRaw);
        if (!empty && SVGDocumentTranslator.STUNNER_ATTR_TRANSFORM_NON_SCALABLE.equalsIgnoreCase(shapeRaw)) {
            return Optional.of(false);
        }
        if (!empty && SVGDocumentTranslator.STUNNER_ATTR_TRANSFORM_NON_SCALABLE.equalsIgnoreCase(shapeRaw)) {
            return Optional.of(true);
        }
        return Optional.empty();
    }

    protected void translatePosition(final E element,
                                     final O def,
                                     final SVGTranslatorContext context) throws TranslatorException {
        final String xr = element.getAttribute(getXAttributeName());
        final String yr = element.getAttribute(getYAttributeName());
        final double x = SVGAttributeParser.toPixelValue(xr,
                                                         0d);
        final double y = SVGAttributeParser.toPixelValue(yr,
                                                         0d);
        def.setX(x);
        def.setY(y);
    }

    protected String getXAttributeName() {
        return X;
    }

    protected String getYAttributeName() {
        return Y;
    }

    protected LayoutDefinition translateLayout(final E element, final O def) {
        final String layoutRaw = element.getAttributeNS(SVGDocumentTranslator.STUNNER_URI,
                                                        SVGDocumentTranslator.STUNNER_ATTR_NS_LAYOUT);
        final LayoutDefinition l = isEmpty(layoutRaw) ? LayoutDefinition.NONE : LayoutDefinition.valueOf(layoutRaw);
        def.setLayoutDefinition(l);
        return l;
    }

    protected TransformDefinition translateTransforms(final E element, final O def) throws TranslatorException {
        final TransformDefinition transformDefinition = SVGStyleTranslator.parseTransformDefinition(element);
        def.setTransformDefinition(transformDefinition);
        return transformDefinition;
    }

    protected StyleDefinition translateStyles(final E element,
                                              final O def,
                                              final SVGTranslatorContext context) throws TranslatorException {
        final StyleDefinition styleDefinition = SVGStyleTranslator.parseStyleDefinition(element,
                                                                                        context.getViewId(),
                                                                                        context.getGlobalStyleSheet().orElse(null));
        if (null != styleDefinition) {
            def.setAlpha(null != styleDefinition.getAlpha() ?
                                 styleDefinition.getAlpha() :
                                 1d);
        }
        return styleDefinition;
    }

    protected static String getId(final Element element) {
        String id = element.getAttribute(ID);
        if (isEmpty(id)) {
            id = "prim_" + UUID.uuid(4);
            element.setAttribute(ID, id);
        }
        //return SVGModelUtils.toValidJavaId(id);
        return id;
    }

    protected static void failIfEmpty(final String key,
                                      final String value) throws TranslatorException {
        if (isEmpty(value)) {
            throw new TranslatorException("Empty value for key [" + key + "]");
        }
    }

    protected static boolean isEmpty(final String s) {
        return StringUtils.isEmpty(s);
    }
}
