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

import java.util.LinkedList;
import java.util.List;

import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;
import org.kie.workbench.common.stunner.svg.gen.exception.TranslatorException;
import org.kie.workbench.common.stunner.svg.gen.model.PrimitiveDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.ViewDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.ViewRefDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.impl.AbstractPrimitiveDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.impl.ViewDefinitionImpl;
import org.kie.workbench.common.stunner.svg.gen.translator.SVGDocumentTranslator;
import org.kie.workbench.common.stunner.svg.gen.translator.SVGElementTranslator;
import org.kie.workbench.common.stunner.svg.gen.translator.css.SVGAttributeParserUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class SVGDocumentTranslatorImpl implements SVGDocumentTranslator {

    private static final String SVG_TAG = "svg";
    public static final String X = "x";
    public static final double X_DEFAULT = 0d;
    public static final String Y = "y";
    public static final double Y_DEFAULT = 0d;
    public static final String WIDTH = "width";
    public static final String HEIGHT = "height";
    public static final String VIEW_BOX = "viewBox";

    private final SVGElementTranslator<Element, Object>[] elementTranslators;

    public SVGDocumentTranslatorImpl(final SVGElementTranslator<Element, Object>[] elementTranslators) {
        this.elementTranslators = elementTranslators;
    }

    @Override
    public Class<Document> getInputType() {
        return Document.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ViewDefinition<SVGShapeView> translate(final Document root) throws TranslatorException {

        final SVGTranslatorContextImpl context = new SVGTranslatorContextImpl(root,
                                                                              elementTranslators);

        // Process the SVG node stuff.
        String svgId = null;
        final double[] svgCoord = new double[]{0d, 0d};
        final double[] svgSize = new double[]{0d, 0d};
        final ViewDefinition.ViewBoxDefinition[] viewBox = new ViewDefinition.ViewBoxDefinition[1];
        final NodeList svgNodes = root.getElementsByTagName(SVG_TAG);
        if (null != svgNodes && 1 == svgNodes.getLength()) {
            final Element svgNode = (Element) svgNodes.item(0);
            // SVG id.
            svgId = svgNode.getAttribute(X);
            // View box.
            final String x = svgNode.getAttribute(X);
            final String y = svgNode.getAttribute(Y);
            final String width = svgNode.getAttribute(WIDTH);
            final String height = svgNode.getAttribute(HEIGHT);
            svgCoord[0] = SVGAttributeParserUtils.toPixelValue(x,
                                                               X_DEFAULT);
            svgCoord[1] = SVGAttributeParserUtils.toPixelValue(y,
                                                               Y_DEFAULT);
            svgSize[0] = SVGAttributeParserUtils.toPixelValue(width);
            svgSize[1] = SVGAttributeParserUtils.toPixelValue(height);
            final String vbox = svgNode.getAttribute(VIEW_BOX);
            viewBox[0] = SVGViewBoxTranslator.translate(vbox);
        } else {
            throw new TranslatorException("No SVG root node found!");
        }

        // Parser innver SVG View elements.
        PrimitiveDefinition<?> potentialMainShape = null;
        PrimitiveDefinition<?> mainShape = null;
        final List<PrimitiveDefinition<?>> result = new LinkedList<>();
        for (final SVGElementTranslator<Element, Object> translator : elementTranslators) {
            final String tagName = translator.getTagName();
            final NodeList nodes = root.getElementsByTagName(tagName);
            if (null != nodes && 0 < nodes.getLength()) {
                for (int i = 0; i < nodes.getLength(); i++) {
                    final Element node = (Element) nodes.item(i);
                    final Object definition = translator.translate(node,
                                                                   context);
                    if (null != definition) {
                        if (definition instanceof PrimitiveDefinition) {
                            final PrimitiveDefinition primitiveDefinition = (PrimitiveDefinition) definition;
                            if (primitiveDefinition.isMain()) {
                                mainShape = primitiveDefinition;
                            } else if (null == potentialMainShape) {
                                potentialMainShape = primitiveDefinition;
                            } else {
                                result.add(primitiveDefinition);
                            }
                        } else if (definition instanceof ViewRefDefinition) {
                            context.addSVGViewRef((ViewRefDefinition) definition);
                        }
                    }
                }
            }
        }

        if (null != mainShape && null != potentialMainShape) {
            result.add(potentialMainShape);
        }

        final PrimitiveDefinition<?> main = null != mainShape ? mainShape : potentialMainShape;
        if (null == main) {
            throw new TranslatorException("No SVG main node found!!");
        }

        // Main view shape should listen for events.
        if (main instanceof AbstractPrimitiveDefinition) {
            ((AbstractPrimitiveDefinition) main).setListening(true);
        }

        // Generate the view definition instance.
        final ViewDefinition viewDefinition =
                new ViewDefinitionImpl(svgId,
                                       svgCoord[0],
                                       svgCoord[1],
                                       svgSize[0],
                                       svgSize[1],
                                       viewBox[0],
                                       main,
                                       result.toArray(new PrimitiveDefinition<?>[result.size()]));
        viewDefinition.getSVGViewRefs().addAll(context.viewRefDefinitions);
        return viewDefinition;
    }
}
