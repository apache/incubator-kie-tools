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

import java.util.LinkedList;
import java.util.List;

import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;
import org.kie.workbench.common.stunner.svg.gen.exception.TranslatorException;
import org.kie.workbench.common.stunner.svg.gen.model.PrimitiveDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.ShapeDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.StyleSheetDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.ViewDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.ViewRefDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.impl.AbstractShapeDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.impl.GroupDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.impl.ViewDefinitionImpl;
import org.kie.workbench.common.stunner.svg.gen.translator.SVGDocumentTranslator;
import org.kie.workbench.common.stunner.svg.gen.translator.SVGElementTranslator;
import org.kie.workbench.common.stunner.svg.gen.translator.SVGTranslatorContext;
import org.kie.workbench.common.stunner.svg.gen.translator.css.SVGAttributeParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SVGDocumentTranslatorImpl implements SVGDocumentTranslator {

    private static final String SVG_TAG = "svg";
    public static final String ID = "id";
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
    public ViewDefinition<SVGShapeView> translate(final SVGTranslatorContext context) throws TranslatorException {
        final Document root = context.getRoot();
        final StyleSheetDefinition styleSheetDefinition = context.getGlobalStyleSheet().orElse(null);

        // Initialze the context with the available translators.
        context.setElementTranslators(elementTranslators);

        // Process the SVG node stuff.
        final double[] svgCoord = new double[]{0d, 0d};
        final double[] svgSize = new double[]{0d, 0d};
        final ViewDefinition.ViewBoxDefinition[] viewBox = new ViewDefinition.ViewBoxDefinition[1];
        final NodeList svgNodes = root.getElementsByTagName(SVG_TAG);
        if (null != svgNodes && svgNodes.getLength() > 1) {
            throw new TranslatorException("Only a single SVG node supported.!");
        } else if (null == svgNodes || svgNodes.getLength() == 0) {
            throw new TranslatorException("No SVG root node found!");
        }
        final Element svgNode = (Element) svgNodes.item(0);
        // SVG id.
        String svgId = svgNode.getAttribute(ID);
        if (isEmpty(svgId)) {
            throw new TranslatorException("The SVG node must contain a valid ID attribute.");
        }
        context.setSVGId(svgId);
        if (null == context.getViewId()) {
            svgId = svgNode.getAttribute(ID);
            context.setViewId(svgId);
        }
        // View box.
        final String x = svgNode.getAttribute(X);
        final String y = svgNode.getAttribute(Y);
        final String width = svgNode.getAttribute(WIDTH);
        if (isEmpty(width)) {
            throw new TranslatorException("The SVG node [" + svgId + "] must contain a valid WIDTH attribute.");
        }
        final String height = svgNode.getAttribute(HEIGHT);
        if (isEmpty(height)) {
            throw new TranslatorException("The SVG node [" + svgId + "] must contain a valid HEIGHT attribute.");
        }
        svgCoord[0] = SVGAttributeParser.toPixelValue(x,
                                                      X_DEFAULT);
        svgCoord[1] = SVGAttributeParser.toPixelValue(y,
                                                      Y_DEFAULT);
        svgSize[0] = SVGAttributeParser.toPixelValue(width);
        svgSize[1] = SVGAttributeParser.toPixelValue(height);
        final String vbox = svgNode.getAttribute(VIEW_BOX);
        if (isEmpty(vbox)) {
            throw new TranslatorException("The SVG node [" + svgId + "] must contain a valid VIEWBOX attribute.");
        }
        viewBox[0] = SVGViewBoxTranslator.translate(vbox);

        // Parser innver SVG View elements.
        ShapeDefinition<?> main = null;
        final List<PrimitiveDefinition<?>> result = new LinkedList<>();
        final NodeList nodes = svgNode.getChildNodes();
        if (null != nodes) {
            for (int i = 0; i < nodes.getLength(); i++) {
                final Node node = nodes.item(i);
                if (node instanceof Element) {
                    final Element element = (Element) node;
                    final SVGElementTranslator<Element, Object> translator =
                            context.getElementTranslator(element.getTagName());
                    if (null != translator) {
                        final Object definition = translator.translate(element,
                                                                       context);
                        if (null != definition) {
                            if (definition instanceof PrimitiveDefinition) {
                                final PrimitiveDefinition primitiveDefinition = (PrimitiveDefinition) definition;
                                if (null == main) {
                                    main = (ShapeDefinition<?>) primitiveDefinition;
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
        }

        if (null == main) {
            throw new TranslatorException("No SVG main node found.");
        }

        if (main instanceof GroupDefinition) {
            throw new TranslatorException("Main node cannot be a group.");
        }

        // Main view shape should listen for events.
        if (main instanceof AbstractShapeDefinition) {
            ((AbstractShapeDefinition) main).setMainShape(true);
            ((AbstractShapeDefinition) main).setListening(true);
        }

        // Generate the view definition instance.
        final ViewDefinition viewDefinition =
                new ViewDefinitionImpl(svgId,
                                       svgCoord[0],
                                       svgCoord[1],
                                       svgSize[0],
                                       svgSize[1],
                                       styleSheetDefinition,
                                       viewBox[0],
                                       main,
                                       result.toArray(new PrimitiveDefinition<?>[result.size()]));
        viewDefinition.getSVGViewRefs().addAll(context.getViewRefDefinitions());
        return viewDefinition;
    }

    private static boolean isEmpty(final String s) {
        return null == s || s.trim().length() == 0;
    }
}
