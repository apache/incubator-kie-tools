/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.svg.gen.translator.impl;

import org.kie.workbench.common.stunner.svg.gen.exception.TranslatorException;
import org.kie.workbench.common.stunner.svg.gen.model.PrimitiveDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.ShapeDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.ViewRefDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.impl.GroupDefinition;
import org.kie.workbench.common.stunner.svg.gen.translator.SVGElementTranslator;
import org.kie.workbench.common.stunner.svg.gen.translator.SVGTranslatorContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SVGGroupTranslator
        extends AbstractSVGPrimitiveTranslator<Element, GroupDefinition>
        implements SVGElementTranslator<Element, GroupDefinition> {

    @Override
    public String getTagName() {
        return "g";
    }

    @Override
    protected GroupDefinition doTranslate(final Element element,
                                          final SVGTranslatorContext context) throws TranslatorException {

        final String id = getId(element);
        final GroupDefinition groupDefinition = new GroupDefinition(id);
        final NodeList childNodes = element.getChildNodes();
        if (null != childNodes && childNodes.getLength() > 0) {
            for (int i = 0; i < childNodes.getLength(); i++) {
                final Node child = childNodes.item(i);
                if (child instanceof Element) {
                    final Element childElement = (Element) child;
                    final SVGElementTranslator<Element, Object> translator =
                            context.getElementTranslator(childElement.getTagName());
                    if (null != translator) {
                        final Object childDefinition =
                                translator.translate(childElement,
                                                     context);
                        if (childDefinition instanceof ViewRefDefinition) {
                            context.addSVGViewRef((ViewRefDefinition) childDefinition);
                        } else if (childDefinition instanceof ShapeDefinition) {
                            groupDefinition.getChildren().add((PrimitiveDefinition) childDefinition);
                        } else if (childDefinition instanceof GroupDefinition) {
                            throw new UnsupportedOperationException("Nested SVG groups are not allowed! [svgId=" +
                                                                            context.getSVGId() + "]");
                        }
                    }
                }
            }
        }
        return groupDefinition;
    }

    @Override
    public Class<Element> getInputType() {
        return Element.class;
    }
}
